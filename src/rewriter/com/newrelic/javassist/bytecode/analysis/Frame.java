/*     */ package com.newrelic.javassist.bytecode.analysis;
/*     */ 
/*     */ public class Frame
/*     */ {
/*     */   private Type[] locals;
/*     */   private Type[] stack;
/*     */   private int top;
/*     */   private boolean jsrMerged;
/*     */   private boolean retMerged;
/*     */ 
/*     */   public Frame(int locals, int stack)
/*     */   {
/*  37 */     this.locals = new Type[locals];
/*  38 */     this.stack = new Type[stack];
/*     */   }
/*     */ 
/*     */   public Type getLocal(int index)
/*     */   {
/*  48 */     return this.locals[index];
/*     */   }
/*     */ 
/*     */   public void setLocal(int index, Type type)
/*     */   {
/*  58 */     this.locals[index] = type;
/*     */   }
/*     */ 
/*     */   public Type getStack(int index)
/*     */   {
/*  69 */     return this.stack[index];
/*     */   }
/*     */ 
/*     */   public void setStack(int index, Type type)
/*     */   {
/*  79 */     this.stack[index] = type;
/*     */   }
/*     */ 
/*     */   public void clearStack()
/*     */   {
/*  86 */     this.top = 0;
/*     */   }
/*     */ 
/*     */   public int getTopIndex()
/*     */   {
/*  98 */     return this.top - 1;
/*     */   }
/*     */ 
/*     */   public int localsLength()
/*     */   {
/* 108 */     return this.locals.length;
/*     */   }
/*     */ 
/*     */   public Type peek()
/*     */   {
/* 117 */     if (this.top < 1) {
/* 118 */       throw new IndexOutOfBoundsException("Stack is empty");
/*     */     }
/* 120 */     return this.stack[(this.top - 1)];
/*     */   }
/*     */ 
/*     */   public Type pop()
/*     */   {
/* 129 */     if (this.top < 1)
/* 130 */       throw new IndexOutOfBoundsException("Stack is empty");
/* 131 */     return this.stack[(--this.top)];
/*     */   }
/*     */ 
/*     */   public void push(Type type)
/*     */   {
/* 140 */     this.stack[(this.top++)] = type;
/*     */   }
/*     */ 
/*     */   public Frame copy()
/*     */   {
/* 151 */     Frame frame = new Frame(this.locals.length, this.stack.length);
/* 152 */     System.arraycopy(this.locals, 0, frame.locals, 0, this.locals.length);
/* 153 */     System.arraycopy(this.stack, 0, frame.stack, 0, this.stack.length);
/* 154 */     frame.top = this.top;
/* 155 */     return frame;
/*     */   }
/*     */ 
/*     */   public Frame copyStack()
/*     */   {
/* 165 */     Frame frame = new Frame(this.locals.length, this.stack.length);
/* 166 */     System.arraycopy(this.stack, 0, frame.stack, 0, this.stack.length);
/* 167 */     frame.top = this.top;
/* 168 */     return frame;
/*     */   }
/*     */ 
/*     */   public boolean mergeStack(Frame frame)
/*     */   {
/* 179 */     boolean changed = false;
/* 180 */     if (this.top != frame.top) {
/* 181 */       throw new RuntimeException("Operand stacks could not be merged, they are different sizes!");
/*     */     }
/* 183 */     for (int i = 0; i < this.top; i++) {
/* 184 */       if (this.stack[i] != null) {
/* 185 */         Type prev = this.stack[i];
/* 186 */         Type merged = prev.merge(frame.stack[i]);
/* 187 */         if (merged == Type.BOGUS) {
/* 188 */           throw new RuntimeException("Operand stacks could not be merged due to differing primitive types: pos = " + i);
/*     */         }
/* 190 */         this.stack[i] = merged;
/*     */ 
/* 192 */         if ((!merged.equals(prev)) || (merged.popChanged())) {
/* 193 */           changed = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 198 */     return changed;
/*     */   }
/*     */ 
/*     */   public boolean merge(Frame frame)
/*     */   {
/* 209 */     boolean changed = false;
/*     */ 
/* 212 */     for (int i = 0; i < this.locals.length; i++) {
/* 213 */       if (this.locals[i] != null) {
/* 214 */         Type prev = this.locals[i];
/* 215 */         Type merged = prev.merge(frame.locals[i]);
/*     */ 
/* 217 */         this.locals[i] = merged;
/* 218 */         if ((!merged.equals(prev)) || (merged.popChanged()))
/* 219 */           changed = true;
/*     */       }
/* 221 */       else if (frame.locals[i] != null) {
/* 222 */         this.locals[i] = frame.locals[i];
/* 223 */         changed = true;
/*     */       }
/*     */     }
/*     */ 
/* 227 */     changed |= mergeStack(frame);
/* 228 */     return changed;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 232 */     StringBuffer buffer = new StringBuffer();
/*     */ 
/* 234 */     buffer.append("locals = [");
/* 235 */     for (int i = 0; i < this.locals.length; i++) {
/* 236 */       buffer.append(this.locals[i] == null ? "empty" : this.locals[i].toString());
/* 237 */       if (i < this.locals.length - 1)
/* 238 */         buffer.append(", ");
/*     */     }
/* 240 */     buffer.append("] stack = [");
/* 241 */     for (int i = 0; i < this.top; i++) {
/* 242 */       buffer.append(this.stack[i]);
/* 243 */       if (i < this.top - 1)
/* 244 */         buffer.append(", ");
/*     */     }
/* 246 */     buffer.append("]");
/*     */ 
/* 248 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   boolean isJsrMerged()
/*     */   {
/* 257 */     return this.jsrMerged;
/*     */   }
/*     */ 
/*     */   void setJsrMerged(boolean jsrMerged)
/*     */   {
/* 266 */     this.jsrMerged = jsrMerged;
/*     */   }
/*     */ 
/*     */   boolean isRetMerged()
/*     */   {
/* 276 */     return this.retMerged;
/*     */   }
/*     */ 
/*     */   void setRetMerged(boolean retMerged)
/*     */   {
/* 286 */     this.retMerged = retMerged;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.analysis.Frame
 * JD-Core Version:    0.6.2
 */