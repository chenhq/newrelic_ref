/*    */ package com.newrelic.agent.android.instrumentation;
/*    */ 
/*    */ import com.newrelic.agent.android.tracing.TraceMachine;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import org.json.JSONException;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ public class JSONObjectInstrumentation
/*    */ {
/* 12 */   private static final ArrayList<String> categoryParams = new ArrayList(Arrays.asList(new String[] { "category", MetricCategory.class.getName(), "JSON" }));
/*    */ 
/*    */   @TraceConstructor
/*    */   public static JSONObject init(String json)
/*    */     throws JSONException
/*    */   {
/*    */     JSONObject jsonObject;
/*    */     try
/*    */     {
/* 21 */       TraceMachine.enterMethod(null, "JSONObject#<init>", categoryParams);
/* 22 */       jsonObject = new JSONObject(json);
/* 23 */       TraceMachine.exitMethod();
/*    */     } catch (JSONException e) {
/* 25 */       TraceMachine.exitMethod();
/* 26 */       throw e;
/*    */     }
/*    */ 
/* 29 */     return jsonObject;
/*    */   }
/*    */ 
/*    */   @ReplaceCallSite(scope="org.json.JSONObject")
/*    */   public static String toString(JSONObject jsonObject) {
/* 34 */     TraceMachine.enterMethod(null, "JSONObject#toString", categoryParams);
/* 35 */     String jsonString = jsonObject.toString();
/* 36 */     TraceMachine.exitMethod();
/*    */ 
/* 38 */     return jsonString;
/*    */   }
/*    */ 
/*    */   @ReplaceCallSite(scope="org.json.JSONObject")
/*    */   public static String toString(JSONObject jsonObject, int indentFactor) throws JSONException
/*    */   {
/* 45 */     TraceMachine.enterMethod(null, "JSONObject#toString", categoryParams);
/*    */     String jsonString;
/*    */     try {
/* 48 */       jsonString = jsonObject.toString(indentFactor);
/* 49 */       TraceMachine.exitMethod();
/*    */     } catch (JSONException e) {
/* 51 */       TraceMachine.exitMethod();
/* 52 */       throw e;
/*    */     }
/*    */ 
/* 55 */     return jsonString;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.JSONObjectInstrumentation
 * JD-Core Version:    0.6.2
 */