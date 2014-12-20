/*    */ package com.newrelic.org.dom4j.tree;
/*    */ 
/*    */ import com.newrelic.org.dom4j.Element;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ /** @deprecated */
/*    */ public class ElementNameIterator extends FilterIterator
/*    */ {
/*    */   private String name;
/*    */ 
/*    */   public ElementNameIterator(Iterator proxy, String name)
/*    */   {
/* 30 */     super(proxy);
/* 31 */     this.name = name;
/*    */   }
/*    */ 
/*    */   protected boolean matches(Object object)
/*    */   {
/* 44 */     if ((object instanceof Element)) {
/* 45 */       Element element = (Element)object;
/*    */ 
/* 47 */       return this.name.equals(element.getName());
/*    */     }
/*    */ 
/* 50 */     return false;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.tree.ElementNameIterator
 * JD-Core Version:    0.6.2
 */