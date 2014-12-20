/*    */ package com.newrelic.javassist.compiler;
/*    */ 
/*    */ class Token
/*    */ {
/* 19 */   public Token next = null;
/*    */   public int tokenId;
/*    */   public long longValue;
/*    */   public double doubleValue;
/*    */   public String textValue;
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.Token
 * JD-Core Version:    0.6.2
 */