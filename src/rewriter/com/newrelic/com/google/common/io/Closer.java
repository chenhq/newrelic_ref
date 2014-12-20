/*     */ package com.newrelic.com.google.common.io;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.VisibleForTesting;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.base.Throwables;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ public final class Closer
/*     */   implements Closeable
/*     */ {
/*  96 */   private static final Suppressor SUPPRESSOR = SuppressingSuppressor.isAvailable() ? SuppressingSuppressor.INSTANCE : LoggingSuppressor.INSTANCE;
/*     */ 
/*     */   @VisibleForTesting
/*     */   final Suppressor suppressor;
/* 110 */   private final Deque<Closeable> stack = new ArrayDeque(4);
/*     */   private Throwable thrown;
/*     */ 
/* 104 */   public static Closer create() { return new Closer(SUPPRESSOR); }
/*     */ 
/*     */ 
/*     */   @VisibleForTesting
/*     */   Closer(Suppressor suppressor)
/*     */   {
/* 114 */     this.suppressor = ((Suppressor)Preconditions.checkNotNull(suppressor));
/*     */   }
/*     */ 
/*     */   public <C extends Closeable> C register(@Nullable C closeable)
/*     */   {
/* 125 */     if (closeable != null) {
/* 126 */       this.stack.addFirst(closeable);
/*     */     }
/*     */ 
/* 129 */     return closeable;
/*     */   }
/*     */ 
/*     */   public RuntimeException rethrow(Throwable e)
/*     */     throws IOException
/*     */   {
/* 146 */     Preconditions.checkNotNull(e);
/* 147 */     this.thrown = e;
/* 148 */     Throwables.propagateIfPossible(e, IOException.class);
/* 149 */     throw new RuntimeException(e);
/*     */   }
/*     */ 
/*     */   public <X extends Exception> RuntimeException rethrow(Throwable e, Class<X> declaredType)
/*     */     throws IOException, Exception
/*     */   {
/* 168 */     Preconditions.checkNotNull(e);
/* 169 */     this.thrown = e;
/* 170 */     Throwables.propagateIfPossible(e, IOException.class);
/* 171 */     Throwables.propagateIfPossible(e, declaredType);
/* 172 */     throw new RuntimeException(e);
/*     */   }
/*     */ 
/*     */   public <X1 extends Exception, X2 extends Exception> RuntimeException rethrow(Throwable e, Class<X1> declaredType1, Class<X2> declaredType2)
/*     */     throws IOException, Exception, Exception
/*     */   {
/* 192 */     Preconditions.checkNotNull(e);
/* 193 */     this.thrown = e;
/* 194 */     Throwables.propagateIfPossible(e, IOException.class);
/* 195 */     Throwables.propagateIfPossible(e, declaredType1, declaredType2);
/* 196 */     throw new RuntimeException(e);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 208 */     Throwable throwable = this.thrown;
/*     */ 
/* 211 */     while (!this.stack.isEmpty()) {
/* 212 */       Closeable closeable = (Closeable)this.stack.removeFirst();
/*     */       try {
/* 214 */         closeable.close();
/*     */       } catch (Throwable e) {
/* 216 */         if (throwable == null)
/* 217 */           throwable = e;
/*     */         else {
/* 219 */           this.suppressor.suppress(closeable, throwable, e);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 224 */     if ((this.thrown == null) && (throwable != null)) {
/* 225 */       Throwables.propagateIfPossible(throwable, IOException.class);
/* 226 */       throw new AssertionError(throwable);
/*     */     }
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static final class SuppressingSuppressor
/*     */     implements Closer.Suppressor
/*     */   {
/* 263 */     static final SuppressingSuppressor INSTANCE = new SuppressingSuppressor();
/*     */ 
/* 269 */     static final Method addSuppressed = getAddSuppressed();
/*     */ 
/*     */     static boolean isAvailable()
/*     */     {
/* 266 */       return addSuppressed != null;
/*     */     }
/*     */ 
/*     */     private static Method getAddSuppressed()
/*     */     {
/*     */       try
/*     */       {
/* 273 */         return Throwable.class.getMethod("addSuppressed", new Class[] { Throwable.class }); } catch (Throwable e) {
/*     */       }
/* 275 */       return null;
/*     */     }
/*     */ 
/*     */     public void suppress(Closeable closeable, Throwable thrown, Throwable suppressed)
/*     */     {
/* 282 */       if (thrown == suppressed)
/* 283 */         return;
/*     */       try
/*     */       {
/* 286 */         addSuppressed.invoke(thrown, new Object[] { suppressed });
/*     */       }
/*     */       catch (Throwable e) {
/* 289 */         Closer.LoggingSuppressor.INSTANCE.suppress(closeable, thrown, suppressed);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static final class LoggingSuppressor
/*     */     implements Closer.Suppressor
/*     */   {
/* 247 */     static final LoggingSuppressor INSTANCE = new LoggingSuppressor();
/*     */ 
/*     */     public void suppress(Closeable closeable, Throwable thrown, Throwable suppressed)
/*     */     {
/* 252 */       Closeables.logger.log(Level.WARNING, "Suppressing exception thrown when closing " + closeable, suppressed);
/*     */     }
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static abstract interface Suppressor
/*     */   {
/*     */     public abstract void suppress(Closeable paramCloseable, Throwable paramThrowable1, Throwable paramThrowable2);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.io.Closer
 * JD-Core Version:    0.6.2
 */