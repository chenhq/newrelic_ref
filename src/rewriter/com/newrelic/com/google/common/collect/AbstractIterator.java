/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class AbstractIterator<T> extends UnmodifiableIterator<T>
/*     */ {
/*  65 */   private State state = State.NOT_READY;
/*     */   private T next;
/*     */ 
/*     */   protected abstract T computeNext();
/*     */ 
/*     */   protected final T endOfData()
/*     */   {
/* 124 */     this.state = State.DONE;
/* 125 */     return null;
/*     */   }
/*     */ 
/*     */   public final boolean hasNext()
/*     */   {
/* 130 */     Preconditions.checkState(this.state != State.FAILED);
/* 131 */     switch (1.$SwitchMap$com$google$common$collect$AbstractIterator$State[this.state.ordinal()]) {
/*     */     case 1:
/* 133 */       return false;
/*     */     case 2:
/* 135 */       return true;
/*     */     }
/*     */ 
/* 138 */     return tryToComputeNext();
/*     */   }
/*     */ 
/*     */   private boolean tryToComputeNext() {
/* 142 */     this.state = State.FAILED;
/* 143 */     this.next = computeNext();
/* 144 */     if (this.state != State.DONE) {
/* 145 */       this.state = State.READY;
/* 146 */       return true;
/*     */     }
/* 148 */     return false;
/*     */   }
/*     */ 
/*     */   public final T next()
/*     */   {
/* 153 */     if (!hasNext()) {
/* 154 */       throw new NoSuchElementException();
/*     */     }
/* 156 */     this.state = State.NOT_READY;
/* 157 */     Object result = this.next;
/* 158 */     this.next = null;
/* 159 */     return result;
/*     */   }
/*     */ 
/*     */   public final T peek()
/*     */   {
/* 170 */     if (!hasNext()) {
/* 171 */       throw new NoSuchElementException();
/*     */     }
/* 173 */     return this.next;
/*     */   }
/*     */ 
/*     */   private static enum State
/*     */   {
/*  72 */     READY, 
/*     */ 
/*  75 */     NOT_READY, 
/*     */ 
/*  78 */     DONE, 
/*     */ 
/*  81 */     FAILED;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.AbstractIterator
 * JD-Core Version:    0.6.2
 */