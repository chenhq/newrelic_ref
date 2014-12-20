/*     */ package com.newrelic.javassist.bytecode.stackmap;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.bytecode.StackMap;
/*     */ import com.newrelic.javassist.bytecode.StackMap.Writer;
/*     */ import com.newrelic.javassist.bytecode.StackMapTable;
/*     */ import com.newrelic.javassist.bytecode.StackMapTable.Writer;
/*     */ 
/*     */ public class MapMaker extends Tracer
/*     */ {
/*     */   public static StackMapTable make(ClassPool classes, MethodInfo minfo)
/*     */     throws BadBytecode
/*     */   {
/*  87 */     CodeAttribute ca = minfo.getCodeAttribute();
/*  88 */     if (ca == null) {
/*  89 */       return null;
/*     */     }
/*  91 */     TypedBlock[] blocks = TypedBlock.makeBlocks(minfo, ca, true);
/*  92 */     if (blocks == null) {
/*  93 */       return null;
/*     */     }
/*  95 */     MapMaker mm = new MapMaker(classes, minfo, ca);
/*  96 */     mm.make(blocks, ca.getCode());
/*  97 */     return mm.toStackMap(blocks);
/*     */   }
/*     */ 
/*     */   public static StackMap make2(ClassPool classes, MethodInfo minfo)
/*     */     throws BadBytecode
/*     */   {
/* 108 */     CodeAttribute ca = minfo.getCodeAttribute();
/* 109 */     if (ca == null) {
/* 110 */       return null;
/*     */     }
/* 112 */     TypedBlock[] blocks = TypedBlock.makeBlocks(minfo, ca, true);
/* 113 */     if (blocks == null) {
/* 114 */       return null;
/*     */     }
/* 116 */     MapMaker mm = new MapMaker(classes, minfo, ca);
/* 117 */     mm.make(blocks, ca.getCode());
/* 118 */     return mm.toStackMap2(minfo.getConstPool(), blocks);
/*     */   }
/*     */ 
/*     */   public MapMaker(ClassPool classes, MethodInfo minfo, CodeAttribute ca) {
/* 122 */     super(classes, minfo.getConstPool(), ca.getMaxStack(), ca.getMaxLocals(), TypedBlock.getRetType(minfo.getDescriptor()));
/*     */   }
/*     */ 
/*     */   protected MapMaker(MapMaker old, boolean copyStack)
/*     */   {
/* 128 */     super(old, copyStack);
/*     */   }
/*     */ 
/*     */   void make(TypedBlock[] blocks, byte[] code)
/*     */     throws BadBytecode
/*     */   {
/* 137 */     TypedBlock first = blocks[0];
/* 138 */     fixParamTypes(first);
/* 139 */     TypeData[] srcTypes = first.localsTypes;
/* 140 */     copyFrom(srcTypes.length, srcTypes, this.localsTypes);
/* 141 */     make(code, first);
/*     */ 
/* 143 */     int n = blocks.length;
/* 144 */     for (int i = 0; i < n; i++)
/* 145 */       evalExpected(blocks[i]);
/*     */   }
/*     */ 
/*     */   private void fixParamTypes(TypedBlock first)
/*     */     throws BadBytecode
/*     */   {
/* 155 */     TypeData[] types = first.localsTypes;
/* 156 */     int n = types.length;
/* 157 */     for (int i = 0; i < n; i++) {
/* 158 */       TypeData t = types[i];
/* 159 */       if ((t instanceof TypeData.ClassName))
/*     */       {
/* 163 */         TypeData.setType(t, t.getName(), this.classPool);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void make(byte[] code, TypedBlock tb)
/*     */     throws BadBytecode
/*     */   {
/* 173 */     BasicBlock.Catch handlers = tb.toCatch;
/* 174 */     while (handlers != null) {
/* 175 */       traceException(code, handlers);
/* 176 */       handlers = handlers.next;
/*     */     }
/*     */ 
/* 179 */     int pos = tb.position;
/* 180 */     int end = pos + tb.length;
/* 181 */     while (pos < end) {
/* 182 */       pos += doOpcode(pos, code);
/*     */     }
/* 184 */     if (tb.exit != null)
/* 185 */       for (int i = 0; i < tb.exit.length; i++) {
/* 186 */         TypedBlock e = (TypedBlock)tb.exit[i];
/* 187 */         if (e.alreadySet()) {
/* 188 */           mergeMap(e, true);
/*     */         } else {
/* 190 */           recordStackMap(e);
/* 191 */           MapMaker maker = new MapMaker(this, true);
/* 192 */           maker.make(code, e);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   private void traceException(byte[] code, BasicBlock.Catch handler)
/*     */     throws BadBytecode
/*     */   {
/* 201 */     TypedBlock tb = (TypedBlock)handler.body;
/* 202 */     if (tb.alreadySet()) {
/* 203 */       mergeMap(tb, false);
/*     */     } else {
/* 205 */       recordStackMap(tb, handler.typeIndex);
/* 206 */       MapMaker maker = new MapMaker(this, false);
/*     */ 
/* 211 */       maker.stackTypes[0] = tb.stackTypes[0].getSelf();
/* 212 */       maker.stackTop = 1;
/* 213 */       maker.make(code, tb);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void mergeMap(TypedBlock dest, boolean mergeStack) {
/* 218 */     boolean[] inputs = dest.inputs;
/* 219 */     int n = inputs.length;
/* 220 */     for (int i = 0; i < n; i++) {
/* 221 */       if (inputs[i] != 0)
/* 222 */         merge(this.localsTypes[i], dest.localsTypes[i]);
/*     */     }
/* 224 */     if (mergeStack) {
/* 225 */       n = this.stackTop;
/* 226 */       for (int i = 0; i < n; i++)
/* 227 */         merge(this.stackTypes[i], dest.stackTypes[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void merge(TypeData td, TypeData target) {
/* 232 */     boolean tdIsObj = false;
/* 233 */     boolean targetIsObj = false;
/*     */ 
/* 235 */     if ((td != TOP) && (td.isObjectType())) {
/* 236 */       tdIsObj = true;
/*     */     }
/* 238 */     if ((target != TOP) && (target.isObjectType())) {
/* 239 */       targetIsObj = true;
/*     */     }
/* 241 */     if ((tdIsObj) && (targetIsObj))
/* 242 */       target.merge(td);
/*     */   }
/*     */ 
/*     */   private void recordStackMap(TypedBlock target)
/*     */     throws BadBytecode
/*     */   {
/* 248 */     TypeData[] tStackTypes = new TypeData[this.stackTypes.length];
/* 249 */     int st = this.stackTop;
/* 250 */     copyFrom(st, this.stackTypes, tStackTypes);
/* 251 */     recordStackMap0(target, st, tStackTypes);
/*     */   }
/*     */ 
/*     */   private void recordStackMap(TypedBlock target, int exceptionType)
/*     */     throws BadBytecode
/*     */   {
/*     */     String type;
/*     */     String type;
/* 258 */     if (exceptionType == 0)
/* 259 */       type = "java.lang.Throwable";
/*     */     else {
/* 261 */       type = this.cpool.getClassInfo(exceptionType);
/*     */     }
/* 263 */     TypeData[] tStackTypes = new TypeData[this.stackTypes.length];
/* 264 */     tStackTypes[0] = new TypeData.ClassName(type);
/*     */ 
/* 266 */     recordStackMap0(target, 1, tStackTypes);
/*     */   }
/*     */ 
/*     */   private void recordStackMap0(TypedBlock target, int st, TypeData[] tStackTypes)
/*     */     throws BadBytecode
/*     */   {
/* 272 */     int n = this.localsTypes.length;
/* 273 */     TypeData[] tLocalsTypes = new TypeData[n];
/* 274 */     int k = copyFrom(n, this.localsTypes, tLocalsTypes);
/*     */ 
/* 276 */     boolean[] inputs = target.inputs;
/* 277 */     for (int i = 0; i < n; i++) {
/* 278 */       if (inputs[i] == 0)
/* 279 */         tLocalsTypes[i] = TOP;
/*     */     }
/* 281 */     target.setStackMap(st, tStackTypes, k, tLocalsTypes);
/*     */   }
/*     */ 
/*     */   void evalExpected(TypedBlock target)
/*     */     throws BadBytecode
/*     */   {
/* 287 */     ClassPool cp = this.classPool;
/* 288 */     evalExpected(cp, target.stackTop, target.stackTypes);
/* 289 */     TypeData[] types = target.localsTypes;
/* 290 */     if (types != null)
/* 291 */       evalExpected(cp, types.length, types);
/*     */   }
/*     */ 
/*     */   private static void evalExpected(ClassPool cp, int n, TypeData[] types)
/*     */     throws BadBytecode
/*     */   {
/* 297 */     for (int i = 0; i < n; i++) {
/* 298 */       TypeData td = types[i];
/* 299 */       if (td != null)
/* 300 */         td.evalExpectedType(cp);
/*     */     }
/*     */   }
/*     */ 
/*     */   public StackMapTable toStackMap(TypedBlock[] blocks)
/*     */   {
/* 307 */     StackMapTable.Writer writer = new StackMapTable.Writer(32);
/* 308 */     int n = blocks.length;
/* 309 */     TypedBlock prev = blocks[0];
/* 310 */     int offsetDelta = prev.length;
/* 311 */     if (prev.incoming > 0) {
/* 312 */       writer.sameFrame(0);
/* 313 */       offsetDelta--;
/*     */     }
/*     */ 
/* 316 */     for (int i = 1; i < n; i++) {
/* 317 */       TypedBlock bb = blocks[i];
/* 318 */       if (isTarget(bb, blocks[(i - 1)])) {
/* 319 */         bb.resetNumLocals();
/* 320 */         int diffL = stackMapDiff(prev.numLocals, prev.localsTypes, bb.numLocals, bb.localsTypes);
/*     */ 
/* 322 */         toStackMapBody(writer, bb, diffL, offsetDelta, prev);
/* 323 */         offsetDelta = bb.length - 1;
/* 324 */         prev = bb;
/*     */       }
/*     */       else {
/* 327 */         offsetDelta += bb.length;
/*     */       }
/*     */     }
/* 330 */     return writer.toStackMapTable(this.cpool);
/*     */   }
/*     */ 
/*     */   private boolean isTarget(TypedBlock cur, TypedBlock prev)
/*     */   {
/* 337 */     int in = cur.incoming;
/* 338 */     if (in > 1)
/* 339 */       return true;
/* 340 */     if (in < 1) {
/* 341 */       return false;
/*     */     }
/* 343 */     return prev.stop;
/*     */   }
/*     */ 
/*     */   private void toStackMapBody(StackMapTable.Writer writer, TypedBlock bb, int diffL, int offsetDelta, TypedBlock prev)
/*     */   {
/* 351 */     int stackTop = bb.stackTop;
/* 352 */     if (stackTop == 0) {
/* 353 */       if (diffL == 0) {
/* 354 */         writer.sameFrame(offsetDelta);
/* 355 */         return;
/*     */       }
/* 357 */       if ((0 > diffL) && (diffL >= -3)) {
/* 358 */         writer.chopFrame(offsetDelta, -diffL);
/* 359 */         return;
/*     */       }
/* 361 */       if ((0 < diffL) && (diffL <= 3)) {
/* 362 */         int[] data = new int[diffL];
/* 363 */         int[] tags = fillStackMap(bb.numLocals - prev.numLocals, prev.numLocals, data, bb.localsTypes);
/*     */ 
/* 366 */         writer.appendFrame(offsetDelta, tags, data);
/*     */       }
/*     */     }
/*     */     else {
/* 370 */       if ((stackTop == 1) && (diffL == 0)) {
/* 371 */         TypeData td = bb.stackTypes[0];
/* 372 */         if (td == TOP)
/* 373 */           writer.sameLocals(offsetDelta, 0, 0);
/*     */         else {
/* 375 */           writer.sameLocals(offsetDelta, td.getTypeTag(), td.getTypeData(this.cpool));
/*     */         }
/* 377 */         return;
/*     */       }
/* 379 */       if ((stackTop == 2) && (diffL == 0)) {
/* 380 */         TypeData td = bb.stackTypes[0];
/* 381 */         if ((td != TOP) && (td.is2WordType()))
/*     */         {
/* 383 */           writer.sameLocals(offsetDelta, td.getTypeTag(), td.getTypeData(this.cpool));
/*     */ 
/* 385 */           return;
/*     */         }
/*     */       }
/*     */     }
/* 389 */     int[] sdata = new int[stackTop];
/* 390 */     int[] stags = fillStackMap(stackTop, 0, sdata, bb.stackTypes);
/* 391 */     int[] ldata = new int[bb.numLocals];
/* 392 */     int[] ltags = fillStackMap(bb.numLocals, 0, ldata, bb.localsTypes);
/* 393 */     writer.fullFrame(offsetDelta, ltags, ldata, stags, sdata);
/*     */   }
/*     */ 
/*     */   private int[] fillStackMap(int num, int offset, int[] data, TypeData[] types) {
/* 397 */     int realNum = diffSize(types, offset, offset + num);
/* 398 */     ConstPool cp = this.cpool;
/* 399 */     int[] tags = new int[realNum];
/* 400 */     int j = 0;
/* 401 */     for (int i = 0; i < num; i++) {
/* 402 */       TypeData td = types[(offset + i)];
/* 403 */       if (td == TOP) {
/* 404 */         tags[j] = 0;
/* 405 */         data[j] = 0;
/*     */       }
/*     */       else {
/* 408 */         tags[j] = td.getTypeTag();
/* 409 */         data[j] = td.getTypeData(cp);
/* 410 */         if (td.is2WordType()) {
/* 411 */           i++;
/*     */         }
/*     */       }
/* 414 */       j++;
/*     */     }
/*     */ 
/* 417 */     return tags;
/*     */   }
/*     */ 
/*     */   private static int stackMapDiff(int oldTdLen, TypeData[] oldTd, int newTdLen, TypeData[] newTd)
/*     */   {
/* 423 */     int diff = newTdLen - oldTdLen;
/*     */     int len;
/*     */     int len;
/* 425 */     if (diff > 0)
/* 426 */       len = oldTdLen;
/*     */     else {
/* 428 */       len = newTdLen;
/*     */     }
/* 430 */     if (stackMapEq(oldTd, newTd, len)) {
/* 431 */       if (diff > 0) {
/* 432 */         return diffSize(newTd, len, newTdLen);
/*     */       }
/* 434 */       return -diffSize(oldTd, len, oldTdLen);
/*     */     }
/* 436 */     return -100;
/*     */   }
/*     */ 
/*     */   private static boolean stackMapEq(TypeData[] oldTd, TypeData[] newTd, int len) {
/* 440 */     for (int i = 0; i < len; i++) {
/* 441 */       TypeData td = oldTd[i];
/* 442 */       if (td == TOP) {
/* 443 */         if (newTd[i] != TOP) {
/* 444 */           return false;
/*     */         }
/*     */       }
/* 447 */       else if (!oldTd[i].equals(newTd[i])) {
/* 448 */         return false;
/*     */       }
/*     */     }
/* 451 */     return true;
/*     */   }
/*     */ 
/*     */   private static int diffSize(TypeData[] types, int offset, int len) {
/* 455 */     int num = 0;
/* 456 */     while (offset < len) {
/* 457 */       TypeData td = types[(offset++)];
/* 458 */       num++;
/* 459 */       if ((td != TOP) && (td.is2WordType())) {
/* 460 */         offset++;
/*     */       }
/*     */     }
/* 463 */     return num;
/*     */   }
/*     */ 
/*     */   public StackMap toStackMap2(ConstPool cp, TypedBlock[] blocks)
/*     */   {
/* 469 */     StackMap.Writer writer = new StackMap.Writer();
/* 470 */     int n = blocks.length;
/* 471 */     boolean[] effective = new boolean[n];
/* 472 */     TypedBlock prev = blocks[0];
/*     */ 
/* 475 */     effective[0] = (prev.incoming > 0 ? 1 : false);
/*     */ 
/* 477 */     int num = effective[0] != 0 ? 1 : 0;
/* 478 */     for (int i = 1; i < n; i++) {
/* 479 */       TypedBlock bb = blocks[i];
/* 480 */       if ((effective[i] = isTarget(bb, blocks[(i - 1)]))) {
/* 481 */         bb.resetNumLocals();
/* 482 */         prev = bb;
/* 483 */         num++;
/*     */       }
/*     */     }
/*     */ 
/* 487 */     if (num == 0) {
/* 488 */       return null;
/*     */     }
/* 490 */     writer.write16bit(num);
/* 491 */     for (int i = 0; i < n; i++) {
/* 492 */       if (effective[i] != 0)
/* 493 */         writeStackFrame(writer, cp, blocks[i].position, blocks[i]);
/*     */     }
/* 495 */     return writer.toStackMap(cp);
/*     */   }
/*     */ 
/*     */   private void writeStackFrame(StackMap.Writer writer, ConstPool cp, int offset, TypedBlock tb) {
/* 499 */     writer.write16bit(offset);
/* 500 */     writeVerifyTypeInfo(writer, cp, tb.localsTypes, tb.numLocals);
/* 501 */     writeVerifyTypeInfo(writer, cp, tb.stackTypes, tb.stackTop);
/*     */   }
/*     */ 
/*     */   private void writeVerifyTypeInfo(StackMap.Writer writer, ConstPool cp, TypeData[] types, int num) {
/* 505 */     int numDWord = 0;
/* 506 */     for (int i = 0; i < num; i++) {
/* 507 */       TypeData td = types[i];
/* 508 */       if ((td != null) && (td.is2WordType())) {
/* 509 */         numDWord++;
/* 510 */         i++;
/*     */       }
/*     */     }
/*     */ 
/* 514 */     writer.write16bit(num - numDWord);
/* 515 */     for (int i = 0; i < num; i++) {
/* 516 */       TypeData td = types[i];
/* 517 */       if (td == TOP) {
/* 518 */         writer.writeVerifyTypeInfo(0, 0);
/*     */       } else {
/* 520 */         writer.writeVerifyTypeInfo(td.getTypeTag(), td.getTypeData(cp));
/* 521 */         if (td.is2WordType())
/* 522 */           i++;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.stackmap.MapMaker
 * JD-Core Version:    0.6.2
 */