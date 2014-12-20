/*    */ package com.newrelic.javassist.convert;
/*    */ 
/*    */ import com.newrelic.javassist.CtClass;
/*    */ import com.newrelic.javassist.CtField;
/*    */ import com.newrelic.javassist.Modifier;
/*    */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*    */ import com.newrelic.javassist.bytecode.CodeIterator;
/*    */ import com.newrelic.javassist.bytecode.ConstPool;
/*    */ 
/*    */ public final class TransformFieldAccess extends Transformer
/*    */ {
/*    */   private String newClassname;
/*    */   private String newFieldname;
/*    */   private String fieldname;
/*    */   private CtClass fieldClass;
/*    */   private boolean isPrivate;
/*    */   private int newIndex;
/*    */   private ConstPool constPool;
/*    */ 
/*    */   public TransformFieldAccess(Transformer next, CtField field, String newClassname, String newFieldname)
/*    */   {
/* 36 */     super(next);
/* 37 */     this.fieldClass = field.getDeclaringClass();
/* 38 */     this.fieldname = field.getName();
/* 39 */     this.isPrivate = Modifier.isPrivate(field.getModifiers());
/* 40 */     this.newClassname = newClassname;
/* 41 */     this.newFieldname = newFieldname;
/* 42 */     this.constPool = null;
/*    */   }
/*    */ 
/*    */   public void initialize(ConstPool cp, CodeAttribute attr) {
/* 46 */     if (this.constPool != cp)
/* 47 */       this.newIndex = 0;
/*    */   }
/*    */ 
/*    */   public int transform(CtClass clazz, int pos, CodeIterator iterator, ConstPool cp)
/*    */   {
/* 59 */     int c = iterator.byteAt(pos);
/* 60 */     if ((c == 180) || (c == 178) || (c == 181) || (c == 179))
/*    */     {
/* 62 */       int index = iterator.u16bitAt(pos + 1);
/* 63 */       String typedesc = TransformReadField.isField(clazz.getClassPool(), cp, this.fieldClass, this.fieldname, this.isPrivate, index);
/*    */ 
/* 66 */       if (typedesc != null) {
/* 67 */         if (this.newIndex == 0) {
/* 68 */           int nt = cp.addNameAndTypeInfo(this.newFieldname, typedesc);
/*    */ 
/* 70 */           this.newIndex = cp.addFieldrefInfo(cp.addClassInfo(this.newClassname), nt);
/*    */ 
/* 72 */           this.constPool = cp;
/*    */         }
/*    */ 
/* 75 */         iterator.write16bit(this.newIndex, pos + 1);
/*    */       }
/*    */     }
/*    */ 
/* 79 */     return pos;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.convert.TransformFieldAccess
 * JD-Core Version:    0.6.2
 */