/*     */ package com.newrelic.com.google.common.io;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.VisibleForTesting;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ public final class Closeables
/*     */ {
/*     */ 
/*     */   @VisibleForTesting
/*  39 */   static final Logger logger = Logger.getLogger(Closeables.class.getName());
/*     */ 
/*     */   public static void close(@Nullable Closeable closeable, boolean swallowIOException)
/*     */     throws IOException
/*     */   {
/*  75 */     if (closeable == null)
/*  76 */       return;
/*     */     try
/*     */     {
/*  79 */       closeable.close();
/*     */     } catch (IOException e) {
/*  81 */       if (swallowIOException) {
/*  82 */         logger.log(Level.WARNING, "IOException thrown while closing Closeable.", e);
/*     */       }
/*     */       else
/*  85 */         throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void closeQuietly(@Nullable InputStream inputStream)
/*     */   {
/*     */     try
/*     */     {
/* 106 */       close(inputStream, true);
/*     */     } catch (IOException impossible) {
/* 108 */       throw new AssertionError(impossible);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void closeQuietly(@Nullable Reader reader)
/*     */   {
/*     */     try
/*     */     {
/* 127 */       close(reader, true);
/*     */     } catch (IOException impossible) {
/* 129 */       throw new AssertionError(impossible);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.io.Closeables
 * JD-Core Version:    0.6.2
 */