/*    */ package com.newrelic.javassist.convert;
/*    */ 
/*    */ import com.newrelic.javassist.CtMethod;
/*    */ import com.newrelic.javassist.NotFoundException;
/*    */ import com.newrelic.javassist.bytecode.BadBytecode;
/*    */ import com.newrelic.javassist.bytecode.CodeIterator;
/*    */ 
/*    */ public class TransformAfter extends TransformBefore
/*    */ {
/*    */   public TransformAfter(Transformer next, CtMethod origMethod, CtMethod afterMethod)
/*    */     throws NotFoundException
/*    */   {
/* 27 */     super(next, origMethod, afterMethod);
/*    */   }
/*    */ 
/*    */   protected int match2(int pos, CodeIterator iterator) throws BadBytecode {
/* 31 */     iterator.move(pos);
/* 32 */     iterator.insert(this.saveCode);
/* 33 */     iterator.insert(this.loadCode);
/* 34 */     int p = iterator.insertGap(3);
/* 35 */     iterator.setMark(p);
/* 36 */     iterator.insert(this.loadCode);
/* 37 */     pos = iterator.next();
/* 38 */     p = iterator.getMark();
/* 39 */     iterator.writeByte(iterator.byteAt(pos), p);
/* 40 */     iterator.write16bit(iterator.u16bitAt(pos + 1), p + 1);
/* 41 */     iterator.writeByte(184, pos);
/* 42 */     iterator.write16bit(this.newIndex, pos + 1);
/* 43 */     iterator.move(p);
/* 44 */     return iterator.next();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.convert.TransformAfter
 * JD-Core Version:    0.6.2
 */