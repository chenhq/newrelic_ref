/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ final class ByteStream extends OutputStream
/*     */ {
/*     */   private byte[] buf;
/*     */   private int count;
/*     */ 
/*     */   public ByteStream()
/*     */   {
/*  25 */     this(32);
/*     */   }
/*     */   public ByteStream(int size) {
/*  28 */     this.buf = new byte[size];
/*  29 */     this.count = 0;
/*     */   }
/*     */   public int getPos() {
/*  32 */     return this.count; } 
/*  33 */   public int size() { return this.count; }
/*     */ 
/*     */   public void writeBlank(int len) {
/*  36 */     enlarge(len);
/*  37 */     this.count += len;
/*     */   }
/*     */ 
/*     */   public void write(byte[] data) {
/*  41 */     write(data, 0, data.length);
/*     */   }
/*     */ 
/*     */   public void write(byte[] data, int off, int len) {
/*  45 */     enlarge(len);
/*  46 */     System.arraycopy(data, off, this.buf, this.count, len);
/*  47 */     this.count += len;
/*     */   }
/*     */ 
/*     */   public void write(int b) {
/*  51 */     enlarge(1);
/*  52 */     int oldCount = this.count;
/*  53 */     this.buf[oldCount] = ((byte)b);
/*  54 */     this.count = (oldCount + 1);
/*     */   }
/*     */ 
/*     */   public void writeShort(int s) {
/*  58 */     enlarge(2);
/*  59 */     int oldCount = this.count;
/*  60 */     this.buf[oldCount] = ((byte)(s >>> 8));
/*  61 */     this.buf[(oldCount + 1)] = ((byte)s);
/*  62 */     this.count = (oldCount + 2);
/*     */   }
/*     */ 
/*     */   public void writeInt(int i) {
/*  66 */     enlarge(4);
/*  67 */     int oldCount = this.count;
/*  68 */     this.buf[oldCount] = ((byte)(i >>> 24));
/*  69 */     this.buf[(oldCount + 1)] = ((byte)(i >>> 16));
/*  70 */     this.buf[(oldCount + 2)] = ((byte)(i >>> 8));
/*  71 */     this.buf[(oldCount + 3)] = ((byte)i);
/*  72 */     this.count = (oldCount + 4);
/*     */   }
/*     */ 
/*     */   public void writeLong(long i) {
/*  76 */     enlarge(8);
/*  77 */     int oldCount = this.count;
/*  78 */     this.buf[oldCount] = ((byte)(int)(i >>> 56));
/*  79 */     this.buf[(oldCount + 1)] = ((byte)(int)(i >>> 48));
/*  80 */     this.buf[(oldCount + 2)] = ((byte)(int)(i >>> 40));
/*  81 */     this.buf[(oldCount + 3)] = ((byte)(int)(i >>> 32));
/*  82 */     this.buf[(oldCount + 4)] = ((byte)(int)(i >>> 24));
/*  83 */     this.buf[(oldCount + 5)] = ((byte)(int)(i >>> 16));
/*  84 */     this.buf[(oldCount + 6)] = ((byte)(int)(i >>> 8));
/*  85 */     this.buf[(oldCount + 7)] = ((byte)(int)i);
/*  86 */     this.count = (oldCount + 8);
/*     */   }
/*     */ 
/*     */   public void writeFloat(float v) {
/*  90 */     writeInt(Float.floatToIntBits(v));
/*     */   }
/*     */ 
/*     */   public void writeDouble(double v) {
/*  94 */     writeLong(Double.doubleToLongBits(v));
/*     */   }
/*     */ 
/*     */   public void writeUTF(String s) {
/*  98 */     int sLen = s.length();
/*  99 */     int pos = this.count;
/* 100 */     enlarge(sLen + 2);
/*     */ 
/* 102 */     byte[] buffer = this.buf;
/* 103 */     buffer[(pos++)] = ((byte)(sLen >>> 8));
/* 104 */     buffer[(pos++)] = ((byte)sLen);
/* 105 */     for (int i = 0; i < sLen; i++) {
/* 106 */       char c = s.charAt(i);
/* 107 */       if (('\001' <= c) && (c <= '')) {
/* 108 */         buffer[(pos++)] = ((byte)c);
/*     */       } else {
/* 110 */         writeUTF2(s, sLen, i);
/* 111 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 115 */     this.count = pos;
/*     */   }
/*     */ 
/*     */   private void writeUTF2(String s, int sLen, int offset) {
/* 119 */     int size = sLen;
/* 120 */     for (int i = offset; i < sLen; i++) {
/* 121 */       int c = s.charAt(i);
/* 122 */       if (c > 2047)
/* 123 */         size += 2;
/* 124 */       else if ((c == 0) || (c > 127)) {
/* 125 */         size++;
/*     */       }
/*     */     }
/* 128 */     if (size > 65535) {
/* 129 */       throw new RuntimeException("encoded string too long: " + sLen + size + " bytes");
/*     */     }
/*     */ 
/* 132 */     enlarge(size + 2);
/* 133 */     int pos = this.count;
/* 134 */     byte[] buffer = this.buf;
/* 135 */     buffer[pos] = ((byte)(size >>> 8));
/* 136 */     buffer[(pos + 1)] = ((byte)size);
/* 137 */     pos += 2 + offset;
/* 138 */     for (int j = offset; j < sLen; j++) {
/* 139 */       int c = s.charAt(j);
/* 140 */       if ((1 <= c) && (c <= 127)) {
/* 141 */         buffer[(pos++)] = ((byte)c);
/* 142 */       } else if (c > 2047) {
/* 143 */         buffer[pos] = ((byte)(0xE0 | c >> 12 & 0xF));
/* 144 */         buffer[(pos + 1)] = ((byte)(0x80 | c >> 6 & 0x3F));
/* 145 */         buffer[(pos + 2)] = ((byte)(0x80 | c & 0x3F));
/* 146 */         pos += 3;
/*     */       }
/*     */       else {
/* 149 */         buffer[pos] = ((byte)(0xC0 | c >> 6 & 0x1F));
/* 150 */         buffer[(pos + 1)] = ((byte)(0x80 | c & 0x3F));
/* 151 */         pos += 2;
/*     */       }
/*     */     }
/*     */ 
/* 155 */     this.count = pos;
/*     */   }
/*     */ 
/*     */   public void write(int pos, int value) {
/* 159 */     this.buf[pos] = ((byte)value);
/*     */   }
/*     */ 
/*     */   public void writeShort(int pos, int value) {
/* 163 */     this.buf[pos] = ((byte)(value >>> 8));
/* 164 */     this.buf[(pos + 1)] = ((byte)value);
/*     */   }
/*     */ 
/*     */   public void writeInt(int pos, int value) {
/* 168 */     this.buf[pos] = ((byte)(value >>> 24));
/* 169 */     this.buf[(pos + 1)] = ((byte)(value >>> 16));
/* 170 */     this.buf[(pos + 2)] = ((byte)(value >>> 8));
/* 171 */     this.buf[(pos + 3)] = ((byte)value);
/*     */   }
/*     */ 
/*     */   public byte[] toByteArray() {
/* 175 */     byte[] buf2 = new byte[this.count];
/* 176 */     System.arraycopy(this.buf, 0, buf2, 0, this.count);
/* 177 */     return buf2;
/*     */   }
/*     */ 
/*     */   public void writeTo(OutputStream out) throws IOException {
/* 181 */     out.write(this.buf, 0, this.count);
/*     */   }
/*     */ 
/*     */   public void enlarge(int delta) {
/* 185 */     int newCount = this.count + delta;
/* 186 */     if (newCount > this.buf.length) {
/* 187 */       int newLen = this.buf.length << 1;
/* 188 */       byte[] newBuf = new byte[newLen > newCount ? newLen : newCount];
/* 189 */       System.arraycopy(this.buf, 0, newBuf, 0, this.count);
/* 190 */       this.buf = newBuf;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ByteStream
 * JD-Core Version:    0.6.2
 */