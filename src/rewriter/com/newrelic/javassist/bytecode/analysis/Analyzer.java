/*     */ package com.newrelic.javassist.bytecode.analysis;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtMethod;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import com.newrelic.javassist.bytecode.ExceptionTable;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.bytecode.Opcode;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ public class Analyzer
/*     */   implements Opcode
/*     */ {
/*     */   private final SubroutineScanner scanner;
/*     */   private CtClass clazz;
/*     */   private ExceptionInfo[] exceptions;
/*     */   private Frame[] frames;
/*     */   private Subroutine[] subroutines;
/*     */ 
/*     */   public Analyzer()
/*     */   {
/*  86 */     this.scanner = new SubroutineScanner();
/*     */   }
/*     */ 
/*     */   public Frame[] analyze(CtClass clazz, MethodInfo method)
/*     */     throws BadBytecode
/*     */   {
/* 122 */     this.clazz = clazz;
/* 123 */     CodeAttribute codeAttribute = method.getCodeAttribute();
/*     */ 
/* 125 */     if (codeAttribute == null) {
/* 126 */       return null;
/*     */     }
/* 128 */     int maxLocals = codeAttribute.getMaxLocals();
/* 129 */     int maxStack = codeAttribute.getMaxStack();
/* 130 */     int codeLength = codeAttribute.getCodeLength();
/*     */ 
/* 132 */     CodeIterator iter = codeAttribute.iterator();
/* 133 */     IntQueue queue = new IntQueue();
/*     */ 
/* 135 */     this.exceptions = buildExceptionInfo(method);
/* 136 */     this.subroutines = this.scanner.scan(method);
/*     */ 
/* 138 */     Executor executor = new Executor(clazz.getClassPool(), method.getConstPool());
/* 139 */     this.frames = new Frame[codeLength];
/* 140 */     this.frames[iter.lookAhead()] = firstFrame(method, maxLocals, maxStack);
/* 141 */     queue.add(iter.next());
/* 142 */     while (!queue.isEmpty()) {
/* 143 */       analyzeNextEntry(method, iter, queue, executor);
/*     */     }
/*     */ 
/* 146 */     return this.frames;
/*     */   }
/*     */ 
/*     */   public Frame[] analyze(CtMethod method)
/*     */     throws BadBytecode
/*     */   {
/* 164 */     return analyze(method.getDeclaringClass(), method.getMethodInfo2());
/*     */   }
/*     */ 
/*     */   private void analyzeNextEntry(MethodInfo method, CodeIterator iter, IntQueue queue, Executor executor) throws BadBytecode
/*     */   {
/* 169 */     int pos = queue.take();
/* 170 */     iter.move(pos);
/* 171 */     iter.next();
/*     */ 
/* 173 */     Frame frame = this.frames[pos].copy();
/* 174 */     Subroutine subroutine = this.subroutines[pos];
/*     */     try
/*     */     {
/* 177 */       executor.execute(method, pos, iter, frame, subroutine);
/*     */     } catch (RuntimeException e) {
/* 179 */       throw new BadBytecode(e.getMessage() + "[pos = " + pos + "]", e);
/*     */     }
/*     */ 
/* 182 */     int opcode = iter.byteAt(pos);
/*     */ 
/* 184 */     if (opcode == 170) {
/* 185 */       mergeTableSwitch(queue, pos, iter, frame);
/* 186 */     } else if (opcode == 171) {
/* 187 */       mergeLookupSwitch(queue, pos, iter, frame);
/* 188 */     } else if (opcode == 169) {
/* 189 */       mergeRet(queue, iter, pos, frame, subroutine);
/* 190 */     } else if (Util.isJumpInstruction(opcode)) {
/* 191 */       int target = Util.getJumpTarget(pos, iter);
/*     */ 
/* 193 */       if (Util.isJsr(opcode))
/*     */       {
/* 195 */         mergeJsr(queue, this.frames[pos], this.subroutines[target], pos, lookAhead(iter, pos));
/* 196 */       } else if (!Util.isGoto(opcode)) {
/* 197 */         merge(queue, frame, lookAhead(iter, pos));
/*     */       }
/*     */ 
/* 200 */       merge(queue, frame, target);
/* 201 */     } else if ((opcode != 191) && (!Util.isReturn(opcode)))
/*     */     {
/* 203 */       merge(queue, frame, lookAhead(iter, pos));
/*     */     }
/*     */ 
/* 209 */     mergeExceptionHandlers(queue, method, pos, frame);
/*     */   }
/*     */ 
/*     */   private ExceptionInfo[] buildExceptionInfo(MethodInfo method) {
/* 213 */     ConstPool constPool = method.getConstPool();
/* 214 */     ClassPool classes = this.clazz.getClassPool();
/*     */ 
/* 216 */     ExceptionTable table = method.getCodeAttribute().getExceptionTable();
/* 217 */     ExceptionInfo[] exceptions = new ExceptionInfo[table.size()];
/* 218 */     for (int i = 0; i < table.size(); i++) { int index = table.catchType(i);
/*     */       Type type;
/*     */       try {
/* 222 */         type = index == 0 ? Type.THROWABLE : Type.get(classes.get(constPool.getClassInfo(index)));
/*     */       } catch (NotFoundException e) {
/* 224 */         throw new IllegalStateException(e.getMessage());
/*     */       }
/*     */ 
/* 227 */       exceptions[i] = new ExceptionInfo(table.startPc(i), table.endPc(i), table.handlerPc(i), type, null);
/*     */     }
/*     */ 
/* 230 */     return exceptions;
/*     */   }
/*     */ 
/*     */   private Frame firstFrame(MethodInfo method, int maxLocals, int maxStack) {
/* 234 */     int pos = 0;
/*     */ 
/* 236 */     Frame first = new Frame(maxLocals, maxStack);
/* 237 */     if ((method.getAccessFlags() & 0x8) == 0) {
/* 238 */       first.setLocal(pos++, Type.get(this.clazz));
/*     */     }
/*     */     CtClass[] parameters;
/*     */     try
/*     */     {
/* 243 */       parameters = Descriptor.getParameterTypes(method.getDescriptor(), this.clazz.getClassPool());
/*     */     } catch (NotFoundException e) {
/* 245 */       throw new RuntimeException(e);
/*     */     }
/*     */ 
/* 248 */     for (int i = 0; i < parameters.length; i++) {
/* 249 */       Type type = zeroExtend(Type.get(parameters[i]));
/* 250 */       first.setLocal(pos++, type);
/* 251 */       if (type.getSize() == 2) {
/* 252 */         first.setLocal(pos++, Type.TOP);
/*     */       }
/*     */     }
/* 255 */     return first;
/*     */   }
/*     */ 
/*     */   private int getNext(CodeIterator iter, int of, int restore) throws BadBytecode {
/* 259 */     iter.move(of);
/* 260 */     iter.next();
/* 261 */     int next = iter.lookAhead();
/* 262 */     iter.move(restore);
/* 263 */     iter.next();
/*     */ 
/* 265 */     return next;
/*     */   }
/*     */ 
/*     */   private int lookAhead(CodeIterator iter, int pos) throws BadBytecode {
/* 269 */     if (!iter.hasNext()) {
/* 270 */       throw new BadBytecode("Execution falls off end! [pos = " + pos + "]");
/*     */     }
/* 272 */     return iter.lookAhead();
/*     */   }
/*     */ 
/*     */   private void merge(IntQueue queue, Frame frame, int target)
/*     */   {
/* 277 */     Frame old = this.frames[target];
/*     */     boolean changed;
/*     */     boolean changed;
/* 280 */     if (old == null) {
/* 281 */       this.frames[target] = frame.copy();
/* 282 */       changed = true;
/*     */     } else {
/* 284 */       changed = old.merge(frame);
/*     */     }
/*     */ 
/* 287 */     if (changed)
/* 288 */       queue.add(target);
/*     */   }
/*     */ 
/*     */   private void mergeExceptionHandlers(IntQueue queue, MethodInfo method, int pos, Frame frame)
/*     */   {
/* 293 */     for (int i = 0; i < this.exceptions.length; i++) {
/* 294 */       ExceptionInfo exception = this.exceptions[i];
/*     */ 
/* 297 */       if ((pos >= exception.start) && (pos < exception.end)) {
/* 298 */         Frame newFrame = frame.copy();
/* 299 */         newFrame.clearStack();
/* 300 */         newFrame.push(exception.type);
/* 301 */         merge(queue, newFrame, exception.handler);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void mergeJsr(IntQueue queue, Frame frame, Subroutine sub, int pos, int next) throws BadBytecode {
/* 307 */     if (sub == null) {
/* 308 */       throw new BadBytecode("No subroutine at jsr target! [pos = " + pos + "]");
/*     */     }
/* 310 */     Frame old = this.frames[next];
/* 311 */     boolean changed = false;
/*     */ 
/* 313 */     if (old == null) {
/* 314 */       old = this.frames[next] =  = frame.copy();
/* 315 */       changed = true;
/*     */     } else {
/* 317 */       for (int i = 0; i < frame.localsLength(); i++)
/*     */       {
/* 319 */         if (!sub.isAccessed(i)) {
/* 320 */           Type oldType = old.getLocal(i);
/* 321 */           Type newType = frame.getLocal(i);
/* 322 */           if (oldType == null) {
/* 323 */             old.setLocal(i, newType);
/* 324 */             changed = true;
/*     */           }
/*     */           else
/*     */           {
/* 328 */             newType = oldType.merge(newType);
/*     */ 
/* 330 */             old.setLocal(i, newType);
/* 331 */             if ((!newType.equals(oldType)) || (newType.popChanged()))
/* 332 */               changed = true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 337 */     if (!old.isJsrMerged()) {
/* 338 */       old.setJsrMerged(true);
/* 339 */       changed = true;
/*     */     }
/*     */ 
/* 342 */     if ((changed) && (old.isRetMerged()))
/* 343 */       queue.add(next);
/*     */   }
/*     */ 
/*     */   private void mergeLookupSwitch(IntQueue queue, int pos, CodeIterator iter, Frame frame) throws BadBytecode
/*     */   {
/* 348 */     int index = (pos & 0xFFFFFFFC) + 4;
/*     */ 
/* 350 */     merge(queue, frame, pos + iter.s32bitAt(index));
/* 351 */     index += 4; int npairs = iter.s32bitAt(index);
/* 352 */     index += 4; int end = npairs * 8 + index;
/*     */ 
/* 355 */     for (index += 4; index < end; index += 8) {
/* 356 */       int target = iter.s32bitAt(index) + pos;
/* 357 */       merge(queue, frame, target);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void mergeRet(IntQueue queue, CodeIterator iter, int pos, Frame frame, Subroutine subroutine) throws BadBytecode {
/* 362 */     if (subroutine == null) {
/* 363 */       throw new BadBytecode("Ret on no subroutine! [pos = " + pos + "]");
/*     */     }
/* 365 */     Iterator callerIter = subroutine.callers().iterator();
/* 366 */     while (callerIter.hasNext()) {
/* 367 */       int caller = ((Integer)callerIter.next()).intValue();
/* 368 */       int returnLoc = getNext(iter, caller, pos);
/* 369 */       boolean changed = false;
/*     */ 
/* 371 */       Frame old = this.frames[returnLoc];
/* 372 */       if (old == null) {
/* 373 */         old = this.frames[returnLoc] =  = frame.copyStack();
/* 374 */         changed = true;
/*     */       } else {
/* 376 */         changed = old.mergeStack(frame);
/*     */       }
/*     */ 
/* 379 */       for (Iterator i = subroutine.accessed().iterator(); i.hasNext(); ) {
/* 380 */         int index = ((Integer)i.next()).intValue();
/* 381 */         Type oldType = old.getLocal(index);
/* 382 */         Type newType = frame.getLocal(index);
/* 383 */         if (oldType != newType) {
/* 384 */           old.setLocal(index, newType);
/* 385 */           changed = true;
/*     */         }
/*     */       }
/*     */ 
/* 389 */       if (!old.isRetMerged()) {
/* 390 */         old.setRetMerged(true);
/* 391 */         changed = true;
/*     */       }
/*     */ 
/* 394 */       if ((changed) && (old.isJsrMerged()))
/* 395 */         queue.add(returnLoc);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void mergeTableSwitch(IntQueue queue, int pos, CodeIterator iter, Frame frame)
/*     */     throws BadBytecode
/*     */   {
/* 402 */     int index = (pos & 0xFFFFFFFC) + 4;
/*     */ 
/* 404 */     merge(queue, frame, pos + iter.s32bitAt(index));
/* 405 */     index += 4; int low = iter.s32bitAt(index);
/* 406 */     index += 4; int high = iter.s32bitAt(index);
/* 407 */     index += 4; int end = (high - low + 1) * 4 + index;
/*     */ 
/* 410 */     for (; index < end; index += 4) {
/* 411 */       int target = iter.s32bitAt(index) + pos;
/* 412 */       merge(queue, frame, target);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Type zeroExtend(Type type) {
/* 417 */     if ((type == Type.SHORT) || (type == Type.BYTE) || (type == Type.CHAR) || (type == Type.BOOLEAN)) {
/* 418 */       return Type.INTEGER;
/*     */     }
/* 420 */     return type;
/*     */   }
/*     */ 
/*     */   private static class ExceptionInfo
/*     */   {
/*     */     private int end;
/*     */     private int handler;
/*     */     private int start;
/*     */     private Type type;
/*     */ 
/*     */     private ExceptionInfo(int start, int end, int handler, Type type)
/*     */     {
/*  99 */       this.start = start;
/* 100 */       this.end = end;
/* 101 */       this.handler = handler;
/* 102 */       this.type = type;
/*     */     }
/*     */ 
/*     */     ExceptionInfo(int x0, int x1, int x2, Type x3, Analyzer.1 x4)
/*     */     {
/*  92 */       this(x0, x1, x2, x3);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.analysis.Analyzer
 * JD-Core Version:    0.6.2
 */