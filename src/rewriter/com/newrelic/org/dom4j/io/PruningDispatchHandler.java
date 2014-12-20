/*    */ package com.newrelic.org.dom4j.io;
/*    */ 
/*    */ import com.newrelic.org.dom4j.Element;
/*    */ import com.newrelic.org.dom4j.ElementPath;
/*    */ 
/*    */ class PruningDispatchHandler extends DispatchHandler
/*    */ {
/*    */   public void onEnd(ElementPath elementPath)
/*    */   {
/* 21 */     super.onEnd(elementPath);
/*    */ 
/* 23 */     if (getActiveHandlerCount() == 0)
/* 24 */       elementPath.getCurrent().detach();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.io.PruningDispatchHandler
 * JD-Core Version:    0.6.2
 */