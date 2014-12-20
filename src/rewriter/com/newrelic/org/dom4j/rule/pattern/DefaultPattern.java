/*    */ package com.newrelic.org.dom4j.rule.pattern;
/*    */ 
/*    */ import com.newrelic.org.dom4j.Node;
/*    */ import com.newrelic.org.dom4j.NodeFilter;
/*    */ import com.newrelic.org.dom4j.rule.Pattern;
/*    */ 
/*    */ public class DefaultPattern
/*    */   implements Pattern
/*    */ {
/*    */   private NodeFilter filter;
/*    */ 
/*    */   public DefaultPattern(NodeFilter filter)
/*    */   {
/* 31 */     this.filter = filter;
/*    */   }
/*    */ 
/*    */   public boolean matches(Node node) {
/* 35 */     return this.filter.matches(node);
/*    */   }
/*    */ 
/*    */   public double getPriority() {
/* 39 */     return 0.5D;
/*    */   }
/*    */ 
/*    */   public Pattern[] getUnionPatterns() {
/* 43 */     return null;
/*    */   }
/*    */ 
/*    */   public short getMatchType() {
/* 47 */     return 0;
/*    */   }
/*    */ 
/*    */   public String getMatchesNodeName() {
/* 51 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.rule.pattern.DefaultPattern
 * JD-Core Version:    0.6.2
 */