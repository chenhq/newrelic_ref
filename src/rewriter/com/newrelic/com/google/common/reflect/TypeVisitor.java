/*    */ package com.newrelic.com.google.common.reflect;
/*    */ 
/*    */ import com.newrelic.com.google.common.collect.Sets;
/*    */ import java.lang.reflect.GenericArrayType;
/*    */ import java.lang.reflect.ParameterizedType;
/*    */ import java.lang.reflect.Type;
/*    */ import java.lang.reflect.TypeVariable;
/*    */ import java.lang.reflect.WildcardType;
/*    */ import java.util.Set;
/*    */ import javax.annotation.concurrent.NotThreadSafe;
/*    */ 
/*    */ @NotThreadSafe
/*    */ abstract class TypeVisitor
/*    */ {
/* 63 */   private final Set<Type> visited = Sets.newHashSet();
/*    */ 
/*    */   public final void visit(Type[] types)
/*    */   {
/* 70 */     for (Type type : types)
/* 71 */       if ((type != null) && (this.visited.add(type)))
/*    */       {
/* 75 */         boolean succeeded = false;
/*    */         try {
/* 77 */           if ((type instanceof TypeVariable))
/* 78 */             visitTypeVariable((TypeVariable)type);
/* 79 */           else if ((type instanceof WildcardType))
/* 80 */             visitWildcardType((WildcardType)type);
/* 81 */           else if ((type instanceof ParameterizedType))
/* 82 */             visitParameterizedType((ParameterizedType)type);
/* 83 */           else if ((type instanceof Class))
/* 84 */             visitClass((Class)type);
/* 85 */           else if ((type instanceof GenericArrayType))
/* 86 */             visitGenericArrayType((GenericArrayType)type);
/*    */           else {
/* 88 */             throw new AssertionError("Unknown type: " + type);
/*    */           }
/* 90 */           succeeded = true;
/*    */         } finally {
/* 92 */           if (!succeeded)
/* 93 */             this.visited.remove(type);
/*    */         }
/*    */       }
/*    */   }
/*    */ 
/*    */   void visitClass(Class<?> t)
/*    */   {
/*    */   }
/*    */ 
/*    */   void visitGenericArrayType(GenericArrayType t)
/*    */   {
/*    */   }
/*    */ 
/*    */   void visitParameterizedType(ParameterizedType t)
/*    */   {
/*    */   }
/*    */ 
/*    */   void visitTypeVariable(TypeVariable<?> t)
/*    */   {
/*    */   }
/*    */ 
/*    */   void visitWildcardType(WildcardType t)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.reflect.TypeVisitor
 * JD-Core Version:    0.6.2
 */