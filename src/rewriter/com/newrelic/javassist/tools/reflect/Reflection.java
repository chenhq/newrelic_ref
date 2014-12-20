/*     */ package com.newrelic.javassist.tools.reflect;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CodeConverter;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtField;
/*     */ import com.newrelic.javassist.CtField.Initializer;
/*     */ import com.newrelic.javassist.CtMethod;
/*     */ import com.newrelic.javassist.CtMethod.ConstParameter;
/*     */ import com.newrelic.javassist.CtNewMethod;
/*     */ import com.newrelic.javassist.Modifier;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.Translator;
/*     */ 
/*     */ public class Reflection
/*     */   implements Translator
/*     */ {
/*     */   static final String classobjectField = "_classobject";
/*     */   static final String classobjectAccessor = "_getClass";
/*     */   static final String metaobjectField = "_metaobject";
/*     */   static final String metaobjectGetter = "_getMetaobject";
/*     */   static final String metaobjectSetter = "_setMetaobject";
/*     */   static final String readPrefix = "_r_";
/*     */   static final String writePrefix = "_w_";
/*     */   static final String metaobjectClassName = "com.newrelic.javassist.tools.reflect.Metaobject";
/*     */   static final String classMetaobjectClassName = "com.newrelic.javassist.tools.reflect.ClassMetaobject";
/*     */   protected CtMethod trapMethod;
/*     */   protected CtMethod trapStaticMethod;
/*     */   protected CtMethod trapRead;
/*     */   protected CtMethod trapWrite;
/*     */   protected CtClass[] readParam;
/*     */   protected ClassPool classPool;
/*     */   protected CodeConverter converter;
/*     */ 
/*     */   private boolean isExcluded(String name)
/*     */   {
/*  84 */     return (name.startsWith("_m_")) || (name.equals("_getClass")) || (name.equals("_setMetaobject")) || (name.equals("_getMetaobject")) || (name.startsWith("_r_")) || (name.startsWith("_w_"));
/*     */   }
/*     */ 
/*     */   public Reflection()
/*     */   {
/*  96 */     this.classPool = null;
/*  97 */     this.converter = new CodeConverter();
/*     */   }
/*     */ 
/*     */   public void start(ClassPool pool)
/*     */     throws NotFoundException
/*     */   {
/* 104 */     this.classPool = pool;
/* 105 */     String msg = "javassist.tools.reflect.Sample is not found or broken.";
/*     */     try
/*     */     {
/* 108 */       CtClass c = this.classPool.get("com.newrelic.javassist.tools.reflect.Sample");
/* 109 */       this.trapMethod = c.getDeclaredMethod("trap");
/* 110 */       this.trapStaticMethod = c.getDeclaredMethod("trapStatic");
/* 111 */       this.trapRead = c.getDeclaredMethod("trapRead");
/* 112 */       this.trapWrite = c.getDeclaredMethod("trapWrite");
/* 113 */       this.readParam = new CtClass[] { this.classPool.get("java.lang.Object") };
/*     */     }
/*     */     catch (NotFoundException e)
/*     */     {
/* 117 */       throw new RuntimeException("javassist.tools.reflect.Sample is not found or broken.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onLoad(ClassPool pool, String classname)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/* 128 */     CtClass clazz = pool.get(classname);
/* 129 */     clazz.instrument(this.converter);
/*     */   }
/*     */ 
/*     */   public boolean makeReflective(String classname, String metaobject, String metaclass)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/* 149 */     return makeReflective(this.classPool.get(classname), this.classPool.get(metaobject), this.classPool.get(metaclass));
/*     */   }
/*     */ 
/*     */   public boolean makeReflective(Class clazz, Class metaobject, Class metaclass)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/* 175 */     return makeReflective(clazz.getName(), metaobject.getName(), metaclass.getName());
/*     */   }
/*     */ 
/*     */   public boolean makeReflective(CtClass clazz, CtClass metaobject, CtClass metaclass)
/*     */     throws CannotCompileException, CannotReflectException, NotFoundException
/*     */   {
/* 202 */     if (clazz.isInterface()) {
/* 203 */       throw new CannotReflectException("Cannot reflect an interface: " + clazz.getName());
/*     */     }
/*     */ 
/* 206 */     if (clazz.subclassOf(this.classPool.get("com.newrelic.javassist.tools.reflect.ClassMetaobject"))) {
/* 207 */       throw new CannotReflectException("Cannot reflect a subclass of ClassMetaobject: " + clazz.getName());
/*     */     }
/*     */ 
/* 211 */     if (clazz.subclassOf(this.classPool.get("com.newrelic.javassist.tools.reflect.Metaobject"))) {
/* 212 */       throw new CannotReflectException("Cannot reflect a subclass of Metaobject: " + clazz.getName());
/*     */     }
/*     */ 
/* 216 */     registerReflectiveClass(clazz);
/* 217 */     return modifyClassfile(clazz, metaobject, metaclass);
/*     */   }
/*     */ 
/*     */   private void registerReflectiveClass(CtClass clazz)
/*     */   {
/* 225 */     CtField[] fs = clazz.getDeclaredFields();
/* 226 */     for (int i = 0; i < fs.length; i++) {
/* 227 */       CtField f = fs[i];
/* 228 */       int mod = f.getModifiers();
/* 229 */       if (((mod & 0x1) != 0) && ((mod & 0x10) == 0)) {
/* 230 */         String name = f.getName();
/* 231 */         this.converter.replaceFieldRead(f, clazz, "_r_" + name);
/* 232 */         this.converter.replaceFieldWrite(f, clazz, "_w_" + name);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean modifyClassfile(CtClass clazz, CtClass metaobject, CtClass metaclass)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/* 241 */     if (clazz.getAttribute("Reflective") != null) {
/* 242 */       return false;
/*     */     }
/* 244 */     clazz.setAttribute("Reflective", new byte[0]);
/*     */ 
/* 246 */     CtClass mlevel = this.classPool.get("com.newrelic.javassist.tools.reflect.Metalevel");
/* 247 */     boolean addMeta = !clazz.subtypeOf(mlevel);
/* 248 */     if (addMeta) {
/* 249 */       clazz.addInterface(mlevel);
/*     */     }
/* 251 */     processMethods(clazz, addMeta);
/* 252 */     processFields(clazz);
/*     */ 
/* 255 */     if (addMeta) {
/* 256 */       CtField f = new CtField(this.classPool.get("com.newrelic.javassist.tools.reflect.Metaobject"), "_metaobject", clazz);
/*     */ 
/* 258 */       f.setModifiers(4);
/* 259 */       clazz.addField(f, CtField.Initializer.byNewWithParams(metaobject));
/*     */ 
/* 261 */       clazz.addMethod(CtNewMethod.getter("_getMetaobject", f));
/* 262 */       clazz.addMethod(CtNewMethod.setter("_setMetaobject", f));
/*     */     }
/*     */ 
/* 265 */     CtField f = new CtField(this.classPool.get("com.newrelic.javassist.tools.reflect.ClassMetaobject"), "_classobject", clazz);
/*     */ 
/* 267 */     f.setModifiers(10);
/* 268 */     clazz.addField(f, CtField.Initializer.byNew(metaclass, new String[] { clazz.getName() }));
/*     */ 
/* 271 */     clazz.addMethod(CtNewMethod.getter("_getClass", f));
/* 272 */     return true;
/*     */   }
/*     */ 
/*     */   private void processMethods(CtClass clazz, boolean dontSearch)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/* 278 */     CtMethod[] ms = clazz.getMethods();
/* 279 */     for (int i = 0; i < ms.length; i++) {
/* 280 */       CtMethod m = ms[i];
/* 281 */       int mod = m.getModifiers();
/* 282 */       if ((Modifier.isPublic(mod)) && (!Modifier.isAbstract(mod)))
/* 283 */         processMethods0(mod, clazz, m, i, dontSearch);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processMethods0(int mod, CtClass clazz, CtMethod m, int identifier, boolean dontSearch)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/* 292 */     String name = m.getName();
/*     */ 
/* 294 */     if (isExcluded(name))
/*     */       return;
/*     */     CtMethod m2;
/* 298 */     if (m.getDeclaringClass() == clazz) {
/* 299 */       if (Modifier.isNative(mod)) {
/* 300 */         return;
/*     */       }
/* 302 */       CtMethod m2 = m;
/* 303 */       if (Modifier.isFinal(mod)) {
/* 304 */         mod &= -17;
/* 305 */         m2.setModifiers(mod);
/*     */       }
/*     */     }
/*     */     else {
/* 309 */       if (Modifier.isFinal(mod)) {
/* 310 */         return;
/*     */       }
/* 312 */       mod &= -257;
/* 313 */       m2 = CtNewMethod.delegator(findOriginal(m, dontSearch), clazz);
/* 314 */       m2.setModifiers(mod);
/* 315 */       clazz.addMethod(m2);
/*     */     }
/*     */ 
/* 318 */     m2.setName("_m_" + identifier + "_" + name);
/*     */     CtMethod body;
/*     */     CtMethod body;
/* 321 */     if (Modifier.isStatic(mod))
/* 322 */       body = this.trapStaticMethod;
/*     */     else {
/* 324 */       body = this.trapMethod;
/*     */     }
/* 326 */     CtMethod wmethod = CtNewMethod.wrapped(m.getReturnType(), name, m.getParameterTypes(), m.getExceptionTypes(), body, CtMethod.ConstParameter.integer(identifier), clazz);
/*     */ 
/* 331 */     wmethod.setModifiers(mod);
/* 332 */     clazz.addMethod(wmethod);
/*     */   }
/*     */ 
/*     */   private CtMethod findOriginal(CtMethod m, boolean dontSearch)
/*     */     throws NotFoundException
/*     */   {
/* 338 */     if (dontSearch) {
/* 339 */       return m;
/*     */     }
/* 341 */     String name = m.getName();
/* 342 */     CtMethod[] ms = m.getDeclaringClass().getDeclaredMethods();
/* 343 */     for (int i = 0; i < ms.length; i++) {
/* 344 */       String orgName = ms[i].getName();
/* 345 */       if ((orgName.endsWith(name)) && (orgName.startsWith("_m_")) && (ms[i].getSignature().equals(m.getSignature())))
/*     */       {
/* 348 */         return ms[i];
/*     */       }
/*     */     }
/* 351 */     return m;
/*     */   }
/*     */ 
/*     */   private void processFields(CtClass clazz)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/* 357 */     CtField[] fs = clazz.getDeclaredFields();
/* 358 */     for (int i = 0; i < fs.length; i++) {
/* 359 */       CtField f = fs[i];
/* 360 */       int mod = f.getModifiers();
/* 361 */       if (((mod & 0x1) != 0) && ((mod & 0x10) == 0)) {
/* 362 */         mod |= 8;
/* 363 */         String name = f.getName();
/* 364 */         CtClass ftype = f.getType();
/* 365 */         CtMethod wmethod = CtNewMethod.wrapped(ftype, "_r_" + name, this.readParam, null, this.trapRead, CtMethod.ConstParameter.string(name), clazz);
/*     */ 
/* 370 */         wmethod.setModifiers(mod);
/* 371 */         clazz.addMethod(wmethod);
/* 372 */         CtClass[] writeParam = new CtClass[2];
/* 373 */         writeParam[0] = this.classPool.get("java.lang.Object");
/* 374 */         writeParam[1] = ftype;
/* 375 */         wmethod = CtNewMethod.wrapped(CtClass.voidType, "_w_" + name, writeParam, null, this.trapWrite, CtMethod.ConstParameter.string(name), clazz);
/*     */ 
/* 379 */         wmethod.setModifiers(mod);
/* 380 */         clazz.addMethod(wmethod);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.reflect.Reflection
 * JD-Core Version:    0.6.2
 */