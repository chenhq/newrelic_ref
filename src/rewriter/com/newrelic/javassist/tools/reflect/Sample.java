/*    */ package com.newrelic.javassist.tools.reflect;
/*    */ 
/*    */ public class Sample
/*    */ {
/*    */   private Metaobject _metaobject;
/*    */   private static ClassMetaobject _classobject;
/*    */ 
/*    */   public Object trap(Object[] args, int identifier)
/*    */     throws Throwable
/*    */   {
/* 27 */     Metaobject mobj = this._metaobject;
/* 28 */     if (mobj == null) {
/* 29 */       return ClassMetaobject.invoke(this, identifier, args);
/*    */     }
/* 31 */     return mobj.trapMethodcall(identifier, args);
/*    */   }
/*    */ 
/*    */   public static Object trapStatic(Object[] args, int identifier)
/*    */     throws Throwable
/*    */   {
/* 37 */     return _classobject.trapMethodcall(identifier, args);
/*    */   }
/*    */ 
/*    */   public static Object trapRead(Object[] args, String name) {
/* 41 */     if (args[0] == null) {
/* 42 */       return _classobject.trapFieldRead(name);
/*    */     }
/* 44 */     return ((Metalevel)args[0])._getMetaobject().trapFieldRead(name);
/*    */   }
/*    */ 
/*    */   public static Object trapWrite(Object[] args, String name) {
/* 48 */     Metalevel base = (Metalevel)args[0];
/* 49 */     if (base == null)
/* 50 */       _classobject.trapFieldWrite(name, args[1]);
/*    */     else {
/* 52 */       base._getMetaobject().trapFieldWrite(name, args[1]);
/*    */     }
/* 54 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.reflect.Sample
 * JD-Core Version:    0.6.2
 */