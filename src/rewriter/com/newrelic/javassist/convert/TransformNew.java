/*    */ package com.newrelic.javassist.convert;
/*    */ 
/*    */ import com.newrelic.javassist.CannotCompileException;
/*    */ import com.newrelic.javassist.CtClass;
/*    */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*    */ import com.newrelic.javassist.bytecode.CodeIterator;
/*    */ import com.newrelic.javassist.bytecode.ConstPool;
/*    */ import com.newrelic.javassist.bytecode.Descriptor;
/*    */ import com.newrelic.javassist.bytecode.StackMap;
/*    */ import com.newrelic.javassist.bytecode.StackMapTable;
/*    */ 
/*    */ public final class TransformNew extends Transformer
/*    */ {
/*    */   private int nested;
/*    */   private String classname;
/*    */   private String trapClass;
/*    */   private String trapMethod;
/*    */ 
/*    */   public TransformNew(Transformer next, String classname, String trapClass, String trapMethod)
/*    */   {
/* 28 */     super(next);
/* 29 */     this.classname = classname;
/* 30 */     this.trapClass = trapClass;
/* 31 */     this.trapMethod = trapMethod;
/*    */   }
/*    */ 
/*    */   public void initialize(ConstPool cp, CodeAttribute attr) {
/* 35 */     this.nested = 0;
/*    */   }
/*    */ 
/*    */   public int transform(CtClass clazz, int pos, CodeIterator iterator, ConstPool cp)
/*    */     throws CannotCompileException
/*    */   {
/* 54 */     int c = iterator.byteAt(pos);
/* 55 */     if (c == 187) {
/* 56 */       int index = iterator.u16bitAt(pos + 1);
/* 57 */       if (cp.getClassInfo(index).equals(this.classname)) {
/* 58 */         if (iterator.byteAt(pos + 3) != 89) {
/* 59 */           throw new CannotCompileException("NEW followed by no DUP was found");
/*    */         }
/*    */ 
/* 62 */         iterator.writeByte(0, pos);
/* 63 */         iterator.writeByte(0, pos + 1);
/* 64 */         iterator.writeByte(0, pos + 2);
/* 65 */         iterator.writeByte(0, pos + 3);
/* 66 */         this.nested += 1;
/*    */ 
/* 68 */         StackMapTable smt = (StackMapTable)iterator.get().getAttribute("StackMapTable");
/*    */ 
/* 70 */         if (smt != null) {
/* 71 */           smt.removeNew(pos);
/*    */         }
/* 73 */         StackMap sm = (StackMap)iterator.get().getAttribute("StackMap");
/*    */ 
/* 75 */         if (sm != null)
/* 76 */           sm.removeNew(pos);
/*    */       }
/*    */     }
/* 79 */     else if (c == 183) {
/* 80 */       int index = iterator.u16bitAt(pos + 1);
/* 81 */       int typedesc = cp.isConstructor(this.classname, index);
/* 82 */       if ((typedesc != 0) && (this.nested > 0)) {
/* 83 */         int methodref = computeMethodref(typedesc, cp);
/* 84 */         iterator.writeByte(184, pos);
/* 85 */         iterator.write16bit(methodref, pos + 1);
/* 86 */         this.nested -= 1;
/*    */       }
/*    */     }
/*    */ 
/* 90 */     return pos;
/*    */   }
/*    */ 
/*    */   private int computeMethodref(int typedesc, ConstPool cp) {
/* 94 */     int classIndex = cp.addClassInfo(this.trapClass);
/* 95 */     int mnameIndex = cp.addUtf8Info(this.trapMethod);
/* 96 */     typedesc = cp.addUtf8Info(Descriptor.changeReturnType(this.classname, cp.getUtf8Info(typedesc)));
/*    */ 
/* 99 */     return cp.addMethodrefInfo(classIndex, cp.addNameAndTypeInfo(mnameIndex, typedesc));
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.convert.TransformNew
 * JD-Core Version:    0.6.2
 */