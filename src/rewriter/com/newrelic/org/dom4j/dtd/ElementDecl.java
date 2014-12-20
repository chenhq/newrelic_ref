/*    */ package com.newrelic.org.dom4j.dtd;
/*    */ 
/*    */ public class ElementDecl
/*    */ {
/*    */   private String name;
/*    */   private String model;
/*    */ 
/*    */   public ElementDecl()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ElementDecl(String name, String model)
/*    */   {
/* 29 */     this.name = name;
/* 30 */     this.model = model;
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 39 */     return this.name;
/*    */   }
/*    */ 
/*    */   public void setName(String name)
/*    */   {
/* 49 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public String getModel()
/*    */   {
/* 58 */     return this.model;
/*    */   }
/*    */ 
/*    */   public void setModel(String model)
/*    */   {
/* 68 */     this.model = model;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 72 */     return "<!ELEMENT " + this.name + " " + this.model + ">";
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.dtd.ElementDecl
 * JD-Core Version:    0.6.2
 */