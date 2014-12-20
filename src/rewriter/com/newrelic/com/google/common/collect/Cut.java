/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.primitives.Booleans;
/*     */ import java.io.Serializable;
/*     */ import java.util.NoSuchElementException;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ abstract class Cut<C extends Comparable>
/*     */   implements Comparable<Cut<C>>, Serializable
/*     */ {
/*     */   final C endpoint;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   Cut(@Nullable C endpoint)
/*     */   {
/*  41 */     this.endpoint = endpoint;
/*     */   }
/*     */ 
/*     */   abstract boolean isLessThan(C paramC);
/*     */ 
/*     */   abstract BoundType typeAsLowerBound();
/*     */ 
/*     */   abstract BoundType typeAsUpperBound();
/*     */ 
/*     */   abstract Cut<C> withLowerBoundType(BoundType paramBoundType, DiscreteDomain<C> paramDiscreteDomain);
/*     */ 
/*     */   abstract Cut<C> withUpperBoundType(BoundType paramBoundType, DiscreteDomain<C> paramDiscreteDomain);
/*     */ 
/*     */   abstract void describeAsLowerBound(StringBuilder paramStringBuilder);
/*     */ 
/*     */   abstract void describeAsUpperBound(StringBuilder paramStringBuilder);
/*     */ 
/*     */   abstract C leastValueAbove(DiscreteDomain<C> paramDiscreteDomain);
/*     */ 
/*     */   abstract C greatestValueBelow(DiscreteDomain<C> paramDiscreteDomain);
/*     */ 
/*     */   Cut<C> canonical(DiscreteDomain<C> domain) {
/*  63 */     return this;
/*     */   }
/*     */ 
/*     */   public int compareTo(Cut<C> that)
/*     */   {
/*  69 */     if (that == belowAll()) {
/*  70 */       return 1;
/*     */     }
/*  72 */     if (that == aboveAll()) {
/*  73 */       return -1;
/*     */     }
/*  75 */     int result = Range.compareOrThrow(this.endpoint, that.endpoint);
/*  76 */     if (result != 0) {
/*  77 */       return result;
/*     */     }
/*     */ 
/*  80 */     return Booleans.compare(this instanceof AboveValue, that instanceof AboveValue);
/*     */   }
/*     */ 
/*     */   C endpoint()
/*     */   {
/*  85 */     return this.endpoint;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  90 */     if ((obj instanceof Cut))
/*     */     {
/*  92 */       Cut that = (Cut)obj;
/*     */       try {
/*  94 */         int compareResult = compareTo(that);
/*  95 */         return compareResult == 0;
/*     */       } catch (ClassCastException ignored) {
/*     */       }
/*     */     }
/*  99 */     return false;
/*     */   }
/*     */ 
/*     */   static <C extends Comparable> Cut<C> belowAll()
/*     */   {
/* 108 */     return BelowAll.INSTANCE;
/*     */   }
/*     */ 
/*     */   static <C extends Comparable> Cut<C> aboveAll()
/*     */   {
/* 179 */     return AboveAll.INSTANCE;
/*     */   }
/*     */ 
/*     */   static <C extends Comparable> Cut<C> belowValue(C endpoint)
/*     */   {
/* 235 */     return new BelowValue(endpoint);
/*     */   }
/*     */ 
/*     */   static <C extends Comparable> Cut<C> aboveValue(C endpoint)
/*     */   {
/* 296 */     return new AboveValue(endpoint);
/*     */   }
/*     */   private static final class AboveValue<C extends Comparable> extends Cut<C> {
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/* 301 */     AboveValue(C endpoint) { super(); }
/*     */ 
/*     */     boolean isLessThan(C value)
/*     */     {
/* 305 */       return Range.compareOrThrow(this.endpoint, value) < 0;
/*     */     }
/*     */     BoundType typeAsLowerBound() {
/* 308 */       return BoundType.OPEN;
/*     */     }
/*     */     BoundType typeAsUpperBound() {
/* 311 */       return BoundType.CLOSED;
/*     */     }
/*     */     Cut<C> withLowerBoundType(BoundType boundType, DiscreteDomain<C> domain) {
/* 314 */       switch (Cut.1.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */       case 2:
/* 316 */         return this;
/*     */       case 1:
/* 318 */         Comparable next = domain.next(this.endpoint);
/* 319 */         return next == null ? Cut.belowAll() : belowValue(next);
/*     */       }
/* 321 */       throw new AssertionError();
/*     */     }
/*     */ 
/*     */     Cut<C> withUpperBoundType(BoundType boundType, DiscreteDomain<C> domain) {
/* 325 */       switch (Cut.1.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */       case 2:
/* 327 */         Comparable next = domain.next(this.endpoint);
/* 328 */         return next == null ? Cut.aboveAll() : belowValue(next);
/*     */       case 1:
/* 330 */         return this;
/*     */       }
/* 332 */       throw new AssertionError();
/*     */     }
/*     */ 
/*     */     void describeAsLowerBound(StringBuilder sb) {
/* 336 */       sb.append('(').append(this.endpoint);
/*     */     }
/*     */     void describeAsUpperBound(StringBuilder sb) {
/* 339 */       sb.append(this.endpoint).append(']');
/*     */     }
/*     */     C leastValueAbove(DiscreteDomain<C> domain) {
/* 342 */       return domain.next(this.endpoint);
/*     */     }
/*     */     C greatestValueBelow(DiscreteDomain<C> domain) {
/* 345 */       return this.endpoint;
/*     */     }
/*     */     Cut<C> canonical(DiscreteDomain<C> domain) {
/* 348 */       Comparable next = leastValueAbove(domain);
/* 349 */       return next != null ? belowValue(next) : Cut.aboveAll();
/*     */     }
/*     */     public int hashCode() {
/* 352 */       return this.endpoint.hashCode() ^ 0xFFFFFFFF;
/*     */     }
/*     */     public String toString() {
/* 355 */       return "/" + this.endpoint + "\\";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class BelowValue<C extends Comparable> extends Cut<C>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     BelowValue(C endpoint)
/*     */     {
/* 240 */       super();
/*     */     }
/*     */ 
/*     */     boolean isLessThan(C value) {
/* 244 */       return Range.compareOrThrow(this.endpoint, value) <= 0;
/*     */     }
/*     */     BoundType typeAsLowerBound() {
/* 247 */       return BoundType.CLOSED;
/*     */     }
/*     */     BoundType typeAsUpperBound() {
/* 250 */       return BoundType.OPEN;
/*     */     }
/*     */     Cut<C> withLowerBoundType(BoundType boundType, DiscreteDomain<C> domain) {
/* 253 */       switch (Cut.1.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */       case 1:
/* 255 */         return this;
/*     */       case 2:
/* 257 */         Comparable previous = domain.previous(this.endpoint);
/* 258 */         return previous == null ? Cut.belowAll() : new Cut.AboveValue(previous);
/*     */       }
/* 260 */       throw new AssertionError();
/*     */     }
/*     */ 
/*     */     Cut<C> withUpperBoundType(BoundType boundType, DiscreteDomain<C> domain) {
/* 264 */       switch (Cut.1.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */       case 1:
/* 266 */         Comparable previous = domain.previous(this.endpoint);
/* 267 */         return previous == null ? Cut.aboveAll() : new Cut.AboveValue(previous);
/*     */       case 2:
/* 269 */         return this;
/*     */       }
/* 271 */       throw new AssertionError();
/*     */     }
/*     */ 
/*     */     void describeAsLowerBound(StringBuilder sb) {
/* 275 */       sb.append('[').append(this.endpoint);
/*     */     }
/*     */     void describeAsUpperBound(StringBuilder sb) {
/* 278 */       sb.append(this.endpoint).append(')');
/*     */     }
/*     */     C leastValueAbove(DiscreteDomain<C> domain) {
/* 281 */       return this.endpoint;
/*     */     }
/*     */     C greatestValueBelow(DiscreteDomain<C> domain) {
/* 284 */       return domain.previous(this.endpoint);
/*     */     }
/*     */     public int hashCode() {
/* 287 */       return this.endpoint.hashCode();
/*     */     }
/*     */     public String toString() {
/* 290 */       return "\\" + this.endpoint + "/";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class AboveAll extends Cut<Comparable<?>>
/*     */   {
/* 183 */     private static final AboveAll INSTANCE = new AboveAll();
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private AboveAll()
/*     */     {
/* 186 */       super();
/*     */     }
/*     */     Comparable<?> endpoint() {
/* 189 */       throw new IllegalStateException("range unbounded on this side");
/*     */     }
/*     */     boolean isLessThan(Comparable<?> value) {
/* 192 */       return false;
/*     */     }
/*     */     BoundType typeAsLowerBound() {
/* 195 */       throw new AssertionError("this statement should be unreachable");
/*     */     }
/*     */     BoundType typeAsUpperBound() {
/* 198 */       throw new IllegalStateException();
/*     */     }
/*     */ 
/*     */     Cut<Comparable<?>> withLowerBoundType(BoundType boundType, DiscreteDomain<Comparable<?>> domain) {
/* 202 */       throw new AssertionError("this statement should be unreachable");
/*     */     }
/*     */ 
/*     */     Cut<Comparable<?>> withUpperBoundType(BoundType boundType, DiscreteDomain<Comparable<?>> domain) {
/* 206 */       throw new IllegalStateException();
/*     */     }
/*     */     void describeAsLowerBound(StringBuilder sb) {
/* 209 */       throw new AssertionError();
/*     */     }
/*     */     void describeAsUpperBound(StringBuilder sb) {
/* 212 */       sb.append("+∞)");
/*     */     }
/*     */ 
/*     */     Comparable<?> leastValueAbove(DiscreteDomain<Comparable<?>> domain) {
/* 216 */       throw new AssertionError();
/*     */     }
/*     */ 
/*     */     Comparable<?> greatestValueBelow(DiscreteDomain<Comparable<?>> domain) {
/* 220 */       return domain.maxValue();
/*     */     }
/*     */     public int compareTo(Cut<Comparable<?>> o) {
/* 223 */       return o == this ? 0 : 1;
/*     */     }
/*     */     public String toString() {
/* 226 */       return "+∞";
/*     */     }
/*     */     private Object readResolve() {
/* 229 */       return INSTANCE;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class BelowAll extends Cut<Comparable<?>>
/*     */   {
/* 114 */     private static final BelowAll INSTANCE = new BelowAll();
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private BelowAll()
/*     */     {
/* 117 */       super();
/*     */     }
/*     */     Comparable<?> endpoint() {
/* 120 */       throw new IllegalStateException("range unbounded on this side");
/*     */     }
/*     */     boolean isLessThan(Comparable<?> value) {
/* 123 */       return true;
/*     */     }
/*     */     BoundType typeAsLowerBound() {
/* 126 */       throw new IllegalStateException();
/*     */     }
/*     */     BoundType typeAsUpperBound() {
/* 129 */       throw new AssertionError("this statement should be unreachable");
/*     */     }
/*     */ 
/*     */     Cut<Comparable<?>> withLowerBoundType(BoundType boundType, DiscreteDomain<Comparable<?>> domain) {
/* 133 */       throw new IllegalStateException();
/*     */     }
/*     */ 
/*     */     Cut<Comparable<?>> withUpperBoundType(BoundType boundType, DiscreteDomain<Comparable<?>> domain) {
/* 137 */       throw new AssertionError("this statement should be unreachable");
/*     */     }
/*     */     void describeAsLowerBound(StringBuilder sb) {
/* 140 */       sb.append("(-∞");
/*     */     }
/*     */     void describeAsUpperBound(StringBuilder sb) {
/* 143 */       throw new AssertionError();
/*     */     }
/*     */ 
/*     */     Comparable<?> leastValueAbove(DiscreteDomain<Comparable<?>> domain) {
/* 147 */       return domain.minValue();
/*     */     }
/*     */ 
/*     */     Comparable<?> greatestValueBelow(DiscreteDomain<Comparable<?>> domain) {
/* 151 */       throw new AssertionError();
/*     */     }
/*     */ 
/*     */     Cut<Comparable<?>> canonical(DiscreteDomain<Comparable<?>> domain) {
/*     */       try {
/* 156 */         return Cut.belowValue(domain.minValue()); } catch (NoSuchElementException e) {
/*     */       }
/* 158 */       return this;
/*     */     }
/*     */ 
/*     */     public int compareTo(Cut<Comparable<?>> o) {
/* 162 */       return o == this ? 0 : -1;
/*     */     }
/*     */     public String toString() {
/* 165 */       return "-∞";
/*     */     }
/*     */     private Object readResolve() {
/* 168 */       return INSTANCE;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.Cut
 * JD-Core Version:    0.6.2
 */