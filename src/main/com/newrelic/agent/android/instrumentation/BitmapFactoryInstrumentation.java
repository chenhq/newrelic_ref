/*     */ package com.newrelic.agent.android.instrumentation;
/*     */ 
/*     */ import android.content.res.Resources;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.BitmapFactory;
/*     */ import android.graphics.BitmapFactory.Options;
/*     */ import android.graphics.Rect;
/*     */ import android.util.TypedValue;
/*     */ import com.newrelic.agent.android.tracing.TraceMachine;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class BitmapFactoryInstrumentation
/*     */ {
/*  20 */   private static final ArrayList<String> categoryParams = new ArrayList(Arrays.asList(new String[] { "category", MetricCategory.class.getName(), "IMAGE" }));
/*     */ 
/*     */   @ReplaceCallSite(isStatic=true, scope="android.graphics.BitmapFactory")
/*     */   public static Bitmap decodeFile(String pathName, BitmapFactory.Options opts)
/*     */   {
/*  26 */     TraceMachine.enterMethod("BitmapFactory#decodeFile", categoryParams);
/*  27 */     Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
/*  28 */     TraceMachine.exitMethod();
/*     */ 
/*  30 */     return bitmap;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(isStatic=true, scope="android.graphics.BitmapFactory")
/*     */   public static Bitmap decodeFile(String pathName) {
/*  35 */     TraceMachine.enterMethod("BitmapFactory#decodeFile", categoryParams);
/*  36 */     Bitmap bitmap = BitmapFactory.decodeFile(pathName);
/*  37 */     TraceMachine.exitMethod();
/*     */ 
/*  39 */     return bitmap;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(isStatic=true, scope="android.graphics.BitmapFactory")
/*     */   public static Bitmap decodeResourceStream(Resources res, TypedValue value, InputStream is, Rect pad, BitmapFactory.Options opts) {
/*  44 */     TraceMachine.enterMethod("BitmapFactory#decodeResourceStream", categoryParams);
/*  45 */     Bitmap bitmap = BitmapFactory.decodeResourceStream(res, value, is, pad, opts);
/*  46 */     TraceMachine.exitMethod();
/*     */ 
/*  48 */     return bitmap;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(isStatic=true, scope="android.graphics.BitmapFactory")
/*     */   public static Bitmap decodeResource(Resources res, int id, BitmapFactory.Options opts) {
/*  53 */     TraceMachine.enterMethod("BitmapFactory#decodeResource", categoryParams);
/*  54 */     Bitmap bitmap = BitmapFactory.decodeResource(res, id, opts);
/*  55 */     TraceMachine.exitMethod();
/*     */ 
/*  57 */     return bitmap;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(isStatic=true, scope="android.graphics.BitmapFactory")
/*     */   public static Bitmap decodeResource(Resources res, int id)
/*     */   {
/*  63 */     TraceMachine.enterMethod("BitmapFactory#decodeResource", categoryParams);
/*  64 */     Bitmap bitmap = BitmapFactory.decodeResource(res, id);
/*  65 */     TraceMachine.exitMethod();
/*     */ 
/*  67 */     return bitmap;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(isStatic=true, scope="android.graphics.BitmapFactory")
/*     */   public static Bitmap decodeByteArray(byte[] data, int offset, int length, BitmapFactory.Options opts) {
/*  72 */     TraceMachine.enterMethod("BitmapFactory#decodeByteArray", categoryParams);
/*  73 */     Bitmap bitmap = BitmapFactory.decodeByteArray(data, offset, length, opts);
/*  74 */     TraceMachine.exitMethod();
/*     */ 
/*  76 */     return bitmap;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(isStatic=true, scope="android.graphics.BitmapFactory")
/*     */   public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
/*  81 */     TraceMachine.enterMethod("BitmapFactory#decodeByteArray", categoryParams);
/*  82 */     Bitmap bitmap = BitmapFactory.decodeByteArray(data, offset, length);
/*  83 */     TraceMachine.exitMethod();
/*     */ 
/*  85 */     return bitmap;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(isStatic=true, scope="android.graphics.BitmapFactory")
/*     */   public static Bitmap decodeStream(InputStream is, Rect outPadding, BitmapFactory.Options opts) {
/*  90 */     TraceMachine.enterMethod("BitmapFactory#decodeStream", categoryParams);
/*  91 */     Bitmap bitmap = BitmapFactory.decodeStream(is, outPadding, opts);
/*  92 */     TraceMachine.exitMethod();
/*     */ 
/*  94 */     return bitmap;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(isStatic=true, scope="android.graphics.BitmapFactory")
/*     */   public static Bitmap decodeStream(InputStream is) {
/*  99 */     TraceMachine.enterMethod("BitmapFactory#decodeStream", categoryParams);
/* 100 */     Bitmap bitmap = BitmapFactory.decodeStream(is);
/* 101 */     TraceMachine.exitMethod();
/*     */ 
/* 103 */     return bitmap;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(isStatic=true, scope="android.graphics.BitmapFactory")
/*     */   public static Bitmap decodeFileDescriptor(FileDescriptor fd, Rect outPadding, BitmapFactory.Options opts) {
/* 108 */     TraceMachine.enterMethod("BitmapFactory#decodeFileDescriptor", categoryParams);
/* 109 */     Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd, outPadding, opts);
/* 110 */     TraceMachine.exitMethod();
/*     */ 
/* 112 */     return bitmap;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(isStatic=true, scope="android.graphics.BitmapFactory")
/*     */   public static Bitmap decodeFileDescriptor(FileDescriptor fd) {
/* 117 */     TraceMachine.enterMethod("BitmapFactory#decodeFileDescriptor", categoryParams);
/* 118 */     Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd);
/* 119 */     TraceMachine.exitMethod();
/*     */ 
/* 121 */     return bitmap;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.BitmapFactoryInstrumentation
 * JD-Core Version:    0.6.2
 */