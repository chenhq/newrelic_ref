/*    */ package com.newrelic.javassist.convert;
/*    */ 
/*    */ import com.newrelic.javassist.CtClass;
/*    */ import com.newrelic.javassist.CtMethod;
/*    */ import com.newrelic.javassist.NotFoundException;
/*    */ import com.newrelic.javassist.bytecode.BadBytecode;
/*    */ import com.newrelic.javassist.bytecode.Bytecode;
/*    */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*    */ import com.newrelic.javassist.bytecode.CodeIterator;
/*    */ import com.newrelic.javassist.bytecode.ConstPool;
/*    */ import com.newrelic.javassist.bytecode.Descriptor;
/*    */ import com.newrelic.javassist.bytecode.MethodInfo;
/*    */ 
/*    */ public class TransformBefore extends TransformCall
/*    */ {
/*    */   protected CtClass[] parameterTypes;
/*    */   protected int locals;
/*    */   protected int maxLocals;
/*    */   protected byte[] saveCode;
/*    */   protected byte[] loadCode;
/*    */ 
/*    */   public TransformBefore(Transformer next, CtMethod origMethod, CtMethod beforeMethod)
/*    */     throws NotFoundException
/*    */   {
/* 33 */     super(next, origMethod, beforeMethod);
/*    */ 
/* 36 */     this.methodDescriptor = origMethod.getMethodInfo2().getDescriptor();
/*    */ 
/* 38 */     this.parameterTypes = origMethod.getParameterTypes();
/* 39 */     this.locals = 0;
/* 40 */     this.maxLocals = 0;
/* 41 */     this.saveCode = (this.loadCode = null);
/*    */   }
/*    */ 
/*    */   public void initialize(ConstPool cp, CodeAttribute attr) {
/* 45 */     super.initialize(cp, attr);
/* 46 */     this.locals = 0;
/* 47 */     this.maxLocals = attr.getMaxLocals();
/* 48 */     this.saveCode = (this.loadCode = null);
/*    */   }
/*    */ 
/*    */   protected int match(int c, int pos, CodeIterator iterator, int typedesc, ConstPool cp)
/*    */     throws BadBytecode
/*    */   {
/* 54 */     if (this.newIndex == 0) {
/* 55 */       String desc = Descriptor.ofParameters(this.parameterTypes) + 'V';
/* 56 */       desc = Descriptor.insertParameter(this.classname, desc);
/* 57 */       int nt = cp.addNameAndTypeInfo(this.newMethodname, desc);
/* 58 */       int ci = cp.addClassInfo(this.newClassname);
/* 59 */       this.newIndex = cp.addMethodrefInfo(ci, nt);
/* 60 */       this.constPool = cp;
/*    */     }
/*    */ 
/* 63 */     if (this.saveCode == null) {
/* 64 */       makeCode(this.parameterTypes, cp);
/*    */     }
/* 66 */     return match2(pos, iterator);
/*    */   }
/*    */ 
/*    */   protected int match2(int pos, CodeIterator iterator) throws BadBytecode {
/* 70 */     iterator.move(pos);
/* 71 */     iterator.insert(this.saveCode);
/* 72 */     iterator.insert(this.loadCode);
/* 73 */     int p = iterator.insertGap(3);
/* 74 */     iterator.writeByte(184, p);
/* 75 */     iterator.write16bit(this.newIndex, p + 1);
/* 76 */     iterator.insert(this.loadCode);
/* 77 */     return iterator.next();
/*    */   }
/*    */   public int extraLocals() {
/* 80 */     return this.locals;
/*    */   }
/*    */   protected void makeCode(CtClass[] paramTypes, ConstPool cp) {
/* 83 */     Bytecode save = new Bytecode(cp, 0, 0);
/* 84 */     Bytecode load = new Bytecode(cp, 0, 0);
/*    */ 
/* 86 */     int var = this.maxLocals;
/* 87 */     int len = paramTypes == null ? 0 : paramTypes.length;
/* 88 */     load.addAload(var);
/* 89 */     makeCode2(save, load, 0, len, paramTypes, var + 1);
/* 90 */     save.addAstore(var);
/*    */ 
/* 92 */     this.saveCode = save.get();
/* 93 */     this.loadCode = load.get();
/*    */   }
/*    */ 
/*    */   private void makeCode2(Bytecode save, Bytecode load, int i, int n, CtClass[] paramTypes, int var)
/*    */   {
/* 99 */     if (i < n) {
/* 100 */       int size = load.addLoad(var, paramTypes[i]);
/* 101 */       makeCode2(save, load, i + 1, n, paramTypes, var + size);
/* 102 */       save.addStore(var, paramTypes[i]);
/*    */     }
/*    */     else {
/* 105 */       this.locals = (var - this.maxLocals);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.convert.TransformBefore
 * JD-Core Version:    0.6.2
 */