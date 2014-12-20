/*    */ package com.newrelic.org.dom4j.tree;
/*    */ 
/*    */ import com.newrelic.org.dom4j.CDATA;
/*    */ import com.newrelic.org.dom4j.Element;
/*    */ import com.newrelic.org.dom4j.Node;
/*    */ 
/*    */ public class FlyweightCDATA extends AbstractCDATA
/*    */   implements CDATA
/*    */ {
/*    */   protected String text;
/*    */ 
/*    */   public FlyweightCDATA(String text)
/*    */   {
/* 39 */     this.text = text;
/*    */   }
/*    */ 
/*    */   public String getText() {
/* 43 */     return this.text;
/*    */   }
/*    */ 
/*    */   protected Node createXPathResult(Element parent) {
/* 47 */     return new DefaultCDATA(parent, getText());
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.tree.FlyweightCDATA
 * JD-Core Version:    0.6.2
 */