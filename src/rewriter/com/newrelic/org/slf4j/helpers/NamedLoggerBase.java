/*    */ package com.newrelic.org.slf4j.helpers;
/*    */ 
/*    */ import com.newrelic.org.slf4j.Logger;
/*    */ import com.newrelic.org.slf4j.LoggerFactory;
/*    */ import java.io.ObjectStreamException;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ abstract class NamedLoggerBase
/*    */   implements Logger, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 7535258609338176893L;
/*    */   protected String name;
/*    */ 
/*    */   public String getName()
/*    */   {
/* 23 */     return this.name;
/*    */   }
/*    */ 
/*    */   protected Object readResolve()
/*    */     throws ObjectStreamException
/*    */   {
/* 43 */     return LoggerFactory.getLogger(getName());
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.helpers.NamedLoggerBase
 * JD-Core Version:    0.6.2
 */