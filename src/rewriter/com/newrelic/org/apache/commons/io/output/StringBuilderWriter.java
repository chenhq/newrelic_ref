/*     */ package com.newrelic.org.apache.commons.io.output;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.io.Writer;
/*     */ 
/*     */ public class StringBuilderWriter extends Writer
/*     */   implements Serializable
/*     */ {
/*     */   private final StringBuilder builder;
/*     */ 
/*     */   public StringBuilderWriter()
/*     */   {
/*  42 */     this.builder = new StringBuilder();
/*     */   }
/*     */ 
/*     */   public StringBuilderWriter(int capacity)
/*     */   {
/*  51 */     this.builder = new StringBuilder(capacity);
/*     */   }
/*     */ 
/*     */   public StringBuilderWriter(StringBuilder builder)
/*     */   {
/*  60 */     this.builder = (builder != null ? builder : new StringBuilder());
/*     */   }
/*     */ 
/*     */   public Writer append(char value)
/*     */   {
/*  71 */     this.builder.append(value);
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */   public Writer append(CharSequence value)
/*     */   {
/*  83 */     this.builder.append(value);
/*  84 */     return this;
/*     */   }
/*     */ 
/*     */   public Writer append(CharSequence value, int start, int end)
/*     */   {
/*  97 */     this.builder.append(value, start, end);
/*  98 */     return this;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void write(String value)
/*     */   {
/* 123 */     if (value != null)
/* 124 */       this.builder.append(value);
/*     */   }
/*     */ 
/*     */   public void write(char[] value, int offset, int length)
/*     */   {
/* 137 */     if (value != null)
/* 138 */       this.builder.append(value, offset, length);
/*     */   }
/*     */ 
/*     */   public StringBuilder getBuilder()
/*     */   {
/* 148 */     return this.builder;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 158 */     return this.builder.toString();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.output.StringBuilderWriter
 * JD-Core Version:    0.6.2
 */