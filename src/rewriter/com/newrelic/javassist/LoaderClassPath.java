/*    */ package com.newrelic.javassist;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.lang.ref.WeakReference;
/*    */ import java.net.URL;
/*    */ 
/*    */ public class LoaderClassPath
/*    */   implements ClassPath
/*    */ {
/*    */   private WeakReference clref;
/*    */ 
/*    */   public LoaderClassPath(ClassLoader cl)
/*    */   {
/* 48 */     this.clref = new WeakReference(cl);
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 52 */     Object cl = null;
/* 53 */     if (this.clref != null) {
/* 54 */       cl = this.clref.get();
/*    */     }
/* 56 */     return cl == null ? "<null>" : cl.toString();
/*    */   }
/*    */ 
/*    */   public InputStream openClassfile(String classname)
/*    */   {
/* 65 */     String cname = classname.replace('.', '/') + ".class";
/* 66 */     ClassLoader cl = (ClassLoader)this.clref.get();
/* 67 */     if (cl == null) {
/* 68 */       return null;
/*    */     }
/* 70 */     return cl.getResourceAsStream(cname);
/*    */   }
/*    */ 
/*    */   public URL find(String classname)
/*    */   {
/* 81 */     String cname = classname.replace('.', '/') + ".class";
/* 82 */     ClassLoader cl = (ClassLoader)this.clref.get();
/* 83 */     if (cl == null) {
/* 84 */       return null;
/*    */     }
/* 86 */     return cl.getResource(cname);
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/* 93 */     this.clref = null;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.LoaderClassPath
 * JD-Core Version:    0.6.2
 */