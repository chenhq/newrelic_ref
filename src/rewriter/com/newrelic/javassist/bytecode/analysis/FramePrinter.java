/*     */ package com.newrelic.javassist.bytecode.analysis;
/*     */ 
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtMethod;
/*     */ import com.newrelic.javassist.Modifier;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import com.newrelic.javassist.bytecode.InstructionPrinter;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public final class FramePrinter
/*     */ {
/*     */   private final PrintStream stream;
/*     */ 
/*     */   public FramePrinter(PrintStream stream)
/*     */   {
/*  44 */     this.stream = stream;
/*     */   }
/*     */ 
/*     */   public static void print(CtClass clazz, PrintStream stream)
/*     */   {
/*  51 */     new FramePrinter(stream).print(clazz);
/*     */   }
/*     */ 
/*     */   public void print(CtClass clazz)
/*     */   {
/*  58 */     CtMethod[] methods = clazz.getDeclaredMethods();
/*  59 */     for (int i = 0; i < methods.length; i++)
/*  60 */       print(methods[i]);
/*     */   }
/*     */ 
/*     */   private String getMethodString(CtMethod method)
/*     */   {
/*     */     try {
/*  66 */       return Modifier.toString(method.getModifiers()) + " " + method.getReturnType().getName() + " " + method.getName() + Descriptor.toString(method.getSignature()) + ";";
/*     */     }
/*     */     catch (NotFoundException e)
/*     */     {
/*  70 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void print(CtMethod method)
/*     */   {
/*  78 */     this.stream.println("\n" + getMethodString(method));
/*  79 */     MethodInfo info = method.getMethodInfo2();
/*  80 */     ConstPool pool = info.getConstPool();
/*  81 */     CodeAttribute code = info.getCodeAttribute();
/*  82 */     if (code == null)
/*     */       return;
/*     */     Frame[] frames;
/*     */     try
/*     */     {
/*  87 */       frames = new Analyzer().analyze(method.getDeclaringClass(), info);
/*     */     } catch (BadBytecode e) {
/*  89 */       throw new RuntimeException(e);
/*     */     }
/*     */ 
/*  92 */     int spacing = String.valueOf(code.getCodeLength()).length();
/*     */ 
/*  94 */     CodeIterator iterator = code.iterator();
/*  95 */     while (iterator.hasNext()) {
/*     */       int pos;
/*     */       try {
/*  98 */         pos = iterator.next();
/*     */       } catch (BadBytecode e) {
/* 100 */         throw new RuntimeException(e);
/*     */       }
/*     */ 
/* 103 */       this.stream.println(pos + ": " + InstructionPrinter.instructionString(iterator, pos, pool));
/*     */ 
/* 105 */       addSpacing(spacing + 3);
/* 106 */       Frame frame = frames[pos];
/* 107 */       if (frame == null) {
/* 108 */         this.stream.println("--DEAD CODE--");
/*     */       }
/*     */       else {
/* 111 */         printStack(frame);
/*     */ 
/* 113 */         addSpacing(spacing + 3);
/* 114 */         printLocals(frame);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void printStack(Frame frame) {
/* 120 */     this.stream.print("stack [");
/* 121 */     int top = frame.getTopIndex();
/* 122 */     for (int i = 0; i <= top; i++) {
/* 123 */       if (i > 0)
/* 124 */         this.stream.print(", ");
/* 125 */       Type type = frame.getStack(i);
/* 126 */       this.stream.print(type);
/*     */     }
/* 128 */     this.stream.println("]");
/*     */   }
/*     */ 
/*     */   private void printLocals(Frame frame) {
/* 132 */     this.stream.print("locals [");
/* 133 */     int length = frame.localsLength();
/* 134 */     for (int i = 0; i < length; i++) {
/* 135 */       if (i > 0)
/* 136 */         this.stream.print(", ");
/* 137 */       Type type = frame.getLocal(i);
/* 138 */       this.stream.print(type == null ? "empty" : type.toString());
/*     */     }
/* 140 */     this.stream.println("]");
/*     */   }
/*     */ 
/*     */   private void addSpacing(int count) {
/* 144 */     while (count-- > 0)
/* 145 */       this.stream.print(' ');
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.analysis.FramePrinter
 * JD-Core Version:    0.6.2
 */