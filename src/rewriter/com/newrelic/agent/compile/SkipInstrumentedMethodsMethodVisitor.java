/*    */ package com.newrelic.agent.compile;
/*    */ 
/*    */ import com.newrelic.objectweb.asm.AnnotationVisitor;
/*    */ import com.newrelic.objectweb.asm.MethodAdapter;
/*    */ import com.newrelic.objectweb.asm.MethodVisitor;
/*    */ import com.newrelic.objectweb.asm.Type;
/*    */ 
/*    */ class SkipInstrumentedMethodsMethodVisitor extends MethodAdapter
/*    */ {
/*    */   public SkipInstrumentedMethodsMethodVisitor(MethodVisitor mv)
/*    */   {
/* 11 */     super(mv);
/*    */   }
/*    */ 
/*    */   public AnnotationVisitor visitAnnotation(String desc, boolean visible)
/*    */   {
/* 16 */     if (Type.getDescriptor(InstrumentedMethod.class).equals(desc)) {
/* 17 */       throw new SkipException();
/*    */     }
/* 19 */     return super.visitAnnotation(desc, visible);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.SkipInstrumentedMethodsMethodVisitor
 * JD-Core Version:    0.6.2
 */