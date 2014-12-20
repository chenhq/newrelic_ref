/*     */ package com.newrelic.org.slf4j.helpers;
/*     */ 
/*     */ import com.newrelic.org.slf4j.spi.MDCAdapter;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class BasicMDCAdapter
/*     */   implements MDCAdapter
/*     */ {
/*  47 */   private InheritableThreadLocal inheritableThreadLocal = new InheritableThreadLocal();
/*     */ 
/*     */   public void put(String key, String val)
/*     */   {
/*  62 */     if (key == null) {
/*  63 */       throw new IllegalArgumentException("key cannot be null");
/*     */     }
/*  65 */     HashMap map = (HashMap)this.inheritableThreadLocal.get();
/*  66 */     if (map == null) {
/*  67 */       map = new HashMap();
/*  68 */       this.inheritableThreadLocal.set(map);
/*     */     }
/*  70 */     map.put(key, val);
/*     */   }
/*     */ 
/*     */   public String get(String key)
/*     */   {
/*  77 */     HashMap hashMap = (HashMap)this.inheritableThreadLocal.get();
/*  78 */     if ((hashMap != null) && (key != null)) {
/*  79 */       return (String)hashMap.get(key);
/*     */     }
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */   public void remove(String key)
/*     */   {
/*  89 */     HashMap map = (HashMap)this.inheritableThreadLocal.get();
/*  90 */     if (map != null)
/*  91 */       map.remove(key);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  99 */     HashMap hashMap = (HashMap)this.inheritableThreadLocal.get();
/* 100 */     if (hashMap != null) {
/* 101 */       hashMap.clear();
/*     */ 
/* 104 */       this.inheritableThreadLocal.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set getKeys()
/*     */   {
/* 115 */     HashMap hashMap = (HashMap)this.inheritableThreadLocal.get();
/* 116 */     if (hashMap != null) {
/* 117 */       return hashMap.keySet();
/*     */     }
/* 119 */     return null;
/*     */   }
/*     */ 
/*     */   public Map getCopyOfContextMap()
/*     */   {
/* 128 */     HashMap hashMap = (HashMap)this.inheritableThreadLocal.get();
/* 129 */     if (hashMap != null) {
/* 130 */       return new HashMap(hashMap);
/*     */     }
/* 132 */     return null;
/*     */   }
/*     */ 
/*     */   public void setContextMap(Map contextMap)
/*     */   {
/* 137 */     HashMap hashMap = (HashMap)this.inheritableThreadLocal.get();
/* 138 */     if (hashMap != null) {
/* 139 */       hashMap.clear();
/* 140 */       hashMap.putAll(contextMap);
/*     */     } else {
/* 142 */       hashMap = new HashMap(contextMap);
/* 143 */       this.inheritableThreadLocal.set(hashMap);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.helpers.BasicMDCAdapter
 * JD-Core Version:    0.6.2
 */