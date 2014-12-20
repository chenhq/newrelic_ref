/*    */ package com.newrelic.javassist.compiler;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.ast.Declarator;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public final class SymbolTable extends HashMap
/*    */ {
/*    */   private SymbolTable parent;
/*    */ 
/*    */   public SymbolTable()
/*    */   {
/* 24 */     this(null);
/*    */   }
/*    */ 
/*    */   public SymbolTable(SymbolTable p) {
/* 28 */     this.parent = p;
/*    */   }
/*    */   public SymbolTable getParent() {
/* 31 */     return this.parent;
/*    */   }
/*    */   public Declarator lookup(String name) {
/* 34 */     Declarator found = (Declarator)get(name);
/* 35 */     if ((found == null) && (this.parent != null)) {
/* 36 */       return this.parent.lookup(name);
/*    */     }
/* 38 */     return found;
/*    */   }
/*    */ 
/*    */   public void append(String name, Declarator value) {
/* 42 */     put(name, value);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.SymbolTable
 * JD-Core Version:    0.6.2
 */