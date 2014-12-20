/*    */ package com.newrelic.com.google.gson.internal.bind;
/*    */ 
/*    */ import com.newrelic.com.google.gson.Gson;
/*    */ import com.newrelic.com.google.gson.TypeAdapter;
/*    */ import com.newrelic.com.google.gson.TypeAdapterFactory;
/*    */ import com.newrelic.com.google.gson.internal..Gson.Types;
/*    */ import com.newrelic.com.google.gson.internal.ConstructorConstructor;
/*    */ import com.newrelic.com.google.gson.internal.ObjectConstructor;
/*    */ import com.newrelic.com.google.gson.reflect.TypeToken;
/*    */ import com.newrelic.com.google.gson.stream.JsonReader;
/*    */ import com.newrelic.com.google.gson.stream.JsonToken;
/*    */ import com.newrelic.com.google.gson.stream.JsonWriter;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ public final class CollectionTypeAdapterFactory
/*    */   implements TypeAdapterFactory
/*    */ {
/*    */   private final ConstructorConstructor constructorConstructor;
/*    */ 
/*    */   public CollectionTypeAdapterFactory(ConstructorConstructor constructorConstructor)
/*    */   {
/* 40 */     this.constructorConstructor = constructorConstructor;
/*    */   }
/*    */ 
/*    */   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 44 */     Type type = typeToken.getType();
/*    */ 
/* 46 */     Class rawType = typeToken.getRawType();
/* 47 */     if (!Collection.class.isAssignableFrom(rawType)) {
/* 48 */       return null;
/*    */     }
/*    */ 
/* 51 */     Type elementType = .Gson.Types.getCollectionElementType(type, rawType);
/* 52 */     TypeAdapter elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));
/* 53 */     ObjectConstructor constructor = this.constructorConstructor.get(typeToken);
/*    */ 
/* 56 */     TypeAdapter result = new Adapter(gson, elementType, elementTypeAdapter, constructor);
/* 57 */     return result;
/*    */   }
/*    */ 
/*    */   private static final class Adapter<E> extends TypeAdapter<Collection<E>>
/*    */   {
/*    */     private final TypeAdapter<E> elementTypeAdapter;
/*    */     private final ObjectConstructor<? extends Collection<E>> constructor;
/*    */ 
/*    */     public Adapter(Gson context, Type elementType, TypeAdapter<E> elementTypeAdapter, ObjectConstructor<? extends Collection<E>> constructor) {
/* 67 */       this.elementTypeAdapter = new TypeAdapterRuntimeTypeWrapper(context, elementTypeAdapter, elementType);
/*    */ 
/* 69 */       this.constructor = constructor;
/*    */     }
/*    */ 
/*    */     public Collection<E> read(JsonReader in) throws IOException {
/* 73 */       if (in.peek() == JsonToken.NULL) {
/* 74 */         in.nextNull();
/* 75 */         return null;
/*    */       }
/*    */ 
/* 78 */       Collection collection = (Collection)this.constructor.construct();
/* 79 */       in.beginArray();
/* 80 */       while (in.hasNext()) {
/* 81 */         Object instance = this.elementTypeAdapter.read(in);
/* 82 */         collection.add(instance);
/*    */       }
/* 84 */       in.endArray();
/* 85 */       return collection;
/*    */     }
/*    */ 
/*    */     public void write(JsonWriter out, Collection<E> collection) throws IOException {
/* 89 */       if (collection == null) {
/* 90 */         out.nullValue();
/* 91 */         return;
/*    */       }
/*    */ 
/* 94 */       out.beginArray();
/* 95 */       for (Iterator i$ = collection.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 96 */         this.elementTypeAdapter.write(out, element);
/*    */       }
/* 98 */       out.endArray();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.com.google.gson.internal.bind.CollectionTypeAdapterFactory
 * JD-Core Version:    0.6.2
 */