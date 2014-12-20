/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ 
/*     */ class CtNewClass extends CtClassType
/*     */ {
/*     */   protected boolean hasConstructor;
/*     */ 
/*     */   CtNewClass(String name, ClassPool cp, boolean isInterface, CtClass superclass)
/*     */   {
/*  29 */     super(name, cp);
/*  30 */     this.wasChanged = true;
/*     */     String superName;
/*     */     String superName;
/*  32 */     if ((isInterface) || (superclass == null))
/*  33 */       superName = null;
/*     */     else {
/*  35 */       superName = superclass.getName();
/*     */     }
/*  37 */     this.classfile = new ClassFile(isInterface, name, superName);
/*  38 */     if ((isInterface) && (superclass != null)) {
/*  39 */       this.classfile.setInterfaces(new String[] { superclass.getName() });
/*     */     }
/*  41 */     setModifiers(Modifier.setPublic(getModifiers()));
/*  42 */     this.hasConstructor = isInterface;
/*     */   }
/*     */ 
/*     */   protected void extendToString(StringBuffer buffer) {
/*  46 */     if (this.hasConstructor) {
/*  47 */       buffer.append("hasConstructor ");
/*     */     }
/*  49 */     super.extendToString(buffer);
/*     */   }
/*     */ 
/*     */   public void addConstructor(CtConstructor c)
/*     */     throws CannotCompileException
/*     */   {
/*  55 */     this.hasConstructor = true;
/*  56 */     super.addConstructor(c);
/*     */   }
/*     */ 
/*     */   public void toBytecode(DataOutputStream out)
/*     */     throws CannotCompileException, IOException
/*     */   {
/*  62 */     if (!this.hasConstructor) {
/*     */       try {
/*  64 */         inheritAllConstructors();
/*  65 */         this.hasConstructor = true;
/*     */       }
/*     */       catch (NotFoundException e) {
/*  68 */         throw new CannotCompileException(e);
/*     */       }
/*     */     }
/*  71 */     super.toBytecode(out);
/*     */   }
/*     */ 
/*     */   public void inheritAllConstructors()
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/*  87 */     CtClass superclazz = getSuperclass();
/*  88 */     CtConstructor[] cs = superclazz.getDeclaredConstructors();
/*     */ 
/*  90 */     int n = 0;
/*  91 */     for (int i = 0; i < cs.length; i++) {
/*  92 */       CtConstructor c = cs[i];
/*  93 */       int mod = c.getModifiers();
/*  94 */       if (isInheritable(mod, superclazz)) {
/*  95 */         CtConstructor cons = CtNewConstructor.make(c.getParameterTypes(), c.getExceptionTypes(), this);
/*     */ 
/*  98 */         cons.setModifiers(mod & 0x7);
/*  99 */         addConstructor(cons);
/* 100 */         n++;
/*     */       }
/*     */     }
/*     */ 
/* 104 */     if (n < 1)
/* 105 */       throw new CannotCompileException("no inheritable constructor in " + superclazz.getName());
/*     */   }
/*     */ 
/*     */   private boolean isInheritable(int mod, CtClass superclazz)
/*     */   {
/* 111 */     if (Modifier.isPrivate(mod)) {
/* 112 */       return false;
/*     */     }
/* 114 */     if (Modifier.isPackage(mod)) {
/* 115 */       String pname = getPackageName();
/* 116 */       String pname2 = superclazz.getPackageName();
/* 117 */       if (pname == null) {
/* 118 */         return pname2 == null;
/*     */       }
/* 120 */       return pname.equals(pname2);
/*     */     }
/*     */ 
/* 123 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtNewClass
 * JD-Core Version:    0.6.2
 */