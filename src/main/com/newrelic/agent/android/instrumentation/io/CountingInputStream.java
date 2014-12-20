/*     */ package com.newrelic.agent.android.instrumentation.io;
/*     */ 
/*     */ import com.newrelic.agent.android.Agent;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ public final class CountingInputStream extends InputStream
/*     */   implements StreamCompleteListenerSource
/*     */ {
/*     */   private final InputStream impl;
/*  15 */   private long count = 0L;
/*  16 */   private final StreamCompleteListenerManager listenerManager = new StreamCompleteListenerManager();
/*     */   private final ByteBuffer buffer;
/*  18 */   private boolean enableBuffering = false;
/*     */ 
/*  20 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */ 
/*     */   public CountingInputStream(InputStream impl) {
/*  23 */     this.impl = impl;
/*     */ 
/*  25 */     if (this.enableBuffering) {
/*  26 */       this.buffer = ByteBuffer.allocate(Agent.getResponseBodyLimit());
/*  27 */       fillBuffer();
/*     */     } else {
/*  29 */       this.buffer = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public CountingInputStream(InputStream impl, boolean enableBuffering) {
/*  34 */     this.impl = impl;
/*  35 */     this.enableBuffering = enableBuffering;
/*     */ 
/*  37 */     if (enableBuffering) {
/*  38 */       this.buffer = ByteBuffer.allocate(Agent.getResponseBodyLimit());
/*  39 */       fillBuffer();
/*     */     } else {
/*  41 */       this.buffer = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addStreamCompleteListener(StreamCompleteListener streamCompleteListener) {
/*  46 */     this.listenerManager.addStreamCompleteListener(streamCompleteListener);
/*     */   }
/*     */ 
/*     */   public void removeStreamCompleteListener(StreamCompleteListener streamCompleteListener) {
/*  50 */     this.listenerManager.removeStreamCompleteListener(streamCompleteListener);
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  57 */     if (this.enableBuffering) {
/*  58 */       synchronized (this.buffer) {
/*  59 */         if (bufferHasBytes(1L)) {
/*  60 */           int n = readBuffer();
/*  61 */           if (n >= 0) {
/*  62 */             this.count += 1L;
/*     */           }
/*  64 */           return n;
/*     */         }
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/*  70 */       int n = this.impl.read();
/*  71 */       if (n >= 0)
/*  72 */         this.count += 1L;
/*     */       else {
/*  74 */         notifyStreamComplete();
/*     */       }
/*  76 */       return n;
/*     */     } catch (IOException e) {
/*  78 */       notifyStreamError(e);
/*  79 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int read(byte[] b) throws IOException
/*     */   {
/*  85 */     int n = 0;
/*  86 */     int numBytesFromBuffer = 0;
/*  87 */     int inputBufferRemaining = b.length;
/*     */ 
/*  90 */     if (this.enableBuffering) {
/*  91 */       synchronized (this.buffer)
/*     */       {
/*  93 */         if (bufferHasBytes(inputBufferRemaining)) {
/*  94 */           n = readBufferBytes(b);
/*  95 */           if (n >= 0) {
/*  96 */             this.count += n;
/*     */           }
/*     */           else {
/*  99 */             throw new IOException("readBufferBytes failed");
/*     */           }
/* 101 */           return n;
/*     */         }
/*     */ 
/* 104 */         int remaining = this.buffer.remaining();
/* 105 */         if (remaining > 0)
/*     */         {
/* 107 */           numBytesFromBuffer = readBufferBytes(b, 0, remaining);
/* 108 */           if (numBytesFromBuffer < 0)
/* 109 */             throw new IOException("partial read from buffer failed");
/* 110 */           inputBufferRemaining -= numBytesFromBuffer;
/* 111 */           this.count += numBytesFromBuffer;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 118 */       n = this.impl.read(b, numBytesFromBuffer, inputBufferRemaining);
/* 119 */       if (n >= 0) {
/* 120 */         this.count += n;
/* 121 */         return n + numBytesFromBuffer;
/*     */       }
/* 123 */       if (numBytesFromBuffer <= 0) {
/* 124 */         notifyStreamComplete();
/* 125 */         return n;
/*     */       }
/* 127 */       return numBytesFromBuffer;
/*     */     }
/*     */     catch (IOException e) {
/* 130 */       log.error(e.toString());
/* 131 */       System.out.println("NOTIFY STREAM ERROR: " + e);
/* 132 */       e.printStackTrace();
/* 133 */       notifyStreamError(e);
/* 134 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 139 */     int n = 0;
/* 140 */     int numBytesFromBuffer = 0;
/* 141 */     int inputBufferRemaining = len;
/*     */ 
/* 143 */     if (this.enableBuffering) {
/* 144 */       synchronized (this.buffer)
/*     */       {
/* 146 */         if (bufferHasBytes(inputBufferRemaining)) {
/* 147 */           n = readBufferBytes(b, off, len);
/* 148 */           if (n >= 0) {
/* 149 */             this.count += n;
/*     */           }
/*     */           else {
/* 152 */             throw new IOException("readBufferBytes failed");
/*     */           }
/* 154 */           return n;
/*     */         }
/*     */ 
/* 157 */         int remaining = this.buffer.remaining();
/* 158 */         if (remaining > 0)
/*     */         {
/* 160 */           numBytesFromBuffer = readBufferBytes(b, off, remaining);
/* 161 */           if (numBytesFromBuffer < 0)
/* 162 */             throw new IOException("partial read from buffer failed");
/* 163 */           inputBufferRemaining -= numBytesFromBuffer;
/* 164 */           this.count += numBytesFromBuffer;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 171 */       n = this.impl.read(b, off + numBytesFromBuffer, inputBufferRemaining);
/* 172 */       if (n >= 0) {
/* 173 */         this.count += n;
/* 174 */         return n + numBytesFromBuffer;
/*     */       }
/* 176 */       if (numBytesFromBuffer <= 0) {
/* 177 */         notifyStreamComplete();
/* 178 */         return n;
/*     */       }
/* 180 */       return numBytesFromBuffer;
/*     */     }
/*     */     catch (IOException e) {
/* 183 */       notifyStreamError(e);
/* 184 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long skip(long byteCount) throws IOException {
/* 189 */     long toSkip = byteCount;
/*     */ 
/* 191 */     if (this.enableBuffering) {
/* 192 */       synchronized (this.buffer) {
/* 193 */         if (bufferHasBytes(byteCount)) {
/* 194 */           this.buffer.position((int)byteCount);
/* 195 */           this.count += byteCount;
/* 196 */           return byteCount;
/*     */         }
/*     */ 
/* 199 */         toSkip = byteCount - this.buffer.remaining();
/* 200 */         if (toSkip > 0L)
/* 201 */           this.buffer.position(this.buffer.remaining());
/*     */         else {
/* 203 */           throw new IOException("partial read from buffer (skip) failed");
/*     */         }
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 209 */       long n = this.impl.skip(toSkip);
/* 210 */       this.count += n;
/* 211 */       return n;
/*     */     } catch (IOException e) {
/* 213 */       notifyStreamError(e);
/* 214 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int available() throws IOException {
/* 219 */     int remaining = 0;
/*     */ 
/* 221 */     if (this.enableBuffering) {
/* 222 */       remaining = this.buffer.remaining();
/*     */     }
/*     */     try
/*     */     {
/* 226 */       return remaining + this.impl.available();
/*     */     } catch (IOException e) {
/* 228 */       notifyStreamError(e);
/* 229 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close() throws IOException {
/*     */     try {
/* 235 */       this.impl.close();
/* 236 */       notifyStreamComplete();
/*     */     } catch (IOException e) {
/* 238 */       notifyStreamError(e);
/* 239 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mark(int readlimit) {
/* 244 */     if (!markSupported())
/* 245 */       return;
/* 246 */     this.impl.mark(readlimit);
/*     */   }
/*     */ 
/*     */   public boolean markSupported() {
/* 250 */     return this.impl.markSupported();
/*     */   }
/*     */ 
/*     */   public void reset() throws IOException {
/* 254 */     if (!markSupported())
/* 255 */       return;
/*     */     try
/*     */     {
/* 258 */       this.impl.reset();
/*     */     } catch (IOException e) {
/* 260 */       notifyStreamError(e);
/* 261 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   private int readBuffer() {
/* 266 */     if (bufferEmpty())
/* 267 */       return -1;
/* 268 */     return this.buffer.get();
/*     */   }
/*     */ 
/*     */   private int readBufferBytes(byte[] bytes) {
/* 272 */     return readBufferBytes(bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */   private int readBufferBytes(byte[] bytes, int offset, int length) {
/* 276 */     if (bufferEmpty()) {
/* 277 */       return -1;
/*     */     }
/* 279 */     int remainingBefore = this.buffer.remaining();
/* 280 */     this.buffer.get(bytes, offset, length);
/* 281 */     return remainingBefore - this.buffer.remaining();
/*     */   }
/*     */ 
/*     */   private boolean bufferHasBytes(long num) {
/* 285 */     return this.buffer.remaining() >= num;
/*     */   }
/*     */ 
/*     */   private boolean bufferEmpty() {
/* 289 */     if (this.buffer.hasRemaining()) {
/* 290 */       return false;
/*     */     }
/* 292 */     return true;
/*     */   }
/*     */ 
/*     */   public void fillBuffer() {
/* 296 */     if (this.buffer != null) {
/* 297 */       if (!this.buffer.hasArray()) {
/* 298 */         return;
/*     */       }
/* 300 */       synchronized (this.buffer) {
/* 301 */         int bytesRead = 0;
/*     */         try {
/* 303 */           bytesRead = this.impl.read(this.buffer.array(), 0, this.buffer.capacity());
/*     */         } catch (IOException e) {
/* 305 */           log.error(e.toString());
/*     */         }
/* 307 */         if (bytesRead <= 0) {
/* 308 */           this.buffer.limit(0);
/*     */         }
/* 310 */         else if (bytesRead < this.buffer.capacity())
/* 311 */           this.buffer.limit(bytesRead);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void notifyStreamComplete()
/*     */   {
/* 319 */     if (!this.listenerManager.isComplete())
/* 320 */       this.listenerManager.notifyStreamComplete(new StreamCompleteEvent(this, this.count));
/*     */   }
/*     */ 
/*     */   private void notifyStreamError(Exception e)
/*     */   {
/* 325 */     if (!this.listenerManager.isComplete())
/* 326 */       this.listenerManager.notifyStreamError(new StreamCompleteEvent(this, this.count, e));
/*     */   }
/*     */ 
/*     */   public void setBufferingEnabled(boolean enableBuffering)
/*     */   {
/* 331 */     this.enableBuffering = enableBuffering;
/*     */   }
/*     */ 
/*     */   public String getBufferAsString() {
/* 335 */     if (this.buffer != null) {
/* 336 */       synchronized (this.buffer) {
/* 337 */         byte[] buf = new byte[this.buffer.limit()];
/* 338 */         for (int i = 0; i < this.buffer.limit(); i++) {
/* 339 */           buf[i] = this.buffer.get(i);
/*     */         }
/* 341 */         return new String(buf);
/*     */       }
/*     */     }
/* 344 */     return "";
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.io.CountingInputStream
 * JD-Core Version:    0.6.2
 */