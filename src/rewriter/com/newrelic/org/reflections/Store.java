/*     */ package com.newrelic.org.reflections;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Predicate;
/*     */ import com.newrelic.com.google.common.base.Supplier;
/*     */ import com.newrelic.com.google.common.collect.Collections2;
/*     */ import com.newrelic.com.google.common.collect.Multimap;
/*     */ import com.newrelic.com.google.common.collect.Multimaps;
/*     */ import com.newrelic.com.google.common.collect.SetMultimap;
/*     */ import com.newrelic.com.google.common.collect.Sets;
/*     */ import com.newrelic.org.reflections.scanners.FieldAnnotationsScanner;
/*     */ import com.newrelic.org.reflections.scanners.MethodAnnotationsScanner;
/*     */ import com.newrelic.org.reflections.scanners.ResourcesScanner;
/*     */ import com.newrelic.org.reflections.scanners.Scanner;
/*     */ import com.newrelic.org.reflections.scanners.SubTypesScanner;
/*     */ import com.newrelic.org.reflections.scanners.TypeAnnotationsScanner;
/*     */ import java.lang.annotation.Inherited;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class Store
/*     */ {
/*     */   private final Map<String, Multimap<String, String>> storeMap;
/*     */   private final transient boolean concurrent;
/* 250 */   private static final transient Supplier<Set<String>> setSupplier = new Supplier() {
/*     */     public Set<String> get() {
/* 252 */       return Sets.newHashSet();
/*     */     }
/* 250 */   };
/*     */ 
/*     */   protected Store()
/*     */   {
/*  27 */     this(false);
/*     */   }
/*     */   protected Store(boolean concurrent) {
/*  30 */     this.concurrent = concurrent;
/*  31 */     this.storeMap = new HashMap();
/*     */   }
/*     */ 
/*     */   private SetMultimap<String, String> createMultimap() {
/*  35 */     return this.concurrent ? Multimaps.synchronizedSetMultimap(Multimaps.newSetMultimap(new HashMap(), setSupplier)) : Multimaps.newSetMultimap(new HashMap(), setSupplier);
/*     */   }
/*     */ 
/*     */   public Multimap<String, String> getOrCreate(String indexName)
/*     */   {
/*  41 */     if (indexName.contains(".")) {
/*  42 */       indexName = indexName.substring(indexName.lastIndexOf(".") + 1);
/*     */     }
/*  44 */     Multimap mmap = (Multimap)this.storeMap.get(indexName);
/*  45 */     if (mmap == null) {
/*  46 */       this.storeMap.put(indexName, mmap = createMultimap());
/*     */     }
/*  48 */     return mmap;
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public Multimap<String, String> get(Class<? extends Scanner> scannerClass) {
/*  53 */     return (Multimap)this.storeMap.get(scannerClass.getSimpleName());
/*     */   }
/*     */ 
/*     */   public Set<String> get(Class<? extends Scanner> scannerClass, String[] keys)
/*     */   {
/*  58 */     Set result = Sets.newHashSet();
/*     */ 
/*  60 */     Multimap map = get(scannerClass);
/*  61 */     if (map != null) {
/*  62 */       for (String key : keys) {
/*  63 */         result.addAll(map.get(key));
/*     */       }
/*     */     }
/*     */ 
/*  67 */     return result;
/*     */   }
/*     */ 
/*     */   public Set<String> get(Class<? extends Scanner> scannerClass, Iterable<String> keys)
/*     */   {
/*  72 */     Set result = Sets.newHashSet();
/*     */ 
/*  74 */     Multimap map = get(scannerClass);
/*  75 */     if (map != null) {
/*  76 */       for (String key : keys) {
/*  77 */         result.addAll(map.get(key));
/*     */       }
/*     */     }
/*     */ 
/*  81 */     return result;
/*     */   }
/*     */ 
/*     */   public Map<String, Multimap<String, String>> getStoreMap()
/*     */   {
/*  86 */     return this.storeMap;
/*     */   }
/*     */ 
/*     */   void merge(Store outer)
/*     */   {
/*  91 */     if (outer != null)
/*  92 */       for (String indexName : outer.storeMap.keySet())
/*  93 */         getOrCreate(indexName).putAll((Multimap)outer.storeMap.get(indexName));
/*     */   }
/*     */ 
/*     */   public Integer getKeysCount()
/*     */   {
/* 100 */     Integer keys = Integer.valueOf(0);
/* 101 */     for (Multimap multimap : this.storeMap.values()) {
/* 102 */       keys = Integer.valueOf(keys.intValue() + multimap.keySet().size());
/*     */     }
/* 104 */     return keys;
/*     */   }
/*     */ 
/*     */   public Integer getValuesCount()
/*     */   {
/* 109 */     Integer values = Integer.valueOf(0);
/* 110 */     for (Multimap multimap : this.storeMap.values()) {
/* 111 */       values = Integer.valueOf(values.intValue() + multimap.size());
/*     */     }
/* 113 */     return values;
/*     */   }
/*     */ 
/*     */   public Set<String> getSubTypesOf(String type)
/*     */   {
/* 119 */     Set result = new HashSet();
/*     */ 
/* 121 */     Set subTypes = get(SubTypesScanner.class, new String[] { type });
/* 122 */     result.addAll(subTypes);
/*     */ 
/* 124 */     for (String subType : subTypes) {
/* 125 */       result.addAll(getSubTypesOf(subType));
/*     */     }
/*     */ 
/* 128 */     return result;
/*     */   }
/*     */ 
/*     */   public Set<String> getTypesAnnotatedWithDirectly(String annotation)
/*     */   {
/* 135 */     return get(TypeAnnotationsScanner.class, new String[] { annotation });
/*     */   }
/*     */ 
/*     */   public Set<String> getTypesAnnotatedWith(String annotation)
/*     */   {
/* 145 */     return getTypesAnnotatedWith(annotation, true);
/*     */   }
/*     */ 
/*     */   public Set<String> getTypesAnnotatedWith(String annotation, boolean honorInherited)
/*     */   {
/* 155 */     Set result = new HashSet();
/*     */ 
/* 157 */     if (isAnnotation(annotation)) {
/* 158 */       Set types = getTypesAnnotatedWithDirectly(annotation);
/* 159 */       Set inherited = getInheritedSubTypes(types, annotation, honorInherited);
/* 160 */       result.addAll(inherited);
/*     */     }
/* 162 */     return result;
/*     */   }
/*     */ 
/*     */   public Set<String> getInheritedSubTypes(Iterable<String> types, String annotation, boolean honorInherited) {
/* 166 */     Set result = Sets.newHashSet(types);
/*     */ 
/* 168 */     if ((honorInherited) && (isInheritedAnnotation(annotation)))
/*     */     {
/* 170 */       for (String type : types) {
/* 171 */         if (isClass(type))
/* 172 */           result.addAll(getSubTypesOf(type));
/*     */       }
/*     */     }
/* 175 */     else if (!honorInherited)
/*     */     {
/* 177 */       for (String type : types) {
/* 178 */         if (isAnnotation(type))
/* 179 */           result.addAll(getTypesAnnotatedWith(type, false));
/*     */         else {
/* 181 */           result.addAll(getSubTypesOf(type));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 186 */     return result;
/*     */   }
/*     */ 
/*     */   public Set<String> getMethodsAnnotatedWith(String annotation)
/*     */   {
/* 191 */     return get(MethodAnnotationsScanner.class, new String[] { annotation });
/*     */   }
/*     */ 
/*     */   public Set<String> getFieldsAnnotatedWith(String annotation)
/*     */   {
/* 196 */     return get(FieldAnnotationsScanner.class, new String[] { annotation });
/*     */   }
/*     */ 
/*     */   public Set<String> getResources(String key)
/*     */   {
/* 201 */     return get(ResourcesScanner.class, new String[] { key });
/*     */   }
/*     */ 
/*     */   public Set<String> getResources(Predicate<String> namePredicate)
/*     */   {
/* 206 */     Multimap mmap = get(ResourcesScanner.class);
/* 207 */     if (mmap != null) {
/* 208 */       return get(ResourcesScanner.class, Collections2.filter(mmap.keySet(), namePredicate));
/*     */     }
/* 210 */     return Sets.newHashSet();
/*     */   }
/*     */ 
/*     */   public Set<String> getResources(final Pattern pattern)
/*     */   {
/* 217 */     return getResources(new Predicate() {
/*     */       public boolean apply(String input) {
/* 219 */         return pattern.matcher(input).matches();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public boolean isClass(String type)
/*     */   {
/* 228 */     return !isInterface(type);
/*     */   }
/*     */ 
/*     */   public boolean isInterface(String aClass)
/*     */   {
/* 234 */     return ReflectionUtils.forName(aClass, new ClassLoader[0]).isInterface();
/*     */   }
/*     */ 
/*     */   public boolean isAnnotation(String typeAnnotatedWith)
/*     */   {
/* 239 */     Multimap mmap = get(TypeAnnotationsScanner.class);
/* 240 */     return (mmap != null) && (mmap.keySet().contains(typeAnnotatedWith));
/*     */   }
/*     */ 
/*     */   public boolean isInheritedAnnotation(String typeAnnotatedWith)
/*     */   {
/* 245 */     Multimap mmap = get(TypeAnnotationsScanner.class);
/* 246 */     return (mmap != null) && (mmap.get(Inherited.class.getName()).contains(typeAnnotatedWith));
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.Store
 * JD-Core Version:    0.6.2
 */