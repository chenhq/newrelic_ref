/*     */ package com.newrelic.javassist.compiler;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtPrimitiveType;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.compiler.ast.ASTList;
/*     */ import com.newrelic.javassist.compiler.ast.ASTree;
/*     */ import com.newrelic.javassist.compiler.ast.CallExpr;
/*     */ import com.newrelic.javassist.compiler.ast.CastExpr;
/*     */ import com.newrelic.javassist.compiler.ast.Expr;
/*     */ import com.newrelic.javassist.compiler.ast.Member;
/*     */ import com.newrelic.javassist.compiler.ast.Symbol;
/*     */ 
/*     */ public class JvstTypeChecker extends TypeChecker
/*     */ {
/*     */   private JvstCodeGen codeGen;
/*     */ 
/*     */   public JvstTypeChecker(CtClass cc, ClassPool cp, JvstCodeGen gen)
/*     */   {
/*  28 */     super(cc, cp);
/*  29 */     this.codeGen = gen;
/*     */   }
/*     */ 
/*     */   public void addNullIfVoid()
/*     */   {
/*  36 */     if (this.exprType == 344) {
/*  37 */       this.exprType = 307;
/*  38 */       this.arrayDim = 0;
/*  39 */       this.className = "java/lang/Object";
/*     */     }
/*     */   }
/*     */ 
/*     */   public void atMember(Member mem)
/*     */     throws CompileError
/*     */   {
/*  47 */     String name = mem.get();
/*  48 */     if (name.equals(this.codeGen.paramArrayName)) {
/*  49 */       this.exprType = 307;
/*  50 */       this.arrayDim = 1;
/*  51 */       this.className = "java/lang/Object";
/*     */     }
/*  53 */     else if (name.equals("$sig")) {
/*  54 */       this.exprType = 307;
/*  55 */       this.arrayDim = 1;
/*  56 */       this.className = "java/lang/Class";
/*     */     }
/*  58 */     else if ((name.equals("$type")) || (name.equals("$class")))
/*     */     {
/*  60 */       this.exprType = 307;
/*  61 */       this.arrayDim = 0;
/*  62 */       this.className = "java/lang/Class";
/*     */     }
/*     */     else {
/*  65 */       super.atMember(mem);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void atFieldAssign(Expr expr, int op, ASTree left, ASTree right) throws CompileError
/*     */   {
/*  71 */     if (((left instanceof Member)) && (((Member)left).get().equals(this.codeGen.paramArrayName)))
/*     */     {
/*  73 */       right.accept(this);
/*  74 */       CtClass[] params = this.codeGen.paramTypeList;
/*  75 */       if (params == null) {
/*  76 */         return;
/*     */       }
/*  78 */       int n = params.length;
/*  79 */       for (int i = 0; i < n; i++)
/*  80 */         compileUnwrapValue(params[i]);
/*     */     }
/*     */     else {
/*  83 */       super.atFieldAssign(expr, op, left, right);
/*     */     }
/*     */   }
/*     */ 
/*  87 */   public void atCastExpr(CastExpr expr) throws CompileError { ASTList classname = expr.getClassName();
/*  88 */     if ((classname != null) && (expr.getArrayDim() == 0)) {
/*  89 */       ASTree p = classname.head();
/*  90 */       if (((p instanceof Symbol)) && (classname.tail() == null)) {
/*  91 */         String typename = ((Symbol)p).get();
/*  92 */         if (typename.equals(this.codeGen.returnCastName)) {
/*  93 */           atCastToRtype(expr);
/*  94 */           return;
/*     */         }
/*  96 */         if (typename.equals("$w")) {
/*  97 */           atCastToWrapper(expr);
/*  98 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 103 */     super.atCastExpr(expr);
/*     */   }
/*     */ 
/*     */   protected void atCastToRtype(CastExpr expr)
/*     */     throws CompileError
/*     */   {
/* 111 */     CtClass returnType = this.codeGen.returnType;
/* 112 */     expr.getOprand().accept(this);
/* 113 */     if ((this.exprType == 344) || (CodeGen.isRefType(this.exprType)) || (this.arrayDim > 0)) {
/* 114 */       compileUnwrapValue(returnType);
/* 115 */     } else if ((returnType instanceof CtPrimitiveType)) {
/* 116 */       CtPrimitiveType pt = (CtPrimitiveType)returnType;
/* 117 */       int destType = MemberResolver.descToType(pt.getDescriptor());
/* 118 */       this.exprType = destType;
/* 119 */       this.arrayDim = 0;
/* 120 */       this.className = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void atCastToWrapper(CastExpr expr) throws CompileError {
/* 125 */     expr.getOprand().accept(this);
/* 126 */     if ((CodeGen.isRefType(this.exprType)) || (this.arrayDim > 0)) {
/* 127 */       return;
/*     */     }
/* 129 */     CtClass clazz = this.resolver.lookupClass(this.exprType, this.arrayDim, this.className);
/* 130 */     if ((clazz instanceof CtPrimitiveType)) {
/* 131 */       this.exprType = 307;
/* 132 */       this.arrayDim = 0;
/* 133 */       this.className = "java/lang/Object";
/*     */     }
/*     */   }
/*     */ 
/*     */   public void atCallExpr(CallExpr expr)
/*     */     throws CompileError
/*     */   {
/* 141 */     ASTree method = expr.oprand1();
/* 142 */     if ((method instanceof Member)) {
/* 143 */       String name = ((Member)method).get();
/* 144 */       if ((this.codeGen.procHandler != null) && (name.equals(this.codeGen.proceedName)))
/*     */       {
/* 146 */         this.codeGen.procHandler.setReturnType(this, (ASTList)expr.oprand2());
/*     */ 
/* 148 */         return;
/*     */       }
/* 150 */       if (name.equals("$cflow")) {
/* 151 */         atCflow((ASTList)expr.oprand2());
/* 152 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 156 */     super.atCallExpr(expr);
/*     */   }
/*     */ 
/*     */   protected void atCflow(ASTList cname)
/*     */     throws CompileError
/*     */   {
/* 162 */     this.exprType = 324;
/* 163 */     this.arrayDim = 0;
/* 164 */     this.className = null;
/*     */   }
/*     */ 
/*     */   public boolean isParamListName(ASTList args)
/*     */   {
/* 171 */     if ((this.codeGen.paramTypeList != null) && (args != null) && (args.tail() == null))
/*     */     {
/* 173 */       ASTree left = args.head();
/* 174 */       return ((left instanceof Member)) && (((Member)left).get().equals(this.codeGen.paramListName));
/*     */     }
/*     */ 
/* 178 */     return false;
/*     */   }
/*     */ 
/*     */   public int getMethodArgsLength(ASTList args) {
/* 182 */     String pname = this.codeGen.paramListName;
/* 183 */     int n = 0;
/* 184 */     while (args != null) {
/* 185 */       ASTree a = args.head();
/* 186 */       if (((a instanceof Member)) && (((Member)a).get().equals(pname))) {
/* 187 */         if (this.codeGen.paramTypeList != null)
/* 188 */           n += this.codeGen.paramTypeList.length;
/*     */       }
/*     */       else {
/* 191 */         n++;
/*     */       }
/* 193 */       args = args.tail();
/*     */     }
/*     */ 
/* 196 */     return n;
/*     */   }
/*     */ 
/*     */   public void atMethodArgs(ASTList args, int[] types, int[] dims, String[] cnames) throws CompileError
/*     */   {
/* 201 */     CtClass[] params = this.codeGen.paramTypeList;
/* 202 */     String pname = this.codeGen.paramListName;
/* 203 */     int i = 0;
/* 204 */     while (args != null) {
/* 205 */       ASTree a = args.head();
/* 206 */       if (((a instanceof Member)) && (((Member)a).get().equals(pname))) {
/* 207 */         if (params != null) {
/* 208 */           int n = params.length;
/* 209 */           for (int k = 0; k < n; k++) {
/* 210 */             CtClass p = params[k];
/* 211 */             setType(p);
/* 212 */             types[i] = this.exprType;
/* 213 */             dims[i] = this.arrayDim;
/* 214 */             cnames[i] = this.className;
/* 215 */             i++;
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 220 */         a.accept(this);
/* 221 */         types[i] = this.exprType;
/* 222 */         dims[i] = this.arrayDim;
/* 223 */         cnames[i] = this.className;
/* 224 */         i++;
/*     */       }
/*     */ 
/* 227 */       args = args.tail();
/*     */     }
/*     */   }
/*     */ 
/*     */   void compileInvokeSpecial(ASTree target, String classname, String methodname, String descriptor, ASTList args)
/*     */     throws CompileError
/*     */   {
/* 238 */     target.accept(this);
/* 239 */     int nargs = getMethodArgsLength(args);
/* 240 */     atMethodArgs(args, new int[nargs], new int[nargs], new String[nargs]);
/*     */ 
/* 242 */     setReturnType(descriptor);
/* 243 */     addNullIfVoid();
/*     */   }
/*     */ 
/*     */   protected void compileUnwrapValue(CtClass type) throws CompileError
/*     */   {
/* 248 */     if (type == CtClass.voidType)
/* 249 */       addNullIfVoid();
/*     */     else
/* 251 */       setType(type);
/*     */   }
/*     */ 
/*     */   public void setType(CtClass type)
/*     */     throws CompileError
/*     */   {
/* 258 */     setType(type, 0);
/*     */   }
/*     */ 
/*     */   private void setType(CtClass type, int dim) throws CompileError {
/* 262 */     if (type.isPrimitive()) {
/* 263 */       CtPrimitiveType pt = (CtPrimitiveType)type;
/* 264 */       this.exprType = MemberResolver.descToType(pt.getDescriptor());
/* 265 */       this.arrayDim = dim;
/* 266 */       this.className = null;
/*     */     }
/* 268 */     else if (type.isArray()) {
/*     */       try {
/* 270 */         setType(type.getComponentType(), dim + 1);
/*     */       }
/*     */       catch (NotFoundException e) {
/* 273 */         throw new CompileError("undefined type: " + type.getName());
/*     */       }
/*     */     } else {
/* 276 */       this.exprType = 307;
/* 277 */       this.arrayDim = dim;
/* 278 */       this.className = MemberResolver.javaToJvmName(type.getName());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.JvstTypeChecker
 * JD-Core Version:    0.6.2
 */