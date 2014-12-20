/*    */ package com.newrelic.javassist.convert;
/*    */ 
/*    */ import com.newrelic.javassist.ClassPool;
/*    */ import com.newrelic.javassist.CtClass;
/*    */ import com.newrelic.javassist.CtField;
/*    */ import com.newrelic.javassist.Modifier;
/*    */ import com.newrelic.javassist.NotFoundException;
/*    */ import com.newrelic.javassist.bytecode.BadBytecode;
/*    */ import com.newrelic.javassist.bytecode.CodeIterator;
/*    */ import com.newrelic.javassist.bytecode.ConstPool;
/*    */ 
/*    */ public class TransformReadField extends Transformer
/*    */ {
/*    */   protected String fieldname;
/*    */   protected CtClass fieldClass;
/*    */   protected boolean isPrivate;
/*    */   protected String methodClassname;
/*    */   protected String methodName;
/*    */ 
/*    */   public TransformReadField(Transformer next, CtField field, String methodClassname, String methodName)
/*    */   {
/* 34 */     super(next);
/* 35 */     this.fieldClass = field.getDeclaringClass();
/* 36 */     this.fieldname = field.getName();
/* 37 */     this.methodClassname = methodClassname;
/* 38 */     this.methodName = methodName;
/* 39 */     this.isPrivate = Modifier.isPrivate(field.getModifiers());
/*    */   }
/*    */ 
/*    */   static String isField(ClassPool pool, ConstPool cp, CtClass fclass, String fname, boolean is_private, int index)
/*    */   {
/* 44 */     if (!cp.getFieldrefName(index).equals(fname))
/* 45 */       return null;
/*    */     try
/*    */     {
/* 48 */       CtClass c = pool.get(cp.getFieldrefClassName(index));
/* 49 */       if ((c == fclass) || ((!is_private) && (isFieldInSuper(c, fclass, fname))))
/* 50 */         return cp.getFieldrefType(index);
/*    */     } catch (NotFoundException e) {
/*    */     }
/* 53 */     return null;
/*    */   }
/*    */ 
/*    */   static boolean isFieldInSuper(CtClass clazz, CtClass fclass, String fname) {
/* 57 */     if (!clazz.subclassOf(fclass))
/* 58 */       return false;
/*    */     try
/*    */     {
/* 61 */       CtField f = clazz.getField(fname);
/* 62 */       return f.getDeclaringClass() == fclass;
/*    */     } catch (NotFoundException e) {
/*    */     }
/* 65 */     return false;
/*    */   }
/*    */ 
/*    */   public int transform(CtClass tclazz, int pos, CodeIterator iterator, ConstPool cp)
/*    */     throws BadBytecode
/*    */   {
/* 71 */     int c = iterator.byteAt(pos);
/* 72 */     if ((c == 180) || (c == 178)) {
/* 73 */       int index = iterator.u16bitAt(pos + 1);
/* 74 */       String typedesc = isField(tclazz.getClassPool(), cp, this.fieldClass, this.fieldname, this.isPrivate, index);
/*    */ 
/* 76 */       if (typedesc != null) {
/* 77 */         if (c == 178) {
/* 78 */           iterator.move(pos);
/* 79 */           pos = iterator.insertGap(1);
/* 80 */           iterator.writeByte(1, pos);
/* 81 */           pos = iterator.next();
/*    */         }
/*    */ 
/* 84 */         String type = "(Ljava/lang/Object;)" + typedesc;
/* 85 */         int mi = cp.addClassInfo(this.methodClassname);
/* 86 */         int methodref = cp.addMethodrefInfo(mi, this.methodName, type);
/* 87 */         iterator.writeByte(184, pos);
/* 88 */         iterator.write16bit(methodref, pos + 1);
/* 89 */         return pos;
/*    */       }
/*    */     }
/*    */ 
/* 93 */     return pos;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.convert.TransformReadField
 * JD-Core Version:    0.6.2
 */