/*    */ package com.newrelic.javassist.tools;
/*    */ 
/*    */ import com.newrelic.javassist.ClassPool;
/*    */ import com.newrelic.javassist.CtClass;
/*    */ import com.newrelic.javassist.bytecode.analysis.FramePrinter;
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class framedump
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 37 */     if (args.length != 1) {
/* 38 */       System.err.println("Usage: java javassist.tools.framedump <class file name>");
/* 39 */       return;
/*    */     }
/*    */ 
/* 42 */     ClassPool pool = ClassPool.getDefault();
/* 43 */     CtClass clazz = pool.get(args[0]);
/* 44 */     System.out.println("Frame Dump of " + clazz.getName() + ":");
/* 45 */     FramePrinter.print(clazz, System.out);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.framedump
 * JD-Core Version:    0.6.2
 */