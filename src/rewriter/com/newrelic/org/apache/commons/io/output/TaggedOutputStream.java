/*     */ package com.newrelic.org.apache.commons.io.output;
/*     */ 
/*     */ import com.newrelic.org.apache.commons.io.TaggedIOException;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.UUID;
/*     */ 
/*     */ public class TaggedOutputStream extends ProxyOutputStream
/*     */ {
/*  69 */   private final Serializable tag = UUID.randomUUID();
/*     */ 
/*     */   public TaggedOutputStream(OutputStream proxy)
/*     */   {
/*  77 */     super(proxy);
/*     */   }
/*     */ 
/*     */   public boolean isCauseOf(Exception exception)
/*     */   {
/*  88 */     return TaggedIOException.isTaggedWith(exception, this.tag);
/*     */   }
/*     */ 
/*     */   public void throwIfCauseOf(Exception exception)
/*     */     throws IOException
/*     */   {
/* 102 */     TaggedIOException.throwCauseIfTaggedWith(exception, this.tag);
/*     */   }
/*     */ 
/*     */   protected void handleIOException(IOException e)
/*     */     throws IOException
/*     */   {
/* 113 */     throw new TaggedIOException(e, this.tag);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.output.TaggedOutputStream
 * JD-Core Version:    0.6.2
 */