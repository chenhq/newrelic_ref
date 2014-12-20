/*    */ package com.newrelic.javassist.util.proxy;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectStreamClass;
/*    */ 
/*    */ public class ProxyObjectInputStream extends ObjectInputStream
/*    */ {
/*    */   private ClassLoader loader;
/*    */ 
/*    */   public ProxyObjectInputStream(InputStream in)
/*    */     throws IOException
/*    */   {
/* 45 */     super(in);
/* 46 */     this.loader = Thread.currentThread().getContextClassLoader();
/* 47 */     if (this.loader == null)
/* 48 */       this.loader = ClassLoader.getSystemClassLoader();
/*    */   }
/*    */ 
/*    */   public void setClassLoader(ClassLoader loader)
/*    */   {
/* 58 */     if (loader != null)
/* 59 */       this.loader = loader;
/*    */     else
/* 61 */       loader = ClassLoader.getSystemClassLoader();
/*    */   }
/*    */ 
/*    */   protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException
/*    */   {
/* 66 */     boolean isProxy = readBoolean();
/* 67 */     if (isProxy) {
/* 68 */       String name = (String)readObject();
/* 69 */       Class superClass = this.loader.loadClass(name);
/* 70 */       int length = readInt();
/* 71 */       Class[] interfaces = new Class[length];
/* 72 */       for (int i = 0; i < length; i++) {
/* 73 */         name = (String)readObject();
/* 74 */         interfaces[i] = this.loader.loadClass(name);
/*    */       }
/* 76 */       length = readInt();
/* 77 */       byte[] signature = new byte[length];
/* 78 */       read(signature);
/* 79 */       ProxyFactory factory = new ProxyFactory();
/*    */ 
/* 82 */       factory.setUseCache(true);
/* 83 */       factory.setUseWriteReplace(false);
/* 84 */       factory.setSuperclass(superClass);
/* 85 */       factory.setInterfaces(interfaces);
/* 86 */       Class proxyClass = factory.createClass(signature);
/* 87 */       return ObjectStreamClass.lookup(proxyClass);
/*    */     }
/* 89 */     return super.readClassDescriptor();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.util.proxy.ProxyObjectInputStream
 * JD-Core Version:    0.6.2
 */