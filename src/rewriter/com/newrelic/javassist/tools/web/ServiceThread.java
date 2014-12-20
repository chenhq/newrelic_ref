/*     */ package com.newrelic.javassist.tools.web;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ 
/*     */ class ServiceThread extends Thread
/*     */ {
/*     */   Webserver web;
/*     */   Socket sock;
/*     */ 
/*     */   public ServiceThread(Webserver w, Socket s)
/*     */   {
/* 396 */     this.web = w;
/* 397 */     this.sock = s;
/*     */   }
/*     */ 
/*     */   public void run() {
/*     */     try {
/* 402 */       this.web.process(this.sock);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.web.ServiceThread
 * JD-Core Version:    0.6.2
 */