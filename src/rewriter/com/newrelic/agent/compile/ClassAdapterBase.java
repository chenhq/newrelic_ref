/*    */ package com.newrelic.agent.compile;
/*    */ 
/*    */ import com.newrelic.objectweb.asm.ClassAdapter;
/*    */ import com.newrelic.objectweb.asm.ClassVisitor;
/*    */ import com.newrelic.objectweb.asm.MethodVisitor;
/*    */ import com.newrelic.objectweb.asm.commons.Method;
/*    */ import java.util.Map;
/*    */ 
/*    */ class ClassAdapterBase extends ClassAdapter
/*    */ {
/*    */   final Map<Method, MethodVisitorFactory> methodVisitors;
/*    */   private final Log log;
/*    */ 
/*    */   public ClassAdapterBase(Log log, ClassVisitor cv, Map<Method, MethodVisitorFactory> methodVisitors)
/*    */   {
/* 20 */     super(cv);
/* 21 */     this.methodVisitors = methodVisitors;
/* 22 */     this.log = log;
/*    */   }
/*    */ 
/*    */   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*    */   {
/* 27 */     MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
/*    */ 
/* 30 */     MethodVisitorFactory factory = (MethodVisitorFactory)this.methodVisitors.get(new Method(name, desc));
/* 31 */     if (factory != null)
/*    */     {
/* 33 */       return new SkipInstrumentedMethodsMethodVisitor(factory.create(mv, access, name, desc));
/*    */     }
/*    */ 
/* 37 */     return mv;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.ClassAdapterBase
 * JD-Core Version:    0.6.2
 */