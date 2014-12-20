/*    */ package com.newrelic.agent.compile.visitor;
/*    */ 
/*    */ import com.newrelic.agent.compile.InstrumentationContext;
/*    */ import com.newrelic.agent.compile.Log;
/*    */ import com.newrelic.com.google.common.collect.ImmutableMap;
/*    */ import com.newrelic.objectweb.asm.ClassAdapter;
/*    */ import com.newrelic.objectweb.asm.ClassVisitor;
/*    */ import com.newrelic.objectweb.asm.MethodVisitor;
/*    */ import com.newrelic.objectweb.asm.Type;
/*    */ 
/*    */ public class AsyncTaskClassVisitor extends ClassAdapter
/*    */ {
/*    */   public static final String TARGET_CLASS = "android/os/AsyncTask";
/*    */   private final InstrumentationContext context;
/*    */   private final Log log;
/* 19 */   private boolean instrument = false;
/*    */ 
/* 24 */   public static final ImmutableMap<String, String> traceMethodMap = ImmutableMap.of("doInBackground", "([Ljava/lang/Object;)Ljava/lang/Object;");
/*    */ 
/* 28 */   public static final ImmutableMap<String, String> endTraceMethodMap = ImmutableMap.of("onPostExecute", "(Ljava/lang/Object;)V");
/*    */ 
/*    */   public AsyncTaskClassVisitor(ClassVisitor cv, InstrumentationContext context, Log log)
/*    */   {
/* 33 */     super(cv);
/* 34 */     this.context = context;
/* 35 */     this.log = log;
/*    */   }
/*    */ 
/*    */   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
/*    */   {
/* 40 */     if ((superName != null) && (superName.equals("android/os/AsyncTask"))) {
/* 41 */       interfaces = TraceClassDecorator.addInterface(interfaces);
/* 42 */       super.visit(version, access, name, signature, superName, interfaces);
/*    */ 
/* 44 */       this.instrument = true;
/* 45 */       this.log.info("Rewriting " + this.context.getClassName());
/* 46 */       this.context.markModified();
/*    */     } else {
/* 48 */       super.visit(version, access, name, signature, superName, interfaces);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void visitEnd()
/*    */   {
/* 54 */     if (this.instrument) {
/* 55 */       TraceClassDecorator decorator = new TraceClassDecorator(this);
/*    */ 
/* 57 */       decorator.addTraceField();
/* 58 */       decorator.addTraceInterface(Type.getObjectType(this.context.getClassName()));
/*    */ 
/* 60 */       this.log.info("Added Trace object and interface to " + this.context.getClassName());
/*    */     }
/*    */ 
/* 63 */     super.visitEnd();
/*    */   }
/*    */ 
/*    */   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*    */   {
/* 68 */     MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
/*    */ 
/* 70 */     if (this.instrument)
/*    */     {
/* 73 */       if ((traceMethodMap.containsKey(name)) && (((String)traceMethodMap.get(name)).equals(desc))) {
/* 74 */         TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor(methodVisitor, access, name, desc, this.context);
/* 75 */         traceMethodVisitor.setUnloadContext();
/* 76 */         return traceMethodVisitor;
/*    */       }
/*    */ 
/* 79 */       if ((endTraceMethodMap.containsKey(name)) && (((String)endTraceMethodMap.get(name)).equals(desc)))
/*    */       {
/* 81 */         TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor(methodVisitor, access, name, desc, this.context);
/* 82 */         return traceMethodVisitor;
/*    */       }
/*    */     }
/*    */ 
/* 86 */     return methodVisitor;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.visitor.AsyncTaskClassVisitor
 * JD-Core Version:    0.6.2
 */