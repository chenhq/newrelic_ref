/*    */ package com.newrelic.javassist.convert;
/*    */ 
/*    */ import com.newrelic.javassist.CannotCompileException;
/*    */ import com.newrelic.javassist.CtClass;
/*    */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*    */ import com.newrelic.javassist.bytecode.CodeIterator;
/*    */ import com.newrelic.javassist.bytecode.ConstPool;
/*    */ 
/*    */ public final class TransformNewClass extends Transformer
/*    */ {
/*    */   private int nested;
/*    */   private String classname;
/*    */   private String newClassName;
/*    */   private int newClassIndex;
/*    */   private int newMethodNTIndex;
/*    */   private int newMethodIndex;
/*    */ 
/*    */   public TransformNewClass(Transformer next, String classname, String newClassName)
/*    */   {
/* 29 */     super(next);
/* 30 */     this.classname = classname;
/* 31 */     this.newClassName = newClassName;
/*    */   }
/*    */ 
/*    */   public void initialize(ConstPool cp, CodeAttribute attr) {
/* 35 */     this.nested = 0;
/* 36 */     this.newClassIndex = (this.newMethodNTIndex = this.newMethodIndex = 0);
/*    */   }
/*    */ 
/*    */   public int transform(CtClass clazz, int pos, CodeIterator iterator, ConstPool cp)
/*    */     throws CannotCompileException
/*    */   {
/* 50 */     int c = iterator.byteAt(pos);
/* 51 */     if (c == 187) {
/* 52 */       int index = iterator.u16bitAt(pos + 1);
/* 53 */       if (cp.getClassInfo(index).equals(this.classname)) {
/* 54 */         if (iterator.byteAt(pos + 3) != 89) {
/* 55 */           throw new CannotCompileException("NEW followed by no DUP was found");
/*    */         }
/*    */ 
/* 58 */         if (this.newClassIndex == 0) {
/* 59 */           this.newClassIndex = cp.addClassInfo(this.newClassName);
/*    */         }
/* 61 */         iterator.write16bit(this.newClassIndex, pos + 1);
/* 62 */         this.nested += 1;
/*    */       }
/*    */     }
/* 65 */     else if (c == 183) {
/* 66 */       int index = iterator.u16bitAt(pos + 1);
/* 67 */       int typedesc = cp.isConstructor(this.classname, index);
/* 68 */       if ((typedesc != 0) && (this.nested > 0)) {
/* 69 */         int nt = cp.getMethodrefNameAndType(index);
/* 70 */         if (this.newMethodNTIndex != nt) {
/* 71 */           this.newMethodNTIndex = nt;
/* 72 */           this.newMethodIndex = cp.addMethodrefInfo(this.newClassIndex, nt);
/*    */         }
/*    */ 
/* 75 */         iterator.write16bit(this.newMethodIndex, pos + 1);
/* 76 */         this.nested -= 1;
/*    */       }
/*    */     }
/*    */ 
/* 80 */     return pos;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.convert.TransformNewClass
 * JD-Core Version:    0.6.2
 */