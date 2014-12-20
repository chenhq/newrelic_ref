/*    */ package com.newrelic.javassist.bytecode.analysis;
/*    */ 
/*    */ import com.newrelic.javassist.bytecode.CodeIterator;
/*    */ import com.newrelic.javassist.bytecode.Opcode;
/*    */ 
/*    */ public class Util
/*    */   implements Opcode
/*    */ {
/*    */   public static int getJumpTarget(int pos, CodeIterator iter)
/*    */   {
/* 27 */     int opcode = iter.byteAt(pos);
/* 28 */     pos += ((opcode == 201) || (opcode == 200) ? iter.s32bitAt(pos + 1) : iter.s16bitAt(pos + 1));
/* 29 */     return pos;
/*    */   }
/*    */ 
/*    */   public static boolean isJumpInstruction(int opcode) {
/* 33 */     return ((opcode >= 153) && (opcode <= 168)) || (opcode == 198) || (opcode == 199) || (opcode == 201) || (opcode == 200);
/*    */   }
/*    */ 
/*    */   public static boolean isGoto(int opcode) {
/* 37 */     return (opcode == 167) || (opcode == 200);
/*    */   }
/*    */ 
/*    */   public static boolean isJsr(int opcode) {
/* 41 */     return (opcode == 168) || (opcode == 201);
/*    */   }
/*    */ 
/*    */   public static boolean isReturn(int opcode) {
/* 45 */     return (opcode >= 172) && (opcode <= 177);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.analysis.Util
 * JD-Core Version:    0.6.2
 */