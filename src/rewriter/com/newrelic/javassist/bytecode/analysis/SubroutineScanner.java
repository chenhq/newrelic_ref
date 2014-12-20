/*     */ package com.newrelic.javassist.bytecode.analysis;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.ExceptionTable;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.bytecode.Opcode;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class SubroutineScanner
/*     */   implements Opcode
/*     */ {
/*     */   private Subroutine[] subroutines;
/*  37 */   Map subTable = new HashMap();
/*  38 */   Set done = new HashSet();
/*     */ 
/*     */   public Subroutine[] scan(MethodInfo method) throws BadBytecode
/*     */   {
/*  42 */     CodeAttribute code = method.getCodeAttribute();
/*  43 */     CodeIterator iter = code.iterator();
/*     */ 
/*  45 */     this.subroutines = new Subroutine[code.getCodeLength()];
/*  46 */     this.subTable.clear();
/*  47 */     this.done.clear();
/*     */ 
/*  49 */     scan(0, iter, null);
/*     */ 
/*  51 */     ExceptionTable exceptions = code.getExceptionTable();
/*  52 */     for (int i = 0; i < exceptions.size(); i++) {
/*  53 */       int handler = exceptions.handlerPc(i);
/*     */ 
/*  56 */       scan(handler, iter, this.subroutines[exceptions.startPc(i)]);
/*     */     }
/*     */ 
/*  59 */     return this.subroutines;
/*     */   }
/*     */ 
/*     */   private void scan(int pos, CodeIterator iter, Subroutine sub) throws BadBytecode
/*     */   {
/*  64 */     if (this.done.contains(new Integer(pos))) {
/*  65 */       return;
/*  67 */     }this.done.add(new Integer(pos));
/*     */ 
/*  69 */     int old = iter.lookAhead();
/*  70 */     iter.move(pos);
/*     */     boolean next;
/*     */     do { pos = iter.next();
/*  75 */       next = (scanOp(pos, iter, sub)) && (iter.hasNext()); }
/*  76 */     while (next);
/*     */ 
/*  78 */     iter.move(old);
/*     */   }
/*     */ 
/*     */   private boolean scanOp(int pos, CodeIterator iter, Subroutine sub) throws BadBytecode {
/*  82 */     this.subroutines[pos] = sub;
/*     */ 
/*  84 */     int opcode = iter.byteAt(pos);
/*     */ 
/*  86 */     if (opcode == 170) {
/*  87 */       scanTableSwitch(pos, iter, sub);
/*     */ 
/*  89 */       return false;
/*     */     }
/*     */ 
/*  92 */     if (opcode == 171) {
/*  93 */       scanLookupSwitch(pos, iter, sub);
/*     */ 
/*  95 */       return false;
/*     */     }
/*     */ 
/*  99 */     if ((Util.isReturn(opcode)) || (opcode == 169) || (opcode == 191)) {
/* 100 */       return false;
/*     */     }
/* 102 */     if (Util.isJumpInstruction(opcode)) {
/* 103 */       int target = Util.getJumpTarget(pos, iter);
/* 104 */       if ((opcode == 168) || (opcode == 201)) {
/* 105 */         Subroutine s = (Subroutine)this.subTable.get(new Integer(target));
/* 106 */         if (s == null) {
/* 107 */           s = new Subroutine(target, pos);
/* 108 */           this.subTable.put(new Integer(target), s);
/* 109 */           scan(target, iter, s);
/*     */         } else {
/* 111 */           s.addCaller(pos);
/*     */         }
/*     */       } else {
/* 114 */         scan(target, iter, sub);
/*     */ 
/* 117 */         if (Util.isGoto(opcode)) {
/* 118 */           return false;
/*     */         }
/*     */       }
/*     */     }
/* 122 */     return true;
/*     */   }
/*     */ 
/*     */   private void scanLookupSwitch(int pos, CodeIterator iter, Subroutine sub) throws BadBytecode {
/* 126 */     int index = (pos & 0xFFFFFFFC) + 4;
/*     */ 
/* 128 */     scan(pos + iter.s32bitAt(index), iter, sub);
/* 129 */     index += 4; int npairs = iter.s32bitAt(index);
/* 130 */     index += 4; int end = npairs * 8 + index;
/*     */ 
/* 133 */     for (index += 4; index < end; index += 8) {
/* 134 */       int target = iter.s32bitAt(index) + pos;
/* 135 */       scan(target, iter, sub);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void scanTableSwitch(int pos, CodeIterator iter, Subroutine sub) throws BadBytecode
/*     */   {
/* 141 */     int index = (pos & 0xFFFFFFFC) + 4;
/*     */ 
/* 143 */     scan(pos + iter.s32bitAt(index), iter, sub);
/* 144 */     index += 4; int low = iter.s32bitAt(index);
/* 145 */     index += 4; int high = iter.s32bitAt(index);
/* 146 */     index += 4; int end = (high - low + 1) * 4 + index;
/*     */ 
/* 149 */     for (; index < end; index += 4) {
/* 150 */       int target = iter.s32bitAt(index) + pos;
/* 151 */       scan(target, iter, sub);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.analysis.SubroutineScanner
 * JD-Core Version:    0.6.2
 */