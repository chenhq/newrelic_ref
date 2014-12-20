/*    */ package com.newrelic.com.google.common.io;
/*    */ 
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
/*    */ import java.util.Iterator;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ class MultiReader extends Reader
/*    */ {
/*    */   private final Iterator<? extends CharSource> it;
/*    */   private Reader current;
/*    */ 
/*    */   MultiReader(Iterator<? extends CharSource> readers)
/*    */     throws IOException
/*    */   {
/* 38 */     this.it = readers;
/* 39 */     advance();
/*    */   }
/*    */ 
/*    */   private void advance()
/*    */     throws IOException
/*    */   {
/* 46 */     close();
/* 47 */     if (this.it.hasNext())
/* 48 */       this.current = ((CharSource)this.it.next()).openStream();
/*    */   }
/*    */ 
/*    */   public int read(@Nullable char[] cbuf, int off, int len) throws IOException
/*    */   {
/* 53 */     if (this.current == null) {
/* 54 */       return -1;
/*    */     }
/* 56 */     int result = this.current.read(cbuf, off, len);
/* 57 */     if (result == -1) {
/* 58 */       advance();
/* 59 */       return read(cbuf, off, len);
/*    */     }
/* 61 */     return result;
/*    */   }
/*    */ 
/*    */   public long skip(long n) throws IOException {
/* 65 */     Preconditions.checkArgument(n >= 0L, "n is negative");
/* 66 */     if (n > 0L) {
/* 67 */       while (this.current != null) {
/* 68 */         long result = this.current.skip(n);
/* 69 */         if (result > 0L) {
/* 70 */           return result;
/*    */         }
/* 72 */         advance();
/*    */       }
/*    */     }
/* 75 */     return 0L;
/*    */   }
/*    */ 
/*    */   public boolean ready() throws IOException {
/* 79 */     return (this.current != null) && (this.current.ready());
/*    */   }
/*    */ 
/*    */   public void close() throws IOException {
/* 83 */     if (this.current != null)
/*    */       try {
/* 85 */         this.current.close();
/*    */       } finally {
/* 87 */         this.current = null;
/*    */       }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.io.MultiReader
 * JD-Core Version:    0.6.2
 */