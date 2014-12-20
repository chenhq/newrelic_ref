/*    */ package com.newrelic.agent.util;
/*    */ 
/*    */ import com.newrelic.objectweb.asm.AnnotationVisitor;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class AnnotationImpl
/*    */   implements AnnotationVisitor
/*    */ {
/*    */   private final String name;
/*    */   private Map<String, Object> attributes;
/*    */ 
/*    */   public AnnotationImpl(String name)
/*    */   {
/* 17 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public void visitEnum(String name, String desc, String value)
/*    */   {
/* 22 */     if (this.attributes == null) {
/* 23 */       this.attributes = new HashMap();
/*    */     }
/* 25 */     this.attributes.put(name, value);
/*    */   }
/*    */ 
/*    */   public void visitEnd()
/*    */   {
/*    */   }
/*    */ 
/*    */   public AnnotationVisitor visitArray(String name)
/*    */   {
/* 36 */     return new ArrayVisitor(name);
/*    */   }
/*    */ 
/*    */   public AnnotationVisitor visitAnnotation(String name, String desc)
/*    */   {
/* 41 */     return null;
/*    */   }
/*    */ 
/*    */   public void visit(String name, Object value)
/*    */   {
/* 46 */     if (this.attributes == null) {
/* 47 */       this.attributes = new HashMap();
/*    */     }
/* 49 */     this.attributes.put(name, value);
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 53 */     return this.name;
/*    */   }
/*    */ 
/*    */   public Map<String, Object> getAttributes() {
/* 57 */     return this.attributes == null ? Collections.emptyMap() : this.attributes;
/*    */   }
/*    */ 
/*    */   private final class ArrayVisitor
/*    */     implements AnnotationVisitor
/*    */   {
/*    */     private final String name;
/* 62 */     private final ArrayList<Object> values = new ArrayList();
/*    */ 
/*    */     public ArrayVisitor(String name) {
/* 65 */       this.name = name;
/*    */     }
/*    */ 
/*    */     public void visit(String name, Object value)
/*    */     {
/* 70 */       this.values.add(value);
/*    */     }
/*    */ 
/*    */     public AnnotationVisitor visitAnnotation(String arg0, String arg1)
/*    */     {
/* 75 */       return null;
/*    */     }
/*    */ 
/*    */     public AnnotationVisitor visitArray(String name)
/*    */     {
/* 80 */       return null;
/*    */     }
/*    */ 
/*    */     public void visitEnd()
/*    */     {
/* 85 */       AnnotationImpl.this.visit(this.name, this.values.toArray(new String[0]));
/*    */     }
/*    */ 
/*    */     public void visitEnum(String arg0, String arg1, String arg2)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.util.AnnotationImpl
 * JD-Core Version:    0.6.2
 */