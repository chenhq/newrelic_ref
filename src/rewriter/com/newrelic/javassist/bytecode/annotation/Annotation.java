/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtMethod;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class Annotation
/*     */ {
/*     */   ConstPool pool;
/*     */   int typeIndex;
/*     */   LinkedHashMap members;
/*     */ 
/*     */   public Annotation(int type, ConstPool cp)
/*     */   {
/*  71 */     this.pool = cp;
/*  72 */     this.typeIndex = type;
/*  73 */     this.members = null;
/*     */   }
/*     */ 
/*     */   public Annotation(String typeName, ConstPool cp)
/*     */   {
/*  86 */     this(cp.addUtf8Info(Descriptor.of(typeName)), cp);
/*     */   }
/*     */ 
/*     */   public Annotation(ConstPool cp, CtClass clazz)
/*     */     throws NotFoundException
/*     */   {
/* 102 */     this(cp.addUtf8Info(Descriptor.of(clazz.getName())), cp);
/*     */ 
/* 104 */     if (!clazz.isInterface()) {
/* 105 */       throw new RuntimeException("Only interfaces are allowed for Annotation creation.");
/*     */     }
/*     */ 
/* 108 */     CtMethod[] methods = clazz.getDeclaredMethods();
/* 109 */     if (methods.length > 0) {
/* 110 */       this.members = new LinkedHashMap();
/*     */     }
/*     */ 
/* 113 */     for (int i = 0; i < methods.length; i++) {
/* 114 */       CtClass returnType = methods[i].getReturnType();
/* 115 */       addMemberValue(methods[i].getName(), createMemberValue(cp, returnType));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static MemberValue createMemberValue(ConstPool cp, CtClass type)
/*     */     throws NotFoundException
/*     */   {
/* 132 */     if (type == CtClass.booleanType)
/* 133 */       return new BooleanMemberValue(cp);
/* 134 */     if (type == CtClass.byteType)
/* 135 */       return new ByteMemberValue(cp);
/* 136 */     if (type == CtClass.charType)
/* 137 */       return new CharMemberValue(cp);
/* 138 */     if (type == CtClass.shortType)
/* 139 */       return new ShortMemberValue(cp);
/* 140 */     if (type == CtClass.intType)
/* 141 */       return new IntegerMemberValue(cp);
/* 142 */     if (type == CtClass.longType)
/* 143 */       return new LongMemberValue(cp);
/* 144 */     if (type == CtClass.floatType)
/* 145 */       return new FloatMemberValue(cp);
/* 146 */     if (type == CtClass.doubleType)
/* 147 */       return new DoubleMemberValue(cp);
/* 148 */     if (type.getName().equals("java.lang.Class"))
/* 149 */       return new ClassMemberValue(cp);
/* 150 */     if (type.getName().equals("java.lang.String"))
/* 151 */       return new StringMemberValue(cp);
/* 152 */     if (type.isArray()) {
/* 153 */       CtClass arrayType = type.getComponentType();
/* 154 */       MemberValue member = createMemberValue(cp, arrayType);
/* 155 */       return new ArrayMemberValue(member, cp);
/*     */     }
/* 157 */     if (type.isInterface()) {
/* 158 */       Annotation info = new Annotation(cp, type);
/* 159 */       return new AnnotationMemberValue(info, cp);
/*     */     }
/*     */ 
/* 165 */     EnumMemberValue emv = new EnumMemberValue(cp);
/* 166 */     emv.setType(type.getName());
/* 167 */     return emv;
/*     */   }
/*     */ 
/*     */   public void addMemberValue(int nameIndex, MemberValue value)
/*     */   {
/* 181 */     Pair p = new Pair();
/* 182 */     p.name = nameIndex;
/* 183 */     p.value = value;
/* 184 */     addMemberValue(p);
/*     */   }
/*     */ 
/*     */   public void addMemberValue(String name, MemberValue value)
/*     */   {
/* 194 */     Pair p = new Pair();
/* 195 */     p.name = this.pool.addUtf8Info(name);
/* 196 */     p.value = value;
/* 197 */     if (this.members == null) {
/* 198 */       this.members = new LinkedHashMap();
/*     */     }
/* 200 */     this.members.put(name, p);
/*     */   }
/*     */ 
/*     */   private void addMemberValue(Pair pair) {
/* 204 */     String name = this.pool.getUtf8Info(pair.name);
/* 205 */     if (this.members == null) {
/* 206 */       this.members = new LinkedHashMap();
/*     */     }
/* 208 */     this.members.put(name, pair);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 215 */     StringBuffer buf = new StringBuffer("@");
/* 216 */     buf.append(getTypeName());
/* 217 */     if (this.members != null) {
/* 218 */       buf.append("(");
/* 219 */       Iterator mit = this.members.keySet().iterator();
/* 220 */       while (mit.hasNext()) {
/* 221 */         String name = (String)mit.next();
/* 222 */         buf.append(name).append("=").append(getMemberValue(name));
/* 223 */         if (mit.hasNext())
/* 224 */           buf.append(", ");
/*     */       }
/* 226 */       buf.append(")");
/*     */     }
/*     */ 
/* 229 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public String getTypeName()
/*     */   {
/* 238 */     return Descriptor.toClassName(this.pool.getUtf8Info(this.typeIndex));
/*     */   }
/*     */ 
/*     */   public Set getMemberNames()
/*     */   {
/* 247 */     if (this.members == null) {
/* 248 */       return null;
/*     */     }
/* 250 */     return this.members.keySet();
/*     */   }
/*     */ 
/*     */   public MemberValue getMemberValue(String name)
/*     */   {
/* 269 */     if (this.members == null) {
/* 270 */       return null;
/*     */     }
/* 272 */     Pair p = (Pair)this.members.get(name);
/* 273 */     if (p == null) {
/* 274 */       return null;
/*     */     }
/* 276 */     return p.value;
/*     */   }
/*     */ 
/*     */   public Object toAnnotationType(ClassLoader cl, ClassPool cp)
/*     */     throws ClassNotFoundException, NoSuchClassError
/*     */   {
/* 294 */     return AnnotationImpl.make(cl, MemberValue.loadClass(cl, getTypeName()), cp, this);
/*     */   }
/*     */ 
/*     */   public void write(AnnotationsWriter writer)
/*     */     throws IOException
/*     */   {
/* 306 */     String typeName = this.pool.getUtf8Info(this.typeIndex);
/* 307 */     if (this.members == null) {
/* 308 */       writer.annotation(typeName, 0);
/* 309 */       return;
/*     */     }
/*     */ 
/* 312 */     writer.annotation(typeName, this.members.size());
/* 313 */     Iterator it = this.members.values().iterator();
/* 314 */     while (it.hasNext()) {
/* 315 */       Pair pair = (Pair)it.next();
/* 316 */       writer.memberValuePair(pair.name);
/* 317 */       pair.value.write(writer);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 326 */     if (obj == this)
/* 327 */       return true;
/* 328 */     if ((obj == null) || (!(obj instanceof Annotation))) {
/* 329 */       return false;
/*     */     }
/* 331 */     Annotation other = (Annotation)obj;
/*     */ 
/* 333 */     if (!getTypeName().equals(other.getTypeName())) {
/* 334 */       return false;
/*     */     }
/* 336 */     LinkedHashMap otherMembers = other.members;
/* 337 */     if (this.members == otherMembers)
/* 338 */       return true;
/* 339 */     if (this.members == null) {
/* 340 */       return otherMembers == null;
/*     */     }
/* 342 */     if (otherMembers == null) {
/* 343 */       return false;
/*     */     }
/* 345 */     return this.members.equals(otherMembers);
/*     */   }
/*     */ 
/*     */   static class Pair
/*     */   {
/*     */     int name;
/*     */     MemberValue value;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.Annotation
 * JD-Core Version:    0.6.2
 */