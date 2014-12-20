/*     */ package com.newrelic.javassist.compiler.ast;
/*     */ 
/*     */ import com.newrelic.javassist.compiler.CompileError;
/*     */ import com.newrelic.javassist.compiler.TokenId;
/*     */ 
/*     */ public class Declarator extends ASTList
/*     */   implements TokenId
/*     */ {
/*     */   protected int varType;
/*     */   protected int arrayDim;
/*     */   protected int localVar;
/*     */   protected String qualifiedClass;
/*     */ 
/*     */   public Declarator(int type, int dim)
/*     */   {
/*  31 */     super(null);
/*  32 */     this.varType = type;
/*  33 */     this.arrayDim = dim;
/*  34 */     this.localVar = -1;
/*  35 */     this.qualifiedClass = null;
/*     */   }
/*     */ 
/*     */   public Declarator(ASTList className, int dim) {
/*  39 */     super(null);
/*  40 */     this.varType = 307;
/*  41 */     this.arrayDim = dim;
/*  42 */     this.localVar = -1;
/*  43 */     this.qualifiedClass = astToClassName(className, '/');
/*     */   }
/*     */ 
/*     */   public Declarator(int type, String jvmClassName, int dim, int var, Symbol sym)
/*     */   {
/*  50 */     super(null);
/*  51 */     this.varType = type;
/*  52 */     this.arrayDim = dim;
/*  53 */     this.localVar = var;
/*  54 */     this.qualifiedClass = jvmClassName;
/*  55 */     setLeft(sym);
/*  56 */     append(this, null);
/*     */   }
/*     */ 
/*     */   public Declarator make(Symbol sym, int dim, ASTree init) {
/*  60 */     Declarator d = new Declarator(this.varType, this.arrayDim + dim);
/*  61 */     d.qualifiedClass = this.qualifiedClass;
/*  62 */     d.setLeft(sym);
/*  63 */     append(d, init);
/*  64 */     return d;
/*     */   }
/*     */ 
/*     */   public int getType()
/*     */   {
/*  70 */     return this.varType;
/*     */   }
/*  72 */   public int getArrayDim() { return this.arrayDim; } 
/*     */   public void addArrayDim(int d) {
/*  74 */     this.arrayDim += d;
/*     */   }
/*  76 */   public String getClassName() { return this.qualifiedClass; } 
/*     */   public void setClassName(String s) {
/*  78 */     this.qualifiedClass = s;
/*     */   }
/*  80 */   public Symbol getVariable() { return (Symbol)getLeft(); } 
/*     */   public void setVariable(Symbol sym) {
/*  82 */     setLeft(sym);
/*     */   }
/*     */   public ASTree getInitializer() {
/*  85 */     ASTList t = tail();
/*  86 */     if (t != null) {
/*  87 */       return t.head();
/*     */     }
/*  89 */     return null;
/*     */   }
/*     */   public void setLocalVar(int n) {
/*  92 */     this.localVar = n;
/*     */   }
/*  94 */   public int getLocalVar() { return this.localVar; } 
/*     */   public String getTag() {
/*  96 */     return "decl";
/*     */   }
/*     */   public void accept(Visitor v) throws CompileError {
/*  99 */     v.atDeclarator(this);
/*     */   }
/*     */ 
/*     */   public static String astToClassName(ASTList name, char sep) {
/* 103 */     if (name == null) {
/* 104 */       return null;
/*     */     }
/* 106 */     StringBuffer sbuf = new StringBuffer();
/* 107 */     astToClassName(sbuf, name, sep);
/* 108 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   private static void astToClassName(StringBuffer sbuf, ASTList name, char sep)
/*     */   {
/*     */     while (true) {
/* 114 */       ASTree h = name.head();
/* 115 */       if ((h instanceof Symbol))
/* 116 */         sbuf.append(((Symbol)h).get());
/* 117 */       else if ((h instanceof ASTList)) {
/* 118 */         astToClassName(sbuf, (ASTList)h, sep);
/*     */       }
/* 120 */       name = name.tail();
/* 121 */       if (name == null) {
/*     */         break;
/*     */       }
/* 124 */       sbuf.append(sep);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.Declarator
 * JD-Core Version:    0.6.2
 */