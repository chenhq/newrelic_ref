/*    */ package com.newrelic.agent.android.instrumentation;
/*    */ 
/*    */ import android.os.AsyncTask;
/*    */ import com.newrelic.agent.android.api.v2.TraceFieldInterface;
/*    */ import com.newrelic.agent.android.tracing.TraceMachine;
/*    */ import com.newrelic.agent.android.tracing.TracingInactiveException;
/*    */ import java.util.concurrent.Executor;
/*    */ 
/*    */ public class AsyncTaskInstrumentation
/*    */ {
/*    */   @ReplaceCallSite
/*    */   public static final <Params, Progress, Result> AsyncTask execute(AsyncTask<Params, Progress, Result> task, Params[] params)
/*    */   {
/*    */     try
/*    */     {
/* 21 */       ((TraceFieldInterface)task)._nr_setTrace(TraceMachine.getCurrentTrace());
/*    */     } catch (TracingInactiveException e) {
/*    */     } catch (NoSuchFieldError e) {
/*    */     }
/* 25 */     return task.execute(params);
/*    */   }
/*    */ 
/*    */   @ReplaceCallSite
/*    */   public static final <Params, Progress, Result> AsyncTask executeOnExecutor(AsyncTask<Params, Progress, Result> task, Executor exec, Params[] params)
/*    */   {
/*    */     try {
/* 32 */       ((TraceFieldInterface)task)._nr_setTrace(TraceMachine.getCurrentTrace());
/*    */     } catch (TracingInactiveException e) {
/*    */     } catch (NoSuchFieldError e) {
/*    */     }
/* 36 */     return task.executeOnExecutor(exec, params);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.AsyncTaskInstrumentation
 * JD-Core Version:    0.6.2
 */