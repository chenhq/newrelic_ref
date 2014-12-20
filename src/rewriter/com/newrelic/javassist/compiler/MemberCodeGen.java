/*      */ package com.newrelic.javassist.compiler;
/*      */ 
/*      */ import com.newrelic.javassist.ClassPool;
/*      */ import com.newrelic.javassist.CtClass;
/*      */ import com.newrelic.javassist.CtField;
/*      */ import com.newrelic.javassist.CtMethod;
/*      */ import com.newrelic.javassist.Modifier;
/*      */ import com.newrelic.javassist.NotFoundException;
/*      */ import com.newrelic.javassist.bytecode.AccessFlag;
/*      */ import com.newrelic.javassist.bytecode.Bytecode;
/*      */ import com.newrelic.javassist.bytecode.ClassFile;
/*      */ import com.newrelic.javassist.bytecode.ConstPool;
/*      */ import com.newrelic.javassist.bytecode.Descriptor;
/*      */ import com.newrelic.javassist.bytecode.FieldInfo;
/*      */ import com.newrelic.javassist.bytecode.MethodInfo;
/*      */ import com.newrelic.javassist.compiler.ast.ASTList;
/*      */ import com.newrelic.javassist.compiler.ast.ASTree;
/*      */ import com.newrelic.javassist.compiler.ast.ArrayInit;
/*      */ import com.newrelic.javassist.compiler.ast.CallExpr;
/*      */ import com.newrelic.javassist.compiler.ast.Declarator;
/*      */ import com.newrelic.javassist.compiler.ast.Expr;
/*      */ import com.newrelic.javassist.compiler.ast.Keyword;
/*      */ import com.newrelic.javassist.compiler.ast.Member;
/*      */ import com.newrelic.javassist.compiler.ast.MethodDecl;
/*      */ import com.newrelic.javassist.compiler.ast.NewExpr;
/*      */ import com.newrelic.javassist.compiler.ast.Pair;
/*      */ import com.newrelic.javassist.compiler.ast.Stmnt;
/*      */ import com.newrelic.javassist.compiler.ast.Symbol;
/*      */ import java.util.ArrayList;
/*      */ 
/*      */ public class MemberCodeGen extends CodeGen
/*      */ {
/*      */   protected MemberResolver resolver;
/*      */   protected CtClass thisClass;
/*      */   protected MethodInfo thisMethod;
/*      */   protected boolean resultStatic;
/*      */ 
/*      */   public MemberCodeGen(Bytecode b, CtClass cc, ClassPool cp)
/*      */   {
/*   34 */     super(b);
/*   35 */     this.resolver = new MemberResolver(cp);
/*   36 */     this.thisClass = cc;
/*   37 */     this.thisMethod = null;
/*      */   }
/*      */ 
/*      */   public int getMajorVersion()
/*      */   {
/*   45 */     ClassFile cf = this.thisClass.getClassFile2();
/*   46 */     if (cf == null) {
/*   47 */       return ClassFile.MAJOR_VERSION;
/*      */     }
/*   49 */     return cf.getMajorVersion();
/*      */   }
/*      */ 
/*      */   public void setThisMethod(CtMethod m)
/*      */   {
/*   56 */     this.thisMethod = m.getMethodInfo2();
/*   57 */     if (this.typeChecker != null)
/*   58 */       this.typeChecker.setThisMethod(this.thisMethod); 
/*      */   }
/*      */ 
/*   61 */   public CtClass getThisClass() { return this.thisClass; }
/*      */ 
/*      */ 
/*      */   protected String getThisName()
/*      */   {
/*   67 */     return MemberResolver.javaToJvmName(this.thisClass.getName());
/*      */   }
/*      */ 
/*      */   protected String getSuperName()
/*      */     throws CompileError
/*      */   {
/*   74 */     return MemberResolver.javaToJvmName(MemberResolver.getSuperclass(this.thisClass).getName());
/*      */   }
/*      */ 
/*      */   protected void insertDefaultSuperCall() throws CompileError
/*      */   {
/*   79 */     this.bytecode.addAload(0);
/*   80 */     this.bytecode.addInvokespecial(MemberResolver.getSuperclass(this.thisClass), "<init>", "()V");
/*      */   }
/*      */ 
/*      */   protected void atTryStmnt(Stmnt st)
/*      */     throws CompileError
/*      */   {
/*  189 */     Bytecode bc = this.bytecode;
/*  190 */     Stmnt body = (Stmnt)st.getLeft();
/*  191 */     if (body == null) {
/*  192 */       return;
/*      */     }
/*  194 */     ASTList catchList = (ASTList)st.getRight().getLeft();
/*  195 */     Stmnt finallyBlock = (Stmnt)st.getRight().getRight().getLeft();
/*  196 */     ArrayList gotoList = new ArrayList();
/*      */ 
/*  198 */     JsrHook jsrHook = null;
/*  199 */     if (finallyBlock != null) {
/*  200 */       jsrHook = new JsrHook(this);
/*      */     }
/*  202 */     int start = bc.currentPc();
/*  203 */     body.accept(this);
/*  204 */     int end = bc.currentPc();
/*  205 */     if (start == end) {
/*  206 */       throw new CompileError("empty try block");
/*      */     }
/*  208 */     boolean tryNotReturn = !this.hasReturned;
/*  209 */     if (tryNotReturn) {
/*  210 */       bc.addOpcode(167);
/*  211 */       gotoList.add(new Integer(bc.currentPc()));
/*  212 */       bc.addIndex(0);
/*      */     }
/*      */ 
/*  215 */     int var = getMaxLocals();
/*  216 */     incMaxLocals(1);
/*  217 */     while (catchList != null)
/*      */     {
/*  219 */       Pair p = (Pair)catchList.head();
/*  220 */       catchList = catchList.tail();
/*  221 */       Declarator decl = (Declarator)p.getLeft();
/*  222 */       Stmnt block = (Stmnt)p.getRight();
/*      */ 
/*  224 */       decl.setLocalVar(var);
/*      */ 
/*  226 */       CtClass type = this.resolver.lookupClassByJvmName(decl.getClassName());
/*  227 */       decl.setClassName(MemberResolver.javaToJvmName(type.getName()));
/*  228 */       bc.addExceptionHandler(start, end, bc.currentPc(), type);
/*  229 */       bc.growStack(1);
/*  230 */       bc.addAstore(var);
/*  231 */       this.hasReturned = false;
/*  232 */       if (block != null) {
/*  233 */         block.accept(this);
/*      */       }
/*  235 */       if (!this.hasReturned) {
/*  236 */         bc.addOpcode(167);
/*  237 */         gotoList.add(new Integer(bc.currentPc()));
/*  238 */         bc.addIndex(0);
/*  239 */         tryNotReturn = true;
/*      */       }
/*      */     }
/*      */ 
/*  243 */     if (finallyBlock != null) {
/*  244 */       jsrHook.remove(this);
/*      */ 
/*  246 */       int pcAnyCatch = bc.currentPc();
/*  247 */       bc.addExceptionHandler(start, pcAnyCatch, pcAnyCatch, 0);
/*  248 */       bc.growStack(1);
/*  249 */       bc.addAstore(var);
/*  250 */       this.hasReturned = false;
/*  251 */       finallyBlock.accept(this);
/*  252 */       if (!this.hasReturned) {
/*  253 */         bc.addAload(var);
/*  254 */         bc.addOpcode(191);
/*      */       }
/*      */ 
/*  257 */       addFinally(jsrHook.jsrList, finallyBlock);
/*      */     }
/*      */ 
/*  260 */     int pcEnd = bc.currentPc();
/*  261 */     patchGoto(gotoList, pcEnd);
/*  262 */     this.hasReturned = (!tryNotReturn);
/*  263 */     if ((finallyBlock != null) && 
/*  264 */       (tryNotReturn))
/*  265 */       finallyBlock.accept(this);
/*      */   }
/*      */ 
/*      */   private void addFinally(ArrayList returnList, Stmnt finallyBlock)
/*      */     throws CompileError
/*      */   {
/*  275 */     Bytecode bc = this.bytecode;
/*  276 */     int n = returnList.size();
/*  277 */     for (int i = 0; i < n; i++) {
/*  278 */       int[] ret = (int[])returnList.get(i);
/*  279 */       int pc = ret[0];
/*  280 */       bc.write16bit(pc, bc.currentPc() - pc + 1);
/*  281 */       CodeGen.ReturnHook hook = new JsrHook2(this, ret);
/*  282 */       finallyBlock.accept(this);
/*  283 */       hook.remove(this);
/*  284 */       if (!this.hasReturned) {
/*  285 */         bc.addOpcode(167);
/*  286 */         bc.addIndex(pc + 3 - bc.currentPc());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void atNewExpr(NewExpr expr) throws CompileError {
/*  292 */     if (expr.isArray()) {
/*  293 */       atNewArrayExpr(expr);
/*      */     } else {
/*  295 */       CtClass clazz = this.resolver.lookupClassByName(expr.getClassName());
/*  296 */       String cname = clazz.getName();
/*  297 */       ASTList args = expr.getArguments();
/*  298 */       this.bytecode.addNew(cname);
/*  299 */       this.bytecode.addOpcode(89);
/*      */ 
/*  301 */       atMethodCallCore(clazz, "<init>", args, false, true, -1, null);
/*      */ 
/*  304 */       this.exprType = 307;
/*  305 */       this.arrayDim = 0;
/*  306 */       this.className = MemberResolver.javaToJvmName(cname);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void atNewArrayExpr(NewExpr expr) throws CompileError {
/*  311 */     int type = expr.getArrayType();
/*  312 */     ASTList size = expr.getArraySize();
/*  313 */     ASTList classname = expr.getClassName();
/*  314 */     ArrayInit init = expr.getInitializer();
/*  315 */     if (size.length() > 1) {
/*  316 */       if (init != null) {
/*  317 */         throw new CompileError("sorry, multi-dimensional array initializer for new is not supported");
/*      */       }
/*      */ 
/*  321 */       atMultiNewArray(type, classname, size);
/*  322 */       return;
/*      */     }
/*      */ 
/*  325 */     ASTree sizeExpr = size.head();
/*  326 */     atNewArrayExpr2(type, sizeExpr, Declarator.astToClassName(classname, '/'), init);
/*      */   }
/*      */ 
/*      */   private void atNewArrayExpr2(int type, ASTree sizeExpr, String jvmClassname, ArrayInit init) throws CompileError
/*      */   {
/*  331 */     if (init == null) {
/*  332 */       if (sizeExpr == null) {
/*  333 */         throw new CompileError("no array size");
/*      */       }
/*  335 */       sizeExpr.accept(this);
/*      */     }
/*  337 */     else if (sizeExpr == null) {
/*  338 */       int s = init.length();
/*  339 */       this.bytecode.addIconst(s);
/*      */     }
/*      */     else {
/*  342 */       throw new CompileError("unnecessary array size specified for new");
/*      */     }
/*      */     String elementClass;
/*  345 */     if (type == 307) {
/*  346 */       String elementClass = resolveClassName(jvmClassname);
/*  347 */       this.bytecode.addAnewarray(MemberResolver.jvmToJavaName(elementClass));
/*      */     }
/*      */     else {
/*  350 */       elementClass = null;
/*  351 */       int atype = 0;
/*  352 */       switch (type) {
/*      */       case 301:
/*  354 */         atype = 4;
/*  355 */         break;
/*      */       case 306:
/*  357 */         atype = 5;
/*  358 */         break;
/*      */       case 317:
/*  360 */         atype = 6;
/*  361 */         break;
/*      */       case 312:
/*  363 */         atype = 7;
/*  364 */         break;
/*      */       case 303:
/*  366 */         atype = 8;
/*  367 */         break;
/*      */       case 334:
/*  369 */         atype = 9;
/*  370 */         break;
/*      */       case 324:
/*  372 */         atype = 10;
/*  373 */         break;
/*      */       case 326:
/*  375 */         atype = 11;
/*  376 */         break;
/*      */       default:
/*  378 */         badNewExpr();
/*      */       }
/*      */ 
/*  382 */       this.bytecode.addOpcode(188);
/*  383 */       this.bytecode.add(atype);
/*      */     }
/*      */ 
/*  386 */     if (init != null) {
/*  387 */       int s = init.length();
/*  388 */       ASTList list = init;
/*  389 */       for (int i = 0; i < s; i++) {
/*  390 */         this.bytecode.addOpcode(89);
/*  391 */         this.bytecode.addIconst(i);
/*  392 */         list.head().accept(this);
/*  393 */         if (!isRefType(type)) {
/*  394 */           atNumCastExpr(this.exprType, type);
/*      */         }
/*  396 */         this.bytecode.addOpcode(getArrayWriteOp(type, 0));
/*  397 */         list = list.tail();
/*      */       }
/*      */     }
/*      */ 
/*  401 */     this.exprType = type;
/*  402 */     this.arrayDim = 1;
/*  403 */     this.className = elementClass;
/*      */   }
/*      */ 
/*      */   private static void badNewExpr() throws CompileError {
/*  407 */     throw new CompileError("bad new expression");
/*      */   }
/*      */ 
/*      */   protected void atArrayVariableAssign(ArrayInit init, int varType, int varArray, String varClass) throws CompileError
/*      */   {
/*  412 */     atNewArrayExpr2(varType, null, varClass, init);
/*      */   }
/*      */ 
/*      */   public void atArrayInit(ArrayInit init) throws CompileError {
/*  416 */     throw new CompileError("array initializer is not supported");
/*      */   }
/*      */ 
/*      */   protected void atMultiNewArray(int type, ASTList classname, ASTList size)
/*      */     throws CompileError
/*      */   {
/*  423 */     int dim = size.length();
/*  424 */     for (int count = 0; size != null; size = size.tail()) {
/*  425 */       ASTree s = size.head();
/*  426 */       if (s == null) {
/*      */         break;
/*      */       }
/*  429 */       count++;
/*  430 */       s.accept(this);
/*  431 */       if (this.exprType != 324) {
/*  432 */         throw new CompileError("bad type for array size");
/*      */       }
/*      */     }
/*      */ 
/*  436 */     this.exprType = type;
/*  437 */     this.arrayDim = dim;
/*      */     String desc;
/*      */     String desc;
/*  438 */     if (type == 307) {
/*  439 */       this.className = resolveClassName(classname);
/*  440 */       desc = toJvmArrayName(this.className, dim);
/*      */     }
/*      */     else {
/*  443 */       desc = toJvmTypeName(type, dim);
/*      */     }
/*  445 */     this.bytecode.addMultiNewarray(desc, count);
/*      */   }
/*      */ 
/*      */   public void atCallExpr(CallExpr expr) throws CompileError {
/*  449 */     String mname = null;
/*  450 */     CtClass targetClass = null;
/*  451 */     ASTree method = expr.oprand1();
/*  452 */     ASTList args = (ASTList)expr.oprand2();
/*  453 */     boolean isStatic = false;
/*  454 */     boolean isSpecial = false;
/*  455 */     int aload0pos = -1;
/*      */ 
/*  457 */     MemberResolver.Method cached = expr.getMethod();
/*  458 */     if ((method instanceof Member)) {
/*  459 */       mname = ((Member)method).get();
/*  460 */       targetClass = this.thisClass;
/*  461 */       if ((this.inStaticMethod) || ((cached != null) && (cached.isStatic()))) {
/*  462 */         isStatic = true;
/*      */       } else {
/*  464 */         aload0pos = this.bytecode.currentPc();
/*  465 */         this.bytecode.addAload(0);
/*      */       }
/*      */     }
/*  468 */     else if ((method instanceof Keyword)) {
/*  469 */       isSpecial = true;
/*  470 */       mname = "<init>";
/*  471 */       targetClass = this.thisClass;
/*  472 */       if (this.inStaticMethod) {
/*  473 */         throw new CompileError("a constructor cannot be static");
/*      */       }
/*  475 */       this.bytecode.addAload(0);
/*      */ 
/*  477 */       if (((Keyword)method).get() == 336)
/*  478 */         targetClass = MemberResolver.getSuperclass(targetClass);
/*      */     }
/*  480 */     else if ((method instanceof Expr)) {
/*  481 */       Expr e = (Expr)method;
/*  482 */       mname = ((Symbol)e.oprand2()).get();
/*  483 */       int op = e.getOperator();
/*  484 */       if (op == 35) {
/*  485 */         targetClass = this.resolver.lookupClass(((Symbol)e.oprand1()).get(), false);
/*      */ 
/*  487 */         isStatic = true;
/*      */       }
/*  489 */       else if (op == 46) {
/*  490 */         ASTree target = e.oprand1();
/*  491 */         if (((target instanceof Keyword)) && 
/*  492 */           (((Keyword)target).get() == 336))
/*  493 */           isSpecial = true;
/*      */         try
/*      */         {
/*  496 */           target.accept(this);
/*      */         }
/*      */         catch (NoFieldException nfe) {
/*  499 */           if (nfe.getExpr() != target) {
/*  500 */             throw nfe;
/*      */           }
/*      */ 
/*  503 */           this.exprType = 307;
/*  504 */           this.arrayDim = 0;
/*  505 */           this.className = nfe.getField();
/*  506 */           this.resolver.recordPackage(this.className);
/*  507 */           isStatic = true;
/*      */         }
/*      */ 
/*  510 */         if (this.arrayDim > 0)
/*  511 */           targetClass = this.resolver.lookupClass("java.lang.Object", true);
/*  512 */         else if (this.exprType == 307)
/*  513 */           targetClass = this.resolver.lookupClassByJvmName(this.className);
/*      */         else
/*  515 */           badMethod();
/*      */       }
/*      */       else {
/*  518 */         badMethod();
/*      */       }
/*      */     } else {
/*  521 */       fatal();
/*      */     }
/*  523 */     atMethodCallCore(targetClass, mname, args, isStatic, isSpecial, aload0pos, cached);
/*      */   }
/*      */ 
/*      */   private static void badMethod() throws CompileError
/*      */   {
/*  528 */     throw new CompileError("bad method");
/*      */   }
/*      */ 
/*      */   public void atMethodCallCore(CtClass targetClass, String mname, ASTList args, boolean isStatic, boolean isSpecial, int aload0pos, MemberResolver.Method found)
/*      */     throws CompileError
/*      */   {
/*  542 */     int nargs = getMethodArgsLength(args);
/*  543 */     int[] types = new int[nargs];
/*  544 */     int[] dims = new int[nargs];
/*  545 */     String[] cnames = new String[nargs];
/*      */ 
/*  547 */     if ((!isStatic) && (found != null) && (found.isStatic())) {
/*  548 */       this.bytecode.addOpcode(87);
/*  549 */       isStatic = true;
/*      */     }
/*      */ 
/*  552 */     int stack = this.bytecode.getStackDepth();
/*      */ 
/*  555 */     atMethodArgs(args, types, dims, cnames);
/*      */ 
/*  558 */     int count = this.bytecode.getStackDepth() - stack + 1;
/*      */ 
/*  560 */     if (found == null) {
/*  561 */       found = this.resolver.lookupMethod(targetClass, this.thisClass, this.thisMethod, mname, types, dims, cnames);
/*      */     }
/*      */ 
/*  564 */     if (found == null)
/*      */     {
/*      */       String msg;
/*      */       String msg;
/*  566 */       if (mname.equals("<init>"))
/*  567 */         msg = "constructor not found";
/*      */       else {
/*  569 */         msg = "Method " + mname + " not found in " + targetClass.getName();
/*      */       }
/*      */ 
/*  572 */       throw new CompileError(msg);
/*      */     }
/*      */ 
/*  575 */     atMethodCallCore2(targetClass, mname, isStatic, isSpecial, aload0pos, count, found);
/*      */   }
/*      */ 
/*      */   private void atMethodCallCore2(CtClass targetClass, String mname, boolean isStatic, boolean isSpecial, int aload0pos, int count, MemberResolver.Method found)
/*      */     throws CompileError
/*      */   {
/*  585 */     CtClass declClass = found.declaring;
/*  586 */     MethodInfo minfo = found.info;
/*  587 */     String desc = minfo.getDescriptor();
/*  588 */     int acc = minfo.getAccessFlags();
/*      */ 
/*  590 */     if (mname.equals("<init>")) {
/*  591 */       isSpecial = true;
/*  592 */       if (declClass != targetClass) {
/*  593 */         throw new CompileError("no such a constructor");
/*      */       }
/*  595 */       if ((declClass != this.thisClass) && (AccessFlag.isPrivate(acc))) {
/*  596 */         desc = getAccessibleConstructor(desc, declClass, minfo);
/*  597 */         this.bytecode.addOpcode(1);
/*      */       }
/*      */     }
/*  600 */     else if (AccessFlag.isPrivate(acc)) {
/*  601 */       if (declClass == this.thisClass) {
/*  602 */         isSpecial = true;
/*      */       } else {
/*  604 */         isSpecial = false;
/*  605 */         isStatic = true;
/*  606 */         String origDesc = desc;
/*  607 */         if ((acc & 0x8) == 0) {
/*  608 */           desc = Descriptor.insertParameter(declClass.getName(), origDesc);
/*      */         }
/*      */ 
/*  611 */         acc = AccessFlag.setPackage(acc) | 0x8;
/*  612 */         mname = getAccessiblePrivate(mname, origDesc, desc, minfo, declClass);
/*      */       }
/*      */     }
/*      */ 
/*  616 */     boolean popTarget = false;
/*  617 */     if ((acc & 0x8) != 0) {
/*  618 */       if (!isStatic)
/*      */       {
/*  624 */         isStatic = true;
/*  625 */         if (aload0pos >= 0)
/*  626 */           this.bytecode.write(aload0pos, 0);
/*      */         else {
/*  628 */           popTarget = true;
/*      */         }
/*      */       }
/*  631 */       this.bytecode.addInvokestatic(declClass, mname, desc);
/*      */     }
/*  633 */     else if (isSpecial) {
/*  634 */       this.bytecode.addInvokespecial(declClass, mname, desc);
/*      */     } else {
/*  636 */       if ((!Modifier.isPublic(declClass.getModifiers())) || (declClass.isInterface() != targetClass.isInterface()))
/*      */       {
/*  638 */         declClass = targetClass;
/*      */       }
/*  640 */       if (declClass.isInterface()) {
/*  641 */         this.bytecode.addInvokeinterface(declClass, mname, desc, count);
/*      */       } else {
/*  643 */         if (isStatic) {
/*  644 */           throw new CompileError(mname + " is not static");
/*      */         }
/*  646 */         this.bytecode.addInvokevirtual(declClass, mname, desc);
/*      */       }
/*      */     }
/*  649 */     setReturnType(desc, isStatic, popTarget);
/*      */   }
/*      */ 
/*      */   protected String getAccessiblePrivate(String methodName, String desc, String newDesc, MethodInfo minfo, CtClass declClass)
/*      */     throws CompileError
/*      */   {
/*  664 */     if (isEnclosing(declClass, this.thisClass)) {
/*  665 */       AccessorMaker maker = declClass.getAccessorMaker();
/*  666 */       if (maker != null) {
/*  667 */         return maker.getMethodAccessor(methodName, desc, newDesc, minfo);
/*      */       }
/*      */     }
/*      */ 
/*  671 */     throw new CompileError("Method " + methodName + " is private");
/*      */   }
/*      */ 
/*      */   protected String getAccessibleConstructor(String desc, CtClass declClass, MethodInfo minfo)
/*      */     throws CompileError
/*      */   {
/*  688 */     if (isEnclosing(declClass, this.thisClass)) {
/*  689 */       AccessorMaker maker = declClass.getAccessorMaker();
/*  690 */       if (maker != null) {
/*  691 */         return maker.getConstructor(declClass, desc, minfo);
/*      */       }
/*      */     }
/*  694 */     throw new CompileError("the called constructor is private in " + declClass.getName());
/*      */   }
/*      */ 
/*      */   private boolean isEnclosing(CtClass outer, CtClass inner)
/*      */   {
/*      */     try {
/*  700 */       while (inner != null) {
/*  701 */         inner = inner.getDeclaringClass();
/*  702 */         if (inner == outer)
/*  703 */           return true;
/*      */       }
/*      */     } catch (NotFoundException e) {
/*      */     }
/*  707 */     return false;
/*      */   }
/*      */ 
/*      */   public int getMethodArgsLength(ASTList args) {
/*  711 */     return ASTList.length(args);
/*      */   }
/*      */ 
/*      */   public void atMethodArgs(ASTList args, int[] types, int[] dims, String[] cnames) throws CompileError
/*      */   {
/*  716 */     int i = 0;
/*  717 */     while (args != null) {
/*  718 */       ASTree a = args.head();
/*  719 */       a.accept(this);
/*  720 */       types[i] = this.exprType;
/*  721 */       dims[i] = this.arrayDim;
/*  722 */       cnames[i] = this.className;
/*  723 */       i++;
/*  724 */       args = args.tail();
/*      */     }
/*      */   }
/*      */ 
/*      */   void setReturnType(String desc, boolean isStatic, boolean popTarget)
/*      */     throws CompileError
/*      */   {
/*  731 */     int i = desc.indexOf(')');
/*  732 */     if (i < 0) {
/*  733 */       badMethod();
/*      */     }
/*  735 */     char c = desc.charAt(++i);
/*  736 */     int dim = 0;
/*  737 */     while (c == '[') {
/*  738 */       dim++;
/*  739 */       c = desc.charAt(++i);
/*      */     }
/*      */ 
/*  742 */     this.arrayDim = dim;
/*  743 */     if (c == 'L') {
/*  744 */       int j = desc.indexOf(';', i + 1);
/*  745 */       if (j < 0) {
/*  746 */         badMethod();
/*      */       }
/*  748 */       this.exprType = 307;
/*  749 */       this.className = desc.substring(i + 1, j);
/*      */     }
/*      */     else {
/*  752 */       this.exprType = MemberResolver.descToType(c);
/*  753 */       this.className = null;
/*      */     }
/*      */ 
/*  756 */     int etype = this.exprType;
/*  757 */     if ((isStatic) && 
/*  758 */       (popTarget))
/*  759 */       if (is2word(etype, dim)) {
/*  760 */         this.bytecode.addOpcode(93);
/*  761 */         this.bytecode.addOpcode(88);
/*  762 */         this.bytecode.addOpcode(87);
/*      */       }
/*  764 */       else if (etype == 344) {
/*  765 */         this.bytecode.addOpcode(87);
/*      */       } else {
/*  767 */         this.bytecode.addOpcode(95);
/*  768 */         this.bytecode.addOpcode(87);
/*      */       }
/*      */   }
/*      */ 
/*      */   protected void atFieldAssign(Expr expr, int op, ASTree left, ASTree right, boolean doDup)
/*      */     throws CompileError
/*      */   {
/*  777 */     CtField f = fieldAccess(left, false);
/*  778 */     boolean is_static = this.resultStatic;
/*  779 */     if ((op != 61) && (!is_static))
/*  780 */       this.bytecode.addOpcode(89);
/*      */     int fi;
/*      */     int fi;
/*  783 */     if (op == 61) {
/*  784 */       FieldInfo finfo = f.getFieldInfo2();
/*  785 */       setFieldType(finfo);
/*  786 */       AccessorMaker maker = isAccessibleField(f, finfo);
/*      */       int fi;
/*  787 */       if (maker == null)
/*  788 */         fi = addFieldrefInfo(f, finfo);
/*      */       else
/*  790 */         fi = 0;
/*      */     }
/*      */     else {
/*  793 */       fi = atFieldRead(f, is_static);
/*      */     }
/*  795 */     int fType = this.exprType;
/*  796 */     int fDim = this.arrayDim;
/*  797 */     String cname = this.className;
/*      */ 
/*  799 */     atAssignCore(expr, op, right, fType, fDim, cname);
/*      */ 
/*  801 */     boolean is2w = is2word(fType, fDim);
/*  802 */     if (doDup)
/*      */     {
/*      */       int dup_code;
/*      */       int dup_code;
/*  804 */       if (is_static)
/*  805 */         dup_code = is2w ? 92 : 89;
/*      */       else {
/*  807 */         dup_code = is2w ? 93 : 90;
/*      */       }
/*  809 */       this.bytecode.addOpcode(dup_code);
/*      */     }
/*      */ 
/*  812 */     atFieldAssignCore(f, is_static, fi, is2w);
/*      */ 
/*  814 */     this.exprType = fType;
/*  815 */     this.arrayDim = fDim;
/*  816 */     this.className = cname;
/*      */   }
/*      */ 
/*      */   private void atFieldAssignCore(CtField f, boolean is_static, int fi, boolean is2byte)
/*      */     throws CompileError
/*      */   {
/*  823 */     if (fi != 0) {
/*  824 */       if (is_static) {
/*  825 */         this.bytecode.add(179);
/*  826 */         this.bytecode.growStack(is2byte ? -2 : -1);
/*      */       }
/*      */       else {
/*  829 */         this.bytecode.add(181);
/*  830 */         this.bytecode.growStack(is2byte ? -3 : -2);
/*      */       }
/*      */ 
/*  833 */       this.bytecode.addIndex(fi);
/*      */     }
/*      */     else {
/*  836 */       CtClass declClass = f.getDeclaringClass();
/*  837 */       AccessorMaker maker = declClass.getAccessorMaker();
/*      */ 
/*  839 */       FieldInfo finfo = f.getFieldInfo2();
/*  840 */       MethodInfo minfo = maker.getFieldSetter(finfo, is_static);
/*  841 */       this.bytecode.addInvokestatic(declClass, minfo.getName(), minfo.getDescriptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void atMember(Member mem)
/*      */     throws CompileError
/*      */   {
/*  849 */     atFieldRead(mem);
/*      */   }
/*      */ 
/*      */   protected void atFieldRead(ASTree expr) throws CompileError
/*      */   {
/*  854 */     CtField f = fieldAccess(expr, true);
/*  855 */     if (f == null) {
/*  856 */       atArrayLength(expr);
/*  857 */       return;
/*      */     }
/*      */ 
/*  860 */     boolean is_static = this.resultStatic;
/*  861 */     ASTree cexpr = TypeChecker.getConstantFieldValue(f);
/*  862 */     if (cexpr == null) {
/*  863 */       atFieldRead(f, is_static);
/*      */     } else {
/*  865 */       cexpr.accept(this);
/*  866 */       setFieldType(f.getFieldInfo2());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void atArrayLength(ASTree expr) throws CompileError {
/*  871 */     if (this.arrayDim == 0) {
/*  872 */       throw new CompileError(".length applied to a non array");
/*      */     }
/*  874 */     this.bytecode.addOpcode(190);
/*  875 */     this.exprType = 324;
/*  876 */     this.arrayDim = 0;
/*      */   }
/*      */ 
/*      */   private int atFieldRead(CtField f, boolean isStatic)
/*      */     throws CompileError
/*      */   {
/*  885 */     FieldInfo finfo = f.getFieldInfo2();
/*  886 */     boolean is2byte = setFieldType(finfo);
/*  887 */     AccessorMaker maker = isAccessibleField(f, finfo);
/*  888 */     if (maker != null) {
/*  889 */       MethodInfo minfo = maker.getFieldGetter(finfo, isStatic);
/*  890 */       this.bytecode.addInvokestatic(f.getDeclaringClass(), minfo.getName(), minfo.getDescriptor());
/*      */ 
/*  892 */       return 0;
/*      */     }
/*      */ 
/*  895 */     int fi = addFieldrefInfo(f, finfo);
/*  896 */     if (isStatic) {
/*  897 */       this.bytecode.add(178);
/*  898 */       this.bytecode.growStack(is2byte ? 2 : 1);
/*      */     }
/*      */     else {
/*  901 */       this.bytecode.add(180);
/*  902 */       this.bytecode.growStack(is2byte ? 1 : 0);
/*      */     }
/*      */ 
/*  905 */     this.bytecode.addIndex(fi);
/*  906 */     return fi;
/*      */   }
/*      */ 
/*      */   private AccessorMaker isAccessibleField(CtField f, FieldInfo finfo)
/*      */     throws CompileError
/*      */   {
/*  918 */     if ((AccessFlag.isPrivate(finfo.getAccessFlags())) && (f.getDeclaringClass() != this.thisClass))
/*      */     {
/*  920 */       CtClass declClass = f.getDeclaringClass();
/*  921 */       if (isEnclosing(declClass, this.thisClass)) {
/*  922 */         AccessorMaker maker = declClass.getAccessorMaker();
/*  923 */         if (maker != null) {
/*  924 */           return maker;
/*      */         }
/*  926 */         throw new CompileError("fatal error.  bug?");
/*      */       }
/*      */ 
/*  929 */       throw new CompileError("Field " + f.getName() + " in " + declClass.getName() + " is private.");
/*      */     }
/*      */ 
/*  933 */     return null;
/*      */   }
/*      */ 
/*      */   private boolean setFieldType(FieldInfo finfo)
/*      */     throws CompileError
/*      */   {
/*  942 */     String type = finfo.getDescriptor();
/*      */ 
/*  944 */     int i = 0;
/*  945 */     int dim = 0;
/*  946 */     char c = type.charAt(i);
/*  947 */     while (c == '[') {
/*  948 */       dim++;
/*  949 */       c = type.charAt(++i);
/*      */     }
/*      */ 
/*  952 */     this.arrayDim = dim;
/*  953 */     this.exprType = MemberResolver.descToType(c);
/*      */ 
/*  955 */     if (c == 'L')
/*  956 */       this.className = type.substring(i + 1, type.indexOf(';', i + 1));
/*      */     else {
/*  958 */       this.className = null;
/*      */     }
/*  960 */     boolean is2byte = (c == 'J') || (c == 'D');
/*  961 */     return is2byte;
/*      */   }
/*      */ 
/*      */   private int addFieldrefInfo(CtField f, FieldInfo finfo) {
/*  965 */     ConstPool cp = this.bytecode.getConstPool();
/*  966 */     String cname = f.getDeclaringClass().getName();
/*  967 */     int ci = cp.addClassInfo(cname);
/*  968 */     String name = finfo.getName();
/*  969 */     String type = finfo.getDescriptor();
/*  970 */     return cp.addFieldrefInfo(ci, name, type);
/*      */   }
/*      */ 
/*      */   protected void atClassObject2(String cname) throws CompileError {
/*  974 */     if (getMajorVersion() < 49)
/*  975 */       super.atClassObject2(cname);
/*      */     else
/*  977 */       this.bytecode.addLdc(this.bytecode.getConstPool().addClassInfo(cname));
/*      */   }
/*      */ 
/*      */   protected void atFieldPlusPlus(int token, boolean isPost, ASTree oprand, Expr expr, boolean doDup)
/*      */     throws CompileError
/*      */   {
/*  984 */     CtField f = fieldAccess(oprand, false);
/*  985 */     boolean is_static = this.resultStatic;
/*  986 */     if (!is_static) {
/*  987 */       this.bytecode.addOpcode(89);
/*      */     }
/*  989 */     int fi = atFieldRead(f, is_static);
/*  990 */     int t = this.exprType;
/*  991 */     boolean is2w = is2word(t, this.arrayDim);
/*      */     int dup_code;
/*      */     int dup_code;
/*  994 */     if (is_static)
/*  995 */       dup_code = is2w ? 92 : 89;
/*      */     else {
/*  997 */       dup_code = is2w ? 93 : 90;
/*      */     }
/*  999 */     atPlusPlusCore(dup_code, doDup, token, isPost, expr);
/* 1000 */     atFieldAssignCore(f, is_static, fi, is2w);
/*      */   }
/*      */ 
/*      */   protected CtField fieldAccess(ASTree expr, boolean acceptLength)
/*      */     throws CompileError
/*      */   {
/* 1010 */     if ((expr instanceof Member)) {
/* 1011 */       String name = ((Member)expr).get();
/* 1012 */       CtField f = null;
/*      */       try {
/* 1014 */         f = this.thisClass.getField(name);
/*      */       }
/*      */       catch (NotFoundException e)
/*      */       {
/* 1018 */         throw new NoFieldException(name, expr);
/*      */       }
/*      */ 
/* 1021 */       boolean is_static = Modifier.isStatic(f.getModifiers());
/* 1022 */       if (!is_static) {
/* 1023 */         if (this.inStaticMethod) {
/* 1024 */           throw new CompileError("not available in a static method: " + name);
/*      */         }
/*      */ 
/* 1027 */         this.bytecode.addAload(0);
/*      */       }
/* 1029 */       this.resultStatic = is_static;
/* 1030 */       return f;
/*      */     }
/* 1032 */     if ((expr instanceof Expr)) {
/* 1033 */       Expr e = (Expr)expr;
/* 1034 */       int op = e.getOperator();
/* 1035 */       if (op == 35)
/*      */       {
/* 1040 */         CtField f = this.resolver.lookupField(((Symbol)e.oprand1()).get(), (Symbol)e.oprand2());
/*      */ 
/* 1042 */         this.resultStatic = true;
/* 1043 */         return f;
/*      */       }
/* 1045 */       if (op == 46) {
/* 1046 */         CtField f = null;
/*      */         try {
/* 1048 */           e.oprand1().accept(this);
/*      */ 
/* 1053 */           if ((this.exprType == 307) && (this.arrayDim == 0)) {
/* 1054 */             f = this.resolver.lookupFieldByJvmName(this.className, (Symbol)e.oprand2());
/*      */           } else {
/* 1056 */             if ((acceptLength) && (this.arrayDim > 0) && (((Symbol)e.oprand2()).get().equals("length")))
/*      */             {
/* 1058 */               return null;
/*      */             }
/* 1060 */             badLvalue();
/*      */           }
/* 1062 */           boolean is_static = Modifier.isStatic(f.getModifiers());
/* 1063 */           if (is_static) {
/* 1064 */             this.bytecode.addOpcode(87);
/*      */           }
/* 1066 */           this.resultStatic = is_static;
/* 1067 */           return f;
/*      */         }
/*      */         catch (NoFieldException nfe) {
/* 1070 */           if (nfe.getExpr() != e.oprand1()) {
/* 1071 */             throw nfe;
/*      */           }
/*      */ 
/* 1077 */           Symbol fname = (Symbol)e.oprand2();
/* 1078 */           String cname = nfe.getField();
/* 1079 */           f = this.resolver.lookupFieldByJvmName2(cname, fname, expr);
/* 1080 */           this.resolver.recordPackage(cname);
/* 1081 */           this.resultStatic = true;
/* 1082 */           return f;
/*      */         }
/*      */       }
/*      */ 
/* 1086 */       badLvalue();
/*      */     }
/*      */     else {
/* 1089 */       badLvalue();
/*      */     }
/* 1091 */     this.resultStatic = false;
/* 1092 */     return null;
/*      */   }
/*      */ 
/*      */   private static void badLvalue() throws CompileError {
/* 1096 */     throw new CompileError("bad l-value");
/*      */   }
/*      */ 
/*      */   public CtClass[] makeParamList(MethodDecl md) throws CompileError
/*      */   {
/* 1101 */     ASTList plist = md.getParams();
/*      */     CtClass[] params;
/*      */     CtClass[] params;
/* 1102 */     if (plist == null) {
/* 1103 */       params = new CtClass[0];
/*      */     } else {
/* 1105 */       int i = 0;
/* 1106 */       params = new CtClass[plist.length()];
/* 1107 */       while (plist != null) {
/* 1108 */         params[(i++)] = this.resolver.lookupClass((Declarator)plist.head());
/* 1109 */         plist = plist.tail();
/*      */       }
/*      */     }
/*      */ 
/* 1113 */     return params;
/*      */   }
/*      */ 
/*      */   public CtClass[] makeThrowsList(MethodDecl md) throws CompileError
/*      */   {
/* 1118 */     ASTList list = md.getThrows();
/* 1119 */     if (list == null) {
/* 1120 */       return null;
/*      */     }
/* 1122 */     int i = 0;
/* 1123 */     CtClass[] clist = new CtClass[list.length()];
/* 1124 */     while (list != null) {
/* 1125 */       clist[(i++)] = this.resolver.lookupClassByName((ASTList)list.head());
/* 1126 */       list = list.tail();
/*      */     }
/*      */ 
/* 1129 */     return clist;
/*      */   }
/*      */ 
/*      */   protected String resolveClassName(ASTList name)
/*      */     throws CompileError
/*      */   {
/* 1139 */     return this.resolver.resolveClassName(name);
/*      */   }
/*      */ 
/*      */   protected String resolveClassName(String jvmName)
/*      */     throws CompileError
/*      */   {
/* 1146 */     return this.resolver.resolveJvmClassName(jvmName);
/*      */   }
/*      */ 
/*      */   static class JsrHook2 extends CodeGen.ReturnHook
/*      */   {
/*      */     int var;
/*      */     int target;
/*      */ 
/*      */     JsrHook2(CodeGen gen, int[] retTarget)
/*      */     {
/*  154 */       super();
/*  155 */       this.target = retTarget[0];
/*  156 */       this.var = retTarget[1];
/*      */     }
/*      */ 
/*      */     protected boolean doit(Bytecode b, int opcode) {
/*  160 */       switch (opcode) {
/*      */       case 177:
/*  162 */         break;
/*      */       case 176:
/*  164 */         b.addAstore(this.var);
/*  165 */         break;
/*      */       case 172:
/*  167 */         b.addIstore(this.var);
/*  168 */         break;
/*      */       case 173:
/*  170 */         b.addLstore(this.var);
/*  171 */         break;
/*      */       case 175:
/*  173 */         b.addDstore(this.var);
/*  174 */         break;
/*      */       case 174:
/*  176 */         b.addFstore(this.var);
/*  177 */         break;
/*      */       default:
/*  179 */         throw new RuntimeException("fatal");
/*      */       }
/*      */ 
/*  182 */       b.addOpcode(167);
/*  183 */       b.addIndex(this.target - b.currentPc() + 3);
/*  184 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class JsrHook extends CodeGen.ReturnHook
/*      */   {
/*      */     ArrayList jsrList;
/*      */     CodeGen cgen;
/*      */     int var;
/*      */ 
/*      */     JsrHook(CodeGen gen)
/*      */     {
/*   90 */       super();
/*   91 */       this.jsrList = new ArrayList();
/*   92 */       this.cgen = gen;
/*   93 */       this.var = -1;
/*      */     }
/*      */ 
/*      */     private int getVar(int size) {
/*   97 */       if (this.var < 0) {
/*   98 */         this.var = this.cgen.getMaxLocals();
/*   99 */         this.cgen.incMaxLocals(size);
/*      */       }
/*      */ 
/*  102 */       return this.var;
/*      */     }
/*      */ 
/*      */     private void jsrJmp(Bytecode b) {
/*  106 */       b.addOpcode(167);
/*  107 */       this.jsrList.add(new int[] { b.currentPc(), this.var });
/*  108 */       b.addIndex(0);
/*      */     }
/*      */ 
/*      */     protected boolean doit(Bytecode b, int opcode) {
/*  112 */       switch (opcode) {
/*      */       case 177:
/*  114 */         jsrJmp(b);
/*  115 */         break;
/*      */       case 176:
/*  117 */         b.addAstore(getVar(1));
/*  118 */         jsrJmp(b);
/*  119 */         b.addAload(this.var);
/*  120 */         break;
/*      */       case 172:
/*  122 */         b.addIstore(getVar(1));
/*  123 */         jsrJmp(b);
/*  124 */         b.addIload(this.var);
/*  125 */         break;
/*      */       case 173:
/*  127 */         b.addLstore(getVar(2));
/*  128 */         jsrJmp(b);
/*  129 */         b.addLload(this.var);
/*  130 */         break;
/*      */       case 175:
/*  132 */         b.addDstore(getVar(2));
/*  133 */         jsrJmp(b);
/*  134 */         b.addDload(this.var);
/*  135 */         break;
/*      */       case 174:
/*  137 */         b.addFstore(getVar(1));
/*  138 */         jsrJmp(b);
/*  139 */         b.addFload(this.var);
/*  140 */         break;
/*      */       default:
/*  142 */         throw new RuntimeException("fatal");
/*      */       }
/*      */ 
/*  145 */       return false;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.MemberCodeGen
 * JD-Core Version:    0.6.2
 */