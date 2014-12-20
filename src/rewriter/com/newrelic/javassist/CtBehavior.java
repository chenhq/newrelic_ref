/*      */ package com.newrelic.javassist;
/*      */ 
/*      */ import com.newrelic.javassist.bytecode.AccessFlag;
/*      */ import com.newrelic.javassist.bytecode.AnnotationsAttribute;
/*      */ import com.newrelic.javassist.bytecode.AttributeInfo;
/*      */ import com.newrelic.javassist.bytecode.BadBytecode;
/*      */ import com.newrelic.javassist.bytecode.Bytecode;
/*      */ import com.newrelic.javassist.bytecode.ClassFile;
/*      */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*      */ import com.newrelic.javassist.bytecode.CodeAttribute.RuntimeCopyException;
/*      */ import com.newrelic.javassist.bytecode.CodeIterator;
/*      */ import com.newrelic.javassist.bytecode.CodeIterator.Gap;
/*      */ import com.newrelic.javassist.bytecode.ConstPool;
/*      */ import com.newrelic.javassist.bytecode.Descriptor;
/*      */ import com.newrelic.javassist.bytecode.ExceptionTable;
/*      */ import com.newrelic.javassist.bytecode.ExceptionsAttribute;
/*      */ import com.newrelic.javassist.bytecode.LineNumberAttribute;
/*      */ import com.newrelic.javassist.bytecode.LineNumberAttribute.Pc;
/*      */ import com.newrelic.javassist.bytecode.LocalVariableAttribute;
/*      */ import com.newrelic.javassist.bytecode.MethodInfo;
/*      */ import com.newrelic.javassist.bytecode.ParameterAnnotationsAttribute;
/*      */ import com.newrelic.javassist.bytecode.StackMap;
/*      */ import com.newrelic.javassist.bytecode.StackMapTable;
/*      */ import com.newrelic.javassist.compiler.CompileError;
/*      */ import com.newrelic.javassist.compiler.Javac;
/*      */ import com.newrelic.javassist.expr.ExprEditor;
/*      */ import java.util.List;
/*      */ 
/*      */ public abstract class CtBehavior extends CtMember
/*      */ {
/*      */   protected MethodInfo methodInfo;
/*      */ 
/*      */   protected CtBehavior(CtClass clazz, MethodInfo minfo)
/*      */   {
/*   33 */     super(clazz);
/*   34 */     this.methodInfo = minfo;
/*      */   }
/*      */ 
/*      */   void copy(CtBehavior src, boolean isCons, ClassMap map)
/*      */     throws CannotCompileException
/*      */   {
/*   43 */     CtClass declaring = this.declaringClass;
/*   44 */     MethodInfo srcInfo = src.methodInfo;
/*   45 */     CtClass srcClass = src.getDeclaringClass();
/*   46 */     ConstPool cp = declaring.getClassFile2().getConstPool();
/*      */ 
/*   48 */     map = new ClassMap(map);
/*   49 */     map.put(srcClass.getName(), declaring.getName());
/*      */     try {
/*   51 */       boolean patch = false;
/*   52 */       CtClass srcSuper = srcClass.getSuperclass();
/*   53 */       CtClass destSuper = declaring.getSuperclass();
/*   54 */       String destSuperName = null;
/*   55 */       if ((srcSuper != null) && (destSuper != null)) {
/*   56 */         String srcSuperName = srcSuper.getName();
/*   57 */         destSuperName = destSuper.getName();
/*   58 */         if (!srcSuperName.equals(destSuperName)) {
/*   59 */           if (srcSuperName.equals("java.lang.Object"))
/*   60 */             patch = true;
/*      */           else {
/*   62 */             map.putIfNone(srcSuperName, destSuperName);
/*      */           }
/*      */         }
/*      */       }
/*   66 */       this.methodInfo = new MethodInfo(cp, srcInfo.getName(), srcInfo, map);
/*   67 */       if ((isCons) && (patch))
/*   68 */         this.methodInfo.setSuperclass(destSuperName);
/*      */     }
/*      */     catch (NotFoundException e) {
/*   71 */       throw new CannotCompileException(e);
/*      */     }
/*      */     catch (BadBytecode e) {
/*   74 */       throw new CannotCompileException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void extendToString(StringBuffer buffer) {
/*   79 */     buffer.append(' ');
/*   80 */     buffer.append(getName());
/*   81 */     buffer.append(' ');
/*   82 */     buffer.append(this.methodInfo.getDescriptor());
/*      */   }
/*      */ 
/*      */   public abstract String getLongName();
/*      */ 
/*      */   public MethodInfo getMethodInfo()
/*      */   {
/*   98 */     this.declaringClass.checkModify();
/*   99 */     return this.methodInfo;
/*      */   }
/*      */ 
/*      */   public MethodInfo getMethodInfo2()
/*      */   {
/*  121 */     return this.methodInfo;
/*      */   }
/*      */ 
/*      */   public int getModifiers()
/*      */   {
/*  131 */     return AccessFlag.toModifier(this.methodInfo.getAccessFlags());
/*      */   }
/*      */ 
/*      */   public void setModifiers(int mod)
/*      */   {
/*  144 */     this.declaringClass.checkModify();
/*  145 */     this.methodInfo.setAccessFlags(AccessFlag.of(mod));
/*      */   }
/*      */ 
/*      */   public boolean hasAnnotation(Class clz)
/*      */   {
/*  157 */     MethodInfo mi = getMethodInfo2();
/*  158 */     AnnotationsAttribute ainfo = (AnnotationsAttribute)mi.getAttribute("RuntimeInvisibleAnnotations");
/*      */ 
/*  160 */     AnnotationsAttribute ainfo2 = (AnnotationsAttribute)mi.getAttribute("RuntimeVisibleAnnotations");
/*      */ 
/*  162 */     return CtClassType.hasAnnotationType(clz, getDeclaringClass().getClassPool(), ainfo, ainfo2);
/*      */   }
/*      */ 
/*      */   public Object getAnnotation(Class clz)
/*      */     throws ClassNotFoundException
/*      */   {
/*  179 */     MethodInfo mi = getMethodInfo2();
/*  180 */     AnnotationsAttribute ainfo = (AnnotationsAttribute)mi.getAttribute("RuntimeInvisibleAnnotations");
/*      */ 
/*  182 */     AnnotationsAttribute ainfo2 = (AnnotationsAttribute)mi.getAttribute("RuntimeVisibleAnnotations");
/*      */ 
/*  184 */     return CtClassType.getAnnotationType(clz, getDeclaringClass().getClassPool(), ainfo, ainfo2);
/*      */   }
/*      */ 
/*      */   public Object[] getAnnotations()
/*      */     throws ClassNotFoundException
/*      */   {
/*  197 */     return getAnnotations(false);
/*      */   }
/*      */ 
/*      */   public Object[] getAvailableAnnotations()
/*      */   {
/*      */     try
/*      */     {
/*  211 */       return getAnnotations(true);
/*      */     }
/*      */     catch (ClassNotFoundException e) {
/*  214 */       throw new RuntimeException("Unexpected exception", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Object[] getAnnotations(boolean ignoreNotFound)
/*      */     throws ClassNotFoundException
/*      */   {
/*  221 */     MethodInfo mi = getMethodInfo2();
/*  222 */     AnnotationsAttribute ainfo = (AnnotationsAttribute)mi.getAttribute("RuntimeInvisibleAnnotations");
/*      */ 
/*  224 */     AnnotationsAttribute ainfo2 = (AnnotationsAttribute)mi.getAttribute("RuntimeVisibleAnnotations");
/*      */ 
/*  226 */     return CtClassType.toAnnotationType(ignoreNotFound, getDeclaringClass().getClassPool(), ainfo, ainfo2);
/*      */   }
/*      */ 
/*      */   public Object[][] getParameterAnnotations()
/*      */     throws ClassNotFoundException
/*      */   {
/*  243 */     return getParameterAnnotations(false);
/*      */   }
/*      */ 
/*      */   public Object[][] getAvailableParameterAnnotations()
/*      */   {
/*      */     try
/*      */     {
/*  261 */       return getParameterAnnotations(true);
/*      */     }
/*      */     catch (ClassNotFoundException e) {
/*  264 */       throw new RuntimeException("Unexpected exception", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   Object[][] getParameterAnnotations(boolean ignoreNotFound)
/*      */     throws ClassNotFoundException
/*      */   {
/*  271 */     MethodInfo mi = getMethodInfo2();
/*  272 */     ParameterAnnotationsAttribute ainfo = (ParameterAnnotationsAttribute)mi.getAttribute("RuntimeInvisibleParameterAnnotations");
/*      */ 
/*  274 */     ParameterAnnotationsAttribute ainfo2 = (ParameterAnnotationsAttribute)mi.getAttribute("RuntimeVisibleParameterAnnotations");
/*      */ 
/*  276 */     return CtClassType.toAnnotationType(ignoreNotFound, getDeclaringClass().getClassPool(), ainfo, ainfo2, mi);
/*      */   }
/*      */ 
/*      */   public CtClass[] getParameterTypes()
/*      */     throws NotFoundException
/*      */   {
/*  285 */     return Descriptor.getParameterTypes(this.methodInfo.getDescriptor(), this.declaringClass.getClassPool());
/*      */   }
/*      */ 
/*      */   CtClass getReturnType0()
/*      */     throws NotFoundException
/*      */   {
/*  293 */     return Descriptor.getReturnType(this.methodInfo.getDescriptor(), this.declaringClass.getClassPool());
/*      */   }
/*      */ 
/*      */   public String getSignature()
/*      */   {
/*  319 */     return this.methodInfo.getDescriptor();
/*      */   }
/*      */ 
/*      */   public CtClass[] getExceptionTypes()
/*      */     throws NotFoundException
/*      */   {
/*  329 */     ExceptionsAttribute ea = this.methodInfo.getExceptionsAttribute();
/*      */     String[] exceptions;
/*      */     String[] exceptions;
/*  330 */     if (ea == null)
/*  331 */       exceptions = null;
/*      */     else {
/*  333 */       exceptions = ea.getExceptions();
/*      */     }
/*  335 */     return this.declaringClass.getClassPool().get(exceptions);
/*      */   }
/*      */ 
/*      */   public void setExceptionTypes(CtClass[] types)
/*      */     throws NotFoundException
/*      */   {
/*  342 */     this.declaringClass.checkModify();
/*  343 */     if ((types == null) || (types.length == 0)) {
/*  344 */       this.methodInfo.removeExceptionsAttribute();
/*  345 */       return;
/*      */     }
/*      */ 
/*  348 */     String[] names = new String[types.length];
/*  349 */     for (int i = 0; i < types.length; i++) {
/*  350 */       names[i] = types[i].getName();
/*      */     }
/*  352 */     ExceptionsAttribute ea = this.methodInfo.getExceptionsAttribute();
/*  353 */     if (ea == null) {
/*  354 */       ea = new ExceptionsAttribute(this.methodInfo.getConstPool());
/*  355 */       this.methodInfo.setExceptionsAttribute(ea);
/*      */     }
/*      */ 
/*  358 */     ea.setExceptions(names);
/*      */   }
/*      */ 
/*      */   public abstract boolean isEmpty();
/*      */ 
/*      */   public void setBody(String src)
/*      */     throws CannotCompileException
/*      */   {
/*  375 */     setBody(src, null, null);
/*      */   }
/*      */ 
/*      */   public void setBody(String src, String delegateObj, String delegateMethod)
/*      */     throws CannotCompileException
/*      */   {
/*  394 */     CtClass cc = this.declaringClass;
/*  395 */     cc.checkModify();
/*      */     try {
/*  397 */       Javac jv = new Javac(cc);
/*  398 */       if (delegateMethod != null) {
/*  399 */         jv.recordProceed(delegateObj, delegateMethod);
/*      */       }
/*  401 */       Bytecode b = jv.compileBody(this, src);
/*  402 */       this.methodInfo.setCodeAttribute(b.toCodeAttribute());
/*  403 */       this.methodInfo.setAccessFlags(this.methodInfo.getAccessFlags() & 0xFFFFFBFF);
/*      */ 
/*  405 */       this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
/*  406 */       this.declaringClass.rebuildClassFile();
/*      */     }
/*      */     catch (CompileError e) {
/*  409 */       throw new CannotCompileException(e);
/*      */     } catch (BadBytecode e) {
/*  411 */       throw new CannotCompileException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   static void setBody0(CtClass srcClass, MethodInfo srcInfo, CtClass destClass, MethodInfo destInfo, ClassMap map)
/*      */     throws CannotCompileException
/*      */   {
/*  420 */     destClass.checkModify();
/*      */ 
/*  422 */     map = new ClassMap(map);
/*  423 */     map.put(srcClass.getName(), destClass.getName());
/*      */     try {
/*  425 */       CodeAttribute cattr = srcInfo.getCodeAttribute();
/*  426 */       if (cattr != null) {
/*  427 */         ConstPool cp = destInfo.getConstPool();
/*  428 */         CodeAttribute ca = (CodeAttribute)cattr.copy(cp, map);
/*  429 */         destInfo.setCodeAttribute(ca);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (CodeAttribute.RuntimeCopyException e)
/*      */     {
/*  436 */       throw new CannotCompileException(e);
/*      */     }
/*      */ 
/*  439 */     destInfo.setAccessFlags(destInfo.getAccessFlags() & 0xFFFFFBFF);
/*      */ 
/*  441 */     destClass.rebuildClassFile();
/*      */   }
/*      */ 
/*      */   public byte[] getAttribute(String name)
/*      */   {
/*  456 */     AttributeInfo ai = this.methodInfo.getAttribute(name);
/*  457 */     if (ai == null) {
/*  458 */       return null;
/*      */     }
/*  460 */     return ai.get();
/*      */   }
/*      */ 
/*      */   public void setAttribute(String name, byte[] data)
/*      */   {
/*  474 */     this.declaringClass.checkModify();
/*  475 */     this.methodInfo.addAttribute(new AttributeInfo(this.methodInfo.getConstPool(), name, data));
/*      */   }
/*      */ 
/*      */   public void useCflow(String name)
/*      */     throws CannotCompileException
/*      */   {
/*  497 */     CtClass cc = this.declaringClass;
/*  498 */     cc.checkModify();
/*  499 */     ClassPool pool = cc.getClassPool();
/*      */ 
/*  501 */     int i = 0;
/*      */     while (true) {
/*  503 */       String fname = "_cflow$" + i++;
/*      */       try {
/*  505 */         cc.getDeclaredField(fname);
/*      */       }
/*      */       catch (NotFoundException e)
/*      */       {
/*  512 */         pool.recordCflow(name, this.declaringClass.getName(), fname);
/*      */         try {
/*  514 */           CtClass type = pool.get("com.newrelic.javassist.runtime.Cflow");
/*  515 */           CtField field = new CtField(type, fname, cc);
/*  516 */           field.setModifiers(9);
/*  517 */           cc.addField(field, CtField.Initializer.byNew(type));
/*  518 */           insertBefore(fname + ".enter();", false);
/*  519 */           String src = fname + ".exit();";
/*  520 */           insertAfter(src, true);
/*      */         }
/*      */         catch (NotFoundException e) {
/*  523 */           throw new CannotCompileException(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addLocalVariable(String name, CtClass type)
/*      */     throws CannotCompileException
/*      */   {
/*  545 */     this.declaringClass.checkModify();
/*  546 */     ConstPool cp = this.methodInfo.getConstPool();
/*  547 */     CodeAttribute ca = this.methodInfo.getCodeAttribute();
/*  548 */     if (ca == null) {
/*  549 */       throw new CannotCompileException("no method body");
/*      */     }
/*  551 */     LocalVariableAttribute va = (LocalVariableAttribute)ca.getAttribute("LocalVariableTable");
/*      */ 
/*  553 */     if (va == null) {
/*  554 */       va = new LocalVariableAttribute(cp);
/*  555 */       ca.getAttributes().add(va);
/*      */     }
/*      */ 
/*  558 */     int maxLocals = ca.getMaxLocals();
/*  559 */     String desc = Descriptor.of(type);
/*  560 */     va.addEntry(0, ca.getCodeLength(), cp.addUtf8Info(name), cp.addUtf8Info(desc), maxLocals);
/*      */ 
/*  562 */     ca.setMaxLocals(maxLocals + Descriptor.dataSize(desc));
/*      */   }
/*      */ 
/*      */   public void insertParameter(CtClass type)
/*      */     throws CannotCompileException
/*      */   {
/*  571 */     this.declaringClass.checkModify();
/*  572 */     String desc = this.methodInfo.getDescriptor();
/*  573 */     String desc2 = Descriptor.insertParameter(type, desc);
/*      */     try {
/*  575 */       addParameter2(Modifier.isStatic(getModifiers()) ? 0 : 1, type, desc);
/*      */     }
/*      */     catch (BadBytecode e) {
/*  578 */       throw new CannotCompileException(e);
/*      */     }
/*      */ 
/*  581 */     this.methodInfo.setDescriptor(desc2);
/*      */   }
/*      */ 
/*      */   public void addParameter(CtClass type)
/*      */     throws CannotCompileException
/*      */   {
/*  590 */     this.declaringClass.checkModify();
/*  591 */     String desc = this.methodInfo.getDescriptor();
/*  592 */     String desc2 = Descriptor.appendParameter(type, desc);
/*  593 */     int offset = Modifier.isStatic(getModifiers()) ? 0 : 1;
/*      */     try {
/*  595 */       addParameter2(offset + Descriptor.paramSize(desc), type, desc);
/*      */     }
/*      */     catch (BadBytecode e) {
/*  598 */       throw new CannotCompileException(e);
/*      */     }
/*      */ 
/*  601 */     this.methodInfo.setDescriptor(desc2);
/*      */   }
/*      */ 
/*      */   private void addParameter2(int where, CtClass type, String desc)
/*      */     throws BadBytecode
/*      */   {
/*  607 */     CodeAttribute ca = this.methodInfo.getCodeAttribute();
/*  608 */     if (ca != null) {
/*  609 */       int size = 1;
/*  610 */       char typeDesc = 'L';
/*  611 */       int classInfo = 0;
/*  612 */       if (type.isPrimitive()) {
/*  613 */         CtPrimitiveType cpt = (CtPrimitiveType)type;
/*  614 */         size = cpt.getDataSize();
/*  615 */         typeDesc = cpt.getDescriptor();
/*      */       }
/*      */       else {
/*  618 */         classInfo = this.methodInfo.getConstPool().addClassInfo(type);
/*      */       }
/*  620 */       ca.insertLocalVar(where, size);
/*  621 */       LocalVariableAttribute va = (LocalVariableAttribute)ca.getAttribute("LocalVariableTable");
/*      */ 
/*  624 */       if (va != null) {
/*  625 */         va.shiftIndex(where, size);
/*      */       }
/*  627 */       StackMapTable smt = (StackMapTable)ca.getAttribute("StackMapTable");
/*  628 */       if (smt != null) {
/*  629 */         smt.insertLocal(where, StackMapTable.typeTagOf(typeDesc), classInfo);
/*      */       }
/*  631 */       StackMap sm = (StackMap)ca.getAttribute("StackMap");
/*  632 */       if (sm != null)
/*  633 */         sm.insertLocal(where, StackMapTable.typeTagOf(typeDesc), classInfo);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void instrument(CodeConverter converter)
/*      */     throws CannotCompileException
/*      */   {
/*  645 */     this.declaringClass.checkModify();
/*  646 */     ConstPool cp = this.methodInfo.getConstPool();
/*  647 */     converter.doit(getDeclaringClass(), this.methodInfo, cp);
/*      */   }
/*      */ 
/*      */   public void instrument(ExprEditor editor)
/*      */     throws CannotCompileException
/*      */   {
/*  660 */     if (this.declaringClass.isFrozen()) {
/*  661 */       this.declaringClass.checkModify();
/*      */     }
/*  663 */     if (editor.doit(this.declaringClass, this.methodInfo))
/*  664 */       this.declaringClass.checkModify();
/*      */   }
/*      */ 
/*      */   public void insertBefore(String src)
/*      */     throws CannotCompileException
/*      */   {
/*  685 */     insertBefore(src, true);
/*      */   }
/*      */ 
/*      */   private void insertBefore(String src, boolean rebuild)
/*      */     throws CannotCompileException
/*      */   {
/*  691 */     CtClass cc = this.declaringClass;
/*  692 */     cc.checkModify();
/*  693 */     CodeAttribute ca = this.methodInfo.getCodeAttribute();
/*  694 */     if (ca == null) {
/*  695 */       throw new CannotCompileException("no method body");
/*      */     }
/*  697 */     CodeIterator iterator = ca.iterator();
/*  698 */     Javac jv = new Javac(cc);
/*      */     try {
/*  700 */       int nvars = jv.recordParams(getParameterTypes(), Modifier.isStatic(getModifiers()));
/*      */ 
/*  702 */       jv.recordParamNames(ca, nvars);
/*  703 */       jv.recordLocalVariables(ca, 0);
/*  704 */       jv.recordType(getReturnType0());
/*  705 */       jv.compileStmnt(src);
/*  706 */       Bytecode b = jv.getBytecode();
/*  707 */       int stack = b.getMaxStack();
/*  708 */       int locals = b.getMaxLocals();
/*      */ 
/*  710 */       if (stack > ca.getMaxStack()) {
/*  711 */         ca.setMaxStack(stack);
/*      */       }
/*  713 */       if (locals > ca.getMaxLocals()) {
/*  714 */         ca.setMaxLocals(locals);
/*      */       }
/*  716 */       int pos = iterator.insertEx(b.get());
/*  717 */       iterator.insert(b.getExceptionTable(), pos);
/*  718 */       if (rebuild)
/*  719 */         this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
/*      */     }
/*      */     catch (NotFoundException e) {
/*  722 */       throw new CannotCompileException(e);
/*      */     }
/*      */     catch (CompileError e) {
/*  725 */       throw new CannotCompileException(e);
/*      */     }
/*      */     catch (BadBytecode e) {
/*  728 */       throw new CannotCompileException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void insertAfter(String src)
/*      */     throws CannotCompileException
/*      */   {
/*  743 */     insertAfter(src, false);
/*      */   }
/*      */ 
/*      */   public void insertAfter(String src, boolean asFinally)
/*      */     throws CannotCompileException
/*      */   {
/*  761 */     CtClass cc = this.declaringClass;
/*  762 */     cc.checkModify();
/*  763 */     ConstPool pool = this.methodInfo.getConstPool();
/*  764 */     CodeAttribute ca = this.methodInfo.getCodeAttribute();
/*  765 */     if (ca == null) {
/*  766 */       throw new CannotCompileException("no method body");
/*      */     }
/*  768 */     CodeIterator iterator = ca.iterator();
/*  769 */     int retAddr = ca.getMaxLocals();
/*  770 */     Bytecode b = new Bytecode(pool, 0, retAddr + 1);
/*  771 */     b.setStackDepth(ca.getMaxStack() + 1);
/*  772 */     Javac jv = new Javac(b, cc);
/*      */     try {
/*  774 */       int nvars = jv.recordParams(getParameterTypes(), Modifier.isStatic(getModifiers()));
/*      */ 
/*  776 */       jv.recordParamNames(ca, nvars);
/*  777 */       CtClass rtype = getReturnType0();
/*  778 */       int varNo = jv.recordReturnType(rtype, true);
/*  779 */       jv.recordLocalVariables(ca, 0);
/*      */ 
/*  782 */       int handlerLen = insertAfterHandler(asFinally, b, rtype, varNo, jv, src);
/*      */ 
/*  785 */       insertAfterAdvice(b, jv, src, pool, rtype, varNo);
/*      */ 
/*  787 */       ca.setMaxStack(b.getMaxStack());
/*  788 */       ca.setMaxLocals(b.getMaxLocals());
/*      */ 
/*  790 */       int gapPos = iterator.append(b.get());
/*  791 */       iterator.append(b.getExceptionTable(), gapPos);
/*      */ 
/*  793 */       if (asFinally) {
/*  794 */         ca.getExceptionTable().add(getStartPosOfBody(ca), gapPos, gapPos, 0);
/*      */       }
/*  796 */       int gapLen = iterator.getCodeLength() - gapPos - handlerLen;
/*  797 */       int subr = iterator.getCodeLength() - gapLen;
/*      */ 
/*  799 */       while (iterator.hasNext()) {
/*  800 */         int pos = iterator.next();
/*  801 */         if (pos >= subr) {
/*      */           break;
/*      */         }
/*  804 */         int c = iterator.byteAt(pos);
/*  805 */         if ((c == 176) || (c == 172) || (c == 174) || (c == 173) || (c == 175) || (c == 177))
/*      */         {
/*  808 */           insertGoto(iterator, subr, pos);
/*  809 */           subr = iterator.getCodeLength() - gapLen;
/*      */         }
/*      */       }
/*      */ 
/*  813 */       this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
/*      */     }
/*      */     catch (NotFoundException e) {
/*  816 */       throw new CannotCompileException(e);
/*      */     }
/*      */     catch (CompileError e) {
/*  819 */       throw new CannotCompileException(e);
/*      */     }
/*      */     catch (BadBytecode e) {
/*  822 */       throw new CannotCompileException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void insertAfterAdvice(Bytecode code, Javac jv, String src, ConstPool cp, CtClass rtype, int varNo)
/*      */     throws CompileError
/*      */   {
/*  830 */     if (rtype == CtClass.voidType) {
/*  831 */       code.addOpcode(1);
/*  832 */       code.addAstore(varNo);
/*  833 */       jv.compileStmnt(src);
/*  834 */       code.addOpcode(177);
/*  835 */       if (code.getMaxLocals() < 1)
/*  836 */         code.setMaxLocals(1);
/*      */     }
/*      */     else {
/*  839 */       code.addStore(varNo, rtype);
/*  840 */       jv.compileStmnt(src);
/*  841 */       code.addLoad(varNo, rtype);
/*  842 */       if (rtype.isPrimitive())
/*  843 */         code.addOpcode(((CtPrimitiveType)rtype).getReturnOp());
/*      */       else
/*  845 */         code.addOpcode(176);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void insertGoto(CodeIterator iterator, int subr, int pos)
/*      */     throws BadBytecode
/*      */   {
/*  855 */     iterator.setMark(subr);
/*      */ 
/*  857 */     iterator.writeByte(0, pos);
/*  858 */     boolean wide = subr + 2 - pos > 32767;
/*  859 */     pos = iterator.insertGapAt(pos, wide ? 4 : 2, false).position;
/*  860 */     int offset = iterator.getMark() - pos;
/*  861 */     if (wide) {
/*  862 */       iterator.writeByte(200, pos);
/*  863 */       iterator.write32bit(offset, pos + 1);
/*      */     }
/*  865 */     else if (offset <= 32767) {
/*  866 */       iterator.writeByte(167, pos);
/*  867 */       iterator.write16bit(offset, pos + 1);
/*      */     }
/*      */     else {
/*  870 */       pos = iterator.insertGapAt(pos, 2, false).position;
/*  871 */       iterator.writeByte(200, pos);
/*  872 */       iterator.write32bit(iterator.getMark() - pos, pos + 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private int insertAfterHandler(boolean asFinally, Bytecode b, CtClass rtype, int returnVarNo, Javac javac, String src)
/*      */     throws CompileError
/*      */   {
/*  883 */     if (!asFinally) {
/*  884 */       return 0;
/*      */     }
/*  886 */     int var = b.getMaxLocals();
/*  887 */     b.incMaxLocals(1);
/*  888 */     int pc = b.currentPc();
/*  889 */     b.addAstore(var);
/*  890 */     if (rtype.isPrimitive()) {
/*  891 */       char c = ((CtPrimitiveType)rtype).getDescriptor();
/*  892 */       if (c == 'D') {
/*  893 */         b.addDconst(0.0D);
/*  894 */         b.addDstore(returnVarNo);
/*      */       }
/*  896 */       else if (c == 'F') {
/*  897 */         b.addFconst(0.0F);
/*  898 */         b.addFstore(returnVarNo);
/*      */       }
/*  900 */       else if (c == 'J') {
/*  901 */         b.addLconst(0L);
/*  902 */         b.addLstore(returnVarNo);
/*      */       }
/*  904 */       else if (c == 'V') {
/*  905 */         b.addOpcode(1);
/*  906 */         b.addAstore(returnVarNo);
/*      */       }
/*      */       else {
/*  909 */         b.addIconst(0);
/*  910 */         b.addIstore(returnVarNo);
/*      */       }
/*      */     }
/*      */     else {
/*  914 */       b.addOpcode(1);
/*  915 */       b.addAstore(returnVarNo);
/*      */     }
/*      */ 
/*  918 */     javac.compileStmnt(src);
/*  919 */     b.addAload(var);
/*  920 */     b.addOpcode(191);
/*  921 */     return b.currentPc() - pc;
/*      */   }
/*      */ 
/*      */   public void addCatch(String src, CtClass exceptionType)
/*      */     throws CannotCompileException
/*      */   {
/*  987 */     addCatch(src, exceptionType, "$e");
/*      */   }
/*      */ 
/*      */   public void addCatch(String src, CtClass exceptionType, String exceptionName)
/*      */     throws CannotCompileException
/*      */   {
/* 1006 */     CtClass cc = this.declaringClass;
/* 1007 */     cc.checkModify();
/* 1008 */     ConstPool cp = this.methodInfo.getConstPool();
/* 1009 */     CodeAttribute ca = this.methodInfo.getCodeAttribute();
/* 1010 */     CodeIterator iterator = ca.iterator();
/* 1011 */     Bytecode b = new Bytecode(cp, ca.getMaxStack(), ca.getMaxLocals());
/* 1012 */     b.setStackDepth(1);
/* 1013 */     Javac jv = new Javac(b, cc);
/*      */     try {
/* 1015 */       jv.recordParams(getParameterTypes(), Modifier.isStatic(getModifiers()));
/*      */ 
/* 1017 */       int var = jv.recordVariable(exceptionType, exceptionName);
/* 1018 */       b.addAstore(var);
/* 1019 */       jv.compileStmnt(src);
/*      */ 
/* 1021 */       int stack = b.getMaxStack();
/* 1022 */       int locals = b.getMaxLocals();
/*      */ 
/* 1024 */       if (stack > ca.getMaxStack()) {
/* 1025 */         ca.setMaxStack(stack);
/*      */       }
/* 1027 */       if (locals > ca.getMaxLocals()) {
/* 1028 */         ca.setMaxLocals(locals);
/*      */       }
/* 1030 */       int len = iterator.getCodeLength();
/* 1031 */       int pos = iterator.append(b.get());
/* 1032 */       ca.getExceptionTable().add(getStartPosOfBody(ca), len, len, cp.addClassInfo(exceptionType));
/*      */ 
/* 1034 */       iterator.append(b.getExceptionTable(), pos);
/* 1035 */       this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
/*      */     }
/*      */     catch (NotFoundException e) {
/* 1038 */       throw new CannotCompileException(e);
/*      */     }
/*      */     catch (CompileError e) {
/* 1041 */       throw new CannotCompileException(e);
/*      */     } catch (BadBytecode e) {
/* 1043 */       throw new CannotCompileException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   int getStartPosOfBody(CodeAttribute ca)
/*      */     throws CannotCompileException
/*      */   {
/* 1050 */     return 0;
/*      */   }
/*      */ 
/*      */   public int insertAt(int lineNum, String src)
/*      */     throws CannotCompileException
/*      */   {
/* 1073 */     return insertAt(lineNum, true, src);
/*      */   }
/*      */ 
/*      */   public int insertAt(int lineNum, boolean modify, String src)
/*      */     throws CannotCompileException
/*      */   {
/* 1101 */     CodeAttribute ca = this.methodInfo.getCodeAttribute();
/* 1102 */     if (ca == null) {
/* 1103 */       throw new CannotCompileException("no method body");
/*      */     }
/* 1105 */     LineNumberAttribute ainfo = (LineNumberAttribute)ca.getAttribute("LineNumberTable");
/*      */ 
/* 1107 */     if (ainfo == null) {
/* 1108 */       throw new CannotCompileException("no line number info");
/*      */     }
/* 1110 */     LineNumberAttribute.Pc pc = ainfo.toNearPc(lineNum);
/* 1111 */     lineNum = pc.line;
/* 1112 */     int index = pc.index;
/* 1113 */     if (!modify) {
/* 1114 */       return lineNum;
/*      */     }
/* 1116 */     CtClass cc = this.declaringClass;
/* 1117 */     cc.checkModify();
/* 1118 */     CodeIterator iterator = ca.iterator();
/* 1119 */     Javac jv = new Javac(cc);
/*      */     try {
/* 1121 */       jv.recordLocalVariables(ca, index);
/* 1122 */       jv.recordParams(getParameterTypes(), Modifier.isStatic(getModifiers()));
/*      */ 
/* 1124 */       jv.setMaxLocals(ca.getMaxLocals());
/* 1125 */       jv.compileStmnt(src);
/* 1126 */       Bytecode b = jv.getBytecode();
/* 1127 */       int locals = b.getMaxLocals();
/* 1128 */       int stack = b.getMaxStack();
/* 1129 */       ca.setMaxLocals(locals);
/*      */ 
/* 1134 */       if (stack > ca.getMaxStack()) {
/* 1135 */         ca.setMaxStack(stack);
/*      */       }
/* 1137 */       index = iterator.insertAt(index, b.get());
/* 1138 */       iterator.insert(b.getExceptionTable(), index);
/* 1139 */       this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
/* 1140 */       return lineNum;
/*      */     }
/*      */     catch (NotFoundException e) {
/* 1143 */       throw new CannotCompileException(e);
/*      */     }
/*      */     catch (CompileError e) {
/* 1146 */       throw new CannotCompileException(e);
/*      */     }
/*      */     catch (BadBytecode e) {
/* 1149 */       throw new CannotCompileException(e);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtBehavior
 * JD-Core Version:    0.6.2
 */