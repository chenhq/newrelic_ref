/*     */ package com.newrelic.javassist.util;
/*     */ 
/*     */ import com.sun.jdi.Bootstrap;
/*     */ import com.sun.jdi.ReferenceType;
/*     */ import com.sun.jdi.VirtualMachine;
/*     */ import com.sun.jdi.VirtualMachineManager;
/*     */ import com.sun.jdi.connect.AttachingConnector;
/*     */ import com.sun.jdi.connect.Connector;
/*     */ import com.sun.jdi.connect.Connector.Argument;
/*     */ import com.sun.jdi.connect.IllegalConnectorArgumentsException;
/*     */ import com.sun.jdi.event.Event;
/*     */ import com.sun.jdi.event.EventIterator;
/*     */ import com.sun.jdi.event.EventQueue;
/*     */ import com.sun.jdi.event.EventSet;
/*     */ import com.sun.jdi.event.MethodEntryEvent;
/*     */ import com.sun.jdi.request.EventRequestManager;
/*     */ import com.sun.jdi.request.MethodEntryRequest;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class HotSwapper
/*     */ {
/*     */   private VirtualMachine jvm;
/*     */   private MethodEntryRequest request;
/*     */   private Map newClassFiles;
/*     */   private Trigger trigger;
/*     */   private static final String HOST_NAME = "localhost";
/*  84 */   private static final String TRIGGER_NAME = Trigger.class.getName();
/*     */ 
/*     */   public HotSwapper(int port)
/*     */     throws IOException, IllegalConnectorArgumentsException
/*     */   {
/*  94 */     this(Integer.toString(port));
/*     */   }
/*     */ 
/*     */   public HotSwapper(String port)
/*     */     throws IOException, IllegalConnectorArgumentsException
/*     */   {
/* 105 */     this.jvm = null;
/* 106 */     this.request = null;
/* 107 */     this.newClassFiles = null;
/* 108 */     this.trigger = new Trigger();
/* 109 */     AttachingConnector connector = (AttachingConnector)findConnector("com.sun.jdi.SocketAttach");
/*     */ 
/* 112 */     Map arguments = connector.defaultArguments();
/* 113 */     ((Connector.Argument)arguments.get("hostname")).setValue("localhost");
/* 114 */     ((Connector.Argument)arguments.get("port")).setValue(port);
/* 115 */     this.jvm = connector.attach(arguments);
/* 116 */     EventRequestManager manager = this.jvm.eventRequestManager();
/* 117 */     this.request = methodEntryRequests(manager, TRIGGER_NAME);
/*     */   }
/*     */ 
/*     */   private Connector findConnector(String connector) throws IOException {
/* 121 */     List connectors = Bootstrap.virtualMachineManager().allConnectors();
/* 122 */     Iterator iter = connectors.iterator();
/* 123 */     while (iter.hasNext()) {
/* 124 */       Connector con = (Connector)iter.next();
/* 125 */       if (con.name().equals(connector)) {
/* 126 */         return con;
/*     */       }
/*     */     }
/*     */ 
/* 130 */     throw new IOException("Not found: " + connector);
/*     */   }
/*     */ 
/*     */   private static MethodEntryRequest methodEntryRequests(EventRequestManager manager, String classpattern)
/*     */   {
/* 136 */     MethodEntryRequest mereq = manager.createMethodEntryRequest();
/* 137 */     mereq.addClassFilter(classpattern);
/* 138 */     mereq.setSuspendPolicy(1);
/* 139 */     return mereq;
/*     */   }
/*     */ 
/*     */   private void deleteEventRequest(EventRequestManager manager, MethodEntryRequest request)
/*     */   {
/* 146 */     manager.deleteEventRequest(request);
/*     */   }
/*     */ 
/*     */   public void reload(String className, byte[] classFile)
/*     */   {
/* 156 */     ReferenceType classtype = toRefType(className);
/* 157 */     Map map = new HashMap();
/* 158 */     map.put(classtype, classFile);
/* 159 */     reload2(map, className);
/*     */   }
/*     */ 
/*     */   public void reload(Map classFiles)
/*     */   {
/* 171 */     Set set = classFiles.entrySet();
/* 172 */     Iterator it = set.iterator();
/* 173 */     Map map = new HashMap();
/* 174 */     String className = null;
/* 175 */     while (it.hasNext()) {
/* 176 */       Map.Entry e = (Map.Entry)it.next();
/* 177 */       className = (String)e.getKey();
/* 178 */       map.put(toRefType(className), e.getValue());
/*     */     }
/*     */ 
/* 181 */     if (className != null)
/* 182 */       reload2(map, className + " etc.");
/*     */   }
/*     */ 
/*     */   private ReferenceType toRefType(String className) {
/* 186 */     List list = this.jvm.classesByName(className);
/* 187 */     if ((list == null) || (list.isEmpty())) {
/* 188 */       throw new RuntimeException("no such a class: " + className);
/*     */     }
/* 190 */     return (ReferenceType)list.get(0);
/*     */   }
/*     */ 
/*     */   private void reload2(Map map, String msg) {
/* 194 */     synchronized (this.trigger) {
/* 195 */       startDaemon();
/* 196 */       this.newClassFiles = map;
/* 197 */       this.request.enable();
/* 198 */       this.trigger.doSwap();
/* 199 */       this.request.disable();
/* 200 */       Map ncf = this.newClassFiles;
/* 201 */       if (ncf != null) {
/* 202 */         this.newClassFiles = null;
/* 203 */         throw new RuntimeException("failed to reload: " + msg);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void startDaemon() {
/* 209 */     new Thread() {
/*     */       private void errorMsg(Throwable e) {
/* 211 */         System.err.print("Exception in thread \"HotSwap\" ");
/* 212 */         e.printStackTrace(System.err);
/*     */       }
/*     */ 
/*     */       public void run() {
/* 216 */         EventSet events = null;
/*     */         try {
/* 218 */           events = HotSwapper.this.waitEvent();
/* 219 */           EventIterator iter = events.eventIterator();
/* 220 */           while (iter.hasNext()) {
/* 221 */             Event event = iter.nextEvent();
/* 222 */             if ((event instanceof MethodEntryEvent)) {
/* 223 */               HotSwapper.this.hotswap();
/* 224 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 229 */           errorMsg(e);
/*     */         }
/*     */         try {
/* 232 */           if (events != null)
/* 233 */             events.resume();
/*     */         }
/*     */         catch (Throwable e) {
/* 236 */           errorMsg(e);
/*     */         }
/*     */       }
/*     */     }
/* 209 */     .start();
/*     */   }
/*     */ 
/*     */   EventSet waitEvent()
/*     */     throws InterruptedException
/*     */   {
/* 243 */     EventQueue queue = this.jvm.eventQueue();
/* 244 */     return queue.remove();
/*     */   }
/*     */ 
/*     */   void hotswap() {
/* 248 */     Map map = this.newClassFiles;
/* 249 */     this.jvm.redefineClasses(map);
/* 250 */     this.newClassFiles = null;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.util.HotSwapper
 * JD-Core Version:    0.6.2
 */