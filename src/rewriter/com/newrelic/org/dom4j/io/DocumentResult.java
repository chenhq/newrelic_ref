/*    */ package com.newrelic.org.dom4j.io;
/*    */ 
/*    */ import com.newrelic.org.dom4j.Document;
/*    */ import javax.xml.transform.sax.SAXResult;
/*    */ import org.xml.sax.ContentHandler;
/*    */ import org.xml.sax.ext.LexicalHandler;
/*    */ 
/*    */ public class DocumentResult extends SAXResult
/*    */ {
/*    */   private SAXContentHandler contentHandler;
/*    */ 
/*    */   public DocumentResult()
/*    */   {
/* 30 */     this(new SAXContentHandler());
/*    */   }
/*    */ 
/*    */   public DocumentResult(SAXContentHandler contentHandler) {
/* 34 */     this.contentHandler = contentHandler;
/* 35 */     super.setHandler(this.contentHandler);
/* 36 */     super.setLexicalHandler(this.contentHandler);
/*    */   }
/*    */ 
/*    */   public Document getDocument()
/*    */   {
/* 45 */     return this.contentHandler.getDocument();
/*    */   }
/*    */ 
/*    */   public void setHandler(ContentHandler handler)
/*    */   {
/* 51 */     if ((handler instanceof SAXContentHandler)) {
/* 52 */       this.contentHandler = ((SAXContentHandler)handler);
/* 53 */       super.setHandler(this.contentHandler);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setLexicalHandler(LexicalHandler handler) {
/* 58 */     if ((handler instanceof SAXContentHandler)) {
/* 59 */       this.contentHandler = ((SAXContentHandler)handler);
/* 60 */       super.setLexicalHandler(this.contentHandler);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.io.DocumentResult
 * JD-Core Version:    0.6.2
 */