/*    */ package com.newrelic.javassist.convert;
/*    */ 
/*    */ import com.newrelic.javassist.CtClass;
/*    */ import com.newrelic.javassist.CtField;
/*    */ import com.newrelic.javassist.bytecode.BadBytecode;
/*    */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*    */ import com.newrelic.javassist.bytecode.CodeIterator;
/*    */ import com.newrelic.javassist.bytecode.ConstPool;
/*    */ 
/*    */ public final class TransformWriteField extends TransformReadField
/*    */ {
/*    */   public TransformWriteField(Transformer next, CtField field, String methodClassname, String methodName)
/*    */   {
/* 26 */     super(next, field, methodClassname, methodName);
/*    */   }
/*    */ 
/*    */   public int transform(CtClass tclazz, int pos, CodeIterator iterator, ConstPool cp)
/*    */     throws BadBytecode
/*    */   {
/* 32 */     int c = iterator.byteAt(pos);
/* 33 */     if ((c == 181) || (c == 179)) {
/* 34 */       int index = iterator.u16bitAt(pos + 1);
/* 35 */       String typedesc = isField(tclazz.getClassPool(), cp, this.fieldClass, this.fieldname, this.isPrivate, index);
/*    */ 
/* 37 */       if (typedesc != null) {
/* 38 */         if (c == 179) {
/* 39 */           CodeAttribute ca = iterator.get();
/* 40 */           iterator.move(pos);
/* 41 */           char c0 = typedesc.charAt(0);
/* 42 */           if ((c0 == 'J') || (c0 == 'D'))
/*    */           {
/* 44 */             pos = iterator.insertGap(3);
/* 45 */             iterator.writeByte(1, pos);
/* 46 */             iterator.writeByte(91, pos + 1);
/* 47 */             iterator.writeByte(87, pos + 2);
/* 48 */             ca.setMaxStack(ca.getMaxStack() + 2);
/*    */           }
/*    */           else
/*    */           {
/* 52 */             pos = iterator.insertGap(2);
/* 53 */             iterator.writeByte(1, pos);
/* 54 */             iterator.writeByte(95, pos + 1);
/* 55 */             ca.setMaxStack(ca.getMaxStack() + 1);
/*    */           }
/*    */ 
/* 58 */           pos = iterator.next();
/*    */         }
/*    */ 
/* 61 */         int mi = cp.addClassInfo(this.methodClassname);
/* 62 */         String type = "(Ljava/lang/Object;" + typedesc + ")V";
/* 63 */         int methodref = cp.addMethodrefInfo(mi, this.methodName, type);
/* 64 */         iterator.writeByte(184, pos);
/* 65 */         iterator.write16bit(methodref, pos + 1);
/*    */       }
/*    */     }
/*    */ 
/* 69 */     return pos;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.convert.TransformWriteField
 * JD-Core Version:    0.6.2
 */