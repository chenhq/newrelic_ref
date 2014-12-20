/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public class ClassMap extends HashMap
/*     */ {
/*     */   private ClassMap parent;
/*     */ 
/*     */   public ClassMap()
/*     */   {
/*  53 */     this.parent = null;
/*     */   }
/*  55 */   ClassMap(ClassMap map) { this.parent = map; }
/*     */ 
/*     */ 
/*     */   public void put(CtClass oldname, CtClass newname)
/*     */   {
/*  68 */     put(oldname.getName(), newname.getName());
/*     */   }
/*     */ 
/*     */   public void put(String oldname, String newname)
/*     */   {
/*  90 */     if (oldname == newname) {
/*  91 */       return;
/*     */     }
/*  93 */     String oldname2 = toJvmName(oldname);
/*  94 */     String s = (String)get(oldname2);
/*  95 */     if ((s == null) || (!s.equals(oldname2)))
/*  96 */       super.put(oldname2, toJvmName(newname));
/*     */   }
/*     */ 
/*     */   public void putIfNone(String oldname, String newname)
/*     */   {
/* 109 */     if (oldname == newname) {
/* 110 */       return;
/*     */     }
/* 112 */     String oldname2 = toJvmName(oldname);
/* 113 */     String s = (String)get(oldname2);
/* 114 */     if (s == null)
/* 115 */       super.put(oldname2, toJvmName(newname));
/*     */   }
/*     */ 
/*     */   protected final void put0(Object oldname, Object newname) {
/* 119 */     super.put(oldname, newname);
/*     */   }
/*     */ 
/*     */   public Object get(Object jvmClassName)
/*     */   {
/* 133 */     Object found = super.get(jvmClassName);
/* 134 */     if ((found == null) && (this.parent != null)) {
/* 135 */       return this.parent.get(jvmClassName);
/*     */     }
/* 137 */     return found;
/*     */   }
/*     */ 
/*     */   public void fix(CtClass clazz)
/*     */   {
/* 144 */     fix(clazz.getName());
/*     */   }
/*     */ 
/*     */   public void fix(String name)
/*     */   {
/* 151 */     String name2 = toJvmName(name);
/* 152 */     super.put(name2, name2);
/*     */   }
/*     */ 
/*     */   public static String toJvmName(String classname)
/*     */   {
/* 160 */     return Descriptor.toJvmName(classname);
/*     */   }
/*     */ 
/*     */   public static String toJavaName(String classname)
/*     */   {
/* 168 */     return Descriptor.toJavaName(classname);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.ClassMap
 * JD-Core Version:    0.6.2
 */