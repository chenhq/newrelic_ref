/*     */ package com.newrelic.com.google.common.io;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Writer;
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ public abstract class ByteSink
/*     */   implements OutputSupplier<OutputStream>
/*     */ {
/*     */   public CharSink asCharSink(Charset charset)
/*     */   {
/*  59 */     return new AsCharSink(charset, null);
/*     */   }
/*     */ 
/*     */   public abstract OutputStream openStream()
/*     */     throws IOException;
/*     */ 
/*     */   @Deprecated
/*     */   public final OutputStream getOutput()
/*     */     throws IOException
/*     */   {
/*  84 */     return openStream();
/*     */   }
/*     */ 
/*     */   public OutputStream openBufferedStream()
/*     */     throws IOException
/*     */   {
/* 100 */     OutputStream out = openStream();
/* 101 */     return (out instanceof BufferedOutputStream) ? (BufferedOutputStream)out : new BufferedOutputStream(out);
/*     */   }
/*     */ 
/*     */   public void write(byte[] bytes)
/*     */     throws IOException
/*     */   {
/* 112 */     Preconditions.checkNotNull(bytes);
/*     */ 
/* 114 */     Closer closer = Closer.create();
/*     */     try {
/* 116 */       OutputStream out = (OutputStream)closer.register(openStream());
/* 117 */       out.write(bytes);
/* 118 */       out.flush();
/*     */     } catch (Throwable e) {
/* 120 */       throw closer.rethrow(e);
/*     */     } finally {
/* 122 */       closer.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public long writeFrom(InputStream input)
/*     */     throws IOException
/*     */   {
/* 134 */     Preconditions.checkNotNull(input);
/*     */ 
/* 136 */     Closer closer = Closer.create();
/*     */     try {
/* 138 */       OutputStream out = (OutputStream)closer.register(openStream());
/* 139 */       long written = ByteStreams.copy(input, out);
/* 140 */       out.flush();
/* 141 */       return written;
/*     */     } catch (Throwable e) {
/* 143 */       throw closer.rethrow(e);
/*     */     } finally {
/* 145 */       closer.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class AsCharSink extends CharSink
/*     */   {
/*     */     private final Charset charset;
/*     */ 
/*     */     private AsCharSink(Charset charset)
/*     */     {
/* 158 */       this.charset = ((Charset)Preconditions.checkNotNull(charset));
/*     */     }
/*     */ 
/*     */     public Writer openStream() throws IOException
/*     */     {
/* 163 */       return new OutputStreamWriter(ByteSink.this.openStream(), this.charset);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 168 */       return ByteSink.this.toString() + ".asCharSink(" + this.charset + ")";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.io.ByteSink
 * JD-Core Version:    0.6.2
 */