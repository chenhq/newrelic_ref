/*     */ package com.newrelic.javassist.bytecode.stackmap;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.ByteArray;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ 
/*     */ public abstract class Tracer
/*     */   implements TypeTag
/*     */ {
/*     */   protected ClassPool classPool;
/*     */   protected ConstPool cpool;
/*     */   protected String returnType;
/*     */   protected int stackTop;
/*     */   protected TypeData[] stackTypes;
/*     */   protected TypeData[] localsTypes;
/*     */ 
/*     */   public Tracer(ClassPool classes, ConstPool cp, int maxStack, int maxLocals, String retType)
/*     */   {
/*  41 */     this.classPool = classes;
/*  42 */     this.cpool = cp;
/*  43 */     this.returnType = retType;
/*  44 */     this.stackTop = 0;
/*  45 */     this.stackTypes = new TypeData[maxStack];
/*  46 */     this.localsTypes = new TypeData[maxLocals];
/*     */   }
/*     */ 
/*     */   public Tracer(Tracer t, boolean copyStack) {
/*  50 */     this.classPool = t.classPool;
/*  51 */     this.cpool = t.cpool;
/*  52 */     this.returnType = t.returnType;
/*     */ 
/*  54 */     this.stackTop = t.stackTop;
/*  55 */     int size = t.stackTypes.length;
/*  56 */     this.stackTypes = new TypeData[size];
/*  57 */     if (copyStack) {
/*  58 */       copyFrom(t.stackTop, t.stackTypes, this.stackTypes);
/*     */     }
/*  60 */     int size2 = t.localsTypes.length;
/*  61 */     this.localsTypes = new TypeData[size2];
/*  62 */     copyFrom(size2, t.localsTypes, this.localsTypes);
/*     */   }
/*     */ 
/*     */   protected static int copyFrom(int n, TypeData[] srcTypes, TypeData[] destTypes) {
/*  66 */     int k = -1;
/*  67 */     for (int i = 0; i < n; i++) {
/*  68 */       TypeData t = srcTypes[i];
/*  69 */       destTypes[i] = (t == TOP ? TOP : t.getSelf());
/*  70 */       if (t != TOP) {
/*  71 */         if (t.is2WordType())
/*  72 */           k = i + 1;
/*     */         else
/*  74 */           k = i;
/*     */       }
/*     */     }
/*  77 */     return k + 1;
/*     */   }
/*     */ 
/*     */   protected int doOpcode(int pos, byte[] code)
/*     */     throws BadBytecode
/*     */   {
/*  91 */     int op = code[pos] & 0xFF;
/*  92 */     if (op < 96) {
/*  93 */       if (op < 54) {
/*  94 */         return doOpcode0_53(pos, code, op);
/*     */       }
/*  96 */       return doOpcode54_95(pos, code, op);
/*     */     }
/*  98 */     if (op < 148) {
/*  99 */       return doOpcode96_147(pos, code, op);
/*     */     }
/* 101 */     return doOpcode148_201(pos, code, op);
/*     */   }
/*     */ 
/*     */   protected void visitBranch(int pos, byte[] code, int offset)
/*     */     throws BadBytecode
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void visitGoto(int pos, byte[] code, int offset)
/*     */     throws BadBytecode
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void visitReturn(int pos, byte[] code)
/*     */     throws BadBytecode
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void visitThrow(int pos, byte[] code) throws BadBytecode
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void visitTableSwitch(int pos, byte[] code, int n, int offsetPos, int defaultOffset) throws BadBytecode
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void visitLookupSwitch(int pos, byte[] code, int n, int pairsPos, int defaultOffset) throws BadBytecode
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void visitJSR(int pos, byte[] code) throws BadBytecode
/*     */   {
/* 133 */     throwBadBytecode(pos, "jsr");
/*     */   }
/*     */ 
/*     */   protected void visitRET(int pos, byte[] code)
/*     */     throws BadBytecode
/*     */   {
/* 140 */     throwBadBytecode(pos, "ret");
/*     */   }
/*     */ 
/*     */   private void throwBadBytecode(int pos, String name) throws BadBytecode {
/* 144 */     throw new BadBytecode(name + " at " + pos);
/*     */   }
/*     */ 
/*     */   private int doOpcode0_53(int pos, byte[] code, int op) throws BadBytecode
/*     */   {
/* 149 */     TypeData[] stackTypes = this.stackTypes;
/* 150 */     switch (op) {
/*     */     case 0:
/* 152 */       break;
/*     */     case 1:
/* 154 */       stackTypes[(this.stackTop++)] = new TypeData.NullType();
/* 155 */       break;
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 163 */       stackTypes[(this.stackTop++)] = INTEGER;
/* 164 */       break;
/*     */     case 9:
/*     */     case 10:
/* 167 */       stackTypes[(this.stackTop++)] = LONG;
/* 168 */       stackTypes[(this.stackTop++)] = TOP;
/* 169 */       break;
/*     */     case 11:
/*     */     case 12:
/*     */     case 13:
/* 173 */       stackTypes[(this.stackTop++)] = FLOAT;
/* 174 */       break;
/*     */     case 14:
/*     */     case 15:
/* 177 */       stackTypes[(this.stackTop++)] = DOUBLE;
/* 178 */       stackTypes[(this.stackTop++)] = TOP;
/* 179 */       break;
/*     */     case 16:
/*     */     case 17:
/* 182 */       stackTypes[(this.stackTop++)] = INTEGER;
/* 183 */       return op == 17 ? 3 : 2;
/*     */     case 18:
/* 185 */       doLDC(code[(pos + 1)] & 0xFF);
/* 186 */       return 2;
/*     */     case 19:
/*     */     case 20:
/* 189 */       doLDC(ByteArray.readU16bit(code, pos + 1));
/* 190 */       return 3;
/*     */     case 21:
/* 192 */       return doXLOAD(INTEGER, code, pos);
/*     */     case 22:
/* 194 */       return doXLOAD(LONG, code, pos);
/*     */     case 23:
/* 196 */       return doXLOAD(FLOAT, code, pos);
/*     */     case 24:
/* 198 */       return doXLOAD(DOUBLE, code, pos);
/*     */     case 25:
/* 200 */       return doALOAD(code[(pos + 1)] & 0xFF);
/*     */     case 26:
/*     */     case 27:
/*     */     case 28:
/*     */     case 29:
/* 205 */       stackTypes[(this.stackTop++)] = INTEGER;
/* 206 */       break;
/*     */     case 30:
/*     */     case 31:
/*     */     case 32:
/*     */     case 33:
/* 211 */       stackTypes[(this.stackTop++)] = LONG;
/* 212 */       stackTypes[(this.stackTop++)] = TOP;
/* 213 */       break;
/*     */     case 34:
/*     */     case 35:
/*     */     case 36:
/*     */     case 37:
/* 218 */       stackTypes[(this.stackTop++)] = FLOAT;
/* 219 */       break;
/*     */     case 38:
/*     */     case 39:
/*     */     case 40:
/*     */     case 41:
/* 224 */       stackTypes[(this.stackTop++)] = DOUBLE;
/* 225 */       stackTypes[(this.stackTop++)] = TOP;
/* 226 */       break;
/*     */     case 42:
/*     */     case 43:
/*     */     case 44:
/*     */     case 45:
/* 231 */       int reg = op - 42;
/* 232 */       stackTypes[(this.stackTop++)] = this.localsTypes[reg];
/* 233 */       break;
/*     */     case 46:
/* 235 */       stackTypes[(--this.stackTop - 1)] = INTEGER;
/* 236 */       break;
/*     */     case 47:
/* 238 */       stackTypes[(this.stackTop - 2)] = LONG;
/* 239 */       stackTypes[(this.stackTop - 1)] = TOP;
/* 240 */       break;
/*     */     case 48:
/* 242 */       stackTypes[(--this.stackTop - 1)] = FLOAT;
/* 243 */       break;
/*     */     case 49:
/* 245 */       stackTypes[(this.stackTop - 2)] = DOUBLE;
/* 246 */       stackTypes[(this.stackTop - 1)] = TOP;
/* 247 */       break;
/*     */     case 50:
/* 249 */       int s = --this.stackTop - 1;
/* 250 */       TypeData data = stackTypes[s];
/* 251 */       if ((data == null) || (!data.isObjectType())) {
/* 252 */         throw new BadBytecode("bad AALOAD");
/*     */       }
/* 254 */       stackTypes[s] = new TypeData.ArrayElement(data);
/*     */ 
/* 256 */       break;
/*     */     case 51:
/*     */     case 52:
/*     */     case 53:
/* 260 */       stackTypes[(--this.stackTop - 1)] = INTEGER;
/* 261 */       break;
/*     */     default:
/* 263 */       throw new RuntimeException("fatal");
/*     */     }
/*     */ 
/* 266 */     return 1;
/*     */   }
/*     */ 
/*     */   private void doLDC(int index) {
/* 270 */     TypeData[] stackTypes = this.stackTypes;
/* 271 */     int tag = this.cpool.getTag(index);
/* 272 */     if (tag == 8) {
/* 273 */       stackTypes[(this.stackTop++)] = new TypeData.ClassName("java.lang.String");
/* 274 */     } else if (tag == 3) {
/* 275 */       stackTypes[(this.stackTop++)] = INTEGER;
/* 276 */     } else if (tag == 4) {
/* 277 */       stackTypes[(this.stackTop++)] = FLOAT;
/* 278 */     } else if (tag == 5) {
/* 279 */       stackTypes[(this.stackTop++)] = LONG;
/* 280 */       stackTypes[(this.stackTop++)] = TOP;
/*     */     }
/* 282 */     else if (tag == 6) {
/* 283 */       stackTypes[(this.stackTop++)] = DOUBLE;
/* 284 */       stackTypes[(this.stackTop++)] = TOP;
/*     */     }
/* 286 */     else if (tag == 7) {
/* 287 */       stackTypes[(this.stackTop++)] = new TypeData.ClassName("java.lang.Class");
/*     */     } else {
/* 289 */       throw new RuntimeException("bad LDC: " + tag);
/*     */     }
/*     */   }
/*     */ 
/* 293 */   private int doXLOAD(TypeData type, byte[] code, int pos) { int localVar = code[(pos + 1)] & 0xFF;
/* 294 */     return doXLOAD(localVar, type); }
/*     */ 
/*     */   private int doXLOAD(int localVar, TypeData type)
/*     */   {
/* 298 */     this.stackTypes[(this.stackTop++)] = type;
/* 299 */     if (type.is2WordType()) {
/* 300 */       this.stackTypes[(this.stackTop++)] = TOP;
/*     */     }
/* 302 */     return 2;
/*     */   }
/*     */ 
/*     */   private int doALOAD(int localVar) {
/* 306 */     this.stackTypes[(this.stackTop++)] = this.localsTypes[localVar];
/* 307 */     return 2;
/*     */   }
/*     */ 
/*     */   private int doOpcode54_95(int pos, byte[] code, int op) throws BadBytecode {
/* 311 */     TypeData[] localsTypes = this.localsTypes;
/* 312 */     TypeData[] stackTypes = this.stackTypes;
/* 313 */     switch (op) {
/*     */     case 54:
/* 315 */       return doXSTORE(pos, code, INTEGER);
/*     */     case 55:
/* 317 */       return doXSTORE(pos, code, LONG);
/*     */     case 56:
/* 319 */       return doXSTORE(pos, code, FLOAT);
/*     */     case 57:
/* 321 */       return doXSTORE(pos, code, DOUBLE);
/*     */     case 58:
/* 323 */       return doASTORE(code[(pos + 1)] & 0xFF);
/*     */     case 59:
/*     */     case 60:
/*     */     case 61:
/*     */     case 62:
/* 328 */       int var = op - 59;
/* 329 */       localsTypes[var] = INTEGER;
/* 330 */       this.stackTop -= 1;
/* 331 */       break;
/*     */     case 63:
/*     */     case 64:
/*     */     case 65:
/*     */     case 66:
/* 336 */       int var = op - 63;
/* 337 */       localsTypes[var] = LONG;
/* 338 */       localsTypes[(var + 1)] = TOP;
/* 339 */       this.stackTop -= 2;
/* 340 */       break;
/*     */     case 67:
/*     */     case 68:
/*     */     case 69:
/*     */     case 70:
/* 345 */       int var = op - 67;
/* 346 */       localsTypes[var] = FLOAT;
/* 347 */       this.stackTop -= 1;
/* 348 */       break;
/*     */     case 71:
/*     */     case 72:
/*     */     case 73:
/*     */     case 74:
/* 353 */       int var = op - 71;
/* 354 */       localsTypes[var] = DOUBLE;
/* 355 */       localsTypes[(var + 1)] = TOP;
/* 356 */       this.stackTop -= 2;
/* 357 */       break;
/*     */     case 75:
/*     */     case 76:
/*     */     case 77:
/*     */     case 78:
/* 362 */       int var = op - 75;
/* 363 */       doASTORE(var);
/* 364 */       break;
/*     */     case 79:
/*     */     case 80:
/*     */     case 81:
/*     */     case 82:
/* 369 */       this.stackTop -= ((op == 80) || (op == 82) ? 4 : 3);
/* 370 */       break;
/*     */     case 83:
/* 372 */       TypeData.setType(stackTypes[(this.stackTop - 1)], TypeData.ArrayElement.getElementType(stackTypes[(this.stackTop - 3)].getName()), this.classPool);
/*     */ 
/* 375 */       this.stackTop -= 3;
/* 376 */       break;
/*     */     case 84:
/*     */     case 85:
/*     */     case 86:
/* 380 */       this.stackTop -= 3;
/* 381 */       break;
/*     */     case 87:
/* 383 */       this.stackTop -= 1;
/* 384 */       break;
/*     */     case 88:
/* 386 */       this.stackTop -= 2;
/* 387 */       break;
/*     */     case 89:
/* 389 */       int sp = this.stackTop;
/* 390 */       stackTypes[sp] = stackTypes[(sp - 1)];
/* 391 */       this.stackTop = (sp + 1);
/* 392 */       break;
/*     */     case 90:
/*     */     case 91:
/* 395 */       int len = op - 90 + 2;
/* 396 */       doDUP_XX(1, len);
/* 397 */       int sp = this.stackTop;
/* 398 */       stackTypes[(sp - len)] = stackTypes[sp];
/* 399 */       this.stackTop = (sp + 1);
/* 400 */       break;
/*     */     case 92:
/* 402 */       doDUP_XX(2, 2);
/* 403 */       this.stackTop += 2;
/* 404 */       break;
/*     */     case 93:
/*     */     case 94:
/* 407 */       int len = op - 93 + 3;
/* 408 */       doDUP_XX(2, len);
/* 409 */       int sp = this.stackTop;
/* 410 */       stackTypes[(sp - len)] = stackTypes[sp];
/* 411 */       stackTypes[(sp - len + 1)] = stackTypes[(sp + 1)];
/* 412 */       this.stackTop = (sp + 2);
/* 413 */       break;
/*     */     case 95:
/* 415 */       int sp = this.stackTop - 1;
/* 416 */       TypeData t = stackTypes[sp];
/* 417 */       stackTypes[sp] = stackTypes[(sp - 1)];
/* 418 */       stackTypes[(sp - 1)] = t;
/* 419 */       break;
/*     */     default:
/* 421 */       throw new RuntimeException("fatal");
/*     */     }
/*     */ 
/* 424 */     return 1;
/*     */   }
/*     */ 
/*     */   private int doXSTORE(int pos, byte[] code, TypeData type) {
/* 428 */     int index = code[(pos + 1)] & 0xFF;
/* 429 */     return doXSTORE(index, type);
/*     */   }
/*     */ 
/*     */   private int doXSTORE(int index, TypeData type) {
/* 433 */     this.stackTop -= 1;
/* 434 */     this.localsTypes[index] = type;
/* 435 */     if (type.is2WordType()) {
/* 436 */       this.stackTop -= 1;
/* 437 */       this.localsTypes[(index + 1)] = TOP;
/*     */     }
/*     */ 
/* 440 */     return 2;
/*     */   }
/*     */ 
/*     */   private int doASTORE(int index) {
/* 444 */     this.stackTop -= 1;
/*     */ 
/* 446 */     this.localsTypes[index] = this.stackTypes[this.stackTop].copy();
/* 447 */     return 2;
/*     */   }
/*     */ 
/*     */   private void doDUP_XX(int delta, int len) {
/* 451 */     TypeData[] types = this.stackTypes;
/* 452 */     int sp = this.stackTop - 1;
/* 453 */     int end = sp - len;
/* 454 */     while (sp > end) {
/* 455 */       types[(sp + delta)] = types[sp];
/* 456 */       sp--;
/*     */     }
/*     */   }
/*     */ 
/*     */   private int doOpcode96_147(int pos, byte[] code, int op) {
/* 461 */     if (op <= 131) {
/* 462 */       this.stackTop += com.newrelic.javassist.bytecode.Opcode.STACK_GROW[op];
/* 463 */       return 1;
/*     */     }
/*     */ 
/* 466 */     switch (op)
/*     */     {
/*     */     case 132:
/* 469 */       return 3;
/*     */     case 133:
/* 471 */       this.stackTypes[this.stackTop] = LONG;
/* 472 */       this.stackTypes[(this.stackTop - 1)] = TOP;
/* 473 */       this.stackTop += 1;
/* 474 */       break;
/*     */     case 134:
/* 476 */       this.stackTypes[(this.stackTop - 1)] = FLOAT;
/* 477 */       break;
/*     */     case 135:
/* 479 */       this.stackTypes[this.stackTop] = DOUBLE;
/* 480 */       this.stackTypes[(this.stackTop - 1)] = TOP;
/* 481 */       this.stackTop += 1;
/* 482 */       break;
/*     */     case 136:
/* 484 */       this.stackTypes[(--this.stackTop - 1)] = INTEGER;
/* 485 */       break;
/*     */     case 137:
/* 487 */       this.stackTypes[(--this.stackTop - 1)] = FLOAT;
/* 488 */       break;
/*     */     case 138:
/* 490 */       this.stackTypes[(this.stackTop - 1)] = DOUBLE;
/* 491 */       break;
/*     */     case 139:
/* 493 */       this.stackTypes[(this.stackTop - 1)] = INTEGER;
/* 494 */       break;
/*     */     case 140:
/* 496 */       this.stackTypes[(this.stackTop - 1)] = TOP;
/* 497 */       this.stackTypes[(this.stackTop++)] = LONG;
/* 498 */       break;
/*     */     case 141:
/* 500 */       this.stackTypes[(this.stackTop - 1)] = TOP;
/* 501 */       this.stackTypes[(this.stackTop++)] = DOUBLE;
/* 502 */       break;
/*     */     case 142:
/* 504 */       this.stackTypes[(--this.stackTop - 1)] = INTEGER;
/* 505 */       break;
/*     */     case 143:
/* 507 */       this.stackTypes[(this.stackTop - 1)] = LONG;
/* 508 */       break;
/*     */     case 144:
/* 510 */       this.stackTypes[(--this.stackTop - 1)] = FLOAT;
/* 511 */       break;
/*     */     case 145:
/*     */     case 146:
/*     */     case 147:
/* 515 */       break;
/*     */     default:
/* 517 */       throw new RuntimeException("fatal");
/*     */     }
/*     */ 
/* 520 */     return 1;
/*     */   }
/*     */ 
/*     */   private int doOpcode148_201(int pos, byte[] code, int op) throws BadBytecode {
/* 524 */     switch (op) {
/*     */     case 148:
/* 526 */       this.stackTypes[(this.stackTop - 4)] = INTEGER;
/* 527 */       this.stackTop -= 3;
/* 528 */       break;
/*     */     case 149:
/*     */     case 150:
/* 531 */       this.stackTypes[(--this.stackTop - 1)] = INTEGER;
/* 532 */       break;
/*     */     case 151:
/*     */     case 152:
/* 535 */       this.stackTypes[(this.stackTop - 4)] = INTEGER;
/* 536 */       this.stackTop -= 3;
/* 537 */       break;
/*     */     case 153:
/*     */     case 154:
/*     */     case 155:
/*     */     case 156:
/*     */     case 157:
/*     */     case 158:
/* 544 */       this.stackTop -= 1;
/* 545 */       visitBranch(pos, code, ByteArray.readS16bit(code, pos + 1));
/* 546 */       return 3;
/*     */     case 159:
/*     */     case 160:
/*     */     case 161:
/*     */     case 162:
/*     */     case 163:
/*     */     case 164:
/*     */     case 165:
/*     */     case 166:
/* 555 */       this.stackTop -= 2;
/* 556 */       visitBranch(pos, code, ByteArray.readS16bit(code, pos + 1));
/* 557 */       return 3;
/*     */     case 167:
/* 559 */       visitGoto(pos, code, ByteArray.readS16bit(code, pos + 1));
/* 560 */       return 3;
/*     */     case 168:
/* 562 */       this.stackTypes[(this.stackTop++)] = TOP;
/* 563 */       visitJSR(pos, code);
/* 564 */       return 3;
/*     */     case 169:
/* 566 */       visitRET(pos, code);
/* 567 */       return 2;
/*     */     case 170:
/* 569 */       this.stackTop -= 1;
/* 570 */       int pos2 = (pos & 0xFFFFFFFC) + 8;
/* 571 */       int low = ByteArray.read32bit(code, pos2);
/* 572 */       int high = ByteArray.read32bit(code, pos2 + 4);
/* 573 */       int n = high - low + 1;
/* 574 */       visitTableSwitch(pos, code, n, pos2 + 8, ByteArray.read32bit(code, pos2 - 4));
/* 575 */       return n * 4 + 16 - (pos & 0x3);
/*     */     case 171:
/* 577 */       this.stackTop -= 1;
/* 578 */       int pos2 = (pos & 0xFFFFFFFC) + 8;
/* 579 */       int n = ByteArray.read32bit(code, pos2);
/* 580 */       visitLookupSwitch(pos, code, n, pos2 + 4, ByteArray.read32bit(code, pos2 - 4));
/* 581 */       return n * 8 + 12 - (pos & 0x3);
/*     */     case 172:
/* 583 */       this.stackTop -= 1;
/* 584 */       visitReturn(pos, code);
/* 585 */       break;
/*     */     case 173:
/* 587 */       this.stackTop -= 2;
/* 588 */       visitReturn(pos, code);
/* 589 */       break;
/*     */     case 174:
/* 591 */       this.stackTop -= 1;
/* 592 */       visitReturn(pos, code);
/* 593 */       break;
/*     */     case 175:
/* 595 */       this.stackTop -= 2;
/* 596 */       visitReturn(pos, code);
/* 597 */       break;
/*     */     case 176:
/* 599 */       TypeData.setType(this.stackTypes[(--this.stackTop)], this.returnType, this.classPool);
/* 600 */       visitReturn(pos, code);
/* 601 */       break;
/*     */     case 177:
/* 603 */       visitReturn(pos, code);
/* 604 */       break;
/*     */     case 178:
/* 606 */       return doGetField(pos, code, false);
/*     */     case 179:
/* 608 */       return doPutField(pos, code, false);
/*     */     case 180:
/* 610 */       return doGetField(pos, code, true);
/*     */     case 181:
/* 612 */       return doPutField(pos, code, true);
/*     */     case 182:
/*     */     case 183:
/* 615 */       return doInvokeMethod(pos, code, true);
/*     */     case 184:
/* 617 */       return doInvokeMethod(pos, code, false);
/*     */     case 185:
/* 619 */       return doInvokeIntfMethod(pos, code);
/*     */     case 186:
/* 621 */       throw new RuntimeException("bad opcode 186");
/*     */     case 187:
/* 623 */       int i = ByteArray.readU16bit(code, pos + 1);
/* 624 */       this.stackTypes[(this.stackTop++)] = new TypeData.UninitData(pos, this.cpool.getClassInfo(i));
/*     */ 
/* 626 */       return 3;
/*     */     case 188:
/* 628 */       return doNEWARRAY(pos, code);
/*     */     case 189:
/* 630 */       int i = ByteArray.readU16bit(code, pos + 1);
/* 631 */       String type = this.cpool.getClassInfo(i).replace('.', '/');
/* 632 */       if (type.charAt(0) == '[')
/* 633 */         type = "[" + type;
/*     */       else {
/* 635 */         type = "[L" + type + ";";
/*     */       }
/* 637 */       this.stackTypes[(this.stackTop - 1)] = new TypeData.ClassName(type);
/*     */ 
/* 639 */       return 3;
/*     */     case 190:
/* 641 */       TypeData.setType(this.stackTypes[(this.stackTop - 1)], "[Ljava.lang.Object;", this.classPool);
/* 642 */       this.stackTypes[(this.stackTop - 1)] = INTEGER;
/* 643 */       break;
/*     */     case 191:
/* 645 */       TypeData.setType(this.stackTypes[(--this.stackTop)], "java.lang.Throwable", this.classPool);
/* 646 */       visitThrow(pos, code);
/* 647 */       break;
/*     */     case 192:
/* 650 */       int i = ByteArray.readU16bit(code, pos + 1);
/* 651 */       this.stackTypes[(this.stackTop - 1)] = new TypeData.ClassName(this.cpool.getClassInfo(i));
/* 652 */       return 3;
/*     */     case 193:
/* 655 */       this.stackTypes[(this.stackTop - 1)] = INTEGER;
/* 656 */       return 3;
/*     */     case 194:
/*     */     case 195:
/* 659 */       this.stackTop -= 1;
/*     */ 
/* 661 */       break;
/*     */     case 196:
/* 663 */       return doWIDE(pos, code);
/*     */     case 197:
/* 665 */       return doMultiANewArray(pos, code);
/*     */     case 198:
/*     */     case 199:
/* 668 */       this.stackTop -= 1;
/* 669 */       visitBranch(pos, code, ByteArray.readS16bit(code, pos + 1));
/* 670 */       return 3;
/*     */     case 200:
/* 672 */       visitGoto(pos, code, ByteArray.read32bit(code, pos + 1));
/* 673 */       return 5;
/*     */     case 201:
/* 675 */       this.stackTypes[(this.stackTop++)] = TOP;
/* 676 */       visitJSR(pos, code);
/* 677 */       return 5;
/*     */     }
/* 679 */     return 1;
/*     */   }
/*     */ 
/*     */   private int doWIDE(int pos, byte[] code) throws BadBytecode {
/* 683 */     int op = code[(pos + 1)] & 0xFF;
/* 684 */     switch (op) {
/*     */     case 21:
/* 686 */       doWIDE_XLOAD(pos, code, INTEGER);
/* 687 */       break;
/*     */     case 22:
/* 689 */       doWIDE_XLOAD(pos, code, LONG);
/* 690 */       break;
/*     */     case 23:
/* 692 */       doWIDE_XLOAD(pos, code, FLOAT);
/* 693 */       break;
/*     */     case 24:
/* 695 */       doWIDE_XLOAD(pos, code, DOUBLE);
/* 696 */       break;
/*     */     case 25:
/* 698 */       int index = ByteArray.readU16bit(code, pos + 2);
/* 699 */       doALOAD(index);
/* 700 */       break;
/*     */     case 54:
/* 702 */       doWIDE_STORE(pos, code, INTEGER);
/* 703 */       break;
/*     */     case 55:
/* 705 */       doWIDE_STORE(pos, code, LONG);
/* 706 */       break;
/*     */     case 56:
/* 708 */       doWIDE_STORE(pos, code, FLOAT);
/* 709 */       break;
/*     */     case 57:
/* 711 */       doWIDE_STORE(pos, code, DOUBLE);
/* 712 */       break;
/*     */     case 58:
/* 714 */       int index = ByteArray.readU16bit(code, pos + 2);
/* 715 */       doASTORE(index);
/* 716 */       break;
/*     */     case 132:
/* 719 */       return 6;
/*     */     case 169:
/* 721 */       visitRET(pos, code);
/* 722 */       break;
/*     */     default:
/* 724 */       throw new RuntimeException("bad WIDE instruction: " + op);
/*     */     }
/*     */ 
/* 727 */     return 4;
/*     */   }
/*     */ 
/*     */   private void doWIDE_XLOAD(int pos, byte[] code, TypeData type) {
/* 731 */     int index = ByteArray.readU16bit(code, pos + 2);
/* 732 */     doXLOAD(index, type);
/*     */   }
/*     */ 
/*     */   private void doWIDE_STORE(int pos, byte[] code, TypeData type) {
/* 736 */     int index = ByteArray.readU16bit(code, pos + 2);
/* 737 */     doXSTORE(index, type);
/*     */   }
/*     */ 
/*     */   private int doPutField(int pos, byte[] code, boolean notStatic) throws BadBytecode {
/* 741 */     int index = ByteArray.readU16bit(code, pos + 1);
/* 742 */     String desc = this.cpool.getFieldrefType(index);
/* 743 */     this.stackTop -= Descriptor.dataSize(desc);
/* 744 */     char c = desc.charAt(0);
/* 745 */     if (c == 'L')
/* 746 */       TypeData.setType(this.stackTypes[this.stackTop], getFieldClassName(desc, 0), this.classPool);
/* 747 */     else if (c == '[') {
/* 748 */       TypeData.setType(this.stackTypes[this.stackTop], desc, this.classPool);
/*     */     }
/* 750 */     setFieldTarget(notStatic, index);
/* 751 */     return 3;
/*     */   }
/*     */ 
/*     */   private int doGetField(int pos, byte[] code, boolean notStatic) throws BadBytecode {
/* 755 */     int index = ByteArray.readU16bit(code, pos + 1);
/* 756 */     setFieldTarget(notStatic, index);
/* 757 */     String desc = this.cpool.getFieldrefType(index);
/* 758 */     pushMemberType(desc);
/* 759 */     return 3;
/*     */   }
/*     */ 
/*     */   private void setFieldTarget(boolean notStatic, int index) throws BadBytecode {
/* 763 */     if (notStatic) {
/* 764 */       String className = this.cpool.getFieldrefClassName(index);
/* 765 */       TypeData.setType(this.stackTypes[(--this.stackTop)], className, this.classPool);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int doNEWARRAY(int pos, byte[] code) {
/* 770 */     int s = this.stackTop - 1;
/*     */     String type;
/* 772 */     switch (code[(pos + 1)] & 0xFF) {
/*     */     case 4:
/* 774 */       type = "[Z";
/* 775 */       break;
/*     */     case 5:
/* 777 */       type = "[C";
/* 778 */       break;
/*     */     case 6:
/* 780 */       type = "[F";
/* 781 */       break;
/*     */     case 7:
/* 783 */       type = "[D";
/* 784 */       break;
/*     */     case 8:
/* 786 */       type = "[B";
/* 787 */       break;
/*     */     case 9:
/* 789 */       type = "[S";
/* 790 */       break;
/*     */     case 10:
/* 792 */       type = "[I";
/* 793 */       break;
/*     */     case 11:
/* 795 */       type = "[J";
/* 796 */       break;
/*     */     default:
/* 798 */       throw new RuntimeException("bad newarray");
/*     */     }
/*     */ 
/* 801 */     this.stackTypes[s] = new TypeData.ClassName(type);
/* 802 */     return 2;
/*     */   }
/*     */ 
/*     */   private int doMultiANewArray(int pos, byte[] code) {
/* 806 */     int i = ByteArray.readU16bit(code, pos + 1);
/* 807 */     int dim = code[(pos + 3)] & 0xFF;
/* 808 */     this.stackTop -= dim - 1;
/*     */ 
/* 810 */     String type = this.cpool.getClassInfo(i).replace('.', '/');
/* 811 */     this.stackTypes[(this.stackTop - 1)] = new TypeData.ClassName(type);
/* 812 */     return 4;
/*     */   }
/*     */ 
/*     */   private int doInvokeMethod(int pos, byte[] code, boolean notStatic) throws BadBytecode {
/* 816 */     int i = ByteArray.readU16bit(code, pos + 1);
/* 817 */     String desc = this.cpool.getMethodrefType(i);
/* 818 */     checkParamTypes(desc, 1);
/* 819 */     if (notStatic) {
/* 820 */       String className = this.cpool.getMethodrefClassName(i);
/* 821 */       TypeData.setType(this.stackTypes[(--this.stackTop)], className, this.classPool);
/*     */     }
/*     */ 
/* 824 */     pushMemberType(desc);
/* 825 */     return 3;
/*     */   }
/*     */ 
/*     */   private int doInvokeIntfMethod(int pos, byte[] code) throws BadBytecode {
/* 829 */     int i = ByteArray.readU16bit(code, pos + 1);
/* 830 */     String desc = this.cpool.getInterfaceMethodrefType(i);
/* 831 */     checkParamTypes(desc, 1);
/* 832 */     String className = this.cpool.getInterfaceMethodrefClassName(i);
/* 833 */     TypeData.setType(this.stackTypes[(--this.stackTop)], className, this.classPool);
/* 834 */     pushMemberType(desc);
/* 835 */     return 5;
/*     */   }
/*     */ 
/*     */   private void pushMemberType(String descriptor) {
/* 839 */     int top = 0;
/* 840 */     if (descriptor.charAt(0) == '(') {
/* 841 */       top = descriptor.indexOf(')') + 1;
/* 842 */       if (top < 1) {
/* 843 */         throw new IndexOutOfBoundsException("bad descriptor: " + descriptor);
/*     */       }
/*     */     }
/*     */ 
/* 847 */     TypeData[] types = this.stackTypes;
/* 848 */     int index = this.stackTop;
/* 849 */     switch (descriptor.charAt(top)) {
/*     */     case '[':
/* 851 */       types[index] = new TypeData.ClassName(descriptor.substring(top));
/* 852 */       break;
/*     */     case 'L':
/* 854 */       types[index] = new TypeData.ClassName(getFieldClassName(descriptor, top));
/* 855 */       break;
/*     */     case 'J':
/* 857 */       types[index] = LONG;
/* 858 */       types[(index + 1)] = TOP;
/* 859 */       this.stackTop += 2;
/* 860 */       return;
/*     */     case 'F':
/* 862 */       types[index] = FLOAT;
/* 863 */       break;
/*     */     case 'D':
/* 865 */       types[index] = DOUBLE;
/* 866 */       types[(index + 1)] = TOP;
/* 867 */       this.stackTop += 2;
/* 868 */       return;
/*     */     case 'V':
/* 870 */       return;
/*     */     default:
/* 872 */       types[index] = INTEGER;
/*     */     }
/*     */ 
/* 876 */     this.stackTop += 1;
/*     */   }
/*     */ 
/*     */   private static String getFieldClassName(String desc, int index) {
/* 880 */     return desc.substring(index + 1, desc.length() - 1).replace('/', '.');
/*     */   }
/*     */ 
/*     */   private void checkParamTypes(String desc, int i) throws BadBytecode {
/* 884 */     char c = desc.charAt(i);
/* 885 */     if (c == ')') {
/* 886 */       return;
/*     */     }
/* 888 */     int k = i;
/* 889 */     boolean array = false;
/* 890 */     while (c == '[') {
/* 891 */       array = true;
/* 892 */       c = desc.charAt(++k);
/*     */     }
/*     */ 
/* 895 */     if (c == 'L') {
/* 896 */       k = desc.indexOf(';', k) + 1;
/* 897 */       if (k <= 0)
/* 898 */         throw new IndexOutOfBoundsException("bad descriptor");
/*     */     }
/*     */     else {
/* 901 */       k++;
/*     */     }
/* 903 */     checkParamTypes(desc, k);
/* 904 */     if ((!array) && ((c == 'J') || (c == 'D')))
/* 905 */       this.stackTop -= 2;
/*     */     else {
/* 907 */       this.stackTop -= 1;
/*     */     }
/* 909 */     if (array) {
/* 910 */       TypeData.setType(this.stackTypes[this.stackTop], desc.substring(i, k), this.classPool);
/*     */     }
/* 912 */     else if (c == 'L')
/* 913 */       TypeData.setType(this.stackTypes[this.stackTop], desc.substring(i + 1, k - 1).replace('/', '.'), this.classPool);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.stackmap.Tracer
 * JD-Core Version:    0.6.2
 */