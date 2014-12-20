/*     */ package com.newrelic.javassist.convert;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtMethod;
/*     */ import com.newrelic.javassist.Modifier;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ 
/*     */ public class TransformCall extends Transformer
/*     */ {
/*     */   protected String classname;
/*     */   protected String methodname;
/*     */   protected String methodDescriptor;
/*     */   protected String newClassname;
/*     */   protected String newMethodname;
/*     */   protected boolean newMethodIsPrivate;
/*     */   protected int newIndex;
/*     */   protected ConstPool constPool;
/*     */ 
/*     */   public TransformCall(Transformer next, CtMethod origMethod, CtMethod substMethod)
/*     */   {
/*  37 */     this(next, origMethod.getName(), substMethod);
/*  38 */     this.classname = origMethod.getDeclaringClass().getName();
/*     */   }
/*     */ 
/*     */   public TransformCall(Transformer next, String oldMethodName, CtMethod substMethod)
/*     */   {
/*  44 */     super(next);
/*  45 */     this.methodname = oldMethodName;
/*  46 */     this.methodDescriptor = substMethod.getMethodInfo2().getDescriptor();
/*  47 */     this.classname = (this.newClassname = substMethod.getDeclaringClass().getName());
/*  48 */     this.newMethodname = substMethod.getName();
/*  49 */     this.constPool = null;
/*  50 */     this.newMethodIsPrivate = Modifier.isPrivate(substMethod.getModifiers());
/*     */   }
/*     */ 
/*     */   public void initialize(ConstPool cp, CodeAttribute attr) {
/*  54 */     if (this.constPool != cp)
/*  55 */       this.newIndex = 0;
/*     */   }
/*     */ 
/*     */   public int transform(CtClass clazz, int pos, CodeIterator iterator, ConstPool cp)
/*     */     throws BadBytecode
/*     */   {
/*  68 */     int c = iterator.byteAt(pos);
/*  69 */     if ((c == 185) || (c == 183) || (c == 184) || (c == 182))
/*     */     {
/*  71 */       int index = iterator.u16bitAt(pos + 1);
/*  72 */       String cname = cp.eqMember(this.methodname, this.methodDescriptor, index);
/*  73 */       if ((cname != null) && (matchClass(cname, clazz.getClassPool()))) {
/*  74 */         int ntinfo = cp.getMemberNameAndType(index);
/*  75 */         pos = match(c, pos, iterator, cp.getNameAndTypeDescriptor(ntinfo), cp);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  80 */     return pos;
/*     */   }
/*     */ 
/*     */   private boolean matchClass(String name, ClassPool pool) {
/*  84 */     if (this.classname.equals(name))
/*  85 */       return true;
/*     */     try
/*     */     {
/*  88 */       CtClass clazz = pool.get(name);
/*  89 */       CtClass declClazz = pool.get(this.classname);
/*  90 */       if (clazz.subtypeOf(declClazz))
/*     */         try {
/*  92 */           CtMethod m = clazz.getMethod(this.methodname, this.methodDescriptor);
/*  93 */           return m.getDeclaringClass().getName().equals(this.classname);
/*     */         }
/*     */         catch (NotFoundException e)
/*     */         {
/*  97 */           return true;
/*     */         }
/*     */     }
/*     */     catch (NotFoundException e) {
/* 101 */       return false;
/*     */     }
/*     */ 
/* 104 */     return false;
/*     */   }
/*     */ 
/*     */   protected int match(int c, int pos, CodeIterator iterator, int typedesc, ConstPool cp)
/*     */     throws BadBytecode
/*     */   {
/* 110 */     if (this.newIndex == 0) {
/* 111 */       int nt = cp.addNameAndTypeInfo(cp.addUtf8Info(this.newMethodname), typedesc);
/*     */ 
/* 113 */       int ci = cp.addClassInfo(this.newClassname);
/* 114 */       if (c == 185) {
/* 115 */         this.newIndex = cp.addInterfaceMethodrefInfo(ci, nt);
/*     */       } else {
/* 117 */         if ((this.newMethodIsPrivate) && (c == 182)) {
/* 118 */           iterator.writeByte(183, pos);
/*     */         }
/* 120 */         this.newIndex = cp.addMethodrefInfo(ci, nt);
/*     */       }
/*     */ 
/* 123 */       this.constPool = cp;
/*     */     }
/*     */ 
/* 126 */     iterator.write16bit(this.newIndex, pos + 1);
/* 127 */     return pos;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.convert.TransformCall
 * JD-Core Version:    0.6.2
 */