/*    */ package com.newrelic.agent.android.instrumentation;
/*    */ 
/*    */ import com.newrelic.agent.android.tracing.TraceMachine;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import org.json.JSONArray;
/*    */ import org.json.JSONException;
/*    */ 
/*    */ public class JSONArrayInstrumentation
/*    */ {
/* 15 */   private static final ArrayList<String> categoryParams = new ArrayList(Arrays.asList(new String[] { "category", MetricCategory.class.getName(), "JSON" }));
/*    */ 
/*    */   @TraceConstructor
/*    */   public static JSONArray init(String json)
/*    */     throws JSONException
/*    */   {
/*    */     JSONArray jsonArray;
/*    */     try
/*    */     {
/* 24 */       TraceMachine.enterMethod("JSONArray#<init>", categoryParams);
/* 25 */       jsonArray = new JSONArray(json);
/* 26 */       TraceMachine.exitMethod();
/*    */     } catch (JSONException e) {
/* 28 */       TraceMachine.exitMethod();
/* 29 */       throw e;
/*    */     }
/*    */ 
/* 32 */     return jsonArray;
/*    */   }
/*    */ 
/*    */   @ReplaceCallSite(scope="org.json.JSONArray")
/*    */   public static String toString(JSONArray jsonArray) {
/* 37 */     TraceMachine.enterMethod("JSONArray#toString", categoryParams);
/* 38 */     String jsonString = jsonArray.toString();
/* 39 */     TraceMachine.exitMethod();
/*    */ 
/* 41 */     return jsonString;
/*    */   }
/*    */ 
/*    */   @ReplaceCallSite(scope="org.json.JSONArray")
/*    */   public static String toString(JSONArray jsonArray, int indentFactor) throws JSONException
/*    */   {
/*    */     String jsonString;
/*    */     try {
/* 49 */       TraceMachine.enterMethod("JSONArray#toString", categoryParams);
/* 50 */       jsonString = jsonArray.toString(indentFactor);
/* 51 */       TraceMachine.exitMethod();
/*    */     } catch (JSONException e) {
/* 53 */       TraceMachine.exitMethod();
/* 54 */       throw e;
/*    */     }
/*    */ 
/* 57 */     return jsonString;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.JSONArrayInstrumentation
 * JD-Core Version:    0.6.2
 */