/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class ImmutableCollection<E> extends AbstractCollection<E>
/*     */   implements Serializable
/*     */ {
/*     */   private transient ImmutableList<E> asList;
/*     */ 
/*     */   public abstract UnmodifiableIterator<E> iterator();
/*     */ 
/*     */   public final Object[] toArray()
/*     */   {
/*  60 */     int size = size();
/*  61 */     if (size == 0) {
/*  62 */       return ObjectArrays.EMPTY_ARRAY;
/*     */     }
/*  64 */     Object[] result = new Object[size()];
/*  65 */     copyIntoArray(result, 0);
/*  66 */     return result;
/*     */   }
/*     */ 
/*     */   public final <T> T[] toArray(T[] other)
/*     */   {
/*  71 */     Preconditions.checkNotNull(other);
/*  72 */     int size = size();
/*  73 */     if (other.length < size)
/*  74 */       other = ObjectArrays.newArray(other, size);
/*  75 */     else if (other.length > size) {
/*  76 */       other[size] = null;
/*     */     }
/*  78 */     copyIntoArray(other, 0);
/*  79 */     return other;
/*     */   }
/*     */ 
/*     */   public boolean contains(@Nullable Object object)
/*     */   {
/*  84 */     return (object != null) && (super.contains(object));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public final boolean add(E e)
/*     */   {
/*  96 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public final boolean remove(Object object)
/*     */   {
/* 108 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public final boolean addAll(Collection<? extends E> newElements)
/*     */   {
/* 120 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public final boolean removeAll(Collection<?> oldElements)
/*     */   {
/* 132 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public final boolean retainAll(Collection<?> elementsToKeep)
/*     */   {
/* 144 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public final void clear()
/*     */   {
/* 156 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ImmutableList<E> asList()
/*     */   {
/* 171 */     ImmutableList list = this.asList;
/* 172 */     return list == null ? (this.asList = createAsList()) : list;
/*     */   }
/*     */ 
/*     */   ImmutableList<E> createAsList() {
/* 176 */     switch (size()) {
/*     */     case 0:
/* 178 */       return ImmutableList.of();
/*     */     case 1:
/* 180 */       return ImmutableList.of(iterator().next());
/*     */     }
/* 182 */     return new RegularImmutableAsList(this, toArray());
/*     */   }
/*     */ 
/*     */   abstract boolean isPartialView();
/*     */ 
/*     */   int copyIntoArray(Object[] dst, int offset)
/*     */   {
/* 199 */     for (Iterator i$ = iterator(); i$.hasNext(); ) { Object e = i$.next();
/* 200 */       dst[(offset++)] = e;
/*     */     }
/* 202 */     return offset;
/*     */   }
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 207 */     return new ImmutableList.SerializedForm(toArray());
/*     */   }
/*     */ 
/*     */   static abstract class ArrayBasedBuilder<E> extends ImmutableCollection.Builder<E>
/*     */   {
/*     */     Object[] contents;
/*     */     int size;
/*     */ 
/*     */     ArrayBasedBuilder(int initialCapacity)
/*     */     {
/* 321 */       CollectPreconditions.checkNonnegative(initialCapacity, "initialCapacity");
/* 322 */       this.contents = new Object[initialCapacity];
/* 323 */       this.size = 0;
/*     */     }
/*     */ 
/*     */     private void ensureCapacity(int minCapacity)
/*     */     {
/* 331 */       if (this.contents.length < minCapacity)
/* 332 */         this.contents = ObjectArrays.arraysCopyOf(this.contents, expandedCapacity(this.contents.length, minCapacity));
/*     */     }
/*     */ 
/*     */     public ArrayBasedBuilder<E> add(E element)
/*     */     {
/* 339 */       Preconditions.checkNotNull(element);
/* 340 */       ensureCapacity(this.size + 1);
/* 341 */       this.contents[(this.size++)] = element;
/* 342 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableCollection.Builder<E> add(E[] elements)
/*     */     {
/* 347 */       ObjectArrays.checkElementsNotNull(elements);
/* 348 */       ensureCapacity(this.size + elements.length);
/* 349 */       System.arraycopy(elements, 0, this.contents, this.size, elements.length);
/* 350 */       this.size += elements.length;
/* 351 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableCollection.Builder<E> addAll(Iterable<? extends E> elements)
/*     */     {
/* 356 */       if ((elements instanceof Collection)) {
/* 357 */         Collection collection = (Collection)elements;
/* 358 */         ensureCapacity(this.size + collection.size());
/*     */       }
/* 360 */       super.addAll(elements);
/* 361 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class Builder<E>
/*     */   {
/*     */     static final int DEFAULT_INITIAL_CAPACITY = 4;
/*     */ 
/*     */     static int expandedCapacity(int oldCapacity, int minCapacity)
/*     */     {
/* 219 */       if (minCapacity < 0) {
/* 220 */         throw new AssertionError("cannot store more than MAX_VALUE elements");
/*     */       }
/*     */ 
/* 223 */       int newCapacity = oldCapacity + (oldCapacity >> 1) + 1;
/* 224 */       if (newCapacity < minCapacity) {
/* 225 */         newCapacity = Integer.highestOneBit(minCapacity - 1) << 1;
/*     */       }
/* 227 */       if (newCapacity < 0) {
/* 228 */         newCapacity = 2147483647;
/*     */       }
/*     */ 
/* 231 */       return newCapacity;
/*     */     }
/*     */ 
/*     */     public abstract Builder<E> add(E paramE);
/*     */ 
/*     */     public Builder<E> add(E[] elements)
/*     */     {
/* 262 */       for (Object element : elements) {
/* 263 */         add(element);
/*     */       }
/* 265 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterable<? extends E> elements)
/*     */     {
/* 281 */       for (Iterator i$ = elements.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 282 */         add(element);
/*     */       }
/* 284 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterator<? extends E> elements)
/*     */     {
/* 300 */       while (elements.hasNext()) {
/* 301 */         add(elements.next());
/*     */       }
/* 303 */       return this;
/*     */     }
/*     */ 
/*     */     public abstract ImmutableCollection<E> build();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ImmutableCollection
 * JD-Core Version:    0.6.2
 */