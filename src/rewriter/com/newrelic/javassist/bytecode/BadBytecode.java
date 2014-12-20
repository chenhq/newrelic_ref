/*    */ package com.newrelic.javassist.bytecode;
/*    */ 
/*    */ public class BadBytecode extends Exception
/*    */ {
/*    */   public BadBytecode(int opcode)
/*    */   {
/* 23 */     super("bytecode " + opcode);
/*    */   }
/*    */ 
/*    */   public BadBytecode(String msg) {
/* 27 */     super(msg);
/*    */   }
/*    */ 
/*    */   public BadBytecode(String msg, Throwable cause) {
/* 31 */     super(msg, cause);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.BadBytecode
 * JD-Core Version:    0.6.2
 */