/*     */ package com.newrelic.com.google.common.reflect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.collect.ImmutableList;
/*     */ import com.newrelic.com.google.common.collect.ImmutableList.Builder;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.GenericDeclaration;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.util.Arrays;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ public abstract class Invokable<T, R> extends Element
/*     */   implements GenericDeclaration
/*     */ {
/*     */   <M extends AccessibleObject,  extends Member> Invokable(M member)
/*     */   {
/*  63 */     super(member);
/*     */   }
/*     */ 
/*     */   public static Invokable<?, Object> from(Method method)
/*     */   {
/*  68 */     return new MethodInvokable(method);
/*     */   }
/*     */ 
/*     */   public static <T> Invokable<T, T> from(Constructor<T> constructor)
/*     */   {
/*  73 */     return new ConstructorInvokable(constructor);
/*     */   }
/*     */ 
/*     */   public abstract boolean isOverridable();
/*     */ 
/*     */   public abstract boolean isVarArgs();
/*     */ 
/*     */   public final R invoke(@Nullable T receiver, Object[] args)
/*     */     throws InvocationTargetException, IllegalAccessException
/*     */   {
/* 102 */     return invokeInternal(receiver, (Object[])Preconditions.checkNotNull(args));
/*     */   }
/*     */ 
/*     */   public final TypeToken<? extends R> getReturnType()
/*     */   {
/* 109 */     return TypeToken.of(getGenericReturnType());
/*     */   }
/*     */ 
/*     */   public final ImmutableList<Parameter> getParameters()
/*     */   {
/* 118 */     Type[] parameterTypes = getGenericParameterTypes();
/* 119 */     Annotation[][] annotations = getParameterAnnotations();
/* 120 */     ImmutableList.Builder builder = ImmutableList.builder();
/* 121 */     for (int i = 0; i < parameterTypes.length; i++) {
/* 122 */       builder.add(new Parameter(this, i, TypeToken.of(parameterTypes[i]), annotations[i]));
/*     */     }
/*     */ 
/* 125 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public final ImmutableList<TypeToken<? extends Throwable>> getExceptionTypes()
/*     */   {
/* 130 */     ImmutableList.Builder builder = ImmutableList.builder();
/* 131 */     for (Type type : getGenericExceptionTypes())
/*     */     {
/* 134 */       TypeToken exceptionType = TypeToken.of(type);
/*     */ 
/* 136 */       builder.add(exceptionType);
/*     */     }
/* 138 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public final <R1 extends R> Invokable<T, R1> returning(Class<R1> returnType)
/*     */   {
/* 148 */     return returning(TypeToken.of(returnType));
/*     */   }
/*     */ 
/*     */   public final <R1 extends R> Invokable<T, R1> returning(TypeToken<R1> returnType)
/*     */   {
/* 153 */     if (!returnType.isAssignableFrom(getReturnType())) {
/* 154 */       throw new IllegalArgumentException("Invokable is known to return " + getReturnType() + ", not " + returnType);
/*     */     }
/*     */ 
/* 158 */     Invokable specialized = this;
/* 159 */     return specialized;
/*     */   }
/*     */ 
/*     */   public final Class<? super T> getDeclaringClass()
/*     */   {
/* 164 */     return super.getDeclaringClass();
/*     */   }
/*     */ 
/*     */   public TypeToken<T> getOwnerType()
/*     */   {
/* 171 */     return TypeToken.of(getDeclaringClass());
/*     */   }
/*     */ 
/*     */   abstract Object invokeInternal(@Nullable Object paramObject, Object[] paramArrayOfObject)
/*     */     throws InvocationTargetException, IllegalAccessException;
/*     */ 
/*     */   abstract Type[] getGenericParameterTypes();
/*     */ 
/*     */   abstract Type[] getGenericExceptionTypes();
/*     */ 
/*     */   abstract Annotation[][] getParameterAnnotations();
/*     */ 
/*     */   abstract Type getGenericReturnType();
/*     */ 
/*     */   static class ConstructorInvokable<T> extends Invokable<T, T>
/*     */   {
/*     */     final Constructor<?> constructor;
/*     */ 
/*     */     ConstructorInvokable(Constructor<?> constructor)
/*     */     {
/* 235 */       super();
/* 236 */       this.constructor = constructor;
/*     */     }
/*     */ 
/*     */     final Object invokeInternal(@Nullable Object receiver, Object[] args) throws InvocationTargetException, IllegalAccessException
/*     */     {
/*     */       try {
/* 242 */         return this.constructor.newInstance(args);
/*     */       } catch (InstantiationException e) {
/* 244 */         throw new RuntimeException(this.constructor + " failed.", e);
/*     */       }
/*     */     }
/*     */ 
/*     */     Type getGenericReturnType()
/*     */     {
/* 250 */       Class declaringClass = getDeclaringClass();
/* 251 */       TypeVariable[] typeParams = declaringClass.getTypeParameters();
/* 252 */       if (typeParams.length > 0) {
/* 253 */         return Types.newParameterizedType(declaringClass, typeParams);
/*     */       }
/* 255 */       return declaringClass;
/*     */     }
/*     */ 
/*     */     Type[] getGenericParameterTypes()
/*     */     {
/* 260 */       Type[] types = this.constructor.getGenericParameterTypes();
/* 261 */       if ((types.length > 0) && (mayNeedHiddenThis())) {
/* 262 */         Class[] rawParamTypes = this.constructor.getParameterTypes();
/* 263 */         if ((types.length == rawParamTypes.length) && (rawParamTypes[0] == getDeclaringClass().getEnclosingClass()))
/*     */         {
/* 266 */           return (Type[])Arrays.copyOfRange(types, 1, types.length);
/*     */         }
/*     */       }
/* 269 */       return types;
/*     */     }
/*     */ 
/*     */     Type[] getGenericExceptionTypes() {
/* 273 */       return this.constructor.getGenericExceptionTypes();
/*     */     }
/*     */ 
/*     */     final Annotation[][] getParameterAnnotations() {
/* 277 */       return this.constructor.getParameterAnnotations();
/*     */     }
/*     */ 
/*     */     public final TypeVariable<?>[] getTypeParameters()
/*     */     {
/* 290 */       TypeVariable[] declaredByClass = getDeclaringClass().getTypeParameters();
/* 291 */       TypeVariable[] declaredByConstructor = this.constructor.getTypeParameters();
/* 292 */       TypeVariable[] result = new TypeVariable[declaredByClass.length + declaredByConstructor.length];
/*     */ 
/* 294 */       System.arraycopy(declaredByClass, 0, result, 0, declaredByClass.length);
/* 295 */       System.arraycopy(declaredByConstructor, 0, result, declaredByClass.length, declaredByConstructor.length);
/*     */ 
/* 299 */       return result;
/*     */     }
/*     */ 
/*     */     public final boolean isOverridable() {
/* 303 */       return false;
/*     */     }
/*     */ 
/*     */     public final boolean isVarArgs() {
/* 307 */       return this.constructor.isVarArgs();
/*     */     }
/*     */ 
/*     */     private boolean mayNeedHiddenThis() {
/* 311 */       Class declaringClass = this.constructor.getDeclaringClass();
/* 312 */       if (declaringClass.getEnclosingConstructor() != null)
/*     */       {
/* 314 */         return true;
/*     */       }
/* 316 */       Method enclosingMethod = declaringClass.getEnclosingMethod();
/* 317 */       if (enclosingMethod != null)
/*     */       {
/* 319 */         return !Modifier.isStatic(enclosingMethod.getModifiers());
/*     */       }
/*     */ 
/* 327 */       return (declaringClass.getEnclosingClass() != null) && (!Modifier.isStatic(declaringClass.getModifiers()));
/*     */     }
/*     */   }
/*     */ 
/*     */   static class MethodInvokable<T> extends Invokable<T, Object>
/*     */   {
/*     */     final Method method;
/*     */ 
/*     */     MethodInvokable(Method method)
/*     */     {
/* 191 */       super();
/* 192 */       this.method = method;
/*     */     }
/*     */ 
/*     */     final Object invokeInternal(@Nullable Object receiver, Object[] args) throws InvocationTargetException, IllegalAccessException
/*     */     {
/* 197 */       return this.method.invoke(receiver, args);
/*     */     }
/*     */ 
/*     */     Type getGenericReturnType() {
/* 201 */       return this.method.getGenericReturnType();
/*     */     }
/*     */ 
/*     */     Type[] getGenericParameterTypes() {
/* 205 */       return this.method.getGenericParameterTypes();
/*     */     }
/*     */ 
/*     */     Type[] getGenericExceptionTypes() {
/* 209 */       return this.method.getGenericExceptionTypes();
/*     */     }
/*     */ 
/*     */     final Annotation[][] getParameterAnnotations() {
/* 213 */       return this.method.getParameterAnnotations();
/*     */     }
/*     */ 
/*     */     public final TypeVariable<?>[] getTypeParameters() {
/* 217 */       return this.method.getTypeParameters();
/*     */     }
/*     */ 
/*     */     public final boolean isOverridable() {
/* 221 */       return (!isFinal()) && (!isPrivate()) && (!isStatic()) && (!Modifier.isFinal(getDeclaringClass().getModifiers()));
/*     */     }
/*     */ 
/*     */     public final boolean isVarArgs()
/*     */     {
/* 226 */       return this.method.isVarArgs();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.reflect.Invokable
 * JD-Core Version:    0.6.2
 */