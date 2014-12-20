/*     */ package com.newrelic.agent.android.harvest;
/*     */ 
/*     */ import com.newrelic.agent.android.AgentConfiguration;
/*     */ import com.newrelic.agent.android.TaskQueue;
/*     */ import com.newrelic.agent.android.activity.config.ActivityTraceConfiguration;
/*     */ import com.newrelic.agent.android.activity.config.ActivityTraceConfigurationDeserializer;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.stats.StatsEngine;
/*     */ import com.newrelic.agent.android.tracing.ActivityTrace;
/*     */ import com.newrelic.com.google.gson.Gson;
/*     */ import com.newrelic.com.google.gson.GsonBuilder;
/*     */ import com.newrelic.com.google.gson.JsonSyntaxException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ 
/*     */ public class Harvester
/*     */ {
/*  25 */   private final AgentLog log = AgentLogManager.getAgentLog();
/*     */ 
/*  44 */   private State state = State.UNINITIALIZED;
/*     */   protected boolean stateChanged;
/*     */   private HarvestConnection harvestConnection;
/*     */   private AgentConfiguration agentConfiguration;
/*  54 */   private HarvestConfiguration configuration = HarvestConfiguration.getDefaultHarvestConfiguration();
/*     */   private HarvestData harvestData;
/*  57 */   private final Collection<HarvestLifecycleAware> harvestListeners = new ArrayList();
/*     */ 
/*     */   public void start() {
/*  60 */     fireOnHarvestStart();
/*     */   }
/*     */ 
/*     */   public void stop() {
/*  64 */     fireOnHarvestStop();
/*     */   }
/*     */ 
/*     */   protected void uninitialized()
/*     */   {
/*  73 */     if (this.agentConfiguration == null) {
/*  74 */       this.log.error("Agent configuration unavailable.");
/*  75 */       return;
/*     */     }
/*     */ 
/*  79 */     this.harvestConnection.setConnectInformation(new ConnectInformation());
/*  80 */     this.harvestConnection.setApplicationToken(this.agentConfiguration.getApplicationToken());
/*  81 */     this.harvestConnection.setCollectorHost(this.agentConfiguration.getCollectorHost());
/*  82 */     this.harvestConnection.useSsl(this.agentConfiguration.useSsl());
/*     */ 
/*  85 */     transition(State.DISCONNECTED);
/*  86 */     execute();
/*     */   }
/*     */ 
/*     */   protected void disconnected()
/*     */   {
/*  97 */     if (this.harvestData.getDataToken().isValid()) {
/*  98 */       this.log.verbose("Skipping connect call, saved state is available: " + this.harvestData.getDataToken());
/*     */ 
/* 101 */       StatsEngine.get().sample("Session/Start", 1.0F);
/* 102 */       fireOnHarvestConnected();
/* 103 */       transition(State.CONNECTED);
/* 104 */       execute();
/* 105 */       return;
/*     */     }
/*     */ 
/* 108 */     this.log.info("Connecting, saved state is not available: " + this.harvestData.getDataToken());
/*     */ 
/* 111 */     HarvestResponse response = this.harvestConnection.sendConnect();
/*     */ 
/* 114 */     if (response == null) {
/* 115 */       this.log.error("Unable to connect to the Collector.");
/* 116 */       return;
/*     */     }
/*     */ 
/* 120 */     if (response.isOK()) {
/* 121 */       HarvestConfiguration configuration = parseHarvesterConfiguration(response);
/* 122 */       if (configuration == null) {
/* 123 */         this.log.error("Unable to configure Harvester using Collector configuration.");
/* 124 */         return;
/*     */       }
/*     */ 
/* 127 */       configureHarvester(configuration);
/* 128 */       StatsEngine.get().sampleTimeMs("Supportability/AgentHealth/Collector/Harvest", response.getResponseTime());
/* 129 */       fireOnHarvestConnected();
/*     */ 
/* 132 */       transition(State.CONNECTED);
/* 133 */       return;
/*     */     }
/*     */ 
/* 136 */     this.log.debug("Harvest connect response: " + response.getResponseCode());
/*     */ 
/* 139 */     switch (1.$SwitchMap$com$newrelic$agent$android$harvest$HarvestResponse$Code[response.getResponseCode().ordinal()])
/*     */     {
/*     */     case 1:
/*     */     case 2:
/* 144 */       this.harvestData.getDataToken().clear();
/* 145 */       fireOnHarvestDisconnected();
/* 146 */       return;
/*     */     case 3:
/* 148 */       if (response.isDisableCommand()) {
/* 149 */         this.log.error("Collector has commanded Agent to disable.");
/* 150 */         fireOnHarvestDisabled();
/* 151 */         transition(State.DISABLED);
/* 152 */         return;
/*     */       }
/* 154 */       this.log.error("Unexpected Collector response: FORBIDDEN");
/* 155 */       break;
/*     */     case 4:
/*     */     case 5:
/* 158 */       this.log.error("Invalid ConnectionInformation was sent to the Collector.");
/* 159 */       break;
/*     */     default:
/* 161 */       this.log.error("An unknown error occurred when connecting to the Collector.");
/*     */     }
/*     */ 
/* 164 */     fireOnHarvestError();
/*     */   }
/*     */ 
/*     */   protected void connected()
/*     */   {
/* 176 */     this.log.info("Harvester: connected");
/* 177 */     this.log.info("Harvester: Sending " + this.harvestData.getHttpTransactions().count() + " HTTP transactions.");
/* 178 */     this.log.info("Harvester: Sending " + this.harvestData.getHttpErrors().count() + " HTTP errors.");
/* 179 */     this.log.info("Harvester: Sending " + this.harvestData.getActivityTraces().count() + " activity traces.");
/*     */ 
/* 181 */     HarvestResponse response = this.harvestConnection.sendData(this.harvestData);
/*     */ 
/* 184 */     if ((response == null) || (response.isUnknown())) {
/* 185 */       fireOnHarvestSendFailed();
/* 186 */       return;
/*     */     }
/*     */ 
/* 189 */     this.harvestData.reset();
/*     */ 
/* 191 */     StatsEngine.get().sampleTimeMs("Supportability/AgentHealth/Collector/Harvest", response.getResponseTime());
/*     */ 
/* 193 */     this.log.debug("Harvest data response: " + response.getResponseCode());
/* 194 */     this.log.debug("Harvest data response status code: " + response.getStatusCode());
/* 195 */     this.log.debug("Harvest data response body: " + response.getResponseBody());
/*     */ 
/* 197 */     if (response.isError()) {
/* 198 */       fireOnHarvestError();
/*     */ 
/* 200 */       switch (1.$SwitchMap$com$newrelic$agent$android$harvest$HarvestResponse$Code[response.getResponseCode().ordinal()]) {
/*     */       case 1:
/*     */       case 2:
/* 203 */         this.harvestData.getDataToken().clear();
/* 204 */         transition(State.DISCONNECTED);
/* 205 */         break;
/*     */       case 3:
/* 207 */         if (response.isDisableCommand()) {
/* 208 */           this.log.error("Collector has commanded Agent to disable.");
/* 209 */           transition(State.DISABLED);
/*     */         }
/*     */         else {
/* 212 */           this.log.error("Unexpected Collector response: FORBIDDEN");
/* 213 */           transition(State.DISCONNECTED);
/* 214 */         }break;
/*     */       case 4:
/*     */       case 5:
/* 217 */         this.log.error("Invalid ConnectionInformation was sent to the Collector.");
/* 218 */         break;
/*     */       default:
/* 220 */         this.log.error("An unknown error occurred when connecting to the Collector.");
/*     */       }
/* 222 */       return;
/*     */     }
/*     */ 
/* 225 */     HarvestConfiguration configuration = parseHarvesterConfiguration(response);
/* 226 */     if (configuration == null) {
/* 227 */       this.log.error("Unable to configure Harvester using Collector configuration.");
/* 228 */       return;
/*     */     }
/*     */ 
/* 231 */     configureHarvester(configuration);
/*     */ 
/* 234 */     fireOnHarvestComplete();
/*     */   }
/*     */ 
/*     */   protected void disabled()
/*     */   {
/* 243 */     Harvest.stop();
/* 244 */     fireOnHarvestDisabled();
/*     */   }
/*     */ 
/*     */   protected void execute()
/*     */   {
/* 253 */     this.log.debug("Harvester state: " + this.state);
/*     */ 
/* 255 */     this.stateChanged = false;
/*     */     try
/*     */     {
/* 258 */       expireHarvestData();
/*     */ 
/* 260 */       switch (1.$SwitchMap$com$newrelic$agent$android$harvest$Harvester$State[this.state.ordinal()]) {
/*     */       case 1:
/* 262 */         uninitialized();
/* 263 */         break;
/*     */       case 2:
/* 265 */         fireOnHarvestBefore();
/* 266 */         disconnected();
/* 267 */         break;
/*     */       case 3:
/* 269 */         fireOnHarvestBefore();
/* 270 */         fireOnHarvest();
/* 271 */         fireOnHarvestFinalize();
/*     */ 
/* 273 */         TaskQueue.synchronousDequeue();
/*     */ 
/* 275 */         connected();
/* 276 */         break;
/*     */       case 4:
/* 278 */         disabled();
/* 279 */         break;
/*     */       default:
/* 281 */         throw new IllegalStateException();
/*     */       }
/*     */     } catch (Exception e) {
/* 284 */       this.log.error("Exception encountered while attempting to harvest", e);
/* 285 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void transition(State newState)
/*     */   {
/* 309 */     if (this.stateChanged) {
/* 310 */       this.log.debug("Ignoring multiple transition: " + newState);
/* 311 */       return;
/*     */     }
/*     */ 
/* 315 */     if (this.state == newState) {
/* 316 */       return;
/*     */     }
/* 318 */     switch (1.$SwitchMap$com$newrelic$agent$android$harvest$Harvester$State[this.state.ordinal()]) {
/*     */     case 1:
/* 320 */       if (!stateIn(newState, new State[] { State.DISCONNECTED, newState, State.CONNECTED, State.DISABLED }))
/*     */       {
/* 322 */         throw new IllegalStateException(); } break;
/*     */     case 2:
/* 324 */       if (!stateIn(newState, new State[] { State.UNINITIALIZED, State.CONNECTED, State.DISABLED }))
/*     */       {
/* 326 */         throw new IllegalStateException(); } break;
/*     */     case 3:
/* 328 */       if (!stateIn(newState, new State[] { State.DISCONNECTED, State.DISABLED }))
/*     */       {
/* 330 */         throw new IllegalStateException(); } break;
/*     */     case 4:
/*     */     default:
/* 333 */       throw new IllegalStateException();
/*     */     }
/* 335 */     changeState(newState);
/*     */   }
/*     */ 
/*     */   private HarvestConfiguration parseHarvesterConfiguration(HarvestResponse response)
/*     */   {
/* 345 */     GsonBuilder gsonBuilder = new GsonBuilder();
/* 346 */     gsonBuilder.registerTypeAdapter(ActivityTraceConfiguration.class, new ActivityTraceConfigurationDeserializer());
/* 347 */     Gson gson = gsonBuilder.create();
/* 348 */     HarvestConfiguration config = null;
/*     */     try {
/* 350 */       config = (HarvestConfiguration)gson.fromJson(response.getResponseBody(), HarvestConfiguration.class);
/*     */     } catch (JsonSyntaxException e) {
/* 352 */       this.log.error("Unable to parse collector configuration: " + e.getMessage());
/* 353 */       AgentHealth.noticeException(e);
/*     */     }
/* 355 */     return config;
/*     */   }
/*     */ 
/*     */   private void configureHarvester(HarvestConfiguration harvestConfiguration) {
/* 359 */     this.configuration.reconfigure(harvestConfiguration);
/* 360 */     this.harvestData.setDataToken(this.configuration.getDataToken());
/* 361 */     Harvest.setHarvestConfiguration(this.configuration);
/*     */   }
/*     */ 
/*     */   private void changeState(State newState)
/*     */   {
/* 366 */     this.log.debug("Harvester changing state: " + this.state + " -> " + newState);
/*     */ 
/* 368 */     if (this.state == State.CONNECTED) {
/* 369 */       if (newState == State.DISCONNECTED)
/* 370 */         fireOnHarvestDisconnected();
/* 371 */       else if (newState == State.DISABLED) {
/* 372 */         fireOnHarvestDisabled();
/*     */       }
/*     */     }
/* 375 */     this.state = newState;
/* 376 */     this.stateChanged = true;
/*     */   }
/*     */ 
/*     */   private boolean stateIn(State testState, State[] legalStates)
/*     */   {
/* 381 */     for (State state : legalStates)
/* 382 */       if (testState == state)
/* 383 */         return true;
/* 384 */     return false;
/*     */   }
/*     */ 
/*     */   public State getCurrentState()
/*     */   {
/* 393 */     return this.state;
/*     */   }
/*     */ 
/*     */   public boolean isDisabled()
/*     */   {
/* 402 */     return State.DISABLED == this.state;
/*     */   }
/*     */ 
/*     */   public void addHarvestListener(HarvestLifecycleAware harvestAware) {
/* 406 */     if (harvestAware == null) {
/* 407 */       this.log.error("Can't add null harvest listener");
/* 408 */       new Exception().printStackTrace();
/* 409 */       return;
/*     */     }
/*     */ 
/* 412 */     synchronized (this.harvestListeners) {
/* 413 */       if (this.harvestListeners.contains(harvestAware))
/* 414 */         return;
/* 415 */       this.harvestListeners.add(harvestAware);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeHarvestListener(HarvestLifecycleAware harvestAware) {
/* 420 */     synchronized (this.harvestListeners) {
/* 421 */       if (!this.harvestListeners.contains(harvestAware))
/* 422 */         return;
/* 423 */       this.harvestListeners.remove(harvestAware);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void expireHarvestData() {
/* 428 */     expireHttpErrors();
/* 429 */     expireHttpTransactions();
/* 430 */     expireActivityTraces();
/*     */   }
/*     */ 
/*     */   public void expireHttpErrors() {
/* 434 */     HttpErrors errors = this.harvestData.getHttpErrors();
/*     */ 
/* 438 */     synchronized (errors) {
/* 439 */       Collection oldErrors = new ArrayList();
/* 440 */       long now = System.currentTimeMillis();
/* 441 */       long maxAge = this.configuration.getReportMaxTransactionAgeMilliseconds();
/*     */ 
/* 444 */       for (HttpError error : errors.getHttpErrors()) {
/* 445 */         if (error.getTimestamp().longValue() < now - maxAge) {
/* 446 */           this.log.debug("HttpError too old, purging: " + error);
/* 447 */           oldErrors.add(error);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 452 */       for (HttpError error : oldErrors)
/* 453 */         errors.removeHttpError(error);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void expireHttpTransactions()
/*     */   {
/* 459 */     HttpTransactions transactions = this.harvestData.getHttpTransactions();
/*     */ 
/* 463 */     synchronized (transactions) {
/* 464 */       Collection oldTransactions = new ArrayList();
/* 465 */       long now = System.currentTimeMillis();
/* 466 */       long maxAge = this.configuration.getReportMaxTransactionAgeMilliseconds();
/*     */ 
/* 469 */       for (HttpTransaction txn : transactions.getHttpTransactions()) {
/* 470 */         if (txn.getTimestamp().longValue() < now - maxAge) {
/* 471 */           this.log.debug("HttpTransaction too old, purging: " + txn);
/* 472 */           oldTransactions.add(txn);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 477 */       for (HttpTransaction txn : oldTransactions)
/* 478 */         transactions.remove(txn);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void expireActivityTraces()
/*     */   {
/* 484 */     ActivityTraces traces = this.harvestData.getActivityTraces();
/*     */ 
/* 488 */     synchronized (traces) {
/* 489 */       Collection expiredTraces = new ArrayList();
/* 490 */       long maxAttempts = this.configuration.getActivity_trace_max_report_attempts();
/*     */ 
/* 493 */       for (ActivityTrace trace : traces.getActivityTraces()) {
/* 494 */         if (trace.getReportAttemptCount() >= maxAttempts) {
/* 495 */           this.log.debug("ActivityTrace has had " + trace.getReportAttemptCount() + " report attempts, purging: " + trace);
/*     */ 
/* 497 */           expiredTraces.add(trace);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 502 */       for (ActivityTrace trace : expiredTraces)
/* 503 */         traces.remove(trace);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setAgentConfiguration(AgentConfiguration agentConfiguration)
/*     */   {
/* 509 */     this.agentConfiguration = agentConfiguration;
/*     */   }
/*     */ 
/*     */   public void setHarvestConnection(HarvestConnection connection) {
/* 513 */     this.harvestConnection = connection;
/*     */   }
/*     */ 
/*     */   public HarvestConnection getHarvestConnection() {
/* 517 */     return this.harvestConnection;
/*     */   }
/*     */ 
/*     */   public void setHarvestData(HarvestData harvestData) {
/* 521 */     this.harvestData = harvestData;
/*     */   }
/*     */ 
/*     */   public HarvestData getHarvestData() {
/* 525 */     return this.harvestData;
/*     */   }
/*     */ 
/*     */   private void fireOnHarvestBefore()
/*     */   {
/*     */     try {
/* 531 */       for (HarvestLifecycleAware harvestAware : getHarvestListeners())
/* 532 */         harvestAware.onHarvestBefore();
/*     */     }
/*     */     catch (Exception e) {
/* 535 */       this.log.error("Error in fireOnHarvestBefore", e);
/* 536 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOnHarvestStart()
/*     */   {
/*     */     try {
/* 543 */       for (HarvestLifecycleAware harvestAware : getHarvestListeners())
/* 544 */         harvestAware.onHarvestStart();
/*     */     }
/*     */     catch (Exception e) {
/* 547 */       this.log.error("Error in fireOnHarvestStart", e);
/* 548 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOnHarvestStop()
/*     */   {
/*     */     try {
/* 555 */       for (HarvestLifecycleAware harvestAware : getHarvestListeners())
/* 556 */         harvestAware.onHarvestStop();
/*     */     }
/*     */     catch (Exception e) {
/* 559 */       this.log.error("Error in fireOnHarvestStop", e);
/* 560 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOnHarvest()
/*     */   {
/*     */     try {
/* 567 */       for (HarvestLifecycleAware harvestAware : getHarvestListeners())
/* 568 */         harvestAware.onHarvest();
/*     */     }
/*     */     catch (Exception e) {
/* 571 */       this.log.error("Error in fireOnHarvest", e);
/* 572 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOnHarvestFinalize()
/*     */   {
/*     */     try {
/* 579 */       for (HarvestLifecycleAware harvestAware : getHarvestListeners())
/* 580 */         harvestAware.onHarvestFinalize();
/*     */     }
/*     */     catch (Exception e) {
/* 583 */       this.log.error("Error in fireOnHarvestFinalize", e);
/* 584 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOnHarvestDisabled()
/*     */   {
/*     */     try {
/* 591 */       for (HarvestLifecycleAware harvestAware : getHarvestListeners())
/* 592 */         harvestAware.onHarvestDisabled();
/*     */     }
/*     */     catch (Exception e) {
/* 595 */       this.log.error("Error in fireOnHarvestDisabled", e);
/* 596 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOnHarvestDisconnected()
/*     */   {
/*     */     try {
/* 603 */       for (HarvestLifecycleAware harvestAware : getHarvestListeners())
/* 604 */         harvestAware.onHarvestDisconnected();
/*     */     }
/*     */     catch (Exception e) {
/* 607 */       this.log.error("Error in fireOnHarvestDisconnected", e);
/* 608 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOnHarvestError()
/*     */   {
/*     */     try {
/* 615 */       for (HarvestLifecycleAware harvestAware : getHarvestListeners())
/* 616 */         harvestAware.onHarvestError();
/*     */     }
/*     */     catch (Exception e) {
/* 619 */       this.log.error("Error in fireOnHarvestError", e);
/* 620 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOnHarvestSendFailed()
/*     */   {
/*     */     try {
/* 627 */       for (HarvestLifecycleAware harvestAware : getHarvestListeners())
/* 628 */         harvestAware.onHarvestSendFailed();
/*     */     }
/*     */     catch (Exception e) {
/* 631 */       this.log.error("Error in fireOnHarvestSendFailed", e);
/* 632 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOnHarvestComplete()
/*     */   {
/*     */     try {
/* 639 */       for (HarvestLifecycleAware harvestAware : getHarvestListeners())
/* 640 */         harvestAware.onHarvestComplete();
/*     */     }
/*     */     catch (Exception e) {
/* 643 */       this.log.error("Error in fireOnHarvestComplete", e);
/* 644 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireOnHarvestConnected()
/*     */   {
/*     */     try {
/* 651 */       for (HarvestLifecycleAware harvestAware : getHarvestListeners())
/* 652 */         harvestAware.onHarvestConnected();
/*     */     }
/*     */     catch (Exception e) {
/* 655 */       this.log.error("Error in fireOnHarvestConnected", e);
/* 656 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setConfiguration(HarvestConfiguration configuration) {
/* 661 */     this.configuration = configuration;
/*     */   }
/*     */ 
/*     */   private Collection<HarvestLifecycleAware> getHarvestListeners() {
/* 665 */     return new ArrayList(this.harvestListeners);
/*     */   }
/*     */ 
/*     */   protected static enum State
/*     */   {
/*  37 */     UNINITIALIZED, 
/*  38 */     DISCONNECTED, 
/*  39 */     CONNECTED, 
/*  40 */     DISABLED;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.Harvester
 * JD-Core Version:    0.6.2
 */