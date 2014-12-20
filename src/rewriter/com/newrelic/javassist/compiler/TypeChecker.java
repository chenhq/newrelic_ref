/*      */ package com.newrelic.javassist.compiler;
/*      */ 
/*      */ import com.newrelic.javassist.ClassPool;
/*      */ import com.newrelic.javassist.CtClass;
/*      */ import com.newrelic.javassist.CtField;
/*      */ import com.newrelic.javassist.Modifier;
/*      */ import com.newrelic.javassist.NotFoundException;
/*      */ import com.newrelic.javassist.bytecode.FieldInfo;
/*      */ import com.newrelic.javassist.bytecode.MethodInfo;
/*      */ import com.newrelic.javassist.bytecode.Opcode;
/*      */ import com.newrelic.javassist.compiler.ast.ASTList;
/*      */ import com.newrelic.javassist.compiler.ast.ASTree;
/*      */ import com.newrelic.javassist.compiler.ast.ArrayInit;
/*      */ import com.newrelic.javassist.compiler.ast.AssignExpr;
/*      */ import com.newrelic.javassist.compiler.ast.BinExpr;
/*      */ import com.newrelic.javassist.compiler.ast.CallExpr;
/*      */ import com.newrelic.javassist.compiler.ast.CastExpr;
/*      */ import com.newrelic.javassist.compiler.ast.CondExpr;
/*      */ import com.newrelic.javassist.compiler.ast.Declarator;
/*      */ import com.newrelic.javassist.compiler.ast.DoubleConst;
/*      */ import com.newrelic.javassist.compiler.ast.Expr;
/*      */ import com.newrelic.javassist.compiler.ast.InstanceOfExpr;
/*      */ import com.newrelic.javassist.compiler.ast.IntConst;
/*      */ import com.newrelic.javassist.compiler.ast.Keyword;
/*      */ import com.newrelic.javassist.compiler.ast.Member;
/*      */ import com.newrelic.javassist.compiler.ast.NewExpr;
/*      */ import com.newrelic.javassist.compiler.ast.StringL;
/*      */ import com.newrelic.javassist.compiler.ast.Symbol;
/*      */ import com.newrelic.javassist.compiler.ast.Variable;
/*      */ import com.newrelic.javassist.compiler.ast.Visitor;
/*      */ 
/*      */ public class TypeChecker extends Visitor
/*      */   implements Opcode, TokenId
/*      */ {
/*      */   static final String javaLangObject = "java.lang.Object";
/*      */   static final String jvmJavaLangObject = "java/lang/Object";
/*      */   static final String jvmJavaLangString = "java/lang/String";
/*      */   static final String jvmJavaLangClass = "java/lang/Class";
/*      */   protected int exprType;
/*      */   protected int arrayDim;
/*      */   protected String className;
/*      */   protected MemberResolver resolver;
/*      */   protected CtClass thisClass;
/*      */   protected MethodInfo thisMethod;
/*      */ 
/*      */   public TypeChecker(CtClass cc, ClassPool cp)
/*      */   {
/*   44 */     this.resolver = new MemberResolver(cp);
/*   45 */     this.thisClass = cc;
/*   46 */     this.thisMethod = null;
/*      */   }
/*      */ 
/*      */   protected static String argTypesToString(int[] types, int[] dims, String[] cnames)
/*      */   {
/*   55 */     StringBuffer sbuf = new StringBuffer();
/*   56 */     sbuf.append('(');
/*   57 */     int n = types.length;
/*   58 */     if (n > 0) {
/*   59 */       int i = 0;
/*      */       while (true) {
/*   61 */         typeToString(sbuf, types[i], dims[i], cnames[i]);
/*   62 */         i++; if (i >= n) break;
/*   63 */         sbuf.append(',');
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*   69 */     sbuf.append(')');
/*   70 */     return sbuf.toString();
/*      */   }
/*      */ 
/*      */   protected static StringBuffer typeToString(StringBuffer sbuf, int type, int dim, String cname)
/*      */   {
/*      */     String s;
/*      */     String s;
/*   80 */     if (type == 307) {
/*   81 */       s = MemberResolver.jvmToJavaName(cname);
/*      */     }
/*      */     else
/*      */     {
/*      */       String s;
/*   82 */       if (type == 412)
/*   83 */         s = "Object";
/*      */       else
/*      */         try {
/*   86 */           s = MemberResolver.getTypeName(type);
/*      */         }
/*      */         catch (CompileError e) {
/*   89 */           s = "?";
/*      */         }
/*      */     }
/*   92 */     sbuf.append(s);
/*   93 */     while (dim-- > 0) {
/*   94 */       sbuf.append("[]");
/*      */     }
/*   96 */     return sbuf;
/*      */   }
/*      */ 
/*      */   public void setThisMethod(MethodInfo m)
/*      */   {
/*  103 */     this.thisMethod = m;
/*      */   }
/*      */ 
/*      */   protected static void fatal() throws CompileError {
/*  107 */     throw new CompileError("fatal");
/*      */   }
/*      */ 
/*      */   protected String getThisName()
/*      */   {
/*  114 */     return MemberResolver.javaToJvmName(this.thisClass.getName());
/*      */   }
/*      */ 
/*      */   protected String getSuperName()
/*      */     throws CompileError
/*      */   {
/*  121 */     return MemberResolver.javaToJvmName(MemberResolver.getSuperclass(this.thisClass).getName());
/*      */   }
/*      */ 
/*      */   protected String resolveClassName(ASTList name)
/*      */     throws CompileError
/*      */   {
/*  131 */     return this.resolver.resolveClassName(name);
/*      */   }
/*      */ 
/*      */   protected String resolveClassName(String jvmName)
/*      */     throws CompileError
/*      */   {
/*  138 */     return this.resolver.resolveJvmClassName(jvmName);
/*      */   }
/*      */ 
/*      */   public void atNewExpr(NewExpr expr) throws CompileError {
/*  142 */     if (expr.isArray()) {
/*  143 */       atNewArrayExpr(expr);
/*      */     } else {
/*  145 */       CtClass clazz = this.resolver.lookupClassByName(expr.getClassName());
/*  146 */       String cname = clazz.getName();
/*  147 */       ASTList args = expr.getArguments();
/*  148 */       atMethodCallCore(clazz, "<init>", args);
/*  149 */       this.exprType = 307;
/*  150 */       this.arrayDim = 0;
/*  151 */       this.className = MemberResolver.javaToJvmName(cname);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void atNewArrayExpr(NewExpr expr) throws CompileError {
/*  156 */     int type = expr.getArrayType();
/*  157 */     ASTList size = expr.getArraySize();
/*  158 */     ASTList classname = expr.getClassName();
/*  159 */     ASTree init = expr.getInitializer();
/*  160 */     if (init != null) {
/*  161 */       init.accept(this);
/*      */     }
/*  163 */     if (size.length() > 1) {
/*  164 */       atMultiNewArray(type, classname, size);
/*      */     } else {
/*  166 */       ASTree sizeExpr = size.head();
/*  167 */       if (sizeExpr != null) {
/*  168 */         sizeExpr.accept(this);
/*      */       }
/*  170 */       this.exprType = type;
/*  171 */       this.arrayDim = 1;
/*  172 */       if (type == 307)
/*  173 */         this.className = resolveClassName(classname);
/*      */       else
/*  175 */         this.className = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void atArrayInit(ArrayInit init) throws CompileError {
/*  180 */     ASTList list = init;
/*  181 */     while (list != null) {
/*  182 */       ASTree h = list.head();
/*  183 */       list = list.tail();
/*  184 */       if (h != null)
/*  185 */         h.accept(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void atMultiNewArray(int type, ASTList classname, ASTList size)
/*      */     throws CompileError
/*      */   {
/*  193 */     int dim = size.length();
/*  194 */     for (int count = 0; size != null; size = size.tail()) {
/*  195 */       ASTree s = size.head();
/*  196 */       if (s == null) {
/*      */         break;
/*      */       }
/*  199 */       count++;
/*  200 */       s.accept(this);
/*      */     }
/*      */ 
/*  203 */     this.exprType = type;
/*  204 */     this.arrayDim = dim;
/*  205 */     if (type == 307)
/*  206 */       this.className = resolveClassName(classname);
/*      */     else
/*  208 */       this.className = null;
/*      */   }
/*      */ 
/*      */   public void atAssignExpr(AssignExpr expr) throws CompileError
/*      */   {
/*  213 */     int op = expr.getOperator();
/*  214 */     ASTree left = expr.oprand1();
/*  215 */     ASTree right = expr.oprand2();
/*  216 */     if ((left instanceof Variable)) {
/*  217 */       atVariableAssign(expr, op, (Variable)left, ((Variable)left).getDeclarator(), right);
/*      */     }
/*      */     else
/*      */     {
/*  221 */       if ((left instanceof Expr)) {
/*  222 */         Expr e = (Expr)left;
/*  223 */         if (e.getOperator() == 65) {
/*  224 */           atArrayAssign(expr, op, (Expr)left, right);
/*  225 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  229 */       atFieldAssign(expr, op, left, right);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void atVariableAssign(Expr expr, int op, Variable var, Declarator d, ASTree right)
/*      */     throws CompileError
/*      */   {
/*  241 */     int varType = d.getType();
/*  242 */     int varArray = d.getArrayDim();
/*  243 */     String varClass = d.getClassName();
/*      */ 
/*  245 */     if (op != 61) {
/*  246 */       atVariable(var);
/*      */     }
/*  248 */     right.accept(this);
/*  249 */     this.exprType = varType;
/*  250 */     this.arrayDim = varArray;
/*  251 */     this.className = varClass;
/*      */   }
/*      */ 
/*      */   private void atArrayAssign(Expr expr, int op, Expr array, ASTree right)
/*      */     throws CompileError
/*      */   {
/*  257 */     atArrayRead(array.oprand1(), array.oprand2());
/*  258 */     int aType = this.exprType;
/*  259 */     int aDim = this.arrayDim;
/*  260 */     String cname = this.className;
/*  261 */     right.accept(this);
/*  262 */     this.exprType = aType;
/*  263 */     this.arrayDim = aDim;
/*  264 */     this.className = cname;
/*      */   }
/*      */ 
/*      */   protected void atFieldAssign(Expr expr, int op, ASTree left, ASTree right)
/*      */     throws CompileError
/*      */   {
/*  270 */     CtField f = fieldAccess(left);
/*  271 */     atFieldRead(f);
/*  272 */     int fType = this.exprType;
/*  273 */     int fDim = this.arrayDim;
/*  274 */     String cname = this.className;
/*  275 */     right.accept(this);
/*  276 */     this.exprType = fType;
/*  277 */     this.arrayDim = fDim;
/*  278 */     this.className = cname;
/*      */   }
/*      */ 
/*      */   public void atCondExpr(CondExpr expr) throws CompileError {
/*  282 */     booleanExpr(expr.condExpr());
/*  283 */     expr.thenExpr().accept(this);
/*  284 */     int type1 = this.exprType;
/*  285 */     int dim1 = this.arrayDim;
/*  286 */     String cname1 = this.className;
/*  287 */     expr.elseExpr().accept(this);
/*      */ 
/*  289 */     if ((dim1 == 0) && (dim1 == this.arrayDim))
/*  290 */       if (CodeGen.rightIsStrong(type1, this.exprType)) {
/*  291 */         expr.setThen(new CastExpr(this.exprType, 0, expr.thenExpr()));
/*  292 */       } else if (CodeGen.rightIsStrong(this.exprType, type1)) {
/*  293 */         expr.setElse(new CastExpr(type1, 0, expr.elseExpr()));
/*  294 */         this.exprType = type1;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void atBinExpr(BinExpr expr)
/*      */     throws CompileError
/*      */   {
/*  305 */     int token = expr.getOperator();
/*  306 */     int k = CodeGen.lookupBinOp(token);
/*  307 */     if (k >= 0)
/*      */     {
/*  310 */       if (token == 43) {
/*  311 */         Expr e = atPlusExpr(expr);
/*  312 */         if (e != null)
/*      */         {
/*  316 */           e = CallExpr.makeCall(Expr.make(46, e, new Member("toString")), null);
/*      */ 
/*  318 */           expr.setOprand1(e);
/*  319 */           expr.setOprand2(null);
/*  320 */           this.className = "java/lang/String";
/*      */         }
/*      */       }
/*      */       else {
/*  324 */         ASTree left = expr.oprand1();
/*  325 */         ASTree right = expr.oprand2();
/*  326 */         left.accept(this);
/*  327 */         int type1 = this.exprType;
/*  328 */         right.accept(this);
/*  329 */         if (!isConstant(expr, token, left, right)) {
/*  330 */           computeBinExprType(expr, token, type1);
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  336 */       booleanExpr(expr);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Expr atPlusExpr(BinExpr expr)
/*      */     throws CompileError
/*      */   {
/*  345 */     ASTree left = expr.oprand1();
/*  346 */     ASTree right = expr.oprand2();
/*  347 */     if (right == null)
/*      */     {
/*  350 */       left.accept(this);
/*  351 */       return null;
/*      */     }
/*      */ 
/*  354 */     if (isPlusExpr(left)) {
/*  355 */       Expr newExpr = atPlusExpr((BinExpr)left);
/*  356 */       if (newExpr != null) {
/*  357 */         right.accept(this);
/*  358 */         this.exprType = 307;
/*  359 */         this.arrayDim = 0;
/*  360 */         this.className = "java/lang/StringBuffer";
/*  361 */         return makeAppendCall(newExpr, right);
/*      */       }
/*      */     }
/*      */     else {
/*  365 */       left.accept(this);
/*      */     }
/*  367 */     int type1 = this.exprType;
/*  368 */     int dim1 = this.arrayDim;
/*  369 */     String cname = this.className;
/*  370 */     right.accept(this);
/*      */ 
/*  372 */     if (isConstant(expr, 43, left, right)) {
/*  373 */       return null;
/*      */     }
/*  375 */     if (((type1 == 307) && (dim1 == 0) && ("java/lang/String".equals(cname))) || ((this.exprType == 307) && (this.arrayDim == 0) && ("java/lang/String".equals(this.className))))
/*      */     {
/*  378 */       ASTList sbufClass = ASTList.make(new Symbol("java"), new Symbol("lang"), new Symbol("StringBuffer"));
/*      */ 
/*  380 */       ASTree e = new NewExpr(sbufClass, null);
/*  381 */       this.exprType = 307;
/*  382 */       this.arrayDim = 0;
/*  383 */       this.className = "java/lang/StringBuffer";
/*  384 */       return makeAppendCall(makeAppendCall(e, left), right);
/*      */     }
/*      */ 
/*  387 */     computeBinExprType(expr, 43, type1);
/*  388 */     return null;
/*      */   }
/*      */ 
/*      */   private boolean isConstant(BinExpr expr, int op, ASTree left, ASTree right)
/*      */     throws CompileError
/*      */   {
/*  395 */     left = stripPlusExpr(left);
/*  396 */     right = stripPlusExpr(right);
/*  397 */     ASTree newExpr = null;
/*  398 */     if (((left instanceof StringL)) && ((right instanceof StringL)) && (op == 43)) {
/*  399 */       newExpr = new StringL(((StringL)left).get() + ((StringL)right).get());
/*      */     }
/*  401 */     else if ((left instanceof IntConst))
/*  402 */       newExpr = ((IntConst)left).compute(op, right);
/*  403 */     else if ((left instanceof DoubleConst)) {
/*  404 */       newExpr = ((DoubleConst)left).compute(op, right);
/*      */     }
/*  406 */     if (newExpr == null) {
/*  407 */       return false;
/*      */     }
/*  409 */     expr.setOperator(43);
/*  410 */     expr.setOprand1(newExpr);
/*  411 */     expr.setOprand2(null);
/*  412 */     newExpr.accept(this);
/*  413 */     return true;
/*      */   }
/*      */ 
/*      */   static ASTree stripPlusExpr(ASTree expr)
/*      */   {
/*  420 */     if ((expr instanceof BinExpr)) {
/*  421 */       BinExpr e = (BinExpr)expr;
/*  422 */       if ((e.getOperator() == 43) && (e.oprand2() == null))
/*  423 */         return e.getLeft();
/*      */     }
/*  425 */     else if ((expr instanceof Expr)) {
/*  426 */       Expr e = (Expr)expr;
/*  427 */       int op = e.getOperator();
/*  428 */       if (op == 35) {
/*  429 */         ASTree cexpr = getConstantFieldValue((Member)e.oprand2());
/*  430 */         if (cexpr != null)
/*  431 */           return cexpr;
/*      */       }
/*  433 */       else if ((op == 43) && (e.getRight() == null)) {
/*  434 */         return e.getLeft();
/*      */       }
/*  436 */     } else if ((expr instanceof Member)) {
/*  437 */       ASTree cexpr = getConstantFieldValue((Member)expr);
/*  438 */       if (cexpr != null) {
/*  439 */         return cexpr;
/*      */       }
/*      */     }
/*  442 */     return expr;
/*      */   }
/*      */ 
/*      */   private static ASTree getConstantFieldValue(Member mem)
/*      */   {
/*  450 */     return getConstantFieldValue(mem.getField());
/*      */   }
/*      */ 
/*      */   public static ASTree getConstantFieldValue(CtField f) {
/*  454 */     if (f == null) {
/*  455 */       return null;
/*      */     }
/*  457 */     Object value = f.getConstantValue();
/*  458 */     if (value == null) {
/*  459 */       return null;
/*      */     }
/*  461 */     if ((value instanceof String))
/*  462 */       return new StringL((String)value);
/*  463 */     if (((value instanceof Double)) || ((value instanceof Float))) {
/*  464 */       int token = (value instanceof Double) ? 405 : 404;
/*      */ 
/*  466 */       return new DoubleConst(((Number)value).doubleValue(), token);
/*      */     }
/*  468 */     if ((value instanceof Number)) {
/*  469 */       int token = (value instanceof Long) ? 403 : 402;
/*  470 */       return new IntConst(((Number)value).longValue(), token);
/*      */     }
/*  472 */     if ((value instanceof Boolean)) {
/*  473 */       return new Keyword(((Boolean)value).booleanValue() ? 410 : 411);
/*      */     }
/*      */ 
/*  476 */     return null;
/*      */   }
/*      */ 
/*      */   private static boolean isPlusExpr(ASTree expr) {
/*  480 */     if ((expr instanceof BinExpr)) {
/*  481 */       BinExpr bexpr = (BinExpr)expr;
/*  482 */       int token = bexpr.getOperator();
/*  483 */       return token == 43;
/*      */     }
/*      */ 
/*  486 */     return false;
/*      */   }
/*      */ 
/*      */   private static Expr makeAppendCall(ASTree target, ASTree arg) {
/*  490 */     return CallExpr.makeCall(Expr.make(46, target, new Member("append")), new ASTList(arg));
/*      */   }
/*      */ 
/*      */   private void computeBinExprType(BinExpr expr, int token, int type1)
/*      */     throws CompileError
/*      */   {
/*  498 */     int type2 = this.exprType;
/*  499 */     if ((token == 364) || (token == 366) || (token == 370))
/*  500 */       this.exprType = type1;
/*      */     else {
/*  502 */       insertCast(expr, type1, type2);
/*      */     }
/*  504 */     if (CodeGen.isP_INT(this.exprType))
/*  505 */       this.exprType = 324;
/*      */   }
/*      */ 
/*      */   private void booleanExpr(ASTree expr)
/*      */     throws CompileError
/*      */   {
/*  511 */     int op = CodeGen.getCompOperator(expr);
/*  512 */     if (op == 358) {
/*  513 */       BinExpr bexpr = (BinExpr)expr;
/*  514 */       bexpr.oprand1().accept(this);
/*  515 */       int type1 = this.exprType;
/*  516 */       int dim1 = this.arrayDim;
/*  517 */       bexpr.oprand2().accept(this);
/*  518 */       if ((dim1 == 0) && (this.arrayDim == 0))
/*  519 */         insertCast(bexpr, type1, this.exprType);
/*      */     }
/*  521 */     else if (op == 33) {
/*  522 */       ((Expr)expr).oprand1().accept(this);
/*  523 */     } else if ((op == 369) || (op == 368)) {
/*  524 */       BinExpr bexpr = (BinExpr)expr;
/*  525 */       bexpr.oprand1().accept(this);
/*  526 */       bexpr.oprand2().accept(this);
/*      */     }
/*      */     else {
/*  529 */       expr.accept(this);
/*      */     }
/*  531 */     this.exprType = 301;
/*  532 */     this.arrayDim = 0;
/*      */   }
/*      */ 
/*      */   private void insertCast(BinExpr expr, int type1, int type2)
/*      */     throws CompileError
/*      */   {
/*  538 */     if (CodeGen.rightIsStrong(type1, type2))
/*  539 */       expr.setLeft(new CastExpr(type2, 0, expr.oprand1()));
/*      */     else
/*  541 */       this.exprType = type1;
/*      */   }
/*      */ 
/*      */   public void atCastExpr(CastExpr expr) throws CompileError {
/*  545 */     String cname = resolveClassName(expr.getClassName());
/*  546 */     expr.getOprand().accept(this);
/*  547 */     this.exprType = expr.getType();
/*  548 */     this.arrayDim = expr.getArrayDim();
/*  549 */     this.className = cname;
/*      */   }
/*      */ 
/*      */   public void atInstanceOfExpr(InstanceOfExpr expr) throws CompileError {
/*  553 */     expr.getOprand().accept(this);
/*  554 */     this.exprType = 301;
/*  555 */     this.arrayDim = 0;
/*      */   }
/*      */ 
/*      */   public void atExpr(Expr expr)
/*      */     throws CompileError
/*      */   {
/*  562 */     int token = expr.getOperator();
/*  563 */     ASTree oprand = expr.oprand1();
/*  564 */     if (token == 46) {
/*  565 */       String member = ((Symbol)expr.oprand2()).get();
/*  566 */       if (member.equals("length"))
/*  567 */         atArrayLength(expr);
/*  568 */       else if (member.equals("class"))
/*  569 */         atClassObject(expr);
/*      */       else
/*  571 */         atFieldRead(expr);
/*      */     }
/*  573 */     else if (token == 35) {
/*  574 */       String member = ((Symbol)expr.oprand2()).get();
/*  575 */       if (member.equals("class"))
/*  576 */         atClassObject(expr);
/*      */       else
/*  578 */         atFieldRead(expr);
/*      */     }
/*  580 */     else if (token == 65) {
/*  581 */       atArrayRead(oprand, expr.oprand2());
/*  582 */     } else if ((token == 362) || (token == 363)) {
/*  583 */       atPlusPlus(token, oprand, expr);
/*  584 */     } else if (token == 33) {
/*  585 */       booleanExpr(expr);
/*  586 */     } else if (token == 67) {
/*  587 */       fatal();
/*      */     } else {
/*  589 */       oprand.accept(this);
/*  590 */       if ((!isConstant(expr, token, oprand)) && 
/*  591 */         ((token == 45) || (token == 126)) && 
/*  592 */         (CodeGen.isP_INT(this.exprType)))
/*  593 */         this.exprType = 324;
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isConstant(Expr expr, int op, ASTree oprand) {
/*  598 */     oprand = stripPlusExpr(oprand);
/*  599 */     if ((oprand instanceof IntConst)) {
/*  600 */       IntConst c = (IntConst)oprand;
/*  601 */       long v = c.get();
/*  602 */       if (op == 45)
/*  603 */         v = -v;
/*  604 */       else if (op == 126)
/*  605 */         v ^= -1L;
/*      */       else {
/*  607 */         return false;
/*      */       }
/*  609 */       c.set(v);
/*      */     }
/*  611 */     else if ((oprand instanceof DoubleConst)) {
/*  612 */       DoubleConst c = (DoubleConst)oprand;
/*  613 */       if (op == 45)
/*  614 */         c.set(-c.get());
/*      */       else
/*  616 */         return false;
/*      */     }
/*      */     else {
/*  619 */       return false;
/*      */     }
/*  621 */     expr.setOperator(43);
/*  622 */     return true;
/*      */   }
/*      */ 
/*      */   public void atCallExpr(CallExpr expr) throws CompileError {
/*  626 */     String mname = null;
/*  627 */     CtClass targetClass = null;
/*  628 */     ASTree method = expr.oprand1();
/*  629 */     ASTList args = (ASTList)expr.oprand2();
/*      */ 
/*  631 */     if ((method instanceof Member)) {
/*  632 */       mname = ((Member)method).get();
/*  633 */       targetClass = this.thisClass;
/*      */     }
/*  635 */     else if ((method instanceof Keyword)) {
/*  636 */       mname = "<init>";
/*  637 */       if (((Keyword)method).get() == 336)
/*  638 */         targetClass = MemberResolver.getSuperclass(this.thisClass);
/*      */       else
/*  640 */         targetClass = this.thisClass;
/*      */     }
/*  642 */     else if ((method instanceof Expr)) {
/*  643 */       Expr e = (Expr)method;
/*  644 */       mname = ((Symbol)e.oprand2()).get();
/*  645 */       int op = e.getOperator();
/*  646 */       if (op == 35) {
/*  647 */         targetClass = this.resolver.lookupClass(((Symbol)e.oprand1()).get(), false);
/*      */       }
/*  650 */       else if (op == 46) {
/*  651 */         ASTree target = e.oprand1();
/*      */         try {
/*  653 */           target.accept(this);
/*      */         }
/*      */         catch (NoFieldException nfe) {
/*  656 */           if (nfe.getExpr() != target) {
/*  657 */             throw nfe;
/*      */           }
/*      */ 
/*  660 */           this.exprType = 307;
/*  661 */           this.arrayDim = 0;
/*  662 */           this.className = nfe.getField();
/*  663 */           e.setOperator(35);
/*  664 */           e.setOprand1(new Symbol(MemberResolver.jvmToJavaName(this.className)));
/*      */         }
/*      */ 
/*  668 */         if (this.arrayDim > 0)
/*  669 */           targetClass = this.resolver.lookupClass("java.lang.Object", true);
/*  670 */         else if (this.exprType == 307)
/*  671 */           targetClass = this.resolver.lookupClassByJvmName(this.className);
/*      */         else
/*  673 */           badMethod();
/*      */       }
/*      */       else {
/*  676 */         badMethod();
/*      */       }
/*      */     } else {
/*  679 */       fatal();
/*      */     }
/*  681 */     MemberResolver.Method minfo = atMethodCallCore(targetClass, mname, args);
/*      */ 
/*  683 */     expr.setMethod(minfo);
/*      */   }
/*      */ 
/*      */   private static void badMethod() throws CompileError {
/*  687 */     throw new CompileError("bad method");
/*      */   }
/*      */ 
/*      */   public MemberResolver.Method atMethodCallCore(CtClass targetClass, String mname, ASTList args)
/*      */     throws CompileError
/*      */   {
/*  698 */     int nargs = getMethodArgsLength(args);
/*  699 */     int[] types = new int[nargs];
/*  700 */     int[] dims = new int[nargs];
/*  701 */     String[] cnames = new String[nargs];
/*  702 */     atMethodArgs(args, types, dims, cnames);
/*      */ 
/*  704 */     MemberResolver.Method found = this.resolver.lookupMethod(targetClass, this.thisClass, this.thisMethod, mname, types, dims, cnames);
/*      */ 
/*  707 */     if (found == null) {
/*  708 */       String clazz = targetClass.getName();
/*  709 */       String signature = argTypesToString(types, dims, cnames);
/*      */       String msg;
/*      */       String msg;
/*  711 */       if (mname.equals("<init>"))
/*  712 */         msg = "cannot find constructor " + clazz + signature;
/*      */       else {
/*  714 */         msg = mname + signature + " not found in " + clazz;
/*      */       }
/*  716 */       throw new CompileError(msg);
/*      */     }
/*      */ 
/*  719 */     String desc = found.info.getDescriptor();
/*  720 */     setReturnType(desc);
/*  721 */     return found;
/*      */   }
/*      */ 
/*      */   public int getMethodArgsLength(ASTList args) {
/*  725 */     return ASTList.length(args);
/*      */   }
/*      */ 
/*      */   public void atMethodArgs(ASTList args, int[] types, int[] dims, String[] cnames) throws CompileError
/*      */   {
/*  730 */     int i = 0;
/*  731 */     while (args != null) {
/*  732 */       ASTree a = args.head();
/*  733 */       a.accept(this);
/*  734 */       types[i] = this.exprType;
/*  735 */       dims[i] = this.arrayDim;
/*  736 */       cnames[i] = this.className;
/*  737 */       i++;
/*  738 */       args = args.tail();
/*      */     }
/*      */   }
/*      */ 
/*      */   void setReturnType(String desc) throws CompileError {
/*  743 */     int i = desc.indexOf(')');
/*  744 */     if (i < 0) {
/*  745 */       badMethod();
/*      */     }
/*  747 */     char c = desc.charAt(++i);
/*  748 */     int dim = 0;
/*  749 */     while (c == '[') {
/*  750 */       dim++;
/*  751 */       c = desc.charAt(++i);
/*      */     }
/*      */ 
/*  754 */     this.arrayDim = dim;
/*  755 */     if (c == 'L') {
/*  756 */       int j = desc.indexOf(';', i + 1);
/*  757 */       if (j < 0) {
/*  758 */         badMethod();
/*      */       }
/*  760 */       this.exprType = 307;
/*  761 */       this.className = desc.substring(i + 1, j);
/*      */     }
/*      */     else {
/*  764 */       this.exprType = MemberResolver.descToType(c);
/*  765 */       this.className = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void atFieldRead(ASTree expr) throws CompileError {
/*  770 */     atFieldRead(fieldAccess(expr));
/*      */   }
/*      */ 
/*      */   private void atFieldRead(CtField f) throws CompileError {
/*  774 */     FieldInfo finfo = f.getFieldInfo2();
/*  775 */     String type = finfo.getDescriptor();
/*      */ 
/*  777 */     int i = 0;
/*  778 */     int dim = 0;
/*  779 */     char c = type.charAt(i);
/*  780 */     while (c == '[') {
/*  781 */       dim++;
/*  782 */       c = type.charAt(++i);
/*      */     }
/*      */ 
/*  785 */     this.arrayDim = dim;
/*  786 */     this.exprType = MemberResolver.descToType(c);
/*      */ 
/*  788 */     if (c == 'L')
/*  789 */       this.className = type.substring(i + 1, type.indexOf(';', i + 1));
/*      */     else
/*  791 */       this.className = null;
/*      */   }
/*      */ 
/*      */   protected CtField fieldAccess(ASTree expr)
/*      */     throws CompileError
/*      */   {
/*  800 */     if ((expr instanceof Member)) {
/*  801 */       Member mem = (Member)expr;
/*  802 */       String name = mem.get();
/*      */       try {
/*  804 */         CtField f = this.thisClass.getField(name);
/*  805 */         if (Modifier.isStatic(f.getModifiers())) {
/*  806 */           mem.setField(f);
/*      */         }
/*  808 */         return f;
/*      */       }
/*      */       catch (NotFoundException e)
/*      */       {
/*  812 */         throw new NoFieldException(name, expr);
/*      */       }
/*      */     }
/*  815 */     if ((expr instanceof Expr)) {
/*  816 */       Expr e = (Expr)expr;
/*  817 */       int op = e.getOperator();
/*  818 */       if (op == 35) {
/*  819 */         Member mem = (Member)e.oprand2();
/*  820 */         CtField f = this.resolver.lookupField(((Symbol)e.oprand1()).get(), mem);
/*      */ 
/*  822 */         mem.setField(f);
/*  823 */         return f;
/*      */       }
/*  825 */       if (op == 46) {
/*      */         try {
/*  827 */           e.oprand1().accept(this);
/*      */         }
/*      */         catch (NoFieldException nfe) {
/*  830 */           if (nfe.getExpr() != e.oprand1()) {
/*  831 */             throw nfe;
/*      */           }
/*      */ 
/*  837 */           return fieldAccess2(e, nfe.getField());
/*      */         }
/*      */ 
/*  840 */         CompileError err = null;
/*      */         try {
/*  842 */           if ((this.exprType == 307) && (this.arrayDim == 0))
/*  843 */             return this.resolver.lookupFieldByJvmName(this.className, (Symbol)e.oprand2());
/*      */         }
/*      */         catch (CompileError ce)
/*      */         {
/*  847 */           err = ce;
/*      */         }
/*      */ 
/*  866 */         ASTree oprnd1 = e.oprand1();
/*  867 */         if ((oprnd1 instanceof Symbol)) {
/*  868 */           return fieldAccess2(e, ((Symbol)oprnd1).get());
/*      */         }
/*  870 */         if (err != null) {
/*  871 */           throw err;
/*      */         }
/*      */       }
/*      */     }
/*  875 */     throw new CompileError("bad filed access");
/*      */   }
/*      */ 
/*      */   private CtField fieldAccess2(Expr e, String jvmClassName) throws CompileError {
/*  879 */     Member fname = (Member)e.oprand2();
/*  880 */     CtField f = this.resolver.lookupFieldByJvmName2(jvmClassName, fname, e);
/*  881 */     e.setOperator(35);
/*  882 */     e.setOprand1(new Symbol(MemberResolver.jvmToJavaName(jvmClassName)));
/*  883 */     fname.setField(f);
/*  884 */     return f;
/*      */   }
/*      */ 
/*      */   public void atClassObject(Expr expr) throws CompileError {
/*  888 */     this.exprType = 307;
/*  889 */     this.arrayDim = 0;
/*  890 */     this.className = "java/lang/Class";
/*      */   }
/*      */ 
/*      */   public void atArrayLength(Expr expr) throws CompileError {
/*  894 */     expr.oprand1().accept(this);
/*  895 */     this.exprType = 324;
/*  896 */     this.arrayDim = 0;
/*      */   }
/*      */ 
/*      */   public void atArrayRead(ASTree array, ASTree index)
/*      */     throws CompileError
/*      */   {
/*  902 */     array.accept(this);
/*  903 */     int type = this.exprType;
/*  904 */     int dim = this.arrayDim;
/*  905 */     String cname = this.className;
/*  906 */     index.accept(this);
/*  907 */     this.exprType = type;
/*  908 */     this.arrayDim = (dim - 1);
/*  909 */     this.className = cname;
/*      */   }
/*      */ 
/*      */   private void atPlusPlus(int token, ASTree oprand, Expr expr)
/*      */     throws CompileError
/*      */   {
/*  915 */     boolean isPost = oprand == null;
/*  916 */     if (isPost) {
/*  917 */       oprand = expr.oprand2();
/*      */     }
/*  919 */     if ((oprand instanceof Variable)) {
/*  920 */       Declarator d = ((Variable)oprand).getDeclarator();
/*  921 */       this.exprType = d.getType();
/*  922 */       this.arrayDim = d.getArrayDim();
/*      */     }
/*      */     else {
/*  925 */       if ((oprand instanceof Expr)) {
/*  926 */         Expr e = (Expr)oprand;
/*  927 */         if (e.getOperator() == 65) {
/*  928 */           atArrayRead(e.oprand1(), e.oprand2());
/*      */ 
/*  930 */           int t = this.exprType;
/*  931 */           if ((t == 324) || (t == 303) || (t == 306) || (t == 334)) {
/*  932 */             this.exprType = 324;
/*      */           }
/*  934 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  938 */       atFieldPlusPlus(oprand);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void atFieldPlusPlus(ASTree oprand) throws CompileError
/*      */   {
/*  944 */     CtField f = fieldAccess(oprand);
/*  945 */     atFieldRead(f);
/*  946 */     int t = this.exprType;
/*  947 */     if ((t == 324) || (t == 303) || (t == 306) || (t == 334))
/*  948 */       this.exprType = 324;
/*      */   }
/*      */ 
/*      */   public void atMember(Member mem) throws CompileError {
/*  952 */     atFieldRead(mem);
/*      */   }
/*      */ 
/*      */   public void atVariable(Variable v) throws CompileError {
/*  956 */     Declarator d = v.getDeclarator();
/*  957 */     this.exprType = d.getType();
/*  958 */     this.arrayDim = d.getArrayDim();
/*  959 */     this.className = d.getClassName();
/*      */   }
/*      */ 
/*      */   public void atKeyword(Keyword k) throws CompileError {
/*  963 */     this.arrayDim = 0;
/*  964 */     int token = k.get();
/*  965 */     switch (token) {
/*      */     case 410:
/*      */     case 411:
/*  968 */       this.exprType = 301;
/*  969 */       break;
/*      */     case 412:
/*  971 */       this.exprType = 412;
/*  972 */       break;
/*      */     case 336:
/*      */     case 339:
/*  975 */       this.exprType = 307;
/*  976 */       if (token == 339)
/*  977 */         this.className = getThisName();
/*      */       else
/*  979 */         this.className = getSuperName();
/*  980 */       break;
/*      */     default:
/*  982 */       fatal();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void atStringL(StringL s) throws CompileError {
/*  987 */     this.exprType = 307;
/*  988 */     this.arrayDim = 0;
/*  989 */     this.className = "java/lang/String";
/*      */   }
/*      */ 
/*      */   public void atIntConst(IntConst i) throws CompileError {
/*  993 */     this.arrayDim = 0;
/*  994 */     int type = i.getType();
/*  995 */     if ((type == 402) || (type == 401))
/*  996 */       this.exprType = (type == 402 ? 324 : 306);
/*      */     else
/*  998 */       this.exprType = 326;
/*      */   }
/*      */ 
/*      */   public void atDoubleConst(DoubleConst d) throws CompileError {
/* 1002 */     this.arrayDim = 0;
/* 1003 */     if (d.getType() == 405)
/* 1004 */       this.exprType = 312;
/*      */     else
/* 1006 */       this.exprType = 317;
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.TypeChecker
 * JD-Core Version:    0.6.2
 */