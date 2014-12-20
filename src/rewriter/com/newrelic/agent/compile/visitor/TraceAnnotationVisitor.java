/*    */ package com.newrelic.agent.compile.visitor;
/*    */ 
/*    */ import com.newrelic.agent.compile.InstrumentationContext;
/*    */ import com.newrelic.agent.compile.Log;
/*    */ import com.newrelic.agent.util.AnnotationImpl;
/*    */ import com.newrelic.objectweb.asm.Type;
/*    */ 
/*    */ public class TraceAnnotationVisitor extends AnnotationImpl
/*    */ {
/*    */   final Log log;
/*    */   final InstrumentationContext context;
/*    */ 
/*    */   public TraceAnnotationVisitor(String name, InstrumentationContext context)
/*    */   {
/* 13 */     super(name);
/* 14 */     this.context = context;
/* 15 */     this.log = context.getLog();
/*    */   }
/*    */ 
/*    */   public void visitEnum(String parameterName, String desc, String value)
/*    */   {
/* 20 */     super.visitEnum(parameterName, desc, value);
/* 21 */     String className = Type.getType(desc).getClassName();
/* 22 */     this.context.addTracedMethodParameter(getName(), parameterName, className, value);
/*    */   }
/*    */ 
/*    */   public void visit(String parameterName, Object value)
/*    */   {
/* 27 */     super.visit(parameterName, value);
/* 28 */     String className = value.getClass().getName();
/* 29 */     this.context.addTracedMethodParameter(getName(), parameterName, className, value.toString());
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.visitor.TraceAnnotationVisitor
 * JD-Core Version:    0.6.2
 */