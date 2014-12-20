/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.annotation.Annotation;
/*     */ import com.newrelic.javassist.bytecode.annotation.AnnotationsWriter;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ParameterAnnotationsAttribute extends AttributeInfo
/*     */ {
/*     */   public static final String visibleTag = "RuntimeVisibleParameterAnnotations";
/*     */   public static final String invisibleTag = "RuntimeInvisibleParameterAnnotations";
/*     */ 
/*     */   public ParameterAnnotationsAttribute(ConstPool cp, String attrname, byte[] info)
/*     */   {
/*  67 */     super(cp, attrname, info);
/*     */   }
/*     */ 
/*     */   public ParameterAnnotationsAttribute(ConstPool cp, String attrname)
/*     */   {
/*  82 */     this(cp, attrname, new byte[] { 0 });
/*     */   }
/*     */ 
/*     */   ParameterAnnotationsAttribute(ConstPool cp, int n, DataInputStream in)
/*     */     throws IOException
/*     */   {
/*  91 */     super(cp, n, in);
/*     */   }
/*     */ 
/*     */   public int numParameters()
/*     */   {
/*  98 */     return this.info[0] & 0xFF;
/*     */   }
/*     */ 
/*     */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*     */   {
/* 105 */     AnnotationsAttribute.Copier copier = new AnnotationsAttribute.Copier(this.info, this.constPool, newCp, classnames);
/*     */     try {
/* 107 */       copier.parameters();
/* 108 */       return new ParameterAnnotationsAttribute(newCp, getName(), copier.close());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 112 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Annotation[][] getAnnotations()
/*     */   {
/*     */     try
/*     */     {
/* 130 */       return new AnnotationsAttribute.Parser(this.info, this.constPool).parseParameters();
/*     */     }
/*     */     catch (Exception e) {
/* 133 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setAnnotations(Annotation[][] params)
/*     */   {
/* 147 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/* 148 */     AnnotationsWriter writer = new AnnotationsWriter(output, this.constPool);
/*     */     try {
/* 150 */       int n = params.length;
/* 151 */       writer.numParameters(n);
/* 152 */       for (int i = 0; i < n; i++) {
/* 153 */         Annotation[] anno = params[i];
/* 154 */         writer.numAnnotations(anno.length);
/* 155 */         for (int j = 0; j < anno.length; j++) {
/* 156 */           anno[j].write(writer);
/*     */         }
/*     */       }
/* 159 */       writer.close();
/*     */     }
/*     */     catch (IOException e) {
/* 162 */       throw new RuntimeException(e);
/*     */     }
/*     */ 
/* 165 */     set(output.toByteArray());
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ParameterAnnotationsAttribute
 * JD-Core Version:    0.6.2
 */