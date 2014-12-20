/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.base.Equivalence;
/*     */ import com.newrelic.com.google.common.base.Function;
/*     */ import com.newrelic.com.google.common.base.Objects;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ @Deprecated
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ abstract class GenericMapMaker<K0, V0>
/*     */ {
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   MapMaker.RemovalListener<K0, V0> removalListener;
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   abstract GenericMapMaker<K0, V0> keyEquivalence(Equivalence<Object> paramEquivalence);
/*     */ 
/*     */   public abstract GenericMapMaker<K0, V0> initialCapacity(int paramInt);
/*     */ 
/*     */   abstract GenericMapMaker<K0, V0> maximumSize(int paramInt);
/*     */ 
/*     */   public abstract GenericMapMaker<K0, V0> concurrencyLevel(int paramInt);
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   public abstract GenericMapMaker<K0, V0> weakKeys();
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   public abstract GenericMapMaker<K0, V0> weakValues();
/*     */ 
/*     */   @Deprecated
/*     */   @GwtIncompatible("java.lang.ref.SoftReference")
/*     */   public abstract GenericMapMaker<K0, V0> softValues();
/*     */ 
/*     */   abstract GenericMapMaker<K0, V0> expireAfterWrite(long paramLong, TimeUnit paramTimeUnit);
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   abstract GenericMapMaker<K0, V0> expireAfterAccess(long paramLong, TimeUnit paramTimeUnit);
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   <K extends K0, V extends V0> MapMaker.RemovalListener<K, V> getRemovalListener()
/*     */   {
/* 131 */     return (MapMaker.RemovalListener)Objects.firstNonNull(this.removalListener, NullListener.INSTANCE);
/*     */   }
/*     */ 
/*     */   public abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeMap();
/*     */ 
/*     */   @GwtIncompatible("MapMakerInternalMap")
/*     */   abstract <K, V> MapMakerInternalMap<K, V> makeCustomMap();
/*     */ 
/*     */   @Deprecated
/*     */   abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeComputingMap(Function<? super K, ? extends V> paramFunction);
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   static enum NullListener
/*     */     implements MapMaker.RemovalListener<Object, Object>
/*     */   {
/*  53 */     INSTANCE;
/*     */ 
/*     */     public void onRemoval(MapMaker.RemovalNotification<Object, Object> notification)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.GenericMapMaker
 * JD-Core Version:    0.6.2
 */