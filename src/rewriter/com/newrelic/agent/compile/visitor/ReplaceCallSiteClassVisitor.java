/*    */ package com.newrelic.agent.compile.visitor;
/*    */ 
/*    */ import com.newrelic.agent.compile.InstrumentationContext;
/*    */ import com.newrelic.agent.compile.Log;
/*    */ import com.newrelic.com.google.common.collect.Sets;
/*    */ import com.newrelic.objectweb.asm.AnnotationVisitor;
/*    */ import com.newrelic.objectweb.asm.ClassAdapter;
/*    */ import com.newrelic.objectweb.asm.ClassVisitor;
/*    */ import com.newrelic.objectweb.asm.MethodVisitor;
/*    */ import com.newrelic.objectweb.asm.commons.GeneratorAdapter;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class ReplaceCallSiteClassVisitor extends ClassAdapter
/*    */ {
/*    */   private final InstrumentationContext context;
/*    */   private final Log log;
/* 17 */   private final Set<String> recursiveCallCheckThreadLocals = Sets.newHashSet();
/*    */ 
/*    */   public ReplaceCallSiteClassVisitor(ClassVisitor cv, InstrumentationContext context, Log log) {
/* 20 */     super(cv);
/* 21 */     this.context = context;
/* 22 */     this.log = log;
/*    */   }
/*    */ 
/*    */   public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions)
/*    */   {
/* 27 */     return new MethodWrapMethodVisitor(super.visitMethod(access, name, desc, sig, exceptions), access, name, desc);
/*    */   }
/*    */   private final class MethodWrapMethodVisitor extends GeneratorAdapter {
/*    */     private final String name;
/*    */     private final String desc;
/*    */     private boolean isReplaceClassSite;
/*    */ 
/* 36 */     public MethodWrapMethodVisitor(MethodVisitor mv, int access, String name, String desc) { super(access, name, desc);
/*    */ 
/* 38 */       ReplaceCallSiteClassVisitor.this.log.debug("DUDE " + name + desc);
/* 39 */       this.name = name;
/* 40 */       this.desc = desc;
/*    */     }
/*    */ 
/*    */     public AnnotationVisitor visitAnnotation(String name, boolean arg1)
/*    */     {
/* 47 */       if ("Lcom/newrelic/agent/android/instrumentation/ReplaceCallSite;".equals(name)) {
/* 48 */         this.isReplaceClassSite = true;
/*    */       }
/* 50 */       return super.visitAnnotation(name, arg1);
/*    */     }
/*    */ 
/*    */     public void visitCode()
/*    */     {
/* 58 */       super.visitCode();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.visitor.ReplaceCallSiteClassVisitor
 * JD-Core Version:    0.6.2
 */