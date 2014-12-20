/*    */ package com.newrelic.javassist.bytecode.annotation;
/*    */ 
/*    */ import com.newrelic.javassist.ClassPool;
/*    */ import com.newrelic.javassist.bytecode.ConstPool;
/*    */ import com.newrelic.javassist.bytecode.Descriptor;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public abstract class MemberValue
/*    */ {
/*    */   ConstPool cp;
/*    */   char tag;
/*    */ 
/*    */   MemberValue(char tag, ConstPool cp)
/*    */   {
/* 38 */     this.cp = cp;
/* 39 */     this.tag = tag;
/*    */   }
/*    */ 
/*    */   abstract Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod)
/*    */     throws ClassNotFoundException;
/*    */ 
/*    */   abstract Class getType(ClassLoader paramClassLoader)
/*    */     throws ClassNotFoundException;
/*    */ 
/*    */   static Class loadClass(ClassLoader cl, String classname)
/*    */     throws ClassNotFoundException, NoSuchClassError
/*    */   {
/*    */     try
/*    */     {
/* 55 */       return Class.forName(convertFromArray(classname), true, cl);
/*    */     }
/*    */     catch (LinkageError e) {
/* 58 */       throw new NoSuchClassError(classname, e);
/*    */     }
/*    */   }
/*    */ 
/*    */   private static String convertFromArray(String classname)
/*    */   {
/* 64 */     int index = classname.indexOf("[]");
/* 65 */     if (index != -1) {
/* 66 */       String rawType = classname.substring(0, index);
/* 67 */       StringBuffer sb = new StringBuffer(Descriptor.of(rawType));
/* 68 */       while (index != -1) {
/* 69 */         sb.insert(0, "[");
/* 70 */         index = classname.indexOf("[]", index + 1);
/*    */       }
/* 72 */       return sb.toString().replace('/', '.');
/*    */     }
/* 74 */     return classname;
/*    */   }
/*    */ 
/*    */   public abstract void accept(MemberValueVisitor paramMemberValueVisitor);
/*    */ 
/*    */   public abstract void write(AnnotationsWriter paramAnnotationsWriter)
/*    */     throws IOException;
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.MemberValue
 * JD-Core Version:    0.6.2
 */