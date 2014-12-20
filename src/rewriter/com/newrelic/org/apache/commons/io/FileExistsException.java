/*    */ package com.newrelic.org.apache.commons.io;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class FileExistsException extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public FileExistsException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public FileExistsException(String message)
/*    */   {
/* 48 */     super(message);
/*    */   }
/*    */ 
/*    */   public FileExistsException(File file)
/*    */   {
/* 57 */     super("File " + file + " exists");
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.FileExistsException
 * JD-Core Version:    0.6.2
 */