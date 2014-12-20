/*      */ package com.newrelic.javassist.compiler;
/*      */ 
/*      */ import com.newrelic.javassist.bytecode.Bytecode;
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
/*      */ import com.newrelic.javassist.compiler.ast.FieldDecl;
/*      */ import com.newrelic.javassist.compiler.ast.InstanceOfExpr;
/*      */ import com.newrelic.javassist.compiler.ast.IntConst;
/*      */ import com.newrelic.javassist.compiler.ast.Keyword;
/*      */ import com.newrelic.javassist.compiler.ast.Member;
/*      */ import com.newrelic.javassist.compiler.ast.MethodDecl;
/*      */ import com.newrelic.javassist.compiler.ast.NewExpr;
/*      */ import com.newrelic.javassist.compiler.ast.Pair;
/*      */ import com.newrelic.javassist.compiler.ast.Stmnt;
/*      */ import com.newrelic.javassist.compiler.ast.StringL;
/*      */ import com.newrelic.javassist.compiler.ast.Symbol;
/*      */ import com.newrelic.javassist.compiler.ast.Variable;
/*      */ import com.newrelic.javassist.compiler.ast.Visitor;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ 
/*      */ public abstract class CodeGen extends Visitor
/*      */   implements Opcode, TokenId
/*      */ {
/*      */   static final String javaLangObject = "java.lang.Object";
/*      */   static final String jvmJavaLangObject = "java/lang/Object";
/*      */   static final String javaLangString = "java.lang.String";
/*      */   static final String jvmJavaLangString = "java/lang/String";
/*      */   protected Bytecode bytecode;
/*      */   private int tempVar;
/*      */   TypeChecker typeChecker;
/*      */   protected boolean hasReturned;
/*      */   public boolean inStaticMethod;
/*      */   protected ArrayList breakList;
/*      */   protected ArrayList continueList;
/*      */   protected ReturnHook returnHooks;
/*      */   protected int exprType;
/*      */   protected int arrayDim;
/*      */   protected String className;
/*  933 */   static final int[] binOp = { 43, 99, 98, 97, 96, 45, 103, 102, 101, 100, 42, 107, 106, 105, 104, 47, 111, 110, 109, 108, 37, 115, 114, 113, 112, 124, 0, 0, 129, 128, 94, 0, 0, 131, 130, 38, 0, 0, 127, 126, 364, 0, 0, 121, 120, 366, 0, 0, 123, 122, 370, 0, 0, 125, 124 };
/*      */ 
/* 1182 */   private static final int[] ifOp = { 358, 159, 160, 350, 160, 159, 357, 164, 163, 359, 162, 161, 60, 161, 162, 62, 163, 164 };
/*      */ 
/* 1189 */   private static final int[] ifOp2 = { 358, 153, 154, 350, 154, 153, 357, 158, 157, 359, 156, 155, 60, 155, 156, 62, 157, 158 };
/*      */   private static final int P_DOUBLE = 0;
/*      */   private static final int P_FLOAT = 1;
/*      */   private static final int P_LONG = 2;
/*      */   private static final int P_INT = 3;
/*      */   private static final int P_OTHER = -1;
/* 1295 */   private static final int[] castOp = { 0, 144, 143, 142, 141, 0, 140, 139, 138, 137, 0, 136, 135, 134, 133, 0 };
/*      */ 
/*      */   public CodeGen(Bytecode b)
/*      */   {
/*   83 */     this.bytecode = b;
/*   84 */     this.tempVar = -1;
/*   85 */     this.typeChecker = null;
/*   86 */     this.hasReturned = false;
/*   87 */     this.inStaticMethod = false;
/*   88 */     this.breakList = null;
/*   89 */     this.continueList = null;
/*   90 */     this.returnHooks = null;
/*      */   }
/*      */ 
/*      */   public void setTypeChecker(TypeChecker checker) {
/*   94 */     this.typeChecker = checker;
/*      */   }
/*      */ 
/*      */   protected static void fatal() throws CompileError {
/*   98 */     throw new CompileError("fatal");
/*      */   }
/*      */ 
/*      */   public static boolean is2word(int type, int dim) {
/*  102 */     return (dim == 0) && ((type == 312) || (type == 326));
/*      */   }
/*      */   public int getMaxLocals() {
/*  105 */     return this.bytecode.getMaxLocals();
/*      */   }
/*      */   public void setMaxLocals(int n) {
/*  108 */     this.bytecode.setMaxLocals(n);
/*      */   }
/*      */ 
/*      */   protected void incMaxLocals(int size) {
/*  112 */     this.bytecode.incMaxLocals(size);
/*      */   }
/*      */ 
/*      */   protected int getTempVar()
/*      */   {
/*  120 */     if (this.tempVar < 0) {
/*  121 */       this.tempVar = getMaxLocals();
/*  122 */       incMaxLocals(2);
/*      */     }
/*      */ 
/*  125 */     return this.tempVar;
/*      */   }
/*      */ 
/*      */   protected int getLocalVar(Declarator d) {
/*  129 */     int v = d.getLocalVar();
/*  130 */     if (v < 0) {
/*  131 */       v = getMaxLocals();
/*  132 */       d.setLocalVar(v);
/*  133 */       incMaxLocals(1);
/*      */     }
/*      */ 
/*  136 */     return v;
/*      */   }
/*      */ 
/*      */   protected abstract String getThisName();
/*      */ 
/*      */   protected abstract String getSuperName()
/*      */     throws CompileError;
/*      */ 
/*      */   protected abstract String resolveClassName(ASTList paramASTList)
/*      */     throws CompileError;
/*      */ 
/*      */   protected abstract String resolveClassName(String paramString)
/*      */     throws CompileError;
/*      */ 
/*      */   protected static String toJvmArrayName(String name, int dim)
/*      */   {
/*  168 */     if (name == null) {
/*  169 */       return null;
/*      */     }
/*  171 */     if (dim == 0) {
/*  172 */       return name;
/*      */     }
/*  174 */     StringBuffer sbuf = new StringBuffer();
/*  175 */     int d = dim;
/*  176 */     while (d-- > 0) {
/*  177 */       sbuf.append('[');
/*      */     }
/*  179 */     sbuf.append('L');
/*  180 */     sbuf.append(name);
/*  181 */     sbuf.append(';');
/*      */ 
/*  183 */     return sbuf.toString();
/*      */   }
/*      */ 
/*      */   protected static String toJvmTypeName(int type, int dim)
/*      */   {
/*  188 */     char c = 'I';
/*  189 */     switch (type) {
/*      */     case 301:
/*  191 */       c = 'Z';
/*  192 */       break;
/*      */     case 303:
/*  194 */       c = 'B';
/*  195 */       break;
/*      */     case 306:
/*  197 */       c = 'C';
/*  198 */       break;
/*      */     case 334:
/*  200 */       c = 'S';
/*  201 */       break;
/*      */     case 324:
/*  203 */       c = 'I';
/*  204 */       break;
/*      */     case 326:
/*  206 */       c = 'J';
/*  207 */       break;
/*      */     case 317:
/*  209 */       c = 'F';
/*  210 */       break;
/*      */     case 312:
/*  212 */       c = 'D';
/*  213 */       break;
/*      */     case 344:
/*  215 */       c = 'V';
/*      */     }
/*      */ 
/*  219 */     StringBuffer sbuf = new StringBuffer();
/*  220 */     while (dim-- > 0) {
/*  221 */       sbuf.append('[');
/*      */     }
/*  223 */     sbuf.append(c);
/*  224 */     return sbuf.toString();
/*      */   }
/*      */ 
/*      */   public void compileExpr(ASTree expr) throws CompileError {
/*  228 */     doTypeCheck(expr);
/*  229 */     expr.accept(this);
/*      */   }
/*      */ 
/*      */   public boolean compileBooleanExpr(boolean branchIf, ASTree expr)
/*      */     throws CompileError
/*      */   {
/*  235 */     doTypeCheck(expr);
/*  236 */     return booleanExpr(branchIf, expr);
/*      */   }
/*      */ 
/*      */   public void doTypeCheck(ASTree expr) throws CompileError {
/*  240 */     if (this.typeChecker != null)
/*  241 */       expr.accept(this.typeChecker); 
/*      */   }
/*      */ 
/*  244 */   public void atASTList(ASTList n) throws CompileError { fatal(); } 
/*      */   public void atPair(Pair n) throws CompileError {
/*  246 */     fatal();
/*      */   }
/*  248 */   public void atSymbol(Symbol n) throws CompileError { fatal(); }
/*      */ 
/*      */   public void atFieldDecl(FieldDecl field) throws CompileError {
/*  251 */     field.getInit().accept(this);
/*      */   }
/*      */ 
/*      */   public void atMethodDecl(MethodDecl method) throws CompileError {
/*  255 */     ASTList mods = method.getModifiers();
/*  256 */     setMaxLocals(1);
/*  257 */     while (mods != null) {
/*  258 */       Keyword k = (Keyword)mods.head();
/*  259 */       mods = mods.tail();
/*  260 */       if (k.get() == 335) {
/*  261 */         setMaxLocals(0);
/*  262 */         this.inStaticMethod = true;
/*      */       }
/*      */     }
/*      */ 
/*  266 */     ASTList params = method.getParams();
/*  267 */     while (params != null) {
/*  268 */       atDeclarator((Declarator)params.head());
/*  269 */       params = params.tail();
/*      */     }
/*      */ 
/*  272 */     Stmnt s = method.getBody();
/*  273 */     atMethodBody(s, method.isConstructor(), method.getReturn().getType() == 344);
/*      */   }
/*      */ 
/*      */   public void atMethodBody(Stmnt s, boolean isCons, boolean isVoid)
/*      */     throws CompileError
/*      */   {
/*  284 */     if (s == null) {
/*  285 */       return;
/*      */     }
/*  287 */     if ((isCons) && (needsSuperCall(s))) {
/*  288 */       insertDefaultSuperCall();
/*      */     }
/*  290 */     this.hasReturned = false;
/*  291 */     s.accept(this);
/*  292 */     if (!this.hasReturned)
/*  293 */       if (isVoid) {
/*  294 */         this.bytecode.addOpcode(177);
/*  295 */         this.hasReturned = true;
/*      */       }
/*      */       else {
/*  298 */         throw new CompileError("no return statement");
/*      */       }
/*      */   }
/*      */ 
/*  302 */   private boolean needsSuperCall(Stmnt body) throws CompileError { if (body.getOperator() == 66) {
/*  303 */       body = (Stmnt)body.head();
/*      */     }
/*  305 */     if ((body != null) && (body.getOperator() == 69)) {
/*  306 */       ASTree expr = body.head();
/*  307 */       if ((expr != null) && ((expr instanceof Expr)) && (((Expr)expr).getOperator() == 67))
/*      */       {
/*  309 */         ASTree target = ((Expr)expr).head();
/*  310 */         if ((target instanceof Keyword)) {
/*  311 */           int token = ((Keyword)target).get();
/*  312 */           return (token != 339) && (token != 336);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  317 */     return true; }
/*      */ 
/*      */   protected abstract void insertDefaultSuperCall() throws CompileError;
/*      */ 
/*      */   public void atStmnt(Stmnt st) throws CompileError
/*      */   {
/*  323 */     if (st == null) {
/*  324 */       return;
/*      */     }
/*  326 */     int op = st.getOperator();
/*  327 */     if (op == 69) {
/*  328 */       ASTree expr = st.getLeft();
/*  329 */       doTypeCheck(expr);
/*  330 */       if ((expr instanceof AssignExpr)) {
/*  331 */         atAssignExpr((AssignExpr)expr, false);
/*  332 */       } else if (isPlusPlusExpr(expr)) {
/*  333 */         Expr e = (Expr)expr;
/*  334 */         atPlusPlus(e.getOperator(), e.oprand1(), e, false);
/*      */       }
/*      */       else {
/*  337 */         expr.accept(this);
/*  338 */         if (is2word(this.exprType, this.arrayDim))
/*  339 */           this.bytecode.addOpcode(88);
/*  340 */         else if (this.exprType != 344)
/*  341 */           this.bytecode.addOpcode(87);
/*      */       }
/*      */     }
/*  344 */     else if ((op == 68) || (op == 66)) {
/*  345 */       ASTList list = st;
/*  346 */       while (list != null) {
/*  347 */         ASTree h = list.head();
/*  348 */         list = list.tail();
/*  349 */         if (h != null)
/*  350 */           h.accept(this);
/*      */       }
/*      */     }
/*  353 */     else if (op == 320) {
/*  354 */       atIfStmnt(st);
/*  355 */     } else if ((op == 346) || (op == 311)) {
/*  356 */       atWhileStmnt(st, op == 346);
/*  357 */     } else if (op == 318) {
/*  358 */       atForStmnt(st);
/*  359 */     } else if ((op == 302) || (op == 309)) {
/*  360 */       atBreakStmnt(st, op == 302);
/*  361 */     } else if (op == 333) {
/*  362 */       atReturnStmnt(st);
/*  363 */     } else if (op == 340) {
/*  364 */       atThrowStmnt(st);
/*  365 */     } else if (op == 343) {
/*  366 */       atTryStmnt(st);
/*  367 */     } else if (op == 337) {
/*  368 */       atSwitchStmnt(st);
/*  369 */     } else if (op == 338) {
/*  370 */       atSyncStmnt(st);
/*      */     }
/*      */     else {
/*  373 */       this.hasReturned = false;
/*  374 */       throw new CompileError("sorry, not supported statement: TokenId " + op);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void atIfStmnt(Stmnt st) throws CompileError
/*      */   {
/*  380 */     ASTree expr = st.head();
/*  381 */     Stmnt thenp = (Stmnt)st.tail().head();
/*  382 */     Stmnt elsep = (Stmnt)st.tail().tail().head();
/*  383 */     compileBooleanExpr(false, expr);
/*  384 */     int pc = this.bytecode.currentPc();
/*  385 */     int pc2 = 0;
/*  386 */     this.bytecode.addIndex(0);
/*      */ 
/*  388 */     this.hasReturned = false;
/*  389 */     if (thenp != null) {
/*  390 */       thenp.accept(this);
/*      */     }
/*  392 */     boolean thenHasReturned = this.hasReturned;
/*  393 */     this.hasReturned = false;
/*      */ 
/*  395 */     if ((elsep != null) && (!thenHasReturned)) {
/*  396 */       this.bytecode.addOpcode(167);
/*  397 */       pc2 = this.bytecode.currentPc();
/*  398 */       this.bytecode.addIndex(0);
/*      */     }
/*      */ 
/*  401 */     this.bytecode.write16bit(pc, this.bytecode.currentPc() - pc + 1);
/*      */ 
/*  403 */     if (elsep != null) {
/*  404 */       elsep.accept(this);
/*  405 */       if (!thenHasReturned) {
/*  406 */         this.bytecode.write16bit(pc2, this.bytecode.currentPc() - pc2 + 1);
/*      */       }
/*  408 */       this.hasReturned = ((thenHasReturned) && (this.hasReturned));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void atWhileStmnt(Stmnt st, boolean notDo) throws CompileError {
/*  413 */     ArrayList prevBreakList = this.breakList;
/*  414 */     ArrayList prevContList = this.continueList;
/*  415 */     this.breakList = new ArrayList();
/*  416 */     this.continueList = new ArrayList();
/*      */ 
/*  418 */     ASTree expr = st.head();
/*  419 */     Stmnt body = (Stmnt)st.tail();
/*      */ 
/*  421 */     int pc = 0;
/*  422 */     if (notDo) {
/*  423 */       this.bytecode.addOpcode(167);
/*  424 */       pc = this.bytecode.currentPc();
/*  425 */       this.bytecode.addIndex(0);
/*      */     }
/*      */ 
/*  428 */     int pc2 = this.bytecode.currentPc();
/*  429 */     if (body != null) {
/*  430 */       body.accept(this);
/*      */     }
/*  432 */     int pc3 = this.bytecode.currentPc();
/*  433 */     if (notDo) {
/*  434 */       this.bytecode.write16bit(pc, pc3 - pc + 1);
/*      */     }
/*  436 */     boolean alwaysBranch = compileBooleanExpr(true, expr);
/*  437 */     this.bytecode.addIndex(pc2 - this.bytecode.currentPc() + 1);
/*      */ 
/*  439 */     patchGoto(this.breakList, this.bytecode.currentPc());
/*  440 */     patchGoto(this.continueList, pc3);
/*  441 */     this.continueList = prevContList;
/*  442 */     this.breakList = prevBreakList;
/*  443 */     this.hasReturned = alwaysBranch;
/*      */   }
/*      */ 
/*      */   protected void patchGoto(ArrayList list, int targetPc) {
/*  447 */     int n = list.size();
/*  448 */     for (int i = 0; i < n; i++) {
/*  449 */       int pc = ((Integer)list.get(i)).intValue();
/*  450 */       this.bytecode.write16bit(pc, targetPc - pc + 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void atForStmnt(Stmnt st) throws CompileError {
/*  455 */     ArrayList prevBreakList = this.breakList;
/*  456 */     ArrayList prevContList = this.continueList;
/*  457 */     this.breakList = new ArrayList();
/*  458 */     this.continueList = new ArrayList();
/*      */ 
/*  460 */     Stmnt init = (Stmnt)st.head();
/*  461 */     ASTList p = st.tail();
/*  462 */     ASTree expr = p.head();
/*  463 */     p = p.tail();
/*  464 */     Stmnt update = (Stmnt)p.head();
/*  465 */     Stmnt body = (Stmnt)p.tail();
/*      */ 
/*  467 */     if (init != null) {
/*  468 */       init.accept(this);
/*      */     }
/*  470 */     int pc = this.bytecode.currentPc();
/*  471 */     int pc2 = 0;
/*  472 */     if (expr != null) {
/*  473 */       compileBooleanExpr(false, expr);
/*  474 */       pc2 = this.bytecode.currentPc();
/*  475 */       this.bytecode.addIndex(0);
/*      */     }
/*      */ 
/*  478 */     if (body != null) {
/*  479 */       body.accept(this);
/*      */     }
/*  481 */     int pc3 = this.bytecode.currentPc();
/*  482 */     if (update != null) {
/*  483 */       update.accept(this);
/*      */     }
/*  485 */     this.bytecode.addOpcode(167);
/*  486 */     this.bytecode.addIndex(pc - this.bytecode.currentPc() + 1);
/*      */ 
/*  488 */     int pc4 = this.bytecode.currentPc();
/*  489 */     if (expr != null) {
/*  490 */       this.bytecode.write16bit(pc2, pc4 - pc2 + 1);
/*      */     }
/*  492 */     patchGoto(this.breakList, pc4);
/*  493 */     patchGoto(this.continueList, pc3);
/*  494 */     this.continueList = prevContList;
/*  495 */     this.breakList = prevBreakList;
/*  496 */     this.hasReturned = false;
/*      */   }
/*      */ 
/*      */   private void atSwitchStmnt(Stmnt st) throws CompileError {
/*  500 */     compileExpr(st.head());
/*      */ 
/*  502 */     ArrayList prevBreakList = this.breakList;
/*  503 */     this.breakList = new ArrayList();
/*  504 */     int opcodePc = this.bytecode.currentPc();
/*  505 */     this.bytecode.addOpcode(171);
/*  506 */     int npads = 3 - (opcodePc & 0x3);
/*  507 */     while (npads-- > 0) {
/*  508 */       this.bytecode.add(0);
/*      */     }
/*  510 */     Stmnt body = (Stmnt)st.tail();
/*  511 */     int npairs = 0;
/*  512 */     for (ASTList list = body; list != null; list = list.tail()) {
/*  513 */       if (((Stmnt)list.head()).getOperator() == 304) {
/*  514 */         npairs++;
/*      */       }
/*      */     }
/*  517 */     int opcodePc2 = this.bytecode.currentPc();
/*  518 */     this.bytecode.addGap(4);
/*  519 */     this.bytecode.add32bit(npairs);
/*  520 */     this.bytecode.addGap(npairs * 8);
/*      */ 
/*  522 */     long[] pairs = new long[npairs];
/*  523 */     int ipairs = 0;
/*  524 */     int defaultPc = -1;
/*  525 */     for (ASTList list = body; list != null; list = list.tail()) {
/*  526 */       Stmnt label = (Stmnt)list.head();
/*  527 */       int op = label.getOperator();
/*  528 */       if (op == 310)
/*  529 */         defaultPc = this.bytecode.currentPc();
/*  530 */       else if (op != 304)
/*  531 */         fatal();
/*      */       else {
/*  533 */         pairs[(ipairs++)] = ((computeLabel(label.head()) << 32) + (this.bytecode.currentPc() - opcodePc & 0xFFFFFFFF));
/*      */       }
/*      */ 
/*  538 */       this.hasReturned = false;
/*  539 */       ((Stmnt)label.tail()).accept(this);
/*      */     }
/*      */ 
/*  542 */     Arrays.sort(pairs);
/*  543 */     int pc = opcodePc2 + 8;
/*  544 */     for (int i = 0; i < npairs; i++) {
/*  545 */       this.bytecode.write32bit(pc, (int)(pairs[i] >>> 32));
/*  546 */       this.bytecode.write32bit(pc + 4, (int)pairs[i]);
/*  547 */       pc += 8;
/*      */     }
/*      */ 
/*  550 */     if ((defaultPc < 0) || (this.breakList.size() > 0)) {
/*  551 */       this.hasReturned = false;
/*      */     }
/*  553 */     int endPc = this.bytecode.currentPc();
/*  554 */     if (defaultPc < 0) {
/*  555 */       defaultPc = endPc;
/*      */     }
/*  557 */     this.bytecode.write32bit(opcodePc2, defaultPc - opcodePc);
/*      */ 
/*  559 */     patchGoto(this.breakList, endPc);
/*  560 */     this.breakList = prevBreakList;
/*      */   }
/*      */ 
/*      */   private int computeLabel(ASTree expr) throws CompileError {
/*  564 */     doTypeCheck(expr);
/*  565 */     expr = TypeChecker.stripPlusExpr(expr);
/*  566 */     if ((expr instanceof IntConst)) {
/*  567 */       return (int)((IntConst)expr).get();
/*      */     }
/*  569 */     throw new CompileError("bad case label");
/*      */   }
/*      */ 
/*      */   private void atBreakStmnt(Stmnt st, boolean notCont)
/*      */     throws CompileError
/*      */   {
/*  575 */     if (st.head() != null) {
/*  576 */       throw new CompileError("sorry, not support labeled break or continue");
/*      */     }
/*      */ 
/*  579 */     this.bytecode.addOpcode(167);
/*  580 */     Integer pc = new Integer(this.bytecode.currentPc());
/*  581 */     this.bytecode.addIndex(0);
/*  582 */     if (notCont)
/*  583 */       this.breakList.add(pc);
/*      */     else
/*  585 */       this.continueList.add(pc);
/*      */   }
/*      */ 
/*      */   protected void atReturnStmnt(Stmnt st) throws CompileError {
/*  589 */     atReturnStmnt2(st.getLeft());
/*      */   }
/*      */ 
/*      */   protected final void atReturnStmnt2(ASTree result)
/*      */     throws CompileError
/*      */   {
/*      */     int op;
/*      */     int op;
/*  594 */     if (result == null) {
/*  595 */       op = 177;
/*      */     } else {
/*  597 */       compileExpr(result);
/*      */       int op;
/*  598 */       if (this.arrayDim > 0) {
/*  599 */         op = 176;
/*      */       } else {
/*  601 */         int type = this.exprType;
/*      */         int op;
/*  602 */         if (type == 312) {
/*  603 */           op = 175;
/*      */         }
/*      */         else
/*      */         {
/*      */           int op;
/*  604 */           if (type == 317) {
/*  605 */             op = 174;
/*      */           }
/*      */           else
/*      */           {
/*      */             int op;
/*  606 */             if (type == 326) {
/*  607 */               op = 173;
/*      */             }
/*      */             else
/*      */             {
/*      */               int op;
/*  608 */               if (isRefType(type))
/*  609 */                 op = 176;
/*      */               else
/*  611 */                 op = 172; 
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  615 */     for (ReturnHook har = this.returnHooks; har != null; har = har.next) {
/*  616 */       if (har.doit(this.bytecode, op)) {
/*  617 */         this.hasReturned = true;
/*  618 */         return;
/*      */       }
/*      */     }
/*  621 */     this.bytecode.addOpcode(op);
/*  622 */     this.hasReturned = true;
/*      */   }
/*      */ 
/*      */   private void atThrowStmnt(Stmnt st) throws CompileError {
/*  626 */     ASTree e = st.getLeft();
/*  627 */     compileExpr(e);
/*  628 */     if ((this.exprType != 307) || (this.arrayDim > 0)) {
/*  629 */       throw new CompileError("bad throw statement");
/*      */     }
/*  631 */     this.bytecode.addOpcode(191);
/*  632 */     this.hasReturned = true;
/*      */   }
/*      */ 
/*      */   protected void atTryStmnt(Stmnt st)
/*      */     throws CompileError
/*      */   {
/*  638 */     this.hasReturned = false;
/*      */   }
/*      */ 
/*      */   private void atSyncStmnt(Stmnt st) throws CompileError {
/*  642 */     int nbreaks = getListSize(this.breakList);
/*  643 */     int ncontinues = getListSize(this.continueList);
/*      */ 
/*  645 */     compileExpr(st.head());
/*  646 */     if ((this.exprType != 307) && (this.arrayDim == 0)) {
/*  647 */       throw new CompileError("bad type expr for synchronized block");
/*      */     }
/*  649 */     Bytecode bc = this.bytecode;
/*  650 */     final int var = bc.getMaxLocals();
/*  651 */     bc.incMaxLocals(1);
/*  652 */     bc.addOpcode(89);
/*  653 */     bc.addAstore(var);
/*  654 */     bc.addOpcode(194);
/*      */ 
/*  656 */     ReturnHook rh = new ReturnHook(this) { private final int val$var;
/*      */ 
/*  658 */       protected boolean doit(Bytecode b, int opcode) { b.addAload(var);
/*  659 */         b.addOpcode(195);
/*  660 */         return false;
/*      */       }
/*      */     };
/*  664 */     int pc = bc.currentPc();
/*  665 */     Stmnt body = (Stmnt)st.tail();
/*  666 */     if (body != null) {
/*  667 */       body.accept(this);
/*      */     }
/*  669 */     int pc2 = bc.currentPc();
/*  670 */     int pc3 = 0;
/*  671 */     if (!this.hasReturned) {
/*  672 */       rh.doit(bc, 0);
/*  673 */       bc.addOpcode(167);
/*  674 */       pc3 = bc.currentPc();
/*  675 */       bc.addIndex(0);
/*      */     }
/*      */ 
/*  678 */     if (pc < pc2) {
/*  679 */       int pc4 = bc.currentPc();
/*  680 */       rh.doit(bc, 0);
/*  681 */       bc.addOpcode(191);
/*  682 */       bc.addExceptionHandler(pc, pc2, pc4, 0);
/*      */     }
/*      */ 
/*  685 */     if (!this.hasReturned) {
/*  686 */       bc.write16bit(pc3, bc.currentPc() - pc3 + 1);
/*      */     }
/*  688 */     rh.remove(this);
/*      */ 
/*  690 */     if ((getListSize(this.breakList) != nbreaks) || (getListSize(this.continueList) != ncontinues))
/*      */     {
/*  692 */       throw new CompileError("sorry, cannot break/continue in synchronized block");
/*      */     }
/*      */   }
/*      */ 
/*      */   private static int getListSize(ArrayList list) {
/*  697 */     return list == null ? 0 : list.size();
/*      */   }
/*      */ 
/*      */   private static boolean isPlusPlusExpr(ASTree expr) {
/*  701 */     if ((expr instanceof Expr)) {
/*  702 */       int op = ((Expr)expr).getOperator();
/*  703 */       return (op == 362) || (op == 363);
/*      */     }
/*      */ 
/*  706 */     return false;
/*      */   }
/*      */ 
/*      */   public void atDeclarator(Declarator d) throws CompileError {
/*  710 */     d.setLocalVar(getMaxLocals());
/*  711 */     d.setClassName(resolveClassName(d.getClassName()));
/*      */     int size;
/*      */     int size;
/*  714 */     if (is2word(d.getType(), d.getArrayDim()))
/*  715 */       size = 2;
/*      */     else {
/*  717 */       size = 1;
/*      */     }
/*  719 */     incMaxLocals(size);
/*      */ 
/*  723 */     ASTree init = d.getInitializer();
/*  724 */     if (init != null) {
/*  725 */       doTypeCheck(init);
/*  726 */       atVariableAssign(null, 61, null, d, init, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public abstract void atNewExpr(NewExpr paramNewExpr) throws CompileError;
/*      */ 
/*      */   public abstract void atArrayInit(ArrayInit paramArrayInit) throws CompileError;
/*      */ 
/*      */   public void atAssignExpr(AssignExpr expr) throws CompileError {
/*  735 */     atAssignExpr(expr, true);
/*      */   }
/*      */ 
/*      */   protected void atAssignExpr(AssignExpr expr, boolean doDup)
/*      */     throws CompileError
/*      */   {
/*  742 */     int op = expr.getOperator();
/*  743 */     ASTree left = expr.oprand1();
/*  744 */     ASTree right = expr.oprand2();
/*  745 */     if ((left instanceof Variable)) {
/*  746 */       atVariableAssign(expr, op, (Variable)left, ((Variable)left).getDeclarator(), right, doDup);
/*      */     }
/*      */     else
/*      */     {
/*  750 */       if ((left instanceof Expr)) {
/*  751 */         Expr e = (Expr)left;
/*  752 */         if (e.getOperator() == 65) {
/*  753 */           atArrayAssign(expr, op, (Expr)left, right, doDup);
/*  754 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  758 */       atFieldAssign(expr, op, left, right, doDup);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static void badAssign(Expr expr)
/*      */     throws CompileError
/*      */   {
/*      */     String msg;
/*      */     String msg;
/*  764 */     if (expr == null)
/*  765 */       msg = "incompatible type for assignment";
/*      */     else {
/*  767 */       msg = "incompatible type for " + expr.getName();
/*      */     }
/*  769 */     throw new CompileError(msg);
/*      */   }
/*      */ 
/*      */   private void atVariableAssign(Expr expr, int op, Variable var, Declarator d, ASTree right, boolean doDup)
/*      */     throws CompileError
/*      */   {
/*  780 */     int varType = d.getType();
/*  781 */     int varArray = d.getArrayDim();
/*  782 */     String varClass = d.getClassName();
/*  783 */     int varNo = getLocalVar(d);
/*      */ 
/*  785 */     if (op != 61) {
/*  786 */       atVariable(var);
/*      */     }
/*      */ 
/*  789 */     if ((expr == null) && ((right instanceof ArrayInit)))
/*  790 */       atArrayVariableAssign((ArrayInit)right, varType, varArray, varClass);
/*      */     else {
/*  792 */       atAssignCore(expr, op, right, varType, varArray, varClass);
/*      */     }
/*  794 */     if (doDup) {
/*  795 */       if (is2word(varType, varArray))
/*  796 */         this.bytecode.addOpcode(92);
/*      */       else
/*  798 */         this.bytecode.addOpcode(89);
/*      */     }
/*  800 */     if (varArray > 0)
/*  801 */       this.bytecode.addAstore(varNo);
/*  802 */     else if (varType == 312)
/*  803 */       this.bytecode.addDstore(varNo);
/*  804 */     else if (varType == 317)
/*  805 */       this.bytecode.addFstore(varNo);
/*  806 */     else if (varType == 326)
/*  807 */       this.bytecode.addLstore(varNo);
/*  808 */     else if (isRefType(varType))
/*  809 */       this.bytecode.addAstore(varNo);
/*      */     else {
/*  811 */       this.bytecode.addIstore(varNo);
/*      */     }
/*  813 */     this.exprType = varType;
/*  814 */     this.arrayDim = varArray;
/*  815 */     this.className = varClass;
/*      */   }
/*      */ 
/*      */   protected abstract void atArrayVariableAssign(ArrayInit paramArrayInit, int paramInt1, int paramInt2, String paramString)
/*      */     throws CompileError;
/*      */ 
/*      */   private void atArrayAssign(Expr expr, int op, Expr array, ASTree right, boolean doDup)
/*      */     throws CompileError
/*      */   {
/*  824 */     arrayAccess(array.oprand1(), array.oprand2());
/*      */ 
/*  826 */     if (op != 61) {
/*  827 */       this.bytecode.addOpcode(92);
/*  828 */       this.bytecode.addOpcode(getArrayReadOp(this.exprType, this.arrayDim));
/*      */     }
/*      */ 
/*  831 */     int aType = this.exprType;
/*  832 */     int aDim = this.arrayDim;
/*  833 */     String cname = this.className;
/*      */ 
/*  835 */     atAssignCore(expr, op, right, aType, aDim, cname);
/*      */ 
/*  837 */     if (doDup) {
/*  838 */       if (is2word(aType, aDim))
/*  839 */         this.bytecode.addOpcode(94);
/*      */       else
/*  841 */         this.bytecode.addOpcode(91);
/*      */     }
/*  843 */     this.bytecode.addOpcode(getArrayWriteOp(aType, aDim));
/*  844 */     this.exprType = aType;
/*  845 */     this.arrayDim = aDim;
/*  846 */     this.className = cname;
/*      */   }
/*      */ 
/*      */   protected abstract void atFieldAssign(Expr paramExpr, int paramInt, ASTree paramASTree1, ASTree paramASTree2, boolean paramBoolean)
/*      */     throws CompileError;
/*      */ 
/*      */   protected void atAssignCore(Expr expr, int op, ASTree right, int type, int dim, String cname)
/*      */     throws CompileError
/*      */   {
/*  856 */     if ((op == 354) && (dim == 0) && (type == 307)) {
/*  857 */       atStringPlusEq(expr, type, dim, cname, right);
/*      */     } else {
/*  859 */       right.accept(this);
/*  860 */       if ((invalidDim(this.exprType, this.arrayDim, this.className, type, dim, cname, false)) || ((op != 61) && (dim > 0)))
/*      */       {
/*  862 */         badAssign(expr);
/*      */       }
/*  864 */       if (op != 61) {
/*  865 */         int token = assignOps[(op - 351)];
/*  866 */         int k = lookupBinOp(token);
/*  867 */         if (k < 0) {
/*  868 */           fatal();
/*      */         }
/*  870 */         atArithBinExpr(expr, token, k, type);
/*      */       }
/*      */     }
/*      */ 
/*  874 */     if ((op != 61) || ((dim == 0) && (!isRefType(type))))
/*  875 */       atNumCastExpr(this.exprType, type);
/*      */   }
/*      */ 
/*      */   private void atStringPlusEq(Expr expr, int type, int dim, String cname, ASTree right)
/*      */     throws CompileError
/*      */   {
/*  884 */     if (!"java/lang/String".equals(cname)) {
/*  885 */       badAssign(expr);
/*      */     }
/*  887 */     convToString(type, dim);
/*  888 */     right.accept(this);
/*  889 */     convToString(this.exprType, this.arrayDim);
/*  890 */     this.bytecode.addInvokevirtual("java.lang.String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
/*      */ 
/*  892 */     this.exprType = 307;
/*  893 */     this.arrayDim = 0;
/*  894 */     this.className = "java/lang/String";
/*      */   }
/*      */ 
/*      */   private boolean invalidDim(int srcType, int srcDim, String srcClass, int destType, int destDim, String destClass, boolean isCast)
/*      */   {
/*  901 */     if (srcDim != destDim) {
/*  902 */       if (srcType == 412)
/*  903 */         return false;
/*  904 */       if ((destDim == 0) && (destType == 307) && ("java/lang/Object".equals(destClass)))
/*      */       {
/*  906 */         return false;
/*  907 */       }if ((isCast) && (srcDim == 0) && (srcType == 307) && ("java/lang/Object".equals(srcClass)))
/*      */       {
/*  909 */         return false;
/*      */       }
/*  911 */       return true;
/*      */     }
/*  913 */     return false;
/*      */   }
/*      */ 
/*      */   public void atCondExpr(CondExpr expr) throws CompileError {
/*  917 */     booleanExpr(false, expr.condExpr());
/*  918 */     int pc = this.bytecode.currentPc();
/*  919 */     this.bytecode.addIndex(0);
/*  920 */     expr.thenExpr().accept(this);
/*  921 */     int dim1 = this.arrayDim;
/*  922 */     this.bytecode.addOpcode(167);
/*  923 */     int pc2 = this.bytecode.currentPc();
/*  924 */     this.bytecode.addIndex(0);
/*  925 */     this.bytecode.write16bit(pc, this.bytecode.currentPc() - pc + 1);
/*  926 */     expr.elseExpr().accept(this);
/*  927 */     if (dim1 != this.arrayDim) {
/*  928 */       throw new CompileError("type mismatch in ?:");
/*      */     }
/*  930 */     this.bytecode.write16bit(pc2, this.bytecode.currentPc() - pc2 + 1);
/*      */   }
/*      */ 
/*      */   static int lookupBinOp(int token)
/*      */   {
/*  947 */     int[] code = binOp;
/*  948 */     int s = code.length;
/*  949 */     for (int k = 0; k < s; k += 5) {
/*  950 */       if (code[k] == token)
/*  951 */         return k;
/*      */     }
/*  953 */     return -1;
/*      */   }
/*      */ 
/*      */   public void atBinExpr(BinExpr expr) throws CompileError {
/*  957 */     int token = expr.getOperator();
/*      */ 
/*  961 */     int k = lookupBinOp(token);
/*  962 */     if (k >= 0) {
/*  963 */       expr.oprand1().accept(this);
/*  964 */       ASTree right = expr.oprand2();
/*  965 */       if (right == null) {
/*  966 */         return;
/*      */       }
/*  968 */       int type1 = this.exprType;
/*  969 */       int dim1 = this.arrayDim;
/*  970 */       String cname1 = this.className;
/*  971 */       right.accept(this);
/*  972 */       if (dim1 != this.arrayDim) {
/*  973 */         throw new CompileError("incompatible array types");
/*      */       }
/*  975 */       if ((token == 43) && (dim1 == 0) && ((type1 == 307) || (this.exprType == 307)))
/*      */       {
/*  977 */         atStringConcatExpr(expr, type1, dim1, cname1);
/*      */       }
/*  979 */       else atArithBinExpr(expr, token, k, type1);
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  984 */       booleanExpr(true, expr);
/*  985 */       this.bytecode.addIndex(7);
/*  986 */       this.bytecode.addIconst(0);
/*  987 */       this.bytecode.addOpcode(167);
/*  988 */       this.bytecode.addIndex(4);
/*  989 */       this.bytecode.addIconst(1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void atArithBinExpr(Expr expr, int token, int index, int type1)
/*      */     throws CompileError
/*      */   {
/* 1000 */     if (this.arrayDim != 0) {
/* 1001 */       badTypes(expr);
/*      */     }
/* 1003 */     int type2 = this.exprType;
/* 1004 */     if ((token == 364) || (token == 366) || (token == 370)) {
/* 1005 */       if ((type2 == 324) || (type2 == 334) || (type2 == 306) || (type2 == 303))
/*      */       {
/* 1007 */         this.exprType = type1;
/*      */       }
/* 1009 */       else badTypes(expr); 
/*      */     }
/*      */     else {
/* 1011 */       convertOprandTypes(type1, type2, expr);
/*      */     }
/* 1013 */     int p = typePrecedence(this.exprType);
/* 1014 */     if (p >= 0) {
/* 1015 */       int op = binOp[(index + p + 1)];
/* 1016 */       if (op != 0) {
/* 1017 */         if ((p == 3) && (this.exprType != 301)) {
/* 1018 */           this.exprType = 324;
/*      */         }
/* 1020 */         this.bytecode.addOpcode(op);
/* 1021 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1025 */     badTypes(expr);
/*      */   }
/*      */ 
/*      */   private void atStringConcatExpr(Expr expr, int type1, int dim1, String cname1)
/*      */     throws CompileError
/*      */   {
/* 1031 */     int type2 = this.exprType;
/* 1032 */     int dim2 = this.arrayDim;
/* 1033 */     boolean type2Is2 = is2word(type2, dim2);
/* 1034 */     boolean type2IsString = (type2 == 307) && ("java/lang/String".equals(this.className));
/*      */ 
/* 1037 */     if (type2Is2) {
/* 1038 */       convToString(type2, dim2);
/*      */     }
/* 1040 */     if (is2word(type1, dim1)) {
/* 1041 */       this.bytecode.addOpcode(91);
/* 1042 */       this.bytecode.addOpcode(87);
/*      */     }
/*      */     else {
/* 1045 */       this.bytecode.addOpcode(95);
/*      */     }
/*      */ 
/* 1048 */     convToString(type1, dim1);
/* 1049 */     this.bytecode.addOpcode(95);
/*      */ 
/* 1051 */     if ((!type2Is2) && (!type2IsString)) {
/* 1052 */       convToString(type2, dim2);
/*      */     }
/* 1054 */     this.bytecode.addInvokevirtual("java.lang.String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
/*      */ 
/* 1056 */     this.exprType = 307;
/* 1057 */     this.arrayDim = 0;
/* 1058 */     this.className = "java/lang/String";
/*      */   }
/*      */ 
/*      */   private void convToString(int type, int dim) throws CompileError {
/* 1062 */     String method = "valueOf";
/*      */ 
/* 1064 */     if ((isRefType(type)) || (dim > 0)) {
/* 1065 */       this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
/*      */     }
/* 1067 */     else if (type == 312) {
/* 1068 */       this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(D)Ljava/lang/String;");
/*      */     }
/* 1070 */     else if (type == 317) {
/* 1071 */       this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(F)Ljava/lang/String;");
/*      */     }
/* 1073 */     else if (type == 326) {
/* 1074 */       this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(J)Ljava/lang/String;");
/*      */     }
/* 1076 */     else if (type == 301) {
/* 1077 */       this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(Z)Ljava/lang/String;");
/*      */     }
/* 1079 */     else if (type == 306) {
/* 1080 */       this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(C)Ljava/lang/String;");
/*      */     } else {
/* 1082 */       if (type == 344) {
/* 1083 */         throw new CompileError("void type expression");
/*      */       }
/* 1085 */       this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(I)Ljava/lang/String;");
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean booleanExpr(boolean branchIf, ASTree expr)
/*      */     throws CompileError
/*      */   {
/* 1098 */     int op = getCompOperator(expr);
/* 1099 */     if (op == 358) {
/* 1100 */       BinExpr bexpr = (BinExpr)expr;
/* 1101 */       int type1 = compileOprands(bexpr);
/*      */ 
/* 1104 */       compareExpr(branchIf, bexpr.getOperator(), type1, bexpr);
/*      */     }
/* 1106 */     else if (op == 33) {
/* 1107 */       booleanExpr(!branchIf, ((Expr)expr).oprand1());
/*      */     }
/*      */     else
/*      */     {
/*      */       boolean isAndAnd;
/* 1108 */       if (((isAndAnd = op == 369 ? 1 : 0) != 0) || (op == 368)) {
/* 1109 */         BinExpr bexpr = (BinExpr)expr;
/* 1110 */         booleanExpr(!isAndAnd, bexpr.oprand1());
/* 1111 */         int pc = this.bytecode.currentPc();
/* 1112 */         this.bytecode.addIndex(0);
/*      */ 
/* 1114 */         booleanExpr(isAndAnd, bexpr.oprand2());
/* 1115 */         this.bytecode.write16bit(pc, this.bytecode.currentPc() - pc + 3);
/* 1116 */         if (branchIf != isAndAnd) {
/* 1117 */           this.bytecode.addIndex(6);
/* 1118 */           this.bytecode.addOpcode(167);
/*      */         }
/*      */       } else {
/* 1121 */         if (isAlwaysBranch(expr, branchIf)) {
/* 1122 */           this.bytecode.addOpcode(167);
/* 1123 */           return true;
/*      */         }
/*      */ 
/* 1126 */         expr.accept(this);
/* 1127 */         if ((this.exprType != 301) || (this.arrayDim != 0)) {
/* 1128 */           throw new CompileError("boolean expr is required");
/*      */         }
/* 1130 */         this.bytecode.addOpcode(branchIf ? 154 : 153);
/*      */       }
/*      */     }
/* 1133 */     this.exprType = 301;
/* 1134 */     this.arrayDim = 0;
/* 1135 */     return false;
/*      */   }
/*      */ 
/*      */   private static boolean isAlwaysBranch(ASTree expr, boolean branchIf)
/*      */   {
/* 1140 */     if ((expr instanceof Keyword)) {
/* 1141 */       int t = ((Keyword)expr).get();
/* 1142 */       return t == 410;
/*      */     }
/*      */ 
/* 1145 */     return false;
/*      */   }
/*      */ 
/*      */   static int getCompOperator(ASTree expr) throws CompileError {
/* 1149 */     if ((expr instanceof Expr)) {
/* 1150 */       Expr bexpr = (Expr)expr;
/* 1151 */       int token = bexpr.getOperator();
/* 1152 */       if (token == 33)
/* 1153 */         return 33;
/* 1154 */       if (((bexpr instanceof BinExpr)) && (token != 368) && (token != 369) && (token != 38) && (token != 124))
/*      */       {
/* 1157 */         return 358;
/*      */       }
/* 1159 */       return token;
/*      */     }
/*      */ 
/* 1162 */     return 32;
/*      */   }
/*      */ 
/*      */   private int compileOprands(BinExpr expr) throws CompileError {
/* 1166 */     expr.oprand1().accept(this);
/* 1167 */     int type1 = this.exprType;
/* 1168 */     int dim1 = this.arrayDim;
/* 1169 */     expr.oprand2().accept(this);
/* 1170 */     if (dim1 != this.arrayDim) {
/* 1171 */       if ((type1 != 412) && (this.exprType != 412))
/* 1172 */         throw new CompileError("incompatible array types");
/* 1173 */       if (this.exprType == 412)
/* 1174 */         this.arrayDim = dim1;
/*      */     }
/* 1176 */     if (type1 == 412) {
/* 1177 */       return this.exprType;
/*      */     }
/* 1179 */     return type1;
/*      */   }
/*      */ 
/*      */   private void compareExpr(boolean branchIf, int token, int type1, BinExpr expr)
/*      */     throws CompileError
/*      */   {
/* 1205 */     if (this.arrayDim == 0) {
/* 1206 */       convertOprandTypes(type1, this.exprType, expr);
/*      */     }
/* 1208 */     int p = typePrecedence(this.exprType);
/* 1209 */     if ((p == -1) || (this.arrayDim > 0)) {
/* 1210 */       if (token == 358)
/* 1211 */         this.bytecode.addOpcode(branchIf ? 165 : 166);
/* 1212 */       else if (token == 350)
/* 1213 */         this.bytecode.addOpcode(branchIf ? 166 : 165);
/*      */       else
/* 1215 */         badTypes(expr);
/*      */     }
/* 1217 */     else if (p == 3) {
/* 1218 */       int[] op = ifOp;
/* 1219 */       for (int i = 0; i < op.length; i += 3) {
/* 1220 */         if (op[i] == token) {
/* 1221 */           this.bytecode.addOpcode(op[(i + 2)]);
/* 1222 */           return;
/*      */         }
/*      */       }
/* 1225 */       badTypes(expr);
/*      */     }
/*      */     else {
/* 1228 */       if (p == 0) {
/* 1229 */         if ((token == 60) || (token == 357))
/* 1230 */           this.bytecode.addOpcode(152);
/*      */         else
/* 1232 */           this.bytecode.addOpcode(151);
/* 1233 */       } else if (p == 1) {
/* 1234 */         if ((token == 60) || (token == 357))
/* 1235 */           this.bytecode.addOpcode(150);
/*      */         else
/* 1237 */           this.bytecode.addOpcode(149);
/* 1238 */       } else if (p == 2)
/* 1239 */         this.bytecode.addOpcode(148);
/*      */       else {
/* 1241 */         fatal();
/*      */       }
/* 1243 */       int[] op = ifOp2;
/* 1244 */       for (int i = 0; i < op.length; i += 3) {
/* 1245 */         if (op[i] == token) {
/* 1246 */           this.bytecode.addOpcode(op[(i + 2)]);
/* 1247 */           return;
/*      */         }
/*      */       }
/* 1250 */       badTypes(expr);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static void badTypes(Expr expr) throws CompileError {
/* 1255 */     throw new CompileError("invalid types for " + expr.getName());
/*      */   }
/*      */ 
/*      */   protected static boolean isRefType(int type)
/*      */   {
/* 1265 */     return (type == 307) || (type == 412);
/*      */   }
/*      */ 
/*      */   private static int typePrecedence(int type) {
/* 1269 */     if (type == 312)
/* 1270 */       return 0;
/* 1271 */     if (type == 317)
/* 1272 */       return 1;
/* 1273 */     if (type == 326)
/* 1274 */       return 2;
/* 1275 */     if (isRefType(type))
/* 1276 */       return -1;
/* 1277 */     if (type == 344) {
/* 1278 */       return -1;
/*      */     }
/* 1280 */     return 3;
/*      */   }
/*      */ 
/*      */   static boolean isP_INT(int type)
/*      */   {
/* 1285 */     return typePrecedence(type) == 3;
/*      */   }
/*      */ 
/*      */   static boolean rightIsStrong(int type1, int type2)
/*      */   {
/* 1290 */     int type1_p = typePrecedence(type1);
/* 1291 */     int type2_p = typePrecedence(type2);
/* 1292 */     return (type1_p >= 0) && (type2_p >= 0) && (type1_p > type2_p);
/*      */   }
/*      */ 
/*      */   private void convertOprandTypes(int type1, int type2, Expr expr)
/*      */     throws CompileError
/*      */   {
/* 1309 */     int type1_p = typePrecedence(type1);
/* 1310 */     int type2_p = typePrecedence(type2);
/*      */ 
/* 1312 */     if ((type2_p < 0) && (type1_p < 0)) {
/* 1313 */       return;
/*      */     }
/* 1315 */     if ((type2_p < 0) || (type1_p < 0))
/* 1316 */       badTypes(expr);
/*      */     int result_type;
/*      */     boolean rightStrong;
/*      */     int op;
/*      */     int result_type;
/* 1319 */     if (type1_p <= type2_p) {
/* 1320 */       boolean rightStrong = false;
/* 1321 */       this.exprType = type1;
/* 1322 */       int op = castOp[(type2_p * 4 + type1_p)];
/* 1323 */       result_type = type1_p;
/*      */     }
/*      */     else {
/* 1326 */       rightStrong = true;
/* 1327 */       op = castOp[(type1_p * 4 + type2_p)];
/* 1328 */       result_type = type2_p;
/*      */     }
/*      */ 
/* 1331 */     if (rightStrong) {
/* 1332 */       if ((result_type == 0) || (result_type == 2)) {
/* 1333 */         if ((type1_p == 0) || (type1_p == 2))
/* 1334 */           this.bytecode.addOpcode(94);
/*      */         else {
/* 1336 */           this.bytecode.addOpcode(93);
/*      */         }
/* 1338 */         this.bytecode.addOpcode(88);
/* 1339 */         this.bytecode.addOpcode(op);
/* 1340 */         this.bytecode.addOpcode(94);
/* 1341 */         this.bytecode.addOpcode(88);
/*      */       }
/* 1343 */       else if (result_type == 1) {
/* 1344 */         if (type1_p == 2) {
/* 1345 */           this.bytecode.addOpcode(91);
/* 1346 */           this.bytecode.addOpcode(87);
/*      */         }
/*      */         else {
/* 1349 */           this.bytecode.addOpcode(95);
/*      */         }
/* 1351 */         this.bytecode.addOpcode(op);
/* 1352 */         this.bytecode.addOpcode(95);
/*      */       }
/*      */       else {
/* 1355 */         fatal();
/*      */       }
/* 1357 */     } else if (op != 0)
/* 1358 */       this.bytecode.addOpcode(op);
/*      */   }
/*      */ 
/*      */   public void atCastExpr(CastExpr expr) throws CompileError {
/* 1362 */     String cname = resolveClassName(expr.getClassName());
/* 1363 */     String toClass = checkCastExpr(expr, cname);
/* 1364 */     int srcType = this.exprType;
/* 1365 */     this.exprType = expr.getType();
/* 1366 */     this.arrayDim = expr.getArrayDim();
/* 1367 */     this.className = cname;
/* 1368 */     if (toClass == null)
/* 1369 */       atNumCastExpr(srcType, this.exprType);
/*      */     else
/* 1371 */       this.bytecode.addCheckcast(toClass);
/*      */   }
/*      */ 
/*      */   public void atInstanceOfExpr(InstanceOfExpr expr) throws CompileError {
/* 1375 */     String cname = resolveClassName(expr.getClassName());
/* 1376 */     String toClass = checkCastExpr(expr, cname);
/* 1377 */     this.bytecode.addInstanceof(toClass);
/* 1378 */     this.exprType = 301;
/* 1379 */     this.arrayDim = 0;
/*      */   }
/*      */ 
/*      */   private String checkCastExpr(CastExpr expr, String name)
/*      */     throws CompileError
/*      */   {
/* 1385 */     String msg = "invalid cast";
/* 1386 */     ASTree oprand = expr.getOprand();
/* 1387 */     int dim = expr.getArrayDim();
/* 1388 */     int type = expr.getType();
/* 1389 */     oprand.accept(this);
/* 1390 */     int srcType = this.exprType;
/* 1391 */     if ((invalidDim(srcType, this.arrayDim, this.className, type, dim, name, true)) || (srcType == 344) || (type == 344))
/*      */     {
/* 1393 */       throw new CompileError("invalid cast");
/*      */     }
/* 1395 */     if (type == 307) {
/* 1396 */       if (!isRefType(srcType)) {
/* 1397 */         throw new CompileError("invalid cast");
/*      */       }
/* 1399 */       return toJvmArrayName(name, dim);
/*      */     }
/*      */ 
/* 1402 */     if (dim > 0) {
/* 1403 */       return toJvmTypeName(type, dim);
/*      */     }
/* 1405 */     return null;
/*      */   }
/*      */ 
/*      */   void atNumCastExpr(int srcType, int destType)
/*      */     throws CompileError
/*      */   {
/* 1411 */     if (srcType == destType) {
/* 1412 */       return;
/*      */     }
/*      */ 
/* 1415 */     int stype = typePrecedence(srcType);
/* 1416 */     int dtype = typePrecedence(destType);
/*      */     int op;
/*      */     int op;
/* 1417 */     if ((0 <= stype) && (stype < 3))
/* 1418 */       op = castOp[(stype * 4 + dtype)];
/*      */     else
/* 1420 */       op = 0;
/*      */     int op2;
/*      */     int op2;
/* 1422 */     if (destType == 312) {
/* 1423 */       op2 = 135;
/*      */     }
/*      */     else
/*      */     {
/*      */       int op2;
/* 1424 */       if (destType == 317) {
/* 1425 */         op2 = 134;
/*      */       }
/*      */       else
/*      */       {
/*      */         int op2;
/* 1426 */         if (destType == 326) {
/* 1427 */           op2 = 133;
/*      */         }
/*      */         else
/*      */         {
/*      */           int op2;
/* 1428 */           if (destType == 334) {
/* 1429 */             op2 = 147;
/*      */           }
/*      */           else
/*      */           {
/*      */             int op2;
/* 1430 */             if (destType == 306) {
/* 1431 */               op2 = 146;
/*      */             }
/*      */             else
/*      */             {
/*      */               int op2;
/* 1432 */               if (destType == 303)
/* 1433 */                 op2 = 145;
/*      */               else
/* 1435 */                 op2 = 0; 
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1437 */     if (op != 0) {
/* 1438 */       this.bytecode.addOpcode(op);
/*      */     }
/* 1440 */     if (((op == 0) || (op == 136) || (op == 139) || (op == 142)) && 
/* 1441 */       (op2 != 0))
/* 1442 */       this.bytecode.addOpcode(op2);
/*      */   }
/*      */ 
/*      */   public void atExpr(Expr expr)
/*      */     throws CompileError
/*      */   {
/* 1449 */     int token = expr.getOperator();
/* 1450 */     ASTree oprand = expr.oprand1();
/* 1451 */     if (token == 46) {
/* 1452 */       String member = ((Symbol)expr.oprand2()).get();
/* 1453 */       if (member.equals("class"))
/* 1454 */         atClassObject(expr);
/*      */       else
/* 1456 */         atFieldRead(expr);
/*      */     }
/* 1458 */     else if (token == 35)
/*      */     {
/* 1463 */       atFieldRead(expr);
/*      */     }
/* 1465 */     else if (token == 65) {
/* 1466 */       atArrayRead(oprand, expr.oprand2());
/* 1467 */     } else if ((token == 362) || (token == 363)) {
/* 1468 */       atPlusPlus(token, oprand, expr, true);
/* 1469 */     } else if (token == 33) {
/* 1470 */       booleanExpr(false, expr);
/* 1471 */       this.bytecode.addIndex(7);
/* 1472 */       this.bytecode.addIconst(1);
/* 1473 */       this.bytecode.addOpcode(167);
/* 1474 */       this.bytecode.addIndex(4);
/* 1475 */       this.bytecode.addIconst(0);
/*      */     }
/* 1477 */     else if (token == 67) {
/* 1478 */       fatal();
/*      */     } else {
/* 1480 */       expr.oprand1().accept(this);
/* 1481 */       int type = typePrecedence(this.exprType);
/* 1482 */       if (this.arrayDim > 0) {
/* 1483 */         badType(expr);
/*      */       }
/* 1485 */       if (token == 45) {
/* 1486 */         if (type == 0) {
/* 1487 */           this.bytecode.addOpcode(119);
/* 1488 */         } else if (type == 1) {
/* 1489 */           this.bytecode.addOpcode(118);
/* 1490 */         } else if (type == 2) {
/* 1491 */           this.bytecode.addOpcode(117);
/* 1492 */         } else if (type == 3) {
/* 1493 */           this.bytecode.addOpcode(116);
/* 1494 */           this.exprType = 324;
/*      */         }
/*      */         else {
/* 1497 */           badType(expr);
/*      */         }
/* 1499 */       } else if (token == 126) {
/* 1500 */         if (type == 3) {
/* 1501 */           this.bytecode.addIconst(-1);
/* 1502 */           this.bytecode.addOpcode(130);
/* 1503 */           this.exprType = 324;
/*      */         }
/* 1505 */         else if (type == 2) {
/* 1506 */           this.bytecode.addLconst(-1L);
/* 1507 */           this.bytecode.addOpcode(131);
/*      */         }
/*      */         else {
/* 1510 */           badType(expr);
/*      */         }
/*      */       }
/* 1513 */       else if (token == 43) {
/* 1514 */         if (type == -1) {
/* 1515 */           badType(expr);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1520 */         fatal();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/* 1525 */   protected static void badType(Expr expr) throws CompileError { throw new CompileError("invalid type for " + expr.getName()); }
/*      */ 
/*      */   public abstract void atCallExpr(CallExpr paramCallExpr) throws CompileError;
/*      */ 
/*      */   protected abstract void atFieldRead(ASTree paramASTree) throws CompileError;
/*      */ 
/*      */   public void atClassObject(Expr expr) throws CompileError
/*      */   {
/* 1533 */     ASTree op1 = expr.oprand1();
/* 1534 */     if (!(op1 instanceof Symbol)) {
/* 1535 */       throw new CompileError("fatal error: badly parsed .class expr");
/*      */     }
/* 1537 */     String cname = ((Symbol)op1).get();
/* 1538 */     if (cname.startsWith("[")) {
/* 1539 */       int i = cname.indexOf("[L");
/* 1540 */       if (i >= 0) {
/* 1541 */         String name = cname.substring(i + 2, cname.length() - 1);
/* 1542 */         String name2 = resolveClassName(name);
/* 1543 */         if (!name.equals(name2))
/*      */         {
/* 1548 */           name2 = MemberResolver.jvmToJavaName(name2);
/* 1549 */           StringBuffer sbuf = new StringBuffer();
/* 1550 */           while (i-- >= 0) {
/* 1551 */             sbuf.append('[');
/*      */           }
/* 1553 */           sbuf.append('L').append(name2).append(';');
/* 1554 */           cname = sbuf.toString();
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1559 */       cname = resolveClassName(MemberResolver.javaToJvmName(cname));
/* 1560 */       cname = MemberResolver.jvmToJavaName(cname);
/*      */     }
/*      */ 
/* 1563 */     atClassObject2(cname);
/* 1564 */     this.exprType = 307;
/* 1565 */     this.arrayDim = 0;
/* 1566 */     this.className = "java/lang/Class";
/*      */   }
/*      */ 
/*      */   protected void atClassObject2(String cname)
/*      */     throws CompileError
/*      */   {
/* 1572 */     int start = this.bytecode.currentPc();
/* 1573 */     this.bytecode.addLdc(cname);
/* 1574 */     this.bytecode.addInvokestatic("java.lang.Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;");
/*      */ 
/* 1576 */     int end = this.bytecode.currentPc();
/* 1577 */     this.bytecode.addOpcode(167);
/* 1578 */     int pc = this.bytecode.currentPc();
/* 1579 */     this.bytecode.addIndex(0);
/*      */ 
/* 1581 */     this.bytecode.addExceptionHandler(start, end, this.bytecode.currentPc(), "java.lang.ClassNotFoundException");
/*      */ 
/* 1600 */     this.bytecode.growStack(1);
/* 1601 */     this.bytecode.addInvokestatic("com.newrelic.javassist.runtime.DotClass", "fail", "(Ljava/lang/ClassNotFoundException;)Ljava/lang/NoClassDefFoundError;");
/*      */ 
/* 1604 */     this.bytecode.addOpcode(191);
/* 1605 */     this.bytecode.write16bit(pc, this.bytecode.currentPc() - pc + 1);
/*      */   }
/*      */ 
/*      */   public void atArrayRead(ASTree array, ASTree index)
/*      */     throws CompileError
/*      */   {
/* 1611 */     arrayAccess(array, index);
/* 1612 */     this.bytecode.addOpcode(getArrayReadOp(this.exprType, this.arrayDim));
/*      */   }
/*      */ 
/*      */   protected void arrayAccess(ASTree array, ASTree index)
/*      */     throws CompileError
/*      */   {
/* 1618 */     array.accept(this);
/* 1619 */     int type = this.exprType;
/* 1620 */     int dim = this.arrayDim;
/* 1621 */     if (dim == 0) {
/* 1622 */       throw new CompileError("bad array access");
/*      */     }
/* 1624 */     String cname = this.className;
/*      */ 
/* 1626 */     index.accept(this);
/* 1627 */     if ((typePrecedence(this.exprType) != 3) || (this.arrayDim > 0)) {
/* 1628 */       throw new CompileError("bad array index");
/*      */     }
/* 1630 */     this.exprType = type;
/* 1631 */     this.arrayDim = (dim - 1);
/* 1632 */     this.className = cname;
/*      */   }
/*      */ 
/*      */   protected static int getArrayReadOp(int type, int dim) {
/* 1636 */     if (dim > 0) {
/* 1637 */       return 50;
/*      */     }
/* 1639 */     switch (type) {
/*      */     case 312:
/* 1641 */       return 49;
/*      */     case 317:
/* 1643 */       return 48;
/*      */     case 326:
/* 1645 */       return 47;
/*      */     case 324:
/* 1647 */       return 46;
/*      */     case 334:
/* 1649 */       return 53;
/*      */     case 306:
/* 1651 */       return 52;
/*      */     case 301:
/*      */     case 303:
/* 1654 */       return 51;
/*      */     }
/* 1656 */     return 50;
/*      */   }
/*      */ 
/*      */   protected static int getArrayWriteOp(int type, int dim)
/*      */   {
/* 1661 */     if (dim > 0) {
/* 1662 */       return 83;
/*      */     }
/* 1664 */     switch (type) {
/*      */     case 312:
/* 1666 */       return 82;
/*      */     case 317:
/* 1668 */       return 81;
/*      */     case 326:
/* 1670 */       return 80;
/*      */     case 324:
/* 1672 */       return 79;
/*      */     case 334:
/* 1674 */       return 86;
/*      */     case 306:
/* 1676 */       return 85;
/*      */     case 301:
/*      */     case 303:
/* 1679 */       return 84;
/*      */     }
/* 1681 */     return 83;
/*      */   }
/*      */ 
/*      */   private void atPlusPlus(int token, ASTree oprand, Expr expr, boolean doDup)
/*      */     throws CompileError
/*      */   {
/* 1688 */     boolean isPost = oprand == null;
/* 1689 */     if (isPost) {
/* 1690 */       oprand = expr.oprand2();
/*      */     }
/* 1692 */     if ((oprand instanceof Variable)) {
/* 1693 */       Declarator d = ((Variable)oprand).getDeclarator();
/* 1694 */       int t = this.exprType = d.getType();
/* 1695 */       this.arrayDim = d.getArrayDim();
/* 1696 */       int var = getLocalVar(d);
/* 1697 */       if (this.arrayDim > 0) {
/* 1698 */         badType(expr);
/*      */       }
/* 1700 */       if (t == 312) {
/* 1701 */         this.bytecode.addDload(var);
/* 1702 */         if ((doDup) && (isPost)) {
/* 1703 */           this.bytecode.addOpcode(92);
/*      */         }
/* 1705 */         this.bytecode.addDconst(1.0D);
/* 1706 */         this.bytecode.addOpcode(token == 362 ? 99 : 103);
/* 1707 */         if ((doDup) && (!isPost)) {
/* 1708 */           this.bytecode.addOpcode(92);
/*      */         }
/* 1710 */         this.bytecode.addDstore(var);
/*      */       }
/* 1712 */       else if (t == 326) {
/* 1713 */         this.bytecode.addLload(var);
/* 1714 */         if ((doDup) && (isPost)) {
/* 1715 */           this.bytecode.addOpcode(92);
/*      */         }
/* 1717 */         this.bytecode.addLconst(1L);
/* 1718 */         this.bytecode.addOpcode(token == 362 ? 97 : 101);
/* 1719 */         if ((doDup) && (!isPost)) {
/* 1720 */           this.bytecode.addOpcode(92);
/*      */         }
/* 1722 */         this.bytecode.addLstore(var);
/*      */       }
/* 1724 */       else if (t == 317) {
/* 1725 */         this.bytecode.addFload(var);
/* 1726 */         if ((doDup) && (isPost)) {
/* 1727 */           this.bytecode.addOpcode(89);
/*      */         }
/* 1729 */         this.bytecode.addFconst(1.0F);
/* 1730 */         this.bytecode.addOpcode(token == 362 ? 98 : 102);
/* 1731 */         if ((doDup) && (!isPost)) {
/* 1732 */           this.bytecode.addOpcode(89);
/*      */         }
/* 1734 */         this.bytecode.addFstore(var);
/*      */       }
/* 1736 */       else if ((t == 303) || (t == 306) || (t == 334) || (t == 324)) {
/* 1737 */         if ((doDup) && (isPost)) {
/* 1738 */           this.bytecode.addIload(var);
/*      */         }
/* 1740 */         int delta = token == 362 ? 1 : -1;
/* 1741 */         if (var > 255) {
/* 1742 */           this.bytecode.addOpcode(196);
/* 1743 */           this.bytecode.addOpcode(132);
/* 1744 */           this.bytecode.addIndex(var);
/* 1745 */           this.bytecode.addIndex(delta);
/*      */         }
/*      */         else {
/* 1748 */           this.bytecode.addOpcode(132);
/* 1749 */           this.bytecode.add(var);
/* 1750 */           this.bytecode.add(delta);
/*      */         }
/*      */ 
/* 1753 */         if ((doDup) && (!isPost))
/* 1754 */           this.bytecode.addIload(var);
/*      */       }
/*      */       else {
/* 1757 */         badType(expr);
/*      */       }
/*      */     } else {
/* 1760 */       if ((oprand instanceof Expr)) {
/* 1761 */         Expr e = (Expr)oprand;
/* 1762 */         if (e.getOperator() == 65) {
/* 1763 */           atArrayPlusPlus(token, isPost, e, doDup);
/* 1764 */           return;
/*      */         }
/*      */       }
/*      */ 
/* 1768 */       atFieldPlusPlus(token, isPost, oprand, expr, doDup);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void atArrayPlusPlus(int token, boolean isPost, Expr expr, boolean doDup)
/*      */     throws CompileError
/*      */   {
/* 1775 */     arrayAccess(expr.oprand1(), expr.oprand2());
/* 1776 */     int t = this.exprType;
/* 1777 */     int dim = this.arrayDim;
/* 1778 */     if (dim > 0) {
/* 1779 */       badType(expr);
/*      */     }
/* 1781 */     this.bytecode.addOpcode(92);
/* 1782 */     this.bytecode.addOpcode(getArrayReadOp(t, this.arrayDim));
/* 1783 */     int dup_code = is2word(t, dim) ? 94 : 91;
/* 1784 */     atPlusPlusCore(dup_code, doDup, token, isPost, expr);
/* 1785 */     this.bytecode.addOpcode(getArrayWriteOp(t, dim));
/*      */   }
/*      */ 
/*      */   protected void atPlusPlusCore(int dup_code, boolean doDup, int token, boolean isPost, Expr expr)
/*      */     throws CompileError
/*      */   {
/* 1792 */     int t = this.exprType;
/*      */ 
/* 1794 */     if ((doDup) && (isPost)) {
/* 1795 */       this.bytecode.addOpcode(dup_code);
/*      */     }
/* 1797 */     if ((t == 324) || (t == 303) || (t == 306) || (t == 334)) {
/* 1798 */       this.bytecode.addIconst(1);
/* 1799 */       this.bytecode.addOpcode(token == 362 ? 96 : 100);
/* 1800 */       this.exprType = 324;
/*      */     }
/* 1802 */     else if (t == 326) {
/* 1803 */       this.bytecode.addLconst(1L);
/* 1804 */       this.bytecode.addOpcode(token == 362 ? 97 : 101);
/*      */     }
/* 1806 */     else if (t == 317) {
/* 1807 */       this.bytecode.addFconst(1.0F);
/* 1808 */       this.bytecode.addOpcode(token == 362 ? 98 : 102);
/*      */     }
/* 1810 */     else if (t == 312) {
/* 1811 */       this.bytecode.addDconst(1.0D);
/* 1812 */       this.bytecode.addOpcode(token == 362 ? 99 : 103);
/*      */     }
/*      */     else {
/* 1815 */       badType(expr);
/*      */     }
/* 1817 */     if ((doDup) && (!isPost))
/* 1818 */       this.bytecode.addOpcode(dup_code);
/*      */   }
/*      */ 
/*      */   protected abstract void atFieldPlusPlus(int paramInt, boolean paramBoolean1, ASTree paramASTree, Expr paramExpr, boolean paramBoolean2) throws CompileError;
/*      */ 
/*      */   public abstract void atMember(Member paramMember) throws CompileError;
/*      */ 
/*      */   public void atVariable(Variable v) throws CompileError
/*      */   {
/* 1827 */     Declarator d = v.getDeclarator();
/* 1828 */     this.exprType = d.getType();
/* 1829 */     this.arrayDim = d.getArrayDim();
/* 1830 */     this.className = d.getClassName();
/* 1831 */     int var = getLocalVar(d);
/*      */ 
/* 1833 */     if (this.arrayDim > 0)
/* 1834 */       this.bytecode.addAload(var);
/*      */     else
/* 1836 */       switch (this.exprType) {
/*      */       case 307:
/* 1838 */         this.bytecode.addAload(var);
/* 1839 */         break;
/*      */       case 326:
/* 1841 */         this.bytecode.addLload(var);
/* 1842 */         break;
/*      */       case 317:
/* 1844 */         this.bytecode.addFload(var);
/* 1845 */         break;
/*      */       case 312:
/* 1847 */         this.bytecode.addDload(var);
/* 1848 */         break;
/*      */       default:
/* 1850 */         this.bytecode.addIload(var);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void atKeyword(Keyword k) throws CompileError
/*      */   {
/* 1856 */     this.arrayDim = 0;
/* 1857 */     int token = k.get();
/* 1858 */     switch (token) {
/*      */     case 410:
/* 1860 */       this.bytecode.addIconst(1);
/* 1861 */       this.exprType = 301;
/* 1862 */       break;
/*      */     case 411:
/* 1864 */       this.bytecode.addIconst(0);
/* 1865 */       this.exprType = 301;
/* 1866 */       break;
/*      */     case 412:
/* 1868 */       this.bytecode.addOpcode(1);
/* 1869 */       this.exprType = 412;
/* 1870 */       break;
/*      */     case 336:
/*      */     case 339:
/* 1873 */       if (this.inStaticMethod) {
/* 1874 */         throw new CompileError("not-available: " + (token == 339 ? "this" : "super"));
/*      */       }
/*      */ 
/* 1877 */       this.bytecode.addAload(0);
/* 1878 */       this.exprType = 307;
/* 1879 */       if (token == 339)
/* 1880 */         this.className = getThisName();
/*      */       else
/* 1882 */         this.className = getSuperName();
/* 1883 */       break;
/*      */     default:
/* 1885 */       fatal();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void atStringL(StringL s) throws CompileError {
/* 1890 */     this.exprType = 307;
/* 1891 */     this.arrayDim = 0;
/* 1892 */     this.className = "java/lang/String";
/* 1893 */     this.bytecode.addLdc(s.get());
/*      */   }
/*      */ 
/*      */   public void atIntConst(IntConst i) throws CompileError {
/* 1897 */     this.arrayDim = 0;
/* 1898 */     long value = i.get();
/* 1899 */     int type = i.getType();
/* 1900 */     if ((type == 402) || (type == 401)) {
/* 1901 */       this.exprType = (type == 402 ? 324 : 306);
/* 1902 */       this.bytecode.addIconst((int)value);
/*      */     }
/*      */     else {
/* 1905 */       this.exprType = 326;
/* 1906 */       this.bytecode.addLconst(value);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void atDoubleConst(DoubleConst d) throws CompileError {
/* 1911 */     this.arrayDim = 0;
/* 1912 */     if (d.getType() == 405) {
/* 1913 */       this.exprType = 312;
/* 1914 */       this.bytecode.addDconst(d.get());
/*      */     }
/*      */     else {
/* 1917 */       this.exprType = 317;
/* 1918 */       this.bytecode.addFconst((float)d.get());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static abstract class ReturnHook
/*      */   {
/*      */     ReturnHook next;
/*      */ 
/*      */     protected abstract boolean doit(Bytecode paramBytecode, int paramInt);
/*      */ 
/*      */     protected ReturnHook(CodeGen gen)
/*      */     {
/*   64 */       this.next = gen.returnHooks;
/*   65 */       gen.returnHooks = this;
/*      */     }
/*      */ 
/*      */     protected void remove(CodeGen gen) {
/*   69 */       gen.returnHooks = this.next;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.CodeGen
 * JD-Core Version:    0.6.2
 */