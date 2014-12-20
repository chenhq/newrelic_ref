/*    */ package com.newrelic.org.dom4j.rule.pattern;
/*    */ 
/*    */ import com.newrelic.org.dom4j.Node;
/*    */ import com.newrelic.org.dom4j.rule.Pattern;
/*    */ 
/*    */ public class NodeTypePattern
/*    */   implements Pattern
/*    */ {
/* 24 */   public static final NodeTypePattern ANY_ATTRIBUTE = new NodeTypePattern((short)2);
/*    */ 
/* 28 */   public static final NodeTypePattern ANY_COMMENT = new NodeTypePattern((short)8);
/*    */ 
/* 32 */   public static final NodeTypePattern ANY_DOCUMENT = new NodeTypePattern((short)9);
/*    */ 
/* 36 */   public static final NodeTypePattern ANY_ELEMENT = new NodeTypePattern((short)1);
/*    */ 
/* 40 */   public static final NodeTypePattern ANY_PROCESSING_INSTRUCTION = new NodeTypePattern((short)7);
/*    */ 
/* 44 */   public static final NodeTypePattern ANY_TEXT = new NodeTypePattern((short)3);
/*    */   private short nodeType;
/*    */ 
/*    */   public NodeTypePattern(short nodeType)
/*    */   {
/* 50 */     this.nodeType = nodeType;
/*    */   }
/*    */ 
/*    */   public boolean matches(Node node) {
/* 54 */     return node.getNodeType() == this.nodeType;
/*    */   }
/*    */ 
/*    */   public double getPriority() {
/* 58 */     return 0.5D;
/*    */   }
/*    */ 
/*    */   public Pattern[] getUnionPatterns() {
/* 62 */     return null;
/*    */   }
/*    */ 
/*    */   public short getMatchType() {
/* 66 */     return this.nodeType;
/*    */   }
/*    */ 
/*    */   public String getMatchesNodeName() {
/* 70 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.rule.pattern.NodeTypePattern
 * JD-Core Version:    0.6.2
 */