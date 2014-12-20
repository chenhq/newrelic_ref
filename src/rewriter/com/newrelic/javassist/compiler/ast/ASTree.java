/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public abstract class ASTree
/*    */   implements Serializable
/*    */ {
/*    */   public ASTree getLeft()
/*    */   {
/* 27 */     return null;
/*    */   }
/* 29 */   public ASTree getRight() { return null; }
/*    */ 
/*    */   public void setLeft(ASTree _left)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void setRight(ASTree _right)
/*    */   {
/*    */   }
/*    */ 
/*    */   public abstract void accept(Visitor paramVisitor) throws CompileError;
/*    */ 
/*    */   public String toString()
/*    */   {
/* 43 */     StringBuffer sbuf = new StringBuffer();
/* 44 */     sbuf.append('<');
/* 45 */     sbuf.append(getTag());
/* 46 */     sbuf.append('>');
/* 47 */     return sbuf.toString();
/*    */   }
/*    */ 
/*    */   protected String getTag()
/*    */   {
/* 55 */     String name = getClass().getName();
/* 56 */     return name.substring(name.lastIndexOf('.') + 1);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.ASTree
 * JD-Core Version:    0.6.2
 */