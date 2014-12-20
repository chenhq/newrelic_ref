/*    */ package com.newrelic.javassist;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ 
/*    */ public class ClassClassPath
/*    */   implements ClassPath
/*    */ {
/*    */   private Class thisClass;
/*    */ 
/*    */   public ClassClassPath(Class c)
/*    */   {
/* 54 */     this.thisClass = c;
/*    */   }
/*    */ 
/*    */   ClassClassPath()
/*    */   {
/* 66 */     this(Object.class);
/*    */   }
/*    */ 
/*    */   public InputStream openClassfile(String classname)
/*    */   {
/* 73 */     String jarname = "/" + classname.replace('.', '/') + ".class";
/* 74 */     return this.thisClass.getResourceAsStream(jarname);
/*    */   }
/*    */ 
/*    */   public URL find(String classname)
/*    */   {
/* 83 */     String jarname = "/" + classname.replace('.', '/') + ".class";
/* 84 */     return this.thisClass.getResource(jarname);
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 94 */     return this.thisClass.getName() + ".class";
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.ClassClassPath
 * JD-Core Version:    0.6.2
 */