/*    */ package com.newrelic.org.dom4j.util;
/*    */ 
/*    */ import com.newrelic.org.dom4j.QName;
/*    */ import com.newrelic.org.dom4j.tree.DefaultAttribute;
/*    */ 
/*    */ public class UserDataAttribute extends DefaultAttribute
/*    */ {
/*    */   private Object data;
/*    */ 
/*    */   public UserDataAttribute(QName qname)
/*    */   {
/* 30 */     super(qname);
/*    */   }
/*    */ 
/*    */   public UserDataAttribute(QName qname, String text) {
/* 34 */     super(qname, text);
/*    */   }
/*    */ 
/*    */   public Object getData() {
/* 38 */     return this.data;
/*    */   }
/*    */ 
/*    */   public void setData(Object data) {
/* 42 */     this.data = data;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.util.UserDataAttribute
 * JD-Core Version:    0.6.2
 */