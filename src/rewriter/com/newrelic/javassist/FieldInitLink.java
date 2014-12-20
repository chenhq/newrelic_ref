/*      */ package com.newrelic.javassist;
/*      */ 
/*      */ class FieldInitLink
/*      */ {
/*      */   FieldInitLink next;
/*      */   CtField field;
/*      */   CtField.Initializer init;
/*      */ 
/*      */   FieldInitLink(CtField f, CtField.Initializer i)
/*      */   {
/* 1681 */     this.next = null;
/* 1682 */     this.field = f;
/* 1683 */     this.init = i;
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.FieldInitLink
 * JD-Core Version:    0.6.2
 */