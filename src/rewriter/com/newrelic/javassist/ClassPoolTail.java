/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URL;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ final class ClassPoolTail
/*     */ {
/*     */   protected ClassPathList pathList;
/*     */   private Hashtable packages;
/*     */ 
/*     */   public ClassPoolTail()
/*     */   {
/* 183 */     this.pathList = null;
/* 184 */     this.packages = new Hashtable();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 188 */     StringBuffer buf = new StringBuffer();
/* 189 */     buf.append("[class path: ");
/* 190 */     ClassPathList list = this.pathList;
/* 191 */     while (list != null) {
/* 192 */       buf.append(list.path.toString());
/* 193 */       buf.append(File.pathSeparatorChar);
/* 194 */       list = list.next;
/*     */     }
/*     */ 
/* 197 */     buf.append(']');
/* 198 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public synchronized ClassPath insertClassPath(ClassPath cp) {
/* 202 */     this.pathList = new ClassPathList(cp, this.pathList);
/* 203 */     return cp;
/*     */   }
/*     */ 
/*     */   public synchronized ClassPath appendClassPath(ClassPath cp) {
/* 207 */     ClassPathList tail = new ClassPathList(cp, null);
/* 208 */     ClassPathList list = this.pathList;
/* 209 */     if (list == null) {
/* 210 */       this.pathList = tail;
/*     */     } else {
/* 212 */       while (list.next != null) {
/* 213 */         list = list.next;
/*     */       }
/* 215 */       list.next = tail;
/*     */     }
/*     */ 
/* 218 */     return cp;
/*     */   }
/*     */ 
/*     */   public synchronized void removeClassPath(ClassPath cp) {
/* 222 */     ClassPathList list = this.pathList;
/* 223 */     if (list != null) {
/* 224 */       if (list.path == cp)
/* 225 */         this.pathList = list.next;
/*     */       else {
/* 227 */         while (list.next != null)
/* 228 */           if (list.next.path == cp)
/* 229 */             list.next = list.next.next;
/*     */           else
/* 231 */             list = list.next;
/*     */       }
/*     */     }
/* 234 */     cp.close();
/*     */   }
/*     */ 
/*     */   public ClassPath appendSystemPath() {
/* 238 */     return appendClassPath(new ClassClassPath());
/*     */   }
/*     */ 
/*     */   public ClassPath insertClassPath(String pathname)
/*     */     throws NotFoundException
/*     */   {
/* 244 */     return insertClassPath(makePathObject(pathname));
/*     */   }
/*     */ 
/*     */   public ClassPath appendClassPath(String pathname)
/*     */     throws NotFoundException
/*     */   {
/* 250 */     return appendClassPath(makePathObject(pathname));
/*     */   }
/*     */ 
/*     */   private static ClassPath makePathObject(String pathname)
/*     */     throws NotFoundException
/*     */   {
/* 256 */     String lower = pathname.toLowerCase();
/* 257 */     if ((lower.endsWith(".jar")) || (lower.endsWith(".zip"))) {
/* 258 */       return new JarClassPath(pathname);
/*     */     }
/* 260 */     int len = pathname.length();
/* 261 */     if ((len > 2) && (pathname.charAt(len - 1) == '*') && ((pathname.charAt(len - 2) == '/') || (pathname.charAt(len - 2) == File.separatorChar)))
/*     */     {
/* 264 */       String dir = pathname.substring(0, len - 2);
/* 265 */       return new JarDirClassPath(dir);
/*     */     }
/*     */ 
/* 268 */     return new DirClassPath(pathname);
/*     */   }
/*     */ 
/*     */   public void recordInvalidClassName(String name)
/*     */   {
/* 276 */     this.packages.put(name, name);
/*     */   }
/*     */ 
/*     */   void writeClassfile(String classname, OutputStream out)
/*     */     throws NotFoundException, IOException, CannotCompileException
/*     */   {
/* 285 */     InputStream fin = openClassfile(classname);
/* 286 */     if (fin == null)
/* 287 */       throw new NotFoundException(classname);
/*     */     try
/*     */     {
/* 290 */       copyStream(fin, out);
/*     */     }
/*     */     finally {
/* 293 */       fin.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   InputStream openClassfile(String classname)
/*     */     throws NotFoundException
/*     */   {
/* 327 */     if (this.packages.get(classname) != null) {
/* 328 */       return null;
/*     */     }
/* 330 */     ClassPathList list = this.pathList;
/* 331 */     InputStream ins = null;
/* 332 */     NotFoundException error = null;
/* 333 */     while (list != null) {
/*     */       try {
/* 335 */         ins = list.path.openClassfile(classname);
/*     */       }
/*     */       catch (NotFoundException e) {
/* 338 */         if (error == null) {
/* 339 */           error = e;
/*     */         }
/*     */       }
/* 342 */       if (ins == null)
/* 343 */         list = list.next;
/*     */       else {
/* 345 */         return ins;
/*     */       }
/*     */     }
/* 348 */     if (error != null) {
/* 349 */       throw error;
/*     */     }
/* 351 */     return null;
/*     */   }
/*     */ 
/*     */   public URL find(String classname)
/*     */   {
/* 363 */     if (this.packages.get(classname) != null) {
/* 364 */       return null;
/*     */     }
/* 366 */     ClassPathList list = this.pathList;
/* 367 */     URL url = null;
/* 368 */     while (list != null) {
/* 369 */       url = list.path.find(classname);
/* 370 */       if (url == null)
/* 371 */         list = list.next;
/*     */       else {
/* 373 */         return url;
/*     */       }
/*     */     }
/* 376 */     return null;
/*     */   }
/*     */ 
/*     */   public static byte[] readStream(InputStream fin)
/*     */     throws IOException
/*     */   {
/* 385 */     byte[][] bufs = new byte[8][];
/* 386 */     int bufsize = 4096;
/*     */ 
/* 388 */     for (int i = 0; i < 8; i++) {
/* 389 */       bufs[i] = new byte[bufsize];
/* 390 */       int size = 0;
/* 391 */       int len = 0;
/*     */       do {
/* 393 */         len = fin.read(bufs[i], size, bufsize - size);
/* 394 */         if (len >= 0) {
/* 395 */           size += len;
/*     */         } else {
/* 397 */           byte[] result = new byte[bufsize - 4096 + size];
/* 398 */           int s = 0;
/* 399 */           for (int j = 0; j < i; j++) {
/* 400 */             System.arraycopy(bufs[j], 0, result, s, s + 4096);
/* 401 */             s = s + s + 4096;
/*     */           }
/*     */ 
/* 404 */           System.arraycopy(bufs[i], 0, result, s, size);
/* 405 */           return result;
/*     */         }
/*     */       }
/* 407 */       while (size < bufsize);
/* 408 */       bufsize *= 2;
/*     */     }
/*     */ 
/* 411 */     throw new IOException("too much data");
/*     */   }
/*     */ 
/*     */   public static void copyStream(InputStream fin, OutputStream fout)
/*     */     throws IOException
/*     */   {
/* 422 */     int bufsize = 4096;
/* 423 */     for (int i = 0; i < 8; i++) {
/* 424 */       byte[] buf = new byte[bufsize];
/* 425 */       int size = 0;
/* 426 */       int len = 0;
/*     */       do {
/* 428 */         len = fin.read(buf, size, bufsize - size);
/* 429 */         if (len >= 0) {
/* 430 */           size += len;
/*     */         } else {
/* 432 */           fout.write(buf, 0, size);
/* 433 */           return;
/*     */         }
/*     */       }
/* 435 */       while (size < bufsize);
/* 436 */       fout.write(buf);
/* 437 */       bufsize *= 2;
/*     */     }
/*     */ 
/* 440 */     throw new IOException("too much data");
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.ClassPoolTail
 * JD-Core Version:    0.6.2
 */