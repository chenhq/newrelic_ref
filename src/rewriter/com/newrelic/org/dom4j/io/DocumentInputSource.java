/*     */ package com.newrelic.org.dom4j.io;
/*     */ 
/*     */ import com.newrelic.org.dom4j.Document;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import org.xml.sax.InputSource;
/*     */ 
/*     */ class DocumentInputSource extends InputSource
/*     */ {
/*     */   private Document document;
/*     */ 
/*     */   public DocumentInputSource()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DocumentInputSource(Document document)
/*     */   {
/*  36 */     this.document = document;
/*  37 */     setSystemId(document.getName());
/*     */   }
/*     */ 
/*     */   public Document getDocument()
/*     */   {
/*  49 */     return this.document;
/*     */   }
/*     */ 
/*     */   public void setDocument(Document document)
/*     */   {
/*  59 */     this.document = document;
/*  60 */     setSystemId(document.getName());
/*     */   }
/*     */ 
/*     */   public void setCharacterStream(Reader characterStream)
/*     */     throws UnsupportedOperationException
/*     */   {
/*  78 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Reader getCharacterStream()
/*     */   {
/*     */     try
/*     */     {
/*  90 */       StringWriter out = new StringWriter();
/*  91 */       XMLWriter writer = new XMLWriter(out);
/*  92 */       writer.write(this.document);
/*  93 */       writer.flush();
/*     */ 
/*  95 */       return new StringReader(out.toString());
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 100 */       return new Reader() {
/*     */         private final IOException val$e;
/*     */ 
/* 103 */         public int read(char[] ch, int offset, int length) throws IOException { throw this.val$e; }
/*     */ 
/*     */ 
/*     */         public void close()
/*     */           throws IOException
/*     */         {
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.io.DocumentInputSource
 * JD-Core Version:    0.6.2
 */