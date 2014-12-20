/*     */ package com.newrelic.com.google.gson;
/*     */ 
/*     */ import com.newrelic.com.google.gson.internal.ConstructorConstructor;
/*     */ import com.newrelic.com.google.gson.internal.Excluder;
/*     */ import com.newrelic.com.google.gson.internal.Primitives;
/*     */ import com.newrelic.com.google.gson.internal.Streams;
/*     */ import com.newrelic.com.google.gson.internal.bind.ArrayTypeAdapter;
/*     */ import com.newrelic.com.google.gson.internal.bind.CollectionTypeAdapterFactory;
/*     */ import com.newrelic.com.google.gson.internal.bind.DateTypeAdapter;
/*     */ import com.newrelic.com.google.gson.internal.bind.JsonTreeReader;
/*     */ import com.newrelic.com.google.gson.internal.bind.JsonTreeWriter;
/*     */ import com.newrelic.com.google.gson.internal.bind.MapTypeAdapterFactory;
/*     */ import com.newrelic.com.google.gson.internal.bind.ObjectTypeAdapter;
/*     */ import com.newrelic.com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
/*     */ import com.newrelic.com.google.gson.internal.bind.SqlDateTypeAdapter;
/*     */ import com.newrelic.com.google.gson.internal.bind.TimeTypeAdapter;
/*     */ import com.newrelic.com.google.gson.internal.bind.TypeAdapters;
/*     */ import com.newrelic.com.google.gson.reflect.TypeToken;
/*     */ import com.newrelic.com.google.gson.stream.JsonReader;
/*     */ import com.newrelic.com.google.gson.stream.JsonToken;
/*     */ import com.newrelic.com.google.gson.stream.JsonWriter;
/*     */ import com.newrelic.com.google.gson.stream.MalformedJsonException;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.lang.reflect.Type;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class Gson
/*     */ {
/*     */   static final boolean DEFAULT_JSON_NON_EXECUTABLE = false;
/*     */   private static final String JSON_NON_EXECUTABLE_PREFIX = ")]}'\n";
/* 109 */   private final ThreadLocal<Map<TypeToken<?>, FutureTypeAdapter<?>>> calls = new ThreadLocal();
/*     */ 
/* 112 */   private final Map<TypeToken<?>, TypeAdapter<?>> typeTokenCache = Collections.synchronizedMap(new HashMap());
/*     */   private final List<TypeAdapterFactory> factories;
/*     */   private final ConstructorConstructor constructorConstructor;
/*     */   private final boolean serializeNulls;
/*     */   private final boolean htmlSafe;
/*     */   private final boolean generateNonExecutableJson;
/*     */   private final boolean prettyPrinting;
/* 123 */   final JsonDeserializationContext deserializationContext = new JsonDeserializationContext()
/*     */   {
/*     */     public <T> T deserialize(JsonElement json, Type typeOfT) throws JsonParseException {
/* 126 */       return Gson.this.fromJson(json, typeOfT);
/*     */     }
/* 123 */   };
/*     */ 
/* 130 */   final JsonSerializationContext serializationContext = new JsonSerializationContext() {
/*     */     public JsonElement serialize(Object src) {
/* 132 */       return Gson.this.toJsonTree(src);
/*     */     }
/*     */     public JsonElement serialize(Object src, Type typeOfSrc) {
/* 135 */       return Gson.this.toJsonTree(src, typeOfSrc);
/*     */     }
/* 130 */   };
/*     */ 
/*     */   public Gson()
/*     */   {
/* 174 */     this(Excluder.DEFAULT, FieldNamingPolicy.IDENTITY, Collections.emptyMap(), false, false, false, true, false, false, LongSerializationPolicy.DEFAULT, Collections.emptyList());
/*     */   }
/*     */ 
/*     */   Gson(Excluder excluder, FieldNamingStrategy fieldNamingPolicy, Map<Type, InstanceCreator<?>> instanceCreators, boolean serializeNulls, boolean complexMapKeySerialization, boolean generateNonExecutableGson, boolean htmlSafe, boolean prettyPrinting, boolean serializeSpecialFloatingPointValues, LongSerializationPolicy longSerializationPolicy, List<TypeAdapterFactory> typeAdapterFactories)
/*     */   {
/* 186 */     this.constructorConstructor = new ConstructorConstructor(instanceCreators);
/* 187 */     this.serializeNulls = serializeNulls;
/* 188 */     this.generateNonExecutableJson = generateNonExecutableGson;
/* 189 */     this.htmlSafe = htmlSafe;
/* 190 */     this.prettyPrinting = prettyPrinting;
/*     */ 
/* 192 */     List factories = new ArrayList();
/*     */ 
/* 195 */     factories.add(TypeAdapters.JSON_ELEMENT_FACTORY);
/* 196 */     factories.add(ObjectTypeAdapter.FACTORY);
/*     */ 
/* 199 */     factories.add(excluder);
/*     */ 
/* 202 */     factories.addAll(typeAdapterFactories);
/*     */ 
/* 205 */     factories.add(TypeAdapters.STRING_FACTORY);
/* 206 */     factories.add(TypeAdapters.INTEGER_FACTORY);
/* 207 */     factories.add(TypeAdapters.BOOLEAN_FACTORY);
/* 208 */     factories.add(TypeAdapters.BYTE_FACTORY);
/* 209 */     factories.add(TypeAdapters.SHORT_FACTORY);
/* 210 */     factories.add(TypeAdapters.newFactory(Long.TYPE, Long.class, longAdapter(longSerializationPolicy)));
/*     */ 
/* 212 */     factories.add(TypeAdapters.newFactory(Double.TYPE, Double.class, doubleAdapter(serializeSpecialFloatingPointValues)));
/*     */ 
/* 214 */     factories.add(TypeAdapters.newFactory(Float.TYPE, Float.class, floatAdapter(serializeSpecialFloatingPointValues)));
/*     */ 
/* 216 */     factories.add(TypeAdapters.NUMBER_FACTORY);
/* 217 */     factories.add(TypeAdapters.CHARACTER_FACTORY);
/* 218 */     factories.add(TypeAdapters.STRING_BUILDER_FACTORY);
/* 219 */     factories.add(TypeAdapters.STRING_BUFFER_FACTORY);
/* 220 */     factories.add(TypeAdapters.newFactory(BigDecimal.class, TypeAdapters.BIG_DECIMAL));
/* 221 */     factories.add(TypeAdapters.newFactory(BigInteger.class, TypeAdapters.BIG_INTEGER));
/* 222 */     factories.add(TypeAdapters.URL_FACTORY);
/* 223 */     factories.add(TypeAdapters.URI_FACTORY);
/* 224 */     factories.add(TypeAdapters.UUID_FACTORY);
/* 225 */     factories.add(TypeAdapters.LOCALE_FACTORY);
/* 226 */     factories.add(TypeAdapters.INET_ADDRESS_FACTORY);
/* 227 */     factories.add(TypeAdapters.BIT_SET_FACTORY);
/* 228 */     factories.add(DateTypeAdapter.FACTORY);
/* 229 */     factories.add(TypeAdapters.CALENDAR_FACTORY);
/* 230 */     factories.add(TimeTypeAdapter.FACTORY);
/* 231 */     factories.add(SqlDateTypeAdapter.FACTORY);
/* 232 */     factories.add(TypeAdapters.TIMESTAMP_FACTORY);
/* 233 */     factories.add(ArrayTypeAdapter.FACTORY);
/* 234 */     factories.add(TypeAdapters.ENUM_FACTORY);
/* 235 */     factories.add(TypeAdapters.CLASS_FACTORY);
/*     */ 
/* 238 */     factories.add(new CollectionTypeAdapterFactory(this.constructorConstructor));
/* 239 */     factories.add(new MapTypeAdapterFactory(this.constructorConstructor, complexMapKeySerialization));
/* 240 */     factories.add(new ReflectiveTypeAdapterFactory(this.constructorConstructor, fieldNamingPolicy, excluder));
/*     */ 
/* 243 */     this.factories = Collections.unmodifiableList(factories);
/*     */   }
/*     */ 
/*     */   private TypeAdapter<Number> doubleAdapter(boolean serializeSpecialFloatingPointValues) {
/* 247 */     if (serializeSpecialFloatingPointValues) {
/* 248 */       return TypeAdapters.DOUBLE;
/*     */     }
/* 250 */     return new TypeAdapter() {
/*     */       public Double read(JsonReader in) throws IOException {
/* 252 */         if (in.peek() == JsonToken.NULL) {
/* 253 */           in.nextNull();
/* 254 */           return null;
/*     */         }
/* 256 */         return Double.valueOf(in.nextDouble());
/*     */       }
/*     */       public void write(JsonWriter out, Number value) throws IOException {
/* 259 */         if (value == null) {
/* 260 */           out.nullValue();
/* 261 */           return;
/*     */         }
/* 263 */         double doubleValue = value.doubleValue();
/* 264 */         Gson.this.checkValidFloatingPoint(doubleValue);
/* 265 */         out.value(value);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private TypeAdapter<Number> floatAdapter(boolean serializeSpecialFloatingPointValues) {
/* 271 */     if (serializeSpecialFloatingPointValues) {
/* 272 */       return TypeAdapters.FLOAT;
/*     */     }
/* 274 */     return new TypeAdapter() {
/*     */       public Float read(JsonReader in) throws IOException {
/* 276 */         if (in.peek() == JsonToken.NULL) {
/* 277 */           in.nextNull();
/* 278 */           return null;
/*     */         }
/* 280 */         return Float.valueOf((float)in.nextDouble());
/*     */       }
/*     */       public void write(JsonWriter out, Number value) throws IOException {
/* 283 */         if (value == null) {
/* 284 */           out.nullValue();
/* 285 */           return;
/*     */         }
/* 287 */         float floatValue = value.floatValue();
/* 288 */         Gson.this.checkValidFloatingPoint(floatValue);
/* 289 */         out.value(value);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private void checkValidFloatingPoint(double value) {
/* 295 */     if ((Double.isNaN(value)) || (Double.isInfinite(value)))
/* 296 */       throw new IllegalArgumentException(value + " is not a valid double value as per JSON specification. To override this" + " behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method.");
/*     */   }
/*     */ 
/*     */   private TypeAdapter<Number> longAdapter(LongSerializationPolicy longSerializationPolicy)
/*     */   {
/* 303 */     if (longSerializationPolicy == LongSerializationPolicy.DEFAULT) {
/* 304 */       return TypeAdapters.LONG;
/*     */     }
/* 306 */     return new TypeAdapter() {
/*     */       public Number read(JsonReader in) throws IOException {
/* 308 */         if (in.peek() == JsonToken.NULL) {
/* 309 */           in.nextNull();
/* 310 */           return null;
/*     */         }
/* 312 */         return Long.valueOf(in.nextLong());
/*     */       }
/*     */       public void write(JsonWriter out, Number value) throws IOException {
/* 315 */         if (value == null) {
/* 316 */           out.nullValue();
/* 317 */           return;
/*     */         }
/* 319 */         out.value(value.toString());
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public <T> TypeAdapter<T> getAdapter(TypeToken<T> type)
/*     */   {
/* 332 */     TypeAdapter cached = (TypeAdapter)this.typeTokenCache.get(type);
/* 333 */     if (cached != null) {
/* 334 */       return cached;
/*     */     }
/*     */ 
/* 337 */     Map threadCalls = (Map)this.calls.get();
/* 338 */     boolean requiresThreadLocalCleanup = false;
/* 339 */     if (threadCalls == null) {
/* 340 */       threadCalls = new HashMap();
/* 341 */       this.calls.set(threadCalls);
/* 342 */       requiresThreadLocalCleanup = true;
/*     */     }
/*     */ 
/* 346 */     FutureTypeAdapter ongoingCall = (FutureTypeAdapter)threadCalls.get(type);
/* 347 */     if (ongoingCall != null) {
/* 348 */       return ongoingCall;
/*     */     }
/*     */     try
/*     */     {
/* 352 */       FutureTypeAdapter call = new FutureTypeAdapter();
/* 353 */       threadCalls.put(type, call);
/*     */ 
/* 355 */       for (TypeAdapterFactory factory : this.factories) {
/* 356 */         TypeAdapter candidate = factory.create(this, type);
/* 357 */         if (candidate != null) {
/* 358 */           call.setDelegate(candidate);
/* 359 */           this.typeTokenCache.put(type, candidate);
/* 360 */           return candidate;
/*     */         }
/*     */       }
/* 363 */       throw new IllegalArgumentException("GSON cannot handle " + type);
/*     */     } finally {
/* 365 */       threadCalls.remove(type);
/*     */ 
/* 367 */       if (requiresThreadLocalCleanup)
/* 368 */         this.calls.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> TypeAdapter<T> getDelegateAdapter(TypeAdapterFactory skipPast, TypeToken<T> type)
/*     */   {
/* 420 */     boolean skipPastFound = false;
/*     */ 
/* 422 */     for (TypeAdapterFactory factory : this.factories) {
/* 423 */       if (!skipPastFound) {
/* 424 */         if (factory == skipPast) {
/* 425 */           skipPastFound = true;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 430 */         TypeAdapter candidate = factory.create(this, type);
/* 431 */         if (candidate != null)
/* 432 */           return candidate;
/*     */       }
/*     */     }
/* 435 */     throw new IllegalArgumentException("GSON cannot serialize " + type);
/*     */   }
/*     */ 
/*     */   public <T> TypeAdapter<T> getAdapter(Class<T> type)
/*     */   {
/* 445 */     return getAdapter(TypeToken.get(type));
/*     */   }
/*     */ 
/*     */   public JsonElement toJsonTree(Object src)
/*     */   {
/* 462 */     if (src == null) {
/* 463 */       return JsonNull.INSTANCE;
/*     */     }
/* 465 */     return toJsonTree(src, src.getClass());
/*     */   }
/*     */ 
/*     */   public JsonElement toJsonTree(Object src, Type typeOfSrc)
/*     */   {
/* 485 */     JsonTreeWriter writer = new JsonTreeWriter();
/* 486 */     toJson(src, typeOfSrc, writer);
/* 487 */     return writer.get();
/*     */   }
/*     */ 
/*     */   public String toJson(Object src)
/*     */   {
/* 504 */     if (src == null) {
/* 505 */       return toJson(JsonNull.INSTANCE);
/*     */     }
/* 507 */     return toJson(src, src.getClass());
/*     */   }
/*     */ 
/*     */   public String toJson(Object src, Type typeOfSrc)
/*     */   {
/* 526 */     StringWriter writer = new StringWriter();
/* 527 */     toJson(src, typeOfSrc, writer);
/* 528 */     return writer.toString();
/*     */   }
/*     */ 
/*     */   public void toJson(Object src, Appendable writer)
/*     */     throws JsonIOException
/*     */   {
/* 546 */     if (src != null)
/* 547 */       toJson(src, src.getClass(), writer);
/*     */     else
/* 549 */       toJson(JsonNull.INSTANCE, writer);
/*     */   }
/*     */ 
/*     */   public void toJson(Object src, Type typeOfSrc, Appendable writer)
/*     */     throws JsonIOException
/*     */   {
/*     */     try
/*     */     {
/* 571 */       JsonWriter jsonWriter = newJsonWriter(Streams.writerForAppendable(writer));
/* 572 */       toJson(src, typeOfSrc, jsonWriter);
/*     */     } catch (IOException e) {
/* 574 */       throw new JsonIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void toJson(Object src, Type typeOfSrc, JsonWriter writer)
/*     */     throws JsonIOException
/*     */   {
/* 585 */     TypeAdapter adapter = getAdapter(TypeToken.get(typeOfSrc));
/* 586 */     boolean oldLenient = writer.isLenient();
/* 587 */     writer.setLenient(true);
/* 588 */     boolean oldHtmlSafe = writer.isHtmlSafe();
/* 589 */     writer.setHtmlSafe(this.htmlSafe);
/* 590 */     boolean oldSerializeNulls = writer.getSerializeNulls();
/* 591 */     writer.setSerializeNulls(this.serializeNulls);
/*     */     try {
/* 593 */       adapter.write(writer, src);
/*     */     } catch (IOException e) {
/* 595 */       throw new JsonIOException(e);
/*     */     } finally {
/* 597 */       writer.setLenient(oldLenient);
/* 598 */       writer.setHtmlSafe(oldHtmlSafe);
/* 599 */       writer.setSerializeNulls(oldSerializeNulls);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toJson(JsonElement jsonElement)
/*     */   {
/* 611 */     StringWriter writer = new StringWriter();
/* 612 */     toJson(jsonElement, writer);
/* 613 */     return writer.toString();
/*     */   }
/*     */ 
/*     */   public void toJson(JsonElement jsonElement, Appendable writer)
/*     */     throws JsonIOException
/*     */   {
/*     */     try
/*     */     {
/* 626 */       JsonWriter jsonWriter = newJsonWriter(Streams.writerForAppendable(writer));
/* 627 */       toJson(jsonElement, jsonWriter);
/*     */     } catch (IOException e) {
/* 629 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private JsonWriter newJsonWriter(Writer writer)
/*     */     throws IOException
/*     */   {
/* 638 */     if (this.generateNonExecutableJson) {
/* 639 */       writer.write(")]}'\n");
/*     */     }
/* 641 */     JsonWriter jsonWriter = new JsonWriter(writer);
/* 642 */     if (this.prettyPrinting) {
/* 643 */       jsonWriter.setIndent("  ");
/*     */     }
/* 645 */     jsonWriter.setSerializeNulls(this.serializeNulls);
/* 646 */     return jsonWriter;
/*     */   }
/*     */ 
/*     */   public void toJson(JsonElement jsonElement, JsonWriter writer)
/*     */     throws JsonIOException
/*     */   {
/* 654 */     boolean oldLenient = writer.isLenient();
/* 655 */     writer.setLenient(true);
/* 656 */     boolean oldHtmlSafe = writer.isHtmlSafe();
/* 657 */     writer.setHtmlSafe(this.htmlSafe);
/* 658 */     boolean oldSerializeNulls = writer.getSerializeNulls();
/* 659 */     writer.setSerializeNulls(this.serializeNulls);
/*     */     try {
/* 661 */       Streams.write(jsonElement, writer);
/*     */     } catch (IOException e) {
/* 663 */       throw new JsonIOException(e);
/*     */     } finally {
/* 665 */       writer.setLenient(oldLenient);
/* 666 */       writer.setHtmlSafe(oldHtmlSafe);
/* 667 */       writer.setSerializeNulls(oldSerializeNulls);
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(String json, Class<T> classOfT)
/*     */     throws JsonSyntaxException
/*     */   {
/* 689 */     Object object = fromJson(json, classOfT);
/* 690 */     return Primitives.wrap(classOfT).cast(object);
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(String json, Type typeOfT)
/*     */     throws JsonSyntaxException
/*     */   {
/* 713 */     if (json == null) {
/* 714 */       return null;
/*     */     }
/* 716 */     StringReader reader = new StringReader(json);
/* 717 */     Object target = fromJson(reader, typeOfT);
/* 718 */     return target;
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(Reader json, Class<T> classOfT)
/*     */     throws JsonSyntaxException, JsonIOException
/*     */   {
/* 740 */     JsonReader jsonReader = new JsonReader(json);
/* 741 */     Object object = fromJson(jsonReader, classOfT);
/* 742 */     assertFullConsumption(object, jsonReader);
/* 743 */     return Primitives.wrap(classOfT).cast(object);
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(Reader json, Type typeOfT)
/*     */     throws JsonIOException, JsonSyntaxException
/*     */   {
/* 767 */     JsonReader jsonReader = new JsonReader(json);
/* 768 */     Object object = fromJson(jsonReader, typeOfT);
/* 769 */     assertFullConsumption(object, jsonReader);
/* 770 */     return object;
/*     */   }
/*     */ 
/*     */   private static void assertFullConsumption(Object obj, JsonReader reader) {
/*     */     try {
/* 775 */       if ((obj != null) && (reader.peek() != JsonToken.END_DOCUMENT))
/* 776 */         throw new JsonIOException("JSON document was not fully consumed.");
/*     */     }
/*     */     catch (MalformedJsonException e) {
/* 779 */       throw new JsonSyntaxException(e);
/*     */     } catch (IOException e) {
/* 781 */       throw new JsonIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(JsonReader reader, Type typeOfT)
/*     */     throws JsonIOException, JsonSyntaxException
/*     */   {
/* 795 */     boolean isEmpty = true;
/* 796 */     boolean oldLenient = reader.isLenient();
/* 797 */     reader.setLenient(true);
/*     */     try {
/* 799 */       reader.peek();
/* 800 */       isEmpty = false;
/* 801 */       TypeToken typeToken = TypeToken.get(typeOfT);
/* 802 */       typeAdapter = getAdapter(typeToken);
/* 803 */       Object object = typeAdapter.read(reader);
/* 804 */       return object;
/*     */     }
/*     */     catch (EOFException e)
/*     */     {
/*     */       TypeAdapter typeAdapter;
/* 810 */       if (isEmpty) {
/* 811 */         return null;
/*     */       }
/* 813 */       throw new JsonSyntaxException(e);
/*     */     } catch (IllegalStateException e) {
/* 815 */       throw new JsonSyntaxException(e);
/*     */     }
/*     */     catch (IOException e) {
/* 818 */       throw new JsonSyntaxException(e);
/*     */     } finally {
/* 820 */       reader.setLenient(oldLenient);
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(JsonElement json, Class<T> classOfT)
/*     */     throws JsonSyntaxException
/*     */   {
/* 841 */     Object object = fromJson(json, classOfT);
/* 842 */     return Primitives.wrap(classOfT).cast(object);
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(JsonElement json, Type typeOfT)
/*     */     throws JsonSyntaxException
/*     */   {
/* 865 */     if (json == null) {
/* 866 */       return null;
/*     */     }
/* 868 */     return fromJson(new JsonTreeReader(json), typeOfT);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 898 */     return "{serializeNulls:" + this.serializeNulls + "factories:" + this.factories + ",instanceCreators:" + this.constructorConstructor + "}";
/*     */   }
/*     */ 
/*     */   static class FutureTypeAdapter<T> extends TypeAdapter<T>
/*     */   {
/*     */     private TypeAdapter<T> delegate;
/*     */ 
/*     */     public void setDelegate(TypeAdapter<T> typeAdapter)
/*     */     {
/* 875 */       if (this.delegate != null) {
/* 876 */         throw new AssertionError();
/*     */       }
/* 878 */       this.delegate = typeAdapter;
/*     */     }
/*     */ 
/*     */     public T read(JsonReader in) throws IOException {
/* 882 */       if (this.delegate == null) {
/* 883 */         throw new IllegalStateException();
/*     */       }
/* 885 */       return this.delegate.read(in);
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, T value) throws IOException {
/* 889 */       if (this.delegate == null) {
/* 890 */         throw new IllegalStateException();
/*     */       }
/* 892 */       this.delegate.write(out, value);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.com.google.gson.Gson
 * JD-Core Version:    0.6.2
 */