/*     */ package com.newrelic.com.google.gson;
/*     */ 
/*     */ import com.newrelic.com.google.gson.internal..Gson.Preconditions;
/*     */ import com.newrelic.com.google.gson.internal.Streams;
/*     */ import com.newrelic.com.google.gson.reflect.TypeToken;
/*     */ import com.newrelic.com.google.gson.stream.JsonReader;
/*     */ import com.newrelic.com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ 
/*     */ final class TreeTypeAdapter<T> extends TypeAdapter<T>
/*     */ {
/*     */   private final JsonSerializer<T> serializer;
/*     */   private final JsonDeserializer<T> deserializer;
/*     */   private final Gson gson;
/*     */   private final TypeToken<T> typeToken;
/*     */   private final TypeAdapterFactory skipPast;
/*     */   private TypeAdapter<T> delegate;
/*     */ 
/*     */   private TreeTypeAdapter(JsonSerializer<T> serializer, JsonDeserializer<T> deserializer, Gson gson, TypeToken<T> typeToken, TypeAdapterFactory skipPast)
/*     */   {
/*  43 */     this.serializer = serializer;
/*  44 */     this.deserializer = deserializer;
/*  45 */     this.gson = gson;
/*  46 */     this.typeToken = typeToken;
/*  47 */     this.skipPast = skipPast;
/*     */   }
/*     */ 
/*     */   public T read(JsonReader in) throws IOException {
/*  51 */     if (this.deserializer == null) {
/*  52 */       return delegate().read(in);
/*     */     }
/*  54 */     JsonElement value = Streams.parse(in);
/*  55 */     if (value.isJsonNull()) {
/*  56 */       return null;
/*     */     }
/*  58 */     return this.deserializer.deserialize(value, this.typeToken.getType(), this.gson.deserializationContext);
/*     */   }
/*     */ 
/*     */   public void write(JsonWriter out, T value) throws IOException {
/*  62 */     if (this.serializer == null) {
/*  63 */       delegate().write(out, value);
/*  64 */       return;
/*     */     }
/*  66 */     if (value == null) {
/*  67 */       out.nullValue();
/*  68 */       return;
/*     */     }
/*  70 */     JsonElement tree = this.serializer.serialize(value, this.typeToken.getType(), this.gson.serializationContext);
/*  71 */     Streams.write(tree, out);
/*     */   }
/*     */ 
/*     */   private TypeAdapter<T> delegate() {
/*  75 */     TypeAdapter d = this.delegate;
/*  76 */     return this.delegate = this.gson.getDelegateAdapter(this.skipPast, this.typeToken);
/*     */   }
/*     */ 
/*     */   public static TypeAdapterFactory newFactory(TypeToken<?> exactType, Object typeAdapter)
/*     */   {
/*  85 */     return new SingleTypeFactory(typeAdapter, exactType, false, null, null);
/*     */   }
/*     */ 
/*     */   public static TypeAdapterFactory newFactoryWithMatchRawType(TypeToken<?> exactType, Object typeAdapter)
/*     */   {
/*  95 */     boolean matchRawType = exactType.getType() == exactType.getRawType();
/*  96 */     return new SingleTypeFactory(typeAdapter, exactType, matchRawType, null, null);
/*     */   }
/*     */ 
/*     */   public static TypeAdapterFactory newTypeHierarchyFactory(Class<?> hierarchyType, Object typeAdapter)
/*     */   {
/* 105 */     return new SingleTypeFactory(typeAdapter, null, false, hierarchyType, null);
/*     */   }
/*     */   private static class SingleTypeFactory implements TypeAdapterFactory { private final TypeToken<?> exactType;
/*     */     private final boolean matchRawType;
/*     */     private final Class<?> hierarchyType;
/*     */     private final JsonSerializer<?> serializer;
/*     */     private final JsonDeserializer<?> deserializer;
/*     */ 
/* 117 */     private SingleTypeFactory(Object typeAdapter, TypeToken<?> exactType, boolean matchRawType, Class<?> hierarchyType) { this.serializer = ((typeAdapter instanceof JsonSerializer) ? (JsonSerializer)typeAdapter : null);
/*     */ 
/* 120 */       this.deserializer = ((typeAdapter instanceof JsonDeserializer) ? (JsonDeserializer)typeAdapter : null);
/*     */ 
/* 123 */       .Gson.Preconditions.checkArgument((this.serializer != null) || (this.deserializer != null));
/* 124 */       this.exactType = exactType;
/* 125 */       this.matchRawType = matchRawType;
/* 126 */       this.hierarchyType = hierarchyType;
/*     */     }
/*     */ 
/*     */     public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type)
/*     */     {
/* 131 */       boolean matches = this.exactType != null ? false : (this.exactType.equals(type)) || ((this.matchRawType) && (this.exactType.getType() == type.getRawType())) ? true : this.hierarchyType.isAssignableFrom(type.getRawType());
/*     */ 
/* 134 */       return matches ? new TreeTypeAdapter(this.serializer, this.deserializer, gson, type, this, null) : null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.com.google.gson.TreeTypeAdapter
 * JD-Core Version:    0.6.2
 */