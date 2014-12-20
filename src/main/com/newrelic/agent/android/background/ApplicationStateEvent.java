/*   */ package com.newrelic.agent.android.background;
/*   */ 
/*   */ import java.util.EventObject;
/*   */ 
/*   */ public class ApplicationStateEvent extends EventObject
/*   */ {
/*   */   private static final long serialVersionUID = 1L;
/*   */ 
/*   */   public ApplicationStateEvent(Object source)
/*   */   {
/* 9 */     super(source);
/*   */   }
/*   */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.background.ApplicationStateEvent
 * JD-Core Version:    0.6.2
 */