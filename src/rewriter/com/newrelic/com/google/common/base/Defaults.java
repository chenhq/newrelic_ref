/*    */ package com.newrelic.com.google.common.base;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public final class Defaults
/*    */ {
/* 47 */   private static final Map<Class<?>, Object> DEFAULTS = Collections.unmodifiableMap(map);
/*    */ 
/*    */   private static <T> void put(Map<Class<?>, Object> map, Class<T> type, T value)
/*    */   {
/* 51 */     map.put(type, value);
/*    */   }
/*    */ 
/*    */   public static <T> T defaultValue(Class<T> type)
/*    */   {
/* 62 */     Object t = DEFAULTS.get(Preconditions.checkNotNull(type));
/* 63 */     return t;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 38 */     Map map = new HashMap();
/* 39 */     put(map, Boolean.TYPE, Boolean.valueOf(false));
/* 40 */     put(map, Character.TYPE, Character.valueOf('\000'));
/* 41 */     put(map, Byte.TYPE, Byte.valueOf((byte)0));
/* 42 */     put(map, Short.TYPE, Short.valueOf((short)0));
/* 43 */     put(map, Integer.TYPE, Integer.valueOf(0));
/* 44 */     put(map, Long.TYPE, Long.valueOf(0L));
/* 45 */     put(map, Float.TYPE, Float.valueOf(0.0F));
/* 46 */     put(map, Double.TYPE, Double.valueOf(0.0D));
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.Defaults
 * JD-Core Version:    0.6.2
 */