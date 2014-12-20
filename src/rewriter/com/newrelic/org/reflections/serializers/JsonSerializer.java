/*    */ package com.newrelic.org.reflections.serializers;
/*    */ 
/*    */ import com.google.gson.Gson;
/*    */ import com.google.gson.GsonBuilder;
/*    */ import com.google.gson.JsonArray;
/*    */ import com.google.gson.JsonDeserializationContext;
/*    */ import com.google.gson.JsonDeserializer;
/*    */ import com.google.gson.JsonElement;
/*    */ import com.google.gson.JsonObject;
/*    */ import com.google.gson.JsonParseException;
/*    */ import com.google.gson.JsonSerializationContext;
/*    */ import com.newrelic.com.google.common.base.Supplier;
/*    */ import com.newrelic.com.google.common.collect.Multimap;
/*    */ import com.newrelic.com.google.common.collect.Multimaps;
/*    */ import com.newrelic.com.google.common.collect.SetMultimap;
/*    */ import com.newrelic.com.google.common.collect.Sets;
/*    */ import com.newrelic.com.google.common.io.Files;
/*    */ import com.newrelic.org.reflections.Reflections;
/*    */ import com.newrelic.org.reflections.util.Utils;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.lang.reflect.Type;
/*    */ import java.nio.charset.Charset;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class JsonSerializer
/*    */   implements Serializer
/*    */ {
/*    */   private Gson gson;
/*    */ 
/*    */   public Reflections read(InputStream inputStream)
/*    */   {
/* 33 */     return (Reflections)getGson().fromJson(new InputStreamReader(inputStream), Reflections.class);
/*    */   }
/*    */ 
/*    */   public File save(Reflections reflections, String filename) {
/*    */     try {
/* 38 */       File file = Utils.prepareFile(filename);
/* 39 */       Files.write(toString(reflections), file, Charset.defaultCharset());
/* 40 */       return file;
/*    */     } catch (IOException e) {
/* 42 */       throw new RuntimeException(e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String toString(Reflections reflections) {
/* 47 */     return getGson().toJson(reflections);
/*    */   }
/*    */ 
/*    */   private Gson getGson() {
/* 51 */     if (this.gson == null) {
/* 52 */       this.gson = new GsonBuilder().registerTypeAdapter(Multimap.class, new com.google.gson.JsonSerializer()
/*    */       {
/*    */         public JsonElement serialize(Multimap multimap, Type type, JsonSerializationContext jsonSerializationContext) {
/* 55 */           return jsonSerializationContext.serialize(multimap.asMap());
/*    */         }
/*    */       }).registerTypeAdapter(Multimap.class, new JsonDeserializer()
/*    */       {
/*    */         public Multimap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
/*    */           throws JsonParseException
/*    */         {
/* 60 */           SetMultimap map = Multimaps.newSetMultimap(new HashMap(), new Supplier() {
/*    */             public Set<String> get() {
/* 62 */               return Sets.newHashSet();
/*    */             }
/*    */           });
/* 65 */           for (Iterator i$ = ((JsonObject)jsonElement).entrySet().iterator(); i$.hasNext(); ) { entry = (Map.Entry)i$.next();
/* 66 */             for (JsonElement element : (JsonArray)entry.getValue())
/* 67 */               map.get(entry.getKey()).add(element.getAsString());
/*    */           }
/*    */           Map.Entry entry;
/* 70 */           return map;
/*    */         }
/*    */       }).setPrettyPrinting().create();
/*    */     }
/*    */ 
/* 77 */     return this.gson;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.serializers.JsonSerializer
 * JD-Core Version:    0.6.2
 */