/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ class CodeAnalyzer
/*     */   implements Opcode
/*     */ {
/*     */   private ConstPool constPool;
/*     */   private CodeAttribute codeAttr;
/*     */ 
/*     */   public CodeAnalyzer(CodeAttribute ca)
/*     */   {
/*  26 */     this.codeAttr = ca;
/*  27 */     this.constPool = ca.getConstPool();
/*     */   }
/*     */ 
/*     */   public int computeMaxStack() throws BadBytecode
/*     */   {
/*  38 */     CodeIterator ci = this.codeAttr.iterator();
/*  39 */     int length = ci.getCodeLength();
/*  40 */     int[] stack = new int[length];
/*  41 */     this.constPool = this.codeAttr.getConstPool();
/*  42 */     initStack(stack, this.codeAttr);
/*     */     boolean repeat;
/*     */     do
/*     */     {
/*  45 */       repeat = false;
/*  46 */       for (int i = 0; i < length; i++)
/*  47 */         if (stack[i] < 0) {
/*  48 */           repeat = true;
/*  49 */           visitBytecode(ci, stack, i);
/*     */         }
/*     */     }
/*  51 */     while (repeat);
/*     */ 
/*  53 */     int maxStack = 1;
/*  54 */     for (int i = 0; i < length; i++) {
/*  55 */       if (stack[i] > maxStack)
/*  56 */         maxStack = stack[i];
/*     */     }
/*  58 */     return maxStack - 1;
/*     */   }
/*     */ 
/*     */   private void initStack(int[] stack, CodeAttribute ca) {
/*  62 */     stack[0] = -1;
/*  63 */     ExceptionTable et = ca.getExceptionTable();
/*  64 */     if (et != null) {
/*  65 */       int size = et.size();
/*  66 */       for (int i = 0; i < size; i++)
/*  67 */         stack[et.handlerPc(i)] = -2;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void visitBytecode(CodeIterator ci, int[] stack, int index)
/*     */     throws BadBytecode
/*     */   {
/*  74 */     int codeLength = stack.length;
/*  75 */     ci.move(index);
/*  76 */     int stackDepth = -stack[index];
/*  77 */     while (ci.hasNext()) {
/*  78 */       index = ci.next();
/*  79 */       stack[index] = stackDepth;
/*  80 */       int op = ci.byteAt(index);
/*  81 */       stackDepth = visitInst(op, ci, index, stackDepth);
/*  82 */       if (stackDepth < 1) {
/*  83 */         throw new BadBytecode("stack underflow at " + index);
/*     */       }
/*  85 */       if (processBranch(op, ci, index, codeLength, stack, stackDepth)) {
/*     */         break;
/*     */       }
/*  88 */       if (isEnd(op)) {
/*     */         break;
/*     */       }
/*  91 */       if ((op == 168) || (op == 201))
/*  92 */         stackDepth--;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean processBranch(int opcode, CodeIterator ci, int index, int codeLength, int[] stack, int stackDepth)
/*     */     throws BadBytecode
/*     */   {
/* 100 */     if (((153 <= opcode) && (opcode <= 166)) || (opcode == 198) || (opcode == 199))
/*     */     {
/* 102 */       int target = index + ci.s16bitAt(index + 1);
/* 103 */       checkTarget(index, target, codeLength, stack, stackDepth);
/*     */     }
/*     */     else
/*     */     {
/*     */       int target;
/*     */       int target;
/* 107 */       switch (opcode) {
/*     */       case 167:
/* 109 */         target = index + ci.s16bitAt(index + 1);
/* 110 */         checkTarget(index, target, codeLength, stack, stackDepth);
/* 111 */         return true;
/*     */       case 200:
/* 113 */         target = index + ci.s32bitAt(index + 1);
/* 114 */         checkTarget(index, target, codeLength, stack, stackDepth);
/* 115 */         return true;
/*     */       case 168:
/*     */       case 201:
/* 118 */         if (opcode == 168)
/* 119 */           target = index + ci.s16bitAt(index + 1);
/*     */         else {
/* 121 */           target = index + ci.s32bitAt(index + 1);
/*     */         }
/* 123 */         checkTarget(index, target, codeLength, stack, stackDepth);
/* 124 */         if (stackDepth == 2) {
/* 125 */           return false;
/*     */         }
/* 127 */         throw new BadBytecode("sorry, cannot compute this data flow due to JSR");
/*     */       case 169:
/* 130 */         if (stackDepth == 1) {
/* 131 */           return true;
/*     */         }
/* 133 */         throw new BadBytecode("sorry, cannot compute this data flow due to RET");
/*     */       case 170:
/*     */       case 171:
/* 137 */         int index2 = (index & 0xFFFFFFFC) + 4;
/* 138 */         target = index + ci.s32bitAt(index2);
/* 139 */         checkTarget(index, target, codeLength, stack, stackDepth);
/* 140 */         if (opcode == 171) {
/* 141 */           int npairs = ci.s32bitAt(index2 + 4);
/* 142 */           index2 += 12;
/* 143 */           for (int i = 0; i < npairs; i++) {
/* 144 */             target = index + ci.s32bitAt(index2);
/* 145 */             checkTarget(index, target, codeLength, stack, stackDepth);
/*     */ 
/* 147 */             index2 += 8;
/*     */           }
/*     */         }
/*     */         else {
/* 151 */           int low = ci.s32bitAt(index2 + 4);
/* 152 */           int high = ci.s32bitAt(index2 + 8);
/* 153 */           int n = high - low + 1;
/* 154 */           index2 += 12;
/* 155 */           for (int i = 0; i < n; i++) {
/* 156 */             target = index + ci.s32bitAt(index2);
/* 157 */             checkTarget(index, target, codeLength, stack, stackDepth);
/*     */ 
/* 159 */             index2 += 4;
/*     */           }
/*     */         }
/*     */ 
/* 163 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 167 */     return false;
/*     */   }
/*     */ 
/*     */   private void checkTarget(int opIndex, int target, int codeLength, int[] stack, int stackDepth)
/*     */     throws BadBytecode
/*     */   {
/* 174 */     if ((target < 0) || (codeLength <= target)) {
/* 175 */       throw new BadBytecode("bad branch offset at " + opIndex);
/*     */     }
/* 177 */     int d = stack[target];
/* 178 */     if (d == 0)
/* 179 */       stack[target] = (-stackDepth);
/* 180 */     else if ((d != stackDepth) && (d != -stackDepth))
/* 181 */       throw new BadBytecode("verification error (" + stackDepth + "," + d + ") at " + opIndex);
/*     */   }
/*     */ 
/*     */   private static boolean isEnd(int opcode)
/*     */   {
/* 186 */     return ((172 <= opcode) && (opcode <= 177)) || (opcode == 191);
/*     */   }
/*     */ 
/*     */   private int visitInst(int op, CodeIterator ci, int index, int stack)
/*     */     throws BadBytecode
/*     */   {
/*     */     String desc;
/* 196 */     switch (op) {
/*     */     case 180:
/* 198 */       stack += getFieldSize(ci, index) - 1;
/* 199 */       break;
/*     */     case 181:
/* 201 */       stack -= getFieldSize(ci, index) + 1;
/* 202 */       break;
/*     */     case 178:
/* 204 */       stack += getFieldSize(ci, index);
/* 205 */       break;
/*     */     case 179:
/* 207 */       stack -= getFieldSize(ci, index);
/* 208 */       break;
/*     */     case 182:
/*     */     case 183:
/* 211 */       desc = this.constPool.getMethodrefType(ci.u16bitAt(index + 1));
/* 212 */       stack += Descriptor.dataSize(desc) - 1;
/* 213 */       break;
/*     */     case 184:
/* 215 */       desc = this.constPool.getMethodrefType(ci.u16bitAt(index + 1));
/* 216 */       stack += Descriptor.dataSize(desc);
/* 217 */       break;
/*     */     case 185:
/* 219 */       desc = this.constPool.getInterfaceMethodrefType(ci.u16bitAt(index + 1));
/*     */ 
/* 221 */       stack += Descriptor.dataSize(desc) - 1;
/* 222 */       break;
/*     */     case 191:
/* 224 */       stack = 1;
/* 225 */       break;
/*     */     case 197:
/* 227 */       stack += 1 - ci.byteAt(index + 3);
/* 228 */       break;
/*     */     case 196:
/* 230 */       op = ci.byteAt(index + 1);
/*     */     case 186:
/*     */     case 187:
/*     */     case 188:
/*     */     case 189:
/*     */     case 190:
/*     */     case 192:
/*     */     case 193:
/*     */     case 194:
/*     */     case 195:
/*     */     default:
/* 233 */       stack += STACK_GROW[op];
/*     */     }
/*     */ 
/* 236 */     return stack;
/*     */   }
/*     */ 
/*     */   private int getFieldSize(CodeIterator ci, int index) {
/* 240 */     String desc = this.constPool.getFieldrefType(ci.u16bitAt(index + 1));
/* 241 */     return Descriptor.dataSize(desc);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.CodeAnalyzer
 * JD-Core Version:    0.6.2
 */