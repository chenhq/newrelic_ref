/*     */ package com.newrelic.agent.android.instrumentation;
/*     */ 
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonIOException;
/*     */ import com.google.gson.JsonSyntaxException;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import com.newrelic.agent.android.tracing.TraceMachine;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class GsonInstrumentation
/*     */ {
/*  21 */   private static final ArrayList<String> categoryParams = new ArrayList(Arrays.asList(new String[] { "category", MetricCategory.class.getName(), "JSON" }));
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static String toJson(Gson gson, Object src)
/*     */   {
/*  27 */     TraceMachine.enterMethod("Gson#toJson", categoryParams);
/*  28 */     String string = gson.toJson(src);
/*  29 */     TraceMachine.exitMethod();
/*     */ 
/*  31 */     return string;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static String toJson(Gson gson, Object src, Type typeOfSrc) {
/*  36 */     TraceMachine.enterMethod("Gson#toJson", categoryParams);
/*  37 */     String string = gson.toJson(src, typeOfSrc);
/*  38 */     TraceMachine.exitMethod();
/*     */ 
/*  40 */     return string;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static void toJson(Gson gson, Object src, Appendable writer) throws JsonIOException {
/*  45 */     TraceMachine.enterMethod("Gson#toJson", categoryParams);
/*  46 */     gson.toJson(src, writer);
/*  47 */     TraceMachine.exitMethod();
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static void toJson(Gson gson, Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
/*  52 */     TraceMachine.enterMethod("Gson#toJson", categoryParams);
/*  53 */     gson.toJson(src, typeOfSrc, writer);
/*  54 */     TraceMachine.exitMethod();
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static void toJson(Gson gson, Object src, Type typeOfSrc, JsonWriter writer) throws JsonIOException {
/*  59 */     TraceMachine.enterMethod("Gson#toJson", categoryParams);
/*  60 */     gson.toJson(src, typeOfSrc, writer);
/*  61 */     TraceMachine.exitMethod();
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static String toJson(Gson gson, JsonElement jsonElement) {
/*  66 */     TraceMachine.enterMethod("Gson#toJson", categoryParams);
/*  67 */     String string = gson.toJson(jsonElement);
/*  68 */     TraceMachine.exitMethod();
/*     */ 
/*  70 */     return string;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static void toJson(Gson gson, JsonElement jsonElement, Appendable writer) throws JsonIOException {
/*  75 */     TraceMachine.enterMethod("Gson#toJson", categoryParams);
/*  76 */     gson.toJson(jsonElement, writer);
/*  77 */     TraceMachine.exitMethod();
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static void toJson(Gson gson, JsonElement jsonElement, JsonWriter writer) throws JsonIOException {
/*  82 */     TraceMachine.enterMethod("Gson#toJson", categoryParams);
/*  83 */     gson.toJson(jsonElement, writer);
/*  84 */     TraceMachine.exitMethod();
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static <T> T fromJson(Gson gson, String json, Class<T> classOfT) throws JsonSyntaxException {
/*  89 */     TraceMachine.enterMethod("Gson#fromJson", categoryParams);
/*  90 */     Object object = gson.fromJson(json, classOfT);
/*  91 */     TraceMachine.exitMethod();
/*     */ 
/*  93 */     return object;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static <T> T fromJson(Gson gson, String json, Type typeOfT) throws JsonSyntaxException {
/*  98 */     TraceMachine.enterMethod("Gson#fromJson", categoryParams);
/*  99 */     Object object = gson.fromJson(json, typeOfT);
/* 100 */     TraceMachine.exitMethod();
/*     */ 
/* 102 */     return object;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static <T> T fromJson(Gson gson, Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
/* 107 */     TraceMachine.enterMethod("Gson#fromJson", categoryParams);
/* 108 */     Object object = gson.fromJson(json, classOfT);
/* 109 */     TraceMachine.exitMethod();
/*     */ 
/* 111 */     return object;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static <T> T fromJson(Gson gson, Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
/* 116 */     TraceMachine.enterMethod("Gson#fromJson", categoryParams);
/* 117 */     Object object = gson.fromJson(json, typeOfT);
/* 118 */     TraceMachine.exitMethod();
/*     */ 
/* 120 */     return object;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static <T> T fromJson(Gson gson, JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
/* 125 */     TraceMachine.enterMethod("Gson#fromJson", categoryParams);
/* 126 */     Object object = gson.fromJson(reader, typeOfT);
/* 127 */     TraceMachine.exitMethod();
/*     */ 
/* 129 */     return object;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static <T> T fromJson(Gson gson, JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
/* 134 */     TraceMachine.enterMethod("Gson#fromJson", categoryParams);
/* 135 */     Object object = gson.fromJson(json, classOfT);
/* 136 */     TraceMachine.exitMethod();
/*     */ 
/* 138 */     return object;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite(scope="com.google.gson.Gson")
/*     */   public static <T> T fromJson(Gson gson, JsonElement json, Type typeOfT) throws JsonSyntaxException {
/* 143 */     TraceMachine.enterMethod("Gson#fromJson", categoryParams);
/* 144 */     Object object = gson.fromJson(json, typeOfT);
/* 145 */     TraceMachine.exitMethod();
/*     */ 
/* 147 */     return object;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.GsonInstrumentation
 * JD-Core Version:    0.6.2
 */