/*    */ package com.newrelic.javassist;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.InputStream;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ 
/*    */ public class ByteArrayClassPath
/*    */   implements ClassPath
/*    */ {
/*    */   protected String classname;
/*    */   protected byte[] classfile;
/*    */ 
/*    */   public ByteArrayClassPath(String name, byte[] classfile)
/*    */   {
/* 60 */     this.classname = name;
/* 61 */     this.classfile = classfile;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 70 */     return "byte[]:" + this.classname;
/*    */   }
/*    */ 
/*    */   public InputStream openClassfile(String classname)
/*    */   {
/* 77 */     if (this.classname.equals(classname)) {
/* 78 */       return new ByteArrayInputStream(this.classfile);
/*    */     }
/* 80 */     return null;
/*    */   }
/*    */ 
/*    */   public URL find(String classname)
/*    */   {
/* 87 */     if (this.classname.equals(classname)) {
/* 88 */       String cname = classname.replace('.', '/') + ".class";
/*    */       try
/*    */       {
/* 91 */         return new URL("file:/ByteArrayClassPath/" + cname);
/*    */       }
/*    */       catch (MalformedURLException e) {
/*    */       }
/*    */     }
/* 96 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.ByteArrayClassPath
 * JD-Core Version:    0.6.2
 */