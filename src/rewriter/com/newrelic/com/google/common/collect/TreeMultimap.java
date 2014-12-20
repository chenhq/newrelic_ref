/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.NavigableMap;
/*     */ import java.util.NavigableSet;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public class TreeMultimap<K, V> extends AbstractSortedKeySortedSetMultimap<K, V>
/*     */ {
/*     */   private transient Comparator<? super K> keyComparator;
/*     */   private transient Comparator<? super V> valueComparator;
/*     */ 
/*     */   @GwtIncompatible("not needed in emulated source")
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create()
/*     */   {
/*  89 */     return new TreeMultimap(Ordering.natural(), Ordering.natural());
/*     */   }
/*     */ 
/*     */   public static <K, V> TreeMultimap<K, V> create(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator)
/*     */   {
/* 103 */     return new TreeMultimap((Comparator)Preconditions.checkNotNull(keyComparator), (Comparator)Preconditions.checkNotNull(valueComparator));
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 115 */     return new TreeMultimap(Ordering.natural(), Ordering.natural(), multimap);
/*     */   }
/*     */ 
/*     */   TreeMultimap(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator)
/*     */   {
/* 121 */     super(new TreeMap(keyComparator));
/* 122 */     this.keyComparator = keyComparator;
/* 123 */     this.valueComparator = valueComparator;
/*     */   }
/*     */ 
/*     */   private TreeMultimap(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator, Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 129 */     this(keyComparator, valueComparator);
/* 130 */     putAll(multimap);
/*     */   }
/*     */ 
/*     */   SortedSet<V> createCollection()
/*     */   {
/* 142 */     return new TreeSet(this.valueComparator);
/*     */   }
/*     */ 
/*     */   Collection<V> createCollection(@Nullable K key)
/*     */   {
/* 147 */     if (key == null) {
/* 148 */       keyComparator().compare(key, key);
/*     */     }
/* 150 */     return super.createCollection(key);
/*     */   }
/*     */ 
/*     */   public Comparator<? super K> keyComparator()
/*     */   {
/* 157 */     return this.keyComparator;
/*     */   }
/*     */ 
/*     */   public Comparator<? super V> valueComparator()
/*     */   {
/* 162 */     return this.valueComparator;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableMap")
/*     */   NavigableMap<K, Collection<V>> backingMap()
/*     */   {
/* 174 */     return (NavigableMap)super.backingMap();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public NavigableSet<V> get(@Nullable K key)
/*     */   {
/* 183 */     return (NavigableSet)super.get(key);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   Collection<V> unmodifiableCollectionSubclass(Collection<V> collection)
/*     */   {
/* 189 */     return Sets.unmodifiableNavigableSet((NavigableSet)collection);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   Collection<V> wrapCollection(K key, Collection<V> collection)
/*     */   {
/* 195 */     return new AbstractMapBasedMultimap.WrappedNavigableSet(this, key, (NavigableSet)collection, null);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public NavigableSet<K> keySet()
/*     */   {
/* 210 */     return (NavigableSet)super.keySet();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   NavigableSet<K> createKeySet()
/*     */   {
/* 216 */     return new AbstractMapBasedMultimap.NavigableKeySet(this, backingMap());
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableMap")
/*     */   public NavigableMap<K, Collection<V>> asMap()
/*     */   {
/* 231 */     return (NavigableMap)super.asMap();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableMap")
/*     */   NavigableMap<K, Collection<V>> createAsMap()
/*     */   {
/* 237 */     return new AbstractMapBasedMultimap.NavigableAsMap(this, backingMap());
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/* 247 */     stream.defaultWriteObject();
/* 248 */     stream.writeObject(keyComparator());
/* 249 */     stream.writeObject(valueComparator());
/* 250 */     Serialization.writeMultimap(this, stream);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectInputStream")
/*     */   private void readObject(ObjectInputStream stream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 257 */     stream.defaultReadObject();
/* 258 */     this.keyComparator = ((Comparator)Preconditions.checkNotNull((Comparator)stream.readObject()));
/* 259 */     this.valueComparator = ((Comparator)Preconditions.checkNotNull((Comparator)stream.readObject()));
/* 260 */     setMap(new TreeMap(this.keyComparator));
/* 261 */     Serialization.populateMultimap(this, stream);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.TreeMultimap
 * JD-Core Version:    0.6.2
 */