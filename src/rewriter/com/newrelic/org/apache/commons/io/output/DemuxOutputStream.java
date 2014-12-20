/*    */ package com.newrelic.org.apache.commons.io.output;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class DemuxOutputStream extends OutputStream
/*    */ {
/* 31 */   private final InheritableThreadLocal<OutputStream> m_streams = new InheritableThreadLocal();
/*    */ 
/*    */   public OutputStream bindStream(OutputStream output)
/*    */   {
/* 41 */     OutputStream stream = (OutputStream)this.m_streams.get();
/* 42 */     this.m_streams.set(output);
/* 43 */     return stream;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 55 */     OutputStream output = (OutputStream)this.m_streams.get();
/* 56 */     if (null != output)
/*    */     {
/* 58 */       output.close();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void flush()
/*    */     throws IOException
/*    */   {
/* 71 */     OutputStream output = (OutputStream)this.m_streams.get();
/* 72 */     if (null != output)
/*    */     {
/* 74 */       output.flush();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void write(int ch)
/*    */     throws IOException
/*    */   {
/* 88 */     OutputStream output = (OutputStream)this.m_streams.get();
/* 89 */     if (null != output)
/*    */     {
/* 91 */       output.write(ch);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.output.DemuxOutputStream
 * JD-Core Version:    0.6.2
 */