/*    */ package com.newrelic.org.dom4j.util;
/*    */ 
/*    */ import com.newrelic.org.dom4j.Attribute;
/*    */ import com.newrelic.org.dom4j.Element;
/*    */ import com.newrelic.org.dom4j.QName;
/*    */ 
/*    */ public class AttributeHelper
/*    */ {
/*    */   public static boolean booleanValue(Element element, String attributeName)
/*    */   {
/* 28 */     return booleanValue(element.attribute(attributeName));
/*    */   }
/*    */ 
/*    */   public static boolean booleanValue(Element element, QName attributeQName) {
/* 32 */     return booleanValue(element.attribute(attributeQName));
/*    */   }
/*    */ 
/*    */   protected static boolean booleanValue(Attribute attribute) {
/* 36 */     if (attribute == null) {
/* 37 */       return false;
/*    */     }
/*    */ 
/* 40 */     Object value = attribute.getData();
/*    */ 
/* 42 */     if (value == null)
/* 43 */       return false;
/* 44 */     if ((value instanceof Boolean)) {
/* 45 */       Boolean b = (Boolean)value;
/*    */ 
/* 47 */       return b.booleanValue();
/*    */     }
/* 49 */     return "true".equalsIgnoreCase(value.toString());
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.util.AttributeHelper
 * JD-Core Version:    0.6.2
 */