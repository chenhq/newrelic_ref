/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.annotations.VisibleForTesting;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public final class ArrayListMultimap<K, V> extends AbstractListMultimap<K, V>
/*     */ {
/*     */   private static final int DEFAULT_VALUES_PER_KEY = 3;
/*     */ 
/*     */   @VisibleForTesting
/*     */   transient int expectedValuesPerKey;
/*     */ 
/*     */   @GwtIncompatible("Not needed in emulated source.")
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <K, V> ArrayListMultimap<K, V> create()
/*     */   {
/*  78 */     return new ArrayListMultimap();
/*     */   }
/*     */ 
/*     */   public static <K, V> ArrayListMultimap<K, V> create(int expectedKeys, int expectedValuesPerKey)
/*     */   {
/*  92 */     return new ArrayListMultimap(expectedKeys, expectedValuesPerKey);
/*     */   }
/*     */ 
/*     */   public static <K, V> ArrayListMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 103 */     return new ArrayListMultimap(multimap);
/*     */   }
/*     */ 
/*     */   private ArrayListMultimap() {
/* 107 */     super(new HashMap());
/* 108 */     this.expectedValuesPerKey = 3;
/*     */   }
/*     */ 
/*     */   private ArrayListMultimap(int expectedKeys, int expectedValuesPerKey) {
/* 112 */     super(Maps.newHashMapWithExpectedSize(expectedKeys));
/* 113 */     CollectPreconditions.checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
/* 114 */     this.expectedValuesPerKey = expectedValuesPerKey;
/*     */   }
/*     */ 
/*     */   private ArrayListMultimap(Multimap<? extends K, ? extends V> multimap) {
/* 118 */     this(multimap.keySet().size(), (multimap instanceof ArrayListMultimap) ? ((ArrayListMultimap)multimap).expectedValuesPerKey : 3);
/*     */ 
/* 122 */     putAll(multimap);
/*     */   }
/*     */ 
/*     */   List<V> createCollection()
/*     */   {
/* 130 */     return new ArrayList(this.expectedValuesPerKey);
/*     */   }
/*     */ 
/*     */   public void trimToSize()
/*     */   {
/* 137 */     for (Collection collection : backingMap().values()) {
/* 138 */       ArrayList arrayList = (ArrayList)collection;
/* 139 */       arrayList.trimToSize();
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/* 150 */     stream.defaultWriteObject();
/* 151 */     stream.writeInt(this.expectedValuesPerKey);
/* 152 */     Serialization.writeMultimap(this, stream);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
/*     */   {
/* 158 */     stream.defaultReadObject();
/* 159 */     this.expectedValuesPerKey = stream.readInt();
/* 160 */     int distinctKeys = Serialization.readCount(stream);
/* 161 */     Map map = Maps.newHashMapWithExpectedSize(distinctKeys);
/* 162 */     setMap(map);
/* 163 */     Serialization.populateMultimap(this, stream, distinctKeys);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ArrayListMultimap
 * JD-Core Version:    0.6.2
 */