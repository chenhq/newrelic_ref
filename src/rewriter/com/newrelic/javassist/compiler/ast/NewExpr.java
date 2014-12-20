/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ import com.newrelic.javassist.compiler.TokenId;
/*    */ 
/*    */ public class NewExpr extends ASTList
/*    */   implements TokenId
/*    */ {
/*    */   protected boolean newArray;
/*    */   protected int arrayType;
/*    */ 
/*    */   public NewExpr(ASTList className, ASTList args)
/*    */   {
/* 29 */     super(className, new ASTList(args));
/* 30 */     this.newArray = false;
/* 31 */     this.arrayType = 307;
/*    */   }
/*    */ 
/*    */   public NewExpr(int type, ASTList arraySize, ArrayInit init) {
/* 35 */     super(null, new ASTList(arraySize));
/* 36 */     this.newArray = true;
/* 37 */     this.arrayType = type;
/* 38 */     if (init != null)
/* 39 */       append(this, init);
/*    */   }
/*    */ 
/*    */   public static NewExpr makeObjectArray(ASTList className, ASTList arraySize, ArrayInit init)
/*    */   {
/* 44 */     NewExpr e = new NewExpr(className, arraySize);
/* 45 */     e.newArray = true;
/* 46 */     if (init != null) {
/* 47 */       append(e, init);
/*    */     }
/* 49 */     return e;
/*    */   }
/*    */   public boolean isArray() {
/* 52 */     return this.newArray;
/*    */   }
/*    */ 
/*    */   public int getArrayType() {
/* 56 */     return this.arrayType;
/*    */   }
/* 58 */   public ASTList getClassName() { return (ASTList)getLeft(); } 
/*    */   public ASTList getArguments() {
/* 60 */     return (ASTList)getRight().getLeft();
/*    */   }
/* 62 */   public ASTList getArraySize() { return getArguments(); }
/*    */ 
/*    */   public ArrayInit getInitializer() {
/* 65 */     ASTree t = getRight().getRight();
/* 66 */     if (t == null) {
/* 67 */       return null;
/*    */     }
/* 69 */     return (ArrayInit)t.getLeft();
/*    */   }
/*    */   public void accept(Visitor v) throws CompileError {
/* 72 */     v.atNewExpr(this);
/*    */   }
/*    */   protected String getTag() {
/* 75 */     return this.newArray ? "new[]" : "new";
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.NewExpr
 * JD-Core Version:    0.6.2
 */