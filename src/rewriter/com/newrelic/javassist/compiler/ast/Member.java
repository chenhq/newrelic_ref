/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.CtField;
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class Member extends Symbol
/*    */ {
/*    */   private CtField field;
/*    */ 
/*    */   public Member(String name)
/*    */   {
/* 30 */     super(name);
/* 31 */     this.field = null;
/*    */   }
/*    */   public void setField(CtField f) {
/* 34 */     this.field = f;
/*    */   }
/* 36 */   public CtField getField() { return this.field; } 
/*    */   public void accept(Visitor v) throws CompileError {
/* 38 */     v.atMember(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.Member
 * JD-Core Version:    0.6.2
 */