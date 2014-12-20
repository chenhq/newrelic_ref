/*     */ package com.newrelic.javassist.compiler;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtPrimitiveType;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.Bytecode;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import com.newrelic.javassist.compiler.ast.ASTList;
/*     */ import com.newrelic.javassist.compiler.ast.ASTree;
/*     */ import com.newrelic.javassist.compiler.ast.CallExpr;
/*     */ import com.newrelic.javassist.compiler.ast.CastExpr;
/*     */ import com.newrelic.javassist.compiler.ast.Declarator;
/*     */ import com.newrelic.javassist.compiler.ast.Expr;
/*     */ import com.newrelic.javassist.compiler.ast.Member;
/*     */ import com.newrelic.javassist.compiler.ast.Stmnt;
/*     */ import com.newrelic.javassist.compiler.ast.Symbol;
/*     */ 
/*     */ public class JvstCodeGen extends MemberCodeGen
/*     */ {
/*  26 */   String paramArrayName = null;
/*  27 */   String paramListName = null;
/*  28 */   CtClass[] paramTypeList = null;
/*  29 */   private int paramVarBase = 0;
/*  30 */   private boolean useParam0 = false;
/*  31 */   private String param0Type = null;
/*     */   public static final String sigName = "$sig";
/*     */   public static final String dollarTypeName = "$type";
/*     */   public static final String clazzName = "$class";
/*  35 */   private CtClass dollarType = null;
/*  36 */   CtClass returnType = null;
/*  37 */   String returnCastName = null;
/*  38 */   private String returnVarName = null;
/*     */   public static final String wrapperCastName = "$w";
/*  40 */   String proceedName = null;
/*     */   public static final String cflowName = "$cflow";
/*  42 */   ProceedHandler procHandler = null;
/*     */ 
/*     */   public JvstCodeGen(Bytecode b, CtClass cc, ClassPool cp) {
/*  45 */     super(b, cc, cp);
/*  46 */     setTypeChecker(new JvstTypeChecker(cc, cp, this));
/*     */   }
/*     */ 
/*     */   private int indexOfParam1()
/*     */   {
/*  52 */     return this.paramVarBase + (this.useParam0 ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public void setProceedHandler(ProceedHandler h, String name)
/*     */   {
/*  61 */     this.proceedName = name;
/*  62 */     this.procHandler = h;
/*     */   }
/*     */ 
/*     */   public void addNullIfVoid()
/*     */   {
/*  69 */     if (this.exprType == 344) {
/*  70 */       this.bytecode.addOpcode(1);
/*  71 */       this.exprType = 307;
/*  72 */       this.arrayDim = 0;
/*  73 */       this.className = "java/lang/Object";
/*     */     }
/*     */   }
/*     */ 
/*     */   public void atMember(Member mem)
/*     */     throws CompileError
/*     */   {
/*  81 */     String name = mem.get();
/*  82 */     if (name.equals(this.paramArrayName)) {
/*  83 */       compileParameterList(this.bytecode, this.paramTypeList, indexOfParam1());
/*  84 */       this.exprType = 307;
/*  85 */       this.arrayDim = 1;
/*  86 */       this.className = "java/lang/Object";
/*     */     }
/*  88 */     else if (name.equals("$sig")) {
/*  89 */       this.bytecode.addLdc(Descriptor.ofMethod(this.returnType, this.paramTypeList));
/*  90 */       this.bytecode.addInvokestatic("com/newrelic/javassist/runtime/Desc", "getParams", "(Ljava/lang/String;)[Ljava/lang/Class;");
/*     */ 
/*  92 */       this.exprType = 307;
/*  93 */       this.arrayDim = 1;
/*  94 */       this.className = "java/lang/Class";
/*     */     }
/*  96 */     else if (name.equals("$type")) {
/*  97 */       if (this.dollarType == null) {
/*  98 */         throw new CompileError("$type is not available");
/*     */       }
/* 100 */       this.bytecode.addLdc(Descriptor.of(this.dollarType));
/* 101 */       callGetType("getType");
/*     */     }
/* 103 */     else if (name.equals("$class")) {
/* 104 */       if (this.param0Type == null) {
/* 105 */         throw new CompileError("$class is not available");
/*     */       }
/* 107 */       this.bytecode.addLdc(this.param0Type);
/* 108 */       callGetType("getClazz");
/*     */     }
/*     */     else {
/* 111 */       super.atMember(mem);
/*     */     }
/*     */   }
/*     */ 
/* 115 */   private void callGetType(String method) { this.bytecode.addInvokestatic("com/newrelic/javassist/runtime/Desc", method, "(Ljava/lang/String;)Ljava/lang/Class;");
/*     */ 
/* 117 */     this.exprType = 307;
/* 118 */     this.arrayDim = 0;
/* 119 */     this.className = "java/lang/Class";
/*     */   }
/*     */ 
/*     */   protected void atFieldAssign(Expr expr, int op, ASTree left, ASTree right, boolean doDup)
/*     */     throws CompileError
/*     */   {
/* 125 */     if (((left instanceof Member)) && (((Member)left).get().equals(this.paramArrayName)))
/*     */     {
/* 127 */       if (op != 61) {
/* 128 */         throw new CompileError("bad operator for " + this.paramArrayName);
/*     */       }
/* 130 */       right.accept(this);
/* 131 */       if ((this.arrayDim != 1) || (this.exprType != 307)) {
/* 132 */         throw new CompileError("invalid type for " + this.paramArrayName);
/*     */       }
/* 134 */       atAssignParamList(this.paramTypeList, this.bytecode);
/* 135 */       if (!doDup)
/* 136 */         this.bytecode.addOpcode(87);
/*     */     }
/*     */     else {
/* 139 */       super.atFieldAssign(expr, op, left, right, doDup);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void atAssignParamList(CtClass[] params, Bytecode code) throws CompileError
/*     */   {
/* 145 */     if (params == null) {
/* 146 */       return;
/*     */     }
/* 148 */     int varNo = indexOfParam1();
/* 149 */     int n = params.length;
/* 150 */     for (int i = 0; i < n; i++) {
/* 151 */       code.addOpcode(89);
/* 152 */       code.addIconst(i);
/* 153 */       code.addOpcode(50);
/* 154 */       compileUnwrapValue(params[i], code);
/* 155 */       code.addStore(varNo, params[i]);
/* 156 */       varNo += (is2word(this.exprType, this.arrayDim) ? 2 : 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void atCastExpr(CastExpr expr) throws CompileError {
/* 161 */     ASTList classname = expr.getClassName();
/* 162 */     if ((classname != null) && (expr.getArrayDim() == 0)) {
/* 163 */       ASTree p = classname.head();
/* 164 */       if (((p instanceof Symbol)) && (classname.tail() == null)) {
/* 165 */         String typename = ((Symbol)p).get();
/* 166 */         if (typename.equals(this.returnCastName)) {
/* 167 */           atCastToRtype(expr);
/* 168 */           return;
/*     */         }
/* 170 */         if (typename.equals("$w")) {
/* 171 */           atCastToWrapper(expr);
/* 172 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 177 */     super.atCastExpr(expr);
/*     */   }
/*     */ 
/*     */   protected void atCastToRtype(CastExpr expr)
/*     */     throws CompileError
/*     */   {
/* 185 */     expr.getOprand().accept(this);
/* 186 */     if ((this.exprType == 344) || (isRefType(this.exprType)) || (this.arrayDim > 0)) {
/* 187 */       compileUnwrapValue(this.returnType, this.bytecode);
/* 188 */     } else if ((this.returnType instanceof CtPrimitiveType)) {
/* 189 */       CtPrimitiveType pt = (CtPrimitiveType)this.returnType;
/* 190 */       int destType = MemberResolver.descToType(pt.getDescriptor());
/* 191 */       atNumCastExpr(this.exprType, destType);
/* 192 */       this.exprType = destType;
/* 193 */       this.arrayDim = 0;
/* 194 */       this.className = null;
/*     */     }
/*     */     else {
/* 197 */       throw new CompileError("invalid cast");
/*     */     }
/*     */   }
/*     */ 
/* 201 */   protected void atCastToWrapper(CastExpr expr) throws CompileError { expr.getOprand().accept(this);
/* 202 */     if ((isRefType(this.exprType)) || (this.arrayDim > 0)) {
/* 203 */       return;
/*     */     }
/* 205 */     CtClass clazz = this.resolver.lookupClass(this.exprType, this.arrayDim, this.className);
/* 206 */     if ((clazz instanceof CtPrimitiveType)) {
/* 207 */       CtPrimitiveType pt = (CtPrimitiveType)clazz;
/* 208 */       String wrapper = pt.getWrapperName();
/* 209 */       this.bytecode.addNew(wrapper);
/* 210 */       this.bytecode.addOpcode(89);
/* 211 */       if (pt.getDataSize() > 1)
/* 212 */         this.bytecode.addOpcode(94);
/*     */       else {
/* 214 */         this.bytecode.addOpcode(93);
/*     */       }
/* 216 */       this.bytecode.addOpcode(88);
/* 217 */       this.bytecode.addInvokespecial(wrapper, "<init>", "(" + pt.getDescriptor() + ")V");
/*     */ 
/* 220 */       this.exprType = 307;
/* 221 */       this.arrayDim = 0;
/* 222 */       this.className = "java/lang/Object";
/*     */     }
/*     */   }
/*     */ 
/*     */   public void atCallExpr(CallExpr expr)
/*     */     throws CompileError
/*     */   {
/* 230 */     ASTree method = expr.oprand1();
/* 231 */     if ((method instanceof Member)) {
/* 232 */       String name = ((Member)method).get();
/* 233 */       if ((this.procHandler != null) && (name.equals(this.proceedName))) {
/* 234 */         this.procHandler.doit(this, this.bytecode, (ASTList)expr.oprand2());
/* 235 */         return;
/*     */       }
/* 237 */       if (name.equals("$cflow")) {
/* 238 */         atCflow((ASTList)expr.oprand2());
/* 239 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 243 */     super.atCallExpr(expr);
/*     */   }
/*     */ 
/*     */   protected void atCflow(ASTList cname)
/*     */     throws CompileError
/*     */   {
/* 249 */     StringBuffer sbuf = new StringBuffer();
/* 250 */     if ((cname == null) || (cname.tail() != null)) {
/* 251 */       throw new CompileError("bad $cflow");
/*     */     }
/* 253 */     makeCflowName(sbuf, cname.head());
/* 254 */     String name = sbuf.toString();
/* 255 */     Object[] names = this.resolver.getClassPool().lookupCflow(name);
/* 256 */     if (names == null) {
/* 257 */       throw new CompileError("no such a $cflow: " + name);
/*     */     }
/* 259 */     this.bytecode.addGetstatic((String)names[0], (String)names[1], "Ljavassist/runtime/Cflow;");
/*     */ 
/* 261 */     this.bytecode.addInvokevirtual("com.newrelic.javassist.runtime.Cflow", "value", "()I");
/*     */ 
/* 263 */     this.exprType = 324;
/* 264 */     this.arrayDim = 0;
/* 265 */     this.className = null;
/*     */   }
/*     */ 
/*     */   private static void makeCflowName(StringBuffer sbuf, ASTree name)
/*     */     throws CompileError
/*     */   {
/* 276 */     if ((name instanceof Symbol)) {
/* 277 */       sbuf.append(((Symbol)name).get());
/* 278 */       return;
/*     */     }
/* 280 */     if ((name instanceof Expr)) {
/* 281 */       Expr expr = (Expr)name;
/* 282 */       if (expr.getOperator() == 46) {
/* 283 */         makeCflowName(sbuf, expr.oprand1());
/* 284 */         sbuf.append('.');
/* 285 */         makeCflowName(sbuf, expr.oprand2());
/* 286 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 290 */     throw new CompileError("bad $cflow");
/*     */   }
/*     */ 
/*     */   public boolean isParamListName(ASTList args)
/*     */   {
/* 297 */     if ((this.paramTypeList != null) && (args != null) && (args.tail() == null))
/*     */     {
/* 299 */       ASTree left = args.head();
/* 300 */       return ((left instanceof Member)) && (((Member)left).get().equals(this.paramListName));
/*     */     }
/*     */ 
/* 304 */     return false;
/*     */   }
/*     */ 
/*     */   public int getMethodArgsLength(ASTList args)
/*     */   {
/* 317 */     String pname = this.paramListName;
/* 318 */     int n = 0;
/* 319 */     while (args != null) {
/* 320 */       ASTree a = args.head();
/* 321 */       if (((a instanceof Member)) && (((Member)a).get().equals(pname))) {
/* 322 */         if (this.paramTypeList != null)
/* 323 */           n += this.paramTypeList.length;
/*     */       }
/*     */       else {
/* 326 */         n++;
/*     */       }
/* 328 */       args = args.tail();
/*     */     }
/*     */ 
/* 331 */     return n;
/*     */   }
/*     */ 
/*     */   public void atMethodArgs(ASTList args, int[] types, int[] dims, String[] cnames) throws CompileError
/*     */   {
/* 336 */     CtClass[] params = this.paramTypeList;
/* 337 */     String pname = this.paramListName;
/* 338 */     int i = 0;
/* 339 */     while (args != null) {
/* 340 */       ASTree a = args.head();
/* 341 */       if (((a instanceof Member)) && (((Member)a).get().equals(pname))) {
/* 342 */         if (params != null) {
/* 343 */           int n = params.length;
/* 344 */           int regno = indexOfParam1();
/* 345 */           for (int k = 0; k < n; k++) {
/* 346 */             CtClass p = params[k];
/* 347 */             regno += this.bytecode.addLoad(regno, p);
/* 348 */             setType(p);
/* 349 */             types[i] = this.exprType;
/* 350 */             dims[i] = this.arrayDim;
/* 351 */             cnames[i] = this.className;
/* 352 */             i++;
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 357 */         a.accept(this);
/* 358 */         types[i] = this.exprType;
/* 359 */         dims[i] = this.arrayDim;
/* 360 */         cnames[i] = this.className;
/* 361 */         i++;
/*     */       }
/*     */ 
/* 364 */       args = args.tail();
/*     */     }
/*     */   }
/*     */ 
/*     */   void compileInvokeSpecial(ASTree target, String classname, String methodname, String descriptor, ASTList args)
/*     */     throws CompileError
/*     */   {
/* 400 */     target.accept(this);
/* 401 */     int nargs = getMethodArgsLength(args);
/* 402 */     atMethodArgs(args, new int[nargs], new int[nargs], new String[nargs]);
/*     */ 
/* 404 */     this.bytecode.addInvokespecial(classname, methodname, descriptor);
/* 405 */     setReturnType(descriptor, false, false);
/* 406 */     addNullIfVoid();
/*     */   }
/*     */ 
/*     */   protected void atReturnStmnt(Stmnt st)
/*     */     throws CompileError
/*     */   {
/* 413 */     ASTree result = st.getLeft();
/* 414 */     if ((result != null) && (this.returnType == CtClass.voidType)) {
/* 415 */       compileExpr(result);
/* 416 */       if (is2word(this.exprType, this.arrayDim))
/* 417 */         this.bytecode.addOpcode(88);
/* 418 */       else if (this.exprType != 344) {
/* 419 */         this.bytecode.addOpcode(87);
/*     */       }
/* 421 */       result = null;
/*     */     }
/*     */ 
/* 424 */     atReturnStmnt2(result);
/*     */   }
/*     */ 
/*     */   public int recordReturnType(CtClass type, String castName, String resultName, SymbolTable tbl)
/*     */     throws CompileError
/*     */   {
/* 440 */     this.returnType = type;
/* 441 */     this.returnCastName = castName;
/* 442 */     this.returnVarName = resultName;
/* 443 */     if (resultName == null) {
/* 444 */       return -1;
/*     */     }
/* 446 */     int varNo = getMaxLocals();
/* 447 */     int locals = varNo + recordVar(type, resultName, varNo, tbl);
/* 448 */     setMaxLocals(locals);
/* 449 */     return varNo;
/*     */   }
/*     */ 
/*     */   public void recordType(CtClass t)
/*     */   {
/* 457 */     this.dollarType = t;
/*     */   }
/*     */ 
/*     */   public int recordParams(CtClass[] params, boolean isStatic, String prefix, String paramVarName, String paramsName, SymbolTable tbl)
/*     */     throws CompileError
/*     */   {
/* 470 */     return recordParams(params, isStatic, prefix, paramVarName, paramsName, !isStatic, 0, getThisName(), tbl);
/*     */   }
/*     */ 
/*     */   public int recordParams(CtClass[] params, boolean isStatic, String prefix, String paramVarName, String paramsName, boolean use0, int paramBase, String target, SymbolTable tbl)
/*     */     throws CompileError
/*     */   {
/* 501 */     this.paramTypeList = params;
/* 502 */     this.paramArrayName = paramVarName;
/* 503 */     this.paramListName = paramsName;
/* 504 */     this.paramVarBase = paramBase;
/* 505 */     this.useParam0 = use0;
/*     */ 
/* 507 */     if (target != null) {
/* 508 */       this.param0Type = MemberResolver.jvmToJavaName(target);
/*     */     }
/* 510 */     this.inStaticMethod = isStatic;
/* 511 */     int varNo = paramBase;
/* 512 */     if (use0) {
/* 513 */       String varName = prefix + "0";
/* 514 */       Declarator decl = new Declarator(307, MemberResolver.javaToJvmName(target), 0, varNo++, new Symbol(varName));
/*     */ 
/* 517 */       tbl.append(varName, decl);
/*     */     }
/*     */ 
/* 520 */     for (int i = 0; i < params.length; i++) {
/* 521 */       varNo += recordVar(params[i], prefix + (i + 1), varNo, tbl);
/*     */     }
/* 523 */     if (getMaxLocals() < varNo) {
/* 524 */       setMaxLocals(varNo);
/*     */     }
/* 526 */     return varNo;
/*     */   }
/*     */ 
/*     */   public int recordVariable(CtClass type, String varName, SymbolTable tbl)
/*     */     throws CompileError
/*     */   {
/* 538 */     if (varName == null) {
/* 539 */       return -1;
/*     */     }
/* 541 */     int varNo = getMaxLocals();
/* 542 */     int locals = varNo + recordVar(type, varName, varNo, tbl);
/* 543 */     setMaxLocals(locals);
/* 544 */     return varNo;
/*     */   }
/*     */ 
/*     */   private int recordVar(CtClass cc, String varName, int varNo, SymbolTable tbl)
/*     */     throws CompileError
/*     */   {
/* 551 */     if (cc == CtClass.voidType) {
/* 552 */       this.exprType = 307;
/* 553 */       this.arrayDim = 0;
/* 554 */       this.className = "java/lang/Object";
/*     */     }
/*     */     else {
/* 557 */       setType(cc);
/*     */     }
/* 559 */     Declarator decl = new Declarator(this.exprType, this.className, this.arrayDim, varNo, new Symbol(varName));
/*     */ 
/* 562 */     tbl.append(varName, decl);
/* 563 */     return is2word(this.exprType, this.arrayDim) ? 2 : 1;
/*     */   }
/*     */ 
/*     */   public void recordVariable(String typeDesc, String varName, int varNo, SymbolTable tbl)
/*     */     throws CompileError
/*     */   {
/* 577 */     int dim = 0;
/*     */     char c;
/* 578 */     while ((c = typeDesc.charAt(dim)) == '[') {
/* 579 */       dim++;
/*     */     }
/* 581 */     int type = MemberResolver.descToType(c);
/* 582 */     String cname = null;
/* 583 */     if (type == 307) {
/* 584 */       if (dim == 0)
/* 585 */         cname = typeDesc.substring(1, typeDesc.length() - 1);
/*     */       else {
/* 587 */         cname = typeDesc.substring(dim + 1, typeDesc.length() - 1);
/*     */       }
/*     */     }
/* 590 */     Declarator decl = new Declarator(type, cname, dim, varNo, new Symbol(varName));
/*     */ 
/* 592 */     tbl.append(varName, decl);
/*     */   }
/*     */ 
/*     */   public static int compileParameterList(Bytecode code, CtClass[] params, int regno)
/*     */   {
/* 606 */     if (params == null) {
/* 607 */       code.addIconst(0);
/* 608 */       code.addAnewarray("java.lang.Object");
/* 609 */       return 1;
/*     */     }
/*     */ 
/* 612 */     CtClass[] args = new CtClass[1];
/* 613 */     int n = params.length;
/* 614 */     code.addIconst(n);
/* 615 */     code.addAnewarray("java.lang.Object");
/* 616 */     for (int i = 0; i < n; i++) {
/* 617 */       code.addOpcode(89);
/* 618 */       code.addIconst(i);
/* 619 */       if (params[i].isPrimitive()) {
/* 620 */         CtPrimitiveType pt = (CtPrimitiveType)params[i];
/* 621 */         String wrapper = pt.getWrapperName();
/* 622 */         code.addNew(wrapper);
/* 623 */         code.addOpcode(89);
/* 624 */         int s = code.addLoad(regno, pt);
/* 625 */         regno += s;
/* 626 */         args[0] = pt;
/* 627 */         code.addInvokespecial(wrapper, "<init>", Descriptor.ofMethod(CtClass.voidType, args));
/*     */       }
/*     */       else
/*     */       {
/* 632 */         code.addAload(regno);
/* 633 */         regno++;
/*     */       }
/*     */ 
/* 636 */       code.addOpcode(83);
/*     */     }
/*     */ 
/* 639 */     return 8;
/*     */   }
/*     */ 
/*     */   protected void compileUnwrapValue(CtClass type, Bytecode code)
/*     */     throws CompileError
/*     */   {
/* 646 */     if (type == CtClass.voidType) {
/* 647 */       addNullIfVoid();
/* 648 */       return;
/*     */     }
/*     */ 
/* 651 */     if (this.exprType == 344) {
/* 652 */       throw new CompileError("invalid type for " + this.returnCastName);
/*     */     }
/* 654 */     if ((type instanceof CtPrimitiveType)) {
/* 655 */       CtPrimitiveType pt = (CtPrimitiveType)type;
/*     */ 
/* 657 */       String wrapper = pt.getWrapperName();
/* 658 */       code.addCheckcast(wrapper);
/* 659 */       code.addInvokevirtual(wrapper, pt.getGetMethodName(), pt.getGetMethodDescriptor());
/*     */ 
/* 661 */       setType(type);
/*     */     }
/*     */     else {
/* 664 */       code.addCheckcast(type);
/* 665 */       setType(type);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setType(CtClass type)
/*     */     throws CompileError
/*     */   {
/* 673 */     setType(type, 0);
/*     */   }
/*     */ 
/*     */   private void setType(CtClass type, int dim) throws CompileError {
/* 677 */     if (type.isPrimitive()) {
/* 678 */       CtPrimitiveType pt = (CtPrimitiveType)type;
/* 679 */       this.exprType = MemberResolver.descToType(pt.getDescriptor());
/* 680 */       this.arrayDim = dim;
/* 681 */       this.className = null;
/*     */     }
/* 683 */     else if (type.isArray()) {
/*     */       try {
/* 685 */         setType(type.getComponentType(), dim + 1);
/*     */       }
/*     */       catch (NotFoundException e) {
/* 688 */         throw new CompileError("undefined type: " + type.getName());
/*     */       }
/*     */     } else {
/* 691 */       this.exprType = 307;
/* 692 */       this.arrayDim = dim;
/* 693 */       this.className = MemberResolver.javaToJvmName(type.getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void doNumCast(CtClass type)
/*     */     throws CompileError
/*     */   {
/* 700 */     if ((this.arrayDim == 0) && (!isRefType(this.exprType)))
/* 701 */       if ((type instanceof CtPrimitiveType)) {
/* 702 */         CtPrimitiveType pt = (CtPrimitiveType)type;
/* 703 */         atNumCastExpr(this.exprType, MemberResolver.descToType(pt.getDescriptor()));
/*     */       }
/*     */       else
/*     */       {
/* 707 */         throw new CompileError("type mismatch");
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.JvstCodeGen
 * JD-Core Version:    0.6.2
 */