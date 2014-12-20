/*    */ package com.newrelic.org.dom4j;
/*    */ 
/*    */ public class IllegalAddException extends IllegalArgumentException
/*    */ {
/*    */   public IllegalAddException(String reason)
/*    */   {
/* 20 */     super(reason);
/*    */   }
/*    */ 
/*    */   public IllegalAddException(Element parent, Node node, String reason) {
/* 24 */     super("The node \"" + node.toString() + "\" could not be added to the element \"" + parent.getQualifiedName() + "\" because: " + reason);
/*    */   }
/*    */ 
/*    */   public IllegalAddException(Branch parent, Node node, String reason)
/*    */   {
/* 30 */     super("The node \"" + node.toString() + "\" could not be added to the branch \"" + parent.getName() + "\" because: " + reason);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.IllegalAddException
 * JD-Core Version:    0.6.2
 */