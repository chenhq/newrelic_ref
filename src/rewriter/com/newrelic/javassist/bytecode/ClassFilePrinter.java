/*    */ package com.newrelic.javassist.bytecode;
/*    */ 
/*    */ import com.newrelic.javassist.Modifier;
/*    */ import java.io.PrintWriter;
/*    */ import java.util.List;
/*    */ 
/*    */ public class ClassFilePrinter
/*    */ {
/*    */   public static void print(ClassFile cf)
/*    */   {
/* 32 */     print(cf, new PrintWriter(System.out, true));
/*    */   }
/*    */ 
/*    */   public static void print(ClassFile cf, PrintWriter out)
/*    */   {
/* 45 */     int mod = AccessFlag.toModifier(cf.getAccessFlags() & 0xFFFFFFDF);
/*    */ 
/* 48 */     out.println("major: " + cf.major + ", minor: " + cf.minor + " modifiers: " + Integer.toHexString(cf.getAccessFlags()));
/*    */ 
/* 50 */     out.println(Modifier.toString(mod) + " class " + cf.getName() + " extends " + cf.getSuperclass());
/*    */ 
/* 53 */     String[] infs = cf.getInterfaces();
/* 54 */     if ((infs != null) && (infs.length > 0)) {
/* 55 */       out.print("    implements ");
/* 56 */       out.print(infs[0]);
/* 57 */       for (int i = 1; i < infs.length; i++) {
/* 58 */         out.print(", " + infs[i]);
/*    */       }
/* 60 */       out.println();
/*    */     }
/*    */ 
/* 63 */     out.println();
/* 64 */     List list = cf.getFields();
/* 65 */     int n = list.size();
/* 66 */     for (int i = 0; i < n; i++) {
/* 67 */       FieldInfo finfo = (FieldInfo)list.get(i);
/* 68 */       int acc = finfo.getAccessFlags();
/* 69 */       out.println(Modifier.toString(AccessFlag.toModifier(acc)) + " " + finfo.getName() + "\t" + finfo.getDescriptor());
/*    */ 
/* 72 */       printAttributes(finfo.getAttributes(), out, 'f');
/*    */     }
/*    */ 
/* 75 */     out.println();
/* 76 */     list = cf.getMethods();
/* 77 */     n = list.size();
/* 78 */     for (int i = 0; i < n; i++) {
/* 79 */       MethodInfo minfo = (MethodInfo)list.get(i);
/* 80 */       int acc = minfo.getAccessFlags();
/* 81 */       out.println(Modifier.toString(AccessFlag.toModifier(acc)) + " " + minfo.getName() + "\t" + minfo.getDescriptor());
/*    */ 
/* 84 */       printAttributes(minfo.getAttributes(), out, 'm');
/* 85 */       out.println();
/*    */     }
/*    */ 
/* 88 */     out.println();
/* 89 */     printAttributes(cf.getAttributes(), out, 'c');
/*    */   }
/*    */ 
/*    */   static void printAttributes(List list, PrintWriter out, char kind) {
/* 93 */     if (list == null) {
/* 94 */       return;
/*    */     }
/* 96 */     int n = list.size();
/* 97 */     for (int i = 0; i < n; i++) {
/* 98 */       AttributeInfo ai = (AttributeInfo)list.get(i);
/* 99 */       if ((ai instanceof CodeAttribute)) {
/* 100 */         CodeAttribute ca = (CodeAttribute)ai;
/* 101 */         out.println("attribute: " + ai.getName() + ": " + ai.getClass().getName());
/*    */ 
/* 103 */         out.println("max stack " + ca.getMaxStack() + ", max locals " + ca.getMaxLocals() + ", " + ca.getExceptionTable().size() + " catch blocks");
/*    */ 
/* 107 */         out.println("<code attribute begin>");
/* 108 */         printAttributes(ca.getAttributes(), out, kind);
/* 109 */         out.println("<code attribute end>");
/*    */       }
/* 111 */       else if ((ai instanceof StackMapTable)) {
/* 112 */         out.println("<stack map table begin>");
/* 113 */         StackMapTable.Printer.print((StackMapTable)ai, out);
/* 114 */         out.println("<stack map table end>");
/*    */       }
/* 116 */       else if ((ai instanceof StackMap)) {
/* 117 */         out.println("<stack map begin>");
/* 118 */         ((StackMap)ai).print(out);
/* 119 */         out.println("<stack map end>");
/*    */       }
/* 121 */       else if ((ai instanceof SignatureAttribute)) {
/* 122 */         SignatureAttribute sa = (SignatureAttribute)ai;
/* 123 */         String sig = sa.getSignature();
/* 124 */         out.println("signature: " + sig);
/*    */         try
/*    */         {
/*    */           String s;
/*    */           String s;
/* 127 */           if (kind == 'c') {
/* 128 */             s = SignatureAttribute.toClassSignature(sig).toString();
/*    */           }
/*    */           else
/*    */           {
/*    */             String s;
/* 129 */             if (kind == 'm')
/* 130 */               s = SignatureAttribute.toMethodSignature(sig).toString();
/*    */             else
/* 132 */               s = SignatureAttribute.toFieldSignature(sig).toString();
/*    */           }
/* 134 */           out.println("           " + s);
/*    */         }
/*    */         catch (BadBytecode e) {
/* 137 */           out.println("           syntax error");
/*    */         }
/*    */       }
/*    */       else {
/* 141 */         out.println("attribute: " + ai.getName() + " (" + ai.get().length + " byte): " + ai.getClass().getName());
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ClassFilePrinter
 * JD-Core Version:    0.6.2
 */