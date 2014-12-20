/*     */ package com.newrelic.org.apache.commons.io.output;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class TeeOutputStream extends ProxyOutputStream
/*     */ {
/*     */   protected OutputStream branch;
/*     */ 
/*     */   public TeeOutputStream(OutputStream out, OutputStream branch)
/*     */   {
/*  40 */     super(out);
/*  41 */     this.branch = branch;
/*     */   }
/*     */ 
/*     */   public synchronized void write(byte[] b)
/*     */     throws IOException
/*     */   {
/*  51 */     super.write(b);
/*  52 */     this.branch.write(b);
/*     */   }
/*     */ 
/*     */   public synchronized void write(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/*  64 */     super.write(b, off, len);
/*  65 */     this.branch.write(b, off, len);
/*     */   }
/*     */ 
/*     */   public synchronized void write(int b)
/*     */     throws IOException
/*     */   {
/*  75 */     super.write(b);
/*  76 */     this.branch.write(b);
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/*  85 */     super.flush();
/*  86 */     this.branch.flush();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 103 */       super.close();
/*     */     } finally {
/* 105 */       this.branch.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.output.TeeOutputStream
 * JD-Core Version:    0.6.2
 */