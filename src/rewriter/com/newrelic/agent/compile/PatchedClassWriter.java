/*    */ package com.newrelic.agent.compile;
/*    */ 
/*    */ import com.newrelic.objectweb.asm.ClassWriter;
/*    */ 
/*    */ class PatchedClassWriter extends ClassWriter
/*    */ {
/*    */   private final ClassLoader classLoader;
/*    */ 
/*    */   public PatchedClassWriter(int flags, ClassLoader classLoader)
/*    */   {
/* 16 */     super(flags);
/* 17 */     this.classLoader = classLoader;
/*    */   }
/*    */ 
/*    */   protected String getCommonSuperClass(String type1, String type2)
/*    */   {
/*    */     Class c;
/*    */     Class d;
/*    */     try
/*    */     {
/* 29 */       c = Class.forName(type1.replace('/', '.'), true, this.classLoader);
/* 30 */       d = Class.forName(type2.replace('/', '.'), true, this.classLoader);
/*    */     } catch (Exception e) {
/* 32 */       throw new RuntimeException(e.toString());
/*    */     }
/* 34 */     if (c.isAssignableFrom(d)) {
/* 35 */       return type1;
/*    */     }
/* 37 */     if (d.isAssignableFrom(c)) {
/* 38 */       return type2;
/*    */     }
/* 40 */     if ((c.isInterface()) || (d.isInterface())) {
/* 41 */       return "java/lang/Object";
/*    */     }
/*    */     do
/* 44 */       c = c.getSuperclass();
/* 45 */     while (!c.isAssignableFrom(d));
/* 46 */     return c.getName().replace('.', '/');
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.PatchedClassWriter
 * JD-Core Version:    0.6.2
 */