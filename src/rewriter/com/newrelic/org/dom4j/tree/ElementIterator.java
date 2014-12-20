/*    */ package com.newrelic.org.dom4j.tree;
/*    */ 
/*    */ import com.newrelic.org.dom4j.Element;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ /** @deprecated */
/*    */ public class ElementIterator extends FilterIterator
/*    */ {
/*    */   public ElementIterator(Iterator proxy)
/*    */   {
/* 27 */     super(proxy);
/*    */   }
/*    */ 
/*    */   protected boolean matches(Object element)
/*    */   {
/* 40 */     return element instanceof Element;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.tree.ElementIterator
 * JD-Core Version:    0.6.2
 */