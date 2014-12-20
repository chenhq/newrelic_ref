/*      */ package com.newrelic.javassist.compiler;
/*      */ 
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
/*      */ 
/*      */ public final class Parser
/*      */   implements TokenId
/*      */ {
/*      */   private Lex lex;
/*  820 */   private static final int[] binaryOpPrecedence = { 0, 0, 0, 0, 1, 6, 0, 0, 0, 1, 2, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 4, 0 };
/*      */ 
/*      */   public Parser(Lex lex)
/*      */   {
/*   24 */     this.lex = lex;
/*      */   }
/*      */   public boolean hasMore() {
/*   27 */     return this.lex.lookAhead() >= 0;
/*      */   }
/*      */ 
/*      */   public ASTList parseMember(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*   33 */     ASTList mem = parseMember1(tbl);
/*   34 */     if ((mem instanceof MethodDecl)) {
/*   35 */       return parseMethod2(tbl, (MethodDecl)mem);
/*      */     }
/*   37 */     return mem;
/*      */   }
/*      */ 
/*      */   public ASTList parseMember1(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*   43 */     ASTList mods = parseMemberMods();
/*      */ 
/*   45 */     boolean isConstructor = false;
/*      */     Declarator d;
/*   46 */     if ((this.lex.lookAhead() == 400) && (this.lex.lookAhead(1) == 40)) {
/*   47 */       Declarator d = new Declarator(344, 0);
/*   48 */       isConstructor = true;
/*      */     }
/*      */     else {
/*   51 */       d = parseFormalType(tbl);
/*      */     }
/*   53 */     if (this.lex.get() != 400)
/*   54 */       throw new SyntaxError(this.lex);
/*      */     String name;
/*      */     String name;
/*   57 */     if (isConstructor)
/*   58 */       name = "<init>";
/*      */     else {
/*   60 */       name = this.lex.getString();
/*      */     }
/*   62 */     d.setVariable(new Symbol(name));
/*   63 */     if ((isConstructor) || (this.lex.lookAhead() == 40)) {
/*   64 */       return parseMethod1(tbl, isConstructor, mods, d);
/*      */     }
/*   66 */     return parseField(tbl, mods, d);
/*      */   }
/*      */ 
/*      */   private FieldDecl parseField(SymbolTable tbl, ASTList mods, Declarator d)
/*      */     throws CompileError
/*      */   {
/*   77 */     ASTree expr = null;
/*   78 */     if (this.lex.lookAhead() == 61) {
/*   79 */       this.lex.get();
/*   80 */       expr = parseExpression(tbl);
/*      */     }
/*      */ 
/*   83 */     int c = this.lex.get();
/*   84 */     if (c == 59)
/*   85 */       return new FieldDecl(mods, new ASTList(d, new ASTList(expr)));
/*   86 */     if (c == 44) {
/*   87 */       throw new CompileError("only one field can be declared in one declaration", this.lex);
/*      */     }
/*      */ 
/*   90 */     throw new SyntaxError(this.lex);
/*      */   }
/*      */ 
/*      */   private MethodDecl parseMethod1(SymbolTable tbl, boolean isConstructor, ASTList mods, Declarator d)
/*      */     throws CompileError
/*      */   {
/*  107 */     if (this.lex.get() != 40) {
/*  108 */       throw new SyntaxError(this.lex);
/*      */     }
/*  110 */     ASTList parms = null;
/*  111 */     if (this.lex.lookAhead() != 41) {
/*      */       while (true) {
/*  113 */         parms = ASTList.append(parms, parseFormalParam(tbl));
/*  114 */         int t = this.lex.lookAhead();
/*  115 */         if (t == 44)
/*  116 */           this.lex.get();
/*  117 */         else if (t == 41)
/*      */             break;
/*      */       }
/*      */     }
/*  121 */     this.lex.get();
/*  122 */     d.addArrayDim(parseArrayDimension());
/*  123 */     if ((isConstructor) && (d.getArrayDim() > 0)) {
/*  124 */       throw new SyntaxError(this.lex);
/*      */     }
/*  126 */     ASTList throwsList = null;
/*  127 */     if (this.lex.lookAhead() == 341) {
/*  128 */       this.lex.get();
/*      */       while (true) {
/*  130 */         throwsList = ASTList.append(throwsList, parseClassType(tbl));
/*  131 */         if (this.lex.lookAhead() != 44) break;
/*  132 */         this.lex.get();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  138 */     return new MethodDecl(mods, new ASTList(d, ASTList.make(parms, throwsList, null)));
/*      */   }
/*      */ 
/*      */   public MethodDecl parseMethod2(SymbolTable tbl, MethodDecl md)
/*      */     throws CompileError
/*      */   {
/*  147 */     Stmnt body = null;
/*  148 */     if (this.lex.lookAhead() == 59) {
/*  149 */       this.lex.get();
/*      */     } else {
/*  151 */       body = parseBlock(tbl);
/*  152 */       if (body == null) {
/*  153 */         body = new Stmnt(66);
/*      */       }
/*      */     }
/*  156 */     md.sublist(4).setHead(body);
/*  157 */     return md;
/*      */   }
/*      */ 
/*      */   private ASTList parseMemberMods()
/*      */   {
/*  167 */     ASTList list = null;
/*      */     while (true) {
/*  169 */       int t = this.lex.lookAhead();
/*  170 */       if ((t != 300) && (t != 315) && (t != 332) && (t != 331) && (t != 330) && (t != 338) && (t != 335) && (t != 345) && (t != 342) && (t != 347)) {
/*      */         break;
/*      */       }
/*  173 */       list = new ASTList(new Keyword(this.lex.get()), list);
/*      */     }
/*      */ 
/*  178 */     return list;
/*      */   }
/*      */ 
/*      */   private Declarator parseFormalType(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  184 */     int t = this.lex.lookAhead();
/*  185 */     if ((isBuiltinType(t)) || (t == 344)) {
/*  186 */       this.lex.get();
/*  187 */       int dim = parseArrayDimension();
/*  188 */       return new Declarator(t, dim);
/*      */     }
/*      */ 
/*  191 */     ASTList name = parseClassType(tbl);
/*  192 */     int dim = parseArrayDimension();
/*  193 */     return new Declarator(name, dim);
/*      */   }
/*      */ 
/*      */   private static boolean isBuiltinType(int t)
/*      */   {
/*  198 */     return (t == 301) || (t == 303) || (t == 306) || (t == 334) || (t == 324) || (t == 326) || (t == 317) || (t == 312);
/*      */   }
/*      */ 
/*      */   private Declarator parseFormalParam(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  207 */     Declarator d = parseFormalType(tbl);
/*  208 */     if (this.lex.get() != 400) {
/*  209 */       throw new SyntaxError(this.lex);
/*      */     }
/*  211 */     String name = this.lex.getString();
/*  212 */     d.setVariable(new Symbol(name));
/*  213 */     d.addArrayDim(parseArrayDimension());
/*  214 */     tbl.append(name, d);
/*  215 */     return d;
/*      */   }
/*      */ 
/*      */   public Stmnt parseStatement(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  240 */     int t = this.lex.lookAhead();
/*  241 */     if (t == 123)
/*  242 */       return parseBlock(tbl);
/*  243 */     if (t == 59) {
/*  244 */       this.lex.get();
/*  245 */       return new Stmnt(66);
/*      */     }
/*  247 */     if ((t == 400) && (this.lex.lookAhead(1) == 58)) {
/*  248 */       this.lex.get();
/*  249 */       String label = this.lex.getString();
/*  250 */       this.lex.get();
/*  251 */       return Stmnt.make(76, new Symbol(label), parseStatement(tbl));
/*      */     }
/*  253 */     if (t == 320)
/*  254 */       return parseIf(tbl);
/*  255 */     if (t == 346)
/*  256 */       return parseWhile(tbl);
/*  257 */     if (t == 311)
/*  258 */       return parseDo(tbl);
/*  259 */     if (t == 318)
/*  260 */       return parseFor(tbl);
/*  261 */     if (t == 343)
/*  262 */       return parseTry(tbl);
/*  263 */     if (t == 337)
/*  264 */       return parseSwitch(tbl);
/*  265 */     if (t == 338)
/*  266 */       return parseSynchronized(tbl);
/*  267 */     if (t == 333)
/*  268 */       return parseReturn(tbl);
/*  269 */     if (t == 340)
/*  270 */       return parseThrow(tbl);
/*  271 */     if (t == 302)
/*  272 */       return parseBreak(tbl);
/*  273 */     if (t == 309) {
/*  274 */       return parseContinue(tbl);
/*      */     }
/*  276 */     return parseDeclarationOrExpression(tbl, false);
/*      */   }
/*      */ 
/*      */   private Stmnt parseBlock(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  282 */     if (this.lex.get() != 123) {
/*  283 */       throw new SyntaxError(this.lex);
/*      */     }
/*  285 */     Stmnt body = null;
/*  286 */     SymbolTable tbl2 = new SymbolTable(tbl);
/*  287 */     while (this.lex.lookAhead() != 125) {
/*  288 */       Stmnt s = parseStatement(tbl2);
/*  289 */       if (s != null) {
/*  290 */         body = (Stmnt)ASTList.concat(body, new Stmnt(66, s));
/*      */       }
/*      */     }
/*  293 */     this.lex.get();
/*  294 */     if (body == null) {
/*  295 */       return new Stmnt(66);
/*      */     }
/*  297 */     return body;
/*      */   }
/*      */ 
/*      */   private Stmnt parseIf(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  304 */     int t = this.lex.get();
/*  305 */     ASTree expr = parseParExpression(tbl);
/*  306 */     Stmnt thenp = parseStatement(tbl);
/*      */     Stmnt elsep;
/*      */     Stmnt elsep;
/*  308 */     if (this.lex.lookAhead() == 313) {
/*  309 */       this.lex.get();
/*  310 */       elsep = parseStatement(tbl);
/*      */     }
/*      */     else {
/*  313 */       elsep = null;
/*      */     }
/*  315 */     return new Stmnt(t, expr, new ASTList(thenp, new ASTList(elsep)));
/*      */   }
/*      */ 
/*      */   private Stmnt parseWhile(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  323 */     int t = this.lex.get();
/*  324 */     ASTree expr = parseParExpression(tbl);
/*  325 */     Stmnt body = parseStatement(tbl);
/*  326 */     return new Stmnt(t, expr, body);
/*      */   }
/*      */ 
/*      */   private Stmnt parseDo(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  332 */     int t = this.lex.get();
/*  333 */     Stmnt body = parseStatement(tbl);
/*  334 */     if ((this.lex.get() != 346) || (this.lex.get() != 40)) {
/*  335 */       throw new SyntaxError(this.lex);
/*      */     }
/*  337 */     ASTree expr = parseExpression(tbl);
/*  338 */     if ((this.lex.get() != 41) || (this.lex.get() != 59)) {
/*  339 */       throw new SyntaxError(this.lex);
/*      */     }
/*  341 */     return new Stmnt(t, expr, body);
/*      */   }
/*      */ 
/*      */   private Stmnt parseFor(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  350 */     int t = this.lex.get();
/*      */ 
/*  352 */     SymbolTable tbl2 = new SymbolTable(tbl);
/*      */ 
/*  354 */     if (this.lex.get() != 40)
/*  355 */       throw new SyntaxError(this.lex);
/*      */     Stmnt expr1;
/*      */     Stmnt expr1;
/*  357 */     if (this.lex.lookAhead() == 59) {
/*  358 */       this.lex.get();
/*  359 */       expr1 = null;
/*      */     }
/*      */     else {
/*  362 */       expr1 = parseDeclarationOrExpression(tbl2, true);
/*      */     }
/*      */     ASTree expr2;
/*      */     ASTree expr2;
/*  364 */     if (this.lex.lookAhead() == 59)
/*  365 */       expr2 = null;
/*      */     else {
/*  367 */       expr2 = parseExpression(tbl2);
/*      */     }
/*  369 */     if (this.lex.get() != 59)
/*  370 */       throw new CompileError("; is missing", this.lex);
/*      */     Stmnt expr3;
/*      */     Stmnt expr3;
/*  372 */     if (this.lex.lookAhead() == 41)
/*  373 */       expr3 = null;
/*      */     else {
/*  375 */       expr3 = parseExprList(tbl2);
/*      */     }
/*  377 */     if (this.lex.get() != 41) {
/*  378 */       throw new CompileError(") is missing", this.lex);
/*      */     }
/*  380 */     Stmnt body = parseStatement(tbl2);
/*  381 */     return new Stmnt(t, expr1, new ASTList(expr2, new ASTList(expr3, body)));
/*      */   }
/*      */ 
/*      */   private Stmnt parseSwitch(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  393 */     int t = this.lex.get();
/*  394 */     ASTree expr = parseParExpression(tbl);
/*  395 */     Stmnt body = parseSwitchBlock(tbl);
/*  396 */     return new Stmnt(t, expr, body);
/*      */   }
/*      */ 
/*      */   private Stmnt parseSwitchBlock(SymbolTable tbl) throws CompileError {
/*  400 */     if (this.lex.get() != 123) {
/*  401 */       throw new SyntaxError(this.lex);
/*      */     }
/*  403 */     SymbolTable tbl2 = new SymbolTable(tbl);
/*  404 */     Stmnt s = parseStmntOrCase(tbl2);
/*  405 */     if (s == null) {
/*  406 */       throw new CompileError("empty switch block", this.lex);
/*      */     }
/*  408 */     int op = s.getOperator();
/*  409 */     if ((op != 304) && (op != 310)) {
/*  410 */       throw new CompileError("no case or default in a switch block", this.lex);
/*      */     }
/*      */ 
/*  413 */     Stmnt body = new Stmnt(66, s);
/*  414 */     while (this.lex.lookAhead() != 125) {
/*  415 */       Stmnt s2 = parseStmntOrCase(tbl2);
/*  416 */       if (s2 != null) {
/*  417 */         int op2 = s2.getOperator();
/*  418 */         if ((op2 == 304) || (op2 == 310)) {
/*  419 */           body = (Stmnt)ASTList.concat(body, new Stmnt(66, s2));
/*  420 */           s = s2;
/*      */         }
/*      */         else {
/*  423 */           s = (Stmnt)ASTList.concat(s, new Stmnt(66, s2));
/*      */         }
/*      */       }
/*      */     }
/*  427 */     this.lex.get();
/*  428 */     return body;
/*      */   }
/*      */ 
/*      */   private Stmnt parseStmntOrCase(SymbolTable tbl) throws CompileError {
/*  432 */     int t = this.lex.lookAhead();
/*  433 */     if ((t != 304) && (t != 310)) {
/*  434 */       return parseStatement(tbl);
/*      */     }
/*  436 */     this.lex.get();
/*      */     Stmnt s;
/*      */     Stmnt s;
/*  438 */     if (t == 304)
/*  439 */       s = new Stmnt(t, parseExpression(tbl));
/*      */     else {
/*  441 */       s = new Stmnt(310);
/*      */     }
/*  443 */     if (this.lex.get() != 58) {
/*  444 */       throw new CompileError(": is missing", this.lex);
/*      */     }
/*  446 */     return s;
/*      */   }
/*      */ 
/*      */   private Stmnt parseSynchronized(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  453 */     int t = this.lex.get();
/*  454 */     if (this.lex.get() != 40) {
/*  455 */       throw new SyntaxError(this.lex);
/*      */     }
/*  457 */     ASTree expr = parseExpression(tbl);
/*  458 */     if (this.lex.get() != 41) {
/*  459 */       throw new SyntaxError(this.lex);
/*      */     }
/*  461 */     Stmnt body = parseBlock(tbl);
/*  462 */     return new Stmnt(t, expr, body);
/*      */   }
/*      */ 
/*      */   private Stmnt parseTry(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  471 */     this.lex.get();
/*  472 */     Stmnt block = parseBlock(tbl);
/*  473 */     ASTList catchList = null;
/*  474 */     while (this.lex.lookAhead() == 305) {
/*  475 */       this.lex.get();
/*  476 */       if (this.lex.get() != 40) {
/*  477 */         throw new SyntaxError(this.lex);
/*      */       }
/*  479 */       SymbolTable tbl2 = new SymbolTable(tbl);
/*  480 */       Declarator d = parseFormalParam(tbl2);
/*  481 */       if ((d.getArrayDim() > 0) || (d.getType() != 307)) {
/*  482 */         throw new SyntaxError(this.lex);
/*      */       }
/*  484 */       if (this.lex.get() != 41) {
/*  485 */         throw new SyntaxError(this.lex);
/*      */       }
/*  487 */       Stmnt b = parseBlock(tbl2);
/*  488 */       catchList = ASTList.append(catchList, new Pair(d, b));
/*      */     }
/*      */ 
/*  491 */     Stmnt finallyBlock = null;
/*  492 */     if (this.lex.lookAhead() == 316) {
/*  493 */       this.lex.get();
/*  494 */       finallyBlock = parseBlock(tbl);
/*      */     }
/*      */ 
/*  497 */     return Stmnt.make(343, block, catchList, finallyBlock);
/*      */   }
/*      */ 
/*      */   private Stmnt parseReturn(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  503 */     int t = this.lex.get();
/*  504 */     Stmnt s = new Stmnt(t);
/*  505 */     if (this.lex.lookAhead() != 59) {
/*  506 */       s.setLeft(parseExpression(tbl));
/*      */     }
/*  508 */     if (this.lex.get() != 59) {
/*  509 */       throw new CompileError("; is missing", this.lex);
/*      */     }
/*  511 */     return s;
/*      */   }
/*      */ 
/*      */   private Stmnt parseThrow(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  517 */     int t = this.lex.get();
/*  518 */     ASTree expr = parseExpression(tbl);
/*  519 */     if (this.lex.get() != 59) {
/*  520 */       throw new CompileError("; is missing", this.lex);
/*      */     }
/*  522 */     return new Stmnt(t, expr);
/*      */   }
/*      */ 
/*      */   private Stmnt parseBreak(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  530 */     return parseContinue(tbl);
/*      */   }
/*      */ 
/*      */   private Stmnt parseContinue(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  538 */     int t = this.lex.get();
/*  539 */     Stmnt s = new Stmnt(t);
/*  540 */     int t2 = this.lex.get();
/*  541 */     if (t2 == 400) {
/*  542 */       s.setLeft(new Symbol(this.lex.getString()));
/*  543 */       t2 = this.lex.get();
/*      */     }
/*      */ 
/*  546 */     if (t2 != 59) {
/*  547 */       throw new CompileError("; is missing", this.lex);
/*      */     }
/*  549 */     return s;
/*      */   }
/*      */ 
/*      */   private Stmnt parseDeclarationOrExpression(SymbolTable tbl, boolean exprList)
/*      */     throws CompileError
/*      */   {
/*  565 */     int t = this.lex.lookAhead();
/*  566 */     while (t == 315) {
/*  567 */       this.lex.get();
/*  568 */       t = this.lex.lookAhead();
/*      */     }
/*      */ 
/*  571 */     if (isBuiltinType(t)) {
/*  572 */       t = this.lex.get();
/*  573 */       int dim = parseArrayDimension();
/*  574 */       return parseDeclarators(tbl, new Declarator(t, dim));
/*      */     }
/*  576 */     if (t == 400) {
/*  577 */       int i = nextIsClassType(0);
/*  578 */       if ((i >= 0) && 
/*  579 */         (this.lex.lookAhead(i) == 400)) {
/*  580 */         ASTList name = parseClassType(tbl);
/*  581 */         int dim = parseArrayDimension();
/*  582 */         return parseDeclarators(tbl, new Declarator(name, dim));
/*      */       }
/*      */     }
/*      */     Stmnt expr;
/*      */     Stmnt expr;
/*  587 */     if (exprList)
/*  588 */       expr = parseExprList(tbl);
/*      */     else {
/*  590 */       expr = new Stmnt(69, parseExpression(tbl));
/*      */     }
/*  592 */     if (this.lex.get() != 59) {
/*  593 */       throw new CompileError("; is missing", this.lex);
/*      */     }
/*  595 */     return expr;
/*      */   }
/*      */ 
/*      */   private Stmnt parseExprList(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  601 */     Stmnt expr = null;
/*      */     while (true) {
/*  603 */       Stmnt e = new Stmnt(69, parseExpression(tbl));
/*  604 */       expr = (Stmnt)ASTList.concat(expr, new Stmnt(66, e));
/*  605 */       if (this.lex.lookAhead() == 44)
/*  606 */         this.lex.get();
/*      */       else
/*  608 */         return expr;
/*      */     }
/*      */   }
/*      */ 
/*      */   private Stmnt parseDeclarators(SymbolTable tbl, Declarator d)
/*      */     throws CompileError
/*      */   {
/*  617 */     Stmnt decl = null;
/*      */     while (true) {
/*  619 */       decl = (Stmnt)ASTList.concat(decl, new Stmnt(68, parseDeclarator(tbl, d)));
/*      */ 
/*  621 */       int t = this.lex.get();
/*  622 */       if (t == 59)
/*  623 */         return decl;
/*  624 */       if (t != 44)
/*  625 */         throw new CompileError("; is missing", this.lex);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Declarator parseDeclarator(SymbolTable tbl, Declarator d)
/*      */     throws CompileError
/*      */   {
/*  634 */     if ((this.lex.get() != 400) || (d.getType() == 344)) {
/*  635 */       throw new SyntaxError(this.lex);
/*      */     }
/*  637 */     String name = this.lex.getString();
/*  638 */     Symbol symbol = new Symbol(name);
/*  639 */     int dim = parseArrayDimension();
/*  640 */     ASTree init = null;
/*  641 */     if (this.lex.lookAhead() == 61) {
/*  642 */       this.lex.get();
/*  643 */       init = parseInitializer(tbl);
/*      */     }
/*      */ 
/*  646 */     Declarator decl = d.make(symbol, dim, init);
/*  647 */     tbl.append(name, decl);
/*  648 */     return decl;
/*      */   }
/*      */ 
/*      */   private ASTree parseInitializer(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  654 */     if (this.lex.lookAhead() == 123) {
/*  655 */       return parseArrayInitializer(tbl);
/*      */     }
/*  657 */     return parseExpression(tbl);
/*      */   }
/*      */ 
/*      */   private ArrayInit parseArrayInitializer(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  666 */     this.lex.get();
/*  667 */     ASTree expr = parseExpression(tbl);
/*  668 */     ArrayInit init = new ArrayInit(expr);
/*  669 */     while (this.lex.lookAhead() == 44) {
/*  670 */       this.lex.get();
/*  671 */       expr = parseExpression(tbl);
/*  672 */       ASTList.append(init, expr);
/*      */     }
/*      */ 
/*  675 */     if (this.lex.get() != 125) {
/*  676 */       throw new SyntaxError(this.lex);
/*      */     }
/*  678 */     return init;
/*      */   }
/*      */ 
/*      */   private ASTree parseParExpression(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  684 */     if (this.lex.get() != 40) {
/*  685 */       throw new SyntaxError(this.lex);
/*      */     }
/*  687 */     ASTree expr = parseExpression(tbl);
/*  688 */     if (this.lex.get() != 41) {
/*  689 */       throw new SyntaxError(this.lex);
/*      */     }
/*  691 */     return expr;
/*      */   }
/*      */ 
/*      */   public ASTree parseExpression(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  698 */     ASTree left = parseConditionalExpr(tbl);
/*  699 */     if (!isAssignOp(this.lex.lookAhead())) {
/*  700 */       return left;
/*      */     }
/*  702 */     int t = this.lex.get();
/*  703 */     ASTree right = parseExpression(tbl);
/*  704 */     return AssignExpr.makeAssign(t, left, right);
/*      */   }
/*      */ 
/*      */   private static boolean isAssignOp(int t) {
/*  708 */     return (t == 61) || (t == 351) || (t == 352) || (t == 353) || (t == 354) || (t == 355) || (t == 356) || (t == 360) || (t == 361) || (t == 365) || (t == 367) || (t == 371);
/*      */   }
/*      */ 
/*      */   private ASTree parseConditionalExpr(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  718 */     ASTree cond = parseBinaryExpr(tbl);
/*  719 */     if (this.lex.lookAhead() == 63) {
/*  720 */       this.lex.get();
/*  721 */       ASTree thenExpr = parseExpression(tbl);
/*  722 */       if (this.lex.get() != 58) {
/*  723 */         throw new CompileError(": is missing", this.lex);
/*      */       }
/*  725 */       ASTree elseExpr = parseExpression(tbl);
/*  726 */       return new CondExpr(cond, thenExpr, elseExpr);
/*      */     }
/*      */ 
/*  729 */     return cond;
/*      */   }
/*      */ 
/*      */   private ASTree parseBinaryExpr(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  774 */     ASTree expr = parseUnaryExpr(tbl);
/*      */     while (true) {
/*  776 */       int t = this.lex.lookAhead();
/*  777 */       int p = getOpPrecedence(t);
/*  778 */       if (p == 0) {
/*  779 */         return expr;
/*      */       }
/*  781 */       expr = binaryExpr2(tbl, expr, p);
/*      */     }
/*      */   }
/*      */ 
/*      */   private ASTree parseInstanceOf(SymbolTable tbl, ASTree expr)
/*      */     throws CompileError
/*      */   {
/*  788 */     int t = this.lex.lookAhead();
/*  789 */     if (isBuiltinType(t)) {
/*  790 */       this.lex.get();
/*  791 */       int dim = parseArrayDimension();
/*  792 */       return new InstanceOfExpr(t, dim, expr);
/*      */     }
/*      */ 
/*  795 */     ASTList name = parseClassType(tbl);
/*  796 */     int dim = parseArrayDimension();
/*  797 */     return new InstanceOfExpr(name, dim, expr);
/*      */   }
/*      */ 
/*      */   private ASTree binaryExpr2(SymbolTable tbl, ASTree expr, int prec)
/*      */     throws CompileError
/*      */   {
/*  804 */     int t = this.lex.get();
/*  805 */     if (t == 323) {
/*  806 */       return parseInstanceOf(tbl, expr);
/*      */     }
/*  808 */     ASTree expr2 = parseUnaryExpr(tbl);
/*      */     while (true) {
/*  810 */       int t2 = this.lex.lookAhead();
/*  811 */       int p2 = getOpPrecedence(t2);
/*  812 */       if ((p2 != 0) && (prec > p2))
/*  813 */         expr2 = binaryExpr2(tbl, expr2, p2);
/*      */       else
/*  815 */         return BinExpr.makeBin(t, expr, expr2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private int getOpPrecedence(int c)
/*      */   {
/*  827 */     if ((33 <= c) && (c <= 63))
/*  828 */       return binaryOpPrecedence[(c - 33)];
/*  829 */     if (c == 94)
/*  830 */       return 7;
/*  831 */     if (c == 124)
/*  832 */       return 8;
/*  833 */     if (c == 369)
/*  834 */       return 9;
/*  835 */     if (c == 368)
/*  836 */       return 10;
/*  837 */     if ((c == 358) || (c == 350))
/*  838 */       return 5;
/*  839 */     if ((c == 357) || (c == 359) || (c == 323))
/*  840 */       return 4;
/*  841 */     if ((c == 364) || (c == 366) || (c == 370)) {
/*  842 */       return 3;
/*      */     }
/*  844 */     return 0;
/*      */   }
/*      */ 
/*      */   private ASTree parseUnaryExpr(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  858 */     switch (this.lex.lookAhead()) {
/*      */     case 33:
/*      */     case 43:
/*      */     case 45:
/*      */     case 126:
/*      */     case 362:
/*      */     case 363:
/*  865 */       int t = this.lex.get();
/*  866 */       if (t == 45) {
/*  867 */         int t2 = this.lex.lookAhead();
/*  868 */         switch (t2) {
/*      */         case 401:
/*      */         case 402:
/*      */         case 403:
/*  872 */           this.lex.get();
/*  873 */           return new IntConst(-this.lex.getLong(), t2);
/*      */         case 404:
/*      */         case 405:
/*  876 */           this.lex.get();
/*  877 */           return new DoubleConst(-this.lex.getDouble(), t2);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  883 */       return Expr.make(t, parseUnaryExpr(tbl));
/*      */     case 40:
/*  885 */       return parseCast(tbl);
/*      */     }
/*  887 */     return parsePostfix(tbl);
/*      */   }
/*      */ 
/*      */   private ASTree parseCast(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  900 */     int t = this.lex.lookAhead(1);
/*  901 */     if ((isBuiltinType(t)) && (nextIsBuiltinCast())) {
/*  902 */       this.lex.get();
/*  903 */       this.lex.get();
/*  904 */       int dim = parseArrayDimension();
/*  905 */       if (this.lex.get() != 41) {
/*  906 */         throw new CompileError(") is missing", this.lex);
/*      */       }
/*  908 */       return new CastExpr(t, dim, parseUnaryExpr(tbl));
/*      */     }
/*  910 */     if ((t == 400) && (nextIsClassCast())) {
/*  911 */       this.lex.get();
/*  912 */       ASTList name = parseClassType(tbl);
/*  913 */       int dim = parseArrayDimension();
/*  914 */       if (this.lex.get() != 41) {
/*  915 */         throw new CompileError(") is missing", this.lex);
/*      */       }
/*  917 */       return new CastExpr(name, dim, parseUnaryExpr(tbl));
/*      */     }
/*      */ 
/*  920 */     return parsePostfix(tbl);
/*      */   }
/*      */ 
/*      */   private boolean nextIsBuiltinCast()
/*      */   {
/*  925 */     int i = 2;
/*      */     int t;
/*  926 */     while ((t = this.lex.lookAhead(i++)) == 91) {
/*  927 */       if (this.lex.lookAhead(i++) != 93)
/*  928 */         return false;
/*      */     }
/*  930 */     return this.lex.lookAhead(i - 1) == 41;
/*      */   }
/*      */ 
/*      */   private boolean nextIsClassCast() {
/*  934 */     int i = nextIsClassType(1);
/*  935 */     if (i < 0) {
/*  936 */       return false;
/*      */     }
/*  938 */     int t = this.lex.lookAhead(i);
/*  939 */     if (t != 41) {
/*  940 */       return false;
/*      */     }
/*  942 */     t = this.lex.lookAhead(i + 1);
/*  943 */     return (t == 40) || (t == 412) || (t == 406) || (t == 400) || (t == 339) || (t == 336) || (t == 328) || (t == 410) || (t == 411) || (t == 403) || (t == 402) || (t == 401) || (t == 405) || (t == 404);
/*      */   }
/*      */ 
/*      */   private int nextIsClassType(int i)
/*      */   {
/*  952 */     while (this.lex.lookAhead(++i) == 46)
/*  953 */       if (this.lex.lookAhead(++i) != 400)
/*  954 */         return -1;
/*      */     int t;
/*  956 */     while ((t = this.lex.lookAhead(i++)) == 91) {
/*  957 */       if (this.lex.lookAhead(i++) != 93)
/*  958 */         return -1;
/*      */     }
/*  960 */     return i - 1;
/*      */   }
/*      */ 
/*      */   private int parseArrayDimension()
/*      */     throws CompileError
/*      */   {
/*  966 */     int arrayDim = 0;
/*  967 */     while (this.lex.lookAhead() == 91) {
/*  968 */       arrayDim++;
/*  969 */       this.lex.get();
/*  970 */       if (this.lex.get() != 93) {
/*  971 */         throw new CompileError("] is missing", this.lex);
/*      */       }
/*      */     }
/*  974 */     return arrayDim;
/*      */   }
/*      */ 
/*      */   private ASTList parseClassType(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*  980 */     ASTList list = null;
/*      */     while (true) {
/*  982 */       if (this.lex.get() != 400) {
/*  983 */         throw new SyntaxError(this.lex);
/*      */       }
/*  985 */       list = ASTList.append(list, new Symbol(this.lex.getString()));
/*  986 */       if (this.lex.lookAhead() != 46) break;
/*  987 */       this.lex.get();
/*      */     }
/*      */ 
/*  992 */     return list;
/*      */   }
/*      */ 
/*      */   private ASTree parsePostfix(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/* 1012 */     int token = this.lex.lookAhead();
/* 1013 */     switch (token) {
/*      */     case 401:
/*      */     case 402:
/*      */     case 403:
/* 1017 */       this.lex.get();
/* 1018 */       return new IntConst(this.lex.getLong(), token);
/*      */     case 404:
/*      */     case 405:
/* 1021 */       this.lex.get();
/* 1022 */       return new DoubleConst(this.lex.getDouble(), token);
/*      */     }
/*      */ 
/* 1029 */     ASTree expr = parsePrimaryExpr(tbl);
/*      */     while (true)
/*      */     {
/*      */       int t;
/* 1032 */       switch (this.lex.lookAhead()) {
/*      */       case 40:
/* 1034 */         expr = parseMethodCall(tbl, expr);
/* 1035 */         break;
/*      */       case 91:
/* 1037 */         if (this.lex.lookAhead(1) == 93) {
/* 1038 */           int dim = parseArrayDimension();
/* 1039 */           if ((this.lex.get() != 46) || (this.lex.get() != 307)) {
/* 1040 */             throw new SyntaxError(this.lex);
/*      */           }
/* 1042 */           expr = parseDotClass(expr, dim);
/*      */         }
/*      */         else {
/* 1045 */           ASTree index = parseArrayIndex(tbl);
/* 1046 */           if (index == null) {
/* 1047 */             throw new SyntaxError(this.lex);
/*      */           }
/* 1049 */           expr = Expr.make(65, expr, index);
/*      */         }
/* 1051 */         break;
/*      */       case 362:
/*      */       case 363:
/* 1054 */         t = this.lex.get();
/* 1055 */         expr = Expr.make(t, null, expr);
/* 1056 */         break;
/*      */       case 46:
/* 1058 */         this.lex.get();
/* 1059 */         t = this.lex.get();
/* 1060 */         if (t == 307) {
/* 1061 */           expr = parseDotClass(expr, 0);
/*      */         }
/* 1063 */         else if (t == 400) {
/* 1064 */           String str = this.lex.getString();
/* 1065 */           expr = Expr.make(46, expr, new Member(str));
/*      */         }
/*      */         else {
/* 1068 */           throw new CompileError("missing member name", this.lex);
/*      */         }break;
/*      */       case 35:
/* 1071 */         this.lex.get();
/* 1072 */         t = this.lex.get();
/* 1073 */         if (t != 400) {
/* 1074 */           throw new CompileError("missing static member name", this.lex);
/*      */         }
/* 1076 */         String str = this.lex.getString();
/* 1077 */         expr = Expr.make(35, new Symbol(toClassName(expr)), new Member(str));
/*      */       }
/*      */     }
/*      */ 
/* 1081 */     return expr;
/*      */   }
/*      */ 
/*      */   private ASTree parseDotClass(ASTree className, int dim)
/*      */     throws CompileError
/*      */   {
/* 1093 */     String cname = toClassName(className);
/* 1094 */     if (dim > 0) {
/* 1095 */       StringBuffer sbuf = new StringBuffer();
/* 1096 */       while (dim-- > 0) {
/* 1097 */         sbuf.append('[');
/*      */       }
/* 1099 */       sbuf.append('L').append(cname.replace('.', '/')).append(';');
/* 1100 */       cname = sbuf.toString();
/*      */     }
/*      */ 
/* 1103 */     return Expr.make(46, new Symbol(cname), new Member("class"));
/*      */   }
/*      */ 
/*      */   private ASTree parseDotClass(int builtinType, int dim)
/*      */     throws CompileError
/*      */   {
/* 1113 */     if (dim > 0) {
/* 1114 */       String cname = CodeGen.toJvmTypeName(builtinType, dim);
/* 1115 */       return Expr.make(46, new Symbol(cname), new Member("class"));
/*      */     }
/*      */     String cname;
/* 1119 */     switch (builtinType) {
/*      */     case 301:
/* 1121 */       cname = "java.lang.Boolean";
/* 1122 */       break;
/*      */     case 303:
/* 1124 */       cname = "java.lang.Byte";
/* 1125 */       break;
/*      */     case 306:
/* 1127 */       cname = "java.lang.Character";
/* 1128 */       break;
/*      */     case 334:
/* 1130 */       cname = "java.lang.Short";
/* 1131 */       break;
/*      */     case 324:
/* 1133 */       cname = "java.lang.Integer";
/* 1134 */       break;
/*      */     case 326:
/* 1136 */       cname = "java.lang.Long";
/* 1137 */       break;
/*      */     case 317:
/* 1139 */       cname = "java.lang.Float";
/* 1140 */       break;
/*      */     case 312:
/* 1142 */       cname = "java.lang.Double";
/* 1143 */       break;
/*      */     case 344:
/* 1145 */       cname = "java.lang.Void";
/* 1146 */       break;
/*      */     default:
/* 1148 */       throw new CompileError("invalid builtin type: " + builtinType);
/*      */     }
/*      */ 
/* 1152 */     return Expr.make(35, new Symbol(cname), new Member("TYPE"));
/*      */   }
/*      */ 
/*      */   private ASTree parseMethodCall(SymbolTable tbl, ASTree expr)
/*      */     throws CompileError
/*      */   {
/* 1164 */     if ((expr instanceof Keyword)) {
/* 1165 */       int token = ((Keyword)expr).get();
/* 1166 */       if ((token != 339) && (token != 336))
/* 1167 */         throw new SyntaxError(this.lex);
/*      */     }
/* 1169 */     else if (!(expr instanceof Symbol))
/*      */     {
/* 1171 */       if ((expr instanceof Expr)) {
/* 1172 */         int op = ((Expr)expr).getOperator();
/* 1173 */         if ((op != 46) && (op != 35))
/* 1174 */           throw new SyntaxError(this.lex);
/*      */       }
/*      */     }
/* 1177 */     return CallExpr.makeCall(expr, parseArgumentList(tbl));
/*      */   }
/*      */ 
/*      */   private String toClassName(ASTree name)
/*      */     throws CompileError
/*      */   {
/* 1183 */     StringBuffer sbuf = new StringBuffer();
/* 1184 */     toClassName(name, sbuf);
/* 1185 */     return sbuf.toString();
/*      */   }
/*      */ 
/*      */   private void toClassName(ASTree name, StringBuffer sbuf)
/*      */     throws CompileError
/*      */   {
/* 1191 */     if ((name instanceof Symbol)) {
/* 1192 */       sbuf.append(((Symbol)name).get());
/* 1193 */       return;
/*      */     }
/* 1195 */     if ((name instanceof Expr)) {
/* 1196 */       Expr expr = (Expr)name;
/* 1197 */       if (expr.getOperator() == 46) {
/* 1198 */         toClassName(expr.oprand1(), sbuf);
/* 1199 */         sbuf.append('.');
/* 1200 */         toClassName(expr.oprand2(), sbuf);
/* 1201 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1205 */     throw new CompileError("bad static member access", this.lex);
/*      */   }
/*      */ 
/*      */   private ASTree parsePrimaryExpr(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/*      */     int t;
/* 1224 */     switch (t = this.lex.get()) {
/*      */     case 336:
/*      */     case 339:
/*      */     case 410:
/*      */     case 411:
/*      */     case 412:
/* 1230 */       return new Keyword(t);
/*      */     case 400:
/* 1232 */       String name = this.lex.getString();
/* 1233 */       Declarator decl = tbl.lookup(name);
/* 1234 */       if (decl == null) {
/* 1235 */         return new Member(name);
/*      */       }
/* 1237 */       return new Variable(name, decl);
/*      */     case 406:
/* 1239 */       return new StringL(this.lex.getString());
/*      */     case 328:
/* 1241 */       return parseNew(tbl);
/*      */     case 40:
/* 1243 */       ASTree expr = parseExpression(tbl);
/* 1244 */       if (this.lex.get() == 41) {
/* 1245 */         return expr;
/*      */       }
/* 1247 */       throw new CompileError(") is missing", this.lex);
/*      */     }
/* 1249 */     if ((isBuiltinType(t)) || (t == 344)) {
/* 1250 */       int dim = parseArrayDimension();
/* 1251 */       if ((this.lex.get() == 46) && (this.lex.get() == 307)) {
/* 1252 */         return parseDotClass(t, dim);
/*      */       }
/*      */     }
/* 1255 */     throw new SyntaxError(this.lex);
/*      */   }
/*      */ 
/*      */   private NewExpr parseNew(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/* 1264 */     ArrayInit init = null;
/* 1265 */     int t = this.lex.lookAhead();
/* 1266 */     if (isBuiltinType(t)) {
/* 1267 */       this.lex.get();
/* 1268 */       ASTList size = parseArraySize(tbl);
/* 1269 */       if (this.lex.lookAhead() == 123) {
/* 1270 */         init = parseArrayInitializer(tbl);
/*      */       }
/* 1272 */       return new NewExpr(t, size, init);
/*      */     }
/* 1274 */     if (t == 400) {
/* 1275 */       ASTList name = parseClassType(tbl);
/* 1276 */       t = this.lex.lookAhead();
/* 1277 */       if (t == 40) {
/* 1278 */         ASTList args = parseArgumentList(tbl);
/* 1279 */         return new NewExpr(name, args);
/*      */       }
/* 1281 */       if (t == 91) {
/* 1282 */         ASTList size = parseArraySize(tbl);
/* 1283 */         if (this.lex.lookAhead() == 123) {
/* 1284 */           init = parseArrayInitializer(tbl);
/*      */         }
/* 1286 */         return NewExpr.makeObjectArray(name, size, init);
/*      */       }
/*      */     }
/*      */ 
/* 1290 */     throw new SyntaxError(this.lex);
/*      */   }
/*      */ 
/*      */   private ASTList parseArraySize(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/* 1296 */     ASTList list = null;
/* 1297 */     while (this.lex.lookAhead() == 91) {
/* 1298 */       list = ASTList.append(list, parseArrayIndex(tbl));
/*      */     }
/* 1300 */     return list;
/*      */   }
/*      */ 
/*      */   private ASTree parseArrayIndex(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/* 1306 */     this.lex.get();
/* 1307 */     if (this.lex.lookAhead() == 93) {
/* 1308 */       this.lex.get();
/* 1309 */       return null;
/*      */     }
/*      */ 
/* 1312 */     ASTree index = parseExpression(tbl);
/* 1313 */     if (this.lex.get() != 93) {
/* 1314 */       throw new CompileError("] is missing", this.lex);
/*      */     }
/* 1316 */     return index;
/*      */   }
/*      */ 
/*      */   private ASTList parseArgumentList(SymbolTable tbl)
/*      */     throws CompileError
/*      */   {
/* 1323 */     if (this.lex.get() != 40) {
/* 1324 */       throw new CompileError("( is missing", this.lex);
/*      */     }
/* 1326 */     ASTList list = null;
/* 1327 */     if (this.lex.lookAhead() != 41) {
/*      */       while (true) {
/* 1329 */         list = ASTList.append(list, parseExpression(tbl));
/* 1330 */         if (this.lex.lookAhead() != 44) break;
/* 1331 */         this.lex.get();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1336 */     if (this.lex.get() != 41) {
/* 1337 */       throw new CompileError(") is missing", this.lex);
/*      */     }
/* 1339 */     return list;
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.Parser
 * JD-Core Version:    0.6.2
 */