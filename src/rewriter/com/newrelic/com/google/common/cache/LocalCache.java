/*      */ package com.newrelic.com.google.common.cache;
/*      */ 
/*      */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*      */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*      */ import com.newrelic.com.google.common.annotations.VisibleForTesting;
/*      */ import com.newrelic.com.google.common.base.Equivalence;
/*      */ import com.newrelic.com.google.common.base.Function;
/*      */ import com.newrelic.com.google.common.base.Preconditions;
/*      */ import com.newrelic.com.google.common.base.Stopwatch;
/*      */ import com.newrelic.com.google.common.base.Supplier;
/*      */ import com.newrelic.com.google.common.base.Ticker;
/*      */ import com.newrelic.com.google.common.collect.AbstractSequentialIterator;
/*      */ import com.newrelic.com.google.common.collect.ImmutableMap;
/*      */ import com.newrelic.com.google.common.collect.Iterators;
/*      */ import com.newrelic.com.google.common.collect.Maps;
/*      */ import com.newrelic.com.google.common.collect.Sets;
/*      */ import com.newrelic.com.google.common.primitives.Ints;
/*      */ import com.newrelic.com.google.common.util.concurrent.ExecutionError;
/*      */ import com.newrelic.com.google.common.util.concurrent.Futures;
/*      */ import com.newrelic.com.google.common.util.concurrent.ListenableFuture;
/*      */ import com.newrelic.com.google.common.util.concurrent.ListeningExecutorService;
/*      */ import com.newrelic.com.google.common.util.concurrent.MoreExecutors;
/*      */ import com.newrelic.com.google.common.util.concurrent.SettableFuture;
/*      */ import com.newrelic.com.google.common.util.concurrent.UncheckedExecutionException;
/*      */ import com.newrelic.com.google.common.util.concurrent.Uninterruptibles;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.ref.Reference;
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.util.AbstractCollection;
/*      */ import java.util.AbstractMap;
/*      */ import java.util.AbstractQueue;
/*      */ import java.util.AbstractSet;
/*      */ import java.util.Collection;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Queue;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.Callable;
/*      */ import java.util.concurrent.ConcurrentLinkedQueue;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.concurrent.ExecutionException;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*      */ import java.util.concurrent.locks.ReentrantLock;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ import javax.annotation.Nullable;
/*      */ import javax.annotation.concurrent.GuardedBy;
/*      */ 
/*      */ @GwtCompatible(emulated=true)
/*      */ class LocalCache<K, V> extends AbstractMap<K, V>
/*      */   implements ConcurrentMap<K, V>
/*      */ {
/*      */   static final int MAXIMUM_CAPACITY = 1073741824;
/*      */   static final int MAX_SEGMENTS = 65536;
/*      */   static final int CONTAINS_VALUE_RETRIES = 3;
/*      */   static final int DRAIN_THRESHOLD = 63;
/*      */   static final int DRAIN_MAX = 16;
/*  158 */   static final Logger logger = Logger.getLogger(LocalCache.class.getName());
/*      */ 
/*  160 */   static final ListeningExecutorService sameThreadExecutor = MoreExecutors.sameThreadExecutor();
/*      */   final int segmentMask;
/*      */   final int segmentShift;
/*      */   final Segment<K, V>[] segments;
/*      */   final int concurrencyLevel;
/*      */   final Equivalence<Object> keyEquivalence;
/*      */   final Equivalence<Object> valueEquivalence;
/*      */   final Strength keyStrength;
/*      */   final Strength valueStrength;
/*      */   final long maxWeight;
/*      */   final Weigher<K, V> weigher;
/*      */   final long expireAfterAccessNanos;
/*      */   final long expireAfterWriteNanos;
/*      */   final long refreshNanos;
/*      */   final Queue<RemovalNotification<K, V>> removalNotificationQueue;
/*      */   final RemovalListener<K, V> removalListener;
/*      */   final Ticker ticker;
/*      */   final EntryFactory entryFactory;
/*      */   final AbstractCache.StatsCounter globalStatsCounter;
/*      */ 
/*      */   @Nullable
/*      */   final CacheLoader<? super K, V> defaultLoader;
/*  690 */   static final ValueReference<Object, Object> UNSET = new ValueReference()
/*      */   {
/*      */     public Object get() {
/*  693 */       return null;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/*  698 */       return 0;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<Object, Object> getEntry()
/*      */     {
/*  703 */       return null;
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<Object, Object> copyFor(ReferenceQueue<Object> queue, @Nullable Object value, LocalCache.ReferenceEntry<Object, Object> entry)
/*      */     {
/*  709 */       return this;
/*      */     }
/*      */ 
/*      */     public boolean isLoading()
/*      */     {
/*  714 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean isActive()
/*      */     {
/*  719 */       return false;
/*      */     }
/*      */ 
/*      */     public Object waitForValue()
/*      */     {
/*  724 */       return null;
/*      */     }
/*      */ 
/*      */     public void notifyNewValue(Object newValue)
/*      */     {
/*      */     }
/*  690 */   };
/*      */ 
/* 1021 */   static final Queue<? extends Object> DISCARDING_QUEUE = new AbstractQueue()
/*      */   {
/*      */     public boolean offer(Object o) {
/* 1024 */       return true;
/*      */     }
/*      */ 
/*      */     public Object peek()
/*      */     {
/* 1029 */       return null;
/*      */     }
/*      */ 
/*      */     public Object poll()
/*      */     {
/* 1034 */       return null;
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 1039 */       return 0;
/*      */     }
/*      */ 
/*      */     public Iterator<Object> iterator()
/*      */     {
/* 1044 */       return Iterators.emptyIterator();
/*      */     }
/* 1021 */   };
/*      */   Set<K> keySet;
/*      */   Collection<V> values;
/*      */   Set<Map.Entry<K, V>> entrySet;
/*      */ 
/*      */   LocalCache(CacheBuilder<? super K, ? super V> builder, @Nullable CacheLoader<? super K, V> loader)
/*      */   {
/*  240 */     this.concurrencyLevel = Math.min(builder.getConcurrencyLevel(), 65536);
/*      */ 
/*  242 */     this.keyStrength = builder.getKeyStrength();
/*  243 */     this.valueStrength = builder.getValueStrength();
/*      */ 
/*  245 */     this.keyEquivalence = builder.getKeyEquivalence();
/*  246 */     this.valueEquivalence = builder.getValueEquivalence();
/*      */ 
/*  248 */     this.maxWeight = builder.getMaximumWeight();
/*  249 */     this.weigher = builder.getWeigher();
/*  250 */     this.expireAfterAccessNanos = builder.getExpireAfterAccessNanos();
/*  251 */     this.expireAfterWriteNanos = builder.getExpireAfterWriteNanos();
/*  252 */     this.refreshNanos = builder.getRefreshNanos();
/*      */ 
/*  254 */     this.removalListener = builder.getRemovalListener();
/*  255 */     this.removalNotificationQueue = (this.removalListener == CacheBuilder.NullListener.INSTANCE ? discardingQueue() : new ConcurrentLinkedQueue());
/*      */ 
/*  259 */     this.ticker = builder.getTicker(recordsTime());
/*  260 */     this.entryFactory = EntryFactory.getFactory(this.keyStrength, usesAccessEntries(), usesWriteEntries());
/*  261 */     this.globalStatsCounter = ((AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
/*  262 */     this.defaultLoader = loader;
/*      */ 
/*  264 */     int initialCapacity = Math.min(builder.getInitialCapacity(), 1073741824);
/*  265 */     if ((evictsBySize()) && (!customWeigher())) {
/*  266 */       initialCapacity = Math.min(initialCapacity, (int)this.maxWeight);
/*      */     }
/*      */ 
/*  274 */     int segmentShift = 0;
/*  275 */     int segmentCount = 1;
/*      */ 
/*  277 */     while ((segmentCount < this.concurrencyLevel) && ((!evictsBySize()) || (segmentCount * 20 <= this.maxWeight))) {
/*  278 */       segmentShift++;
/*  279 */       segmentCount <<= 1;
/*      */     }
/*  281 */     this.segmentShift = (32 - segmentShift);
/*  282 */     this.segmentMask = (segmentCount - 1);
/*      */ 
/*  284 */     this.segments = newSegmentArray(segmentCount);
/*      */ 
/*  286 */     int segmentCapacity = initialCapacity / segmentCount;
/*  287 */     if (segmentCapacity * segmentCount < initialCapacity) {
/*  288 */       segmentCapacity++;
/*      */     }
/*      */ 
/*  291 */     int segmentSize = 1;
/*  292 */     while (segmentSize < segmentCapacity) {
/*  293 */       segmentSize <<= 1;
/*      */     }
/*      */ 
/*  296 */     if (evictsBySize())
/*      */     {
/*  298 */       long maxSegmentWeight = this.maxWeight / segmentCount + 1L;
/*  299 */       long remainder = this.maxWeight % segmentCount;
/*  300 */       for (int i = 0; i < this.segments.length; i++) {
/*  301 */         if (i == remainder) {
/*  302 */           maxSegmentWeight -= 1L;
/*      */         }
/*  304 */         this.segments[i] = createSegment(segmentSize, maxSegmentWeight, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
/*      */       }
/*      */     }
/*      */     else {
/*  308 */       for (int i = 0; i < this.segments.length; i++)
/*  309 */         this.segments[i] = createSegment(segmentSize, -1L, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean evictsBySize()
/*      */   {
/*  316 */     return this.maxWeight >= 0L;
/*      */   }
/*      */ 
/*      */   boolean customWeigher() {
/*  320 */     return this.weigher != CacheBuilder.OneWeigher.INSTANCE;
/*      */   }
/*      */ 
/*      */   boolean expires() {
/*  324 */     return (expiresAfterWrite()) || (expiresAfterAccess());
/*      */   }
/*      */ 
/*      */   boolean expiresAfterWrite() {
/*  328 */     return this.expireAfterWriteNanos > 0L;
/*      */   }
/*      */ 
/*      */   boolean expiresAfterAccess() {
/*  332 */     return this.expireAfterAccessNanos > 0L;
/*      */   }
/*      */ 
/*      */   boolean refreshes() {
/*  336 */     return this.refreshNanos > 0L;
/*      */   }
/*      */ 
/*      */   boolean usesAccessQueue() {
/*  340 */     return (expiresAfterAccess()) || (evictsBySize());
/*      */   }
/*      */ 
/*      */   boolean usesWriteQueue() {
/*  344 */     return expiresAfterWrite();
/*      */   }
/*      */ 
/*      */   boolean recordsWrite() {
/*  348 */     return (expiresAfterWrite()) || (refreshes());
/*      */   }
/*      */ 
/*      */   boolean recordsAccess() {
/*  352 */     return expiresAfterAccess();
/*      */   }
/*      */ 
/*      */   boolean recordsTime() {
/*  356 */     return (recordsWrite()) || (recordsAccess());
/*      */   }
/*      */ 
/*      */   boolean usesWriteEntries() {
/*  360 */     return (usesWriteQueue()) || (recordsWrite());
/*      */   }
/*      */ 
/*      */   boolean usesAccessEntries() {
/*  364 */     return (usesAccessQueue()) || (recordsAccess());
/*      */   }
/*      */ 
/*      */   boolean usesKeyReferences() {
/*  368 */     return this.keyStrength != Strength.STRONG;
/*      */   }
/*      */ 
/*      */   boolean usesValueReferences() {
/*  372 */     return this.valueStrength != Strength.STRONG;
/*      */   }
/*      */ 
/*      */   static <K, V> ValueReference<K, V> unset()
/*      */   {
/*  736 */     return UNSET;
/*      */   }
/*      */ 
/*      */   static <K, V> ReferenceEntry<K, V> nullEntry()
/*      */   {
/* 1018 */     return NullEntry.INSTANCE;
/*      */   }
/*      */ 
/*      */   static <E> Queue<E> discardingQueue()
/*      */   {
/* 1053 */     return DISCARDING_QUEUE;
/*      */   }
/*      */ 
/*      */   static int rehash(int h)
/*      */   {
/* 1799 */     h += (h << 15 ^ 0xFFFFCD7D);
/* 1800 */     h ^= h >>> 10;
/* 1801 */     h += (h << 3);
/* 1802 */     h ^= h >>> 6;
/* 1803 */     h += (h << 2) + (h << 14);
/* 1804 */     return h ^ h >>> 16;
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   @VisibleForTesting
/*      */   ReferenceEntry<K, V> newEntry(K key, int hash, @Nullable ReferenceEntry<K, V> next)
/*      */   {
/* 1813 */     return segmentFor(hash).newEntry(key, hash, next);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   @VisibleForTesting
/*      */   ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext)
/*      */   {
/* 1822 */     int hash = original.getHash();
/* 1823 */     return segmentFor(hash).copyEntry(original, newNext);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   @VisibleForTesting
/*      */   ValueReference<K, V> newValueReference(ReferenceEntry<K, V> entry, V value, int weight)
/*      */   {
/* 1832 */     int hash = entry.getHash();
/* 1833 */     return this.valueStrength.referenceValue(segmentFor(hash), entry, Preconditions.checkNotNull(value), weight);
/*      */   }
/*      */ 
/*      */   int hash(@Nullable Object key) {
/* 1837 */     int h = this.keyEquivalence.hash(key);
/* 1838 */     return rehash(h);
/*      */   }
/*      */ 
/*      */   void reclaimValue(ValueReference<K, V> valueReference) {
/* 1842 */     ReferenceEntry entry = valueReference.getEntry();
/* 1843 */     int hash = entry.getHash();
/* 1844 */     segmentFor(hash).reclaimValue(entry.getKey(), hash, valueReference);
/*      */   }
/*      */ 
/*      */   void reclaimKey(ReferenceEntry<K, V> entry) {
/* 1848 */     int hash = entry.getHash();
/* 1849 */     segmentFor(hash).reclaimKey(entry, hash);
/*      */   }
/*      */ 
/*      */   @VisibleForTesting
/*      */   boolean isLive(ReferenceEntry<K, V> entry, long now)
/*      */   {
/* 1858 */     return segmentFor(entry.getHash()).getLiveValue(entry, now) != null;
/*      */   }
/*      */ 
/*      */   Segment<K, V> segmentFor(int hash)
/*      */   {
/* 1869 */     return this.segments[(hash >>> this.segmentShift & this.segmentMask)];
/*      */   }
/*      */ 
/*      */   Segment<K, V> createSegment(int initialCapacity, long maxSegmentWeight, AbstractCache.StatsCounter statsCounter)
/*      */   {
/* 1874 */     return new Segment(this, initialCapacity, maxSegmentWeight, statsCounter);
/*      */   }
/*      */ 
/*      */   @Nullable
/*      */   V getLiveValue(ReferenceEntry<K, V> entry, long now)
/*      */   {
/* 1885 */     if (entry.getKey() == null) {
/* 1886 */       return null;
/*      */     }
/* 1888 */     Object value = entry.getValueReference().get();
/* 1889 */     if (value == null) {
/* 1890 */       return null;
/*      */     }
/*      */ 
/* 1893 */     if (isExpired(entry, now)) {
/* 1894 */       return null;
/*      */     }
/* 1896 */     return value;
/*      */   }
/*      */ 
/*      */   boolean isExpired(ReferenceEntry<K, V> entry, long now)
/*      */   {
/* 1905 */     Preconditions.checkNotNull(entry);
/* 1906 */     if ((expiresAfterAccess()) && (now - entry.getAccessTime() >= this.expireAfterAccessNanos))
/*      */     {
/* 1908 */       return true;
/*      */     }
/* 1910 */     if ((expiresAfterWrite()) && (now - entry.getWriteTime() >= this.expireAfterWriteNanos))
/*      */     {
/* 1912 */       return true;
/*      */     }
/* 1914 */     return false;
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void connectAccessOrder(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next)
/*      */   {
/* 1921 */     previous.setNextInAccessQueue(next);
/* 1922 */     next.setPreviousInAccessQueue(previous);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void nullifyAccessOrder(ReferenceEntry<K, V> nulled) {
/* 1927 */     ReferenceEntry nullEntry = nullEntry();
/* 1928 */     nulled.setNextInAccessQueue(nullEntry);
/* 1929 */     nulled.setPreviousInAccessQueue(nullEntry);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void connectWriteOrder(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next) {
/* 1934 */     previous.setNextInWriteQueue(next);
/* 1935 */     next.setPreviousInWriteQueue(previous);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void nullifyWriteOrder(ReferenceEntry<K, V> nulled) {
/* 1940 */     ReferenceEntry nullEntry = nullEntry();
/* 1941 */     nulled.setNextInWriteQueue(nullEntry);
/* 1942 */     nulled.setPreviousInWriteQueue(nullEntry);
/*      */   }
/*      */ 
/*      */   void processPendingNotifications()
/*      */   {
/*      */     RemovalNotification notification;
/* 1952 */     while ((notification = (RemovalNotification)this.removalNotificationQueue.poll()) != null)
/*      */       try {
/* 1954 */         this.removalListener.onRemoval(notification);
/*      */       } catch (Throwable e) {
/* 1956 */         logger.log(Level.WARNING, "Exception thrown by removal listener", e);
/*      */       }
/*      */   }
/*      */ 
/*      */   final Segment<K, V>[] newSegmentArray(int ssize)
/*      */   {
/* 1963 */     return new Segment[ssize];
/*      */   }
/*      */ 
/*      */   public void cleanUp()
/*      */   {
/* 3857 */     for (Segment segment : this.segments)
/* 3858 */       segment.cleanUp();
/*      */   }
/*      */ 
/*      */   public boolean isEmpty()
/*      */   {
/* 3873 */     long sum = 0L;
/* 3874 */     Segment[] segments = this.segments;
/* 3875 */     for (int i = 0; i < segments.length; i++) {
/* 3876 */       if (segments[i].count != 0) {
/* 3877 */         return false;
/*      */       }
/* 3879 */       sum += segments[i].modCount;
/*      */     }
/*      */ 
/* 3882 */     if (sum != 0L) {
/* 3883 */       for (int i = 0; i < segments.length; i++) {
/* 3884 */         if (segments[i].count != 0) {
/* 3885 */           return false;
/*      */         }
/* 3887 */         sum -= segments[i].modCount;
/*      */       }
/* 3889 */       if (sum != 0L) {
/* 3890 */         return false;
/*      */       }
/*      */     }
/* 3893 */     return true;
/*      */   }
/*      */ 
/*      */   long longSize() {
/* 3897 */     Segment[] segments = this.segments;
/* 3898 */     long sum = 0L;
/* 3899 */     for (int i = 0; i < segments.length; i++) {
/* 3900 */       sum += segments[i].count;
/*      */     }
/* 3902 */     return sum;
/*      */   }
/*      */ 
/*      */   public int size()
/*      */   {
/* 3907 */     return Ints.saturatedCast(longSize());
/*      */   }
/*      */ 
/*      */   @Nullable
/*      */   public V get(@Nullable Object key)
/*      */   {
/* 3913 */     if (key == null) {
/* 3914 */       return null;
/*      */     }
/* 3916 */     int hash = hash(key);
/* 3917 */     return segmentFor(hash).get(key, hash);
/*      */   }
/*      */ 
/*      */   @Nullable
/*      */   public V getIfPresent(Object key) {
/* 3922 */     int hash = hash(Preconditions.checkNotNull(key));
/* 3923 */     Object value = segmentFor(hash).get(key, hash);
/* 3924 */     if (value == null)
/* 3925 */       this.globalStatsCounter.recordMisses(1);
/*      */     else {
/* 3927 */       this.globalStatsCounter.recordHits(1);
/*      */     }
/* 3929 */     return value;
/*      */   }
/*      */ 
/*      */   V get(K key, CacheLoader<? super K, V> loader) throws ExecutionException {
/* 3933 */     int hash = hash(Preconditions.checkNotNull(key));
/* 3934 */     return segmentFor(hash).get(key, hash, loader);
/*      */   }
/*      */ 
/*      */   V getOrLoad(K key) throws ExecutionException {
/* 3938 */     return get(key, this.defaultLoader);
/*      */   }
/*      */ 
/*      */   ImmutableMap<K, V> getAllPresent(Iterable<?> keys) {
/* 3942 */     int hits = 0;
/* 3943 */     int misses = 0;
/*      */ 
/* 3945 */     Map result = Maps.newLinkedHashMap();
/* 3946 */     for (Iterator i$ = keys.iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 3947 */       Object value = get(key);
/* 3948 */       if (value == null) {
/* 3949 */         misses++;
/*      */       }
/*      */       else
/*      */       {
/* 3953 */         Object castKey = key;
/* 3954 */         result.put(castKey, value);
/* 3955 */         hits++;
/*      */       }
/*      */     }
/* 3958 */     this.globalStatsCounter.recordHits(hits);
/* 3959 */     this.globalStatsCounter.recordMisses(misses);
/* 3960 */     return ImmutableMap.copyOf(result);
/*      */   }
/*      */ 
/*      */   ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
/* 3964 */     int hits = 0;
/* 3965 */     int misses = 0;
/*      */ 
/* 3967 */     Map result = Maps.newLinkedHashMap();
/* 3968 */     Set keysToLoad = Sets.newLinkedHashSet();
/* 3969 */     for (Iterator i$ = keys.iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 3970 */       Object value = get(key);
/* 3971 */       if (!result.containsKey(key)) {
/* 3972 */         result.put(key, value);
/* 3973 */         if (value == null) {
/* 3974 */           misses++;
/* 3975 */           keysToLoad.add(key);
/*      */         } else {
/* 3977 */           hits++;
/*      */         }
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 3983 */       if (!keysToLoad.isEmpty()) {
/*      */         Iterator i$;
/*      */         try { newEntries = loadAll(keysToLoad, this.defaultLoader);
/* 3986 */           for (i$ = keysToLoad.iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 3987 */             Object value = newEntries.get(key);
/* 3988 */             if (value == null) {
/* 3989 */               throw new CacheLoader.InvalidCacheLoadException("loadAll failed to return a value for " + key);
/*      */             }
/* 3991 */             result.put(key, value);
/*      */           }
/*      */         }
/*      */         catch (CacheLoader.UnsupportedLoadingOperationException e)
/*      */         {
/* 3995 */           Map newEntries;
/*      */           Iterator i$;
/* 3995 */           i$ = keysToLoad.iterator(); } while (i$.hasNext()) { Object key = i$.next();
/* 3996 */           misses--;
/* 3997 */           result.put(key, get(key, this.defaultLoader));
/*      */         }
/*      */       }
/*      */ 
/* 4001 */       return ImmutableMap.copyOf(result);
/*      */     } finally {
/* 4003 */       this.globalStatsCounter.recordHits(hits);
/* 4004 */       this.globalStatsCounter.recordMisses(misses);
/*      */     }
/*      */   }
/*      */ 
/*      */   @Nullable
/*      */   Map<K, V> loadAll(Set<? extends K> keys, CacheLoader<? super K, V> loader) throws ExecutionException
/*      */   {
/* 4015 */     Preconditions.checkNotNull(loader);
/* 4016 */     Preconditions.checkNotNull(keys);
/* 4017 */     Stopwatch stopwatch = Stopwatch.createStarted();
/*      */ 
/* 4019 */     boolean success = false;
/*      */     Map result;
/*      */     try {
/* 4022 */       Map map = loader.loadAll(keys);
/* 4023 */       result = map;
/* 4024 */       success = true;
/*      */     } catch (CacheLoader.UnsupportedLoadingOperationException e) {
/* 4026 */       success = true;
/* 4027 */       throw e;
/*      */     } catch (InterruptedException e) {
/* 4029 */       Thread.currentThread().interrupt();
/* 4030 */       throw new ExecutionException(e);
/*      */     } catch (RuntimeException e) {
/* 4032 */       throw new UncheckedExecutionException(e);
/*      */     } catch (Exception e) {
/* 4034 */       throw new ExecutionException(e);
/*      */     } catch (Error e) {
/* 4036 */       throw new ExecutionError(e);
/*      */     } finally {
/* 4038 */       if (!success) {
/* 4039 */         this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
/*      */       }
/*      */     }
/*      */ 
/* 4043 */     if (result == null) {
/* 4044 */       this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
/* 4045 */       throw new CacheLoader.InvalidCacheLoadException(loader + " returned null map from loadAll");
/*      */     }
/*      */ 
/* 4048 */     stopwatch.stop();
/*      */ 
/* 4050 */     boolean nullsPresent = false;
/* 4051 */     for (Map.Entry entry : result.entrySet()) {
/* 4052 */       Object key = entry.getKey();
/* 4053 */       Object value = entry.getValue();
/* 4054 */       if ((key == null) || (value == null))
/*      */       {
/* 4056 */         nullsPresent = true;
/*      */       }
/* 4058 */       else put(key, value);
/*      */ 
/*      */     }
/*      */ 
/* 4062 */     if (nullsPresent) {
/* 4063 */       this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
/* 4064 */       throw new CacheLoader.InvalidCacheLoadException(loader + " returned null keys or values from loadAll");
/*      */     }
/*      */ 
/* 4068 */     this.globalStatsCounter.recordLoadSuccess(stopwatch.elapsed(TimeUnit.NANOSECONDS));
/* 4069 */     return result;
/*      */   }
/*      */ 
/*      */   ReferenceEntry<K, V> getEntry(@Nullable Object key)
/*      */   {
/* 4078 */     if (key == null) {
/* 4079 */       return null;
/*      */     }
/* 4081 */     int hash = hash(key);
/* 4082 */     return segmentFor(hash).getEntry(key, hash);
/*      */   }
/*      */ 
/*      */   void refresh(K key) {
/* 4086 */     int hash = hash(Preconditions.checkNotNull(key));
/* 4087 */     segmentFor(hash).refresh(key, hash, this.defaultLoader, false);
/*      */   }
/*      */ 
/*      */   public boolean containsKey(@Nullable Object key)
/*      */   {
/* 4093 */     if (key == null) {
/* 4094 */       return false;
/*      */     }
/* 4096 */     int hash = hash(key);
/* 4097 */     return segmentFor(hash).containsKey(key, hash);
/*      */   }
/*      */ 
/*      */   public boolean containsValue(@Nullable Object value)
/*      */   {
/* 4103 */     if (value == null) {
/* 4104 */       return false;
/*      */     }
/*      */ 
/* 4112 */     long now = this.ticker.read();
/* 4113 */     Segment[] segments = this.segments;
/* 4114 */     long last = -1L;
/* 4115 */     for (int i = 0; i < 3; i++) {
/* 4116 */       long sum = 0L;
/* 4117 */       for (Segment segment : segments)
/*      */       {
/* 4120 */         int c = segment.count;
/*      */ 
/* 4122 */         AtomicReferenceArray table = segment.table;
/* 4123 */         for (int j = 0; j < table.length(); j++) {
/* 4124 */           for (ReferenceEntry e = (ReferenceEntry)table.get(j); e != null; e = e.getNext()) {
/* 4125 */             Object v = segment.getLiveValue(e, now);
/* 4126 */             if ((v != null) && (this.valueEquivalence.equivalent(value, v))) {
/* 4127 */               return true;
/*      */             }
/*      */           }
/*      */         }
/* 4131 */         sum += segment.modCount;
/*      */       }
/* 4133 */       if (sum == last) {
/*      */         break;
/*      */       }
/* 4136 */       last = sum;
/*      */     }
/* 4138 */     return false;
/*      */   }
/*      */ 
/*      */   public V put(K key, V value)
/*      */   {
/* 4143 */     Preconditions.checkNotNull(key);
/* 4144 */     Preconditions.checkNotNull(value);
/* 4145 */     int hash = hash(key);
/* 4146 */     return segmentFor(hash).put(key, hash, value, false);
/*      */   }
/*      */ 
/*      */   public V putIfAbsent(K key, V value)
/*      */   {
/* 4151 */     Preconditions.checkNotNull(key);
/* 4152 */     Preconditions.checkNotNull(value);
/* 4153 */     int hash = hash(key);
/* 4154 */     return segmentFor(hash).put(key, hash, value, true);
/*      */   }
/*      */ 
/*      */   public void putAll(Map<? extends K, ? extends V> m)
/*      */   {
/* 4159 */     for (Map.Entry e : m.entrySet())
/* 4160 */       put(e.getKey(), e.getValue());
/*      */   }
/*      */ 
/*      */   public V remove(@Nullable Object key)
/*      */   {
/* 4166 */     if (key == null) {
/* 4167 */       return null;
/*      */     }
/* 4169 */     int hash = hash(key);
/* 4170 */     return segmentFor(hash).remove(key, hash);
/*      */   }
/*      */ 
/*      */   public boolean remove(@Nullable Object key, @Nullable Object value)
/*      */   {
/* 4175 */     if ((key == null) || (value == null)) {
/* 4176 */       return false;
/*      */     }
/* 4178 */     int hash = hash(key);
/* 4179 */     return segmentFor(hash).remove(key, hash, value);
/*      */   }
/*      */ 
/*      */   public boolean replace(K key, @Nullable V oldValue, V newValue)
/*      */   {
/* 4184 */     Preconditions.checkNotNull(key);
/* 4185 */     Preconditions.checkNotNull(newValue);
/* 4186 */     if (oldValue == null) {
/* 4187 */       return false;
/*      */     }
/* 4189 */     int hash = hash(key);
/* 4190 */     return segmentFor(hash).replace(key, hash, oldValue, newValue);
/*      */   }
/*      */ 
/*      */   public V replace(K key, V value)
/*      */   {
/* 4195 */     Preconditions.checkNotNull(key);
/* 4196 */     Preconditions.checkNotNull(value);
/* 4197 */     int hash = hash(key);
/* 4198 */     return segmentFor(hash).replace(key, hash, value);
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/* 4203 */     for (Segment segment : this.segments)
/* 4204 */       segment.clear();
/*      */   }
/*      */ 
/*      */   void invalidateAll(Iterable<?> keys)
/*      */   {
/* 4210 */     for (Iterator i$ = keys.iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 4211 */       remove(key);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Set<K> keySet()
/*      */   {
/* 4220 */     Set ks = this.keySet;
/* 4221 */     return this.keySet = new KeySet(this);
/*      */   }
/*      */ 
/*      */   public Collection<V> values()
/*      */   {
/* 4229 */     Collection vs = this.values;
/* 4230 */     return this.values = new Values(this);
/*      */   }
/*      */ 
/*      */   @GwtIncompatible("Not supported.")
/*      */   public Set<Map.Entry<K, V>> entrySet()
/*      */   {
/* 4239 */     Set es = this.entrySet;
/* 4240 */     return this.entrySet = new EntrySet(this);
/*      */   }
/*      */ 
/*      */   static class LocalLoadingCache<K, V> extends LocalCache.LocalManualCache<K, V>
/*      */     implements LoadingCache<K, V>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */     LocalLoadingCache(CacheBuilder<? super K, ? super V> builder, CacheLoader<? super K, V> loader)
/*      */     {
/* 4814 */       super(null);
/*      */     }
/*      */ 
/*      */     public V get(K key)
/*      */       throws ExecutionException
/*      */     {
/* 4821 */       return this.localCache.getOrLoad(key);
/*      */     }
/*      */ 
/*      */     public V getUnchecked(K key)
/*      */     {
/*      */       try {
/* 4827 */         return get(key);
/*      */       } catch (ExecutionException e) {
/* 4829 */         throw new UncheckedExecutionException(e.getCause());
/*      */       }
/*      */     }
/*      */ 
/*      */     public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException
/*      */     {
/* 4835 */       return this.localCache.getAll(keys);
/*      */     }
/*      */ 
/*      */     public void refresh(K key)
/*      */     {
/* 4840 */       this.localCache.refresh(key);
/*      */     }
/*      */ 
/*      */     public final V apply(K key)
/*      */     {
/* 4845 */       return getUnchecked(key);
/*      */     }
/*      */ 
/*      */     Object writeReplace()
/*      */     {
/* 4854 */       return new LocalCache.LoadingSerializationProxy(this.localCache);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class LocalManualCache<K, V>
/*      */     implements Cache<K, V>, Serializable
/*      */   {
/*      */     final LocalCache<K, V> localCache;
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */     LocalManualCache(CacheBuilder<? super K, ? super V> builder)
/*      */     {
/* 4718 */       this(new LocalCache(builder, null));
/*      */     }
/*      */ 
/*      */     private LocalManualCache(LocalCache<K, V> localCache) {
/* 4722 */       this.localCache = localCache;
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     public V getIfPresent(Object key)
/*      */     {
/* 4730 */       return this.localCache.getIfPresent(key);
/*      */     }
/*      */ 
/*      */     public V get(K key, final Callable<? extends V> valueLoader) throws ExecutionException
/*      */     {
/* 4735 */       Preconditions.checkNotNull(valueLoader);
/* 4736 */       return this.localCache.get(key, new CacheLoader()
/*      */       {
/*      */         public V load(Object key) throws Exception {
/* 4739 */           return valueLoader.call();
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*      */     public ImmutableMap<K, V> getAllPresent(Iterable<?> keys)
/*      */     {
/* 4746 */       return this.localCache.getAllPresent(keys);
/*      */     }
/*      */ 
/*      */     public void put(K key, V value)
/*      */     {
/* 4751 */       this.localCache.put(key, value);
/*      */     }
/*      */ 
/*      */     public void putAll(Map<? extends K, ? extends V> m)
/*      */     {
/* 4756 */       this.localCache.putAll(m);
/*      */     }
/*      */ 
/*      */     public void invalidate(Object key)
/*      */     {
/* 4761 */       Preconditions.checkNotNull(key);
/* 4762 */       this.localCache.remove(key);
/*      */     }
/*      */ 
/*      */     public void invalidateAll(Iterable<?> keys)
/*      */     {
/* 4767 */       this.localCache.invalidateAll(keys);
/*      */     }
/*      */ 
/*      */     public void invalidateAll()
/*      */     {
/* 4772 */       this.localCache.clear();
/*      */     }
/*      */ 
/*      */     public long size()
/*      */     {
/* 4777 */       return this.localCache.longSize();
/*      */     }
/*      */ 
/*      */     public ConcurrentMap<K, V> asMap()
/*      */     {
/* 4782 */       return this.localCache;
/*      */     }
/*      */ 
/*      */     public CacheStats stats()
/*      */     {
/* 4787 */       AbstractCache.SimpleStatsCounter aggregator = new AbstractCache.SimpleStatsCounter();
/* 4788 */       aggregator.incrementBy(this.localCache.globalStatsCounter);
/* 4789 */       for (LocalCache.Segment segment : this.localCache.segments) {
/* 4790 */         aggregator.incrementBy(segment.statsCounter);
/*      */       }
/* 4792 */       return aggregator.snapshot();
/*      */     }
/*      */ 
/*      */     public void cleanUp()
/*      */     {
/* 4797 */       this.localCache.cleanUp();
/*      */     }
/*      */ 
/*      */     Object writeReplace()
/*      */     {
/* 4805 */       return new LocalCache.ManualSerializationProxy(this.localCache);
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class LoadingSerializationProxy<K, V> extends LocalCache.ManualSerializationProxy<K, V>
/*      */     implements LoadingCache<K, V>, Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */     transient LoadingCache<K, V> autoDelegate;
/*      */ 
/*      */     LoadingSerializationProxy(LocalCache<K, V> cache)
/*      */     {
/* 4675 */       super();
/*      */     }
/*      */ 
/*      */     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 4679 */       in.defaultReadObject();
/* 4680 */       CacheBuilder builder = recreateCacheBuilder();
/* 4681 */       this.autoDelegate = builder.build(this.loader);
/*      */     }
/*      */ 
/*      */     public V get(K key) throws ExecutionException
/*      */     {
/* 4686 */       return this.autoDelegate.get(key);
/*      */     }
/*      */ 
/*      */     public V getUnchecked(K key)
/*      */     {
/* 4691 */       return this.autoDelegate.getUnchecked(key);
/*      */     }
/*      */ 
/*      */     public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException
/*      */     {
/* 4696 */       return this.autoDelegate.getAll(keys);
/*      */     }
/*      */ 
/*      */     public final V apply(K key)
/*      */     {
/* 4701 */       return this.autoDelegate.apply(key);
/*      */     }
/*      */ 
/*      */     public void refresh(K key)
/*      */     {
/* 4706 */       this.autoDelegate.refresh(key);
/*      */     }
/*      */ 
/*      */     private Object readResolve() {
/* 4710 */       return this.autoDelegate;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class ManualSerializationProxy<K, V> extends ForwardingCache<K, V>
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */     final LocalCache.Strength keyStrength;
/*      */     final LocalCache.Strength valueStrength;
/*      */     final Equivalence<Object> keyEquivalence;
/*      */     final Equivalence<Object> valueEquivalence;
/*      */     final long expireAfterWriteNanos;
/*      */     final long expireAfterAccessNanos;
/*      */     final long maxWeight;
/*      */     final Weigher<K, V> weigher;
/*      */     final int concurrencyLevel;
/*      */     final RemovalListener<? super K, ? super V> removalListener;
/*      */     final Ticker ticker;
/*      */     final CacheLoader<? super K, V> loader;
/*      */     transient Cache<K, V> delegate;
/*      */ 
/*      */     ManualSerializationProxy(LocalCache<K, V> cache)
/*      */     {
/* 4576 */       this(cache.keyStrength, cache.valueStrength, cache.keyEquivalence, cache.valueEquivalence, cache.expireAfterWriteNanos, cache.expireAfterAccessNanos, cache.maxWeight, cache.weigher, cache.concurrencyLevel, cache.removalListener, cache.ticker, cache.defaultLoader);
/*      */     }
/*      */ 
/*      */     private ManualSerializationProxy(LocalCache.Strength keyStrength, LocalCache.Strength valueStrength, Equivalence<Object> keyEquivalence, Equivalence<Object> valueEquivalence, long expireAfterWriteNanos, long expireAfterAccessNanos, long maxWeight, Weigher<K, V> weigher, int concurrencyLevel, RemovalListener<? super K, ? super V> removalListener, Ticker ticker, CacheLoader<? super K, V> loader)
/*      */     {
/* 4598 */       this.keyStrength = keyStrength;
/* 4599 */       this.valueStrength = valueStrength;
/* 4600 */       this.keyEquivalence = keyEquivalence;
/* 4601 */       this.valueEquivalence = valueEquivalence;
/* 4602 */       this.expireAfterWriteNanos = expireAfterWriteNanos;
/* 4603 */       this.expireAfterAccessNanos = expireAfterAccessNanos;
/* 4604 */       this.maxWeight = maxWeight;
/* 4605 */       this.weigher = weigher;
/* 4606 */       this.concurrencyLevel = concurrencyLevel;
/* 4607 */       this.removalListener = removalListener;
/* 4608 */       this.ticker = ((ticker == Ticker.systemTicker()) || (ticker == CacheBuilder.NULL_TICKER) ? null : ticker);
/*      */ 
/* 4610 */       this.loader = loader;
/*      */     }
/*      */ 
/*      */     CacheBuilder<K, V> recreateCacheBuilder() {
/* 4614 */       CacheBuilder builder = CacheBuilder.newBuilder().setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).valueEquivalence(this.valueEquivalence).concurrencyLevel(this.concurrencyLevel).removalListener(this.removalListener);
/*      */ 
/* 4621 */       builder.strictParsing = false;
/* 4622 */       if (this.expireAfterWriteNanos > 0L) {
/* 4623 */         builder.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
/*      */       }
/* 4625 */       if (this.expireAfterAccessNanos > 0L) {
/* 4626 */         builder.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
/*      */       }
/* 4628 */       if (this.weigher != CacheBuilder.OneWeigher.INSTANCE) {
/* 4629 */         builder.weigher(this.weigher);
/* 4630 */         if (this.maxWeight != -1L) {
/* 4631 */           builder.maximumWeight(this.maxWeight);
/*      */         }
/*      */       }
/* 4634 */       else if (this.maxWeight != -1L) {
/* 4635 */         builder.maximumSize(this.maxWeight);
/*      */       }
/*      */ 
/* 4638 */       if (this.ticker != null) {
/* 4639 */         builder.ticker(this.ticker);
/*      */       }
/* 4641 */       return builder;
/*      */     }
/*      */ 
/*      */     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 4645 */       in.defaultReadObject();
/* 4646 */       CacheBuilder builder = recreateCacheBuilder();
/* 4647 */       this.delegate = builder.build();
/*      */     }
/*      */ 
/*      */     private Object readResolve() {
/* 4651 */       return this.delegate;
/*      */     }
/*      */ 
/*      */     protected Cache<K, V> delegate()
/*      */     {
/* 4656 */       return this.delegate;
/*      */     }
/*      */   }
/*      */ 
/*      */   final class EntrySet extends LocalCache<K, V>.AbstractCacheSet<Map.Entry<K, V>>
/*      */   {
/*      */     EntrySet()
/*      */     {
/* 4512 */       super(map);
/*      */     }
/*      */ 
/*      */     public Iterator<Map.Entry<K, V>> iterator()
/*      */     {
/* 4517 */       return new LocalCache.EntryIterator(LocalCache.this);
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 4522 */       if (!(o instanceof Map.Entry)) {
/* 4523 */         return false;
/*      */       }
/* 4525 */       Map.Entry e = (Map.Entry)o;
/* 4526 */       Object key = e.getKey();
/* 4527 */       if (key == null) {
/* 4528 */         return false;
/*      */       }
/* 4530 */       Object v = LocalCache.this.get(key);
/*      */ 
/* 4532 */       return (v != null) && (LocalCache.this.valueEquivalence.equivalent(e.getValue(), v));
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 4537 */       if (!(o instanceof Map.Entry)) {
/* 4538 */         return false;
/*      */       }
/* 4540 */       Map.Entry e = (Map.Entry)o;
/* 4541 */       Object key = e.getKey();
/* 4542 */       return (key != null) && (LocalCache.this.remove(key, e.getValue()));
/*      */     }
/*      */   }
/*      */ 
/*      */   final class Values extends AbstractCollection<V>
/*      */   {
/*      */     private final ConcurrentMap<?, ?> map;
/*      */ 
/*      */     Values()
/*      */     {
/* 4483 */       this.map = map;
/*      */     }
/*      */ 
/*      */     public int size() {
/* 4487 */       return this.map.size();
/*      */     }
/*      */ 
/*      */     public boolean isEmpty() {
/* 4491 */       return this.map.isEmpty();
/*      */     }
/*      */ 
/*      */     public void clear() {
/* 4495 */       this.map.clear();
/*      */     }
/*      */ 
/*      */     public Iterator<V> iterator()
/*      */     {
/* 4500 */       return new LocalCache.ValueIterator(LocalCache.this);
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 4505 */       return this.map.containsValue(o);
/*      */     }
/*      */   }
/*      */ 
/*      */   final class KeySet extends LocalCache<K, V>.AbstractCacheSet<K>
/*      */   {
/*      */     KeySet()
/*      */     {
/* 4460 */       super(map);
/*      */     }
/*      */ 
/*      */     public Iterator<K> iterator()
/*      */     {
/* 4465 */       return new LocalCache.KeyIterator(LocalCache.this);
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 4470 */       return this.map.containsKey(o);
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 4475 */       return this.map.remove(o) != null;
/*      */     }
/*      */   }
/*      */ 
/*      */   abstract class AbstractCacheSet<T> extends AbstractSet<T>
/*      */   {
/*      */     final ConcurrentMap<?, ?> map;
/*      */ 
/*      */     AbstractCacheSet()
/*      */     {
/* 4438 */       this.map = map;
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 4443 */       return this.map.size();
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 4448 */       return this.map.isEmpty();
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 4453 */       this.map.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class EntryIterator extends LocalCache<K, V>.HashIterator<Map.Entry<K, V>>
/*      */   {
/*      */     EntryIterator()
/*      */     {
/* 4426 */       super();
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> next() {
/* 4430 */       return nextEntry();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class WriteThroughEntry
/*      */     implements Map.Entry<K, V>
/*      */   {
/*      */     final K key;
/*      */     V value;
/*      */ 
/*      */     WriteThroughEntry(V key)
/*      */     {
/* 4383 */       this.key = key;
/* 4384 */       this.value = value;
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/* 4389 */       return this.key;
/*      */     }
/*      */ 
/*      */     public V getValue()
/*      */     {
/* 4394 */       return this.value;
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object)
/*      */     {
/* 4400 */       if ((object instanceof Map.Entry)) {
/* 4401 */         Map.Entry that = (Map.Entry)object;
/* 4402 */         return (this.key.equals(that.getKey())) && (this.value.equals(that.getValue()));
/*      */       }
/* 4404 */       return false;
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 4410 */       return this.key.hashCode() ^ this.value.hashCode();
/*      */     }
/*      */ 
/*      */     public V setValue(V newValue)
/*      */     {
/* 4415 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/* 4422 */       return getKey() + "=" + getValue();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class ValueIterator extends LocalCache<K, V>.HashIterator<V>
/*      */   {
/*      */     ValueIterator()
/*      */     {
/* 4366 */       super();
/*      */     }
/*      */ 
/*      */     public V next() {
/* 4370 */       return nextEntry().getValue();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class KeyIterator extends LocalCache<K, V>.HashIterator<K>
/*      */   {
/*      */     KeyIterator()
/*      */     {
/* 4358 */       super();
/*      */     }
/*      */ 
/*      */     public K next() {
/* 4362 */       return nextEntry().getKey();
/*      */     }
/*      */   }
/*      */ 
/*      */   abstract class HashIterator<T>
/*      */     implements Iterator<T>
/*      */   {
/*      */     int nextSegmentIndex;
/*      */     int nextTableIndex;
/*      */     LocalCache.Segment<K, V> currentSegment;
/*      */     AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> currentTable;
/*      */     LocalCache.ReferenceEntry<K, V> nextEntry;
/*      */     LocalCache<K, V>.WriteThroughEntry nextExternal;
/*      */     LocalCache<K, V>.WriteThroughEntry lastReturned;
/*      */ 
/*      */     HashIterator()
/*      */     {
/* 4256 */       this.nextSegmentIndex = (LocalCache.this.segments.length - 1);
/* 4257 */       this.nextTableIndex = -1;
/* 4258 */       advance();
/*      */     }
/*      */ 
/*      */     public abstract T next();
/*      */ 
/*      */     final void advance()
/*      */     {
/* 4265 */       this.nextExternal = null;
/*      */ 
/* 4267 */       if (nextInChain()) {
/* 4268 */         return;
/*      */       }
/*      */ 
/* 4271 */       if (nextInTable()) {
/* 4272 */         return;
/*      */       }
/*      */ 
/* 4275 */       while (this.nextSegmentIndex >= 0) {
/* 4276 */         this.currentSegment = LocalCache.this.segments[(this.nextSegmentIndex--)];
/* 4277 */         if (this.currentSegment.count != 0) {
/* 4278 */           this.currentTable = this.currentSegment.table;
/* 4279 */           this.nextTableIndex = (this.currentTable.length() - 1);
/* 4280 */           if (nextInTable());
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean nextInChain()
/*      */     {
/* 4291 */       if (this.nextEntry != null) {
/* 4292 */         for (this.nextEntry = this.nextEntry.getNext(); this.nextEntry != null; this.nextEntry = this.nextEntry.getNext()) {
/* 4293 */           if (advanceTo(this.nextEntry)) {
/* 4294 */             return true;
/*      */           }
/*      */         }
/*      */       }
/* 4298 */       return false;
/*      */     }
/*      */ 
/*      */     boolean nextInTable()
/*      */     {
/* 4305 */       while (this.nextTableIndex >= 0) {
/* 4306 */         if (((this.nextEntry = (LocalCache.ReferenceEntry)this.currentTable.get(this.nextTableIndex--)) != null) && (
/* 4307 */           (advanceTo(this.nextEntry)) || (nextInChain()))) {
/* 4308 */           return true;
/*      */         }
/*      */       }
/*      */ 
/* 4312 */       return false;
/*      */     }
/*      */ 
/*      */     boolean advanceTo(LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/*      */       try
/*      */       {
/* 4321 */         long now = LocalCache.this.ticker.read();
/* 4322 */         Object key = entry.getKey();
/* 4323 */         Object value = LocalCache.this.getLiveValue(entry, now);
/*      */         boolean bool;
/* 4324 */         if (value != null) {
/* 4325 */           this.nextExternal = new LocalCache.WriteThroughEntry(LocalCache.this, key, value);
/* 4326 */           return true;
/*      */         }
/*      */ 
/* 4329 */         return false;
/*      */       }
/*      */       finally {
/* 4332 */         this.currentSegment.postReadCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean hasNext()
/*      */     {
/* 4338 */       return this.nextExternal != null;
/*      */     }
/*      */ 
/*      */     LocalCache<K, V>.WriteThroughEntry nextEntry() {
/* 4342 */       if (this.nextExternal == null) {
/* 4343 */         throw new NoSuchElementException();
/*      */       }
/* 4345 */       this.lastReturned = this.nextExternal;
/* 4346 */       advance();
/* 4347 */       return this.lastReturned;
/*      */     }
/*      */ 
/*      */     public void remove()
/*      */     {
/* 4352 */       Preconditions.checkState(this.lastReturned != null);
/* 4353 */       LocalCache.this.remove(this.lastReturned.getKey());
/* 4354 */       this.lastReturned = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class AccessQueue<K, V> extends AbstractQueue<LocalCache.ReferenceEntry<K, V>>
/*      */   {
/* 3729 */     final LocalCache.ReferenceEntry<K, V> head = new LocalCache.AbstractReferenceEntry()
/*      */     {
/* 3739 */       LocalCache.ReferenceEntry<K, V> nextAccess = this;
/*      */ 
/* 3751 */       LocalCache.ReferenceEntry<K, V> previousAccess = this;
/*      */ 
/*      */       public long getAccessTime()
/*      */       {
/* 3733 */         return 9223372036854775807L;
/*      */       }
/*      */ 
/*      */       public void setAccessTime(long time)
/*      */       {
/*      */       }
/*      */ 
/*      */       public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */       {
/* 3743 */         return this.nextAccess;
/*      */       }
/*      */ 
/*      */       public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */       {
/* 3748 */         this.nextAccess = next;
/*      */       }
/*      */ 
/*      */       public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */       {
/* 3755 */         return this.previousAccess;
/*      */       }
/*      */ 
/*      */       public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */       {
/* 3760 */         this.previousAccess = previous;
/*      */       }
/* 3729 */     };
/*      */ 
/*      */     public boolean offer(LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 3769 */       LocalCache.connectAccessOrder(entry.getPreviousInAccessQueue(), entry.getNextInAccessQueue());
/*      */ 
/* 3772 */       LocalCache.connectAccessOrder(this.head.getPreviousInAccessQueue(), entry);
/* 3773 */       LocalCache.connectAccessOrder(entry, this.head);
/*      */ 
/* 3775 */       return true;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> peek()
/*      */     {
/* 3780 */       LocalCache.ReferenceEntry next = this.head.getNextInAccessQueue();
/* 3781 */       return next == this.head ? null : next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> poll()
/*      */     {
/* 3786 */       LocalCache.ReferenceEntry next = this.head.getNextInAccessQueue();
/* 3787 */       if (next == this.head) {
/* 3788 */         return null;
/*      */       }
/*      */ 
/* 3791 */       remove(next);
/* 3792 */       return next;
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 3798 */       LocalCache.ReferenceEntry e = (LocalCache.ReferenceEntry)o;
/* 3799 */       LocalCache.ReferenceEntry previous = e.getPreviousInAccessQueue();
/* 3800 */       LocalCache.ReferenceEntry next = e.getNextInAccessQueue();
/* 3801 */       LocalCache.connectAccessOrder(previous, next);
/* 3802 */       LocalCache.nullifyAccessOrder(e);
/*      */ 
/* 3804 */       return next != LocalCache.NullEntry.INSTANCE;
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 3810 */       LocalCache.ReferenceEntry e = (LocalCache.ReferenceEntry)o;
/* 3811 */       return e.getNextInAccessQueue() != LocalCache.NullEntry.INSTANCE;
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 3816 */       return this.head.getNextInAccessQueue() == this.head;
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 3821 */       int size = 0;
/* 3822 */       for (LocalCache.ReferenceEntry e = this.head.getNextInAccessQueue(); e != this.head; 
/* 3823 */         e = e.getNextInAccessQueue()) {
/* 3824 */         size++;
/*      */       }
/* 3826 */       return size;
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 3831 */       LocalCache.ReferenceEntry e = this.head.getNextInAccessQueue();
/* 3832 */       while (e != this.head) {
/* 3833 */         LocalCache.ReferenceEntry next = e.getNextInAccessQueue();
/* 3834 */         LocalCache.nullifyAccessOrder(e);
/* 3835 */         e = next;
/*      */       }
/*      */ 
/* 3838 */       this.head.setNextInAccessQueue(this.head);
/* 3839 */       this.head.setPreviousInAccessQueue(this.head);
/*      */     }
/*      */ 
/*      */     public Iterator<LocalCache.ReferenceEntry<K, V>> iterator()
/*      */     {
/* 3844 */       return new AbstractSequentialIterator(peek())
/*      */       {
/*      */         protected LocalCache.ReferenceEntry<K, V> computeNext(LocalCache.ReferenceEntry<K, V> previous) {
/* 3847 */           LocalCache.ReferenceEntry next = previous.getNextInAccessQueue();
/* 3848 */           return next == LocalCache.AccessQueue.this.head ? null : next;
/*      */         }
/*      */       };
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WriteQueue<K, V> extends AbstractQueue<LocalCache.ReferenceEntry<K, V>>
/*      */   {
/* 3592 */     final LocalCache.ReferenceEntry<K, V> head = new LocalCache.AbstractReferenceEntry()
/*      */     {
/* 3602 */       LocalCache.ReferenceEntry<K, V> nextWrite = this;
/*      */ 
/* 3614 */       LocalCache.ReferenceEntry<K, V> previousWrite = this;
/*      */ 
/*      */       public long getWriteTime()
/*      */       {
/* 3596 */         return 9223372036854775807L;
/*      */       }
/*      */ 
/*      */       public void setWriteTime(long time)
/*      */       {
/*      */       }
/*      */ 
/*      */       public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */       {
/* 3606 */         return this.nextWrite;
/*      */       }
/*      */ 
/*      */       public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */       {
/* 3611 */         this.nextWrite = next;
/*      */       }
/*      */ 
/*      */       public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */       {
/* 3618 */         return this.previousWrite;
/*      */       }
/*      */ 
/*      */       public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */       {
/* 3623 */         this.previousWrite = previous;
/*      */       }
/* 3592 */     };
/*      */ 
/*      */     public boolean offer(LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 3632 */       LocalCache.connectWriteOrder(entry.getPreviousInWriteQueue(), entry.getNextInWriteQueue());
/*      */ 
/* 3635 */       LocalCache.connectWriteOrder(this.head.getPreviousInWriteQueue(), entry);
/* 3636 */       LocalCache.connectWriteOrder(entry, this.head);
/*      */ 
/* 3638 */       return true;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> peek()
/*      */     {
/* 3643 */       LocalCache.ReferenceEntry next = this.head.getNextInWriteQueue();
/* 3644 */       return next == this.head ? null : next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> poll()
/*      */     {
/* 3649 */       LocalCache.ReferenceEntry next = this.head.getNextInWriteQueue();
/* 3650 */       if (next == this.head) {
/* 3651 */         return null;
/*      */       }
/*      */ 
/* 3654 */       remove(next);
/* 3655 */       return next;
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 3661 */       LocalCache.ReferenceEntry e = (LocalCache.ReferenceEntry)o;
/* 3662 */       LocalCache.ReferenceEntry previous = e.getPreviousInWriteQueue();
/* 3663 */       LocalCache.ReferenceEntry next = e.getNextInWriteQueue();
/* 3664 */       LocalCache.connectWriteOrder(previous, next);
/* 3665 */       LocalCache.nullifyWriteOrder(e);
/*      */ 
/* 3667 */       return next != LocalCache.NullEntry.INSTANCE;
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 3673 */       LocalCache.ReferenceEntry e = (LocalCache.ReferenceEntry)o;
/* 3674 */       return e.getNextInWriteQueue() != LocalCache.NullEntry.INSTANCE;
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 3679 */       return this.head.getNextInWriteQueue() == this.head;
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 3684 */       int size = 0;
/* 3685 */       for (LocalCache.ReferenceEntry e = this.head.getNextInWriteQueue(); e != this.head; 
/* 3686 */         e = e.getNextInWriteQueue()) {
/* 3687 */         size++;
/*      */       }
/* 3689 */       return size;
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 3694 */       LocalCache.ReferenceEntry e = this.head.getNextInWriteQueue();
/* 3695 */       while (e != this.head) {
/* 3696 */         LocalCache.ReferenceEntry next = e.getNextInWriteQueue();
/* 3697 */         LocalCache.nullifyWriteOrder(e);
/* 3698 */         e = next;
/*      */       }
/*      */ 
/* 3701 */       this.head.setNextInWriteQueue(this.head);
/* 3702 */       this.head.setPreviousInWriteQueue(this.head);
/*      */     }
/*      */ 
/*      */     public Iterator<LocalCache.ReferenceEntry<K, V>> iterator()
/*      */     {
/* 3707 */       return new AbstractSequentialIterator(peek())
/*      */       {
/*      */         protected LocalCache.ReferenceEntry<K, V> computeNext(LocalCache.ReferenceEntry<K, V> previous) {
/* 3710 */           LocalCache.ReferenceEntry next = previous.getNextInWriteQueue();
/* 3711 */           return next == LocalCache.WriteQueue.this.head ? null : next;
/*      */         }
/*      */       };
/*      */     }
/*      */   }
/*      */ 
/*      */   static class LoadingValueReference<K, V>
/*      */     implements LocalCache.ValueReference<K, V>
/*      */   {
/*      */     volatile LocalCache.ValueReference<K, V> oldValue;
/* 3467 */     final SettableFuture<V> futureValue = SettableFuture.create();
/* 3468 */     final Stopwatch stopwatch = Stopwatch.createUnstarted();
/*      */ 
/*      */     public LoadingValueReference() {
/* 3471 */       this(LocalCache.unset());
/*      */     }
/*      */ 
/*      */     public LoadingValueReference(LocalCache.ValueReference<K, V> oldValue) {
/* 3475 */       this.oldValue = oldValue;
/*      */     }
/*      */ 
/*      */     public boolean isLoading()
/*      */     {
/* 3480 */       return true;
/*      */     }
/*      */ 
/*      */     public boolean isActive()
/*      */     {
/* 3485 */       return this.oldValue.isActive();
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 3490 */       return this.oldValue.getWeight();
/*      */     }
/*      */ 
/*      */     public boolean set(@Nullable V newValue) {
/* 3494 */       return this.futureValue.set(newValue);
/*      */     }
/*      */ 
/*      */     public boolean setException(Throwable t) {
/* 3498 */       return this.futureValue.setException(t);
/*      */     }
/*      */ 
/*      */     private ListenableFuture<V> fullyFailedFuture(Throwable t) {
/* 3502 */       return Futures.immediateFailedFuture(t);
/*      */     }
/*      */ 
/*      */     public void notifyNewValue(@Nullable V newValue)
/*      */     {
/* 3507 */       if (newValue != null)
/*      */       {
/* 3510 */         set(newValue);
/*      */       }
/*      */       else
/* 3513 */         this.oldValue = LocalCache.unset();
/*      */     }
/*      */ 
/*      */     public ListenableFuture<V> loadFuture(K key, CacheLoader<? super K, V> loader)
/*      */     {
/* 3520 */       this.stopwatch.start();
/* 3521 */       Object previousValue = this.oldValue.get();
/*      */       try {
/* 3523 */         if (previousValue == null) {
/* 3524 */           Object newValue = loader.load(key);
/* 3525 */           return set(newValue) ? this.futureValue : Futures.immediateFuture(newValue);
/*      */         }
/* 3527 */         ListenableFuture newValue = loader.reload(key, previousValue);
/* 3528 */         if (newValue == null) {
/* 3529 */           return Futures.immediateFuture(null);
/*      */         }
/*      */ 
/* 3533 */         return Futures.transform(newValue, new Function()
/*      */         {
/*      */           public V apply(V newValue) {
/* 3536 */             LocalCache.LoadingValueReference.this.set(newValue);
/* 3537 */             return newValue;
/*      */           } } );
/*      */       }
/*      */       catch (Throwable t) {
/* 3541 */         if ((t instanceof InterruptedException)) {
/* 3542 */           Thread.currentThread().interrupt();
/*      */         }
/* 3544 */         return setException(t) ? this.futureValue : fullyFailedFuture(t);
/*      */       }
/*      */     }
/*      */ 
/*      */     public long elapsedNanos() {
/* 3549 */       return this.stopwatch.elapsed(TimeUnit.NANOSECONDS);
/*      */     }
/*      */ 
/*      */     public V waitForValue() throws ExecutionException
/*      */     {
/* 3554 */       return Uninterruptibles.getUninterruptibly(this.futureValue);
/*      */     }
/*      */ 
/*      */     public V get()
/*      */     {
/* 3559 */       return this.oldValue.get();
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> getOldValue() {
/* 3563 */       return this.oldValue;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getEntry()
/*      */     {
/* 3568 */       return null;
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, @Nullable V value, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 3574 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class Segment<K, V> extends ReentrantLock
/*      */   {
/*      */     final LocalCache<K, V> map;
/*      */     volatile int count;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     int totalWeight;
/*      */     int modCount;
/*      */     int threshold;
/*      */     volatile AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table;
/*      */     final long maxSegmentWeight;
/*      */     final ReferenceQueue<K> keyReferenceQueue;
/*      */     final ReferenceQueue<V> valueReferenceQueue;
/*      */     final Queue<LocalCache.ReferenceEntry<K, V>> recencyQueue;
/* 2068 */     final AtomicInteger readCount = new AtomicInteger();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     final Queue<LocalCache.ReferenceEntry<K, V>> writeQueue;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     final Queue<LocalCache.ReferenceEntry<K, V>> accessQueue;
/*      */     final AbstractCache.StatsCounter statsCounter;
/*      */ 
/*      */     Segment(LocalCache<K, V> map, int initialCapacity, long maxSegmentWeight, AbstractCache.StatsCounter statsCounter)
/*      */     {
/* 2089 */       this.map = map;
/* 2090 */       this.maxSegmentWeight = maxSegmentWeight;
/* 2091 */       this.statsCounter = ((AbstractCache.StatsCounter)Preconditions.checkNotNull(statsCounter));
/* 2092 */       initTable(newEntryArray(initialCapacity));
/*      */ 
/* 2094 */       this.keyReferenceQueue = (map.usesKeyReferences() ? new ReferenceQueue() : null);
/*      */ 
/* 2097 */       this.valueReferenceQueue = (map.usesValueReferences() ? new ReferenceQueue() : null);
/*      */ 
/* 2100 */       this.recencyQueue = (map.usesAccessQueue() ? new ConcurrentLinkedQueue() : LocalCache.discardingQueue());
/*      */ 
/* 2104 */       this.writeQueue = (map.usesWriteQueue() ? new LocalCache.WriteQueue() : LocalCache.discardingQueue());
/*      */ 
/* 2108 */       this.accessQueue = (map.usesAccessQueue() ? new LocalCache.AccessQueue() : LocalCache.discardingQueue());
/*      */     }
/*      */ 
/*      */     AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> newEntryArray(int size)
/*      */     {
/* 2114 */       return new AtomicReferenceArray(size);
/*      */     }
/*      */ 
/*      */     void initTable(AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> newTable) {
/* 2118 */       this.threshold = (newTable.length() * 3 / 4);
/* 2119 */       if ((!this.map.customWeigher()) && (this.threshold == this.maxSegmentWeight))
/*      */       {
/* 2121 */         this.threshold += 1;
/*      */       }
/* 2123 */       this.table = newTable;
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     LocalCache.ReferenceEntry<K, V> newEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 2128 */       return this.map.entryFactory.newEntry(this, Preconditions.checkNotNull(key), hash, next);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.ReferenceEntry<K, V> original, LocalCache.ReferenceEntry<K, V> newNext)
/*      */     {
/* 2137 */       if (original.getKey() == null)
/*      */       {
/* 2139 */         return null;
/*      */       }
/*      */ 
/* 2142 */       LocalCache.ValueReference valueReference = original.getValueReference();
/* 2143 */       Object value = valueReference.get();
/* 2144 */       if ((value == null) && (valueReference.isActive()))
/*      */       {
/* 2146 */         return null;
/*      */       }
/*      */ 
/* 2149 */       LocalCache.ReferenceEntry newEntry = this.map.entryFactory.copyEntry(this, original, newNext);
/* 2150 */       newEntry.setValueReference(valueReference.copyFor(this.valueReferenceQueue, value, newEntry));
/* 2151 */       return newEntry;
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void setValue(LocalCache.ReferenceEntry<K, V> entry, K key, V value, long now)
/*      */     {
/* 2159 */       LocalCache.ValueReference previous = entry.getValueReference();
/* 2160 */       int weight = this.map.weigher.weigh(key, value);
/* 2161 */       Preconditions.checkState(weight >= 0, "Weights must be non-negative");
/*      */ 
/* 2163 */       LocalCache.ValueReference valueReference = this.map.valueStrength.referenceValue(this, entry, value, weight);
/*      */ 
/* 2165 */       entry.setValueReference(valueReference);
/* 2166 */       recordWrite(entry, weight, now);
/* 2167 */       previous.notifyNewValue(value);
/*      */     }
/*      */ 
/*      */     V get(K key, int hash, CacheLoader<? super K, V> loader)
/*      */       throws ExecutionException
/*      */     {
/* 2173 */       Preconditions.checkNotNull(key);
/* 2174 */       Preconditions.checkNotNull(loader);
/*      */       try
/*      */       {
/*      */         LocalCache.ReferenceEntry e;
/* 2176 */         if (this.count != 0)
/*      */         {
/* 2178 */           e = getEntry(key, hash);
/* 2179 */           if (e != null) {
/* 2180 */             long now = this.map.ticker.read();
/* 2181 */             Object value = getLiveValue(e, now);
/* 2182 */             if (value != null) {
/* 2183 */               recordRead(e, now);
/* 2184 */               this.statsCounter.recordHits(1);
/* 2185 */               return scheduleRefresh(e, key, hash, value, now, loader);
/*      */             }
/* 2187 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 2188 */             if (valueReference.isLoading()) {
/* 2189 */               return waitForLoadingValue(e, key, valueReference);
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2195 */         return lockedGetOrLoad(key, hash, loader);
/*      */       } catch (ExecutionException ee) {
/* 2197 */         Throwable cause = ee.getCause();
/* 2198 */         if ((cause instanceof Error))
/* 2199 */           throw new ExecutionError((Error)cause);
/* 2200 */         if ((cause instanceof RuntimeException)) {
/* 2201 */           throw new UncheckedExecutionException(cause);
/*      */         }
/* 2203 */         throw ee;
/*      */       } finally {
/* 2205 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */     V lockedGetOrLoad(K key, int hash, CacheLoader<? super K, V> loader) throws ExecutionException {
/* 2212 */       LocalCache.ValueReference valueReference = null;
/* 2213 */       LocalCache.LoadingValueReference loadingValueReference = null;
/* 2214 */       boolean createNewEntry = true;
/*      */ 
/* 2216 */       lock();
/*      */       LocalCache.ReferenceEntry e;
/*      */       try { long now = this.map.ticker.read();
/* 2220 */         preWriteCleanup(now);
/*      */ 
/* 2222 */         int newCount = this.count - 1;
/* 2223 */         AtomicReferenceArray table = this.table;
/* 2224 */         int index = hash & table.length() - 1;
/* 2225 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 2227 */         for (e = first; e != null; e = e.getNext()) {
/* 2228 */           Object entryKey = e.getKey();
/* 2229 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2231 */             valueReference = e.getValueReference();
/* 2232 */             if (valueReference.isLoading()) {
/* 2233 */               createNewEntry = false; break;
/*      */             }
/* 2235 */             Object value = valueReference.get();
/* 2236 */             if (value == null) {
/* 2237 */               enqueueNotification(entryKey, hash, valueReference, RemovalCause.COLLECTED);
/* 2238 */             } else if (this.map.isExpired(e, now))
/*      */             {
/* 2241 */               enqueueNotification(entryKey, hash, valueReference, RemovalCause.EXPIRED);
/*      */             } else {
/* 2243 */               recordLockedRead(e, now);
/* 2244 */               this.statsCounter.recordHits(1);
/*      */ 
/* 2246 */               return value;
/*      */             }
/*      */ 
/* 2250 */             this.writeQueue.remove(e);
/* 2251 */             this.accessQueue.remove(e);
/* 2252 */             this.count = newCount;
/*      */ 
/* 2254 */             break;
/*      */           }
/*      */         }
/*      */ 
/* 2258 */         if (createNewEntry) {
/* 2259 */           loadingValueReference = new LocalCache.LoadingValueReference();
/*      */ 
/* 2261 */           if (e == null) {
/* 2262 */             e = newEntry(key, hash, first);
/* 2263 */             e.setValueReference(loadingValueReference);
/* 2264 */             table.set(index, e);
/*      */           } else {
/* 2266 */             e.setValueReference(loadingValueReference);
/*      */           }
/*      */         }
/*      */       } finally {
/* 2270 */         unlock();
/* 2271 */         postWriteCleanup();
/*      */       }
/*      */ 
/* 2274 */       if (createNewEntry)
/*      */       {
/*      */         try
/*      */         {
/* 2279 */           synchronized (e) {
/* 2280 */             return loadSync(key, hash, loadingValueReference, loader);
/*      */           }
/*      */         } finally {
/* 2283 */           this.statsCounter.recordMisses(1);
/*      */         }
/*      */       }
/*      */ 
/* 2287 */       return waitForLoadingValue(e, key, valueReference);
/*      */     }
/*      */ 
/*      */     V waitForLoadingValue(LocalCache.ReferenceEntry<K, V> e, K key, LocalCache.ValueReference<K, V> valueReference)
/*      */       throws ExecutionException
/*      */     {
/* 2293 */       if (!valueReference.isLoading()) {
/* 2294 */         throw new AssertionError();
/*      */       }
/*      */ 
/* 2297 */       Preconditions.checkState(!Thread.holdsLock(e), "Recursive load of: %s", new Object[] { key });
/*      */       try
/*      */       {
/* 2300 */         Object value = valueReference.waitForValue();
/* 2301 */         if (value == null) {
/* 2302 */           throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + key + ".");
/*      */         }
/*      */ 
/* 2305 */         long now = this.map.ticker.read();
/* 2306 */         recordRead(e, now);
/* 2307 */         return value;
/*      */       } finally {
/* 2309 */         this.statsCounter.recordMisses(1);
/*      */       }
/*      */     }
/*      */ 
/*      */     V loadSync(K key, int hash, LocalCache.LoadingValueReference<K, V> loadingValueReference, CacheLoader<? super K, V> loader)
/*      */       throws ExecutionException
/*      */     {
/* 2317 */       ListenableFuture loadingFuture = loadingValueReference.loadFuture(key, loader);
/* 2318 */       return getAndRecordStats(key, hash, loadingValueReference, loadingFuture);
/*      */     }
/*      */ 
/*      */     ListenableFuture<V> loadAsync(final K key, final int hash, final LocalCache.LoadingValueReference<K, V> loadingValueReference, CacheLoader<? super K, V> loader)
/*      */     {
/* 2323 */       final ListenableFuture loadingFuture = loadingValueReference.loadFuture(key, loader);
/* 2324 */       loadingFuture.addListener(new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try {
/* 2329 */             newValue = LocalCache.Segment.this.getAndRecordStats(key, hash, loadingValueReference, loadingFuture);
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*      */             Object newValue;
/* 2331 */             LocalCache.logger.log(Level.WARNING, "Exception thrown during refresh", t);
/* 2332 */             loadingValueReference.setException(t);
/*      */           }
/*      */         }
/*      */       }
/*      */       , LocalCache.sameThreadExecutor);
/*      */ 
/* 2336 */       return loadingFuture;
/*      */     }
/*      */ 
/*      */     V getAndRecordStats(K key, int hash, LocalCache.LoadingValueReference<K, V> loadingValueReference, ListenableFuture<V> newValue)
/*      */       throws ExecutionException
/*      */     {
/* 2344 */       Object value = null;
/*      */       try {
/* 2346 */         value = Uninterruptibles.getUninterruptibly(newValue);
/* 2347 */         if (value == null) {
/* 2348 */           throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + key + ".");
/*      */         }
/* 2350 */         this.statsCounter.recordLoadSuccess(loadingValueReference.elapsedNanos());
/* 2351 */         storeLoadedValue(key, hash, loadingValueReference, value);
/* 2352 */         return value;
/*      */       } finally {
/* 2354 */         if (value == null) {
/* 2355 */           this.statsCounter.recordLoadException(loadingValueReference.elapsedNanos());
/* 2356 */           removeLoadingValue(key, hash, loadingValueReference);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     V scheduleRefresh(LocalCache.ReferenceEntry<K, V> entry, K key, int hash, V oldValue, long now, CacheLoader<? super K, V> loader)
/*      */     {
/* 2363 */       if ((this.map.refreshes()) && (now - entry.getWriteTime() > this.map.refreshNanos) && (!entry.getValueReference().isLoading()))
/*      */       {
/* 2365 */         Object newValue = refresh(key, hash, loader, true);
/* 2366 */         if (newValue != null) {
/* 2367 */           return newValue;
/*      */         }
/*      */       }
/* 2370 */       return oldValue;
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     V refresh(K key, int hash, CacheLoader<? super K, V> loader, boolean checkTime)
/*      */     {
/* 2381 */       LocalCache.LoadingValueReference loadingValueReference = insertLoadingValueReference(key, hash, checkTime);
/*      */ 
/* 2383 */       if (loadingValueReference == null) {
/* 2384 */         return null;
/*      */       }
/*      */ 
/* 2387 */       ListenableFuture result = loadAsync(key, hash, loadingValueReference, loader);
/* 2388 */       if (result.isDone())
/*      */         try {
/* 2390 */           return Uninterruptibles.getUninterruptibly(result);
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*      */         }
/* 2395 */       return null;
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     LocalCache.LoadingValueReference<K, V> insertLoadingValueReference(K key, int hash, boolean checkTime)
/*      */     {
/* 2405 */       LocalCache.ReferenceEntry e = null;
/* 2406 */       lock();
/*      */       try {
/* 2408 */         long now = this.map.ticker.read();
/* 2409 */         preWriteCleanup(now);
/*      */ 
/* 2411 */         AtomicReferenceArray table = this.table;
/* 2412 */         int index = hash & table.length() - 1;
/* 2413 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */         LocalCache.ValueReference valueReference;
/* 2416 */         for (e = first; e != null; e = e.getNext()) {
/* 2417 */           Object entryKey = e.getKey();
/* 2418 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2422 */             valueReference = e.getValueReference();
/* 2423 */             if ((valueReference.isLoading()) || ((checkTime) && (now - e.getWriteTime() < this.map.refreshNanos)))
/*      */             {
/* 2428 */               return null;
/*      */             }
/*      */ 
/* 2432 */             this.modCount += 1;
/* 2433 */             LocalCache.LoadingValueReference loadingValueReference = new LocalCache.LoadingValueReference(valueReference);
/*      */ 
/* 2435 */             e.setValueReference(loadingValueReference);
/* 2436 */             return loadingValueReference;
/*      */           }
/*      */         }
/*      */ 
/* 2440 */         this.modCount += 1;
/* 2441 */         LocalCache.LoadingValueReference loadingValueReference = new LocalCache.LoadingValueReference();
/* 2442 */         e = newEntry(key, hash, first);
/* 2443 */         e.setValueReference(loadingValueReference);
/* 2444 */         table.set(index, e);
/* 2445 */         return loadingValueReference;
/*      */       } finally {
/* 2447 */         unlock();
/* 2448 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     void tryDrainReferenceQueues()
/*      */     {
/* 2458 */       if (tryLock())
/*      */         try {
/* 2460 */           drainReferenceQueues();
/*      */         } finally {
/* 2462 */           unlock();
/*      */         }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainReferenceQueues()
/*      */     {
/* 2473 */       if (this.map.usesKeyReferences()) {
/* 2474 */         drainKeyReferenceQueue();
/*      */       }
/* 2476 */       if (this.map.usesValueReferences())
/* 2477 */         drainValueReferenceQueue();
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainKeyReferenceQueue()
/*      */     {
/* 2484 */       int i = 0;
/*      */       Reference ref;
/* 2489 */       for (; (ref = this.keyReferenceQueue.poll()) != null; 
/* 2489 */         i == 16)
/*      */       {
/* 2487 */         LocalCache.ReferenceEntry entry = (LocalCache.ReferenceEntry)ref;
/* 2488 */         this.map.reclaimKey(entry);
/* 2489 */         i++;
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainValueReferenceQueue()
/*      */     {
/* 2498 */       int i = 0;
/*      */       Reference ref;
/* 2503 */       for (; (ref = this.valueReferenceQueue.poll()) != null; 
/* 2503 */         i == 16)
/*      */       {
/* 2501 */         LocalCache.ValueReference valueReference = (LocalCache.ValueReference)ref;
/* 2502 */         this.map.reclaimValue(valueReference);
/* 2503 */         i++;
/*      */       }
/*      */     }
/*      */ 
/*      */     void clearReferenceQueues()
/*      */     {
/* 2513 */       if (this.map.usesKeyReferences()) {
/* 2514 */         clearKeyReferenceQueue();
/*      */       }
/* 2516 */       if (this.map.usesValueReferences())
/* 2517 */         clearValueReferenceQueue();
/*      */     }
/*      */ 
/*      */     void clearKeyReferenceQueue()
/*      */     {
/* 2522 */       while (this.keyReferenceQueue.poll() != null);
/*      */     }
/*      */ 
/*      */     void clearValueReferenceQueue() {
/* 2526 */       while (this.valueReferenceQueue.poll() != null);
/*      */     }
/*      */ 
/*      */     void recordRead(LocalCache.ReferenceEntry<K, V> entry, long now)
/*      */     {
/* 2539 */       if (this.map.recordsAccess()) {
/* 2540 */         entry.setAccessTime(now);
/*      */       }
/* 2542 */       this.recencyQueue.add(entry);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void recordLockedRead(LocalCache.ReferenceEntry<K, V> entry, long now)
/*      */     {
/* 2554 */       if (this.map.recordsAccess()) {
/* 2555 */         entry.setAccessTime(now);
/*      */       }
/* 2557 */       this.accessQueue.add(entry);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void recordWrite(LocalCache.ReferenceEntry<K, V> entry, int weight, long now)
/*      */     {
/* 2567 */       drainRecencyQueue();
/* 2568 */       this.totalWeight += weight;
/*      */ 
/* 2570 */       if (this.map.recordsAccess()) {
/* 2571 */         entry.setAccessTime(now);
/*      */       }
/* 2573 */       if (this.map.recordsWrite()) {
/* 2574 */         entry.setWriteTime(now);
/*      */       }
/* 2576 */       this.accessQueue.add(entry);
/* 2577 */       this.writeQueue.add(entry);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainRecencyQueue()
/*      */     {
/*      */       LocalCache.ReferenceEntry e;
/* 2589 */       while ((e = (LocalCache.ReferenceEntry)this.recencyQueue.poll()) != null)
/*      */       {
/* 2594 */         if (this.accessQueue.contains(e))
/* 2595 */           this.accessQueue.add(e);
/*      */       }
/*      */     }
/*      */ 
/*      */     void tryExpireEntries(long now)
/*      */     {
/* 2606 */       if (tryLock())
/*      */         try {
/* 2608 */           expireEntries(now);
/*      */         } finally {
/* 2610 */           unlock();
/*      */         }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void expireEntries(long now)
/*      */     {
/* 2618 */       drainRecencyQueue();
/*      */       LocalCache.ReferenceEntry e;
/* 2621 */       while (((e = (LocalCache.ReferenceEntry)this.writeQueue.peek()) != null) && (this.map.isExpired(e, now))) {
/* 2622 */         if (!removeEntry(e, e.getHash(), RemovalCause.EXPIRED)) {
/* 2623 */           throw new AssertionError();
/*      */         }
/*      */       }
/* 2626 */       while (((e = (LocalCache.ReferenceEntry)this.accessQueue.peek()) != null) && (this.map.isExpired(e, now)))
/* 2627 */         if (!removeEntry(e, e.getHash(), RemovalCause.EXPIRED))
/* 2628 */           throw new AssertionError();
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void enqueueNotification(LocalCache.ReferenceEntry<K, V> entry, RemovalCause cause)
/*      */     {
/* 2637 */       enqueueNotification(entry.getKey(), entry.getHash(), entry.getValueReference(), cause);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void enqueueNotification(@Nullable K key, int hash, LocalCache.ValueReference<K, V> valueReference, RemovalCause cause)
/*      */     {
/* 2643 */       this.totalWeight -= valueReference.getWeight();
/* 2644 */       if (cause.wasEvicted()) {
/* 2645 */         this.statsCounter.recordEviction();
/*      */       }
/* 2647 */       if (this.map.removalNotificationQueue != LocalCache.DISCARDING_QUEUE) {
/* 2648 */         Object value = valueReference.get();
/* 2649 */         RemovalNotification notification = new RemovalNotification(key, value, cause);
/* 2650 */         this.map.removalNotificationQueue.offer(notification);
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void evictEntries()
/*      */     {
/* 2660 */       if (!this.map.evictsBySize()) {
/* 2661 */         return;
/*      */       }
/*      */ 
/* 2664 */       drainRecencyQueue();
/* 2665 */       while (this.totalWeight > this.maxSegmentWeight) {
/* 2666 */         LocalCache.ReferenceEntry e = getNextEvictable();
/* 2667 */         if (!removeEntry(e, e.getHash(), RemovalCause.SIZE))
/* 2668 */           throw new AssertionError();
/*      */       }
/*      */     }
/*      */ 
/*      */     LocalCache.ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/* 2675 */       for (LocalCache.ReferenceEntry e : this.accessQueue) {
/* 2676 */         int weight = e.getValueReference().getWeight();
/* 2677 */         if (weight > 0) {
/* 2678 */           return e;
/*      */         }
/*      */       }
/* 2681 */       throw new AssertionError();
/*      */     }
/*      */ 
/*      */     LocalCache.ReferenceEntry<K, V> getFirst(int hash)
/*      */     {
/* 2689 */       AtomicReferenceArray table = this.table;
/* 2690 */       return (LocalCache.ReferenceEntry)table.get(hash & table.length() - 1);
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     LocalCache.ReferenceEntry<K, V> getEntry(Object key, int hash)
/*      */     {
/* 2697 */       for (LocalCache.ReferenceEntry e = getFirst(hash); e != null; e = e.getNext()) {
/* 2698 */         if (e.getHash() == hash)
/*      */         {
/* 2702 */           Object entryKey = e.getKey();
/* 2703 */           if (entryKey == null) {
/* 2704 */             tryDrainReferenceQueues();
/*      */           }
/* 2708 */           else if (this.map.keyEquivalence.equivalent(key, entryKey)) {
/* 2709 */             return e;
/*      */           }
/*      */         }
/*      */       }
/* 2713 */       return null;
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     LocalCache.ReferenceEntry<K, V> getLiveEntry(Object key, int hash, long now) {
/* 2718 */       LocalCache.ReferenceEntry e = getEntry(key, hash);
/* 2719 */       if (e == null)
/* 2720 */         return null;
/* 2721 */       if (this.map.isExpired(e, now)) {
/* 2722 */         tryExpireEntries(now);
/* 2723 */         return null;
/*      */       }
/* 2725 */       return e;
/*      */     }
/*      */ 
/*      */     V getLiveValue(LocalCache.ReferenceEntry<K, V> entry, long now)
/*      */     {
/* 2733 */       if (entry.getKey() == null) {
/* 2734 */         tryDrainReferenceQueues();
/* 2735 */         return null;
/*      */       }
/* 2737 */       Object value = entry.getValueReference().get();
/* 2738 */       if (value == null) {
/* 2739 */         tryDrainReferenceQueues();
/* 2740 */         return null;
/*      */       }
/*      */ 
/* 2743 */       if (this.map.isExpired(entry, now)) {
/* 2744 */         tryExpireEntries(now);
/* 2745 */         return null;
/*      */       }
/* 2747 */       return value;
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     V get(Object key, int hash) {
/*      */       try {
/* 2753 */         if (this.count != 0) {
/* 2754 */           long now = this.map.ticker.read();
/* 2755 */           LocalCache.ReferenceEntry e = getLiveEntry(key, hash, now);
/* 2756 */           if (e == null) {
/* 2757 */             return null;
/*      */           }
/*      */ 
/* 2760 */           Object value = e.getValueReference().get();
/* 2761 */           if (value != null) {
/* 2762 */             recordRead(e, now);
/* 2763 */             return scheduleRefresh(e, e.getKey(), hash, value, now, this.map.defaultLoader);
/*      */           }
/* 2765 */           tryDrainReferenceQueues();
/*      */         }
/* 2767 */         return null;
/*      */       } finally {
/* 2769 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean containsKey(Object key, int hash) {
/*      */       try {
/* 2775 */         if (this.count != 0) {
/* 2776 */           long now = this.map.ticker.read();
/* 2777 */           LocalCache.ReferenceEntry e = getLiveEntry(key, hash, now);
/*      */           boolean bool1;
/* 2778 */           if (e == null) {
/* 2779 */             return false;
/*      */           }
/* 2781 */           return e.getValueReference().get() != null;
/*      */         }
/*      */ 
/* 2784 */         return false;
/*      */       } finally {
/* 2786 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @VisibleForTesting
/*      */     boolean containsValue(Object value)
/*      */     {
/*      */       try
/*      */       {
/* 2797 */         if (this.count != 0) {
/* 2798 */           long now = this.map.ticker.read();
/* 2799 */           AtomicReferenceArray table = this.table;
/* 2800 */           int length = table.length();
/* 2801 */           for (int i = 0; i < length; i++) {
/* 2802 */             for (LocalCache.ReferenceEntry e = (LocalCache.ReferenceEntry)table.get(i); e != null; e = e.getNext()) {
/* 2803 */               Object entryValue = getLiveValue(e, now);
/* 2804 */               if (entryValue != null)
/*      */               {
/* 2807 */                 if (this.map.valueEquivalence.equivalent(value, entryValue)) {
/* 2808 */                   return true;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2814 */         return false;
/*      */       } finally {
/* 2816 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     V put(K key, int hash, V value, boolean onlyIfAbsent) {
/* 2822 */       lock();
/*      */       try {
/* 2824 */         long now = this.map.ticker.read();
/* 2825 */         preWriteCleanup(now);
/*      */ 
/* 2827 */         int newCount = this.count + 1;
/* 2828 */         if (newCount > this.threshold) {
/* 2829 */           expand();
/* 2830 */           newCount = this.count + 1;
/*      */         }
/*      */ 
/* 2833 */         AtomicReferenceArray table = this.table;
/* 2834 */         int index = hash & table.length() - 1;
/* 2835 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */         Object entryKey;
/* 2838 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 2839 */           entryKey = e.getKey();
/* 2840 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2844 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 2845 */             Object entryValue = valueReference.get();
/*      */             Object localObject1;
/* 2847 */             if (entryValue == null) {
/* 2848 */               this.modCount += 1;
/* 2849 */               if (valueReference.isActive()) {
/* 2850 */                 enqueueNotification(key, hash, valueReference, RemovalCause.COLLECTED);
/* 2851 */                 setValue(e, key, value, now);
/* 2852 */                 newCount = this.count;
/*      */               } else {
/* 2854 */                 setValue(e, key, value, now);
/* 2855 */                 newCount = this.count + 1;
/*      */               }
/* 2857 */               this.count = newCount;
/* 2858 */               evictEntries();
/* 2859 */               return null;
/* 2860 */             }if (onlyIfAbsent)
/*      */             {
/* 2864 */               recordLockedRead(e, now);
/* 2865 */               return entryValue;
/*      */             }
/*      */ 
/* 2868 */             this.modCount += 1;
/* 2869 */             enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 2870 */             setValue(e, key, value, now);
/* 2871 */             evictEntries();
/* 2872 */             return entryValue;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2878 */         this.modCount += 1;
/* 2879 */         LocalCache.ReferenceEntry newEntry = newEntry(key, hash, first);
/* 2880 */         setValue(newEntry, key, value, now);
/* 2881 */         table.set(index, newEntry);
/* 2882 */         newCount = this.count + 1;
/* 2883 */         this.count = newCount;
/* 2884 */         evictEntries();
/* 2885 */         return null;
/*      */       } finally {
/* 2887 */         unlock();
/* 2888 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void expand()
/*      */     {
/* 2897 */       AtomicReferenceArray oldTable = this.table;
/* 2898 */       int oldCapacity = oldTable.length();
/* 2899 */       if (oldCapacity >= 1073741824) {
/* 2900 */         return;
/*      */       }
/*      */ 
/* 2913 */       int newCount = this.count;
/* 2914 */       AtomicReferenceArray newTable = newEntryArray(oldCapacity << 1);
/* 2915 */       this.threshold = (newTable.length() * 3 / 4);
/* 2916 */       int newMask = newTable.length() - 1;
/* 2917 */       for (int oldIndex = 0; oldIndex < oldCapacity; oldIndex++)
/*      */       {
/* 2920 */         LocalCache.ReferenceEntry head = (LocalCache.ReferenceEntry)oldTable.get(oldIndex);
/*      */ 
/* 2922 */         if (head != null) {
/* 2923 */           LocalCache.ReferenceEntry next = head.getNext();
/* 2924 */           int headIndex = head.getHash() & newMask;
/*      */ 
/* 2927 */           if (next == null) {
/* 2928 */             newTable.set(headIndex, head);
/*      */           }
/*      */           else
/*      */           {
/* 2933 */             LocalCache.ReferenceEntry tail = head;
/* 2934 */             int tailIndex = headIndex;
/* 2935 */             for (LocalCache.ReferenceEntry e = next; e != null; e = e.getNext()) {
/* 2936 */               int newIndex = e.getHash() & newMask;
/* 2937 */               if (newIndex != tailIndex)
/*      */               {
/* 2939 */                 tailIndex = newIndex;
/* 2940 */                 tail = e;
/*      */               }
/*      */             }
/* 2943 */             newTable.set(tailIndex, tail);
/*      */ 
/* 2946 */             for (LocalCache.ReferenceEntry e = head; e != tail; e = e.getNext()) {
/* 2947 */               int newIndex = e.getHash() & newMask;
/* 2948 */               LocalCache.ReferenceEntry newNext = (LocalCache.ReferenceEntry)newTable.get(newIndex);
/* 2949 */               LocalCache.ReferenceEntry newFirst = copyEntry(e, newNext);
/* 2950 */               if (newFirst != null) {
/* 2951 */                 newTable.set(newIndex, newFirst);
/*      */               } else {
/* 2953 */                 removeCollectedEntry(e);
/* 2954 */                 newCount--;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2960 */       this.table = newTable;
/* 2961 */       this.count = newCount;
/*      */     }
/*      */ 
/*      */     boolean replace(K key, int hash, V oldValue, V newValue) {
/* 2965 */       lock();
/*      */       try {
/* 2967 */         long now = this.map.ticker.read();
/* 2968 */         preWriteCleanup(now);
/*      */ 
/* 2970 */         AtomicReferenceArray table = this.table;
/* 2971 */         int index = hash & table.length() - 1;
/* 2972 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 2974 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 2975 */           Object entryKey = e.getKey();
/* 2976 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2978 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 2979 */             Object entryValue = valueReference.get();
/*      */             int newCount;
/* 2980 */             if (entryValue == null) {
/* 2981 */               if (valueReference.isActive())
/*      */               {
/* 2983 */                 newCount = this.count - 1;
/* 2984 */                 this.modCount += 1;
/* 2985 */                 LocalCache.ReferenceEntry newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
/*      */ 
/* 2987 */                 newCount = this.count - 1;
/* 2988 */                 table.set(index, newFirst);
/* 2989 */                 this.count = newCount;
/*      */               }
/* 2991 */               return 0;
/*      */             }
/*      */ 
/* 2994 */             if (this.map.valueEquivalence.equivalent(oldValue, entryValue)) {
/* 2995 */               this.modCount += 1;
/* 2996 */               enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 2997 */               setValue(e, key, newValue, now);
/* 2998 */               evictEntries();
/* 2999 */               return 1;
/*      */             }
/*      */ 
/* 3003 */             recordLockedRead(e, now);
/* 3004 */             return 0;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 3009 */         return 0;
/*      */       } finally {
/* 3011 */         unlock();
/* 3012 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     V replace(K key, int hash, V newValue) {
/* 3018 */       lock();
/*      */       try {
/* 3020 */         long now = this.map.ticker.read();
/* 3021 */         preWriteCleanup(now);
/*      */ 
/* 3023 */         AtomicReferenceArray table = this.table;
/* 3024 */         int index = hash & table.length() - 1;
/* 3025 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3027 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3028 */           Object entryKey = e.getKey();
/* 3029 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3031 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 3032 */             Object entryValue = valueReference.get();
/*      */             int newCount;
/* 3033 */             if (entryValue == null) {
/* 3034 */               if (valueReference.isActive())
/*      */               {
/* 3036 */                 newCount = this.count - 1;
/* 3037 */                 this.modCount += 1;
/* 3038 */                 LocalCache.ReferenceEntry newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
/*      */ 
/* 3040 */                 newCount = this.count - 1;
/* 3041 */                 table.set(index, newFirst);
/* 3042 */                 this.count = newCount;
/*      */               }
/* 3044 */               return null;
/*      */             }
/*      */ 
/* 3047 */             this.modCount += 1;
/* 3048 */             enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 3049 */             setValue(e, key, newValue, now);
/* 3050 */             evictEntries();
/* 3051 */             return entryValue;
/*      */           }
/*      */         }
/*      */ 
/* 3055 */         return null;
/*      */       } finally {
/* 3057 */         unlock();
/* 3058 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     V remove(Object key, int hash) {
/* 3064 */       lock();
/*      */       try {
/* 3066 */         long now = this.map.ticker.read();
/* 3067 */         preWriteCleanup(now);
/*      */ 
/* 3069 */         int newCount = this.count - 1;
/* 3070 */         AtomicReferenceArray table = this.table;
/* 3071 */         int index = hash & table.length() - 1;
/* 3072 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3074 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3075 */           Object entryKey = e.getKey();
/* 3076 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3078 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 3079 */             Object entryValue = valueReference.get();
/*      */             RemovalCause cause;
/* 3082 */             if (entryValue != null) {
/* 3083 */               cause = RemovalCause.EXPLICIT;
/*      */             }
/*      */             else
/*      */             {
/*      */               RemovalCause cause;
/* 3084 */               if (valueReference.isActive()) {
/* 3085 */                 cause = RemovalCause.COLLECTED;
/*      */               }
/*      */               else
/* 3088 */                 return null;
/*      */             }
/*      */             RemovalCause cause;
/* 3091 */             this.modCount += 1;
/* 3092 */             LocalCache.ReferenceEntry newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, cause);
/*      */ 
/* 3094 */             newCount = this.count - 1;
/* 3095 */             table.set(index, newFirst);
/* 3096 */             this.count = newCount;
/* 3097 */             return entryValue;
/*      */           }
/*      */         }
/*      */ 
/* 3101 */         return null;
/*      */       } finally {
/* 3103 */         unlock();
/* 3104 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean storeLoadedValue(K key, int hash, LocalCache.LoadingValueReference<K, V> oldValueReference, V newValue)
/*      */     {
/* 3110 */       lock();
/*      */       try {
/* 3112 */         long now = this.map.ticker.read();
/* 3113 */         preWriteCleanup(now);
/*      */ 
/* 3115 */         int newCount = this.count + 1;
/* 3116 */         if (newCount > this.threshold) {
/* 3117 */           expand();
/* 3118 */           newCount = this.count + 1;
/*      */         }
/*      */ 
/* 3121 */         AtomicReferenceArray table = this.table;
/* 3122 */         int index = hash & table.length() - 1;
/* 3123 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */         Object entryKey;
/* 3125 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3126 */           entryKey = e.getKey();
/* 3127 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3129 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 3130 */             Object entryValue = valueReference.get();
/*      */             RemovalCause cause;
/* 3133 */             if ((oldValueReference == valueReference) || ((entryValue == null) && (valueReference != LocalCache.UNSET)))
/*      */             {
/* 3135 */               this.modCount += 1;
/* 3136 */               if (oldValueReference.isActive()) {
/* 3137 */                 cause = entryValue == null ? RemovalCause.COLLECTED : RemovalCause.REPLACED;
/*      */ 
/* 3139 */                 enqueueNotification(key, hash, oldValueReference, cause);
/* 3140 */                 newCount--;
/*      */               }
/* 3142 */               setValue(e, key, newValue, now);
/* 3143 */               this.count = newCount;
/* 3144 */               evictEntries();
/* 3145 */               return 1;
/*      */             }
/*      */ 
/* 3149 */             valueReference = new LocalCache.WeightedStrongValueReference(newValue, 0);
/* 3150 */             enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 3151 */             return 0;
/*      */           }
/*      */         }
/*      */ 
/* 3155 */         this.modCount += 1;
/* 3156 */         LocalCache.ReferenceEntry newEntry = newEntry(key, hash, first);
/* 3157 */         setValue(newEntry, key, newValue, now);
/* 3158 */         table.set(index, newEntry);
/* 3159 */         this.count = newCount;
/* 3160 */         evictEntries();
/* 3161 */         return 1;
/*      */       } finally {
/* 3163 */         unlock();
/* 3164 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean remove(Object key, int hash, Object value) {
/* 3169 */       lock();
/*      */       try {
/* 3171 */         long now = this.map.ticker.read();
/* 3172 */         preWriteCleanup(now);
/*      */ 
/* 3174 */         int newCount = this.count - 1;
/* 3175 */         AtomicReferenceArray table = this.table;
/* 3176 */         int index = hash & table.length() - 1;
/* 3177 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3179 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3180 */           Object entryKey = e.getKey();
/* 3181 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3183 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 3184 */             Object entryValue = valueReference.get();
/*      */             RemovalCause cause;
/* 3187 */             if (this.map.valueEquivalence.equivalent(value, entryValue)) {
/* 3188 */               cause = RemovalCause.EXPLICIT;
/*      */             }
/*      */             else
/*      */             {
/*      */               RemovalCause cause;
/* 3189 */               if ((entryValue == null) && (valueReference.isActive())) {
/* 3190 */                 cause = RemovalCause.COLLECTED;
/*      */               }
/*      */               else
/* 3193 */                 return false;
/*      */             }
/*      */             RemovalCause cause;
/* 3196 */             this.modCount += 1;
/* 3197 */             LocalCache.ReferenceEntry newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, cause);
/*      */ 
/* 3199 */             newCount = this.count - 1;
/* 3200 */             table.set(index, newFirst);
/* 3201 */             this.count = newCount;
/* 3202 */             return cause == RemovalCause.EXPLICIT;
/*      */           }
/*      */         }
/*      */ 
/* 3206 */         return 0;
/*      */       } finally {
/* 3208 */         unlock();
/* 3209 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     void clear() {
/* 3214 */       if (this.count != 0) {
/* 3215 */         lock();
/*      */         try {
/* 3217 */           AtomicReferenceArray table = this.table;
/* 3218 */           for (int i = 0; i < table.length(); i++) {
/* 3219 */             for (LocalCache.ReferenceEntry e = (LocalCache.ReferenceEntry)table.get(i); e != null; e = e.getNext())
/*      */             {
/* 3221 */               if (e.getValueReference().isActive()) {
/* 3222 */                 enqueueNotification(e, RemovalCause.EXPLICIT);
/*      */               }
/*      */             }
/*      */           }
/* 3226 */           for (int i = 0; i < table.length(); i++) {
/* 3227 */             table.set(i, null);
/*      */           }
/* 3229 */           clearReferenceQueues();
/* 3230 */           this.writeQueue.clear();
/* 3231 */           this.accessQueue.clear();
/* 3232 */           this.readCount.set(0);
/*      */ 
/* 3234 */           this.modCount += 1;
/* 3235 */           this.count = 0;
/*      */         } finally {
/* 3237 */           unlock();
/* 3238 */           postWriteCleanup();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     @GuardedBy("Segment.this")
/*      */     LocalCache.ReferenceEntry<K, V> removeValueFromChain(LocalCache.ReferenceEntry<K, V> first, LocalCache.ReferenceEntry<K, V> entry, @Nullable K key, int hash, LocalCache.ValueReference<K, V> valueReference, RemovalCause cause)
/*      */     {
/* 3248 */       enqueueNotification(key, hash, valueReference, cause);
/* 3249 */       this.writeQueue.remove(entry);
/* 3250 */       this.accessQueue.remove(entry);
/*      */ 
/* 3252 */       if (valueReference.isLoading()) {
/* 3253 */         valueReference.notifyNewValue(null);
/* 3254 */         return first;
/*      */       }
/* 3256 */       return removeEntryFromChain(first, entry);
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     @GuardedBy("Segment.this")
/*      */     LocalCache.ReferenceEntry<K, V> removeEntryFromChain(LocalCache.ReferenceEntry<K, V> first, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 3264 */       int newCount = this.count;
/* 3265 */       LocalCache.ReferenceEntry newFirst = entry.getNext();
/* 3266 */       for (LocalCache.ReferenceEntry e = first; e != entry; e = e.getNext()) {
/* 3267 */         LocalCache.ReferenceEntry next = copyEntry(e, newFirst);
/* 3268 */         if (next != null) {
/* 3269 */           newFirst = next;
/*      */         } else {
/* 3271 */           removeCollectedEntry(e);
/* 3272 */           newCount--;
/*      */         }
/*      */       }
/* 3275 */       this.count = newCount;
/* 3276 */       return newFirst;
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void removeCollectedEntry(LocalCache.ReferenceEntry<K, V> entry) {
/* 3281 */       enqueueNotification(entry, RemovalCause.COLLECTED);
/* 3282 */       this.writeQueue.remove(entry);
/* 3283 */       this.accessQueue.remove(entry);
/*      */     }
/*      */ 
/*      */     boolean reclaimKey(LocalCache.ReferenceEntry<K, V> entry, int hash)
/*      */     {
/* 3290 */       lock();
/*      */       try {
/* 3292 */         int newCount = this.count - 1;
/* 3293 */         AtomicReferenceArray table = this.table;
/* 3294 */         int index = hash & table.length() - 1;
/* 3295 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3297 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3298 */           if (e == entry) {
/* 3299 */             this.modCount += 1;
/* 3300 */             LocalCache.ReferenceEntry newFirst = removeValueFromChain(first, e, e.getKey(), hash, e.getValueReference(), RemovalCause.COLLECTED);
/*      */ 
/* 3302 */             newCount = this.count - 1;
/* 3303 */             table.set(index, newFirst);
/* 3304 */             this.count = newCount;
/* 3305 */             return true;
/*      */           }
/*      */         }
/*      */ 
/* 3309 */         return 0;
/*      */       } finally {
/* 3311 */         unlock();
/* 3312 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean reclaimValue(K key, int hash, LocalCache.ValueReference<K, V> valueReference)
/*      */     {
/* 3320 */       lock();
/*      */       try {
/* 3322 */         int newCount = this.count - 1;
/* 3323 */         AtomicReferenceArray table = this.table;
/* 3324 */         int index = hash & table.length() - 1;
/* 3325 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3327 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3328 */           Object entryKey = e.getKey();
/* 3329 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3331 */             LocalCache.ValueReference v = e.getValueReference();
/*      */             LocalCache.ReferenceEntry newFirst;
/* 3332 */             if (v == valueReference) {
/* 3333 */               this.modCount += 1;
/* 3334 */               newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
/*      */ 
/* 3336 */               newCount = this.count - 1;
/* 3337 */               table.set(index, newFirst);
/* 3338 */               this.count = newCount;
/* 3339 */               return true;
/*      */             }
/* 3341 */             return 0;
/*      */           }
/*      */         }
/*      */ 
/* 3345 */         return 0;
/*      */       } finally {
/* 3347 */         unlock();
/* 3348 */         if (!isHeldByCurrentThread())
/* 3349 */           postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean removeLoadingValue(K key, int hash, LocalCache.LoadingValueReference<K, V> valueReference)
/*      */     {
/* 3355 */       lock();
/*      */       try {
/* 3357 */         AtomicReferenceArray table = this.table;
/* 3358 */         int index = hash & table.length() - 1;
/* 3359 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3361 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3362 */           Object entryKey = e.getKey();
/* 3363 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3365 */             LocalCache.ValueReference v = e.getValueReference();
/*      */             LocalCache.ReferenceEntry newFirst;
/* 3366 */             if (v == valueReference) {
/* 3367 */               if (valueReference.isActive()) {
/* 3368 */                 e.setValueReference(valueReference.getOldValue());
/*      */               } else {
/* 3370 */                 newFirst = removeEntryFromChain(first, e);
/* 3371 */                 table.set(index, newFirst);
/*      */               }
/* 3373 */               return 1;
/*      */             }
/* 3375 */             return 0;
/*      */           }
/*      */         }
/*      */ 
/* 3379 */         return 0;
/*      */       } finally {
/* 3381 */         unlock();
/* 3382 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     boolean removeEntry(LocalCache.ReferenceEntry<K, V> entry, int hash, RemovalCause cause) {
/* 3388 */       int newCount = this.count - 1;
/* 3389 */       AtomicReferenceArray table = this.table;
/* 3390 */       int index = hash & table.length() - 1;
/* 3391 */       LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3393 */       for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3394 */         if (e == entry) {
/* 3395 */           this.modCount += 1;
/* 3396 */           LocalCache.ReferenceEntry newFirst = removeValueFromChain(first, e, e.getKey(), hash, e.getValueReference(), cause);
/*      */ 
/* 3398 */           newCount = this.count - 1;
/* 3399 */           table.set(index, newFirst);
/* 3400 */           this.count = newCount;
/* 3401 */           return true;
/*      */         }
/*      */       }
/*      */ 
/* 3405 */       return false;
/*      */     }
/*      */ 
/*      */     void postReadCleanup()
/*      */     {
/* 3413 */       if ((this.readCount.incrementAndGet() & 0x3F) == 0)
/* 3414 */         cleanUp();
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void preWriteCleanup(long now)
/*      */     {
/* 3426 */       runLockedCleanup(now);
/*      */     }
/*      */ 
/*      */     void postWriteCleanup()
/*      */     {
/* 3433 */       runUnlockedCleanup();
/*      */     }
/*      */ 
/*      */     void cleanUp() {
/* 3437 */       long now = this.map.ticker.read();
/* 3438 */       runLockedCleanup(now);
/* 3439 */       runUnlockedCleanup();
/*      */     }
/*      */ 
/*      */     void runLockedCleanup(long now) {
/* 3443 */       if (tryLock())
/*      */         try {
/* 3445 */           drainReferenceQueues();
/* 3446 */           expireEntries(now);
/* 3447 */           this.readCount.set(0);
/*      */         } finally {
/* 3449 */           unlock();
/*      */         }
/*      */     }
/*      */ 
/*      */     void runUnlockedCleanup()
/*      */     {
/* 3456 */       if (!isHeldByCurrentThread())
/* 3457 */         this.map.processPendingNotifications();
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeightedStrongValueReference<K, V> extends LocalCache.StrongValueReference<K, V>
/*      */   {
/*      */     final int weight;
/*      */ 
/*      */     WeightedStrongValueReference(V referent, int weight)
/*      */     {
/* 1777 */       super();
/* 1778 */       this.weight = weight;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 1783 */       return this.weight;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeightedSoftValueReference<K, V> extends LocalCache.SoftValueReference<K, V>
/*      */   {
/*      */     final int weight;
/*      */ 
/*      */     WeightedSoftValueReference(ReferenceQueue<V> queue, V referent, LocalCache.ReferenceEntry<K, V> entry, int weight)
/*      */     {
/* 1754 */       super(referent, entry);
/* 1755 */       this.weight = weight;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 1760 */       return this.weight;
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1765 */       return new WeightedSoftValueReference(queue, value, entry, this.weight);
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeightedWeakValueReference<K, V> extends LocalCache.WeakValueReference<K, V>
/*      */   {
/*      */     final int weight;
/*      */ 
/*      */     WeightedWeakValueReference(ReferenceQueue<V> queue, V referent, LocalCache.ReferenceEntry<K, V> entry, int weight)
/*      */     {
/* 1730 */       super(referent, entry);
/* 1731 */       this.weight = weight;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 1736 */       return this.weight;
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1742 */       return new WeightedWeakValueReference(queue, value, entry, this.weight);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class StrongValueReference<K, V>
/*      */     implements LocalCache.ValueReference<K, V>
/*      */   {
/*      */     final V referent;
/*      */ 
/*      */     StrongValueReference(V referent)
/*      */     {
/* 1679 */       this.referent = referent;
/*      */     }
/*      */ 
/*      */     public V get()
/*      */     {
/* 1684 */       return this.referent;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 1689 */       return 1;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1694 */       return null;
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1700 */       return this;
/*      */     }
/*      */ 
/*      */     public boolean isLoading()
/*      */     {
/* 1705 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean isActive()
/*      */     {
/* 1710 */       return true;
/*      */     }
/*      */ 
/*      */     public V waitForValue()
/*      */     {
/* 1715 */       return get();
/*      */     }
/*      */ 
/*      */     public void notifyNewValue(V newValue)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   static class SoftValueReference<K, V> extends SoftReference<V>
/*      */     implements LocalCache.ValueReference<K, V>
/*      */   {
/*      */     final LocalCache.ReferenceEntry<K, V> entry;
/*      */ 
/*      */     SoftValueReference(ReferenceQueue<V> queue, V referent, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1633 */       super(queue);
/* 1634 */       this.entry = entry;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 1639 */       return 1;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1644 */       return this.entry;
/*      */     }
/*      */ 
/*      */     public void notifyNewValue(V newValue)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1653 */       return new SoftValueReference(queue, value, entry);
/*      */     }
/*      */ 
/*      */     public boolean isLoading()
/*      */     {
/* 1658 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean isActive()
/*      */     {
/* 1663 */       return true;
/*      */     }
/*      */ 
/*      */     public V waitForValue()
/*      */     {
/* 1668 */       return get();
/*      */     }
/*      */   }
/*      */ 
/*      */   static class WeakValueReference<K, V> extends WeakReference<V>
/*      */     implements LocalCache.ValueReference<K, V>
/*      */   {
/*      */     final LocalCache.ReferenceEntry<K, V> entry;
/*      */ 
/*      */     WeakValueReference(ReferenceQueue<V> queue, V referent, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1586 */       super(queue);
/* 1587 */       this.entry = entry;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 1592 */       return 1;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1597 */       return this.entry;
/*      */     }
/*      */ 
/*      */     public void notifyNewValue(V newValue)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1606 */       return new WeakValueReference(queue, value, entry);
/*      */     }
/*      */ 
/*      */     public boolean isLoading()
/*      */     {
/* 1611 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean isActive()
/*      */     {
/* 1616 */       return true;
/*      */     }
/*      */ 
/*      */     public V waitForValue()
/*      */     {
/* 1621 */       return get();
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeakAccessWriteEntry<K, V> extends LocalCache.WeakEntry<K, V>
/*      */   {
/* 1499 */     volatile long accessTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1511 */     LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1524 */     LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */ 
/* 1539 */     volatile long writeTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1551 */     LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1564 */     LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */ 
/*      */     WeakAccessWriteEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1494 */       super(key, hash, next);
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/* 1503 */       return this.accessTime;
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1508 */       this.accessTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1516 */       return this.nextAccess;
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1521 */       this.nextAccess = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1529 */       return this.previousAccess;
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1534 */       this.previousAccess = previous;
/*      */     }
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/* 1543 */       return this.writeTime;
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1548 */       this.writeTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1556 */       return this.nextWrite;
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1561 */       this.nextWrite = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1569 */       return this.previousWrite;
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1574 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeakWriteEntry<K, V> extends LocalCache.WeakEntry<K, V>
/*      */   {
/* 1452 */     volatile long writeTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1464 */     LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1477 */     LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */ 
/*      */     WeakWriteEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1447 */       super(key, hash, next);
/*      */     }
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/* 1456 */       return this.writeTime;
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1461 */       this.writeTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1469 */       return this.nextWrite;
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1474 */       this.nextWrite = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1482 */       return this.previousWrite;
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1487 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeakAccessEntry<K, V> extends LocalCache.WeakEntry<K, V>
/*      */   {
/* 1405 */     volatile long accessTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1417 */     LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1430 */     LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */ 
/*      */     WeakAccessEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1400 */       super(key, hash, next);
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/* 1409 */       return this.accessTime;
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1414 */       this.accessTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1422 */       return this.nextAccess;
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1427 */       this.nextAccess = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1435 */       return this.previousAccess;
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1440 */       this.previousAccess = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class WeakEntry<K, V> extends WeakReference<K>
/*      */     implements LocalCache.ReferenceEntry<K, V>
/*      */   {
/*      */     final int hash;
/*      */     final LocalCache.ReferenceEntry<K, V> next;
/* 1374 */     volatile LocalCache.ValueReference<K, V> valueReference = LocalCache.unset();
/*      */ 
/*      */     WeakEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1291 */       super(queue);
/* 1292 */       this.hash = hash;
/* 1293 */       this.next = next;
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/* 1298 */       return get();
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/* 1310 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1315 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1320 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1325 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1330 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1335 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/* 1342 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1347 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1352 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1357 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1362 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1367 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> getValueReference()
/*      */     {
/* 1378 */       return this.valueReference;
/*      */     }
/*      */ 
/*      */     public void setValueReference(LocalCache.ValueReference<K, V> valueReference)
/*      */     {
/* 1383 */       this.valueReference = valueReference;
/*      */     }
/*      */ 
/*      */     public int getHash()
/*      */     {
/* 1388 */       return this.hash;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNext()
/*      */     {
/* 1393 */       return this.next;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class StrongAccessWriteEntry<K, V> extends LocalCache.StrongEntry<K, V>
/*      */   {
/* 1207 */     volatile long accessTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1219 */     LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1232 */     LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */ 
/* 1247 */     volatile long writeTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1259 */     LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1272 */     LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */ 
/*      */     StrongAccessWriteEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1202 */       super(hash, next);
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/* 1211 */       return this.accessTime;
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1216 */       this.accessTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1224 */       return this.nextAccess;
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1229 */       this.nextAccess = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1237 */       return this.previousAccess;
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1242 */       this.previousAccess = previous;
/*      */     }
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/* 1251 */       return this.writeTime;
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1256 */       this.writeTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1264 */       return this.nextWrite;
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1269 */       this.nextWrite = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1277 */       return this.previousWrite;
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1282 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class StrongWriteEntry<K, V> extends LocalCache.StrongEntry<K, V>
/*      */   {
/* 1161 */     volatile long writeTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1173 */     LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1186 */     LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */ 
/*      */     StrongWriteEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1156 */       super(hash, next);
/*      */     }
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/* 1165 */       return this.writeTime;
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1170 */       this.writeTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1178 */       return this.nextWrite;
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1183 */       this.nextWrite = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1191 */       return this.previousWrite;
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1196 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class StrongAccessEntry<K, V> extends LocalCache.StrongEntry<K, V>
/*      */   {
/* 1115 */     volatile long accessTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1127 */     LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1140 */     LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */ 
/*      */     StrongAccessEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1110 */       super(hash, next);
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/* 1119 */       return this.accessTime;
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1124 */       this.accessTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1132 */       return this.nextAccess;
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1137 */       this.nextAccess = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1145 */       return this.previousAccess;
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1150 */       this.previousAccess = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class StrongEntry<K, V> extends LocalCache.AbstractReferenceEntry<K, V>
/*      */   {
/*      */     final K key;
/*      */     final int hash;
/*      */     final LocalCache.ReferenceEntry<K, V> next;
/* 1085 */     volatile LocalCache.ValueReference<K, V> valueReference = LocalCache.unset();
/*      */ 
/*      */     StrongEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1071 */       this.key = key;
/* 1072 */       this.hash = hash;
/* 1073 */       this.next = next;
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/* 1078 */       return this.key;
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> getValueReference()
/*      */     {
/* 1089 */       return this.valueReference;
/*      */     }
/*      */ 
/*      */     public void setValueReference(LocalCache.ValueReference<K, V> valueReference)
/*      */     {
/* 1094 */       this.valueReference = valueReference;
/*      */     }
/*      */ 
/*      */     public int getHash()
/*      */     {
/* 1099 */       return this.hash;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNext()
/*      */     {
/* 1104 */       return this.next;
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class AbstractReferenceEntry<K, V>
/*      */     implements LocalCache.ReferenceEntry<K, V>
/*      */   {
/*      */     public LocalCache.ValueReference<K, V> getValueReference()
/*      */     {
/*  932 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setValueReference(LocalCache.ValueReference<K, V> valueReference)
/*      */     {
/*  937 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNext()
/*      */     {
/*  942 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public int getHash()
/*      */     {
/*  947 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/*  952 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/*  957 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/*  962 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/*  967 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/*  972 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/*  977 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/*  982 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/*  987 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/*  992 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/*  997 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1002 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1007 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1012 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static enum NullEntry
/*      */     implements LocalCache.ReferenceEntry<Object, Object>
/*      */   {
/*  855 */     INSTANCE;
/*      */ 
/*      */     public LocalCache.ValueReference<Object, Object> getValueReference()
/*      */     {
/*  859 */       return null;
/*      */     }
/*      */ 
/*      */     public void setValueReference(LocalCache.ValueReference<Object, Object> valueReference)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<Object, Object> getNext() {
/*  867 */       return null;
/*      */     }
/*      */ 
/*      */     public int getHash()
/*      */     {
/*  872 */       return 0;
/*      */     }
/*      */ 
/*      */     public Object getKey()
/*      */     {
/*  877 */       return null;
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/*  882 */       return 0L;
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<Object, Object> getNextInAccessQueue() {
/*  890 */       return this;
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<Object, Object> next)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<Object, Object> getPreviousInAccessQueue() {
/*  898 */       return this;
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<Object, Object> previous)
/*      */     {
/*      */     }
/*      */ 
/*      */     public long getWriteTime() {
/*  906 */       return 0L;
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<Object, Object> getNextInWriteQueue() {
/*  914 */       return this;
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<Object, Object> next)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<Object, Object> getPreviousInWriteQueue() {
/*  922 */       return this;
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<Object, Object> previous)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract interface ReferenceEntry<K, V>
/*      */   {
/*      */     public abstract LocalCache.ValueReference<K, V> getValueReference();
/*      */ 
/*      */     public abstract void setValueReference(LocalCache.ValueReference<K, V> paramValueReference);
/*      */ 
/*      */     @Nullable
/*      */     public abstract ReferenceEntry<K, V> getNext();
/*      */ 
/*      */     public abstract int getHash();
/*      */ 
/*      */     @Nullable
/*      */     public abstract K getKey();
/*      */ 
/*      */     public abstract long getAccessTime();
/*      */ 
/*      */     public abstract void setAccessTime(long paramLong);
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getNextInAccessQueue();
/*      */ 
/*      */     public abstract void setNextInAccessQueue(ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getPreviousInAccessQueue();
/*      */ 
/*      */     public abstract void setPreviousInAccessQueue(ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     public abstract long getWriteTime();
/*      */ 
/*      */     public abstract void setWriteTime(long paramLong);
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getNextInWriteQueue();
/*      */ 
/*      */     public abstract void setNextInWriteQueue(ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getPreviousInWriteQueue();
/*      */ 
/*      */     public abstract void setPreviousInWriteQueue(ReferenceEntry<K, V> paramReferenceEntry);
/*      */   }
/*      */ 
/*      */   static abstract interface ValueReference<K, V>
/*      */   {
/*      */     @Nullable
/*      */     public abstract V get();
/*      */ 
/*      */     public abstract V waitForValue()
/*      */       throws ExecutionException;
/*      */ 
/*      */     public abstract int getWeight();
/*      */ 
/*      */     @Nullable
/*      */     public abstract LocalCache.ReferenceEntry<K, V> getEntry();
/*      */ 
/*      */     public abstract ValueReference<K, V> copyFor(ReferenceQueue<V> paramReferenceQueue, @Nullable V paramV, LocalCache.ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     public abstract void notifyNewValue(@Nullable V paramV);
/*      */ 
/*      */     public abstract boolean isLoading();
/*      */ 
/*      */     public abstract boolean isActive();
/*      */   }
/*      */ 
/*      */   static abstract enum EntryFactory
/*      */   {
/*  446 */     STRONG, 
/*      */ 
/*  453 */     STRONG_ACCESS, 
/*      */ 
/*  468 */     STRONG_WRITE, 
/*      */ 
/*  483 */     STRONG_ACCESS_WRITE, 
/*      */ 
/*  500 */     WEAK, 
/*      */ 
/*  507 */     WEAK_ACCESS, 
/*      */ 
/*  522 */     WEAK_WRITE, 
/*      */ 
/*  537 */     WEAK_ACCESS_WRITE;
/*      */ 
/*      */     static final int ACCESS_MASK = 1;
/*      */     static final int WRITE_MASK = 2;
/*      */     static final int WEAK_MASK = 4;
/*  564 */     static final EntryFactory[] factories = { STRONG, STRONG_ACCESS, STRONG_WRITE, STRONG_ACCESS_WRITE, WEAK, WEAK_ACCESS, WEAK_WRITE, WEAK_ACCESS_WRITE };
/*      */ 
/*      */     static EntryFactory getFactory(LocalCache.Strength keyStrength, boolean usesAccessQueue, boolean usesWriteQueue)
/*      */     {
/*  571 */       int flags = (keyStrength == LocalCache.Strength.WEAK ? 4 : 0) | (usesAccessQueue ? 1 : 0) | (usesWriteQueue ? 2 : 0);
/*      */ 
/*  574 */       return factories[flags];
/*      */     }
/*      */ 
/*      */     abstract <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> paramSegment, K paramK, int paramInt, @Nullable LocalCache.ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> segment, LocalCache.ReferenceEntry<K, V> original, LocalCache.ReferenceEntry<K, V> newNext)
/*      */     {
/*  597 */       return newEntry(segment, original.getKey(), original.getHash(), newNext);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     <K, V> void copyAccessEntry(LocalCache.ReferenceEntry<K, V> original, LocalCache.ReferenceEntry<K, V> newEntry)
/*      */     {
/*  604 */       newEntry.setAccessTime(original.getAccessTime());
/*      */ 
/*  606 */       LocalCache.connectAccessOrder(original.getPreviousInAccessQueue(), newEntry);
/*  607 */       LocalCache.connectAccessOrder(newEntry, original.getNextInAccessQueue());
/*      */ 
/*  609 */       LocalCache.nullifyAccessOrder(original);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     <K, V> void copyWriteEntry(LocalCache.ReferenceEntry<K, V> original, LocalCache.ReferenceEntry<K, V> newEntry)
/*      */     {
/*  616 */       newEntry.setWriteTime(original.getWriteTime());
/*      */ 
/*  618 */       LocalCache.connectWriteOrder(original.getPreviousInWriteQueue(), newEntry);
/*  619 */       LocalCache.connectWriteOrder(newEntry, original.getNextInWriteQueue());
/*      */ 
/*  621 */       LocalCache.nullifyWriteOrder(original);
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract enum Strength
/*      */   {
/*  381 */     STRONG, 
/*      */ 
/*  396 */     SOFT, 
/*      */ 
/*  412 */     WEAK;
/*      */ 
/*      */     abstract <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> paramSegment, LocalCache.ReferenceEntry<K, V> paramReferenceEntry, V paramV, int paramInt);
/*      */ 
/*      */     abstract Equivalence<Object> defaultEquivalence();
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.cache.LocalCache
 * JD-Core Version:    0.6.2
 */