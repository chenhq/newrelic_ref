/*     */ package com.newrelic.com.google.common.hash;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Iterator;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ public final class Funnels
/*     */ {
/*     */   public static Funnel<byte[]> byteArrayFunnel()
/*     */   {
/*  40 */     return ByteArrayFunnel.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static Funnel<CharSequence> unencodedCharsFunnel()
/*     */   {
/*  63 */     return UnencodedCharsFunnel.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static Funnel<CharSequence> stringFunnel(Charset charset)
/*     */   {
/*  85 */     return new StringCharsetFunnel(charset);
/*     */   }
/*     */ 
/*     */   public static Funnel<Integer> integerFunnel()
/*     */   {
/* 140 */     return IntegerFunnel.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static <E> Funnel<Iterable<? extends E>> sequentialFunnel(Funnel<E> elementFunnel)
/*     */   {
/* 162 */     return new SequentialFunnel(elementFunnel);
/*     */   }
/*     */ 
/*     */   public static Funnel<Long> longFunnel()
/*     */   {
/* 201 */     return LongFunnel.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static OutputStream asOutputStream(PrimitiveSink sink)
/*     */   {
/* 227 */     return new SinkAsStream(sink);
/*     */   }
/*     */   private static class SinkAsStream extends OutputStream {
/*     */     final PrimitiveSink sink;
/*     */ 
/*     */     SinkAsStream(PrimitiveSink sink) {
/* 233 */       this.sink = ((PrimitiveSink)Preconditions.checkNotNull(sink));
/*     */     }
/*     */ 
/*     */     public void write(int b) {
/* 237 */       this.sink.putByte((byte)b);
/*     */     }
/*     */ 
/*     */     public void write(byte[] bytes) {
/* 241 */       this.sink.putBytes(bytes);
/*     */     }
/*     */ 
/*     */     public void write(byte[] bytes, int off, int len) {
/* 245 */       this.sink.putBytes(bytes, off, len);
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 249 */       return "Funnels.asOutputStream(" + this.sink + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum LongFunnel
/*     */     implements Funnel<Long>
/*     */   {
/* 205 */     INSTANCE;
/*     */ 
/*     */     public void funnel(Long from, PrimitiveSink into) {
/* 208 */       into.putLong(from.longValue());
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 212 */       return "Funnels.longFunnel()";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SequentialFunnel<E>
/*     */     implements Funnel<Iterable<? extends E>>, Serializable
/*     */   {
/*     */     private final Funnel<E> elementFunnel;
/*     */ 
/*     */     SequentialFunnel(Funnel<E> elementFunnel)
/*     */     {
/* 169 */       this.elementFunnel = ((Funnel)Preconditions.checkNotNull(elementFunnel));
/*     */     }
/*     */ 
/*     */     public void funnel(Iterable<? extends E> from, PrimitiveSink into) {
/* 173 */       for (Iterator i$ = from.iterator(); i$.hasNext(); ) { Object e = i$.next();
/* 174 */         this.elementFunnel.funnel(e, into); }
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 179 */       return "Funnels.sequentialFunnel(" + this.elementFunnel + ")";
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object o) {
/* 183 */       if ((o instanceof SequentialFunnel)) {
/* 184 */         SequentialFunnel funnel = (SequentialFunnel)o;
/* 185 */         return this.elementFunnel.equals(funnel.elementFunnel);
/*     */       }
/* 187 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 191 */       return SequentialFunnel.class.hashCode() ^ this.elementFunnel.hashCode();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum IntegerFunnel
/*     */     implements Funnel<Integer>
/*     */   {
/* 144 */     INSTANCE;
/*     */ 
/*     */     public void funnel(Integer from, PrimitiveSink into) {
/* 147 */       into.putInt(from.intValue());
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 151 */       return "Funnels.integerFunnel()";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class StringCharsetFunnel
/*     */     implements Funnel<CharSequence>, Serializable
/*     */   {
/*     */     private final Charset charset;
/*     */ 
/*     */     StringCharsetFunnel(Charset charset)
/*     */     {
/*  92 */       this.charset = ((Charset)Preconditions.checkNotNull(charset));
/*     */     }
/*     */ 
/*     */     public void funnel(CharSequence from, PrimitiveSink into) {
/*  96 */       into.putString(from, this.charset);
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 100 */       return "Funnels.stringFunnel(" + this.charset.name() + ")";
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object o) {
/* 104 */       if ((o instanceof StringCharsetFunnel)) {
/* 105 */         StringCharsetFunnel funnel = (StringCharsetFunnel)o;
/* 106 */         return this.charset.equals(funnel.charset);
/*     */       }
/* 108 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 112 */       return StringCharsetFunnel.class.hashCode() ^ this.charset.hashCode();
/*     */     }
/*     */ 
/*     */     Object writeReplace() {
/* 116 */       return new SerializedForm(this.charset);
/*     */     }
/*     */     private static class SerializedForm implements Serializable {
/*     */       private final String charsetCanonicalName;
/*     */       private static final long serialVersionUID = 0L;
/*     */ 
/* 123 */       SerializedForm(Charset charset) { this.charsetCanonicalName = charset.name(); }
/*     */ 
/*     */       private Object readResolve()
/*     */       {
/* 127 */         return Funnels.stringFunnel(Charset.forName(this.charsetCanonicalName));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum UnencodedCharsFunnel
/*     */     implements Funnel<CharSequence>
/*     */   {
/*  67 */     INSTANCE;
/*     */ 
/*     */     public void funnel(CharSequence from, PrimitiveSink into) {
/*  70 */       into.putUnencodedChars(from);
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  74 */       return "Funnels.unencodedCharsFunnel()";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum ByteArrayFunnel
/*     */     implements Funnel<byte[]>
/*     */   {
/*  44 */     INSTANCE;
/*     */ 
/*     */     public void funnel(byte[] from, PrimitiveSink into) {
/*  47 */       into.putBytes(from);
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  51 */       return "Funnels.byteArrayFunnel()";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.hash.Funnels
 * JD-Core Version:    0.6.2
 */