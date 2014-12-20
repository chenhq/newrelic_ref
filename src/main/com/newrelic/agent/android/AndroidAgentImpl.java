/*     */ package com.newrelic.agent.android;
/*     */ 
/*     */ import android.app.ActivityManager;
/*     */ import android.app.Application;
/*     */ import android.content.Context;
/*     */ import android.content.pm.ApplicationInfo;
/*     */ import android.content.pm.PackageInfo;
/*     */ import android.content.pm.PackageManager;
/*     */ import android.content.pm.PackageManager.NameNotFoundException;
/*     */ import android.content.res.Configuration;
/*     */ import android.content.res.Resources;
/*     */ import android.location.Address;
/*     */ import android.location.Geocoder;
/*     */ import android.location.Location;
/*     */ import android.location.LocationListener;
/*     */ import android.location.LocationManager;
/*     */ import android.os.Build;
/*     */ import android.os.Build.VERSION;
/*     */ import android.os.Bundle;
/*     */ import android.os.Environment;
/*     */ import android.os.Looper;
/*     */ import android.os.StatFs;
/*     */ import com.newrelic.agent.android.api.common.TransactionData;
/*     */ import com.newrelic.agent.android.api.v1.ConnectionEvent;
/*     */ import com.newrelic.agent.android.api.v1.ConnectionListener;
/*     */ import com.newrelic.agent.android.api.v1.DeviceForm;
/*     */ import com.newrelic.agent.android.api.v2.TraceMachineInterface;
/*     */ import com.newrelic.agent.android.background.ApplicationStateEvent;
/*     */ import com.newrelic.agent.android.background.ApplicationStateListener;
/*     */ import com.newrelic.agent.android.background.ApplicationStateMonitor;
/*     */ import com.newrelic.agent.android.crashes.CrashReporter;
/*     */ import com.newrelic.agent.android.harvest.AgentHealth;
/*     */ import com.newrelic.agent.android.harvest.ApplicationInformation;
/*     */ import com.newrelic.agent.android.harvest.DeviceInformation;
/*     */ import com.newrelic.agent.android.harvest.EnvironmentInformation;
/*     */ import com.newrelic.agent.android.harvest.Harvest;
/*     */ import com.newrelic.agent.android.harvest.HarvestConfiguration;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.sample.MachineMeasurementConsumer;
/*     */ import com.newrelic.agent.android.sample.Sampler;
/*     */ import com.newrelic.agent.android.stats.StatsEngine;
/*     */ import com.newrelic.agent.android.tracing.Sample;
/*     */ import com.newrelic.agent.android.tracing.SampleValue;
/*     */ import com.newrelic.agent.android.tracing.TraceMachine;
/*     */ import com.newrelic.agent.android.util.AndroidEncoder;
/*     */ import com.newrelic.agent.android.util.Connectivity;
/*     */ import com.newrelic.agent.android.util.Encoder;
/*     */ import com.newrelic.agent.android.util.JsonCrashStore;
/*     */ import com.newrelic.agent.android.util.UiBackgroundListener;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import proguard.canary.NewRelicCanary;
/*     */ 
/*     */ public class AndroidAgentImpl
/*     */   implements AgentImpl, ConnectionListener, ApplicationStateListener, TraceMachineInterface
/*     */ {
/*     */   private static final float LOCATION_ACCURACY_THRESHOLD = 500.0F;
/*  41 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */   private final Context context;
/*     */   private SavedState savedState;
/*     */   private LocationListener locationListener;
/*  47 */   private final Lock lock = new ReentrantLock();
/*     */ 
/*  49 */   private final Encoder encoder = new AndroidEncoder();
/*     */   private DeviceInformation deviceInformation;
/*     */   private ApplicationInformation applicationInformation;
/*     */   private final AgentConfiguration agentConfiguration;
/*     */   private MachineMeasurementConsumer machineMeasurementConsumer;
/* 465 */   private static final Comparator<TransactionData> cmp = new Comparator()
/*     */   {
/*     */     public int compare(TransactionData lhs, TransactionData rhs) {
/* 468 */       if (lhs.getTimestamp() > rhs.getTimestamp())
/* 469 */         return -1;
/* 470 */       if (lhs.getTimestamp() < rhs.getTimestamp()) {
/* 471 */         return 1;
/*     */       }
/* 473 */       return 0;
/*     */     }
/* 465 */   };
/*     */ 
/*     */   public AndroidAgentImpl(Context context, AgentConfiguration agentConfiguration)
/*     */     throws AgentInitializationException
/*     */   {
/*  62 */     this.context = appContext(context);
/*  63 */     this.agentConfiguration = agentConfiguration;
/*     */ 
/*  65 */     this.savedState = new SavedState(this.context);
/*     */ 
/*  67 */     if (!agentConfiguration.getApplicationToken().equals(this.savedState.getAppToken())) {
/*  68 */       log.debug("License key has changed. Clearing saved state.");
/*  69 */       this.savedState.clear();
/*     */     }
/*     */ 
/*  72 */     if (!Agent.getVersion().equals(this.savedState.getAgentVersion())) {
/*  73 */       log.debug("Agent version has changed. Clearing saved state.");
/*  74 */       this.savedState.clear();
/*     */     }
/*     */ 
/*  77 */     if (isDisabled()) {
/*  78 */       throw new AgentInitializationException("This version of the agent has been disabled");
/*     */     }
/*     */ 
/*  81 */     initApplicationInformation();
/*     */ 
/*  83 */     if ((agentConfiguration.useLocationService()) && (this.context.getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION", getApplicationInformation().getPackageId()) == 0)) {
/*  84 */       log.debug("Location stats enabled");
/*  85 */       addLocationListener();
/*     */     }
/*     */ 
/*  89 */     TraceMachine.setTraceMachineInterface(this);
/*     */ 
/*  91 */     this.savedState.saveAppToken(agentConfiguration.getApplicationToken());
/*  92 */     this.savedState.saveAgentVersion(Agent.getVersion());
/*  93 */     agentConfiguration.setCrashStore(new JsonCrashStore(context));
/*     */ 
/*  95 */     ApplicationStateMonitor.getInstance().addApplicationStateListener(this);
/*     */ 
/*  97 */     if (Build.VERSION.SDK_INT >= 14)
/*     */     {
/*  99 */       context.registerComponentCallbacks(new UiBackgroundListener());
/*     */     }
/*     */ 
/* 103 */     Sampler.init(context);
/*     */   }
/*     */ 
/*     */   protected void initialize() {
/* 107 */     Harvest.addHarvestListener(this.savedState);
/* 108 */     Harvest.initialize(this.agentConfiguration);
/* 109 */     Harvest.setHarvestConfiguration(this.savedState.getHarvestConfiguration());
/* 110 */     Measurements.initialize();
/* 111 */     log.info(MessageFormat.format("New Relic Agent v{0}", new Object[] { Agent.getVersion() }));
/* 112 */     log.verbose(MessageFormat.format("Application token: {0}", new Object[] { this.agentConfiguration.getApplicationToken() }));
/*     */ 
/* 114 */     this.machineMeasurementConsumer = new MachineMeasurementConsumer();
/* 115 */     Measurements.addMeasurementConsumer(this.machineMeasurementConsumer);
/*     */ 
/* 117 */     StatsEngine.get().inc("Supportability/AgentHealth/UncaughtExceptionHandler/" + getUnhandledExceptionHandlerName());
/* 118 */     CrashReporter.initialize(this.agentConfiguration);
/*     */   }
/*     */ 
/*     */   public DeviceInformation getDeviceInformation()
/*     */   {
/* 123 */     if (this.deviceInformation != null) {
/* 124 */       return this.deviceInformation;
/*     */     }
/* 126 */     DeviceInformation info = new DeviceInformation();
/*     */ 
/* 128 */     info.setOsName("Android");
/* 129 */     info.setOsVersion(Build.VERSION.RELEASE);
/* 130 */     info.setOsBuild(Build.VERSION.INCREMENTAL);
/* 131 */     info.setModel(Build.MODEL);
/* 132 */     info.setAgentName("AndroidAgent");
/* 133 */     info.setAgentVersion(Agent.getVersion());
/* 134 */     info.setManufacturer(Build.MANUFACTURER);
/* 135 */     info.setDeviceId(getUUID());
/* 136 */     info.setArchitecture(System.getProperty("os.arch"));
/* 137 */     info.setRunTime(System.getProperty("java.vm.version"));
/* 138 */     info.setSize(deviceForm(this.context).name().toLowerCase());
/*     */ 
/* 140 */     this.deviceInformation = info;
/*     */ 
/* 142 */     return this.deviceInformation;
/*     */   }
/*     */ 
/*     */   public EnvironmentInformation getEnvironmentInformation()
/*     */   {
/* 147 */     EnvironmentInformation envInfo = new EnvironmentInformation();
/* 148 */     ActivityManager activityManager = (ActivityManager)this.context.getSystemService("activity");
/*     */ 
/* 150 */     long[] free = new long[2];
/*     */     try {
/* 152 */       StatFs rootStatFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
/*     */ 
/* 154 */       StatFs externalStatFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
/*     */ 
/* 156 */       if (Build.VERSION.SDK_INT >= 18) {
/* 157 */         free[0] = (rootStatFs.getAvailableBlocksLong() * rootStatFs.getBlockSizeLong());
/* 158 */         free[1] = (externalStatFs.getAvailableBlocksLong() * rootStatFs.getBlockSizeLong());
/*     */       } else {
/* 160 */         free[0] = (rootStatFs.getAvailableBlocks() * rootStatFs.getBlockSize());
/* 161 */         free[1] = (externalStatFs.getAvailableBlocks() * externalStatFs.getBlockSize());
/*     */       }
/*     */     } catch (Exception e) {
/* 164 */       AgentHealth.noticeException(e);
/*     */     }
/*     */     finally {
/* 167 */       if (free[0] < 0L)
/* 168 */         free[0] = 0L;
/* 169 */       if (free[1] < 0L) {
/* 170 */         free[1] = 0L;
/*     */       }
/* 172 */       envInfo.setDiskAvailable(free);
/*     */     }
/* 174 */     envInfo.setMemoryUsage(Sampler.sampleMemory(activityManager).getSampleValue().asLong().longValue());
/* 175 */     envInfo.setOrientation(this.context.getResources().getConfiguration().orientation);
/* 176 */     envInfo.setNetworkStatus(getNetworkCarrier());
/* 177 */     envInfo.setNetworkWanType(getNetworkWanType());
/*     */ 
/* 179 */     return envInfo;
/*     */   }
/*     */ 
/* 183 */   public void initApplicationInformation() throws AgentInitializationException { String packageName = this.context.getPackageName();
/* 184 */     PackageManager packageManager = this.context.getPackageManager();
/*     */     String appVersion;
/*     */     try {
/* 188 */       PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
/*     */       String appVersion;
/* 190 */       if ((packageInfo != null) && (packageInfo.versionName != null) && (packageInfo.versionName.length() > 0))
/* 191 */         appVersion = packageInfo.versionName;
/*     */       else
/* 193 */         throw new AgentInitializationException("Your app doesn't appear to have a version defined. Ensure you have defined 'versionName' in your manifest.");
/*     */     }
/*     */     catch (PackageManager.NameNotFoundException e) {
/* 196 */       throw new AgentInitializationException("Could not determine package version: " + e.getMessage());
/*     */     }
/*     */     String appName;
/*     */     try
/*     */     {
/* 201 */       ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
/*     */       String appName;
/* 202 */       if (info != null)
/* 203 */         appName = packageManager.getApplicationLabel(info).toString();
/*     */       else
/* 205 */         appName = packageName;
/*     */     }
/*     */     catch (PackageManager.NameNotFoundException e) {
/* 208 */       log.warning(e.toString());
/* 209 */       appName = packageName;
/*     */     } catch (SecurityException e) {
/* 211 */       log.warning(e.toString());
/* 212 */       appName = packageName;
/*     */     }
/*     */ 
/* 215 */     this.applicationInformation = new ApplicationInformation(appName, appVersion, packageName);
/*     */   }
/*     */ 
/*     */   public ApplicationInformation getApplicationInformation()
/*     */   {
/* 220 */     return this.applicationInformation;
/*     */   }
/*     */ 
/*     */   private static DeviceForm deviceForm(Context context)
/*     */   {
/* 225 */     int deviceSize = context.getResources().getConfiguration().screenLayout & 0xF;
/* 226 */     switch (deviceSize) {
/*     */     case 1:
/* 228 */       return DeviceForm.SMALL;
/*     */     case 2:
/* 231 */       return DeviceForm.NORMAL;
/*     */     case 3:
/* 234 */       return DeviceForm.LARGE;
/*     */     }
/*     */ 
/* 240 */     if (deviceSize > 3) {
/* 241 */       return DeviceForm.XLARGE;
/*     */     }
/* 243 */     return DeviceForm.UNKNOWN;
/*     */   }
/*     */ 
/*     */   private static Context appContext(Context context)
/*     */   {
/* 250 */     if (!(context instanceof Application)) {
/* 251 */       return context.getApplicationContext();
/*     */     }
/* 253 */     return context;
/*     */   }
/*     */ 
/*     */   public void addTransactionData(TransactionData transactionData)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mergeTransactionData(List<TransactionData> transactionDataList)
/*     */   {
/*     */   }
/*     */ 
/*     */   public List<TransactionData> getAndClearTransactionData()
/*     */   {
/* 270 */     return null;
/*     */   }
/*     */ 
/*     */   public String getCrossProcessId()
/*     */   {
/* 275 */     this.lock.lock();
/*     */     try {
/* 277 */       return this.savedState.getCrossProcessId();
/*     */     } finally {
/* 279 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getStackTraceLimit()
/*     */   {
/* 285 */     this.lock.lock();
/*     */     try {
/* 287 */       return this.savedState.getStackTraceLimit();
/*     */     } finally {
/* 289 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getResponseBodyLimit()
/*     */   {
/* 295 */     this.lock.lock();
/*     */     try {
/* 297 */       return this.savedState.getHarvestConfiguration().getResponse_body_limit();
/*     */     } finally {
/* 299 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 305 */     if (!isDisabled()) {
/* 306 */       initialize();
/* 307 */       Harvest.start();
/*     */     }
/*     */     else
/*     */     {
/* 313 */       stop(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 319 */     stop(true);
/*     */   }
/*     */ 
/*     */   private void stop(boolean finalSendData)
/*     */   {
/* 324 */     Sampler.shutdown();
/* 325 */     TraceMachine.haltTracing();
/*     */ 
/* 327 */     if (finalSendData) {
/* 328 */       Harvest.harvestNow();
/*     */     }
/*     */ 
/* 331 */     TraceMachine.clearActivityHistory();
/* 332 */     Harvest.shutdown();
/* 333 */     Measurements.shutdown();
/*     */   }
/*     */ 
/*     */   public void disable()
/*     */   {
/* 338 */     log.warning("PERMANENTLY DISABLING AGENT v" + Agent.getVersion());
/*     */     try {
/* 340 */       this.savedState.saveDisabledVersion(Agent.getVersion());
/*     */     } finally {
/*     */       try {
/* 343 */         stop(false);
/*     */       } finally {
/* 345 */         Agent.setImpl(NullAgentImpl.instance);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isDisabled() {
/* 351 */     return Agent.getVersion().equals(this.savedState.getDisabledVersion());
/*     */   }
/*     */ 
/*     */   public String getNetworkCarrier() {
/* 355 */     return Connectivity.carrierNameFromContext(this.context);
/*     */   }
/*     */ 
/*     */   public String getNetworkWanType() {
/* 359 */     return Connectivity.wanType(this.context);
/*     */   }
/*     */ 
/*     */   public static void init(Context context, AgentConfiguration agentConfiguration)
/*     */   {
/*     */     try
/*     */     {
/* 370 */       Agent.setImpl(new AndroidAgentImpl(context, agentConfiguration));
/* 371 */       Agent.start();
/*     */     } catch (AgentInitializationException e) {
/* 373 */       log.error("Failed to initialize the agent: " + e.toString());
/* 374 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void connected(ConnectionEvent e)
/*     */   {
/* 385 */     log.error("AndroidAgentImpl: connected ");
/*     */   }
/*     */ 
/*     */   public void disconnected(ConnectionEvent e)
/*     */   {
/* 395 */     this.savedState.clear();
/*     */   }
/*     */ 
/*     */   public void applicationForegrounded(ApplicationStateEvent e)
/*     */   {
/* 401 */     log.error("AndroidAgentImpl: application foregrounded ");
/*     */ 
/* 403 */     start();
/*     */   }
/*     */ 
/*     */   public void applicationBackgrounded(ApplicationStateEvent e)
/*     */   {
/* 409 */     log.error("AndroidAgentImpl: application backgrounded ");
/* 410 */     stop();
/*     */   }
/*     */ 
/*     */   public void setLocation(String countryCode, String adminRegion)
/*     */   {
/* 415 */     if ((countryCode == null) || (adminRegion == null))
/* 416 */       throw new IllegalArgumentException("Country code and administrative region are required.");
/*     */   }
/*     */ 
/*     */   public void setLocation(Location location)
/*     */   {
/* 430 */     if (location == null) {
/* 431 */       throw new IllegalArgumentException("Location must not be null.");
/*     */     }
/*     */ 
/* 434 */     Geocoder coder = new Geocoder(this.context);
/* 435 */     List addresses = null;
/*     */     try {
/* 437 */       addresses = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
/*     */     } catch (IOException e) {
/* 439 */       log.error("Unable to geocode location: " + e.toString());
/*     */     }
/*     */ 
/* 442 */     if ((addresses == null) || (addresses.size() == 0))
/*     */     {
/* 444 */       return;
/*     */     }
/*     */ 
/* 447 */     Address address = (Address)addresses.get(0);
/* 448 */     if (address == null)
/*     */     {
/* 450 */       return;
/*     */     }
/*     */ 
/* 453 */     String countryCode = address.getCountryCode();
/* 454 */     String adminArea = address.getAdminArea();
/*     */ 
/* 456 */     if ((countryCode != null) && (adminArea != null)) {
/* 457 */       setLocation(countryCode, adminArea);
/* 458 */       removeLocationListener();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addLocationListener()
/*     */   {
/* 479 */     LocationManager locationManager = (LocationManager)this.context.getSystemService("location");
/* 480 */     if (locationManager == null) {
/* 481 */       log.error("Unable to retrieve reference to LocationManager. Disabling location listener.");
/* 482 */       return;
/*     */     }
/*     */ 
/* 485 */     this.locationListener = new LocationListener()
/*     */     {
/*     */       public void onLocationChanged(Location location)
/*     */       {
/* 489 */         if (AndroidAgentImpl.this.isAccurate(location))
/* 490 */           AndroidAgentImpl.this.setLocation(location);
/*     */       }
/*     */ 
/*     */       public void onProviderDisabled(String provider)
/*     */       {
/* 496 */         if ("passive".equals(provider))
/* 497 */           AndroidAgentImpl.this.removeLocationListener();
/*     */       }
/*     */ 
/*     */       public void onProviderEnabled(String provider)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onStatusChanged(String provider, int status, Bundle extras)
/*     */       {
/*     */       }
/*     */     };
/* 510 */     locationManager.requestLocationUpdates("passive", 1000L, 0.0F, this.locationListener);
/*     */   }
/*     */ 
/*     */   private void removeLocationListener() {
/* 514 */     if (this.locationListener == null)
/*     */     {
/* 516 */       return;
/*     */     }
/*     */ 
/* 519 */     LocationManager locationManager = (LocationManager)this.context.getSystemService("location");
/* 520 */     if (locationManager == null) {
/* 521 */       log.error("Unable to retrieve reference to LocationManager. Can't unregister location listener.");
/* 522 */       return;
/*     */     }
/*     */ 
/* 525 */     synchronized (locationManager) {
/* 526 */       locationManager.removeUpdates(this.locationListener);
/* 527 */       this.locationListener = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isAccurate(Location location)
/*     */   {
/* 538 */     if (location == null) {
/* 539 */       return false;
/*     */     }
/* 541 */     return 500.0F >= location.getAccuracy();
/*     */   }
/*     */ 
/*     */   private String getUUID()
/*     */   {
/* 550 */     String uuid = this.savedState.getAndroidIdBugWorkAround();
/* 551 */     if (uuid == null) {
/* 552 */       uuid = UUID.randomUUID().toString();
/* 553 */       this.savedState.saveAndroidIdBugWorkAround(uuid);
/*     */     }
/* 555 */     return uuid;
/*     */   }
/*     */ 
/*     */   private String getUnhandledExceptionHandlerName() {
/*     */     try {
/* 560 */       return Thread.getDefaultUncaughtExceptionHandler().getClass().getName(); } catch (Exception e) {
/*     */     }
/* 562 */     return "unknown";
/*     */   }
/*     */ 
/*     */   public Encoder getEncoder()
/*     */   {
/* 567 */     return this.encoder;
/*     */   }
/*     */ 
/*     */   public long getCurrentThreadId()
/*     */   {
/* 575 */     return Thread.currentThread().getId();
/*     */   }
/*     */ 
/*     */   public boolean isUIThread()
/*     */   {
/* 580 */     return Looper.myLooper() == Looper.getMainLooper();
/*     */   }
/*     */ 
/*     */   public String getCurrentThreadName()
/*     */   {
/* 585 */     return Thread.currentThread().getName();
/*     */   }
/*     */ 
/*     */   private void pokeCanary() {
/* 589 */     NewRelicCanary.canaryMethod();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.AndroidAgentImpl
 * JD-Core Version:    0.6.2
 */