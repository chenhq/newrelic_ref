/*    */ package com.newrelic.org.dom4j;
/*    */ 
/*    */ public class InvalidXPathException extends IllegalArgumentException
/*    */ {
/*    */   private static final long serialVersionUID = 3257009869058881592L;
/*    */ 
/*    */   public InvalidXPathException(String xpath)
/*    */   {
/* 23 */     super("Invalid XPath expression: " + xpath);
/*    */   }
/*    */ 
/*    */   public InvalidXPathException(String xpath, String reason) {
/* 27 */     super("Invalid XPath expression: " + xpath + " " + reason);
/*    */   }
/*    */ 
/*    */   public InvalidXPathException(String xpath, Throwable t) {
/* 31 */     super("Invalid XPath expression: '" + xpath + "'. Caused by: " + t.getMessage());
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.InvalidXPathException
 * JD-Core Version:    0.6.2
 */