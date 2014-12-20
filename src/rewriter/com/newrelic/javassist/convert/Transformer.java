/*    */ package com.newrelic.javassist.convert;
/*    */ 
/*    */ import com.newrelic.javassist.CannotCompileException;
/*    */ import com.newrelic.javassist.CtClass;
/*    */ import com.newrelic.javassist.bytecode.BadBytecode;
/*    */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*    */ import com.newrelic.javassist.bytecode.CodeIterator;
/*    */ import com.newrelic.javassist.bytecode.ConstPool;
/*    */ import com.newrelic.javassist.bytecode.MethodInfo;
/*    */ import com.newrelic.javassist.bytecode.Opcode;
/*    */ 
/*    */ public abstract class Transformer
/*    */   implements Opcode
/*    */ {
/*    */   private Transformer next;
/*    */ 
/*    */   public Transformer(Transformer t)
/*    */   {
/* 37 */     this.next = t;
/*    */   }
/*    */   public Transformer getNext() {
/* 40 */     return this.next;
/*    */   }
/*    */   public void initialize(ConstPool cp, CodeAttribute attr) {
/*    */   }
/*    */   public void initialize(ConstPool cp, CtClass clazz, MethodInfo minfo) throws CannotCompileException {
/* 45 */     initialize(cp, minfo.getCodeAttribute());
/*    */   }
/*    */   public void clean() {
/*    */   }
/*    */ 
/*    */   public abstract int transform(CtClass paramCtClass, int paramInt, CodeIterator paramCodeIterator, ConstPool paramConstPool) throws CannotCompileException, BadBytecode;
/*    */ 
/*    */   public int extraLocals() {
/* 53 */     return 0;
/*    */   }
/* 55 */   public int extraStack() { return 0; }
/*    */ 
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.convert.Transformer
 * JD-Core Version:    0.6.2
 */