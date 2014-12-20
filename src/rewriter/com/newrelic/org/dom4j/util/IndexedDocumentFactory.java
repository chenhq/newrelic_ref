/*    */ package com.newrelic.org.dom4j.util;
/*    */ 
/*    */ import com.newrelic.org.dom4j.DocumentFactory;
/*    */ import com.newrelic.org.dom4j.Element;
/*    */ import com.newrelic.org.dom4j.QName;
/*    */ 
/*    */ public class IndexedDocumentFactory extends DocumentFactory
/*    */ {
/* 27 */   protected static transient IndexedDocumentFactory singleton = new IndexedDocumentFactory();
/*    */ 
/*    */   public static DocumentFactory getInstance()
/*    */   {
/* 38 */     return singleton;
/*    */   }
/*    */ 
/*    */   public Element createElement(QName qname)
/*    */   {
/* 44 */     return new IndexedElement(qname);
/*    */   }
/*    */ 
/*    */   public Element createElement(QName qname, int attributeCount) {
/* 48 */     return new IndexedElement(qname, attributeCount);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.util.IndexedDocumentFactory
 * JD-Core Version:    0.6.2
 */