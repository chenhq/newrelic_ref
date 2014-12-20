/*    */ package com.newrelic.agent.compile.visitor;
/*    */ 
/*    */ import com.newrelic.agent.compile.InstrumentationContext;
/*    */ import com.newrelic.agent.compile.Log;
/*    */ import com.newrelic.objectweb.asm.ClassAdapter;
/*    */ import com.newrelic.objectweb.asm.ClassVisitor;
/*    */ import com.newrelic.objectweb.asm.MethodVisitor;
/*    */ 
/*    */ public class TraceAnnotationClassVisitor extends ClassAdapter
/*    */ {
/*    */   private final InstrumentationContext context;
/*    */ 
/*    */   public TraceAnnotationClassVisitor(ClassVisitor cv, InstrumentationContext context, Log log)
/*    */   {
/* 16 */     super(cv);
/* 17 */     this.context = context;
/*    */   }
/*    */ 
/*    */   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*    */   {
/* 22 */     MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
/*    */ 
/* 24 */     if ((this.context.isTracedMethod(name, desc) & !this.context.isSkippedMethod(name, desc))) {
/* 25 */       this.context.markModified();
/* 26 */       return new TraceMethodVisitor(methodVisitor, access, name, desc, this.context);
/*    */     }
/*    */ 
/* 29 */     return methodVisitor;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.visitor.TraceAnnotationClassVisitor
 * JD-Core Version:    0.6.2
 */