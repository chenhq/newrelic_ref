/*    */ package com.newrelic.javassist.bytecode;
/*    */ 
/*    */ class ExceptionTableEntry
/*    */ {
/*    */   int startPc;
/*    */   int endPc;
/*    */   int handlerPc;
/*    */   int catchType;
/*    */ 
/*    */   ExceptionTableEntry(int start, int end, int handle, int type)
/*    */   {
/* 31 */     this.startPc = start;
/* 32 */     this.endPc = end;
/* 33 */     this.handlerPc = handle;
/* 34 */     this.catchType = type;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ExceptionTableEntry
 * JD-Core Version:    0.6.2
 */