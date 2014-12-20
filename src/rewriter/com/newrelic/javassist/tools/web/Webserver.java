/*     */ package com.newrelic.javassist.tools.web;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.Translator;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class Webserver
/*     */ {
/*     */   private ServerSocket socket;
/*     */   private ClassPool classPool;
/*     */   protected Translator translator;
/*  41 */   private static final byte[] endofline = { 13, 10 };
/*     */   private static final int typeHtml = 1;
/*     */   private static final int typeClass = 2;
/*     */   private static final int typeGif = 3;
/*     */   private static final int typeJpeg = 4;
/*     */   private static final int typeText = 5;
/*  55 */   public String debugDir = null;
/*     */ 
/*  71 */   public String htmlfileBase = null;
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws IOException
/*     */   {
/*  78 */     if (args.length == 1) {
/*  79 */       Webserver web = new Webserver(args[0]);
/*  80 */       web.run();
/*     */     }
/*     */     else {
/*  83 */       System.err.println("Usage: java javassist.tools.web.Webserver <port number>");
/*     */     }
/*     */   }
/*     */ 
/*     */   public Webserver(String port)
/*     */     throws IOException
/*     */   {
/*  93 */     this(Integer.parseInt(port));
/*     */   }
/*     */ 
/*     */   public Webserver(int port)
/*     */     throws IOException
/*     */   {
/* 102 */     this.socket = new ServerSocket(port);
/* 103 */     this.classPool = null;
/* 104 */     this.translator = null;
/*     */   }
/*     */ 
/*     */   public void setClassPool(ClassPool loader)
/*     */   {
/* 112 */     this.classPool = loader;
/*     */   }
/*     */ 
/*     */   public void addTranslator(ClassPool cp, Translator t)
/*     */     throws NotFoundException, CannotCompileException
/*     */   {
/* 126 */     this.classPool = cp;
/* 127 */     this.translator = t;
/* 128 */     t.start(this.classPool);
/*     */   }
/*     */ 
/*     */   public void end()
/*     */     throws IOException
/*     */   {
/* 135 */     this.socket.close();
/*     */   }
/*     */ 
/*     */   public void logging(String msg)
/*     */   {
/* 142 */     System.out.println(msg);
/*     */   }
/*     */ 
/*     */   public void logging(String msg1, String msg2)
/*     */   {
/* 149 */     System.out.print(msg1);
/* 150 */     System.out.print(" ");
/* 151 */     System.out.println(msg2);
/*     */   }
/*     */ 
/*     */   public void logging(String msg1, String msg2, String msg3)
/*     */   {
/* 158 */     System.out.print(msg1);
/* 159 */     System.out.print(" ");
/* 160 */     System.out.print(msg2);
/* 161 */     System.out.print(" ");
/* 162 */     System.out.println(msg3);
/*     */   }
/*     */ 
/*     */   public void logging2(String msg)
/*     */   {
/* 169 */     System.out.print("    ");
/* 170 */     System.out.println(msg);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 177 */     System.err.println("ready to service...");
/*     */     while (true)
/*     */       try {
/* 180 */         ServiceThread th = new ServiceThread(this, this.socket.accept());
/* 181 */         th.start();
/*     */       }
/*     */       catch (IOException e) {
/* 184 */         logging(e.toString());
/*     */       }
/*     */   }
/*     */ 
/*     */   final void process(Socket clnt) throws IOException {
/* 189 */     InputStream in = new BufferedInputStream(clnt.getInputStream());
/* 190 */     String cmd = readLine(in);
/* 191 */     logging(clnt.getInetAddress().getHostName(), new Date().toString(), cmd);
/*     */ 
/* 193 */     while (skipLine(in) > 0);
/* 196 */     OutputStream out = new BufferedOutputStream(clnt.getOutputStream());
/*     */     try {
/* 198 */       doReply(in, out, cmd);
/*     */     }
/*     */     catch (BadHttpRequest e) {
/* 201 */       replyError(out, e);
/*     */     }
/*     */ 
/* 204 */     out.flush();
/* 205 */     in.close();
/* 206 */     out.close();
/* 207 */     clnt.close();
/*     */   }
/*     */ 
/*     */   private String readLine(InputStream in) throws IOException {
/* 211 */     StringBuffer buf = new StringBuffer();
/*     */     int c;
/* 213 */     while (((c = in.read()) >= 0) && (c != 13)) {
/* 214 */       buf.append((char)c);
/*     */     }
/* 216 */     in.read();
/* 217 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private int skipLine(InputStream in) throws IOException
/*     */   {
/* 222 */     int len = 0;
/*     */     int c;
/* 223 */     while (((c = in.read()) >= 0) && (c != 13)) {
/* 224 */       len++;
/*     */     }
/* 226 */     in.read();
/* 227 */     return len;
/*     */   }
/*     */ 
/*     */   public void doReply(InputStream in, OutputStream out, String cmd)
/*     */     throws IOException, BadHttpRequest
/*     */   {
/*     */     String filename;
/* 243 */     if (cmd.startsWith("GET /"))
/*     */     {
/*     */       String urlName;
/* 244 */       filename = urlName = cmd.substring(5, cmd.indexOf(' ', 5));
/*     */     } else {
/* 246 */       throw new BadHttpRequest();
/*     */     }
/*     */     String filename;
/*     */     String urlName;
/*     */     int fileType;
/*     */     int fileType;
/* 248 */     if (filename.endsWith(".class")) {
/* 249 */       fileType = 2;
/*     */     }
/*     */     else
/*     */     {
/*     */       int fileType;
/* 250 */       if ((filename.endsWith(".html")) || (filename.endsWith(".htm"))) {
/* 251 */         fileType = 1;
/*     */       }
/*     */       else
/*     */       {
/*     */         int fileType;
/* 252 */         if (filename.endsWith(".gif")) {
/* 253 */           fileType = 3;
/*     */         }
/*     */         else
/*     */         {
/*     */           int fileType;
/* 254 */           if (filename.endsWith(".jpg"))
/* 255 */             fileType = 4;
/*     */           else
/* 257 */             fileType = 5; 
/*     */         }
/*     */       }
/*     */     }
/* 259 */     int len = filename.length();
/* 260 */     if ((fileType == 2) && (letUsersSendClassfile(out, filename, len)))
/*     */     {
/* 262 */       return;
/*     */     }
/* 264 */     checkFilename(filename, len);
/* 265 */     if (this.htmlfileBase != null) {
/* 266 */       filename = this.htmlfileBase + filename;
/*     */     }
/* 268 */     if (File.separatorChar != '/') {
/* 269 */       filename = filename.replace('/', File.separatorChar);
/*     */     }
/* 271 */     File file = new File(filename);
/* 272 */     if (file.canRead()) {
/* 273 */       sendHeader(out, file.length(), fileType);
/* 274 */       FileInputStream fin = new FileInputStream(file);
/* 275 */       byte[] filebuffer = new byte[4096];
/*     */       while (true) {
/* 277 */         len = fin.read(filebuffer);
/* 278 */         if (len <= 0) {
/*     */           break;
/*     */         }
/* 281 */         out.write(filebuffer, 0, len);
/*     */       }
/*     */ 
/* 284 */       fin.close();
/* 285 */       return;
/*     */     }
/*     */ 
/* 291 */     if (fileType == 2) {
/* 292 */       InputStream fin = getClass().getResourceAsStream("/" + urlName);
/*     */ 
/* 294 */       if (fin != null) {
/* 295 */         ByteArrayOutputStream barray = new ByteArrayOutputStream();
/* 296 */         byte[] filebuffer = new byte[4096];
/*     */         while (true) {
/* 298 */           len = fin.read(filebuffer);
/* 299 */           if (len <= 0) {
/*     */             break;
/*     */           }
/* 302 */           barray.write(filebuffer, 0, len);
/*     */         }
/*     */ 
/* 305 */         byte[] classfile = barray.toByteArray();
/* 306 */         sendHeader(out, classfile.length, 2);
/* 307 */         out.write(classfile);
/* 308 */         fin.close();
/* 309 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 313 */     throw new BadHttpRequest();
/*     */   }
/*     */ 
/*     */   private void checkFilename(String filename, int len)
/*     */     throws BadHttpRequest
/*     */   {
/* 319 */     for (int i = 0; i < len; i++) {
/* 320 */       char c = filename.charAt(i);
/* 321 */       if ((!Character.isJavaIdentifierPart(c)) && (c != '.') && (c != '/')) {
/* 322 */         throw new BadHttpRequest();
/*     */       }
/*     */     }
/* 325 */     if (filename.indexOf("..") >= 0)
/* 326 */       throw new BadHttpRequest();
/*     */   }
/*     */ 
/*     */   private boolean letUsersSendClassfile(OutputStream out, String filename, int length)
/*     */     throws IOException, BadHttpRequest
/*     */   {
/* 333 */     if (this.classPool == null) {
/* 334 */       return false;
/* 337 */     }
/*     */ String classname = filename.substring(0, length - 6).replace('/', '.');
/*     */     byte[] classfile;
/*     */     try {
/* 340 */       if (this.translator != null) {
/* 341 */         this.translator.onLoad(this.classPool, classname);
/*     */       }
/* 343 */       CtClass c = this.classPool.get(classname);
/* 344 */       classfile = c.toBytecode();
/* 345 */       if (this.debugDir != null)
/* 346 */         c.writeFile(this.debugDir);
/*     */     }
/*     */     catch (Exception e) {
/* 349 */       throw new BadHttpRequest(e);
/*     */     }
/*     */ 
/* 352 */     sendHeader(out, classfile.length, 2);
/* 353 */     out.write(classfile);
/* 354 */     return true;
/*     */   }
/*     */ 
/*     */   private void sendHeader(OutputStream out, long dataLength, int filetype)
/*     */     throws IOException
/*     */   {
/* 360 */     out.write("HTTP/1.0 200 OK".getBytes());
/* 361 */     out.write(endofline);
/* 362 */     out.write("Content-Length: ".getBytes());
/* 363 */     out.write(Long.toString(dataLength).getBytes());
/* 364 */     out.write(endofline);
/* 365 */     if (filetype == 2)
/* 366 */       out.write("Content-Type: application/octet-stream".getBytes());
/* 367 */     else if (filetype == 1)
/* 368 */       out.write("Content-Type: text/html".getBytes());
/* 369 */     else if (filetype == 3)
/* 370 */       out.write("Content-Type: image/gif".getBytes());
/* 371 */     else if (filetype == 4)
/* 372 */       out.write("Content-Type: image/jpg".getBytes());
/* 373 */     else if (filetype == 5) {
/* 374 */       out.write("Content-Type: text/plain".getBytes());
/*     */     }
/* 376 */     out.write(endofline);
/* 377 */     out.write(endofline);
/*     */   }
/*     */ 
/*     */   private void replyError(OutputStream out, BadHttpRequest e)
/*     */     throws IOException
/*     */   {
/* 383 */     logging2("bad request: " + e.toString());
/* 384 */     out.write("HTTP/1.0 400 Bad Request".getBytes());
/* 385 */     out.write(endofline);
/* 386 */     out.write(endofline);
/* 387 */     out.write("<H1>Bad Request</H1>".getBytes());
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.web.Webserver
 * JD-Core Version:    0.6.2
 */