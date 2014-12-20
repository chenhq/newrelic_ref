/*     */ package com.newrelic.org.apache.commons.io.input;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public abstract class ProxyInputStream extends FilterInputStream
/*     */ {
/*     */   public ProxyInputStream(InputStream proxy)
/*     */   {
/*  45 */     super(proxy);
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  57 */       beforeRead(1);
/*  58 */       int b = this.in.read();
/*  59 */       afterRead(b != -1 ? 1 : -1);
/*  60 */       return b;
/*     */     } catch (IOException e) {
/*  62 */       handleIOException(e);
/*  63 */     }return -1;
/*     */   }
/*     */ 
/*     */   public int read(byte[] bts)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  76 */       beforeRead(bts != null ? bts.length : 0);
/*  77 */       int n = this.in.read(bts);
/*  78 */       afterRead(n);
/*  79 */       return n;
/*     */     } catch (IOException e) {
/*  81 */       handleIOException(e);
/*  82 */     }return -1;
/*     */   }
/*     */ 
/*     */   public int read(byte[] bts, int off, int len)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  97 */       beforeRead(len);
/*  98 */       int n = this.in.read(bts, off, len);
/*  99 */       afterRead(n);
/* 100 */       return n;
/*     */     } catch (IOException e) {
/* 102 */       handleIOException(e);
/* 103 */     }return -1;
/*     */   }
/*     */ 
/*     */   public long skip(long ln)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 116 */       return this.in.skip(ln);
/*     */     } catch (IOException e) {
/* 118 */       handleIOException(e);
/* 119 */     }return 0L;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 131 */       return super.available();
/*     */     } catch (IOException e) {
/* 133 */       handleIOException(e);
/* 134 */     }return 0;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 145 */       this.in.close();
/*     */     } catch (IOException e) {
/* 147 */       handleIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void mark(int readlimit)
/*     */   {
/* 157 */     this.in.mark(readlimit);
/*     */   }
/*     */ 
/*     */   public synchronized void reset()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 167 */       this.in.reset();
/*     */     } catch (IOException e) {
/* 169 */       handleIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean markSupported()
/*     */   {
/* 179 */     return this.in.markSupported();
/*     */   }
/*     */ 
/*     */   protected void beforeRead(int n)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void afterRead(int n)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void handleIOException(IOException e)
/*     */     throws IOException
/*     */   {
/* 233 */     throw e;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.input.ProxyInputStream
 * JD-Core Version:    0.6.2
 */