/*    */ package com.newrelic.org.dom4j.swing;
/*    */ 
/*    */ import com.newrelic.org.dom4j.Document;
/*    */ import javax.swing.tree.DefaultTreeModel;
/*    */ 
/*    */ public class DocumentTreeModel extends DefaultTreeModel
/*    */ {
/*    */   protected Document document;
/*    */ 
/*    */   public DocumentTreeModel(Document document)
/*    */   {
/* 29 */     super(new BranchTreeNode(document));
/* 30 */     this.document = document;
/*    */   }
/*    */ 
/*    */   public Document getDocument()
/*    */   {
/* 43 */     return this.document;
/*    */   }
/*    */ 
/*    */   public void setDocument(Document document)
/*    */   {
/* 54 */     this.document = document;
/* 55 */     setRoot(new BranchTreeNode(document));
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.swing.DocumentTreeModel
 * JD-Core Version:    0.6.2
 */