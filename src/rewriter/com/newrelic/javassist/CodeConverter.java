/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.convert.TransformAccessArrayField;
/*     */ import com.newrelic.javassist.convert.TransformAfter;
/*     */ import com.newrelic.javassist.convert.TransformBefore;
/*     */ import com.newrelic.javassist.convert.TransformCall;
/*     */ import com.newrelic.javassist.convert.TransformFieldAccess;
/*     */ import com.newrelic.javassist.convert.TransformNew;
/*     */ import com.newrelic.javassist.convert.TransformNewClass;
/*     */ import com.newrelic.javassist.convert.TransformReadField;
/*     */ import com.newrelic.javassist.convert.TransformWriteField;
/*     */ import com.newrelic.javassist.convert.Transformer;
/*     */ 
/*     */ public class CodeConverter
/*     */ {
/*     */   protected Transformer transformers;
/*     */ 
/*     */   public CodeConverter()
/*     */   {
/*  50 */     this.transformers = null;
/*     */   }
/*     */ 
/*     */   public void replaceNew(CtClass newClass, CtClass calledClass, String calledMethod)
/*     */   {
/*  96 */     this.transformers = new TransformNew(this.transformers, newClass.getName(), calledClass.getName(), calledMethod);
/*     */   }
/*     */ 
/*     */   public void replaceNew(CtClass oldClass, CtClass newClass)
/*     */   {
/* 122 */     this.transformers = new TransformNewClass(this.transformers, oldClass.getName(), newClass.getName());
/*     */   }
/*     */ 
/*     */   public void redirectFieldAccess(CtField field, CtClass newClass, String newFieldname)
/*     */   {
/* 145 */     this.transformers = new TransformFieldAccess(this.transformers, field, newClass.getName(), newFieldname);
/*     */   }
/*     */ 
/*     */   public void replaceFieldRead(CtField field, CtClass calledClass, String calledMethod)
/*     */   {
/* 185 */     this.transformers = new TransformReadField(this.transformers, field, calledClass.getName(), calledMethod);
/*     */   }
/*     */ 
/*     */   public void replaceFieldWrite(CtField field, CtClass calledClass, String calledMethod)
/*     */   {
/* 226 */     this.transformers = new TransformWriteField(this.transformers, field, calledClass.getName(), calledMethod);
/*     */   }
/*     */ 
/*     */   public void replaceArrayAccess(CtClass calledClass, ArrayAccessReplacementMethodNames names)
/*     */     throws NotFoundException
/*     */   {
/* 329 */     this.transformers = new TransformAccessArrayField(this.transformers, calledClass.getName(), names);
/*     */   }
/*     */ 
/*     */   public void redirectMethodCall(CtMethod origMethod, CtMethod substMethod)
/*     */     throws CannotCompileException
/*     */   {
/* 351 */     String d1 = origMethod.getMethodInfo2().getDescriptor();
/* 352 */     String d2 = substMethod.getMethodInfo2().getDescriptor();
/* 353 */     if (!d1.equals(d2)) {
/* 354 */       throw new CannotCompileException("signature mismatch: " + substMethod.getLongName());
/*     */     }
/*     */ 
/* 357 */     int mod1 = origMethod.getModifiers();
/* 358 */     int mod2 = substMethod.getModifiers();
/* 359 */     if ((Modifier.isStatic(mod1) != Modifier.isStatic(mod2)) || ((Modifier.isPrivate(mod1)) && (!Modifier.isPrivate(mod2))) || (origMethod.getDeclaringClass().isInterface() != substMethod.getDeclaringClass().isInterface()))
/*     */     {
/* 363 */       throw new CannotCompileException("invoke-type mismatch " + substMethod.getLongName());
/*     */     }
/*     */ 
/* 366 */     this.transformers = new TransformCall(this.transformers, origMethod, substMethod);
/*     */   }
/*     */ 
/*     */   public void redirectMethodCall(String oldMethodName, CtMethod newMethod)
/*     */     throws CannotCompileException
/*     */   {
/* 391 */     this.transformers = new TransformCall(this.transformers, oldMethodName, newMethod);
/*     */   }
/*     */ 
/*     */   public void insertBeforeMethod(CtMethod origMethod, CtMethod beforeMethod)
/*     */     throws CannotCompileException
/*     */   {
/*     */     try
/*     */     {
/* 434 */       this.transformers = new TransformBefore(this.transformers, origMethod, beforeMethod);
/*     */     }
/*     */     catch (NotFoundException e)
/*     */     {
/* 438 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void insertAfterMethod(CtMethod origMethod, CtMethod afterMethod)
/*     */     throws CannotCompileException
/*     */   {
/*     */     try
/*     */     {
/* 481 */       this.transformers = new TransformAfter(this.transformers, origMethod, afterMethod);
/*     */     }
/*     */     catch (NotFoundException e)
/*     */     {
/* 485 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void doit(CtClass clazz, MethodInfo minfo, ConstPool cp)
/*     */     throws CannotCompileException
/*     */   {
/* 496 */     CodeAttribute codeAttr = minfo.getCodeAttribute();
/* 497 */     if ((codeAttr == null) || (this.transformers == null))
/* 498 */       return;
/* 499 */     for (Transformer t = this.transformers; t != null; t = t.getNext()) {
/* 500 */       t.initialize(cp, clazz, minfo);
/*     */     }
/* 502 */     CodeIterator iterator = codeAttr.iterator();
/* 503 */     while (iterator.hasNext()) {
/*     */       try {
/* 505 */         int pos = iterator.next();
/* 506 */         for (t = this.transformers; t != null; t = t.getNext())
/* 507 */           pos = t.transform(clazz, pos, iterator, cp);
/*     */       }
/*     */       catch (BadBytecode e) {
/* 510 */         throw new CannotCompileException(e);
/*     */       }
/*     */     }
/*     */ 
/* 514 */     int locals = 0;
/* 515 */     int stack = 0;
/* 516 */     for (t = this.transformers; t != null; t = t.getNext()) {
/* 517 */       int s = t.extraLocals();
/* 518 */       if (s > locals) {
/* 519 */         locals = s;
/*     */       }
/* 521 */       s = t.extraStack();
/* 522 */       if (s > stack) {
/* 523 */         stack = s;
/*     */       }
/*     */     }
/* 526 */     for (t = this.transformers; t != null; t = t.getNext()) {
/* 527 */       t.clean();
/*     */     }
/* 529 */     if (locals > 0) {
/* 530 */       codeAttr.setMaxLocals(codeAttr.getMaxLocals() + locals);
/*     */     }
/* 532 */     if (stack > 0)
/* 533 */       codeAttr.setMaxStack(codeAttr.getMaxStack() + stack);
/*     */   }
/*     */ 
/*     */   public static class DefaultArrayAccessReplacementMethodNames
/*     */     implements CodeConverter.ArrayAccessReplacementMethodNames
/*     */   {
/*     */     public String byteOrBooleanRead()
/*     */     {
/* 661 */       return "arrayReadByteOrBoolean";
/*     */     }
/*     */ 
/*     */     public String byteOrBooleanWrite()
/*     */     {
/* 670 */       return "arrayWriteByteOrBoolean";
/*     */     }
/*     */ 
/*     */     public String charRead()
/*     */     {
/* 679 */       return "arrayReadChar";
/*     */     }
/*     */ 
/*     */     public String charWrite()
/*     */     {
/* 688 */       return "arrayWriteChar";
/*     */     }
/*     */ 
/*     */     public String doubleRead()
/*     */     {
/* 697 */       return "arrayReadDouble";
/*     */     }
/*     */ 
/*     */     public String doubleWrite()
/*     */     {
/* 706 */       return "arrayWriteDouble";
/*     */     }
/*     */ 
/*     */     public String floatRead()
/*     */     {
/* 715 */       return "arrayReadFloat";
/*     */     }
/*     */ 
/*     */     public String floatWrite()
/*     */     {
/* 724 */       return "arrayWriteFloat";
/*     */     }
/*     */ 
/*     */     public String intRead()
/*     */     {
/* 733 */       return "arrayReadInt";
/*     */     }
/*     */ 
/*     */     public String intWrite()
/*     */     {
/* 742 */       return "arrayWriteInt";
/*     */     }
/*     */ 
/*     */     public String longRead()
/*     */     {
/* 751 */       return "arrayReadLong";
/*     */     }
/*     */ 
/*     */     public String longWrite()
/*     */     {
/* 760 */       return "arrayWriteLong";
/*     */     }
/*     */ 
/*     */     public String objectRead()
/*     */     {
/* 769 */       return "arrayReadObject";
/*     */     }
/*     */ 
/*     */     public String objectWrite()
/*     */     {
/* 778 */       return "arrayWriteObject";
/*     */     }
/*     */ 
/*     */     public String shortRead()
/*     */     {
/* 787 */       return "arrayReadShort";
/*     */     }
/*     */ 
/*     */     public String shortWrite()
/*     */     {
/* 796 */       return "arrayWriteShort";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface ArrayAccessReplacementMethodNames
/*     */   {
/*     */     public abstract String byteOrBooleanRead();
/*     */ 
/*     */     public abstract String byteOrBooleanWrite();
/*     */ 
/*     */     public abstract String charRead();
/*     */ 
/*     */     public abstract String charWrite();
/*     */ 
/*     */     public abstract String doubleRead();
/*     */ 
/*     */     public abstract String doubleWrite();
/*     */ 
/*     */     public abstract String floatRead();
/*     */ 
/*     */     public abstract String floatWrite();
/*     */ 
/*     */     public abstract String intRead();
/*     */ 
/*     */     public abstract String intWrite();
/*     */ 
/*     */     public abstract String longRead();
/*     */ 
/*     */     public abstract String longWrite();
/*     */ 
/*     */     public abstract String objectRead();
/*     */ 
/*     */     public abstract String objectWrite();
/*     */ 
/*     */     public abstract String shortRead();
/*     */ 
/*     */     public abstract String shortWrite();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CodeConverter
 * JD-Core Version:    0.6.2
 */