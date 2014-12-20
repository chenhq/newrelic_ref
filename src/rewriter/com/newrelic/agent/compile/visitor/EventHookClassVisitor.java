/*     */ package com.newrelic.agent.compile.visitor;
/*     */ 
/*     */ import com.newrelic.agent.compile.InstrumentationContext;
/*     */ import com.newrelic.agent.compile.Log;
/*     */ import com.newrelic.objectweb.asm.ClassAdapter;
/*     */ import com.newrelic.objectweb.asm.ClassVisitor;
/*     */ import com.newrelic.objectweb.asm.MethodVisitor;
/*     */ import com.newrelic.objectweb.asm.commons.GeneratorAdapter;
/*     */ import com.newrelic.objectweb.asm.commons.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public abstract class EventHookClassVisitor extends ClassAdapter
/*     */ {
/*     */   protected final Set<String> baseClasses;
/*     */   private final Map<Method, MethodVisitorFactory> methodVisitors;
/*     */   protected String superName;
/*  27 */   protected boolean instrument = false;
/*     */   protected final InstrumentationContext context;
/*     */   protected final Log log;
/*     */ 
/*     */   public EventHookClassVisitor(ClassVisitor cv, InstrumentationContext context, Log log, Set<String> baseClasses, Map<Method, Method> methodMappings)
/*     */   {
/*  32 */     super(cv);
/*  33 */     this.context = context;
/*  34 */     this.log = log;
/*  35 */     this.baseClasses = Collections.unmodifiableSet(baseClasses);
/*  36 */     this.methodVisitors = new HashMap();
/*  37 */     for (Map.Entry entry : methodMappings.entrySet())
/*  38 */       this.methodVisitors.put(entry.getKey(), new MethodVisitorFactory((Method)entry.getValue()));
/*     */   }
/*     */ 
/*     */   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
/*     */   {
/*  45 */     super.visit(version, access, name, signature, superName, interfaces);
/*  46 */     this.superName = superName;
/*     */ 
/*  48 */     this.instrument = this.baseClasses.contains(superName);
/*     */ 
/*  50 */     if (this.instrument) {
/*  51 */       this.context.markModified();
/*  52 */       this.log.info("Rewriting " + name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*     */   {
/*  58 */     MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
/*  59 */     if (!this.instrument) {
/*  60 */       return mv;
/*     */     }
/*     */ 
/*  63 */     Method method = new Method(name, desc);
/*  64 */     MethodVisitorFactory v = (MethodVisitorFactory)this.methodVisitors.get(method);
/*  65 */     if (v != null)
/*     */     {
/*  67 */       this.methodVisitors.remove(method);
/*  68 */       return v.createMethodVisitor(access, method, mv, false);
/*     */     }
/*  70 */     return mv;
/*     */   }
/*     */ 
/*     */   public void visitEnd()
/*     */   {
/*  76 */     if (!this.instrument) {
/*  77 */       return;
/*     */     }
/*     */ 
/*  81 */     for (Map.Entry entry : this.methodVisitors.entrySet()) {
/*  82 */       MethodVisitor mv = super.visitMethod(4, ((Method)entry.getKey()).getName(), ((Method)entry.getKey()).getDescriptor(), null, null);
/*     */ 
/*  84 */       mv = ((MethodVisitorFactory)entry.getValue()).createMethodVisitor(4, (Method)entry.getKey(), mv, true);
/*  85 */       mv.visitCode();
/*  86 */       mv.visitInsn(177);
/*  87 */       mv.visitMaxs(0, 0);
/*  88 */       mv.visitEnd();
/*     */     }
/*  90 */     super.visitEnd();
/*     */   }
/*     */ 
/*     */   protected abstract void injectCodeIntoMethod(GeneratorAdapter paramGeneratorAdapter, Method paramMethod1, Method paramMethod2);
/*     */ 
/*     */   protected class MethodVisitorFactory
/*     */   {
/*     */     final Method monitorMethod;
/*     */ 
/*     */     public MethodVisitorFactory(Method monitorMethod)
/*     */     {
/* 103 */       this.monitorMethod = monitorMethod;
/*     */     }
/*     */ 
/*     */     public MethodVisitor createMethodVisitor(int access, final Method method, MethodVisitor mv, final boolean callSuper) {
/* 107 */       return new GeneratorAdapter(access, method, mv)
/*     */       {
/*     */         public void visitCode() {
/* 110 */           super.visitCode();
/*     */ 
/* 112 */           if (callSuper)
/*     */           {
/* 114 */             loadThis();
/* 115 */             for (int i = 0; i < method.getArgumentTypes().length; i++) {
/* 116 */               loadArg(i);
/*     */             }
/* 118 */             visitMethodInsn(183, EventHookClassVisitor.this.superName, method.getName(), method.getDescriptor());
/*     */           }
/*     */ 
/* 121 */           EventHookClassVisitor.this.injectCodeIntoMethod(this, method, EventHookClassVisitor.MethodVisitorFactory.this.monitorMethod);
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.visitor.EventHookClassVisitor
 * JD-Core Version:    0.6.2
 */