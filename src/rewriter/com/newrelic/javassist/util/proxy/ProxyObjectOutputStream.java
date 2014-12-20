/*    */ package com.newrelic.javassist.util.proxy;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.io.ObjectStreamClass;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class ProxyObjectOutputStream extends ObjectOutputStream
/*    */ {
/*    */   public ProxyObjectOutputStream(OutputStream out)
/*    */     throws IOException
/*    */   {
/* 43 */     super(out);
/*    */   }
/*    */ 
/*    */   protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
/* 47 */     Class cl = desc.forClass();
/* 48 */     if (ProxyFactory.isProxyClass(cl)) {
/* 49 */       writeBoolean(true);
/* 50 */       Class superClass = cl.getSuperclass();
/* 51 */       Class[] interfaces = cl.getInterfaces();
/* 52 */       byte[] signature = ProxyFactory.getFilterSignature(cl);
/* 53 */       String name = superClass.getName();
/* 54 */       writeObject(name);
/*    */ 
/* 56 */       writeInt(interfaces.length - 1);
/* 57 */       for (int i = 0; i < interfaces.length; i++) {
/* 58 */         Class interfaze = interfaces[i];
/* 59 */         if (interfaze != ProxyObject.class) {
/* 60 */           name = interfaces[i].getName();
/* 61 */           writeObject(name);
/*    */         }
/*    */       }
/* 64 */       writeInt(signature.length);
/* 65 */       write(signature);
/*    */     } else {
/* 67 */       writeBoolean(false);
/* 68 */       super.writeClassDescriptor(desc);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.util.proxy.ProxyObjectOutputStream
 * JD-Core Version:    0.6.2
 */