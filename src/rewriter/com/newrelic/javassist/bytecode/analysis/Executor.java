/*      */ package com.newrelic.javassist.bytecode.analysis;
/*      */ 
/*      */ import com.newrelic.javassist.ClassPool;
/*      */ import com.newrelic.javassist.CtClass;
/*      */ import com.newrelic.javassist.NotFoundException;
/*      */ import com.newrelic.javassist.bytecode.BadBytecode;
/*      */ import com.newrelic.javassist.bytecode.CodeIterator;
/*      */ import com.newrelic.javassist.bytecode.ConstPool;
/*      */ import com.newrelic.javassist.bytecode.Descriptor;
/*      */ import com.newrelic.javassist.bytecode.MethodInfo;
/*      */ import com.newrelic.javassist.bytecode.Opcode;
/*      */ 
/*      */ public class Executor
/*      */   implements Opcode
/*      */ {
/*      */   private final ConstPool constPool;
/*      */   private final ClassPool classPool;
/*      */   private final Type STRING_TYPE;
/*      */   private final Type CLASS_TYPE;
/*      */   private final Type THROWABLE_TYPE;
/*      */   private int lastPos;
/*      */ 
/*      */   public Executor(ClassPool classPool, ConstPool constPool)
/*      */   {
/*   41 */     this.constPool = constPool;
/*   42 */     this.classPool = classPool;
/*      */     try
/*      */     {
/*   45 */       this.STRING_TYPE = getType("java.lang.String");
/*   46 */       this.CLASS_TYPE = getType("java.lang.Class");
/*   47 */       this.THROWABLE_TYPE = getType("java.lang.Throwable");
/*      */     } catch (Exception e) {
/*   49 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void execute(MethodInfo method, int pos, CodeIterator iter, Frame frame, Subroutine subroutine)
/*      */     throws BadBytecode
/*      */   {
/*   67 */     this.lastPos = pos;
/*   68 */     int opcode = iter.byteAt(pos);
/*      */ 
/*   72 */     switch (opcode) {
/*      */     case 0:
/*   74 */       break;
/*      */     case 1:
/*   76 */       frame.push(Type.UNINIT);
/*   77 */       break;
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*   85 */       frame.push(Type.INTEGER);
/*   86 */       break;
/*      */     case 9:
/*      */     case 10:
/*   89 */       frame.push(Type.LONG);
/*   90 */       frame.push(Type.TOP);
/*   91 */       break;
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*   95 */       frame.push(Type.FLOAT);
/*   96 */       break;
/*      */     case 14:
/*      */     case 15:
/*   99 */       frame.push(Type.DOUBLE);
/*  100 */       frame.push(Type.TOP);
/*  101 */       break;
/*      */     case 16:
/*      */     case 17:
/*  104 */       frame.push(Type.INTEGER);
/*  105 */       break;
/*      */     case 18:
/*  107 */       evalLDC(iter.byteAt(pos + 1), frame);
/*  108 */       break;
/*      */     case 19:
/*      */     case 20:
/*  111 */       evalLDC(iter.u16bitAt(pos + 1), frame);
/*  112 */       break;
/*      */     case 21:
/*  114 */       evalLoad(Type.INTEGER, iter.byteAt(pos + 1), frame, subroutine);
/*  115 */       break;
/*      */     case 22:
/*  117 */       evalLoad(Type.LONG, iter.byteAt(pos + 1), frame, subroutine);
/*  118 */       break;
/*      */     case 23:
/*  120 */       evalLoad(Type.FLOAT, iter.byteAt(pos + 1), frame, subroutine);
/*  121 */       break;
/*      */     case 24:
/*  123 */       evalLoad(Type.DOUBLE, iter.byteAt(pos + 1), frame, subroutine);
/*  124 */       break;
/*      */     case 25:
/*  126 */       evalLoad(Type.OBJECT, iter.byteAt(pos + 1), frame, subroutine);
/*  127 */       break;
/*      */     case 26:
/*      */     case 27:
/*      */     case 28:
/*      */     case 29:
/*  132 */       evalLoad(Type.INTEGER, opcode - 26, frame, subroutine);
/*  133 */       break;
/*      */     case 30:
/*      */     case 31:
/*      */     case 32:
/*      */     case 33:
/*  138 */       evalLoad(Type.LONG, opcode - 30, frame, subroutine);
/*  139 */       break;
/*      */     case 34:
/*      */     case 35:
/*      */     case 36:
/*      */     case 37:
/*  144 */       evalLoad(Type.FLOAT, opcode - 34, frame, subroutine);
/*  145 */       break;
/*      */     case 38:
/*      */     case 39:
/*      */     case 40:
/*      */     case 41:
/*  150 */       evalLoad(Type.DOUBLE, opcode - 38, frame, subroutine);
/*  151 */       break;
/*      */     case 42:
/*      */     case 43:
/*      */     case 44:
/*      */     case 45:
/*  156 */       evalLoad(Type.OBJECT, opcode - 42, frame, subroutine);
/*  157 */       break;
/*      */     case 46:
/*  159 */       evalArrayLoad(Type.INTEGER, frame);
/*  160 */       break;
/*      */     case 47:
/*  162 */       evalArrayLoad(Type.LONG, frame);
/*  163 */       break;
/*      */     case 48:
/*  165 */       evalArrayLoad(Type.FLOAT, frame);
/*  166 */       break;
/*      */     case 49:
/*  168 */       evalArrayLoad(Type.DOUBLE, frame);
/*  169 */       break;
/*      */     case 50:
/*  171 */       evalArrayLoad(Type.OBJECT, frame);
/*  172 */       break;
/*      */     case 51:
/*      */     case 52:
/*      */     case 53:
/*  176 */       evalArrayLoad(Type.INTEGER, frame);
/*  177 */       break;
/*      */     case 54:
/*  179 */       evalStore(Type.INTEGER, iter.byteAt(pos + 1), frame, subroutine);
/*  180 */       break;
/*      */     case 55:
/*  182 */       evalStore(Type.LONG, iter.byteAt(pos + 1), frame, subroutine);
/*  183 */       break;
/*      */     case 56:
/*  185 */       evalStore(Type.FLOAT, iter.byteAt(pos + 1), frame, subroutine);
/*  186 */       break;
/*      */     case 57:
/*  188 */       evalStore(Type.DOUBLE, iter.byteAt(pos + 1), frame, subroutine);
/*  189 */       break;
/*      */     case 58:
/*  191 */       evalStore(Type.OBJECT, iter.byteAt(pos + 1), frame, subroutine);
/*  192 */       break;
/*      */     case 59:
/*      */     case 60:
/*      */     case 61:
/*      */     case 62:
/*  197 */       evalStore(Type.INTEGER, opcode - 59, frame, subroutine);
/*  198 */       break;
/*      */     case 63:
/*      */     case 64:
/*      */     case 65:
/*      */     case 66:
/*  203 */       evalStore(Type.LONG, opcode - 63, frame, subroutine);
/*  204 */       break;
/*      */     case 67:
/*      */     case 68:
/*      */     case 69:
/*      */     case 70:
/*  209 */       evalStore(Type.FLOAT, opcode - 67, frame, subroutine);
/*  210 */       break;
/*      */     case 71:
/*      */     case 72:
/*      */     case 73:
/*      */     case 74:
/*  215 */       evalStore(Type.DOUBLE, opcode - 71, frame, subroutine);
/*  216 */       break;
/*      */     case 75:
/*      */     case 76:
/*      */     case 77:
/*      */     case 78:
/*  221 */       evalStore(Type.OBJECT, opcode - 75, frame, subroutine);
/*  222 */       break;
/*      */     case 79:
/*  224 */       evalArrayStore(Type.INTEGER, frame);
/*  225 */       break;
/*      */     case 80:
/*  227 */       evalArrayStore(Type.LONG, frame);
/*  228 */       break;
/*      */     case 81:
/*  230 */       evalArrayStore(Type.FLOAT, frame);
/*  231 */       break;
/*      */     case 82:
/*  233 */       evalArrayStore(Type.DOUBLE, frame);
/*  234 */       break;
/*      */     case 83:
/*  236 */       evalArrayStore(Type.OBJECT, frame);
/*  237 */       break;
/*      */     case 84:
/*      */     case 85:
/*      */     case 86:
/*  241 */       evalArrayStore(Type.INTEGER, frame);
/*  242 */       break;
/*      */     case 87:
/*  244 */       if (frame.pop() == Type.TOP)
/*  245 */         throw new BadBytecode("POP can not be used with a category 2 value, pos = " + pos);
/*      */       break;
/*      */     case 88:
/*  248 */       frame.pop();
/*  249 */       frame.pop();
/*  250 */       break;
/*      */     case 89:
/*  252 */       Type type = frame.peek();
/*  253 */       if (type == Type.TOP) {
/*  254 */         throw new BadBytecode("DUP can not be used with a category 2 value, pos = " + pos);
/*      */       }
/*  256 */       frame.push(frame.peek());
/*  257 */       break;
/*      */     case 90:
/*      */     case 91:
/*  261 */       Type type = frame.peek();
/*  262 */       if (type == Type.TOP)
/*  263 */         throw new BadBytecode("DUP can not be used with a category 2 value, pos = " + pos);
/*  264 */       int end = frame.getTopIndex();
/*  265 */       int insert = end - (opcode - 90) - 1;
/*  266 */       frame.push(type);
/*      */ 
/*  268 */       while (end > insert) {
/*  269 */         frame.setStack(end, frame.getStack(end - 1));
/*  270 */         end--;
/*      */       }
/*  272 */       frame.setStack(insert, type);
/*  273 */       break;
/*      */     case 92:
/*  276 */       frame.push(frame.getStack(frame.getTopIndex() - 1));
/*  277 */       frame.push(frame.getStack(frame.getTopIndex() - 1));
/*  278 */       break;
/*      */     case 93:
/*      */     case 94:
/*  281 */       int end = frame.getTopIndex();
/*  282 */       int insert = end - (opcode - 93) - 1;
/*  283 */       Type type1 = frame.getStack(frame.getTopIndex() - 1);
/*  284 */       Type type2 = frame.peek();
/*  285 */       frame.push(type1);
/*  286 */       frame.push(type2);
/*  287 */       while (end > insert) {
/*  288 */         frame.setStack(end, frame.getStack(end - 2));
/*  289 */         end--;
/*      */       }
/*  291 */       frame.setStack(insert, type2);
/*  292 */       frame.setStack(insert - 1, type1);
/*  293 */       break;
/*      */     case 95:
/*  296 */       Type type1 = frame.pop();
/*  297 */       Type type2 = frame.pop();
/*  298 */       if ((type1.getSize() == 2) || (type2.getSize() == 2))
/*  299 */         throw new BadBytecode("Swap can not be used with category 2 values, pos = " + pos);
/*  300 */       frame.push(type1);
/*  301 */       frame.push(type2);
/*  302 */       break;
/*      */     case 96:
/*  307 */       evalBinaryMath(Type.INTEGER, frame);
/*  308 */       break;
/*      */     case 97:
/*  310 */       evalBinaryMath(Type.LONG, frame);
/*  311 */       break;
/*      */     case 98:
/*  313 */       evalBinaryMath(Type.FLOAT, frame);
/*  314 */       break;
/*      */     case 99:
/*  316 */       evalBinaryMath(Type.DOUBLE, frame);
/*  317 */       break;
/*      */     case 100:
/*  319 */       evalBinaryMath(Type.INTEGER, frame);
/*  320 */       break;
/*      */     case 101:
/*  322 */       evalBinaryMath(Type.LONG, frame);
/*  323 */       break;
/*      */     case 102:
/*  325 */       evalBinaryMath(Type.FLOAT, frame);
/*  326 */       break;
/*      */     case 103:
/*  328 */       evalBinaryMath(Type.DOUBLE, frame);
/*  329 */       break;
/*      */     case 104:
/*  331 */       evalBinaryMath(Type.INTEGER, frame);
/*  332 */       break;
/*      */     case 105:
/*  334 */       evalBinaryMath(Type.LONG, frame);
/*  335 */       break;
/*      */     case 106:
/*  337 */       evalBinaryMath(Type.FLOAT, frame);
/*  338 */       break;
/*      */     case 107:
/*  340 */       evalBinaryMath(Type.DOUBLE, frame);
/*  341 */       break;
/*      */     case 108:
/*  343 */       evalBinaryMath(Type.INTEGER, frame);
/*  344 */       break;
/*      */     case 109:
/*  346 */       evalBinaryMath(Type.LONG, frame);
/*  347 */       break;
/*      */     case 110:
/*  349 */       evalBinaryMath(Type.FLOAT, frame);
/*  350 */       break;
/*      */     case 111:
/*  352 */       evalBinaryMath(Type.DOUBLE, frame);
/*  353 */       break;
/*      */     case 112:
/*  355 */       evalBinaryMath(Type.INTEGER, frame);
/*  356 */       break;
/*      */     case 113:
/*  358 */       evalBinaryMath(Type.LONG, frame);
/*  359 */       break;
/*      */     case 114:
/*  361 */       evalBinaryMath(Type.FLOAT, frame);
/*  362 */       break;
/*      */     case 115:
/*  364 */       evalBinaryMath(Type.DOUBLE, frame);
/*  365 */       break;
/*      */     case 116:
/*  369 */       verifyAssignable(Type.INTEGER, simplePeek(frame));
/*  370 */       break;
/*      */     case 117:
/*  372 */       verifyAssignable(Type.LONG, simplePeek(frame));
/*  373 */       break;
/*      */     case 118:
/*  375 */       verifyAssignable(Type.FLOAT, simplePeek(frame));
/*  376 */       break;
/*      */     case 119:
/*  378 */       verifyAssignable(Type.DOUBLE, simplePeek(frame));
/*  379 */       break;
/*      */     case 120:
/*  383 */       evalShift(Type.INTEGER, frame);
/*  384 */       break;
/*      */     case 121:
/*  386 */       evalShift(Type.LONG, frame);
/*  387 */       break;
/*      */     case 122:
/*  389 */       evalShift(Type.INTEGER, frame);
/*  390 */       break;
/*      */     case 123:
/*  392 */       evalShift(Type.LONG, frame);
/*  393 */       break;
/*      */     case 124:
/*  395 */       evalShift(Type.INTEGER, frame);
/*  396 */       break;
/*      */     case 125:
/*  398 */       evalShift(Type.LONG, frame);
/*  399 */       break;
/*      */     case 126:
/*  403 */       evalBinaryMath(Type.INTEGER, frame);
/*  404 */       break;
/*      */     case 127:
/*  406 */       evalBinaryMath(Type.LONG, frame);
/*  407 */       break;
/*      */     case 128:
/*  409 */       evalBinaryMath(Type.INTEGER, frame);
/*  410 */       break;
/*      */     case 129:
/*  412 */       evalBinaryMath(Type.LONG, frame);
/*  413 */       break;
/*      */     case 130:
/*  415 */       evalBinaryMath(Type.INTEGER, frame);
/*  416 */       break;
/*      */     case 131:
/*  418 */       evalBinaryMath(Type.LONG, frame);
/*  419 */       break;
/*      */     case 132:
/*  422 */       int index = iter.byteAt(pos + 1);
/*  423 */       verifyAssignable(Type.INTEGER, frame.getLocal(index));
/*  424 */       access(index, Type.INTEGER, subroutine);
/*  425 */       break;
/*      */     case 133:
/*  430 */       verifyAssignable(Type.INTEGER, simplePop(frame));
/*  431 */       simplePush(Type.LONG, frame);
/*  432 */       break;
/*      */     case 134:
/*  434 */       verifyAssignable(Type.INTEGER, simplePop(frame));
/*  435 */       simplePush(Type.FLOAT, frame);
/*  436 */       break;
/*      */     case 135:
/*  438 */       verifyAssignable(Type.INTEGER, simplePop(frame));
/*  439 */       simplePush(Type.DOUBLE, frame);
/*  440 */       break;
/*      */     case 136:
/*  442 */       verifyAssignable(Type.LONG, simplePop(frame));
/*  443 */       simplePush(Type.INTEGER, frame);
/*  444 */       break;
/*      */     case 137:
/*  446 */       verifyAssignable(Type.LONG, simplePop(frame));
/*  447 */       simplePush(Type.FLOAT, frame);
/*  448 */       break;
/*      */     case 138:
/*  450 */       verifyAssignable(Type.LONG, simplePop(frame));
/*  451 */       simplePush(Type.DOUBLE, frame);
/*  452 */       break;
/*      */     case 139:
/*  454 */       verifyAssignable(Type.FLOAT, simplePop(frame));
/*  455 */       simplePush(Type.INTEGER, frame);
/*  456 */       break;
/*      */     case 140:
/*  458 */       verifyAssignable(Type.FLOAT, simplePop(frame));
/*  459 */       simplePush(Type.LONG, frame);
/*  460 */       break;
/*      */     case 141:
/*  462 */       verifyAssignable(Type.FLOAT, simplePop(frame));
/*  463 */       simplePush(Type.DOUBLE, frame);
/*  464 */       break;
/*      */     case 142:
/*  466 */       verifyAssignable(Type.DOUBLE, simplePop(frame));
/*  467 */       simplePush(Type.INTEGER, frame);
/*  468 */       break;
/*      */     case 143:
/*  470 */       verifyAssignable(Type.DOUBLE, simplePop(frame));
/*  471 */       simplePush(Type.LONG, frame);
/*  472 */       break;
/*      */     case 144:
/*  474 */       verifyAssignable(Type.DOUBLE, simplePop(frame));
/*  475 */       simplePush(Type.FLOAT, frame);
/*  476 */       break;
/*      */     case 145:
/*      */     case 146:
/*      */     case 147:
/*  480 */       verifyAssignable(Type.INTEGER, frame.peek());
/*  481 */       break;
/*      */     case 148:
/*  483 */       verifyAssignable(Type.LONG, simplePop(frame));
/*  484 */       verifyAssignable(Type.LONG, simplePop(frame));
/*  485 */       frame.push(Type.INTEGER);
/*  486 */       break;
/*      */     case 149:
/*      */     case 150:
/*  489 */       verifyAssignable(Type.FLOAT, simplePop(frame));
/*  490 */       verifyAssignable(Type.FLOAT, simplePop(frame));
/*  491 */       frame.push(Type.INTEGER);
/*  492 */       break;
/*      */     case 151:
/*      */     case 152:
/*  495 */       verifyAssignable(Type.DOUBLE, simplePop(frame));
/*  496 */       verifyAssignable(Type.DOUBLE, simplePop(frame));
/*  497 */       frame.push(Type.INTEGER);
/*  498 */       break;
/*      */     case 153:
/*      */     case 154:
/*      */     case 155:
/*      */     case 156:
/*      */     case 157:
/*      */     case 158:
/*  507 */       verifyAssignable(Type.INTEGER, simplePop(frame));
/*  508 */       break;
/*      */     case 159:
/*      */     case 160:
/*      */     case 161:
/*      */     case 162:
/*      */     case 163:
/*      */     case 164:
/*  515 */       verifyAssignable(Type.INTEGER, simplePop(frame));
/*  516 */       verifyAssignable(Type.INTEGER, simplePop(frame));
/*  517 */       break;
/*      */     case 165:
/*      */     case 166:
/*  520 */       verifyAssignable(Type.OBJECT, simplePop(frame));
/*  521 */       verifyAssignable(Type.OBJECT, simplePop(frame));
/*  522 */       break;
/*      */     case 167:
/*  524 */       break;
/*      */     case 168:
/*  526 */       frame.push(Type.RETURN_ADDRESS);
/*  527 */       break;
/*      */     case 169:
/*  529 */       verifyAssignable(Type.RETURN_ADDRESS, frame.getLocal(iter.byteAt(pos + 1)));
/*  530 */       break;
/*      */     case 170:
/*      */     case 171:
/*      */     case 172:
/*  534 */       verifyAssignable(Type.INTEGER, simplePop(frame));
/*  535 */       break;
/*      */     case 173:
/*  537 */       verifyAssignable(Type.LONG, simplePop(frame));
/*  538 */       break;
/*      */     case 174:
/*  540 */       verifyAssignable(Type.FLOAT, simplePop(frame));
/*  541 */       break;
/*      */     case 175:
/*  543 */       verifyAssignable(Type.DOUBLE, simplePop(frame));
/*  544 */       break;
/*      */     case 176:
/*      */       try {
/*  547 */         CtClass returnType = Descriptor.getReturnType(method.getDescriptor(), this.classPool);
/*  548 */         verifyAssignable(Type.get(returnType), simplePop(frame));
/*      */       } catch (NotFoundException e) {
/*  550 */         throw new RuntimeException(e);
/*      */       }
/*      */ 
/*      */     case 177:
/*  554 */       break;
/*      */     case 178:
/*  556 */       evalGetField(opcode, iter.u16bitAt(pos + 1), frame);
/*  557 */       break;
/*      */     case 179:
/*  559 */       evalPutField(opcode, iter.u16bitAt(pos + 1), frame);
/*  560 */       break;
/*      */     case 180:
/*  562 */       evalGetField(opcode, iter.u16bitAt(pos + 1), frame);
/*  563 */       break;
/*      */     case 181:
/*  565 */       evalPutField(opcode, iter.u16bitAt(pos + 1), frame);
/*  566 */       break;
/*      */     case 182:
/*      */     case 183:
/*      */     case 184:
/*  570 */       evalInvokeMethod(opcode, iter.u16bitAt(pos + 1), frame);
/*  571 */       break;
/*      */     case 185:
/*  573 */       evalInvokeIntfMethod(opcode, iter.u16bitAt(pos + 1), frame);
/*  574 */       break;
/*      */     case 186:
/*  576 */       throw new RuntimeException("Bad opcode 186");
/*      */     case 187:
/*  578 */       frame.push(resolveClassInfo(this.constPool.getClassInfo(iter.u16bitAt(pos + 1))));
/*  579 */       break;
/*      */     case 188:
/*  581 */       evalNewArray(pos, iter, frame);
/*  582 */       break;
/*      */     case 189:
/*  584 */       evalNewObjectArray(pos, iter, frame);
/*  585 */       break;
/*      */     case 190:
/*  587 */       Type array = simplePop(frame);
/*  588 */       if ((!array.isArray()) && (array != Type.UNINIT))
/*  589 */         throw new BadBytecode("Array length passed a non-array [pos = " + pos + "]: " + array);
/*  590 */       frame.push(Type.INTEGER);
/*  591 */       break;
/*      */     case 191:
/*  594 */       verifyAssignable(this.THROWABLE_TYPE, simplePop(frame));
/*  595 */       break;
/*      */     case 192:
/*  597 */       verifyAssignable(Type.OBJECT, simplePop(frame));
/*  598 */       frame.push(typeFromDesc(this.constPool.getClassInfo(iter.u16bitAt(pos + 1))));
/*  599 */       break;
/*      */     case 193:
/*  601 */       verifyAssignable(Type.OBJECT, simplePop(frame));
/*  602 */       frame.push(Type.INTEGER);
/*  603 */       break;
/*      */     case 194:
/*      */     case 195:
/*  606 */       verifyAssignable(Type.OBJECT, simplePop(frame));
/*  607 */       break;
/*      */     case 196:
/*  609 */       evalWide(pos, iter, frame, subroutine);
/*  610 */       break;
/*      */     case 197:
/*  612 */       evalNewObjectArray(pos, iter, frame);
/*  613 */       break;
/*      */     case 198:
/*      */     case 199:
/*  616 */       verifyAssignable(Type.OBJECT, simplePop(frame));
/*  617 */       break;
/*      */     case 200:
/*  619 */       break;
/*      */     case 201:
/*  621 */       frame.push(Type.RETURN_ADDRESS);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Type zeroExtend(Type type)
/*      */   {
/*  627 */     if ((type == Type.SHORT) || (type == Type.BYTE) || (type == Type.CHAR) || (type == Type.BOOLEAN)) {
/*  628 */       return Type.INTEGER;
/*      */     }
/*  630 */     return type;
/*      */   }
/*      */ 
/*      */   private void evalArrayLoad(Type expectedComponent, Frame frame) throws BadBytecode {
/*  634 */     Type index = frame.pop();
/*  635 */     Type array = frame.pop();
/*      */ 
/*  639 */     if (array == Type.UNINIT) {
/*  640 */       verifyAssignable(Type.INTEGER, index);
/*  641 */       if (expectedComponent == Type.OBJECT)
/*  642 */         simplePush(Type.UNINIT, frame);
/*      */       else {
/*  644 */         simplePush(expectedComponent, frame);
/*      */       }
/*  646 */       return;
/*      */     }
/*      */ 
/*  649 */     Type component = array.getComponent();
/*      */ 
/*  651 */     if (component == null) {
/*  652 */       throw new BadBytecode("Not an array! [pos = " + this.lastPos + "]: " + component);
/*      */     }
/*  654 */     component = zeroExtend(component);
/*      */ 
/*  656 */     verifyAssignable(expectedComponent, component);
/*  657 */     verifyAssignable(Type.INTEGER, index);
/*  658 */     simplePush(component, frame);
/*      */   }
/*      */ 
/*      */   private void evalArrayStore(Type expectedComponent, Frame frame) throws BadBytecode {
/*  662 */     Type value = simplePop(frame);
/*  663 */     Type index = frame.pop();
/*  664 */     Type array = frame.pop();
/*      */ 
/*  666 */     if (array == Type.UNINIT) {
/*  667 */       verifyAssignable(Type.INTEGER, index);
/*  668 */       return;
/*      */     }
/*      */ 
/*  671 */     Type component = array.getComponent();
/*      */ 
/*  673 */     if (component == null) {
/*  674 */       throw new BadBytecode("Not an array! [pos = " + this.lastPos + "]: " + component);
/*      */     }
/*  676 */     component = zeroExtend(component);
/*      */ 
/*  678 */     verifyAssignable(expectedComponent, component);
/*  679 */     verifyAssignable(Type.INTEGER, index);
/*      */ 
/*  687 */     if (expectedComponent == Type.OBJECT)
/*  688 */       verifyAssignable(expectedComponent, value);
/*      */     else
/*  690 */       verifyAssignable(component, value);
/*      */   }
/*      */ 
/*      */   private void evalBinaryMath(Type expected, Frame frame) throws BadBytecode
/*      */   {
/*  695 */     Type value2 = simplePop(frame);
/*  696 */     Type value1 = simplePop(frame);
/*      */ 
/*  698 */     verifyAssignable(expected, value2);
/*  699 */     verifyAssignable(expected, value1);
/*  700 */     simplePush(value1, frame);
/*      */   }
/*      */ 
/*      */   private void evalGetField(int opcode, int index, Frame frame) throws BadBytecode {
/*  704 */     String desc = this.constPool.getFieldrefType(index);
/*  705 */     Type type = zeroExtend(typeFromDesc(desc));
/*      */ 
/*  707 */     if (opcode == 180) {
/*  708 */       Type objectType = resolveClassInfo(this.constPool.getFieldrefClassName(index));
/*  709 */       verifyAssignable(objectType, simplePop(frame));
/*      */     }
/*      */ 
/*  712 */     simplePush(type, frame);
/*      */   }
/*      */ 
/*      */   private void evalInvokeIntfMethod(int opcode, int index, Frame frame) throws BadBytecode {
/*  716 */     String desc = this.constPool.getInterfaceMethodrefType(index);
/*  717 */     Type[] types = paramTypesFromDesc(desc);
/*  718 */     int i = types.length;
/*      */ 
/*  720 */     while (i > 0) {
/*  721 */       verifyAssignable(zeroExtend(types[(--i)]), simplePop(frame));
/*      */     }
/*  723 */     String classInfo = this.constPool.getInterfaceMethodrefClassName(index);
/*  724 */     Type objectType = resolveClassInfo(classInfo);
/*  725 */     verifyAssignable(objectType, simplePop(frame));
/*      */ 
/*  727 */     Type returnType = returnTypeFromDesc(desc);
/*  728 */     if (returnType != Type.VOID)
/*  729 */       simplePush(zeroExtend(returnType), frame);
/*      */   }
/*      */ 
/*      */   private void evalInvokeMethod(int opcode, int index, Frame frame) throws BadBytecode {
/*  733 */     String desc = this.constPool.getMethodrefType(index);
/*  734 */     Type[] types = paramTypesFromDesc(desc);
/*  735 */     int i = types.length;
/*      */ 
/*  737 */     while (i > 0) {
/*  738 */       verifyAssignable(zeroExtend(types[(--i)]), simplePop(frame));
/*      */     }
/*  740 */     if (opcode != 184) {
/*  741 */       Type objectType = resolveClassInfo(this.constPool.getMethodrefClassName(index));
/*  742 */       verifyAssignable(objectType, simplePop(frame));
/*      */     }
/*      */ 
/*  745 */     Type returnType = returnTypeFromDesc(desc);
/*  746 */     if (returnType != Type.VOID)
/*  747 */       simplePush(zeroExtend(returnType), frame);
/*      */   }
/*      */ 
/*      */   private void evalLDC(int index, Frame frame) throws BadBytecode
/*      */   {
/*  752 */     int tag = this.constPool.getTag(index);
/*      */     Type type;
/*  754 */     switch (tag) {
/*      */     case 8:
/*  756 */       type = this.STRING_TYPE;
/*  757 */       break;
/*      */     case 3:
/*  759 */       type = Type.INTEGER;
/*  760 */       break;
/*      */     case 4:
/*  762 */       type = Type.FLOAT;
/*  763 */       break;
/*      */     case 5:
/*  765 */       type = Type.LONG;
/*  766 */       break;
/*      */     case 6:
/*  768 */       type = Type.DOUBLE;
/*  769 */       break;
/*      */     case 7:
/*  771 */       type = this.CLASS_TYPE;
/*  772 */       break;
/*      */     default:
/*  774 */       throw new BadBytecode("bad LDC [pos = " + this.lastPos + "]: " + tag);
/*      */     }
/*      */ 
/*  777 */     simplePush(type, frame);
/*      */   }
/*      */ 
/*      */   private void evalLoad(Type expected, int index, Frame frame, Subroutine subroutine) throws BadBytecode {
/*  781 */     Type type = frame.getLocal(index);
/*      */ 
/*  783 */     verifyAssignable(expected, type);
/*      */ 
/*  785 */     simplePush(type, frame);
/*  786 */     access(index, type, subroutine);
/*      */   }
/*      */ 
/*      */   private void evalNewArray(int pos, CodeIterator iter, Frame frame) throws BadBytecode {
/*  790 */     verifyAssignable(Type.INTEGER, simplePop(frame));
/*  791 */     Type type = null;
/*  792 */     int typeInfo = iter.byteAt(pos + 1);
/*  793 */     switch (typeInfo) {
/*      */     case 4:
/*  795 */       type = getType("boolean[]");
/*  796 */       break;
/*      */     case 5:
/*  798 */       type = getType("char[]");
/*  799 */       break;
/*      */     case 8:
/*  801 */       type = getType("byte[]");
/*  802 */       break;
/*      */     case 9:
/*  804 */       type = getType("short[]");
/*  805 */       break;
/*      */     case 10:
/*  807 */       type = getType("int[]");
/*  808 */       break;
/*      */     case 11:
/*  810 */       type = getType("long[]");
/*  811 */       break;
/*      */     case 6:
/*  813 */       type = getType("float[]");
/*  814 */       break;
/*      */     case 7:
/*  816 */       type = getType("double[]");
/*  817 */       break;
/*      */     default:
/*  819 */       throw new BadBytecode("Invalid array type [pos = " + pos + "]: " + typeInfo);
/*      */     }
/*      */ 
/*  823 */     frame.push(type);
/*      */   }
/*      */ 
/*      */   private void evalNewObjectArray(int pos, CodeIterator iter, Frame frame) throws BadBytecode
/*      */   {
/*  828 */     Type type = resolveClassInfo(this.constPool.getClassInfo(iter.u16bitAt(pos + 1)));
/*  829 */     String name = type.getCtClass().getName();
/*  830 */     int opcode = iter.byteAt(pos);
/*      */     int dimensions;
/*      */     int dimensions;
/*  833 */     if (opcode == 197) {
/*  834 */       dimensions = iter.byteAt(pos + 3);
/*      */     } else {
/*  836 */       name = name + "[]";
/*  837 */       dimensions = 1;
/*      */     }
/*      */ 
/*  840 */     while (dimensions-- > 0) {
/*  841 */       verifyAssignable(Type.INTEGER, simplePop(frame));
/*      */     }
/*      */ 
/*  844 */     simplePush(getType(name), frame);
/*      */   }
/*      */ 
/*      */   private void evalPutField(int opcode, int index, Frame frame) throws BadBytecode {
/*  848 */     String desc = this.constPool.getFieldrefType(index);
/*  849 */     Type type = zeroExtend(typeFromDesc(desc));
/*      */ 
/*  851 */     verifyAssignable(type, simplePop(frame));
/*      */ 
/*  853 */     if (opcode == 181) {
/*  854 */       Type objectType = resolveClassInfo(this.constPool.getFieldrefClassName(index));
/*  855 */       verifyAssignable(objectType, simplePop(frame));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void evalShift(Type expected, Frame frame) throws BadBytecode {
/*  860 */     Type value2 = simplePop(frame);
/*  861 */     Type value1 = simplePop(frame);
/*      */ 
/*  863 */     verifyAssignable(Type.INTEGER, value2);
/*  864 */     verifyAssignable(expected, value1);
/*  865 */     simplePush(value1, frame);
/*      */   }
/*      */ 
/*      */   private void evalStore(Type expected, int index, Frame frame, Subroutine subroutine) throws BadBytecode {
/*  869 */     Type type = simplePop(frame);
/*      */ 
/*  872 */     if ((expected != Type.OBJECT) || (type != Type.RETURN_ADDRESS))
/*  873 */       verifyAssignable(expected, type);
/*  874 */     simpleSetLocal(index, type, frame);
/*  875 */     access(index, type, subroutine);
/*      */   }
/*      */ 
/*      */   private void evalWide(int pos, CodeIterator iter, Frame frame, Subroutine subroutine) throws BadBytecode {
/*  879 */     int opcode = iter.byteAt(pos + 1);
/*  880 */     int index = iter.u16bitAt(pos + 2);
/*  881 */     switch (opcode) {
/*      */     case 21:
/*  883 */       evalLoad(Type.INTEGER, index, frame, subroutine);
/*  884 */       break;
/*      */     case 22:
/*  886 */       evalLoad(Type.LONG, index, frame, subroutine);
/*  887 */       break;
/*      */     case 23:
/*  889 */       evalLoad(Type.FLOAT, index, frame, subroutine);
/*  890 */       break;
/*      */     case 24:
/*  892 */       evalLoad(Type.DOUBLE, index, frame, subroutine);
/*  893 */       break;
/*      */     case 25:
/*  895 */       evalLoad(Type.OBJECT, index, frame, subroutine);
/*  896 */       break;
/*      */     case 54:
/*  898 */       evalStore(Type.INTEGER, index, frame, subroutine);
/*  899 */       break;
/*      */     case 55:
/*  901 */       evalStore(Type.LONG, index, frame, subroutine);
/*  902 */       break;
/*      */     case 56:
/*  904 */       evalStore(Type.FLOAT, index, frame, subroutine);
/*  905 */       break;
/*      */     case 57:
/*  907 */       evalStore(Type.DOUBLE, index, frame, subroutine);
/*  908 */       break;
/*      */     case 58:
/*  910 */       evalStore(Type.OBJECT, index, frame, subroutine);
/*  911 */       break;
/*      */     case 132:
/*  913 */       verifyAssignable(Type.INTEGER, frame.getLocal(index));
/*  914 */       break;
/*      */     case 169:
/*  916 */       verifyAssignable(Type.RETURN_ADDRESS, frame.getLocal(index));
/*  917 */       break;
/*      */     default:
/*  919 */       throw new BadBytecode("Invalid WIDE operand [pos = " + pos + "]: " + opcode);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Type getType(String name) throws BadBytecode
/*      */   {
/*      */     try {
/*  926 */       return Type.get(this.classPool.get(name)); } catch (NotFoundException e) {
/*      */     }
/*  928 */     throw new BadBytecode("Could not find class [pos = " + this.lastPos + "]: " + name);
/*      */   }
/*      */ 
/*      */   private Type[] paramTypesFromDesc(String desc) throws BadBytecode
/*      */   {
/*  933 */     CtClass[] classes = null;
/*      */     try {
/*  935 */       classes = Descriptor.getParameterTypes(desc, this.classPool);
/*      */     } catch (NotFoundException e) {
/*  937 */       throw new BadBytecode("Could not find class in descriptor [pos = " + this.lastPos + "]: " + e.getMessage());
/*      */     }
/*      */ 
/*  940 */     if (classes == null) {
/*  941 */       throw new BadBytecode("Could not obtain parameters for descriptor [pos = " + this.lastPos + "]: " + desc);
/*      */     }
/*  943 */     Type[] types = new Type[classes.length];
/*  944 */     for (int i = 0; i < types.length; i++) {
/*  945 */       types[i] = Type.get(classes[i]);
/*      */     }
/*  947 */     return types;
/*      */   }
/*      */ 
/*      */   private Type returnTypeFromDesc(String desc) throws BadBytecode {
/*  951 */     CtClass clazz = null;
/*      */     try {
/*  953 */       clazz = Descriptor.getReturnType(desc, this.classPool);
/*      */     } catch (NotFoundException e) {
/*  955 */       throw new BadBytecode("Could not find class in descriptor [pos = " + this.lastPos + "]: " + e.getMessage());
/*      */     }
/*      */ 
/*  958 */     if (clazz == null) {
/*  959 */       throw new BadBytecode("Could not obtain return type for descriptor [pos = " + this.lastPos + "]: " + desc);
/*      */     }
/*  961 */     return Type.get(clazz);
/*      */   }
/*      */ 
/*      */   private Type simplePeek(Frame frame) {
/*  965 */     Type type = frame.peek();
/*  966 */     return type == Type.TOP ? frame.getStack(frame.getTopIndex() - 1) : type;
/*      */   }
/*      */ 
/*      */   private Type simplePop(Frame frame) {
/*  970 */     Type type = frame.pop();
/*  971 */     return type == Type.TOP ? frame.pop() : type;
/*      */   }
/*      */ 
/*      */   private void simplePush(Type type, Frame frame) {
/*  975 */     frame.push(type);
/*  976 */     if (type.getSize() == 2)
/*  977 */       frame.push(Type.TOP);
/*      */   }
/*      */ 
/*      */   private void access(int index, Type type, Subroutine subroutine) {
/*  981 */     if (subroutine == null)
/*  982 */       return;
/*  983 */     subroutine.access(index);
/*  984 */     if (type.getSize() == 2)
/*  985 */       subroutine.access(index + 1);
/*      */   }
/*      */ 
/*      */   private void simpleSetLocal(int index, Type type, Frame frame) {
/*  989 */     frame.setLocal(index, type);
/*  990 */     if (type.getSize() == 2)
/*  991 */       frame.setLocal(index + 1, Type.TOP);
/*      */   }
/*      */ 
/*      */   private Type resolveClassInfo(String info) throws BadBytecode {
/*  995 */     CtClass clazz = null;
/*      */     try {
/*  997 */       if (info.charAt(0) == '[')
/*  998 */         clazz = Descriptor.toCtClass(info, this.classPool);
/*      */       else
/* 1000 */         clazz = this.classPool.get(info);
/*      */     }
/*      */     catch (NotFoundException e)
/*      */     {
/* 1004 */       throw new BadBytecode("Could not find class in descriptor [pos = " + this.lastPos + "]: " + e.getMessage());
/*      */     }
/*      */ 
/* 1007 */     if (clazz == null) {
/* 1008 */       throw new BadBytecode("Could not obtain type for descriptor [pos = " + this.lastPos + "]: " + info);
/*      */     }
/* 1010 */     return Type.get(clazz);
/*      */   }
/*      */ 
/*      */   private Type typeFromDesc(String desc) throws BadBytecode {
/* 1014 */     CtClass clazz = null;
/*      */     try {
/* 1016 */       clazz = Descriptor.toCtClass(desc, this.classPool);
/*      */     } catch (NotFoundException e) {
/* 1018 */       throw new BadBytecode("Could not find class in descriptor [pos = " + this.lastPos + "]: " + e.getMessage());
/*      */     }
/*      */ 
/* 1021 */     if (clazz == null) {
/* 1022 */       throw new BadBytecode("Could not obtain type for descriptor [pos = " + this.lastPos + "]: " + desc);
/*      */     }
/* 1024 */     return Type.get(clazz);
/*      */   }
/*      */ 
/*      */   private void verifyAssignable(Type expected, Type type) throws BadBytecode {
/* 1028 */     if (!expected.isAssignableFrom(type))
/* 1029 */       throw new BadBytecode("Expected type: " + expected + " Got: " + type + " [pos = " + this.lastPos + "]");
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.analysis.Executor
 * JD-Core Version:    0.6.2
 */