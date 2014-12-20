/*    */ package com.newrelic.com.google.common.reflect;
/*    */ 
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.lang.reflect.ParameterizedType;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ abstract class TypeCapture<T>
/*    */ {
/*    */   final Type capture()
/*    */   {
/* 33 */     Type superclass = getClass().getGenericSuperclass();
/* 34 */     Preconditions.checkArgument(superclass instanceof ParameterizedType, "%s isn't parameterized", new Object[] { superclass });
/*    */ 
/* 36 */     return ((ParameterizedType)superclass).getActualTypeArguments()[0];
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.reflect.TypeCapture
 * JD-Core Version:    0.6.2
 */