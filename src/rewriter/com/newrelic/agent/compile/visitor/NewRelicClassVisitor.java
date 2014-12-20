/*     */ package com.newrelic.agent.compile.visitor;
/*     */ 
/*     */ import com.newrelic.agent.Obfuscation.Proguard;
/*     */ import com.newrelic.agent.compile.HaltBuildException;
/*     */ import com.newrelic.agent.compile.InstrumentationContext;
/*     */ import com.newrelic.agent.compile.Log;
/*     */ import com.newrelic.agent.compile.RewriterAgent;
/*     */ import com.newrelic.objectweb.asm.ClassAdapter;
/*     */ import com.newrelic.objectweb.asm.ClassVisitor;
/*     */ import com.newrelic.objectweb.asm.FieldVisitor;
/*     */ import com.newrelic.objectweb.asm.MethodVisitor;
/*     */ import com.newrelic.objectweb.asm.commons.GeneratorAdapter;
/*     */ import java.util.UUID;
/*     */ 
/*     */ public class NewRelicClassVisitor extends ClassAdapter
/*     */ {
/*     */   private static String buildId;
/*     */   private final InstrumentationContext context;
/*     */   private final Log log;
/*     */ 
/*     */   public NewRelicClassVisitor(ClassVisitor cv, InstrumentationContext context, Log log)
/*     */   {
/*  20 */     super(cv);
/*  21 */     this.context = context;
/*  22 */     this.log = log;
/*     */   }
/*     */ 
/*     */   public static String getBuildId() {
/*  26 */     if (buildId == null) {
/*  27 */       buildId = UUID.randomUUID().toString();
/*     */     }
/*     */ 
/*  30 */     return buildId;
/*     */   }
/*     */ 
/*     */   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*     */   {
/*  36 */     if ((this.context.getClassName().equals("com/newrelic/agent/android/NewRelic")) && (name.equals("isInstrumented"))) {
/*  37 */       return new NewRelicMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc);
/*     */     }
/*     */ 
/*  40 */     if ((this.context.getClassName().equals("com/newrelic/agent/android/harvest/crash/Crash")) && (name.equals("getBuildId"))) {
/*  41 */       return new BuildIdMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc);
/*     */     }
/*     */ 
/*  44 */     if ((this.context.getClassName().equals("com/newrelic/agent/android/AndroidAgentImpl")) && (name.equals("pokeCanary"))) {
/*  45 */       return new CanaryMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc);
/*     */     }
/*     */ 
/*  48 */     return super.visitMethod(access, name, desc, signature, exceptions);
/*     */   }
/*     */ 
/*     */   public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
/*     */   {
/*  53 */     if ((this.context.getClassName().equals("com/newrelic/agent/android/Agent")) && (name.equals("VERSION")) && 
/*  54 */       (!value.equals(RewriterAgent.getVersion()))) {
/*  55 */       throw new HaltBuildException("New Relic Error: Your agent and class rewriter versions do not match: agent = " + value + " class rewriter = " + RewriterAgent.getVersion() + ".  You probably need to update one of these components.  If you're using gradle and just updated, run gradle -stop to restart the daemon.");
/*     */     }
/*     */ 
/*  59 */     return super.visitField(access, name, desc, signature, value);
/*     */   }
/*     */ 
/*     */   private final class CanaryMethodVisitor extends GeneratorAdapter
/*     */   {
/* 101 */     private boolean foundCanaryAlive = false;
/*     */ 
/*     */     public CanaryMethodVisitor(MethodVisitor mv, int access, String name, String desc) {
/* 104 */       super(access, name, desc);
/*     */     }
/*     */ 
/*     */     public void visitMethodInsn(int opcode, String owner, String name, String desc)
/*     */     {
/* 109 */       if (name.equals("canaryMethod"))
/* 110 */         this.foundCanaryAlive = true;
/*     */     }
/*     */ 
/*     */     public void visitEnd()
/*     */     {
/* 116 */       if (this.foundCanaryAlive) {
/* 117 */         NewRelicClassVisitor.this.log.info("Found canary alive");
/*     */       } else {
/* 119 */         NewRelicClassVisitor.this.log.info("Evidence of Proguard detected, sending mapping.txt");
/* 120 */         Proguard proguard = new Proguard(NewRelicClassVisitor.this.log);
/* 121 */         proguard.findAndSendMapFile();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class NewRelicMethodVisitor extends GeneratorAdapter
/*     */   {
/*     */     public NewRelicMethodVisitor(MethodVisitor mv, int access, String name, String desc)
/*     */     {
/*  87 */       super(access, name, desc);
/*     */     }
/*     */ 
/*     */     public void visitCode()
/*     */     {
/*  92 */       super.visitInsn(4);
/*  93 */       super.visitInsn(172);
/*     */ 
/*  95 */       NewRelicClassVisitor.this.log.info("Marking NewRelic agent as instrumented");
/*  96 */       NewRelicClassVisitor.this.context.markModified();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class BuildIdMethodVisitor extends GeneratorAdapter
/*     */   {
/*     */     public BuildIdMethodVisitor(MethodVisitor mv, int access, String name, String desc)
/*     */     {
/*  66 */       super(access, name, desc);
/*     */     }
/*     */ 
/*     */     public void visitCode()
/*     */     {
/*  71 */       super.visitLdcInsn(NewRelicClassVisitor.getBuildId());
/*  72 */       super.visitInsn(176);
/*     */ 
/*  74 */       NewRelicClassVisitor.this.log.info("Setting build identifier to " + NewRelicClassVisitor.getBuildId());
/*  75 */       NewRelicClassVisitor.this.context.markModified();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.visitor.NewRelicClassVisitor
 * JD-Core Version:    0.6.2
 */