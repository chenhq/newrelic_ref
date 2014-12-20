/*     */ package com.newrelic.com.google.gson.internal.bind;
/*     */ 
/*     */ import com.newrelic.com.google.gson.Gson;
/*     */ import com.newrelic.com.google.gson.JsonElement;
/*     */ import com.newrelic.com.google.gson.JsonPrimitive;
/*     */ import com.newrelic.com.google.gson.JsonSyntaxException;
/*     */ import com.newrelic.com.google.gson.TypeAdapter;
/*     */ import com.newrelic.com.google.gson.TypeAdapterFactory;
/*     */ import com.newrelic.com.google.gson.internal..Gson.Types;
/*     */ import com.newrelic.com.google.gson.internal.ConstructorConstructor;
/*     */ import com.newrelic.com.google.gson.internal.JsonReaderInternalAccess;
/*     */ import com.newrelic.com.google.gson.internal.ObjectConstructor;
/*     */ import com.newrelic.com.google.gson.internal.Streams;
/*     */ import com.newrelic.com.google.gson.reflect.TypeToken;
/*     */ import com.newrelic.com.google.gson.stream.JsonReader;
/*     */ import com.newrelic.com.google.gson.stream.JsonToken;
/*     */ import com.newrelic.com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public final class MapTypeAdapterFactory
/*     */   implements TypeAdapterFactory
/*     */ {
/*     */   private final ConstructorConstructor constructorConstructor;
/*     */   private final boolean complexMapKeySerialization;
/*     */ 
/*     */   public MapTypeAdapterFactory(ConstructorConstructor constructorConstructor, boolean complexMapKeySerialization)
/*     */   {
/* 111 */     this.constructorConstructor = constructorConstructor;
/* 112 */     this.complexMapKeySerialization = complexMapKeySerialization;
/*     */   }
/*     */ 
/*     */   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 116 */     Type type = typeToken.getType();
/*     */ 
/* 118 */     Class rawType = typeToken.getRawType();
/* 119 */     if (!Map.class.isAssignableFrom(rawType)) {
/* 120 */       return null;
/*     */     }
/*     */ 
/* 123 */     Class rawTypeOfSrc = .Gson.Types.getRawType(type);
/* 124 */     Type[] keyAndValueTypes = .Gson.Types.getMapKeyAndValueTypes(type, rawTypeOfSrc);
/* 125 */     TypeAdapter keyAdapter = getKeyAdapter(gson, keyAndValueTypes[0]);
/* 126 */     TypeAdapter valueAdapter = gson.getAdapter(TypeToken.get(keyAndValueTypes[1]));
/* 127 */     ObjectConstructor constructor = this.constructorConstructor.get(typeToken);
/*     */ 
/* 131 */     TypeAdapter result = new Adapter(gson, keyAndValueTypes[0], keyAdapter, keyAndValueTypes[1], valueAdapter, constructor);
/*     */ 
/* 133 */     return result;
/*     */   }
/*     */ 
/*     */   private TypeAdapter<?> getKeyAdapter(Gson context, Type keyType)
/*     */   {
/* 140 */     return (keyType == Boolean.TYPE) || (keyType == Boolean.class) ? TypeAdapters.BOOLEAN_AS_STRING : context.getAdapter(TypeToken.get(keyType));
/*     */   }
/*     */ 
/*     */   private final class Adapter<K, V> extends TypeAdapter<Map<K, V>>
/*     */   {
/*     */     private final TypeAdapter<K> keyTypeAdapter;
/*     */     private final TypeAdapter<V> valueTypeAdapter;
/*     */     private final ObjectConstructor<? extends Map<K, V>> constructor;
/*     */ 
/*     */     public Adapter(Type context, TypeAdapter<K> keyType, Type keyTypeAdapter, TypeAdapter<V> valueType, ObjectConstructor<? extends Map<K, V>> valueTypeAdapter)
/*     */     {
/* 153 */       this.keyTypeAdapter = new TypeAdapterRuntimeTypeWrapper(context, keyTypeAdapter, keyType);
/*     */ 
/* 155 */       this.valueTypeAdapter = new TypeAdapterRuntimeTypeWrapper(context, valueTypeAdapter, valueType);
/*     */ 
/* 157 */       this.constructor = constructor;
/*     */     }
/*     */ 
/*     */     public Map<K, V> read(JsonReader in) throws IOException {
/* 161 */       JsonToken peek = in.peek();
/* 162 */       if (peek == JsonToken.NULL) {
/* 163 */         in.nextNull();
/* 164 */         return null;
/*     */       }
/*     */ 
/* 167 */       Map map = (Map)this.constructor.construct();
/*     */ 
/* 169 */       if (peek == JsonToken.BEGIN_ARRAY) {
/* 170 */         in.beginArray();
/* 171 */         while (in.hasNext()) {
/* 172 */           in.beginArray();
/* 173 */           Object key = this.keyTypeAdapter.read(in);
/* 174 */           Object value = this.valueTypeAdapter.read(in);
/* 175 */           Object replaced = map.put(key, value);
/* 176 */           if (replaced != null) {
/* 177 */             throw new JsonSyntaxException("duplicate key: " + key);
/*     */           }
/* 179 */           in.endArray();
/*     */         }
/* 181 */         in.endArray();
/*     */       } else {
/* 183 */         in.beginObject();
/* 184 */         while (in.hasNext()) {
/* 185 */           JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
/* 186 */           Object key = this.keyTypeAdapter.read(in);
/* 187 */           Object value = this.valueTypeAdapter.read(in);
/* 188 */           Object replaced = map.put(key, value);
/* 189 */           if (replaced != null) {
/* 190 */             throw new JsonSyntaxException("duplicate key: " + key);
/*     */           }
/*     */         }
/* 193 */         in.endObject();
/*     */       }
/* 195 */       return map;
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, Map<K, V> map) throws IOException {
/* 199 */       if (map == null) {
/* 200 */         out.nullValue();
/* 201 */         return;
/*     */       }
/*     */ 
/* 204 */       if (!MapTypeAdapterFactory.this.complexMapKeySerialization) {
/* 205 */         out.beginObject();
/* 206 */         for (Map.Entry entry : map.entrySet()) {
/* 207 */           out.name(String.valueOf(entry.getKey()));
/* 208 */           this.valueTypeAdapter.write(out, entry.getValue());
/*     */         }
/* 210 */         out.endObject();
/* 211 */         return;
/*     */       }
/*     */ 
/* 214 */       boolean hasComplexKeys = false;
/* 215 */       List keys = new ArrayList(map.size());
/*     */ 
/* 217 */       List values = new ArrayList(map.size());
/* 218 */       for (Map.Entry entry : map.entrySet()) {
/* 219 */         JsonElement keyElement = this.keyTypeAdapter.toJsonTree(entry.getKey());
/* 220 */         keys.add(keyElement);
/* 221 */         values.add(entry.getValue());
/* 222 */         hasComplexKeys |= ((keyElement.isJsonArray()) || (keyElement.isJsonObject()));
/*     */       }
/*     */ 
/* 225 */       if (hasComplexKeys) {
/* 226 */         out.beginArray();
/* 227 */         for (int i = 0; i < keys.size(); i++) {
/* 228 */           out.beginArray();
/* 229 */           Streams.write((JsonElement)keys.get(i), out);
/* 230 */           this.valueTypeAdapter.write(out, values.get(i));
/* 231 */           out.endArray();
/*     */         }
/* 233 */         out.endArray();
/*     */       } else {
/* 235 */         out.beginObject();
/* 236 */         for (int i = 0; i < keys.size(); i++) {
/* 237 */           JsonElement keyElement = (JsonElement)keys.get(i);
/* 238 */           out.name(keyToString(keyElement));
/* 239 */           this.valueTypeAdapter.write(out, values.get(i));
/*     */         }
/* 241 */         out.endObject();
/*     */       }
/*     */     }
/*     */ 
/*     */     private String keyToString(JsonElement keyElement) {
/* 246 */       if (keyElement.isJsonPrimitive()) {
/* 247 */         JsonPrimitive primitive = keyElement.getAsJsonPrimitive();
/* 248 */         if (primitive.isNumber())
/* 249 */           return String.valueOf(primitive.getAsNumber());
/* 250 */         if (primitive.isBoolean())
/* 251 */           return Boolean.toString(primitive.getAsBoolean());
/* 252 */         if (primitive.isString()) {
/* 253 */           return primitive.getAsString();
/*     */         }
/* 255 */         throw new AssertionError();
/*     */       }
/* 257 */       if (keyElement.isJsonNull()) {
/* 258 */         return "null";
/*     */       }
/* 260 */       throw new AssertionError();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.com.google.gson.internal.bind.MapTypeAdapterFactory
 * JD-Core Version:    0.6.2
 */