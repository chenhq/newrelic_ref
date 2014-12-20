/*     */ package com.newrelic.javassist.scopedpool;
/*     */ 
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class SoftValueHashMap extends AbstractMap
/*     */   implements Map
/*     */ {
/*     */   private Map hash;
/*  63 */   private ReferenceQueue queue = new ReferenceQueue();
/*     */ 
/*     */   public Set entrySet()
/*     */   {
/*  55 */     processQueue();
/*  56 */     return this.hash.entrySet();
/*     */   }
/*     */ 
/*     */   private void processQueue()
/*     */   {
/*     */     SoftValueRef ref;
/*  71 */     while ((ref = (SoftValueRef)this.queue.poll()) != null)
/*  72 */       if (ref == (SoftValueRef)this.hash.get(ref.key))
/*     */       {
/*  75 */         this.hash.remove(ref.key);
/*     */       }
/*     */   }
/*     */ 
/*     */   public SoftValueHashMap(int initialCapacity, float loadFactor)
/*     */   {
/*  97 */     this.hash = new HashMap(initialCapacity, loadFactor);
/*     */   }
/*     */ 
/*     */   public SoftValueHashMap(int initialCapacity)
/*     */   {
/* 111 */     this.hash = new HashMap(initialCapacity);
/*     */   }
/*     */ 
/*     */   public SoftValueHashMap()
/*     */   {
/* 119 */     this.hash = new HashMap();
/*     */   }
/*     */ 
/*     */   public SoftValueHashMap(Map t)
/*     */   {
/* 132 */     this(Math.max(2 * t.size(), 11), 0.75F);
/* 133 */     putAll(t);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 145 */     processQueue();
/* 146 */     return this.hash.size();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 153 */     processQueue();
/* 154 */     return this.hash.isEmpty();
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object key)
/*     */   {
/* 165 */     processQueue();
/* 166 */     return this.hash.containsKey(key);
/*     */   }
/*     */ 
/*     */   public Object get(Object key)
/*     */   {
/* 180 */     processQueue();
/* 181 */     SoftReference ref = (SoftReference)this.hash.get(key);
/* 182 */     if (ref != null)
/* 183 */       return ref.get();
/* 184 */     return null;
/*     */   }
/*     */ 
/*     */   public Object put(Object key, Object value)
/*     */   {
/* 203 */     processQueue();
/* 204 */     Object rtn = this.hash.put(key, SoftValueRef.create(key, value, this.queue));
/* 205 */     if (rtn != null)
/* 206 */       rtn = ((SoftReference)rtn).get();
/* 207 */     return rtn;
/*     */   }
/*     */ 
/*     */   public Object remove(Object key)
/*     */   {
/* 221 */     processQueue();
/* 222 */     return this.hash.remove(key);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 229 */     processQueue();
/* 230 */     this.hash.clear();
/*     */   }
/*     */ 
/*     */   private static class SoftValueRef extends SoftReference
/*     */   {
/*     */     public Object key;
/*     */ 
/*     */     private SoftValueRef(Object key, Object val, ReferenceQueue q)
/*     */     {
/*  37 */       super(q);
/*  38 */       this.key = key;
/*     */     }
/*     */ 
/*     */     private static SoftValueRef create(Object key, Object val, ReferenceQueue q)
/*     */     {
/*  43 */       if (val == null) {
/*  44 */         return null;
/*     */       }
/*  46 */       return new SoftValueRef(key, val, q);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.scopedpool.SoftValueHashMap
 * JD-Core Version:    0.6.2
 */