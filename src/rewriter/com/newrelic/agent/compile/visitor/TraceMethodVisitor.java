/*     */ package com.newrelic.agent.compile.visitor;
/*     */ 
/*     */ import com.newrelic.agent.compile.InstrumentationContext;
/*     */ import com.newrelic.agent.compile.Log;
/*     */ import com.newrelic.objectweb.asm.Label;
/*     */ import com.newrelic.objectweb.asm.MethodVisitor;
/*     */ import com.newrelic.objectweb.asm.Type;
/*     */ import com.newrelic.objectweb.asm.commons.AdviceAdapter;
/*     */ import com.newrelic.objectweb.asm.commons.Method;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class TraceMethodVisitor extends AdviceAdapter
/*     */ {
/*     */   public static final String TRACE_MACHINE_INTERNAL_CLASSNAME = "com/newrelic/agent/android/tracing/TraceMachine";
/*     */   protected final InstrumentationContext context;
/*     */   protected final Log log;
/*     */   private String name;
/*  24 */   protected Boolean unloadContext = Boolean.valueOf(false);
/*  25 */   protected Boolean startTracing = Boolean.valueOf(false);
/*     */   private int access;
/*     */ 
/*     */   public TraceMethodVisitor(MethodVisitor mv, int access, String name, String desc, InstrumentationContext context)
/*     */   {
/*  29 */     super(mv, access, name, desc);
/*  30 */     this.access = access;
/*  31 */     this.context = context;
/*  32 */     this.log = context.getLog();
/*  33 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public void setUnloadContext() {
/*  37 */     this.unloadContext = Boolean.valueOf(true);
/*     */   }
/*     */ 
/*     */   public void setStartTracing() {
/*  41 */     this.startTracing = Boolean.valueOf(true);
/*     */   }
/*     */ 
/*     */   protected void onMethodEnter()
/*     */   {
/*  46 */     Type targetType = Type.getObjectType("com/newrelic/agent/android/tracing/TraceMachine");
/*  47 */     if (this.startTracing.booleanValue())
/*     */     {
/*  49 */       super.visitLdcInsn(this.context.getSimpleClassName());
/*     */ 
/*  51 */       super.invokeStatic(targetType, new Method("startTracing", "(Ljava/lang/String;)V"));
/*     */     }
/*     */ 
/*  58 */     if ((this.access & 0x8) != 0) {
/*  59 */       this.log.info("Tracing static method " + this.context.getClassName() + "#" + this.name);
/*     */ 
/*  61 */       super.visitInsn(1);
/*  62 */       super.visitLdcInsn(this.context.getSimpleClassName() + "#" + this.name);
/*  63 */       emitAnnotationParamsList(this.name);
/*  64 */       super.invokeStatic(targetType, new Method("enterMethod", "(Lcom/newrelic/agent/android/tracing/Trace;Ljava/lang/String;Ljava/util/ArrayList;)V"));
/*     */     } else {
/*  66 */       this.log.info("Tracing method " + this.context.getClassName() + "#" + this.name);
/*     */ 
/*  70 */       Label tryStart = new Label();
/*  71 */       Label tryEnd = new Label();
/*  72 */       Label tryHandler = new Label();
/*     */ 
/*  74 */       super.visitLabel(tryStart);
/*  75 */       super.loadThis();
/*  76 */       super.getField(Type.getObjectType(this.context.getClassName()), "_nr_trace", Type.getType("Lcom/newrelic/agent/android/tracing/Trace;"));
/*     */ 
/*  79 */       super.visitLdcInsn(this.context.getSimpleClassName() + "#" + this.name);
/*     */ 
/*  81 */       emitAnnotationParamsList(this.name);
/*     */ 
/*  83 */       super.invokeStatic(targetType, new Method("enterMethod", "(Lcom/newrelic/agent/android/tracing/Trace;Ljava/lang/String;Ljava/util/ArrayList;)V"));
/*     */ 
/*  85 */       super.goTo(tryEnd);
/*  86 */       super.visitLabel(tryHandler);
/*     */ 
/*  89 */       super.pop();
/*  90 */       super.visitInsn(1);
/*  91 */       super.visitLdcInsn(this.context.getSimpleClassName() + "#" + this.name);
/*     */ 
/*  93 */       emitAnnotationParamsList(this.name);
/*     */ 
/*  95 */       super.invokeStatic(targetType, new Method("enterMethod", "(Lcom/newrelic/agent/android/tracing/Trace;Ljava/lang/String;Ljava/util/ArrayList;)V"));
/*     */ 
/*  97 */       super.visitLabel(tryEnd);
/*  98 */       super.visitTryCatchBlock(tryStart, tryEnd, tryHandler, "java/lang/NoSuchFieldError");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void emitAnnotationParamsList(String name) {
/* 103 */     ArrayList annotationParameters = this.context.getTracedMethodParameters(name);
/* 104 */     if ((annotationParameters == null) || (annotationParameters.size() == 0)) {
/* 105 */       super.visitInsn(1);
/* 106 */       return;
/*     */     }
/*     */ 
/* 109 */     Method constructor = Method.getMethod("void <init> ()");
/* 110 */     Method add = Method.getMethod("boolean add(java.lang.Object)");
/* 111 */     Type arrayListType = Type.getObjectType("java/util/ArrayList");
/*     */ 
/* 113 */     super.newInstance(arrayListType);
/* 114 */     super.dup();
/* 115 */     super.invokeConstructor(arrayListType, constructor);
/*     */ 
/* 117 */     for (String parameterEntry : annotationParameters) {
/* 118 */       super.dup();
/* 119 */       super.visitLdcInsn(parameterEntry);
/* 120 */       super.invokeVirtual(arrayListType, add);
/* 121 */       super.pop();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onMethodExit(int opcode)
/*     */   {
/* 128 */     Type targetType = Type.getObjectType("com/newrelic/agent/android/tracing/TraceMachine");
/* 129 */     super.invokeStatic(targetType, new Method("exitMethod", "()V"));
/*     */ 
/* 131 */     if (this.unloadContext.booleanValue()) {
/* 132 */       super.loadThis();
/* 133 */       targetType = Type.getObjectType("com/newrelic/agent/android/tracing/TraceMachine");
/* 134 */       super.invokeStatic(targetType, new Method("unloadTraceContext", "(Ljava/lang/Object;)V"));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.visitor.TraceMethodVisitor
 * JD-Core Version:    0.6.2
 */