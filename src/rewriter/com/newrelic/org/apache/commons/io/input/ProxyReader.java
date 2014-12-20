/*     */ package com.newrelic.org.apache.commons.io.input;
/*     */ 
/*     */ import java.io.FilterReader;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.nio.CharBuffer;
/*     */ 
/*     */ public abstract class ProxyReader extends FilterReader
/*     */ {
/*     */   public ProxyReader(Reader proxy)
/*     */   {
/*  43 */     super(proxy);
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  55 */       beforeRead(1);
/*  56 */       int c = this.in.read();
/*  57 */       afterRead(c != -1 ? 1 : -1);
/*  58 */       return c;
/*     */     } catch (IOException e) {
/*  60 */       handleIOException(e);
/*  61 */     }return -1;
/*     */   }
/*     */ 
/*     */   public int read(char[] chr)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  74 */       beforeRead(chr != null ? chr.length : 0);
/*  75 */       int n = this.in.read(chr);
/*  76 */       afterRead(n);
/*  77 */       return n;
/*     */     } catch (IOException e) {
/*  79 */       handleIOException(e);
/*  80 */     }return -1;
/*     */   }
/*     */ 
/*     */   public int read(char[] chr, int st, int len)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  95 */       beforeRead(len);
/*  96 */       int n = this.in.read(chr, st, len);
/*  97 */       afterRead(n);
/*  98 */       return n;
/*     */     } catch (IOException e) {
/* 100 */       handleIOException(e);
/* 101 */     }return -1;
/*     */   }
/*     */ 
/*     */   public int read(CharBuffer target)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 115 */       beforeRead(target != null ? target.length() : 0);
/* 116 */       int n = this.in.read(target);
/* 117 */       afterRead(n);
/* 118 */       return n;
/*     */     } catch (IOException e) {
/* 120 */       handleIOException(e);
/* 121 */     }return -1;
/*     */   }
/*     */ 
/*     */   public long skip(long ln)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 134 */       return this.in.skip(ln);
/*     */     } catch (IOException e) {
/* 136 */       handleIOException(e);
/* 137 */     }return 0L;
/*     */   }
/*     */ 
/*     */   public boolean ready()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 149 */       return this.in.ready();
/*     */     } catch (IOException e) {
/* 151 */       handleIOException(e);
/* 152 */     }return false;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 163 */       this.in.close();
/*     */     } catch (IOException e) {
/* 165 */       handleIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void mark(int idx)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 177 */       this.in.mark(idx);
/*     */     } catch (IOException e) {
/* 179 */       handleIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void reset()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 190 */       this.in.reset();
/*     */     } catch (IOException e) {
/* 192 */       handleIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean markSupported()
/*     */   {
/* 202 */     return this.in.markSupported();
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
/* 256 */     throw e;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.input.ProxyReader
 * JD-Core Version:    0.6.2
 */