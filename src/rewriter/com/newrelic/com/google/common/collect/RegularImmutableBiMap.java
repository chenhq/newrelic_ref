/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map.Entry;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ class RegularImmutableBiMap<K, V> extends ImmutableBiMap<K, V>
/*     */ {
/*     */   static final double MAX_LOAD_FACTOR = 1.2D;
/*     */   private final transient ImmutableMapEntry<K, V>[] keyTable;
/*     */   private final transient ImmutableMapEntry<K, V>[] valueTable;
/*     */   private final transient ImmutableMapEntry<K, V>[] entries;
/*     */   private final transient int mask;
/*     */   private final transient int hashCode;
/*     */   private transient ImmutableBiMap<V, K> inverse;
/*     */ 
/*     */   RegularImmutableBiMap(ImmutableMapEntry.TerminalEntry<?, ?>[] entriesToAdd)
/*     */   {
/*  46 */     this(entriesToAdd.length, entriesToAdd);
/*     */   }
/*     */ 
/*     */   RegularImmutableBiMap(int n, ImmutableMapEntry.TerminalEntry<?, ?>[] entriesToAdd)
/*     */   {
/*  56 */     int tableSize = Hashing.closedTableSize(n, 1.2D);
/*  57 */     this.mask = (tableSize - 1);
/*  58 */     ImmutableMapEntry[] keyTable = createEntryArray(tableSize);
/*  59 */     ImmutableMapEntry[] valueTable = createEntryArray(tableSize);
/*  60 */     ImmutableMapEntry[] entries = createEntryArray(n);
/*  61 */     int hashCode = 0;
/*     */ 
/*  63 */     for (int i = 0; i < n; i++)
/*     */     {
/*  65 */       ImmutableMapEntry.TerminalEntry entry = entriesToAdd[i];
/*  66 */       Object key = entry.getKey();
/*  67 */       Object value = entry.getValue();
/*     */ 
/*  69 */       int keyHash = key.hashCode();
/*  70 */       int valueHash = value.hashCode();
/*  71 */       int keyBucket = Hashing.smear(keyHash) & this.mask;
/*  72 */       int valueBucket = Hashing.smear(valueHash) & this.mask;
/*     */ 
/*  74 */       ImmutableMapEntry nextInKeyBucket = keyTable[keyBucket];
/*  75 */       for (ImmutableMapEntry keyEntry = nextInKeyBucket; keyEntry != null; 
/*  76 */         keyEntry = keyEntry.getNextInKeyBucket()) {
/*  77 */         checkNoConflict(!key.equals(keyEntry.getKey()), "key", entry, keyEntry);
/*     */       }
/*  79 */       ImmutableMapEntry nextInValueBucket = valueTable[valueBucket];
/*  80 */       for (ImmutableMapEntry valueEntry = nextInValueBucket; valueEntry != null; 
/*  81 */         valueEntry = valueEntry.getNextInValueBucket()) {
/*  82 */         checkNoConflict(!value.equals(valueEntry.getValue()), "value", entry, valueEntry);
/*     */       }
/*  84 */       ImmutableMapEntry newEntry = (nextInKeyBucket == null) && (nextInValueBucket == null) ? entry : new NonTerminalBiMapEntry(entry, nextInKeyBucket, nextInValueBucket);
/*     */ 
/*  88 */       keyTable[keyBucket] = newEntry;
/*  89 */       valueTable[valueBucket] = newEntry;
/*  90 */       entries[i] = newEntry;
/*  91 */       hashCode += (keyHash ^ valueHash);
/*     */     }
/*     */ 
/*  94 */     this.keyTable = keyTable;
/*  95 */     this.valueTable = valueTable;
/*  96 */     this.entries = entries;
/*  97 */     this.hashCode = hashCode;
/*     */   }
/*     */ 
/*     */   RegularImmutableBiMap(Map.Entry<?, ?>[] entriesToAdd)
/*     */   {
/* 104 */     int n = entriesToAdd.length;
/* 105 */     int tableSize = Hashing.closedTableSize(n, 1.2D);
/* 106 */     this.mask = (tableSize - 1);
/* 107 */     ImmutableMapEntry[] keyTable = createEntryArray(tableSize);
/* 108 */     ImmutableMapEntry[] valueTable = createEntryArray(tableSize);
/* 109 */     ImmutableMapEntry[] entries = createEntryArray(n);
/* 110 */     int hashCode = 0;
/*     */ 
/* 112 */     for (int i = 0; i < n; i++)
/*     */     {
/* 114 */       Map.Entry entry = entriesToAdd[i];
/* 115 */       Object key = entry.getKey();
/* 116 */       Object value = entry.getValue();
/* 117 */       CollectPreconditions.checkEntryNotNull(key, value);
/* 118 */       int keyHash = key.hashCode();
/* 119 */       int valueHash = value.hashCode();
/* 120 */       int keyBucket = Hashing.smear(keyHash) & this.mask;
/* 121 */       int valueBucket = Hashing.smear(valueHash) & this.mask;
/*     */ 
/* 123 */       ImmutableMapEntry nextInKeyBucket = keyTable[keyBucket];
/* 124 */       for (ImmutableMapEntry keyEntry = nextInKeyBucket; keyEntry != null; 
/* 125 */         keyEntry = keyEntry.getNextInKeyBucket()) {
/* 126 */         checkNoConflict(!key.equals(keyEntry.getKey()), "key", entry, keyEntry);
/*     */       }
/* 128 */       ImmutableMapEntry nextInValueBucket = valueTable[valueBucket];
/* 129 */       for (ImmutableMapEntry valueEntry = nextInValueBucket; valueEntry != null; 
/* 130 */         valueEntry = valueEntry.getNextInValueBucket()) {
/* 131 */         checkNoConflict(!value.equals(valueEntry.getValue()), "value", entry, valueEntry);
/*     */       }
/* 133 */       ImmutableMapEntry newEntry = (nextInKeyBucket == null) && (nextInValueBucket == null) ? new ImmutableMapEntry.TerminalEntry(key, value) : new NonTerminalBiMapEntry(key, value, nextInKeyBucket, nextInValueBucket);
/*     */ 
/* 137 */       keyTable[keyBucket] = newEntry;
/* 138 */       valueTable[valueBucket] = newEntry;
/* 139 */       entries[i] = newEntry;
/* 140 */       hashCode += (keyHash ^ valueHash);
/*     */     }
/*     */ 
/* 143 */     this.keyTable = keyTable;
/* 144 */     this.valueTable = valueTable;
/* 145 */     this.entries = entries;
/* 146 */     this.hashCode = hashCode;
/*     */   }
/*     */ 
/*     */   private static <K, V> ImmutableMapEntry<K, V>[] createEntryArray(int length)
/*     */   {
/* 183 */     return new ImmutableMapEntry[length];
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public V get(@Nullable Object key)
/*     */   {
/* 189 */     if (key == null) {
/* 190 */       return null;
/*     */     }
/* 192 */     int bucket = Hashing.smear(key.hashCode()) & this.mask;
/* 193 */     for (ImmutableMapEntry entry = this.keyTable[bucket]; entry != null; 
/* 194 */       entry = entry.getNextInKeyBucket()) {
/* 195 */       if (key.equals(entry.getKey())) {
/* 196 */         return entry.getValue();
/*     */       }
/*     */     }
/* 199 */     return null;
/*     */   }
/*     */ 
/*     */   ImmutableSet<Map.Entry<K, V>> createEntrySet()
/*     */   {
/* 204 */     return new ImmutableMapEntrySet()
/*     */     {
/*     */       ImmutableMap<K, V> map() {
/* 207 */         return RegularImmutableBiMap.this;
/*     */       }
/*     */ 
/*     */       public UnmodifiableIterator<Map.Entry<K, V>> iterator()
/*     */       {
/* 212 */         return asList().iterator();
/*     */       }
/*     */ 
/*     */       ImmutableList<Map.Entry<K, V>> createAsList()
/*     */       {
/* 217 */         return new RegularImmutableAsList(this, RegularImmutableBiMap.this.entries);
/*     */       }
/*     */ 
/*     */       boolean isHashCodeFast()
/*     */       {
/* 222 */         return true;
/*     */       }
/*     */ 
/*     */       public int hashCode()
/*     */       {
/* 227 */         return RegularImmutableBiMap.this.hashCode;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   boolean isPartialView()
/*     */   {
/* 234 */     return false;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 239 */     return this.entries.length;
/*     */   }
/*     */ 
/*     */   public ImmutableBiMap<V, K> inverse()
/*     */   {
/* 246 */     ImmutableBiMap result = this.inverse;
/* 247 */     return result == null ? (this.inverse = new Inverse(null)) : result;
/*     */   }
/*     */ 
/*     */   private static class InverseSerializedForm<K, V>
/*     */     implements Serializable
/*     */   {
/*     */     private final ImmutableBiMap<K, V> forward;
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     InverseSerializedForm(ImmutableBiMap<K, V> forward)
/*     */     {
/* 335 */       this.forward = forward;
/*     */     }
/*     */ 
/*     */     Object readResolve() {
/* 339 */       return this.forward.inverse();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class Inverse extends ImmutableBiMap<V, K>
/*     */   {
/*     */     private Inverse()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 254 */       return inverse().size();
/*     */     }
/*     */ 
/*     */     public ImmutableBiMap<K, V> inverse()
/*     */     {
/* 259 */       return RegularImmutableBiMap.this;
/*     */     }
/*     */ 
/*     */     public K get(@Nullable Object value)
/*     */     {
/* 264 */       if (value == null) {
/* 265 */         return null;
/*     */       }
/* 267 */       int bucket = Hashing.smear(value.hashCode()) & RegularImmutableBiMap.this.mask;
/* 268 */       for (ImmutableMapEntry entry = RegularImmutableBiMap.this.valueTable[bucket]; entry != null; 
/* 269 */         entry = entry.getNextInValueBucket()) {
/* 270 */         if (value.equals(entry.getValue())) {
/* 271 */           return entry.getKey();
/*     */         }
/*     */       }
/* 274 */       return null;
/*     */     }
/*     */ 
/*     */     ImmutableSet<Map.Entry<V, K>> createEntrySet()
/*     */     {
/* 279 */       return new InverseEntrySet();
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 322 */       return false;
/*     */     }
/*     */ 
/*     */     Object writeReplace()
/*     */     {
/* 327 */       return new RegularImmutableBiMap.InverseSerializedForm(RegularImmutableBiMap.this);
/*     */     }
/*     */ 
/*     */     final class InverseEntrySet extends ImmutableMapEntrySet<V, K>
/*     */     {
/*     */       InverseEntrySet()
/*     */       {
/*     */       }
/*     */ 
/*     */       ImmutableMap<V, K> map()
/*     */       {
/* 285 */         return RegularImmutableBiMap.Inverse.this;
/*     */       }
/*     */ 
/*     */       boolean isHashCodeFast()
/*     */       {
/* 290 */         return true;
/*     */       }
/*     */ 
/*     */       public int hashCode()
/*     */       {
/* 295 */         return RegularImmutableBiMap.this.hashCode;
/*     */       }
/*     */ 
/*     */       public UnmodifiableIterator<Map.Entry<V, K>> iterator()
/*     */       {
/* 300 */         return asList().iterator();
/*     */       }
/*     */ 
/*     */       ImmutableList<Map.Entry<V, K>> createAsList()
/*     */       {
/* 305 */         return new ImmutableAsList()
/*     */         {
/*     */           public Map.Entry<V, K> get(int index) {
/* 308 */             Map.Entry entry = RegularImmutableBiMap.this.entries[index];
/* 309 */             return Maps.immutableEntry(entry.getValue(), entry.getKey());
/*     */           }
/*     */ 
/*     */           ImmutableCollection<Map.Entry<V, K>> delegateCollection()
/*     */           {
/* 314 */             return RegularImmutableBiMap.Inverse.InverseEntrySet.this;
/*     */           }
/*     */         };
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class NonTerminalBiMapEntry<K, V> extends ImmutableMapEntry<K, V>
/*     */   {
/*     */ 
/*     */     @Nullable
/*     */     private final ImmutableMapEntry<K, V> nextInKeyBucket;
/*     */ 
/*     */     @Nullable
/*     */     private final ImmutableMapEntry<K, V> nextInValueBucket;
/*     */ 
/*     */     NonTerminalBiMapEntry(K key, V value, @Nullable ImmutableMapEntry<K, V> nextInKeyBucket, @Nullable ImmutableMapEntry<K, V> nextInValueBucket)
/*     */     {
/* 155 */       super(value);
/* 156 */       this.nextInKeyBucket = nextInKeyBucket;
/* 157 */       this.nextInValueBucket = nextInValueBucket;
/*     */     }
/*     */ 
/*     */     NonTerminalBiMapEntry(ImmutableMapEntry<K, V> contents, @Nullable ImmutableMapEntry<K, V> nextInKeyBucket, @Nullable ImmutableMapEntry<K, V> nextInValueBucket)
/*     */     {
/* 163 */       super();
/* 164 */       this.nextInKeyBucket = nextInKeyBucket;
/* 165 */       this.nextInValueBucket = nextInValueBucket;
/*     */     }
/*     */ 
/*     */     @Nullable
/*     */     ImmutableMapEntry<K, V> getNextInKeyBucket()
/*     */     {
/* 171 */       return this.nextInKeyBucket;
/*     */     }
/*     */ 
/*     */     @Nullable
/*     */     ImmutableMapEntry<K, V> getNextInValueBucket()
/*     */     {
/* 177 */       return this.nextInValueBucket;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.RegularImmutableBiMap
 * JD-Core Version:    0.6.2
 */