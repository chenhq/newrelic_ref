/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.primitives.Primitives;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public final class ImmutableClassToInstanceMap<B> extends ForwardingMap<Class<? extends B>, B>
/*     */   implements ClassToInstanceMap<B>, Serializable
/*     */ {
/*     */   private final ImmutableMap<Class<? extends B>, B> delegate;
/*     */ 
/*     */   public static <B> Builder<B> builder()
/*     */   {
/*  44 */     return new Builder();
/*     */   }
/*     */ 
/*     */   public static <B, S extends B> ImmutableClassToInstanceMap<B> copyOf(Map<? extends Class<? extends S>, ? extends S> map)
/*     */   {
/* 126 */     if ((map instanceof ImmutableClassToInstanceMap))
/*     */     {
/* 129 */       ImmutableClassToInstanceMap cast = (ImmutableClassToInstanceMap)map;
/* 130 */       return cast;
/*     */     }
/* 132 */     return new Builder().putAll(map).build();
/*     */   }
/*     */ 
/*     */   private ImmutableClassToInstanceMap(ImmutableMap<Class<? extends B>, B> delegate)
/*     */   {
/* 139 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */   protected Map<Class<? extends B>, B> delegate() {
/* 143 */     return this.delegate;
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public <T extends B> T getInstance(Class<T> type)
/*     */   {
/* 150 */     return this.delegate.get(Preconditions.checkNotNull(type));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public <T extends B> T putInstance(Class<T> type, T value)
/*     */   {
/* 162 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public static final class Builder<B>
/*     */   {
/*  65 */     private final ImmutableMap.Builder<Class<? extends B>, B> mapBuilder = ImmutableMap.builder();
/*     */ 
/*     */     public <T extends B> Builder<B> put(Class<T> key, T value)
/*     */     {
/*  73 */       this.mapBuilder.put(key, value);
/*  74 */       return this;
/*     */     }
/*     */ 
/*     */     public <T extends B> Builder<B> putAll(Map<? extends Class<? extends T>, ? extends T> map)
/*     */     {
/*  88 */       for (Map.Entry entry : map.entrySet()) {
/*  89 */         Class type = (Class)entry.getKey();
/*  90 */         Object value = entry.getValue();
/*  91 */         this.mapBuilder.put(type, cast(type, value));
/*     */       }
/*  93 */       return this;
/*     */     }
/*     */ 
/*     */     private static <B, T extends B> T cast(Class<T> type, B value) {
/*  97 */       return Primitives.wrap(type).cast(value);
/*     */     }
/*     */ 
/*     */     public ImmutableClassToInstanceMap<B> build()
/*     */     {
/* 107 */       return new ImmutableClassToInstanceMap(this.mapBuilder.build(), null);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ImmutableClassToInstanceMap
 * JD-Core Version:    0.6.2
 */