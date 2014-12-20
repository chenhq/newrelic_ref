/*    */ package com.newrelic.org.dom4j.tree;
/*    */ 
/*    */ import com.newrelic.org.dom4j.Element;
/*    */ import com.newrelic.org.dom4j.QName;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ /** @deprecated */
/*    */ public class ElementQNameIterator extends FilterIterator
/*    */ {
/*    */   private QName qName;
/*    */ 
/*    */   public ElementQNameIterator(Iterator proxy, QName qName)
/*    */   {
/* 31 */     super(proxy);
/* 32 */     this.qName = qName;
/*    */   }
/*    */ 
/*    */   protected boolean matches(Object object)
/*    */   {
/* 45 */     if ((object instanceof Element)) {
/* 46 */       Element element = (Element)object;
/*    */ 
/* 48 */       return this.qName.equals(element.getQName());
/*    */     }
/*    */ 
/* 51 */     return false;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.tree.ElementQNameIterator
 * JD-Core Version:    0.6.2
 */