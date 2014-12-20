/*     */ package com.newrelic.javassist.compiler;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.CtBehavior;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtConstructor;
/*     */ import com.newrelic.javassist.CtField;
/*     */ import com.newrelic.javassist.CtMember;
/*     */ import com.newrelic.javassist.CtMethod;
/*     */ import com.newrelic.javassist.CtPrimitiveType;
/*     */ import com.newrelic.javassist.Modifier;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.Bytecode;
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.LocalVariableAttribute;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.compiler.ast.ASTList;
/*     */ import com.newrelic.javassist.compiler.ast.ASTree;
/*     */ import com.newrelic.javassist.compiler.ast.CallExpr;
/*     */ import com.newrelic.javassist.compiler.ast.Declarator;
/*     */ import com.newrelic.javassist.compiler.ast.Expr;
/*     */ import com.newrelic.javassist.compiler.ast.FieldDecl;
/*     */ import com.newrelic.javassist.compiler.ast.Member;
/*     */ import com.newrelic.javassist.compiler.ast.MethodDecl;
/*     */ import com.newrelic.javassist.compiler.ast.Stmnt;
/*     */ import com.newrelic.javassist.compiler.ast.Symbol;
/*     */ 
/*     */ public class Javac
/*     */ {
/*     */   JvstCodeGen gen;
/*     */   SymbolTable stable;
/*     */   private Bytecode bytecode;
/*     */   public static final String param0Name = "$0";
/*     */   public static final String resultVarName = "$_";
/*     */   public static final String proceedName = "$proceed";
/*     */ 
/*     */   public Javac(CtClass thisClass)
/*     */   {
/*  52 */     this(new Bytecode(thisClass.getClassFile2().getConstPool(), 0, 0), thisClass);
/*     */   }
/*     */ 
/*     */   public Javac(Bytecode b, CtClass thisClass)
/*     */   {
/*  65 */     this.gen = new JvstCodeGen(b, thisClass, thisClass.getClassPool());
/*  66 */     this.stable = new SymbolTable();
/*  67 */     this.bytecode = b;
/*     */   }
/*     */ 
/*     */   public Bytecode getBytecode()
/*     */   {
/*  73 */     return this.bytecode;
/*     */   }
/*     */ 
/*     */   public CtMember compile(String src)
/*     */     throws CompileError
/*     */   {
/*  88 */     Parser p = new Parser(new Lex(src));
/*  89 */     ASTList mem = p.parseMember1(this.stable);
/*     */     try {
/*  91 */       if ((mem instanceof FieldDecl)) {
/*  92 */         return compileField((FieldDecl)mem);
/*     */       }
/*  94 */       CtBehavior cb = compileMethod(p, (MethodDecl)mem);
/*  95 */       CtClass decl = cb.getDeclaringClass();
/*  96 */       cb.getMethodInfo2().rebuildStackMapIf6(decl.getClassPool(), decl.getClassFile2());
/*     */ 
/*  99 */       return cb;
/*     */     }
/*     */     catch (BadBytecode bb)
/*     */     {
/* 103 */       throw new CompileError(bb.getMessage());
/*     */     }
/*     */     catch (CannotCompileException e) {
/* 106 */       throw new CompileError(e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private CtField compileField(FieldDecl fd)
/*     */     throws CompileError, CannotCompileException
/*     */   {
/* 131 */     Declarator d = fd.getDeclarator();
/* 132 */     CtFieldWithInit f = new CtFieldWithInit(this.gen.resolver.lookupClass(d), d.getVariable().get(), this.gen.getThisClass());
/*     */ 
/* 134 */     f.setModifiers(MemberResolver.getModifiers(fd.getModifiers()));
/* 135 */     if (fd.getInit() != null) {
/* 136 */       f.setInit(fd.getInit());
/*     */     }
/* 138 */     return f;
/*     */   }
/*     */ 
/*     */   private CtBehavior compileMethod(Parser p, MethodDecl md)
/*     */     throws CompileError
/*     */   {
/* 144 */     int mod = MemberResolver.getModifiers(md.getModifiers());
/* 145 */     CtClass[] plist = this.gen.makeParamList(md);
/* 146 */     CtClass[] tlist = this.gen.makeThrowsList(md);
/* 147 */     recordParams(plist, Modifier.isStatic(mod));
/* 148 */     md = p.parseMethod2(this.stable, md);
/*     */     try {
/* 150 */       if (md.isConstructor()) {
/* 151 */         CtConstructor cons = new CtConstructor(plist, this.gen.getThisClass());
/*     */ 
/* 153 */         cons.setModifiers(mod);
/* 154 */         md.accept(this.gen);
/* 155 */         cons.getMethodInfo().setCodeAttribute(this.bytecode.toCodeAttribute());
/*     */ 
/* 157 */         cons.setExceptionTypes(tlist);
/* 158 */         return cons;
/*     */       }
/*     */ 
/* 161 */       Declarator r = md.getReturn();
/* 162 */       CtClass rtype = this.gen.resolver.lookupClass(r);
/* 163 */       recordReturnType(rtype, false);
/* 164 */       CtMethod method = new CtMethod(rtype, r.getVariable().get(), plist, this.gen.getThisClass());
/*     */ 
/* 166 */       method.setModifiers(mod);
/* 167 */       this.gen.setThisMethod(method);
/* 168 */       md.accept(this.gen);
/* 169 */       if (md.getBody() != null) {
/* 170 */         method.getMethodInfo().setCodeAttribute(this.bytecode.toCodeAttribute());
/*     */       }
/*     */       else {
/* 173 */         method.setModifiers(mod | 0x400);
/*     */       }
/* 175 */       method.setExceptionTypes(tlist);
/* 176 */       return method;
/*     */     }
/*     */     catch (NotFoundException e)
/*     */     {
/* 180 */       throw new CompileError(e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Bytecode compileBody(CtBehavior method, String src)
/*     */     throws CompileError
/*     */   {
/*     */     try
/*     */     {
/* 194 */       int mod = method.getModifiers();
/* 195 */       recordParams(method.getParameterTypes(), Modifier.isStatic(mod));
/*     */       CtClass rtype;
/*     */       CtClass rtype;
/* 198 */       if ((method instanceof CtMethod)) {
/* 199 */         this.gen.setThisMethod((CtMethod)method);
/* 200 */         rtype = ((CtMethod)method).getReturnType();
/*     */       }
/*     */       else {
/* 203 */         rtype = CtClass.voidType;
/*     */       }
/* 205 */       recordReturnType(rtype, false);
/* 206 */       boolean isVoid = rtype == CtClass.voidType;
/*     */ 
/* 208 */       if (src == null) {
/* 209 */         makeDefaultBody(this.bytecode, rtype);
/*     */       } else {
/* 211 */         Parser p = new Parser(new Lex(src));
/* 212 */         SymbolTable stb = new SymbolTable(this.stable);
/* 213 */         Stmnt s = p.parseStatement(stb);
/* 214 */         if (p.hasMore()) {
/* 215 */           throw new CompileError("the method/constructor body must be surrounded by {}");
/*     */         }
/*     */ 
/* 218 */         boolean callSuper = false;
/* 219 */         if ((method instanceof CtConstructor)) {
/* 220 */           callSuper = !((CtConstructor)method).isClassInitializer();
/*     */         }
/* 222 */         this.gen.atMethodBody(s, callSuper, isVoid);
/*     */       }
/*     */ 
/* 225 */       return this.bytecode;
/*     */     }
/*     */     catch (NotFoundException e) {
/* 228 */       throw new CompileError(e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void makeDefaultBody(Bytecode b, CtClass type)
/*     */   {
/*     */     int value;
/*     */     int op;
/*     */     int value;
/* 235 */     if ((type instanceof CtPrimitiveType)) {
/* 236 */       CtPrimitiveType pt = (CtPrimitiveType)type;
/* 237 */       int op = pt.getReturnOp();
/*     */       int value;
/* 238 */       if (op == 175) {
/* 239 */         value = 14;
/*     */       }
/*     */       else
/*     */       {
/*     */         int value;
/* 240 */         if (op == 174) {
/* 241 */           value = 11;
/*     */         }
/*     */         else
/*     */         {
/*     */           int value;
/* 242 */           if (op == 173) {
/* 243 */             value = 9;
/*     */           }
/*     */           else
/*     */           {
/*     */             int value;
/* 244 */             if (op == 177)
/* 245 */               value = 0;
/*     */             else
/* 247 */               value = 3; 
/*     */           }
/*     */         }
/*     */       } } else { op = 176;
/* 251 */       value = 1;
/*     */     }
/*     */ 
/* 254 */     if (value != 0) {
/* 255 */       b.addOpcode(value);
/*     */     }
/* 257 */     b.addOpcode(op);
/*     */   }
/*     */ 
/*     */   public boolean recordLocalVariables(CodeAttribute ca, int pc)
/*     */     throws CompileError
/*     */   {
/* 272 */     LocalVariableAttribute va = (LocalVariableAttribute)ca.getAttribute("LocalVariableTable");
/*     */ 
/* 275 */     if (va == null) {
/* 276 */       return false;
/*     */     }
/* 278 */     int n = va.tableLength();
/* 279 */     for (int i = 0; i < n; i++) {
/* 280 */       int start = va.startPc(i);
/* 281 */       int len = va.codeLength(i);
/* 282 */       if ((start <= pc) && (pc < start + len)) {
/* 283 */         this.gen.recordVariable(va.descriptor(i), va.variableName(i), va.index(i), this.stable);
/*     */       }
/*     */     }
/*     */ 
/* 287 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean recordParamNames(CodeAttribute ca, int numOfLocalVars)
/*     */     throws CompileError
/*     */   {
/* 302 */     LocalVariableAttribute va = (LocalVariableAttribute)ca.getAttribute("LocalVariableTable");
/*     */ 
/* 305 */     if (va == null) {
/* 306 */       return false;
/*     */     }
/* 308 */     int n = va.tableLength();
/* 309 */     for (int i = 0; i < n; i++) {
/* 310 */       int index = va.index(i);
/* 311 */       if (index < numOfLocalVars) {
/* 312 */         this.gen.recordVariable(va.descriptor(i), va.variableName(i), index, this.stable);
/*     */       }
/*     */     }
/*     */ 
/* 316 */     return true;
/*     */   }
/*     */ 
/*     */   public int recordParams(CtClass[] params, boolean isStatic)
/*     */     throws CompileError
/*     */   {
/* 333 */     return this.gen.recordParams(params, isStatic, "$", "$args", "$$", this.stable);
/*     */   }
/*     */ 
/*     */   public int recordParams(String target, CtClass[] params, boolean use0, int varNo, boolean isStatic)
/*     */     throws CompileError
/*     */   {
/* 361 */     return this.gen.recordParams(params, isStatic, "$", "$args", "$$", use0, varNo, target, this.stable);
/*     */   }
/*     */ 
/*     */   public void setMaxLocals(int max)
/*     */   {
/* 375 */     this.gen.setMaxLocals(max);
/*     */   }
/*     */ 
/*     */   public int recordReturnType(CtClass type, boolean useResultVar)
/*     */     throws CompileError
/*     */   {
/* 395 */     this.gen.recordType(type);
/* 396 */     return this.gen.recordReturnType(type, "$r", useResultVar ? "$_" : null, this.stable);
/*     */   }
/*     */ 
/*     */   public void recordType(CtClass t)
/*     */   {
/* 407 */     this.gen.recordType(t);
/*     */   }
/*     */ 
/*     */   public int recordVariable(CtClass type, String name)
/*     */     throws CompileError
/*     */   {
/* 419 */     return this.gen.recordVariable(type, name, this.stable);
/*     */   }
/*     */ 
/*     */   public void recordProceed(String target, String method)
/*     */     throws CompileError
/*     */   {
/* 434 */     Parser p = new Parser(new Lex(target));
/* 435 */     final ASTree texpr = p.parseExpression(this.stable);
/* 436 */     final String m = method;
/*     */ 
/* 438 */     ProceedHandler h = new ProceedHandler() { private final String val$m;
/*     */       private final ASTree val$texpr;
/*     */ 
/* 442 */       public void doit(JvstCodeGen gen, Bytecode b, ASTList args) throws CompileError { ASTree expr = new Member(m);
/* 443 */         if (texpr != null) {
/* 444 */           expr = Expr.make(46, texpr, expr);
/*     */         }
/* 446 */         expr = CallExpr.makeCall(expr, args);
/* 447 */         gen.compileExpr(expr);
/* 448 */         gen.addNullIfVoid();
/*     */       }
/*     */ 
/*     */       public void setReturnType(JvstTypeChecker check, ASTList args)
/*     */         throws CompileError
/*     */       {
/* 454 */         ASTree expr = new Member(m);
/* 455 */         if (texpr != null) {
/* 456 */           expr = Expr.make(46, texpr, expr);
/*     */         }
/* 458 */         expr = CallExpr.makeCall(expr, args);
/* 459 */         expr.accept(check);
/* 460 */         check.addNullIfVoid();
/*     */       }
/*     */     };
/* 464 */     this.gen.setProceedHandler(h, "$proceed");
/*     */   }
/*     */ 
/*     */   public void recordStaticProceed(String targetClass, String method)
/*     */     throws CompileError
/*     */   {
/* 479 */     final String c = targetClass;
/* 480 */     final String m = method;
/*     */ 
/* 482 */     ProceedHandler h = new ProceedHandler() { private final String val$c;
/*     */       private final String val$m;
/*     */ 
/* 486 */       public void doit(JvstCodeGen gen, Bytecode b, ASTList args) throws CompileError { Expr expr = Expr.make(35, new Symbol(c), new Member(m));
/*     */ 
/* 488 */         expr = CallExpr.makeCall(expr, args);
/* 489 */         gen.compileExpr(expr);
/* 490 */         gen.addNullIfVoid();
/*     */       }
/*     */ 
/*     */       public void setReturnType(JvstTypeChecker check, ASTList args)
/*     */         throws CompileError
/*     */       {
/* 496 */         Expr expr = Expr.make(35, new Symbol(c), new Member(m));
/*     */ 
/* 498 */         expr = CallExpr.makeCall(expr, args);
/* 499 */         expr.accept(check);
/* 500 */         check.addNullIfVoid();
/*     */       }
/*     */     };
/* 504 */     this.gen.setProceedHandler(h, "$proceed");
/*     */   }
/*     */ 
/*     */   public void recordSpecialProceed(String target, String classname, String methodname, String descriptor)
/*     */     throws CompileError
/*     */   {
/* 522 */     Parser p = new Parser(new Lex(target));
/* 523 */     final ASTree texpr = p.parseExpression(this.stable);
/* 524 */     final String cname = classname;
/* 525 */     final String method = methodname;
/* 526 */     final String desc = descriptor;
/*     */ 
/* 528 */     ProceedHandler h = new ProceedHandler() { private final ASTree val$texpr;
/*     */       private final String val$cname;
/*     */       private final String val$method;
/*     */       private final String val$desc;
/*     */ 
/* 532 */       public void doit(JvstCodeGen gen, Bytecode b, ASTList args) throws CompileError { gen.compileInvokeSpecial(texpr, cname, method, desc, args); }
/*     */ 
/*     */ 
/*     */       public void setReturnType(JvstTypeChecker c, ASTList args)
/*     */         throws CompileError
/*     */       {
/* 538 */         c.compileInvokeSpecial(texpr, cname, method, desc, args);
/*     */       }
/*     */     };
/* 543 */     this.gen.setProceedHandler(h, "$proceed");
/*     */   }
/*     */ 
/*     */   public void recordProceed(ProceedHandler h)
/*     */   {
/* 550 */     this.gen.setProceedHandler(h, "$proceed");
/*     */   }
/*     */ 
/*     */   public void compileStmnt(String src)
/*     */     throws CompileError
/*     */   {
/* 563 */     Parser p = new Parser(new Lex(src));
/* 564 */     SymbolTable stb = new SymbolTable(this.stable);
/* 565 */     while (p.hasMore()) {
/* 566 */       Stmnt s = p.parseStatement(stb);
/* 567 */       if (s != null)
/* 568 */         s.accept(this.gen);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void compileExpr(String src)
/*     */     throws CompileError
/*     */   {
/* 582 */     ASTree e = parseExpr(src, this.stable);
/* 583 */     compileExpr(e);
/*     */   }
/*     */ 
/*     */   public static ASTree parseExpr(String src, SymbolTable st)
/*     */     throws CompileError
/*     */   {
/* 592 */     Parser p = new Parser(new Lex(src));
/* 593 */     return p.parseExpression(st);
/*     */   }
/*     */ 
/*     */   public void compileExpr(ASTree e)
/*     */     throws CompileError
/*     */   {
/* 606 */     if (e != null)
/* 607 */       this.gen.compileExpr(e);
/*     */   }
/*     */ 
/*     */   public static class CtFieldWithInit extends CtField
/*     */   {
/*     */     private ASTree init;
/*     */ 
/*     */     CtFieldWithInit(CtClass type, String name, CtClass declaring)
/*     */       throws CannotCompileException
/*     */     {
/* 116 */       super(name, declaring);
/* 117 */       this.init = null;
/*     */     }
/*     */     protected void setInit(ASTree i) {
/* 120 */       this.init = i;
/*     */     }
/*     */     protected ASTree getInitAST() {
/* 123 */       return this.init;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.Javac
 * JD-Core Version:    0.6.2
 */