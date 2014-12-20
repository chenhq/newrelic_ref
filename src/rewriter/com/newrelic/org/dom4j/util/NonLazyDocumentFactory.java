/*    */ package com.newrelic.org.dom4j.util;
/*    */ 
/*    */ import com.newrelic.org.dom4j.DocumentFactory;
/*    */ import com.newrelic.org.dom4j.Element;
/*    */ import com.newrelic.org.dom4j.QName;
/*    */ 
/*    */ public class NonLazyDocumentFactory extends DocumentFactory
/*    */ {
/* 27 */   protected static transient NonLazyDocumentFactory singleton = new NonLazyDocumentFactory();
/*    */ 
/*    */   public static DocumentFactory getInstance()
/*    */   {
/* 38 */     return singleton;
/*    */   }
/*    */ 
/*    */   public Element createElement(QName qname)
/*    */   {
/* 44 */     return new NonLazyElement(qname);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.util.NonLazyDocumentFactory
 * JD-Core Version:    0.6.2
 */