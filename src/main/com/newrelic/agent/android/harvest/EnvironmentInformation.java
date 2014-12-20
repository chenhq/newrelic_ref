/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ public class EnvironmentInformation
/*    */ {
/*    */   private long memoryUsage;
/*    */   private int orientation;
/*    */   private String networkStatus;
/*    */   private String networkWanType;
/*    */   private long[] diskAvailable;
/*    */ 
/*    */   public EnvironmentInformation()
/*    */   {
/*    */   }
/*    */ 
/*    */   public EnvironmentInformation(long memoryUsage, int orientation, String networkStatus, String networkWanType, long[] diskAvailable)
/*    */   {
/* 17 */     this.memoryUsage = memoryUsage;
/* 18 */     this.orientation = orientation;
/* 19 */     this.networkStatus = networkStatus;
/* 20 */     this.networkWanType = networkWanType;
/* 21 */     this.diskAvailable = diskAvailable;
/*    */   }
/*    */ 
/*    */   public void setMemoryUsage(long memoryUsage) {
/* 25 */     this.memoryUsage = memoryUsage;
/*    */   }
/*    */ 
/*    */   public void setOrientation(int orientation) {
/* 29 */     this.orientation = orientation;
/*    */   }
/*    */ 
/*    */   public void setNetworkStatus(String networkStatus) {
/* 33 */     this.networkStatus = networkStatus;
/*    */   }
/*    */ 
/*    */   public void setNetworkWanType(String networkWanType) {
/* 37 */     this.networkWanType = networkWanType;
/*    */   }
/*    */ 
/*    */   public void setDiskAvailable(long[] diskAvailable) {
/* 41 */     this.diskAvailable = diskAvailable;
/*    */   }
/*    */ 
/*    */   public long getMemoryUsage() {
/* 45 */     return this.memoryUsage;
/*    */   }
/*    */ 
/*    */   public int getOrientation() {
/* 49 */     return this.orientation;
/*    */   }
/*    */ 
/*    */   public String getNetworkStatus() {
/* 53 */     return this.networkStatus;
/*    */   }
/*    */ 
/*    */   public String getNetworkWanType() {
/* 57 */     return this.networkWanType;
/*    */   }
/*    */ 
/*    */   public long[] getDiskAvailable() {
/* 61 */     return this.diskAvailable;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.EnvironmentInformation
 * JD-Core Version:    0.6.2
 */