/*    */ package com.newrelic.org.dom4j.bean;
/*    */ 
/*    */ import com.newrelic.org.dom4j.Element;
/*    */ import com.newrelic.org.dom4j.QName;
/*    */ import com.newrelic.org.dom4j.tree.AbstractAttribute;
/*    */ 
/*    */ public class BeanAttribute extends AbstractAttribute
/*    */ {
/*    */   private final BeanAttributeList beanList;
/*    */   private final int index;
/*    */ 
/*    */   public BeanAttribute(BeanAttributeList beanList, int index)
/*    */   {
/* 31 */     this.beanList = beanList;
/* 32 */     this.index = index;
/*    */   }
/*    */ 
/*    */   public QName getQName() {
/* 36 */     return this.beanList.getQName(this.index);
/*    */   }
/*    */ 
/*    */   public Element getParent() {
/* 40 */     return this.beanList.getParent();
/*    */   }
/*    */ 
/*    */   public String getValue() {
/* 44 */     Object data = getData();
/*    */ 
/* 46 */     return data != null ? data.toString() : null;
/*    */   }
/*    */ 
/*    */   public void setValue(String data) {
/* 50 */     this.beanList.setData(this.index, data);
/*    */   }
/*    */ 
/*    */   public Object getData() {
/* 54 */     return this.beanList.getData(this.index);
/*    */   }
/*    */ 
/*    */   public void setData(Object data) {
/* 58 */     this.beanList.setData(this.index, data);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.bean.BeanAttribute
 * JD-Core Version:    0.6.2
 */