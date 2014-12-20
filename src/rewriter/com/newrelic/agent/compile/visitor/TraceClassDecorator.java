/*    */ package com.newrelic.agent.compile.visitor;
/*    */ 
/*    */ import com.newrelic.objectweb.asm.ClassAdapter;
/*    */ import com.newrelic.objectweb.asm.Label;
/*    */ import com.newrelic.objectweb.asm.MethodVisitor;
/*    */ import com.newrelic.objectweb.asm.Type;
/*    */ import com.newrelic.objectweb.asm.commons.GeneratorAdapter;
/*    */ import com.newrelic.objectweb.asm.commons.Method;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class TraceClassDecorator
/*    */ {
/*    */   private ClassAdapter adapter;
/*    */ 
/*    */   public TraceClassDecorator(ClassAdapter adapter)
/*    */   {
/* 18 */     this.adapter = adapter;
/*    */   }
/*    */ 
/*    */   public void addTraceField() {
/* 22 */     this.adapter.visitField(1, "_nr_trace", "Lcom/newrelic/agent/android/tracing/Trace;", null, null);
/*    */   }
/*    */ 
/*    */   public static String[] addInterface(String[] interfaces) {
/* 26 */     ArrayList newInterfaces = new ArrayList(Arrays.asList(interfaces));
/* 27 */     newInterfaces.add("com/newrelic/agent/android/api/v2/TraceFieldInterface");
/*    */ 
/* 29 */     return (String[])newInterfaces.toArray(new String[newInterfaces.size()]);
/*    */   }
/*    */ 
/*    */   public void addTraceInterface(final Type ownerType) {
/* 33 */     MethodVisitor mv = this.adapter.visitMethod(1, "_nr_setTrace", "(Lcom/newrelic/agent/android/tracing/Trace;)V", null, null);
/*    */ 
/* 35 */     Method method = new Method("_nr_setTrace", "(Lcom/newrelic/agent/android/tracing/Trace;)V");
/* 36 */     mv = new GeneratorAdapter(1, method, mv)
/*    */     {
/*    */       public void visitCode() {
/* 39 */         Label tryStart = new Label();
/* 40 */         Label tryEnd = new Label();
/* 41 */         Label tryHandler = new Label();
/*    */ 
/* 43 */         super.visitCode();
/*    */ 
/* 46 */         visitLabel(tryStart);
/* 47 */         loadThis();
/* 48 */         loadArgs();
/* 49 */         putField(ownerType, "_nr_trace", Type.getType("Lcom/newrelic/agent/android/tracing/Trace;"));
/* 50 */         goTo(tryEnd);
/*    */ 
/* 52 */         visitLabel(tryHandler);
/*    */ 
/* 54 */         pop();
/* 55 */         visitLabel(tryEnd);
/* 56 */         visitTryCatchBlock(tryStart, tryEnd, tryHandler, "java/lang/Exception");
/* 57 */         visitInsn(177);
/*    */       }
/*    */     };
/* 61 */     mv.visitCode();
/* 62 */     mv.visitMaxs(0, 0);
/* 63 */     mv.visitEnd();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.visitor.TraceClassDecorator
 * JD-Core Version:    0.6.2
 */