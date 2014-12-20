/*     */ package com.newrelic.agent.android.harvest.type;
/*     */ 
/*     */ import com.newrelic.com.google.gson.JsonArray;
/*     */ import com.newrelic.com.google.gson.JsonElement;
/*     */ import com.newrelic.com.google.gson.JsonObject;
/*     */ import com.newrelic.com.google.gson.JsonPrimitive;
/*     */ import com.newrelic.com.google.gson.reflect.TypeToken;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class BaseHarvestable
/*     */   implements Harvestable
/*     */ {
/*  20 */   private final Harvestable.Type type;
/*  20 */   protected static final Type GSON_STRING_MAP_TYPE = new TypeToken() {  } .getType();
/*     */ 
/*     */   public BaseHarvestable(Harvestable.Type type)
/*     */   {
/*  24 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public JsonElement asJson()
/*     */   {
/*  29 */     switch (2.$SwitchMap$com$newrelic$agent$android$harvest$type$Harvestable$Type[this.type.ordinal()]) {
/*     */     case 1:
/*  31 */       return asJsonObject();
/*     */     case 2:
/*  33 */       return asJsonArray();
/*     */     case 3:
/*  35 */       return asJsonPrimitive();
/*     */     }
/*  37 */     return null;
/*     */   }
/*     */ 
/*     */   public Harvestable.Type getType()
/*     */   {
/*  43 */     return this.type;
/*     */   }
/*     */ 
/*     */   public String toJsonString()
/*     */   {
/*  53 */     return asJson().toString();
/*     */   }
/*     */ 
/*     */   public JsonArray asJsonArray()
/*     */   {
/*  61 */     return null;
/*     */   }
/*     */ 
/*     */   public JsonObject asJsonObject()
/*     */   {
/*  70 */     return null;
/*     */   }
/*     */ 
/*     */   public JsonPrimitive asJsonPrimitive()
/*     */   {
/*  79 */     return null;
/*     */   }
/*     */ 
/*     */   protected void notEmpty(String argument)
/*     */   {
/*  88 */     if ((argument == null) || (argument.length() == 0))
/*  89 */       throw new IllegalArgumentException("Missing Harvestable field.");
/*     */   }
/*     */ 
/*     */   protected void notNull(Object argument)
/*     */   {
/*  98 */     if (argument == null)
/*  99 */       throw new IllegalArgumentException("Null field in Harvestable object");
/*     */   }
/*     */ 
/*     */   protected String optional(String argument)
/*     */   {
/* 108 */     if (argument == null)
/* 109 */       return "";
/* 110 */     return argument;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.type.BaseHarvestable
 * JD-Core Version:    0.6.2
 */