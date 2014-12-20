/*     */ package com.newrelic.com.google.gson.internal;
/*     */ 
/*     */ import com.newrelic.com.google.gson.JsonElement;
/*     */ import com.newrelic.com.google.gson.JsonIOException;
/*     */ import com.newrelic.com.google.gson.JsonNull;
/*     */ import com.newrelic.com.google.gson.JsonParseException;
/*     */ import com.newrelic.com.google.gson.JsonSyntaxException;
/*     */ import com.newrelic.com.google.gson.TypeAdapter;
/*     */ import com.newrelic.com.google.gson.internal.bind.TypeAdapters;
/*     */ import com.newrelic.com.google.gson.stream.JsonReader;
/*     */ import com.newrelic.com.google.gson.stream.JsonWriter;
/*     */ import com.newrelic.com.google.gson.stream.MalformedJsonException;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ 
/*     */ public final class Streams
/*     */ {
/*     */   public static JsonElement parse(JsonReader reader)
/*     */     throws JsonParseException
/*     */   {
/*  40 */     boolean isEmpty = true;
/*     */     try {
/*  42 */       reader.peek();
/*  43 */       isEmpty = false;
/*  44 */       return (JsonElement)TypeAdapters.JSON_ELEMENT.read(reader);
/*     */     }
/*     */     catch (EOFException e)
/*     */     {
/*  50 */       if (isEmpty) {
/*  51 */         return JsonNull.INSTANCE;
/*     */       }
/*     */ 
/*  54 */       throw new JsonSyntaxException(e);
/*     */     } catch (MalformedJsonException e) {
/*  56 */       throw new JsonSyntaxException(e);
/*     */     } catch (IOException e) {
/*  58 */       throw new JsonIOException(e);
/*     */     } catch (NumberFormatException e) {
/*  60 */       throw new JsonSyntaxException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void write(JsonElement element, JsonWriter writer)
/*     */     throws IOException
/*     */   {
/*  68 */     TypeAdapters.JSON_ELEMENT.write(writer, element);
/*     */   }
/*     */ 
/*     */   public static Writer writerForAppendable(Appendable appendable) {
/*  72 */     return (appendable instanceof Writer) ? (Writer)appendable : new AppendableWriter(appendable, null);
/*     */   }
/*     */ 
/*     */   private static final class AppendableWriter extends Writer
/*     */   {
/*     */     private final Appendable appendable;
/*  81 */     private final CurrentWrite currentWrite = new CurrentWrite();
/*     */ 
/*     */     private AppendableWriter(Appendable appendable) {
/*  84 */       this.appendable = appendable;
/*     */     }
/*     */ 
/*     */     public void write(char[] chars, int offset, int length) throws IOException {
/*  88 */       this.currentWrite.chars = chars;
/*  89 */       this.appendable.append(this.currentWrite, offset, offset + length);
/*     */     }
/*     */ 
/*     */     public void write(int i) throws IOException {
/*  93 */       this.appendable.append((char)i);
/*     */     }
/*     */     public void flush() {
/*     */     }
/*     */ 
/*     */     public void close() {
/*     */     }
/*     */ 
/*     */     static class CurrentWrite implements CharSequence {
/*     */       char[] chars;
/*     */ 
/*     */       public int length() {
/* 105 */         return this.chars.length;
/*     */       }
/*     */       public char charAt(int i) {
/* 108 */         return this.chars[i];
/*     */       }
/*     */       public CharSequence subSequence(int start, int end) {
/* 111 */         return new String(this.chars, start, end - start);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.com.google.gson.internal.Streams
 * JD-Core Version:    0.6.2
 */