/*    */ package com.newrelic.org.apache.commons.io.input;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class DemuxInputStream extends InputStream
/*    */ {
/* 31 */   private final InheritableThreadLocal<InputStream> m_streams = new InheritableThreadLocal();
/*    */ 
/*    */   public InputStream bindStream(InputStream input)
/*    */   {
/* 41 */     InputStream oldValue = (InputStream)this.m_streams.get();
/* 42 */     this.m_streams.set(input);
/* 43 */     return oldValue;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 55 */     InputStream input = (InputStream)this.m_streams.get();
/* 56 */     if (null != input)
/*    */     {
/* 58 */       input.close();
/*    */     }
/*    */   }
/*    */ 
/*    */   public int read()
/*    */     throws IOException
/*    */   {
/* 72 */     InputStream input = (InputStream)this.m_streams.get();
/* 73 */     if (null != input)
/*    */     {
/* 75 */       return input.read();
/*    */     }
/*    */ 
/* 79 */     return -1;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.input.DemuxInputStream
 * JD-Core Version:    0.6.2
 */