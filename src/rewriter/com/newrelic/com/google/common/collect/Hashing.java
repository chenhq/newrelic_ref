/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ final class Hashing
/*    */ {
/*    */   private static final int C1 = -862048943;
/*    */   private static final int C2 = 461845907;
/* 54 */   private static int MAX_TABLE_SIZE = 1073741824;
/*    */ 
/*    */   static int smear(int hashCode)
/*    */   {
/* 47 */     return 461845907 * Integer.rotateLeft(hashCode * -862048943, 15);
/*    */   }
/*    */ 
/*    */   static int smearedHash(@Nullable Object o) {
/* 51 */     return smear(o == null ? 0 : o.hashCode());
/*    */   }
/*    */ 
/*    */   static int closedTableSize(int expectedEntries, double loadFactor)
/*    */   {
/* 59 */     expectedEntries = Math.max(expectedEntries, 2);
/* 60 */     int tableSize = Integer.highestOneBit(expectedEntries);
/*    */ 
/* 62 */     if (expectedEntries > (int)(loadFactor * tableSize)) {
/* 63 */       tableSize <<= 1;
/* 64 */       return tableSize > 0 ? tableSize : MAX_TABLE_SIZE;
/*    */     }
/* 66 */     return tableSize;
/*    */   }
/*    */ 
/*    */   static boolean needsResizing(int size, int tableSize, double loadFactor) {
/* 70 */     return (size > loadFactor * tableSize) && (tableSize < MAX_TABLE_SIZE);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.Hashing
 * JD-Core Version:    0.6.2
 */