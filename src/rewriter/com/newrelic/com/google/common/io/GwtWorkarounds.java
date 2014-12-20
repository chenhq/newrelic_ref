/*     */ package com.newrelic.com.google.common.io;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ final class GwtWorkarounds
/*     */ {
/*     */   @GwtIncompatible("Reader")
/*     */   static CharInput asCharInput(Reader reader)
/*     */   {
/*  53 */     Preconditions.checkNotNull(reader);
/*  54 */     return new CharInput()
/*     */     {
/*     */       public int read() throws IOException {
/*  57 */         return this.val$reader.read();
/*     */       }
/*     */ 
/*     */       public void close() throws IOException
/*     */       {
/*  62 */         this.val$reader.close();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   static CharInput asCharInput(CharSequence chars)
/*     */   {
/*  71 */     Preconditions.checkNotNull(chars);
/*  72 */     return new CharInput() {
/*  73 */       int index = 0;
/*     */ 
/*     */       public int read()
/*     */       {
/*  77 */         if (this.index < this.val$chars.length()) {
/*  78 */           return this.val$chars.charAt(this.index++);
/*     */         }
/*  80 */         return -1;
/*     */       }
/*     */ 
/*     */       public void close()
/*     */       {
/*  86 */         this.index = this.val$chars.length();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("InputStream")
/*     */   static InputStream asInputStream(ByteInput input)
/*     */   {
/* 104 */     Preconditions.checkNotNull(input);
/* 105 */     return new InputStream()
/*     */     {
/*     */       public int read() throws IOException {
/* 108 */         return this.val$input.read();
/*     */       }
/*     */ 
/*     */       public int read(byte[] b, int off, int len) throws IOException
/*     */       {
/* 113 */         Preconditions.checkNotNull(b);
/* 114 */         Preconditions.checkPositionIndexes(off, off + len, b.length);
/* 115 */         if (len == 0) {
/* 116 */           return 0;
/*     */         }
/* 118 */         int firstByte = read();
/* 119 */         if (firstByte == -1) {
/* 120 */           return -1;
/*     */         }
/* 122 */         b[off] = ((byte)firstByte);
/* 123 */         for (int dst = 1; dst < len; dst++) {
/* 124 */           int readByte = read();
/* 125 */           if (readByte == -1) {
/* 126 */             return dst;
/*     */           }
/* 128 */           b[(off + dst)] = ((byte)readByte);
/*     */         }
/* 130 */         return len;
/*     */       }
/*     */ 
/*     */       public void close() throws IOException
/*     */       {
/* 135 */         this.val$input.close();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("OutputStream")
/*     */   static OutputStream asOutputStream(ByteOutput output)
/*     */   {
/* 154 */     Preconditions.checkNotNull(output);
/* 155 */     return new OutputStream()
/*     */     {
/*     */       public void write(int b) throws IOException {
/* 158 */         this.val$output.write((byte)b);
/*     */       }
/*     */ 
/*     */       public void flush() throws IOException
/*     */       {
/* 163 */         this.val$output.flush();
/*     */       }
/*     */ 
/*     */       public void close() throws IOException
/*     */       {
/* 168 */         this.val$output.close();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("Writer")
/*     */   static CharOutput asCharOutput(Writer writer)
/*     */   {
/* 187 */     Preconditions.checkNotNull(writer);
/* 188 */     return new CharOutput()
/*     */     {
/*     */       public void write(char c) throws IOException {
/* 191 */         this.val$writer.append(c);
/*     */       }
/*     */ 
/*     */       public void flush() throws IOException
/*     */       {
/* 196 */         this.val$writer.flush();
/*     */       }
/*     */ 
/*     */       public void close() throws IOException
/*     */       {
/* 201 */         this.val$writer.close();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   static CharOutput stringBuilderOutput(int initialSize)
/*     */   {
/* 211 */     StringBuilder builder = new StringBuilder(initialSize);
/* 212 */     return new CharOutput()
/*     */     {
/*     */       public void write(char c)
/*     */       {
/* 216 */         this.val$builder.append(c);
/*     */       }
/*     */ 
/*     */       public void flush()
/*     */       {
/*     */       }
/*     */ 
/*     */       public void close() {
/*     */       }
/*     */ 
/*     */       public String toString() {
/* 227 */         return this.val$builder.toString();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   static abstract interface CharOutput
/*     */   {
/*     */     public abstract void write(char paramChar)
/*     */       throws IOException;
/*     */ 
/*     */     public abstract void flush()
/*     */       throws IOException;
/*     */ 
/*     */     public abstract void close()
/*     */       throws IOException;
/*     */   }
/*     */ 
/*     */   static abstract interface ByteOutput
/*     */   {
/*     */     public abstract void write(byte paramByte)
/*     */       throws IOException;
/*     */ 
/*     */     public abstract void flush()
/*     */       throws IOException;
/*     */ 
/*     */     public abstract void close()
/*     */       throws IOException;
/*     */   }
/*     */ 
/*     */   static abstract interface ByteInput
/*     */   {
/*     */     public abstract int read()
/*     */       throws IOException;
/*     */ 
/*     */     public abstract void close()
/*     */       throws IOException;
/*     */   }
/*     */ 
/*     */   static abstract interface CharInput
/*     */   {
/*     */     public abstract int read()
/*     */       throws IOException;
/*     */ 
/*     */     public abstract void close()
/*     */       throws IOException;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.io.GwtWorkarounds
 * JD-Core Version:    0.6.2
 */