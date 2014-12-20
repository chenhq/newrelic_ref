/*    */ package com.newrelic.agent.android.instrumentation.httpclient;
/*    */ 
/*    */ import com.newrelic.agent.android.instrumentation.TransactionState;
/*    */ import com.newrelic.agent.android.instrumentation.TransactionStateUtil;
/*    */ import java.io.IOException;
/*    */ import org.apache.http.HttpResponse;
/*    */ import org.apache.http.client.ClientProtocolException;
/*    */ import org.apache.http.client.ResponseHandler;
/*    */ 
/*    */ public final class ResponseHandlerImpl<T>
/*    */   implements ResponseHandler<T>
/*    */ {
/*    */   private final ResponseHandler<T> impl;
/*    */   private final TransactionState transactionState;
/*    */ 
/*    */   private ResponseHandlerImpl(ResponseHandler<T> impl, TransactionState transactionState)
/*    */   {
/* 17 */     this.impl = impl;
/* 18 */     this.transactionState = transactionState;
/*    */   }
/*    */ 
/*    */   public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException
/*    */   {
/* 23 */     TransactionStateUtil.inspectAndInstrument(this.transactionState, response);
/* 24 */     return this.impl.handleResponse(response);
/*    */   }
/*    */ 
/*    */   public static <T> ResponseHandler<? extends T> wrap(ResponseHandler<? extends T> impl, TransactionState transactionState)
/*    */   {
/* 33 */     return new ResponseHandlerImpl(impl, transactionState);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.httpclient.ResponseHandlerImpl
 * JD-Core Version:    0.6.2
 */