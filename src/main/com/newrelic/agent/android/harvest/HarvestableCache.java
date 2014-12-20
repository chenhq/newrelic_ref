/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.Harvestable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ 
/*    */ public class HarvestableCache
/*    */ {
/*    */   private static final int DEFAULT_CACHE_LIMIT = 1024;
/* 11 */   private int limit = 1024;
/* 12 */   private final Collection<Harvestable> cache = new ArrayList();
/*    */ 
/*    */   public void add(Harvestable harvestable) {
/* 15 */     if ((harvestable == null) || (this.cache.size() >= this.limit))
/* 16 */       return;
/* 17 */     this.cache.add(harvestable);
/*    */   }
/*    */ 
/*    */   public Collection<Harvestable> flush() {
/* 21 */     if (this.cache.size() == 0)
/* 22 */       return Collections.emptyList();
/* 23 */     synchronized (this) {
/* 24 */       Collection oldCache = new ArrayList(this.cache);
/* 25 */       this.cache.clear();
/* 26 */       return oldCache;
/*    */     }
/*    */   }
/*    */ 
/*    */   public int getSize() {
/* 31 */     return this.cache.size();
/*    */   }
/*    */ 
/*    */   public void setLimit(int limit) {
/* 35 */     this.limit = limit;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.HarvestableCache
 * JD-Core Version:    0.6.2
 */