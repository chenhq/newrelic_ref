/*     */ package com.newrelic.javassist.tools.reflect;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ 
/*     */ public class Loader extends com.newrelic.javassist.Loader
/*     */ {
/*     */   protected Reflection reflection;
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Throwable
/*     */   {
/* 124 */     Loader cl = new Loader();
/* 125 */     cl.run(args);
/*     */   }
/*     */ 
/*     */   public Loader()
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/* 133 */     delegateLoadingOf("com.newrelic.javassist.tools.reflect.Loader");
/*     */ 
/* 135 */     this.reflection = new Reflection();
/* 136 */     ClassPool pool = ClassPool.getDefault();
/* 137 */     addTranslator(pool, this.reflection);
/*     */   }
/*     */ 
/*     */   public boolean makeReflective(String clazz, String metaobject, String metaclass)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/* 161 */     return this.reflection.makeReflective(clazz, metaobject, metaclass);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.reflect.Loader
 * JD-Core Version:    0.6.2
 */