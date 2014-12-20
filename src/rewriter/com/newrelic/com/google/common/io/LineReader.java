/*    */ package com.newrelic.com.google.common.io;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
/*    */ import java.nio.CharBuffer;
/*    */ import java.util.LinkedList;
/*    */ import java.util.Queue;
/*    */ 
/*    */ @Beta
/*    */ public final class LineReader
/*    */ {
/*    */   private final Readable readable;
/*    */   private final Reader reader;
/* 41 */   private final char[] buf = new char[4096];
/* 42 */   private final CharBuffer cbuf = CharBuffer.wrap(this.buf);
/*    */ 
/* 44 */   private final Queue<String> lines = new LinkedList();
/* 45 */   private final LineBuffer lineBuf = new LineBuffer() {
/*    */     protected void handleLine(String line, String end) {
/* 47 */       LineReader.this.lines.add(line);
/*    */     }
/* 45 */   };
/*    */ 
/*    */   public LineReader(Readable readable)
/*    */   {
/* 56 */     this.readable = ((Readable)Preconditions.checkNotNull(readable));
/* 57 */     this.reader = ((readable instanceof Reader) ? (Reader)readable : null);
/*    */   }
/*    */ 
/*    */   public String readLine()
/*    */     throws IOException
/*    */   {
/* 72 */     while (this.lines.peek() == null) {
/* 73 */       this.cbuf.clear();
/*    */ 
/* 76 */       int read = this.reader != null ? this.reader.read(this.buf, 0, this.buf.length) : this.readable.read(this.cbuf);
/*    */ 
/* 79 */       if (read == -1) {
/* 80 */         this.lineBuf.finish();
/* 81 */         break;
/*    */       }
/* 83 */       this.lineBuf.add(this.buf, 0, read);
/*    */     }
/* 85 */     return (String)this.lines.poll();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.io.LineReader
 * JD-Core Version:    0.6.2
 */