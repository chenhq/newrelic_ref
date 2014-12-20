/*    */ package com.newrelic.org.dom4j.tree;
/*    */ 
/*    */ import com.newrelic.org.dom4j.CharacterData;
/*    */ import com.newrelic.org.dom4j.Element;
/*    */ 
/*    */ public abstract class AbstractCharacterData extends AbstractNode
/*    */   implements CharacterData
/*    */ {
/*    */   public String getPath(Element context)
/*    */   {
/* 28 */     Element parent = getParent();
/*    */ 
/* 30 */     return (parent != null) && (parent != context) ? parent.getPath(context) + "/text()" : "text()";
/*    */   }
/*    */ 
/*    */   public String getUniquePath(Element context)
/*    */   {
/* 35 */     Element parent = getParent();
/*    */ 
/* 37 */     return (parent != null) && (parent != context) ? parent.getUniquePath(context) + "/text()" : "text()";
/*    */   }
/*    */ 
/*    */   public void appendText(String text)
/*    */   {
/* 42 */     setText(getText() + text);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.tree.AbstractCharacterData
 * JD-Core Version:    0.6.2
 */