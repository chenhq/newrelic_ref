/*     */ package com.newrelic.javassist.compiler.ast;
/*     */ 
/*     */ import com.newrelic.javassist.compiler.CompileError;
/*     */ 
/*     */ public class ASTList extends ASTree
/*     */ {
/*     */   private ASTree left;
/*     */   private ASTList right;
/*     */ 
/*     */   public ASTList(ASTree _head, ASTList _tail)
/*     */   {
/*  29 */     this.left = _head;
/*  30 */     this.right = _tail;
/*     */   }
/*     */ 
/*     */   public ASTList(ASTree _head) {
/*  34 */     this.left = _head;
/*  35 */     this.right = null;
/*     */   }
/*     */ 
/*     */   public static ASTList make(ASTree e1, ASTree e2, ASTree e3) {
/*  39 */     return new ASTList(e1, new ASTList(e2, new ASTList(e3)));
/*     */   }
/*     */   public ASTree getLeft() {
/*  42 */     return this.left;
/*     */   }
/*  44 */   public ASTree getRight() { return this.right; } 
/*     */   public void setLeft(ASTree _left) {
/*  46 */     this.left = _left;
/*     */   }
/*     */   public void setRight(ASTree _right) {
/*  49 */     this.right = ((ASTList)_right);
/*     */   }
/*     */ 
/*     */   public ASTree head()
/*     */   {
/*  55 */     return this.left;
/*     */   }
/*     */   public void setHead(ASTree _head) {
/*  58 */     this.left = _head;
/*     */   }
/*     */ 
/*     */   public ASTList tail()
/*     */   {
/*  64 */     return this.right;
/*     */   }
/*     */   public void setTail(ASTList _tail) {
/*  67 */     this.right = _tail;
/*     */   }
/*     */   public void accept(Visitor v) throws CompileError {
/*  70 */     v.atASTList(this);
/*     */   }
/*     */   public String toString() {
/*  73 */     StringBuffer sbuf = new StringBuffer();
/*  74 */     sbuf.append("(<");
/*  75 */     sbuf.append(getTag());
/*  76 */     sbuf.append('>');
/*  77 */     ASTList list = this;
/*  78 */     while (list != null) {
/*  79 */       sbuf.append(' ');
/*  80 */       ASTree a = list.left;
/*  81 */       sbuf.append(a == null ? "<null>" : a.toString());
/*  82 */       list = list.right;
/*     */     }
/*     */ 
/*  85 */     sbuf.append(')');
/*  86 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   public int length()
/*     */   {
/*  93 */     return length(this);
/*     */   }
/*     */ 
/*     */   public static int length(ASTList list) {
/*  97 */     if (list == null) {
/*  98 */       return 0;
/*     */     }
/* 100 */     int n = 0;
/* 101 */     while (list != null) {
/* 102 */       list = list.right;
/* 103 */       n++;
/*     */     }
/*     */ 
/* 106 */     return n;
/*     */   }
/*     */ 
/*     */   public ASTList sublist(int nth)
/*     */   {
/* 116 */     ASTList list = this;
/* 117 */     while (nth-- > 0) {
/* 118 */       list = list.right;
/*     */     }
/* 120 */     return list;
/*     */   }
/*     */ 
/*     */   public boolean subst(ASTree newObj, ASTree oldObj)
/*     */   {
/* 128 */     for (ASTList list = this; list != null; list = list.right) {
/* 129 */       if (list.left == oldObj) {
/* 130 */         list.left = newObj;
/* 131 */         return true;
/*     */       }
/*     */     }
/* 134 */     return false;
/*     */   }
/*     */ 
/*     */   public static ASTList append(ASTList a, ASTree b)
/*     */   {
/* 141 */     return concat(a, new ASTList(b));
/*     */   }
/*     */ 
/*     */   public static ASTList concat(ASTList a, ASTList b)
/*     */   {
/* 148 */     if (a == null) {
/* 149 */       return b;
/*     */     }
/* 151 */     ASTList list = a;
/* 152 */     while (list.right != null) {
/* 153 */       list = list.right;
/*     */     }
/* 155 */     list.right = b;
/* 156 */     return a;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.ASTList
 * JD-Core Version:    0.6.2
 */