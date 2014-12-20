/*   */ package com.newrelic.agent.android.util;
/*   */ 
/*   */ import android.util.Base64;
/*   */ 
/*   */ public class AndroidEncoder
/*   */   implements Encoder
/*   */ {
/*   */   public String encode(byte[] bytes)
/*   */   {
/* 8 */     return Base64.encodeToString(bytes, 0);
/*   */   }
/*   */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.util.AndroidEncoder
 * JD-Core Version:    0.6.2
 */