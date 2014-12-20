/*    */ package com.newrelic.javassist;
/*    */ 
/*    */ import com.newrelic.javassist.bytecode.ClassFile;
/*    */ import com.newrelic.javassist.bytecode.InnerClassesAttribute;
/*    */ 
/*    */ class CtNewNestedClass extends CtNewClass
/*    */ {
/*    */   CtNewNestedClass(String realName, ClassPool cp, boolean isInterface, CtClass superclass)
/*    */   {
/* 28 */     super(realName, cp, isInterface, superclass);
/*    */   }
/*    */ 
/*    */   public void setModifiers(int mod)
/*    */   {
/* 35 */     mod &= -9;
/* 36 */     super.setModifiers(mod);
/* 37 */     updateInnerEntry(mod, getName(), this, true);
/*    */   }
/*    */ 
/*    */   private static void updateInnerEntry(int mod, String name, CtClass clazz, boolean outer) {
/* 41 */     ClassFile cf = clazz.getClassFile2();
/* 42 */     InnerClassesAttribute ica = (InnerClassesAttribute)cf.getAttribute("InnerClasses");
/*    */ 
/* 44 */     if (ica == null) {
/* 45 */       return;
/*    */     }
/* 47 */     int n = ica.tableLength();
/* 48 */     for (int i = 0; i < n; i++)
/* 49 */       if (name.equals(ica.innerClass(i))) {
/* 50 */         int acc = ica.accessFlags(i) & 0x8;
/* 51 */         ica.setAccessFlags(i, mod | acc);
/* 52 */         String outName = ica.outerClass(i);
/* 53 */         if ((outName == null) || (!outer)) break;
/*    */         try {
/* 55 */           CtClass parent = clazz.getClassPool().get(outName);
/* 56 */           updateInnerEntry(mod, name, parent, false);
/*    */         }
/*    */         catch (NotFoundException e) {
/* 59 */           throw new RuntimeException("cannot find the declaring class: " + outName);
/*    */         }
/*    */       }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtNewNestedClass
 * JD-Core Version:    0.6.2
 */