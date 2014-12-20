/*     */ package com.newrelic.org.slf4j;
/*     */ 
/*     */ import com.newrelic.org.slf4j.helpers.NOPLoggerFactory;
/*     */ import com.newrelic.org.slf4j.helpers.SubstituteLoggerFactory;
/*     */ import com.newrelic.org.slf4j.helpers.Util;
/*     */ import com.newrelic.org.slf4j.impl.StaticLoggerBinder;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ 
/*     */ public final class LoggerFactory
/*     */ {
/*     */   static final String CODES_PREFIX = "http://www.slf4j.org/codes.html";
/*     */   static final String NO_STATICLOGGERBINDER_URL = "http://www.slf4j.org/codes.html#StaticLoggerBinder";
/*     */   static final String MULTIPLE_BINDINGS_URL = "http://www.slf4j.org/codes.html#multiple_bindings";
/*     */   static final String NULL_LF_URL = "http://www.slf4j.org/codes.html#null_LF";
/*     */   static final String VERSION_MISMATCH = "http://www.slf4j.org/codes.html#version_mismatch";
/*     */   static final String SUBSTITUTE_LOGGER_URL = "http://www.slf4j.org/codes.html#substituteLogger";
/*     */   static final String UNSUCCESSFUL_INIT_URL = "http://www.slf4j.org/codes.html#unsuccessfulInit";
/*     */   static final String UNSUCCESSFUL_INIT_MSG = "org.slf4j.LoggerFactory could not be successfully initialized. See also http://www.slf4j.org/codes.html#unsuccessfulInit";
/*     */   static final int UNINITIALIZED = 0;
/*     */   static final int ONGOING_INITILIZATION = 1;
/*     */   static final int FAILED_INITILIZATION = 2;
/*     */   static final int SUCCESSFUL_INITILIZATION = 3;
/*     */   static final int NOP_FALLBACK_INITILIZATION = 4;
/*  76 */   static int INITIALIZATION_STATE = 0;
/*  77 */   static SubstituteLoggerFactory TEMP_FACTORY = new SubstituteLoggerFactory();
/*  78 */   static NOPLoggerFactory NOP_FALLBACK_FACTORY = new NOPLoggerFactory();
/*     */ 
/*  87 */   private static final String[] API_COMPATIBILITY_LIST = { "1.6" };
/*     */ 
/* 202 */   private static String STATIC_LOGGER_BINDER_PATH = "com/newrelic/org/slf4j/impl/StaticLoggerBinder.class";
/*     */ 
/*     */   static void reset()
/*     */   {
/* 105 */     INITIALIZATION_STATE = 0;
/* 106 */     TEMP_FACTORY = new SubstituteLoggerFactory();
/*     */   }
/*     */ 
/*     */   private static final void performInitialization() {
/* 110 */     singleImplementationSanityCheck();
/* 111 */     bind();
/* 112 */     if (INITIALIZATION_STATE == 3)
/* 113 */       versionSanityCheck();
/*     */   }
/*     */ 
/*     */   private static final void bind()
/*     */   {
/*     */     try
/*     */     {
/* 121 */       StaticLoggerBinder.getSingleton();
/* 122 */       INITIALIZATION_STATE = 3;
/* 123 */       emitSubstituteLoggerWarning();
/*     */     } catch (NoClassDefFoundError ncde) {
/* 125 */       String msg = ncde.getMessage();
/* 126 */       if ((msg != null) && (msg.indexOf("com/newrelic/org/slf4j/impl/StaticLoggerBinder") != -1)) {
/* 127 */         INITIALIZATION_STATE = 4;
/* 128 */         Util.report("Failed to load class \"org.slf4j.impl.StaticLoggerBinder\".");
/*     */ 
/* 130 */         Util.report("Defaulting to no-operation (NOP) logger implementation");
/* 131 */         Util.report("See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.");
/*     */       }
/*     */       else {
/* 134 */         failedBinding(ncde);
/* 135 */         throw ncde;
/*     */       }
/*     */     } catch (NoSuchMethodError nsme) {
/* 138 */       String msg = nsme.getMessage();
/* 139 */       if ((msg != null) && (msg.indexOf("org.slf4j.impl.StaticLoggerBinder.getSingleton()") != -1)) {
/* 140 */         INITIALIZATION_STATE = 2;
/* 141 */         Util.report("slf4j-api 1.6.x (or later) is incompatible with this binding.");
/* 142 */         Util.report("Your binding is version 1.5.5 or earlier.");
/* 143 */         Util.report("Upgrade your binding to version 1.6.x. or 2.0.x");
/*     */       }
/* 145 */       throw nsme;
/*     */     } catch (Exception e) {
/* 147 */       failedBinding(e);
/* 148 */       throw new IllegalStateException("Unexpected initialization failure", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   static void failedBinding(Throwable t) {
/* 153 */     INITIALIZATION_STATE = 2;
/* 154 */     Util.report("Failed to instantiate SLF4J LoggerFactory", t);
/*     */   }
/*     */ 
/*     */   private static final void emitSubstituteLoggerWarning() {
/* 158 */     List loggerNameList = TEMP_FACTORY.getLoggerNameList();
/* 159 */     if (loggerNameList.size() == 0) {
/* 160 */       return;
/*     */     }
/* 162 */     Util.report("The following loggers will not work becasue they were created");
/*     */ 
/* 164 */     Util.report("during the default configuration phase of the underlying logging system.");
/*     */ 
/* 166 */     Util.report("See also http://www.slf4j.org/codes.html#substituteLogger");
/* 167 */     for (int i = 0; i < loggerNameList.size(); i++) {
/* 168 */       String loggerName = (String)loggerNameList.get(i);
/* 169 */       Util.report(loggerName);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final void versionSanityCheck() {
/*     */     try {
/* 175 */       String requested = StaticLoggerBinder.REQUESTED_API_VERSION;
/*     */ 
/* 177 */       boolean match = false;
/* 178 */       for (int i = 0; i < API_COMPATIBILITY_LIST.length; i++) {
/* 179 */         if (requested.startsWith(API_COMPATIBILITY_LIST[i])) {
/* 180 */           match = true;
/*     */         }
/*     */       }
/* 183 */       if (!match) {
/* 184 */         Util.report("The requested version " + requested + " by your slf4j binding is not compatible with " + Arrays.asList(API_COMPATIBILITY_LIST).toString());
/*     */ 
/* 187 */         Util.report("See http://www.slf4j.org/codes.html#version_mismatch for further details.");
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (NoSuchFieldError nsfe)
/*     */     {
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 196 */       Util.report("Unexpected problem occured during version sanity check", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void singleImplementationSanityCheck()
/*     */   {
/*     */     try
/*     */     {
/* 206 */       ClassLoader loggerFactoryClassLoader = LoggerFactory.class.getClassLoader();
/*     */       Enumeration paths;
/*     */       Enumeration paths;
/* 209 */       if (loggerFactoryClassLoader == null)
/* 210 */         paths = ClassLoader.getSystemResources(STATIC_LOGGER_BINDER_PATH);
/*     */       else {
/* 212 */         paths = loggerFactoryClassLoader.getResources(STATIC_LOGGER_BINDER_PATH);
/*     */       }
/*     */ 
/* 215 */       List implementationList = new ArrayList();
/* 216 */       while (paths.hasMoreElements()) {
/* 217 */         URL path = (URL)paths.nextElement();
/* 218 */         implementationList.add(path);
/*     */       }
/* 220 */       if (implementationList.size() > 1) {
/* 221 */         Util.report("Class path contains multiple SLF4J bindings.");
/* 222 */         for (int i = 0; i < implementationList.size(); i++) {
/* 223 */           Util.report("Found binding in [" + implementationList.get(i) + "]");
/*     */         }
/* 225 */         Util.report("See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.");
/*     */       }
/*     */     } catch (IOException ioe) {
/* 228 */       Util.report("Error getting resources from path", ioe);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name)
/*     */   {
/* 241 */     ILoggerFactory iLoggerFactory = getILoggerFactory();
/* 242 */     return iLoggerFactory.getLogger(name);
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(Class clazz)
/*     */   {
/* 254 */     return getLogger(clazz.getName());
/*     */   }
/*     */ 
/*     */   public static ILoggerFactory getILoggerFactory()
/*     */   {
/* 266 */     if (INITIALIZATION_STATE == 0) {
/* 267 */       INITIALIZATION_STATE = 1;
/* 268 */       performInitialization();
/*     */     }
/*     */ 
/* 271 */     switch (INITIALIZATION_STATE) {
/*     */     case 3:
/* 273 */       return StaticLoggerBinder.getSingleton().getLoggerFactory();
/*     */     case 4:
/* 275 */       return NOP_FALLBACK_FACTORY;
/*     */     case 2:
/* 277 */       throw new IllegalStateException("org.slf4j.LoggerFactory could not be successfully initialized. See also http://www.slf4j.org/codes.html#unsuccessfulInit");
/*     */     case 1:
/* 281 */       return TEMP_FACTORY;
/*     */     }
/* 283 */     throw new IllegalStateException("Unreachable code");
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.LoggerFactory
 * JD-Core Version:    0.6.2
 */