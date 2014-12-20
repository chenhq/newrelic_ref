/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ final class CtArray extends CtClass
/*     */ {
/*     */   protected ClassPool pool;
/*  38 */   private CtClass[] interfaces = null;
/*     */ 
/*     */   CtArray(String name, ClassPool cp)
/*     */   {
/*  26 */     super(name);
/*  27 */     this.pool = cp;
/*     */   }
/*     */ 
/*     */   public ClassPool getClassPool() {
/*  31 */     return this.pool;
/*     */   }
/*     */ 
/*     */   public boolean isArray() {
/*  35 */     return true;
/*     */   }
/*     */ 
/*     */   public int getModifiers()
/*     */   {
/*  41 */     int mod = 16;
/*     */     try {
/*  43 */       mod |= getComponentType().getModifiers() & 0x7;
/*     */     }
/*     */     catch (NotFoundException e) {
/*     */     }
/*  47 */     return mod;
/*     */   }
/*     */ 
/*     */   public CtClass[] getInterfaces() throws NotFoundException {
/*  51 */     if (this.interfaces == null) {
/*  52 */       this.interfaces = new CtClass[] { this.pool.get("java.lang.Cloneable"), this.pool.get("java.io.Serializable") };
/*     */     }
/*     */ 
/*  55 */     return this.interfaces;
/*     */   }
/*     */ 
/*     */   public boolean subtypeOf(CtClass clazz) throws NotFoundException {
/*  59 */     if (super.subtypeOf(clazz)) {
/*  60 */       return true;
/*     */     }
/*  62 */     String cname = clazz.getName();
/*  63 */     if ((cname.equals("java.lang.Object")) || (cname.equals("java.lang.Cloneable")) || (cname.equals("java.io.Serializable")))
/*     */     {
/*  66 */       return true;
/*     */     }
/*  68 */     return (clazz.isArray()) && (getComponentType().subtypeOf(clazz.getComponentType()));
/*     */   }
/*     */ 
/*     */   public CtClass getComponentType() throws NotFoundException
/*     */   {
/*  73 */     String name = getName();
/*  74 */     return this.pool.get(name.substring(0, name.length() - 2));
/*     */   }
/*     */ 
/*     */   public CtClass getSuperclass() throws NotFoundException {
/*  78 */     return this.pool.get("java.lang.Object");
/*     */   }
/*     */ 
/*     */   public CtMethod[] getMethods() {
/*     */     try {
/*  83 */       return getSuperclass().getMethods();
/*     */     } catch (NotFoundException e) {
/*     */     }
/*  86 */     return super.getMethods();
/*     */   }
/*     */ 
/*     */   public CtMethod getMethod(String name, String desc)
/*     */     throws NotFoundException
/*     */   {
/*  93 */     return getSuperclass().getMethod(name, desc);
/*     */   }
/*     */ 
/*     */   public CtConstructor[] getConstructors() {
/*     */     try {
/*  98 */       return getSuperclass().getConstructors();
/*     */     } catch (NotFoundException e) {
/*     */     }
/* 101 */     return super.getConstructors();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtArray
 * JD-Core Version:    0.6.2
 */