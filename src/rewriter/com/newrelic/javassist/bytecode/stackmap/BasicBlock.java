/*     */ package com.newrelic.javassist.bytecode.stackmap;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.ExceptionTable;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public class BasicBlock
/*     */ {
/*     */   public int position;
/*     */   public int length;
/*     */   public int incoming;
/*     */   public BasicBlock[] exit;
/*     */   public boolean stop;
/*     */   public Catch toCatch;
/*     */ 
/*     */   protected BasicBlock(int pos)
/*     */   {
/*  30 */     this.position = pos;
/*  31 */     this.length = 0;
/*  32 */     this.incoming = 0;
/*     */   }
/*     */ 
/*     */   public static BasicBlock find(BasicBlock[] blocks, int pos)
/*     */     throws BadBytecode
/*     */   {
/*  38 */     for (int i = 0; i < blocks.length; i++) {
/*  39 */       int iPos = blocks[i].position;
/*  40 */       if ((iPos <= pos) && (pos < iPos + blocks[i].length)) {
/*  41 */         return blocks[i];
/*     */       }
/*     */     }
/*  44 */     throw new BadBytecode("no basic block at " + pos);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  59 */     StringBuffer sbuf = new StringBuffer();
/*  60 */     String cname = getClass().getName();
/*  61 */     int i = cname.lastIndexOf('.');
/*  62 */     sbuf.append(i < 0 ? cname : cname.substring(i + 1));
/*  63 */     sbuf.append("[");
/*  64 */     toString2(sbuf);
/*  65 */     sbuf.append("]");
/*  66 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   protected void toString2(StringBuffer sbuf) {
/*  70 */     sbuf.append("pos=").append(this.position).append(", len=").append(this.length).append(", in=").append(this.incoming).append(", exit{");
/*     */ 
/*  73 */     if (this.exit != null) {
/*  74 */       for (int i = 0; i < this.exit.length; i++) {
/*  75 */         sbuf.append(this.exit[i].position).append(", ");
/*     */       }
/*     */     }
/*  78 */     sbuf.append("}, {");
/*  79 */     Catch th = this.toCatch;
/*  80 */     while (th != null) {
/*  81 */       sbuf.append("(").append(th.body.position).append(", ").append(th.typeIndex).append("), ");
/*     */ 
/*  83 */       th = th.next;
/*     */     }
/*     */ 
/*  86 */     sbuf.append("}");
/*     */   }
/*     */ 
/*     */   public static class Maker
/*     */   {
/*     */     protected BasicBlock makeBlock(int pos)
/*     */     {
/* 127 */       return new BasicBlock(pos);
/*     */     }
/*     */ 
/*     */     protected BasicBlock[] makeArray(int size) {
/* 131 */       return new BasicBlock[size];
/*     */     }
/*     */ 
/*     */     private BasicBlock[] makeArray(BasicBlock b) {
/* 135 */       BasicBlock[] array = makeArray(1);
/* 136 */       array[0] = b;
/* 137 */       return array;
/*     */     }
/*     */ 
/*     */     private BasicBlock[] makeArray(BasicBlock b1, BasicBlock b2) {
/* 141 */       BasicBlock[] array = makeArray(2);
/* 142 */       array[0] = b1;
/* 143 */       array[1] = b2;
/* 144 */       return array;
/*     */     }
/*     */ 
/*     */     public BasicBlock[] make(MethodInfo minfo) throws BadBytecode {
/* 148 */       CodeAttribute ca = minfo.getCodeAttribute();
/* 149 */       if (ca == null) {
/* 150 */         return null;
/*     */       }
/* 152 */       CodeIterator ci = ca.iterator();
/* 153 */       return make(ci, 0, ci.getCodeLength(), ca.getExceptionTable());
/*     */     }
/*     */ 
/*     */     public BasicBlock[] make(CodeIterator ci, int begin, int end, ExceptionTable et)
/*     */       throws BadBytecode
/*     */     {
/* 160 */       HashMap marks = makeMarks(ci, begin, end, et);
/* 161 */       BasicBlock[] bb = makeBlocks(marks);
/* 162 */       addCatchers(bb, et);
/* 163 */       return bb;
/*     */     }
/*     */ 
/*     */     private BasicBlock.Mark makeMark(HashMap table, int pos)
/*     */     {
/* 169 */       return makeMark0(table, pos, true, true);
/*     */     }
/*     */ 
/*     */     private BasicBlock.Mark makeMark(HashMap table, int pos, BasicBlock[] jump, int size, boolean always)
/*     */     {
/* 177 */       BasicBlock.Mark m = makeMark0(table, pos, false, false);
/* 178 */       m.setJump(jump, size, always);
/* 179 */       return m;
/*     */     }
/*     */ 
/*     */     private BasicBlock.Mark makeMark0(HashMap table, int pos, boolean isBlockBegin, boolean isTarget)
/*     */     {
/* 184 */       Integer p = new Integer(pos);
/* 185 */       BasicBlock.Mark m = (BasicBlock.Mark)table.get(p);
/* 186 */       if (m == null) {
/* 187 */         m = new BasicBlock.Mark(pos);
/* 188 */         table.put(p, m);
/*     */       }
/*     */ 
/* 191 */       if (isBlockBegin) {
/* 192 */         if (m.block == null) {
/* 193 */           m.block = makeBlock(pos);
/*     */         }
/* 195 */         if (isTarget) {
/* 196 */           m.block.incoming += 1;
/*     */         }
/*     */       }
/* 199 */       return m;
/*     */     }
/*     */ 
/*     */     private HashMap makeMarks(CodeIterator ci, int begin, int end, ExceptionTable et)
/*     */       throws BadBytecode
/*     */     {
/* 206 */       ci.begin();
/* 207 */       ci.move(begin);
/* 208 */       HashMap marks = new HashMap();
/* 209 */       while (ci.hasNext()) {
/* 210 */         int index = ci.next();
/* 211 */         if (index >= end) {
/*     */           break;
/*     */         }
/* 214 */         int op = ci.byteAt(index);
/* 215 */         if (((153 <= op) && (op <= 166)) || (op == 198) || (op == 199))
/*     */         {
/* 217 */           BasicBlock.Mark to = makeMark(marks, index + ci.s16bitAt(index + 1));
/* 218 */           BasicBlock.Mark next = makeMark(marks, index + 3);
/* 219 */           makeMark(marks, index, makeArray(to.block, next.block), 3, false);
/*     */         }
/* 221 */         else if ((167 <= op) && (op <= 171)) {
/* 222 */           switch (op) {
/*     */           case 167:
/*     */           case 168:
/* 225 */             makeGotoJsr(marks, index, index + ci.s16bitAt(index + 1), op == 167, 3);
/*     */ 
/* 227 */             break;
/*     */           case 169:
/* 229 */             makeMark(marks, index, null, 1, true);
/* 230 */             break;
/*     */           case 170:
/* 232 */             int pos = (index & 0xFFFFFFFC) + 4;
/* 233 */             int low = ci.s32bitAt(pos + 4);
/* 234 */             int high = ci.s32bitAt(pos + 8);
/* 235 */             int ncases = high - low + 1;
/* 236 */             BasicBlock[] to = makeArray(ncases + 1);
/* 237 */             to[0] = makeMark(marks, index + ci.s32bitAt(pos)).block;
/* 238 */             int p = pos + 12;
/* 239 */             int n = p + ncases * 4;
/* 240 */             int k = 1;
/* 241 */             while (p < n) {
/* 242 */               to[(k++)] = makeMark(marks, index + ci.s32bitAt(p)).block;
/* 243 */               p += 4;
/*     */             }
/* 245 */             makeMark(marks, index, to, n - index, true);
/* 246 */             break;
/*     */           case 171:
/* 248 */             int pos = (index & 0xFFFFFFFC) + 4;
/* 249 */             int ncases = ci.s32bitAt(pos + 4);
/* 250 */             BasicBlock[] to = makeArray(ncases + 1);
/* 251 */             to[0] = makeMark(marks, index + ci.s32bitAt(pos)).block;
/* 252 */             int p = pos + 8 + 4;
/* 253 */             int n = p + ncases * 8 - 4;
/* 254 */             int k = 1;
/* 255 */             while (p < n) {
/* 256 */               to[(k++)] = makeMark(marks, index + ci.s32bitAt(p)).block;
/* 257 */               p += 8;
/*     */             }
/* 259 */             makeMark(marks, index, to, n - index, true);
/* 260 */             break;
/*     */           }
/* 262 */         } else if (((172 <= op) && (op <= 177)) || (op == 191)) {
/* 263 */           makeMark(marks, index, null, 1, true);
/* 264 */         } else if ((op == 200) || (op == 201)) {
/* 265 */           makeGotoJsr(marks, index, index + ci.s32bitAt(index + 1), op == 200, 5);
/*     */         }
/* 267 */         else if ((op == 196) && (ci.byteAt(index + 1) == 169)) {
/* 268 */           makeMark(marks, index, null, 1, true);
/*     */         }
/*     */       }
/* 271 */       if (et != null) {
/* 272 */         int i = et.size();
/*     */         while (true) { i--; if (i < 0) break;
/* 274 */           makeMark0(marks, et.startPc(i), true, false);
/* 275 */           makeMark(marks, et.handlerPc(i));
/*     */         }
/*     */       }
/*     */ 
/* 279 */       return marks;
/*     */     }
/*     */ 
/*     */     private void makeGotoJsr(HashMap marks, int pos, int target, boolean isGoto, int size) {
/* 283 */       BasicBlock.Mark to = makeMark(marks, target);
/*     */       BasicBlock[] jumps;
/*     */       BasicBlock[] jumps;
/* 285 */       if (isGoto) {
/* 286 */         jumps = makeArray(to.block);
/*     */       } else {
/* 288 */         BasicBlock.Mark next = makeMark(marks, pos + size);
/* 289 */         jumps = makeArray(to.block, next.block);
/*     */       }
/*     */ 
/* 292 */       makeMark(marks, pos, jumps, size, isGoto);
/*     */     }
/*     */ 
/*     */     private BasicBlock[] makeBlocks(HashMap markTable) {
/* 296 */       BasicBlock.Mark[] marks = (BasicBlock.Mark[])markTable.values().toArray(new BasicBlock.Mark[markTable.size()]);
/*     */ 
/* 298 */       Arrays.sort(marks);
/* 299 */       ArrayList blocks = new ArrayList();
/* 300 */       int i = 0;
/*     */       BasicBlock prev;
/*     */       BasicBlock prev;
/* 302 */       if ((marks.length > 0) && (marks[0].position == 0) && (marks[0].block != null))
/* 303 */         prev = getBBlock(marks[(i++)]);
/*     */       else {
/* 305 */         prev = makeBlock(0);
/*     */       }
/* 307 */       blocks.add(prev);
/* 308 */       while (i < marks.length) {
/* 309 */         BasicBlock.Mark m = marks[(i++)];
/* 310 */         BasicBlock bb = getBBlock(m);
/* 311 */         if (bb == null)
/*     */         {
/* 313 */           if (prev.length > 0)
/*     */           {
/* 315 */             prev = makeBlock(prev.position + prev.length);
/* 316 */             blocks.add(prev);
/*     */           }
/*     */ 
/* 319 */           prev.length = (m.position + m.size - prev.position);
/* 320 */           prev.exit = m.jump;
/* 321 */           prev.stop = m.alwaysJmp;
/*     */         }
/*     */         else
/*     */         {
/* 325 */           if (prev.length == 0) {
/* 326 */             prev.length = (m.position - prev.position);
/* 327 */             bb.incoming += 1;
/* 328 */             prev.exit = makeArray(bb);
/*     */           }
/*     */           else
/*     */           {
/* 332 */             int prevPos = prev.position;
/* 333 */             if (prevPos + prev.length < m.position) {
/* 334 */               prev = makeBlock(prevPos + prev.length);
/* 335 */               prev.length = (m.position - prevPos);
/*     */ 
/* 338 */               prev.exit = makeArray(bb);
/*     */             }
/*     */           }
/*     */ 
/* 342 */           blocks.add(bb);
/* 343 */           prev = bb;
/*     */         }
/*     */       }
/*     */ 
/* 347 */       return (BasicBlock[])blocks.toArray(makeArray(blocks.size()));
/*     */     }
/*     */ 
/*     */     private static BasicBlock getBBlock(BasicBlock.Mark m) {
/* 351 */       BasicBlock b = m.block;
/* 352 */       if ((b != null) && (m.size > 0)) {
/* 353 */         b.exit = m.jump;
/* 354 */         b.length = m.size;
/* 355 */         b.stop = m.alwaysJmp;
/*     */       }
/*     */ 
/* 358 */       return b;
/*     */     }
/*     */ 
/*     */     private void addCatchers(BasicBlock[] blocks, ExceptionTable et)
/*     */       throws BadBytecode
/*     */     {
/* 364 */       if (et == null) {
/* 365 */         return;
/*     */       }
/* 367 */       int i = et.size();
/*     */       while (true) { i--; if (i < 0) break;
/* 369 */         BasicBlock handler = BasicBlock.find(blocks, et.handlerPc(i));
/* 370 */         int start = et.startPc(i);
/* 371 */         int end = et.endPc(i);
/* 372 */         int type = et.catchType(i);
/* 373 */         handler.incoming -= 1;
/* 374 */         for (int k = 0; k < blocks.length; k++) {
/* 375 */           BasicBlock bb = blocks[k];
/* 376 */           int iPos = bb.position;
/* 377 */           if ((start <= iPos) && (iPos < end)) {
/* 378 */             bb.toCatch = new BasicBlock.Catch(handler, type, bb.toCatch);
/* 379 */             handler.incoming += 1;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Mark
/*     */     implements Comparable
/*     */   {
/*     */     int position;
/*     */     BasicBlock block;
/*     */     BasicBlock[] jump;
/*     */     boolean alwaysJmp;
/*     */     int size;
/*     */     BasicBlock.Catch catcher;
/*     */ 
/*     */     Mark(int p)
/*     */     {
/*  98 */       this.position = p;
/*  99 */       this.block = null;
/* 100 */       this.jump = null;
/* 101 */       this.alwaysJmp = false;
/* 102 */       this.size = 0;
/* 103 */       this.catcher = null;
/*     */     }
/*     */ 
/*     */     public int compareTo(Object obj) {
/* 107 */       if ((obj instanceof Mark)) {
/* 108 */         int pos = ((Mark)obj).position;
/* 109 */         return this.position - pos;
/*     */       }
/*     */ 
/* 112 */       return -1;
/*     */     }
/*     */ 
/*     */     void setJump(BasicBlock[] bb, int s, boolean always) {
/* 116 */       this.jump = bb;
/* 117 */       this.size = s;
/* 118 */       this.alwaysJmp = always;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Catch
/*     */   {
/*     */     Catch next;
/*     */     BasicBlock body;
/*     */     int typeIndex;
/*     */ 
/*     */     Catch(BasicBlock b, int i, Catch c)
/*     */     {
/*  52 */       this.body = b;
/*  53 */       this.typeIndex = i;
/*  54 */       this.next = c;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.stackmap.BasicBlock
 * JD-Core Version:    0.6.2
 */