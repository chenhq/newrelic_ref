/*    */ package com.newrelic.javassist.bytecode.stackmap;
/*    */ 
/*    */ public abstract interface TypeTag
/*    */ {
/* 21 */   public static final TypeData TOP = null;
/* 22 */   public static final TypeData INTEGER = new TypeData.BasicType("int", 1);
/* 23 */   public static final TypeData FLOAT = new TypeData.BasicType("float", 2);
/* 24 */   public static final TypeData DOUBLE = new TypeData.BasicType("double", 3);
/* 25 */   public static final TypeData LONG = new TypeData.BasicType("long", 4);
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.stackmap.TypeTag
 * JD-Core Version:    0.6.2
 */