/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.annotation.AnnotationsWriter;
/*     */ import com.newrelic.javassist.bytecode.annotation.MemberValue;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class AnnotationDefaultAttribute extends AttributeInfo
/*     */ {
/*     */   public static final String tag = "AnnotationDefault";
/*     */ 
/*     */   public AnnotationDefaultAttribute(ConstPool cp, byte[] info)
/*     */   {
/*  80 */     super(cp, "AnnotationDefault", info);
/*     */   }
/*     */ 
/*     */   public AnnotationDefaultAttribute(ConstPool cp)
/*     */   {
/*  91 */     this(cp, new byte[] { 0, 0 });
/*     */   }
/*     */ 
/*     */   AnnotationDefaultAttribute(ConstPool cp, int n, DataInputStream in)
/*     */     throws IOException
/*     */   {
/* 100 */     super(cp, n, in);
/*     */   }
/*     */ 
/*     */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*     */   {
/* 107 */     AnnotationsAttribute.Copier copier = new AnnotationsAttribute.Copier(this.info, this.constPool, newCp, classnames);
/*     */     try
/*     */     {
/* 110 */       copier.memberValue(0);
/* 111 */       return new AnnotationDefaultAttribute(newCp, copier.close());
/*     */     }
/*     */     catch (Exception e) {
/* 114 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public MemberValue getDefaultValue()
/*     */   {
/*     */     try
/*     */     {
/* 124 */       return new AnnotationsAttribute.Parser(this.info, this.constPool).parseMemberValue();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 128 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDefaultValue(MemberValue value)
/*     */   {
/* 139 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/* 140 */     AnnotationsWriter writer = new AnnotationsWriter(output, this.constPool);
/*     */     try {
/* 142 */       value.write(writer);
/* 143 */       writer.close();
/*     */     }
/*     */     catch (IOException e) {
/* 146 */       throw new RuntimeException(e);
/*     */     }
/*     */ 
/* 149 */     set(output.toByteArray());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 157 */     return getDefaultValue().toString();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.AnnotationDefaultAttribute
 * JD-Core Version:    0.6.2
 */