/*    */ package com.newrelic.com.google.gson.internal.bind;
/*    */ 
/*    */ import com.newrelic.com.google.gson.Gson;
/*    */ import com.newrelic.com.google.gson.JsonSyntaxException;
/*    */ import com.newrelic.com.google.gson.TypeAdapter;
/*    */ import com.newrelic.com.google.gson.TypeAdapterFactory;
/*    */ import com.newrelic.com.google.gson.reflect.TypeToken;
/*    */ import com.newrelic.com.google.gson.stream.JsonReader;
/*    */ import com.newrelic.com.google.gson.stream.JsonToken;
/*    */ import com.newrelic.com.google.gson.stream.JsonWriter;
/*    */ import java.io.IOException;
/*    */ import java.sql.Time;
/*    */ import java.text.DateFormat;
/*    */ import java.text.ParseException;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ 
/*    */ public final class TimeTypeAdapter extends TypeAdapter<Time>
/*    */ {
/* 41 */   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory()
/*    */   {
/*    */     public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 44 */       return typeToken.getRawType() == Time.class ? new TimeTypeAdapter() : null;
/*    */     }
/* 41 */   };
/*    */ 
/* 48 */   private final DateFormat format = new SimpleDateFormat("hh:mm:ss a");
/*    */ 
/*    */   public synchronized Time read(JsonReader in) throws IOException {
/* 51 */     if (in.peek() == JsonToken.NULL) {
/* 52 */       in.nextNull();
/* 53 */       return null;
/*    */     }
/*    */     try {
/* 56 */       Date date = this.format.parse(in.nextString());
/* 57 */       return new Time(date.getTime());
/*    */     } catch (ParseException e) {
/* 59 */       throw new JsonSyntaxException(e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public synchronized void write(JsonWriter out, Time value) throws IOException {
/* 64 */     out.value(value == null ? null : this.format.format(value));
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.com.google.gson.internal.bind.TimeTypeAdapter
 * JD-Core Version:    0.6.2
 */