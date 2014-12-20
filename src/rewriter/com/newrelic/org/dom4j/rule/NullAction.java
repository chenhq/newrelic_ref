/*    */ package com.newrelic.org.dom4j.rule;
/*    */ 
/*    */ import com.newrelic.org.dom4j.Node;
/*    */ 
/*    */ public class NullAction
/*    */   implements Action
/*    */ {
/* 22 */   public static final NullAction SINGLETON = new NullAction();
/*    */ 
/*    */   public void run(Node node)
/*    */     throws Exception
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.rule.NullAction
 * JD-Core Version:    0.6.2
 */