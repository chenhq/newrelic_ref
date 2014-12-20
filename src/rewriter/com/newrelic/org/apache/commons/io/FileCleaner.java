/*     */ package com.newrelic.org.apache.commons.io;
/*     */ 
/*     */ import java.io.File;
/*     */ 
/*     */ @Deprecated
/*     */ public class FileCleaner
/*     */ {
/*  43 */   static final FileCleaningTracker theInstance = new FileCleaningTracker();
/*     */ 
/*     */   @Deprecated
/*     */   public static void track(File file, Object marker)
/*     */   {
/*  58 */     theInstance.track(file, marker);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void track(File file, Object marker, FileDeleteStrategy deleteStrategy)
/*     */   {
/*  74 */     theInstance.track(file, marker, deleteStrategy);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void track(String path, Object marker)
/*     */   {
/*  89 */     theInstance.track(path, marker);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void track(String path, Object marker, FileDeleteStrategy deleteStrategy)
/*     */   {
/* 105 */     theInstance.track(path, marker, deleteStrategy);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static int getTrackCount()
/*     */   {
/* 118 */     return theInstance.getTrackCount();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static synchronized void exitWhenFinished()
/*     */   {
/* 145 */     theInstance.exitWhenFinished();
/*     */   }
/*     */ 
/*     */   public static FileCleaningTracker getInstance()
/*     */   {
/* 157 */     return theInstance;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.FileCleaner
 * JD-Core Version:    0.6.2
 */