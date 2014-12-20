/*    */ package com.newrelic.com.google.common.reflect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.lang.reflect.Type;
/*    */ import java.lang.reflect.TypeVariable;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @Beta
/*    */ public abstract class TypeParameter<T> extends TypeCapture<T>
/*    */ {
/*    */   final TypeVariable<?> typeVariable;
/*    */ 
/*    */   protected TypeParameter()
/*    */   {
/* 47 */     Type type = capture();
/* 48 */     Preconditions.checkArgument(type instanceof TypeVariable, "%s should be a type variable.", new Object[] { type });
/* 49 */     this.typeVariable = ((TypeVariable)type);
/*    */   }
/*    */ 
/*    */   public final int hashCode() {
/* 53 */     return this.typeVariable.hashCode();
/*    */   }
/*    */ 
/*    */   public final boolean equals(@Nullable Object o) {
/* 57 */     if ((o instanceof TypeParameter)) {
/* 58 */       TypeParameter that = (TypeParameter)o;
/* 59 */       return this.typeVariable.equals(that.typeVariable);
/*    */     }
/* 61 */     return false;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 65 */     return this.typeVariable.toString();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.reflect.TypeParameter
 * JD-Core Version:    0.6.2
 */