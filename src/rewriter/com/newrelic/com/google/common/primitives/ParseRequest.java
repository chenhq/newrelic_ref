/*    */ package com.newrelic.com.google.common.primitives;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @GwtCompatible
/*    */ final class ParseRequest
/*    */ {
/*    */   final String rawValue;
/*    */   final int radix;
/*    */ 
/*    */   private ParseRequest(String rawValue, int radix)
/*    */   {
/* 28 */     this.rawValue = rawValue;
/* 29 */     this.radix = radix;
/*    */   }
/*    */ 
/*    */   static ParseRequest fromString(String stringValue) {
/* 33 */     if (stringValue.length() == 0) {
/* 34 */       throw new NumberFormatException("empty string");
/*    */     }
/*    */ 
/* 40 */     char firstChar = stringValue.charAt(0);
/*    */     int radix;
/*    */     String rawValue;
/*    */     int radix;
/* 41 */     if ((stringValue.startsWith("0x")) || (stringValue.startsWith("0X"))) {
/* 42 */       String rawValue = stringValue.substring(2);
/* 43 */       radix = 16;
/*    */     }
/*    */     else
/*    */     {
/*    */       int radix;
/* 44 */       if (firstChar == '#') {
/* 45 */         String rawValue = stringValue.substring(1);
/* 46 */         radix = 16;
/*    */       }
/*    */       else
/*    */       {
/*    */         int radix;
/* 47 */         if ((firstChar == '0') && (stringValue.length() > 1)) {
/* 48 */           String rawValue = stringValue.substring(1);
/* 49 */           radix = 8;
/*    */         } else {
/* 51 */           rawValue = stringValue;
/* 52 */           radix = 10;
/*    */         }
/*    */       }
/*    */     }
/* 55 */     return new ParseRequest(rawValue, radix);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.primitives.ParseRequest
 * JD-Core Version:    0.6.2
 */