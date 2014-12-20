/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class FieldDecl extends ASTList
/*    */ {
/*    */   public FieldDecl(ASTree _head, ASTList _tail)
/*    */   {
/* 22 */     super(_head, _tail);
/*    */   }
/*    */   public ASTList getModifiers() {
/* 25 */     return (ASTList)getLeft();
/*    */   }
/* 27 */   public Declarator getDeclarator() { return (Declarator)tail().head(); } 
/*    */   public ASTree getInit() {
/* 29 */     return sublist(2).head();
/*    */   }
/*    */   public void accept(Visitor v) throws CompileError {
/* 32 */     v.atFieldDecl(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.FieldDecl
 * JD-Core Version:    0.6.2
 */