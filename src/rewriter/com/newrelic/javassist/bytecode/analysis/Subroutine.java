/*    */ package com.newrelic.javassist.bytecode.analysis;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.HashSet;
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class Subroutine
/*    */ {
/* 30 */   private List callers = new ArrayList();
/* 31 */   private Set access = new HashSet();
/*    */   private int start;
/*    */ 
/*    */   public Subroutine(int start, int caller)
/*    */   {
/* 35 */     this.start = start;
/* 36 */     this.callers.add(new Integer(caller));
/*    */   }
/*    */ 
/*    */   public void addCaller(int caller) {
/* 40 */     this.callers.add(new Integer(caller));
/*    */   }
/*    */ 
/*    */   public int start() {
/* 44 */     return this.start;
/*    */   }
/*    */ 
/*    */   public void access(int index) {
/* 48 */     this.access.add(new Integer(index));
/*    */   }
/*    */ 
/*    */   public boolean isAccessed(int index) {
/* 52 */     return this.access.contains(new Integer(index));
/*    */   }
/*    */ 
/*    */   public Collection accessed() {
/* 56 */     return this.access;
/*    */   }
/*    */ 
/*    */   public Collection callers() {
/* 60 */     return this.callers;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 64 */     return "start = " + this.start + " callers = " + this.callers.toString();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.analysis.Subroutine
 * JD-Core Version:    0.6.2
 */