/*    */ package com.newrelic.javassist.bytecode.analysis;
/*    */ 
/*    */ import java.util.NoSuchElementException;
/*    */ 
/*    */ class IntQueue
/*    */ {
/*    */   private Entry head;
/*    */   private Entry tail;
/*    */ 
/*    */   void add(int value)
/*    */   {
/* 32 */     Entry entry = new Entry(value, null);
/* 33 */     if (this.tail != null)
/* 34 */       this.tail.next = entry;
/* 35 */     this.tail = entry;
/*    */ 
/* 37 */     if (this.head == null)
/* 38 */       this.head = entry;
/*    */   }
/*    */ 
/*    */   boolean isEmpty() {
/* 42 */     return this.head == null;
/*    */   }
/*    */ 
/*    */   int take() {
/* 46 */     if (this.head == null) {
/* 47 */       throw new NoSuchElementException();
/*    */     }
/* 49 */     int value = this.head.value;
/* 50 */     this.head = this.head.next;
/* 51 */     if (this.head == null) {
/* 52 */       this.tail = null;
/*    */     }
/* 54 */     return value;
/*    */   }
/*    */ 
/*    */   private static class Entry
/*    */   {
/*    */     private Entry next;
/*    */     private int value;
/*    */ 
/*    */     private Entry(int value)
/*    */     {
/* 24 */       this.value = value;
/*    */     }
/*    */ 
/*    */     Entry(int x0, IntQueue.1 x1)
/*    */     {
/* 20 */       this(x0);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.analysis.IntQueue
 * JD-Core Version:    0.6.2
 */