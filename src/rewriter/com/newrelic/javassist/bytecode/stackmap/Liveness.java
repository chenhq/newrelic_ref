/*     */ package com.newrelic.javassist.bytecode.stackmap;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ 
/*     */ public class Liveness
/*     */ {
/*     */   protected static final byte UNKNOWN = 0;
/*     */   protected static final byte READ = 1;
/*     */   protected static final byte UPDATED = 2;
/*     */   protected byte[] localsUsage;
/*  32 */   public static boolean useArgs = true;
/*     */   static final int NOT_YET = 0;
/*     */   static final int CHANGED_LAST = 1;
/*     */   static final int DONE = 2;
/*     */   static final int CHANGED_NOW = 3;
/*     */ 
/*     */   public void compute(CodeIterator ci, TypedBlock[] blocks, int maxLocals, TypeData[] args)
/*     */     throws BadBytecode
/*     */   {
/*  38 */     computeUsage(ci, blocks, maxLocals);
/*  39 */     if (useArgs) {
/*  40 */       useAllArgs(blocks, args);
/*     */     }
/*  42 */     computeLiveness1(blocks[0]);
/*  43 */     while (hasChanged(blocks))
/*  44 */       computeLiveness2(blocks[0]);
/*     */   }
/*     */ 
/*     */   private void useAllArgs(TypedBlock[] blocks, TypeData[] args) {
/*  48 */     for (int k = 0; k < blocks.length; k++) {
/*  49 */       byte[] usage = blocks[k].localsUsage;
/*  50 */       for (int i = 0; i < args.length; i++)
/*  51 */         if (args[i] != TypeTag.TOP)
/*  52 */           usage[i] = 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void computeLiveness1(TypedBlock tb)
/*     */   {
/*  62 */     if (tb.updating)
/*     */     {
/*  64 */       computeLiveness1u(tb);
/*  65 */       return;
/*     */     }
/*     */ 
/*  68 */     if (tb.inputs != null) {
/*  69 */       return;
/*     */     }
/*  71 */     tb.updating = true;
/*  72 */     byte[] usage = tb.localsUsage;
/*  73 */     int n = usage.length;
/*  74 */     boolean[] in = new boolean[n];
/*  75 */     for (int i = 0; i < n; i++) {
/*  76 */       in[i] = (usage[i] == 1 ? 1 : false);
/*     */     }
/*  78 */     BasicBlock.Catch handlers = tb.toCatch;
/*  79 */     while (handlers != null) {
/*  80 */       TypedBlock h = (TypedBlock)handlers.body;
/*  81 */       computeLiveness1(h);
/*  82 */       for (int k = 0; k < n; k++) {
/*  83 */         if (h.inputs[k] != 0)
/*  84 */           in[k] = true;
/*     */       }
/*  86 */       handlers = handlers.next;
/*     */     }
/*     */ 
/*  89 */     if (tb.exit != null) {
/*  90 */       for (int i = 0; i < tb.exit.length; i++) {
/*  91 */         TypedBlock e = (TypedBlock)tb.exit[i];
/*  92 */         computeLiveness1(e);
/*  93 */         for (int k = 0; k < n; k++) {
/*  94 */           if (in[k] == 0)
/*  95 */             in[k] = ((usage[k] == 0) && (e.inputs[k] != 0) ? 1 : false);
/*     */         }
/*     */       }
/*     */     }
/*  99 */     tb.updating = false;
/* 100 */     if (tb.inputs == null) {
/* 101 */       tb.inputs = in;
/* 102 */       tb.status = 2;
/*     */     }
/*     */     else {
/* 105 */       for (int i = 0; i < n; i++)
/* 106 */         if ((in[i] != 0) && (tb.inputs[i] == 0)) {
/* 107 */           tb.inputs[i] = true;
/* 108 */           tb.status = 3;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void computeLiveness1u(TypedBlock tb) {
/* 114 */     if (tb.inputs == null) {
/* 115 */       byte[] usage = tb.localsUsage;
/* 116 */       int n = usage.length;
/* 117 */       boolean[] in = new boolean[n];
/* 118 */       for (int i = 0; i < n; i++) {
/* 119 */         in[i] = (usage[i] == 1 ? 1 : false);
/*     */       }
/* 121 */       tb.inputs = in;
/* 122 */       tb.status = 2;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void computeLiveness2(TypedBlock tb) {
/* 127 */     if ((tb.updating) || (tb.status >= 2)) {
/* 128 */       return;
/*     */     }
/* 130 */     tb.updating = true;
/* 131 */     if (tb.exit == null) {
/* 132 */       tb.status = 2;
/*     */     } else {
/* 134 */       boolean changed = false;
/* 135 */       for (int i = 0; i < tb.exit.length; i++) {
/* 136 */         TypedBlock e = (TypedBlock)tb.exit[i];
/* 137 */         computeLiveness2(e);
/* 138 */         if (e.status != 2) {
/* 139 */           changed = true;
/*     */         }
/*     */       }
/* 142 */       if (changed) {
/* 143 */         changed = false;
/* 144 */         byte[] usage = tb.localsUsage;
/* 145 */         int n = usage.length;
/* 146 */         for (int i = 0; i < tb.exit.length; i++) {
/* 147 */           TypedBlock e = (TypedBlock)tb.exit[i];
/* 148 */           if (e.status != 2) {
/* 149 */             for (int k = 0; k < n; k++) {
/* 150 */               if ((tb.inputs[k] == 0) && 
/* 151 */                 (usage[k] == 0) && (e.inputs[k] != 0)) {
/* 152 */                 tb.inputs[k] = true;
/* 153 */                 changed = true;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 158 */         tb.status = (changed ? 3 : 2);
/*     */       }
/*     */       else {
/* 161 */         tb.status = 2;
/*     */       }
/*     */     }
/* 164 */     if (computeLiveness2except(tb)) {
/* 165 */       tb.status = 3;
/*     */     }
/* 167 */     tb.updating = false;
/*     */   }
/*     */ 
/*     */   private boolean computeLiveness2except(TypedBlock tb) {
/* 171 */     BasicBlock.Catch handlers = tb.toCatch;
/* 172 */     boolean changed = false;
/* 173 */     while (handlers != null) {
/* 174 */       TypedBlock h = (TypedBlock)handlers.body;
/* 175 */       computeLiveness2(h);
/* 176 */       if (h.status != 2) {
/* 177 */         boolean[] in = tb.inputs;
/* 178 */         int n = in.length;
/* 179 */         for (int k = 0; k < n; k++) {
/* 180 */           if ((in[k] == 0) && (h.inputs[k] != 0)) {
/* 181 */             in[k] = true;
/* 182 */             changed = true;
/*     */           }
/*     */         }
/*     */       }
/* 186 */       handlers = handlers.next;
/*     */     }
/*     */ 
/* 189 */     return changed;
/*     */   }
/*     */ 
/*     */   private boolean hasChanged(TypedBlock[] blocks) {
/* 193 */     int n = blocks.length;
/* 194 */     boolean changed = false;
/* 195 */     for (int i = 0; i < n; i++) {
/* 196 */       TypedBlock tb = blocks[i];
/* 197 */       if (tb.status == 3) {
/* 198 */         tb.status = 1;
/* 199 */         changed = true;
/*     */       }
/*     */       else {
/* 202 */         tb.status = 0;
/*     */       }
/*     */     }
/* 205 */     return changed;
/*     */   }
/*     */ 
/*     */   private void computeUsage(CodeIterator ci, TypedBlock[] blocks, int maxLocals)
/*     */     throws BadBytecode
/*     */   {
/* 211 */     int n = blocks.length;
/* 212 */     for (int i = 0; i < n; i++) {
/* 213 */       TypedBlock tb = blocks[i];
/* 214 */       this.localsUsage = (tb.localsUsage = new byte[maxLocals]);
/* 215 */       int pos = tb.position;
/* 216 */       analyze(ci, pos, pos + tb.length);
/* 217 */       this.localsUsage = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void readLocal(int reg) {
/* 222 */     if (this.localsUsage[reg] == 0)
/* 223 */       this.localsUsage[reg] = 1;
/*     */   }
/*     */ 
/*     */   protected final void writeLocal(int reg) {
/* 227 */     if (this.localsUsage[reg] == 0)
/* 228 */       this.localsUsage[reg] = 2;
/*     */   }
/*     */ 
/*     */   protected void analyze(CodeIterator ci, int begin, int end)
/*     */     throws BadBytecode
/*     */   {
/* 234 */     ci.begin();
/* 235 */     ci.move(begin);
/* 236 */     while (ci.hasNext()) {
/* 237 */       int index = ci.next();
/* 238 */       if (index >= end) {
/*     */         break;
/*     */       }
/* 241 */       int op = ci.byteAt(index);
/* 242 */       if (op < 96) {
/* 243 */         if (op < 54)
/* 244 */           doOpcode0_53(ci, index, op);
/*     */         else
/* 246 */           doOpcode54_95(ci, index, op);
/*     */       }
/* 248 */       else if (op == 132)
/*     */       {
/* 250 */         readLocal(ci.byteAt(index + 1));
/*     */       }
/* 252 */       else if (op == 196)
/* 253 */         doWIDE(ci, index);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doOpcode0_53(CodeIterator ci, int pos, int op) {
/* 258 */     switch (op) {
/*     */     case 21:
/*     */     case 22:
/*     */     case 23:
/*     */     case 24:
/*     */     case 25:
/* 264 */       readLocal(ci.byteAt(pos + 1));
/* 265 */       break;
/*     */     case 26:
/*     */     case 27:
/*     */     case 28:
/*     */     case 29:
/* 270 */       readLocal(op - 26);
/* 271 */       break;
/*     */     case 30:
/*     */     case 31:
/*     */     case 32:
/*     */     case 33:
/* 276 */       readLocal(op - 30);
/* 277 */       break;
/*     */     case 34:
/*     */     case 35:
/*     */     case 36:
/*     */     case 37:
/* 282 */       readLocal(op - 34);
/* 283 */       break;
/*     */     case 38:
/*     */     case 39:
/*     */     case 40:
/*     */     case 41:
/* 288 */       readLocal(op - 38);
/* 289 */       break;
/*     */     case 42:
/*     */     case 43:
/*     */     case 44:
/*     */     case 45:
/* 294 */       readLocal(op - 42);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doOpcode54_95(CodeIterator ci, int pos, int op)
/*     */   {
/* 300 */     switch (op) {
/*     */     case 54:
/*     */     case 55:
/*     */     case 56:
/*     */     case 57:
/*     */     case 58:
/* 306 */       writeLocal(ci.byteAt(pos + 1));
/* 307 */       break;
/*     */     case 59:
/*     */     case 60:
/*     */     case 61:
/*     */     case 62:
/* 312 */       writeLocal(op - 59);
/* 313 */       break;
/*     */     case 63:
/*     */     case 64:
/*     */     case 65:
/*     */     case 66:
/* 318 */       writeLocal(op - 63);
/* 319 */       break;
/*     */     case 67:
/*     */     case 68:
/*     */     case 69:
/*     */     case 70:
/* 324 */       writeLocal(op - 67);
/* 325 */       break;
/*     */     case 71:
/*     */     case 72:
/*     */     case 73:
/*     */     case 74:
/* 330 */       writeLocal(op - 71);
/* 331 */       break;
/*     */     case 75:
/*     */     case 76:
/*     */     case 77:
/*     */     case 78:
/* 336 */       writeLocal(op - 75);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doWIDE(CodeIterator ci, int pos) throws BadBytecode
/*     */   {
/* 342 */     int op = ci.byteAt(pos + 1);
/* 343 */     int var = ci.u16bitAt(pos + 2);
/* 344 */     switch (op) {
/*     */     case 21:
/*     */     case 22:
/*     */     case 23:
/*     */     case 24:
/*     */     case 25:
/* 350 */       readLocal(var);
/* 351 */       break;
/*     */     case 54:
/*     */     case 55:
/*     */     case 56:
/*     */     case 57:
/*     */     case 58:
/* 357 */       writeLocal(var);
/* 358 */       break;
/*     */     case 132:
/* 360 */       readLocal(var);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.stackmap.Liveness
 * JD-Core Version:    0.6.2
 */