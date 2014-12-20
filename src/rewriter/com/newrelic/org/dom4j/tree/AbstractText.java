/*    */ package com.newrelic.org.dom4j.tree;
/*    */ 
/*    */ import com.newrelic.org.dom4j.Text;
/*    */ import com.newrelic.org.dom4j.Visitor;
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
/*    */ 
/*    */ public abstract class AbstractText extends AbstractCharacterData
/*    */   implements Text
/*    */ {
/*    */   public short getNodeType()
/*    */   {
/* 30 */     return 3;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 34 */     return super.toString() + " [Text: \"" + getText() + "\"]";
/*    */   }
/*    */ 
/*    */   public String asXML() {
/* 38 */     return getText();
/*    */   }
/*    */ 
/*    */   public void write(Writer writer) throws IOException {
/* 42 */     writer.write(getText());
/*    */   }
/*    */ 
/*    */   public void accept(Visitor visitor) {
/* 46 */     visitor.visit(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.tree.AbstractText
 * JD-Core Version:    0.6.2
 */