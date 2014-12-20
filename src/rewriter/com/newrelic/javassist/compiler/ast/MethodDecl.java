/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class MethodDecl extends ASTList
/*    */ {
/*    */   public static final String initName = "<init>";
/*    */ 
/*    */   public MethodDecl(ASTree _head, ASTList _tail)
/*    */   {
/* 24 */     super(_head, _tail);
/*    */   }
/*    */ 
/*    */   public boolean isConstructor() {
/* 28 */     Symbol sym = getReturn().getVariable();
/* 29 */     return (sym != null) && ("<init>".equals(sym.get()));
/*    */   }
/*    */   public ASTList getModifiers() {
/* 32 */     return (ASTList)getLeft();
/*    */   }
/* 34 */   public Declarator getReturn() { return (Declarator)tail().head(); } 
/*    */   public ASTList getParams() {
/* 36 */     return (ASTList)sublist(2).head();
/*    */   }
/* 38 */   public ASTList getThrows() { return (ASTList)sublist(3).head(); } 
/*    */   public Stmnt getBody() {
/* 40 */     return (Stmnt)sublist(4).head();
/*    */   }
/*    */   public void accept(Visitor v) throws CompileError {
/* 43 */     v.atMethodDecl(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.MethodDecl
 * JD-Core Version:    0.6.2
 */