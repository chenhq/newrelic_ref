/*     */ package com.newrelic.javassist.bytecode.analysis;
/*     */ 
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class MultiType extends Type
/*     */ {
/*     */   private Map interfaces;
/*     */   private Type resolved;
/*     */   private Type potentialClass;
/*     */   private MultiType mergeSource;
/*  53 */   private boolean changed = false;
/*     */ 
/*     */   public MultiType(Map interfaces) {
/*  56 */     this(interfaces, null);
/*     */   }
/*     */ 
/*     */   public MultiType(Map interfaces, Type potentialClass) {
/*  60 */     super(null);
/*  61 */     this.interfaces = interfaces;
/*  62 */     this.potentialClass = potentialClass;
/*     */   }
/*     */ 
/*     */   public CtClass getCtClass()
/*     */   {
/*  70 */     if (this.resolved != null) {
/*  71 */       return this.resolved.getCtClass();
/*     */     }
/*  73 */     return Type.OBJECT.getCtClass();
/*     */   }
/*     */ 
/*     */   public Type getComponent()
/*     */   {
/*  80 */     return null;
/*     */   }
/*     */ 
/*     */   public int getSize()
/*     */   {
/*  87 */     return 1;
/*     */   }
/*     */ 
/*     */   public boolean isArray()
/*     */   {
/*  94 */     return false;
/*     */   }
/*     */ 
/*     */   boolean popChanged()
/*     */   {
/* 101 */     boolean changed = this.changed;
/* 102 */     this.changed = false;
/* 103 */     return changed;
/*     */   }
/*     */ 
/*     */   public boolean isAssignableFrom(Type type) {
/* 107 */     throw new UnsupportedOperationException("Not implemented");
/*     */   }
/*     */ 
/*     */   public boolean isAssignableTo(Type type) {
/* 111 */     if (this.resolved != null) {
/* 112 */       return type.isAssignableFrom(this.resolved);
/*     */     }
/* 114 */     if (Type.OBJECT.equals(type)) {
/* 115 */       return true;
/*     */     }
/* 117 */     if ((this.potentialClass != null) && (!type.isAssignableFrom(this.potentialClass))) {
/* 118 */       this.potentialClass = null;
/*     */     }
/* 120 */     Map map = mergeMultiAndSingle(this, type);
/*     */ 
/* 122 */     if ((map.size() == 1) && (this.potentialClass == null))
/*     */     {
/* 124 */       this.resolved = Type.get((CtClass)map.values().iterator().next());
/* 125 */       propogateResolved();
/*     */ 
/* 127 */       return true;
/*     */     }
/*     */ 
/* 131 */     if (map.size() >= 1) {
/* 132 */       this.interfaces = map;
/* 133 */       propogateState();
/*     */ 
/* 135 */       return true;
/*     */     }
/*     */ 
/* 138 */     if (this.potentialClass != null) {
/* 139 */       this.resolved = this.potentialClass;
/* 140 */       propogateResolved();
/*     */ 
/* 142 */       return true;
/*     */     }
/*     */ 
/* 145 */     return false;
/*     */   }
/*     */ 
/*     */   private void propogateState() {
/* 149 */     MultiType source = this.mergeSource;
/* 150 */     while (source != null) {
/* 151 */       source.interfaces = this.interfaces;
/* 152 */       source.potentialClass = this.potentialClass;
/* 153 */       source = source.mergeSource;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void propogateResolved() {
/* 158 */     MultiType source = this.mergeSource;
/* 159 */     while (source != null) {
/* 160 */       source.resolved = this.resolved;
/* 161 */       source = source.mergeSource;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isReference()
/*     */   {
/* 171 */     return true;
/*     */   }
/*     */ 
/*     */   private Map getAllMultiInterfaces(MultiType type) {
/* 175 */     Map map = new HashMap();
/*     */ 
/* 177 */     Iterator iter = type.interfaces.values().iterator();
/* 178 */     while (iter.hasNext()) {
/* 179 */       CtClass intf = (CtClass)iter.next();
/* 180 */       map.put(intf.getName(), intf);
/* 181 */       getAllInterfaces(intf, map);
/*     */     }
/*     */ 
/* 184 */     return map;
/*     */   }
/*     */ 
/*     */   private Map mergeMultiInterfaces(MultiType type1, MultiType type2)
/*     */   {
/* 189 */     Map map1 = getAllMultiInterfaces(type1);
/* 190 */     Map map2 = getAllMultiInterfaces(type2);
/*     */ 
/* 192 */     return findCommonInterfaces(map1, map2);
/*     */   }
/*     */ 
/*     */   private Map mergeMultiAndSingle(MultiType multi, Type single) {
/* 196 */     Map map1 = getAllMultiInterfaces(multi);
/* 197 */     Map map2 = getAllInterfaces(single.getCtClass(), null);
/*     */ 
/* 199 */     return findCommonInterfaces(map1, map2);
/*     */   }
/*     */ 
/*     */   private boolean inMergeSource(MultiType source) {
/* 203 */     while (source != null) {
/* 204 */       if (source == this) {
/* 205 */         return true;
/*     */       }
/* 207 */       source = source.mergeSource;
/*     */     }
/*     */ 
/* 210 */     return false;
/*     */   }
/*     */ 
/*     */   public Type merge(Type type) {
/* 214 */     if (this == type) {
/* 215 */       return this;
/*     */     }
/* 217 */     if (type == UNINIT) {
/* 218 */       return this;
/*     */     }
/* 220 */     if (type == BOGUS) {
/* 221 */       return BOGUS;
/*     */     }
/* 223 */     if (type == null) {
/* 224 */       return this;
/*     */     }
/* 226 */     if (this.resolved != null) {
/* 227 */       return this.resolved.merge(type);
/*     */     }
/* 229 */     if (this.potentialClass != null) {
/* 230 */       Type mergePotential = this.potentialClass.merge(type);
/* 231 */       if ((!mergePotential.equals(this.potentialClass)) || (mergePotential.popChanged())) {
/* 232 */         this.potentialClass = (Type.OBJECT.equals(mergePotential) ? null : mergePotential);
/* 233 */         this.changed = true;
/*     */       }
/*     */     }
/*     */     Map merged;
/* 239 */     if ((type instanceof MultiType)) {
/* 240 */       MultiType multi = (MultiType)type;
/*     */       Map merged;
/* 242 */       if (multi.resolved != null) {
/* 243 */         merged = mergeMultiAndSingle(this, multi.resolved);
/*     */       } else {
/* 245 */         Map merged = mergeMultiInterfaces(multi, this);
/* 246 */         if (!inMergeSource(multi))
/* 247 */           this.mergeSource = multi;
/*     */       }
/*     */     } else {
/* 250 */       merged = mergeMultiAndSingle(this, type);
/*     */     }
/*     */ 
/* 254 */     if ((merged.size() > 1) || ((merged.size() == 1) && (this.potentialClass != null)))
/*     */     {
/* 256 */       if (merged.size() != this.interfaces.size()) {
/* 257 */         this.changed = true;
/* 258 */       } else if (!this.changed) {
/* 259 */         Iterator iter = merged.keySet().iterator();
/* 260 */         while (iter.hasNext()) {
/* 261 */           if (!this.interfaces.containsKey(iter.next()))
/* 262 */             this.changed = true;
/*     */         }
/*     */       }
/* 265 */       this.interfaces = merged;
/* 266 */       propogateState();
/*     */ 
/* 268 */       return this;
/*     */     }
/*     */ 
/* 271 */     if (merged.size() == 1)
/* 272 */       this.resolved = Type.get((CtClass)merged.values().iterator().next());
/* 273 */     else if (this.potentialClass != null)
/* 274 */       this.resolved = this.potentialClass;
/*     */     else {
/* 276 */       this.resolved = OBJECT;
/*     */     }
/*     */ 
/* 279 */     propogateResolved();
/*     */ 
/* 281 */     return this.resolved;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o) {
/* 285 */     if (!(o instanceof MultiType)) {
/* 286 */       return false;
/*     */     }
/* 288 */     MultiType multi = (MultiType)o;
/* 289 */     if (this.resolved != null)
/* 290 */       return this.resolved.equals(multi.resolved);
/* 291 */     if (multi.resolved != null) {
/* 292 */       return false;
/*     */     }
/* 294 */     return this.interfaces.keySet().equals(multi.interfaces.keySet());
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 298 */     if (this.resolved != null) {
/* 299 */       return this.resolved.toString();
/*     */     }
/* 301 */     StringBuffer buffer = new StringBuffer("{");
/* 302 */     Iterator iter = this.interfaces.keySet().iterator();
/* 303 */     while (iter.hasNext()) {
/* 304 */       buffer.append(iter.next());
/* 305 */       buffer.append(", ");
/*     */     }
/* 307 */     buffer.setLength(buffer.length() - 2);
/* 308 */     if (this.potentialClass != null)
/* 309 */       buffer.append(", *").append(this.potentialClass.toString());
/* 310 */     buffer.append("}");
/* 311 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.analysis.MultiType
 * JD-Core Version:    0.6.2
 */