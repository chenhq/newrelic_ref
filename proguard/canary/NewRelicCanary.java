/*    */ package proguard.canary;
/*    */ 
/*    */ public class NewRelicCanary
/*    */ {
/*    */   private String sound;
/*    */ 
/*    */   public NewRelicCanary(String sound)
/*    */   {
/* 10 */     this.sound = sound;
/*    */   }
/*    */ 
/*    */   public static void canaryMethod() {
/* 14 */     NewRelicCanary canary = new NewRelicCanary("tweet!");
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     proguard.canary.NewRelicCanary
 * JD-Core Version:    0.6.2
 */