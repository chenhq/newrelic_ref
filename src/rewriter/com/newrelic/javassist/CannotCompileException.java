/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import com.newrelic.javassist.compiler.CompileError;
/*     */ 
/*     */ public class CannotCompileException extends Exception
/*     */ {
/*     */   private Throwable myCause;
/*     */   private String message;
/*     */ 
/*     */   public Throwable getCause()
/*     */   {
/*  31 */     return this.myCause == this ? null : this.myCause;
/*     */   }
/*     */ 
/*     */   public synchronized Throwable initCause(Throwable cause)
/*     */   {
/*  39 */     this.myCause = cause;
/*  40 */     return this;
/*     */   }
/*     */ 
/*     */   public String getReason()
/*     */   {
/*  49 */     if (this.message != null) {
/*  50 */       return this.message;
/*     */     }
/*  52 */     return toString();
/*     */   }
/*     */ 
/*     */   public CannotCompileException(String msg)
/*     */   {
/*  61 */     super(msg);
/*  62 */     this.message = msg;
/*  63 */     initCause(null);
/*     */   }
/*     */ 
/*     */   public CannotCompileException(Throwable e)
/*     */   {
/*  73 */     super("by " + e.toString());
/*  74 */     this.message = null;
/*  75 */     initCause(e);
/*     */   }
/*     */ 
/*     */   public CannotCompileException(String msg, Throwable e)
/*     */   {
/*  86 */     this(msg);
/*  87 */     initCause(e);
/*     */   }
/*     */ 
/*     */   public CannotCompileException(NotFoundException e)
/*     */   {
/*  95 */     this("cannot find " + e.getMessage(), e);
/*     */   }
/*     */ 
/*     */   public CannotCompileException(CompileError e)
/*     */   {
/* 102 */     this("[source error] " + e.getMessage(), e);
/*     */   }
/*     */ 
/*     */   public CannotCompileException(ClassNotFoundException e, String name)
/*     */   {
/* 110 */     this("cannot find " + name, e);
/*     */   }
/*     */ 
/*     */   public CannotCompileException(ClassFormatError e, String name)
/*     */   {
/* 117 */     this("invalid class format: " + name, e);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CannotCompileException
 * JD-Core Version:    0.6.2
 */