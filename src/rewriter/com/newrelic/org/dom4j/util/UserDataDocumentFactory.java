/*    */ package com.newrelic.org.dom4j.util;
/*    */ 
/*    */ import com.newrelic.org.dom4j.Attribute;
/*    */ import com.newrelic.org.dom4j.DocumentFactory;
/*    */ import com.newrelic.org.dom4j.Element;
/*    */ import com.newrelic.org.dom4j.QName;
/*    */ 
/*    */ public class UserDataDocumentFactory extends DocumentFactory
/*    */ {
/* 30 */   protected static transient UserDataDocumentFactory singleton = new UserDataDocumentFactory();
/*    */ 
/*    */   public static DocumentFactory getInstance()
/*    */   {
/* 41 */     return singleton;
/*    */   }
/*    */ 
/*    */   public Element createElement(QName qname)
/*    */   {
/* 47 */     return new UserDataElement(qname);
/*    */   }
/*    */ 
/*    */   public Attribute createAttribute(Element owner, QName qname, String value) {
/* 51 */     return new UserDataAttribute(qname, value);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.util.UserDataDocumentFactory
 * JD-Core Version:    0.6.2
 */