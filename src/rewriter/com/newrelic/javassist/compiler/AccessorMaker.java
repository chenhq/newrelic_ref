/*     */ package com.newrelic.javassist.compiler;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.Bytecode;
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import com.newrelic.javassist.bytecode.ExceptionsAttribute;
/*     */ import com.newrelic.javassist.bytecode.FieldInfo;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.bytecode.SyntheticAttribute;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public class AccessorMaker
/*     */ {
/*     */   private CtClass clazz;
/*     */   private int uniqueNumber;
/*     */   private HashMap accessors;
/*     */   static final String lastParamType = "com.newrelic.javassist.runtime.Inner";
/*     */ 
/*     */   public AccessorMaker(CtClass c)
/*     */   {
/*  34 */     this.clazz = c;
/*  35 */     this.uniqueNumber = 1;
/*  36 */     this.accessors = new HashMap();
/*     */   }
/*     */ 
/*     */   public String getConstructor(CtClass c, String desc, MethodInfo orig)
/*     */     throws CompileError
/*     */   {
/*  42 */     String key = "<init>:" + desc;
/*  43 */     String consDesc = (String)this.accessors.get(key);
/*  44 */     if (consDesc != null) {
/*  45 */       return consDesc;
/*     */     }
/*  47 */     consDesc = Descriptor.appendParameter("com.newrelic.javassist.runtime.Inner", desc);
/*  48 */     ClassFile cf = this.clazz.getClassFile();
/*     */     try {
/*  50 */       ConstPool cp = cf.getConstPool();
/*  51 */       ClassPool pool = this.clazz.getClassPool();
/*  52 */       MethodInfo minfo = new MethodInfo(cp, "<init>", consDesc);
/*     */ 
/*  54 */       minfo.setAccessFlags(0);
/*  55 */       minfo.addAttribute(new SyntheticAttribute(cp));
/*  56 */       ExceptionsAttribute ea = orig.getExceptionsAttribute();
/*  57 */       if (ea != null) {
/*  58 */         minfo.addAttribute(ea.copy(cp, null));
/*     */       }
/*  60 */       CtClass[] params = Descriptor.getParameterTypes(desc, pool);
/*  61 */       Bytecode code = new Bytecode(cp);
/*  62 */       code.addAload(0);
/*  63 */       int regno = 1;
/*  64 */       for (int i = 0; i < params.length; i++)
/*  65 */         regno += code.addLoad(regno, params[i]);
/*  66 */       code.setMaxLocals(regno + 1);
/*  67 */       code.addInvokespecial(this.clazz, "<init>", desc);
/*     */ 
/*  69 */       code.addReturn(null);
/*  70 */       minfo.setCodeAttribute(code.toCodeAttribute());
/*  71 */       cf.addMethod(minfo);
/*     */     }
/*     */     catch (CannotCompileException e) {
/*  74 */       throw new CompileError(e);
/*     */     }
/*     */     catch (NotFoundException e) {
/*  77 */       throw new CompileError(e);
/*     */     }
/*     */ 
/*  80 */     this.accessors.put(key, consDesc);
/*  81 */     return consDesc;
/*     */   }
/*     */ 
/*     */   public String getMethodAccessor(String name, String desc, String accDesc, MethodInfo orig)
/*     */     throws CompileError
/*     */   {
/* 101 */     String key = name + ":" + desc;
/* 102 */     String accName = (String)this.accessors.get(key);
/* 103 */     if (accName != null) {
/* 104 */       return accName;
/*     */     }
/* 106 */     ClassFile cf = this.clazz.getClassFile();
/* 107 */     accName = findAccessorName(cf);
/*     */     try {
/* 109 */       ConstPool cp = cf.getConstPool();
/* 110 */       ClassPool pool = this.clazz.getClassPool();
/* 111 */       MethodInfo minfo = new MethodInfo(cp, accName, accDesc);
/*     */ 
/* 113 */       minfo.setAccessFlags(8);
/* 114 */       minfo.addAttribute(new SyntheticAttribute(cp));
/* 115 */       ExceptionsAttribute ea = orig.getExceptionsAttribute();
/* 116 */       if (ea != null) {
/* 117 */         minfo.addAttribute(ea.copy(cp, null));
/*     */       }
/* 119 */       CtClass[] params = Descriptor.getParameterTypes(accDesc, pool);
/* 120 */       int regno = 0;
/* 121 */       Bytecode code = new Bytecode(cp);
/* 122 */       for (int i = 0; i < params.length; i++) {
/* 123 */         regno += code.addLoad(regno, params[i]);
/*     */       }
/* 125 */       code.setMaxLocals(regno);
/* 126 */       if (desc == accDesc)
/* 127 */         code.addInvokestatic(this.clazz, name, desc);
/*     */       else {
/* 129 */         code.addInvokevirtual(this.clazz, name, desc);
/*     */       }
/* 131 */       code.addReturn(Descriptor.getReturnType(desc, pool));
/* 132 */       minfo.setCodeAttribute(code.toCodeAttribute());
/* 133 */       cf.addMethod(minfo);
/*     */     }
/*     */     catch (CannotCompileException e) {
/* 136 */       throw new CompileError(e);
/*     */     }
/*     */     catch (NotFoundException e) {
/* 139 */       throw new CompileError(e);
/*     */     }
/*     */ 
/* 142 */     this.accessors.put(key, accName);
/* 143 */     return accName;
/*     */   }
/*     */ 
/*     */   public MethodInfo getFieldGetter(FieldInfo finfo, boolean is_static)
/*     */     throws CompileError
/*     */   {
/* 152 */     String fieldName = finfo.getName();
/* 153 */     String key = fieldName + ":getter";
/* 154 */     Object res = this.accessors.get(key);
/* 155 */     if (res != null) {
/* 156 */       return (MethodInfo)res;
/*     */     }
/* 158 */     ClassFile cf = this.clazz.getClassFile();
/* 159 */     String accName = findAccessorName(cf);
/*     */     try {
/* 161 */       ConstPool cp = cf.getConstPool();
/* 162 */       ClassPool pool = this.clazz.getClassPool();
/* 163 */       String fieldType = finfo.getDescriptor();
/*     */       String accDesc;
/*     */       String accDesc;
/* 165 */       if (is_static)
/* 166 */         accDesc = "()" + fieldType;
/*     */       else {
/* 168 */         accDesc = "(" + Descriptor.of(this.clazz) + ")" + fieldType;
/*     */       }
/* 170 */       MethodInfo minfo = new MethodInfo(cp, accName, accDesc);
/* 171 */       minfo.setAccessFlags(8);
/* 172 */       minfo.addAttribute(new SyntheticAttribute(cp));
/* 173 */       Bytecode code = new Bytecode(cp);
/* 174 */       if (is_static) {
/* 175 */         code.addGetstatic(Bytecode.THIS, fieldName, fieldType);
/*     */       }
/*     */       else {
/* 178 */         code.addAload(0);
/* 179 */         code.addGetfield(Bytecode.THIS, fieldName, fieldType);
/* 180 */         code.setMaxLocals(1);
/*     */       }
/*     */ 
/* 183 */       code.addReturn(Descriptor.toCtClass(fieldType, pool));
/* 184 */       minfo.setCodeAttribute(code.toCodeAttribute());
/* 185 */       cf.addMethod(minfo);
/* 186 */       this.accessors.put(key, minfo);
/* 187 */       return minfo;
/*     */     }
/*     */     catch (CannotCompileException e) {
/* 190 */       throw new CompileError(e);
/*     */     }
/*     */     catch (NotFoundException e) {
/* 193 */       throw new CompileError(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public MethodInfo getFieldSetter(FieldInfo finfo, boolean is_static)
/*     */     throws CompileError
/*     */   {
/* 203 */     String fieldName = finfo.getName();
/* 204 */     String key = fieldName + ":setter";
/* 205 */     Object res = this.accessors.get(key);
/* 206 */     if (res != null) {
/* 207 */       return (MethodInfo)res;
/*     */     }
/* 209 */     ClassFile cf = this.clazz.getClassFile();
/* 210 */     String accName = findAccessorName(cf);
/*     */     try {
/* 212 */       ConstPool cp = cf.getConstPool();
/* 213 */       ClassPool pool = this.clazz.getClassPool();
/* 214 */       String fieldType = finfo.getDescriptor();
/*     */       String accDesc;
/*     */       String accDesc;
/* 216 */       if (is_static)
/* 217 */         accDesc = "(" + fieldType + ")V";
/*     */       else {
/* 219 */         accDesc = "(" + Descriptor.of(this.clazz) + fieldType + ")V";
/*     */       }
/* 221 */       MethodInfo minfo = new MethodInfo(cp, accName, accDesc);
/* 222 */       minfo.setAccessFlags(8);
/* 223 */       minfo.addAttribute(new SyntheticAttribute(cp));
/* 224 */       Bytecode code = new Bytecode(cp);
/*     */       int reg;
/* 226 */       if (is_static) {
/* 227 */         int reg = code.addLoad(0, Descriptor.toCtClass(fieldType, pool));
/* 228 */         code.addPutstatic(Bytecode.THIS, fieldName, fieldType);
/*     */       }
/*     */       else {
/* 231 */         code.addAload(0);
/* 232 */         reg = code.addLoad(1, Descriptor.toCtClass(fieldType, pool)) + 1;
/*     */ 
/* 234 */         code.addPutfield(Bytecode.THIS, fieldName, fieldType);
/*     */       }
/*     */ 
/* 237 */       code.addReturn(null);
/* 238 */       code.setMaxLocals(reg);
/* 239 */       minfo.setCodeAttribute(code.toCodeAttribute());
/* 240 */       cf.addMethod(minfo);
/* 241 */       this.accessors.put(key, minfo);
/* 242 */       return minfo;
/*     */     }
/*     */     catch (CannotCompileException e) {
/* 245 */       throw new CompileError(e);
/*     */     }
/*     */     catch (NotFoundException e) {
/* 248 */       throw new CompileError(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String findAccessorName(ClassFile cf) {
/*     */     String accName;
/*     */     do
/* 255 */       accName = "access$" + this.uniqueNumber++;
/* 256 */     while (cf.getMethod(accName) != null);
/* 257 */     return accName;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.AccessorMaker
 * JD-Core Version:    0.6.2
 */