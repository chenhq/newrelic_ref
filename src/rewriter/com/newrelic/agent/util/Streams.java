/*    */ package com.newrelic.agent.util;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.File;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class Streams
/*    */ {
/*    */   public static final int DEFAULT_BUFFER_SIZE = 8192;
/*    */ 
/*    */   public static int copy(InputStream input, OutputStream output)
/*    */     throws IOException
/*    */   {
/* 17 */     return copy(input, output, 8192, false);
/*    */   }
/*    */ 
/*    */   public static int copy(InputStream input, OutputStream output, boolean closeStreams) throws IOException {
/* 21 */     return copy(input, output, 8192, closeStreams);
/*    */   }
/*    */ 
/*    */   public static int copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
/* 25 */     return copy(input, output, bufferSize, false);
/*    */   }
/*    */ 
/*    */   public static int copy(InputStream input, OutputStream output, int bufferSize, boolean closeStreams)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 37 */       byte[] buffer = new byte[bufferSize];
/* 38 */       int count = 0;
/* 39 */       int n = 0;
/* 40 */       while (-1 != (n = input.read(buffer))) {
/* 41 */         output.write(buffer, 0, n);
/* 42 */         count += n;
/*    */       }
/* 44 */       return count;
/*    */     } finally {
/* 46 */       if (closeStreams) {
/* 47 */         input.close();
/* 48 */         output.close();
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public static byte[] slurpBytes(InputStream in) throws IOException {
/* 54 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/*    */     try {
/* 56 */       copy(in, out);
/* 57 */       out.flush();
/* 58 */       return out.toByteArray();
/*    */     }
/*    */     finally {
/* 61 */       out.close();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static String slurp(InputStream in, String encoding) throws IOException {
/* 66 */     byte[] bytes = slurpBytes(in);
/* 67 */     return new String(bytes, encoding);
/*    */   }
/*    */ 
/*    */   public static void copyBytesToFile(File file, byte[] newBytes)
/*    */     throws IOException
/*    */   {
/* 77 */     OutputStream oStream = new FileOutputStream(file);
/*    */     try {
/* 79 */       copy(new ByteArrayInputStream(newBytes), oStream, true);
/*    */     } finally {
/* 81 */       oStream.close();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.util.Streams
 * JD-Core Version:    0.6.2
 */