/*    */ package com.newrelic.javassist.tools;
/*    */ 
/*    */ import com.newrelic.javassist.bytecode.ClassFile;
/*    */ import com.newrelic.javassist.bytecode.ClassFilePrinter;
/*    */ import com.newrelic.javassist.bytecode.ConstPool;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ 
/*    */ public class Dump
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 42 */     if (args.length != 1) {
/* 43 */       System.err.println("Usage: java Dump <class file name>");
/* 44 */       return;
/*    */     }
/*    */ 
/* 47 */     DataInputStream in = new DataInputStream(new FileInputStream(args[0]));
/*    */ 
/* 49 */     ClassFile w = new ClassFile(in);
/* 50 */     PrintWriter out = new PrintWriter(System.out, true);
/* 51 */     out.println("*** constant pool ***");
/* 52 */     w.getConstPool().print(out);
/* 53 */     out.println();
/* 54 */     out.println("*** members ***");
/* 55 */     ClassFilePrinter.print(w, out);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.Dump
 * JD-Core Version:    0.6.2
 */