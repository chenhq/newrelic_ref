/*     */ package com.newrelic.agent.compile.visitor;
/*     */ 
/*     */ import com.newrelic.agent.compile.ClassMethod;
/*     */ import com.newrelic.agent.compile.InstrumentationContext;
/*     */ import com.newrelic.agent.compile.Log;
/*     */ import com.newrelic.objectweb.asm.ClassAdapter;
/*     */ import com.newrelic.objectweb.asm.ClassVisitor;
/*     */ import com.newrelic.objectweb.asm.Label;
/*     */ import com.newrelic.objectweb.asm.MethodVisitor;
/*     */ import com.newrelic.objectweb.asm.commons.GeneratorAdapter;
/*     */ import com.newrelic.objectweb.asm.commons.Method;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ public class WrapMethodClassVisitor extends ClassAdapter
/*     */ {
/*     */   private final InstrumentationContext context;
/*     */   private final Log log;
/*     */ 
/*     */   public WrapMethodClassVisitor(ClassVisitor cv, InstrumentationContext context, Log log)
/*     */   {
/*  19 */     super(cv);
/*  20 */     this.context = context;
/*  21 */     this.log = log;
/*     */   }
/*     */ 
/*     */   public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions)
/*     */   {
/*  26 */     if (this.context.isSkippedMethod(name, desc)) {
/*  27 */       return super.visitMethod(access, name, desc, sig, exceptions);
/*     */     }
/*     */ 
/*  30 */     return new MethodWrapMethodVisitor(super.visitMethod(access, name, desc, sig, exceptions), access, name, desc, this.context, this.log);
/*     */   }
/*     */ 
/*     */   private static final class MethodWrapMethodVisitor extends GeneratorAdapter
/*     */   {
/*     */     private final String name;
/*     */     private final String desc;
/*     */     private final InstrumentationContext context;
/*     */     private final Log log;
/*  38 */     private boolean newInstructionFound = false;
/*  39 */     private boolean dupInstructionFound = false;
/*     */ 
/*     */     public MethodWrapMethodVisitor(MethodVisitor mv, int access, String name, String desc, InstrumentationContext context, Log log) {
/*  42 */       super(access, name, desc);
/*  43 */       this.name = name;
/*  44 */       this.desc = desc;
/*  45 */       this.context = context;
/*  46 */       this.log = log;
/*     */     }
/*     */ 
/*     */     public void visitMethodInsn(int opcode, String owner, String name, String desc)
/*     */     {
/*  54 */       if (opcode == 186) {
/*  55 */         this.log.warning(MessageFormat.format("[{0}] INVOKEDYNAMIC instruction cannot be instrumented", new Object[] { this.context.getClassName().replaceAll("/", ".") }));
/*  56 */         super.visitMethodInsn(opcode, owner, name, desc);
/*  57 */         return;
/*     */       }
/*     */ 
/*  60 */       if ((!tryReplaceCallSite(opcode, owner, name, desc)) && 
/*  61 */         (!tryWrapReturnValue(opcode, owner, name, desc)))
/*  62 */         super.visitMethodInsn(opcode, owner, name, desc);
/*     */     }
/*     */ 
/*     */     public void visitTypeInsn(int opcode, String type)
/*     */     {
/*  72 */       if (opcode == 187) {
/*  73 */         this.newInstructionFound = true;
/*  74 */         this.dupInstructionFound = false;
/*     */       }
/*     */ 
/*  77 */       super.visitTypeInsn(opcode, type);
/*     */     }
/*     */ 
/*     */     public void visitInsn(int opcode)
/*     */     {
/*  82 */       if (opcode == 89) {
/*  83 */         this.dupInstructionFound = true;
/*     */       }
/*     */ 
/*  86 */       super.visitInsn(opcode);
/*     */     }
/*     */ 
/*     */     private boolean tryWrapReturnValue(int opcode, String owner, String name, String desc) {
/*  90 */       ClassMethod method = new ClassMethod(owner, name, desc);
/*     */ 
/*  92 */       ClassMethod wrappingMethod = this.context.getMethodWrapper(method);
/*  93 */       if (wrappingMethod != null) {
/*  94 */         this.log.info(MessageFormat.format("[{0}] wrapping call to {1} with {2}", new Object[] { this.context.getClassName().replaceAll("/", "."), method.toString(), wrappingMethod.toString() }));
/*  95 */         super.visitMethodInsn(opcode, owner, name, desc);
/*  96 */         super.visitMethodInsn(184, wrappingMethod.getClassName(), wrappingMethod.getMethodName(), wrappingMethod.getMethodDesc());
/*  97 */         this.context.markModified();
/*  98 */         return true;
/*     */       }
/*     */ 
/* 101 */       return false;
/*     */     }
/*     */ 
/*     */     private boolean tryReplaceCallSite(int opcode, String owner, String name, String desc)
/*     */     {
/* 108 */       Collection replacementMethods = this.context.getCallSiteReplacements(owner, name, desc);
/*     */ 
/* 110 */       if (replacementMethods.isEmpty()) {
/* 111 */         return false;
/*     */       }
/*     */ 
/* 114 */       ClassMethod method = new ClassMethod(owner, name, desc);

/*     */ 
/* 116 */       Iterator i$ = replacementMethods.iterator();if (i$.hasNext()) {ClassMethod replacementMethod = (ClassMethod)i$.next();
/*     */ 
/* 129 */         boolean isSuperCallInOverride = (opcode == 183) && (!owner.equals(this.context.getClassName())) && (this.name.equals(name)) && (this.desc.equals(desc));
/*     */ 
/* 134 */         if (isSuperCallInOverride) {
/* 135 */           this.log.info(MessageFormat.format("[{0}] skipping call site replacement for super call in overriden method: {1}:{2}", new Object[] { this.context.getClassName().replaceAll("/", "."), this.name, this.desc }));
/*     */ 
/* 138 */           return false;
/*     */         }
/*     */ 
/* 151 */         if ((opcode == 183) && (name.equals("<init>"))) {
/* 152 */           Method originalMethod = new Method(name, desc);
/*     */ 
/* 155 */           if ((this.context.getSuperClassName() != null) && (this.context.getSuperClassName().equals(owner)))
/*     */           {
/* 159 */             this.log.info(MessageFormat.format("[{0}] skipping call site replacement for class extending {1}", new Object[] { this.context.getFriendlyClassName(), this.context.getFriendlySuperClassName() }));
/* 160 */             return false;
/*     */           }
/*     */ 
/* 163 */           this.log.info(MessageFormat.format("[{0}] tracing constructor call to {1} - {2}", new Object[] { this.context.getFriendlyClassName(), method.toString(), owner }));
/*     */ 
/* 166 */           int[] locals = new int[originalMethod.getArgumentTypes().length];
/* 167 */           for (int i = locals.length - 1; i >= 0; i--) {
/* 168 */             locals[i] = newLocal(originalMethod.getArgumentTypes()[i]);
/* 169 */             storeLocal(locals[i]);
/*     */           }
/*     */ 
/* 173 */           visitInsn(87);
/*     */ 
/* 177 */           if ((this.newInstructionFound) && (this.dupInstructionFound)) {
/* 178 */             visitInsn(87);
/*     */           }
/*     */ 
/* 182 */           for (int local : locals) {
/* 183 */             loadLocal(local);
/*     */           }
/*     */ 
/* 187 */           super.visitMethodInsn(184, replacementMethod.getClassName(), replacementMethod.getMethodName(), replacementMethod.getMethodDesc());
/*     */ 
/* 191 */           if ((this.newInstructionFound) && (!this.dupInstructionFound))
/* 192 */             visitInsn(87);
/*     */         }
/* 194 */         else if (opcode == 184) {
/* 195 */           this.log.info(MessageFormat.format("[{0}] replacing static call to {1} with {2}", new Object[] { this.context.getClassName().replaceAll("/", "."), method.toString(), replacementMethod.toString() }));
/*     */ 
/* 198 */           super.visitMethodInsn(184, replacementMethod.getClassName(), replacementMethod.getMethodName(), replacementMethod.getMethodDesc());
/*     */         } else {
/* 200 */           Method newMethod = new Method(replacementMethod.getMethodName(), replacementMethod.getMethodDesc());
/*     */ 
/* 204 */           this.log.info(MessageFormat.format("[{0}] replacing call to {1} with {2} (with instance check)", new Object[] { this.context.getClassName().replaceAll("/", "."), method.toString(), replacementMethod.toString() }));
/*     */           // [com.github.chenhq.httpclient.service.UserServiceImpl]
/* 211 */           Method originalMethod = new Method(name, desc);
/*     */ 
/* 214 */           int[] locals = new int[originalMethod.getArgumentTypes().length];
/* 215 */           for (int i = locals.length - 1; i >= 0; i--) {
/* 216 */             locals[i] = newLocal(originalMethod.getArgumentTypes()[i]);
/* 217 */             storeLocal(locals[i]);
/*     */           }
/*     */ 
/* 221 */           dup();
/*     */ 
/* 224 */           instanceOf(newMethod.getArgumentTypes()[0]);
/* 225 */           Label isInstanceOfLabel = new Label();
/* 226 */           visitJumpInsn(154, isInstanceOfLabel);
/*     */ 
/* 230 */           for (int local : locals) {
/* 231 */             loadLocal(local);
/*     */           }
/* 233 */           super.visitMethodInsn(opcode, owner, name, desc);
/*     */ 
/* 235 */           Label end = new Label();
/* 236 */           visitJumpInsn(167, end);
/* 237 */           visitLabel(isInstanceOfLabel);
/*     */ 
/* 241 */           checkCast(newMethod.getArgumentTypes()[0]);
/*     */ 
/* 244 */           for (int local : locals) {
/* 245 */             loadLocal(local);
/*     */           }
/* 247 */           super.visitMethodInsn(184, replacementMethod.getClassName(), replacementMethod.getMethodName(), replacementMethod.getMethodDesc());
/*     */ 
/* 250 */           visitLabel(end);
/*     */         }
/*     */ 
/* 253 */         this.context.markModified();
/* 254 */         return true;
/*     */       }
/*     */ 
/* 257 */       return false;
/*     */     }
/*     */   }
/*     */ }
