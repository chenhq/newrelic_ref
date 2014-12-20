/*    */ package com.newrelic.agent.util;
/*    */ 
/*    */ import com.newrelic.objectweb.asm.AnnotationVisitor;
/*    */ import com.newrelic.objectweb.asm.ClassReader;
/*    */ import com.newrelic.objectweb.asm.MethodVisitor;
/*    */ import com.newrelic.objectweb.asm.commons.EmptyVisitor;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ 
/*    */ class MethodAnnotationVisitor
/*    */ {
/*    */   public static Collection<MethodAnnotation> getAnnotations(ClassReader cr, String annotationDescription)
/*    */   {
/* 13 */     MethodAnnotationClassVisitor visitor = new MethodAnnotationClassVisitor(annotationDescription);
/* 14 */     cr.accept(visitor, 0);
/* 15 */     return visitor.getAnnotations();
/*    */   }
/*    */ 
/*    */   private static class MethodAnnotationClassVisitor extends EmptyVisitor
/*    */   {
/*    */     String className;
/*    */     private final String annotationDescription;
/* 22 */     private final Collection<MethodAnnotation> annotations = new ArrayList();
/*    */ 
/*    */     public MethodAnnotationClassVisitor(String annotationDescription) {
/* 25 */       this.annotationDescription = annotationDescription;
/*    */     }
/*    */ 
/*    */     public Collection<MethodAnnotation> getAnnotations() {
/* 29 */       return this.annotations;
/*    */     }
/*    */ 
/*    */     public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
/*    */     {
/* 34 */       this.className = name;
/*    */     }
/*    */ 
/*    */     public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*    */     {
/* 40 */       return new MethodAnnotationVisitorImpl(name, desc);
/*    */     }
/*    */ 
/*    */     private class MethodAnnotationVisitorImpl extends EmptyVisitor {
/*    */       private final String methodName;
/*    */       private final String methodDesc;
/*    */ 
/*    */       public MethodAnnotationVisitorImpl(String name, String desc) {
/* 49 */         this.methodName = name;
/* 50 */         this.methodDesc = desc;
/*    */       }
/*    */ 
/*    */       public AnnotationVisitor visitAnnotation(String desc, boolean visible)
/*    */       {
/* 55 */         if (MethodAnnotationVisitor.MethodAnnotationClassVisitor.this.annotationDescription.equals(desc)) {
/* 56 */           MethodAnnotationImpl annotation = new MethodAnnotationImpl(desc);
/* 57 */           MethodAnnotationVisitor.MethodAnnotationClassVisitor.this.annotations.add(annotation);
/* 58 */           return annotation;
/*    */         }
/* 60 */         return null;
/*    */       }
/*    */ 
/*    */       private class MethodAnnotationImpl extends AnnotationImpl implements MethodAnnotation
/*    */       {
/*    */         public MethodAnnotationImpl(String desc) {
/* 66 */           super();
/*    */         }
/*    */ 
/*    */         public String getMethodName()
/*    */         {
/* 71 */           return MethodAnnotationVisitor.MethodAnnotationClassVisitor.MethodAnnotationVisitorImpl.this.methodName;
/*    */         }
/*    */ 
/*    */         public String getMethodDesc()
/*    */         {
/* 76 */           return MethodAnnotationVisitor.MethodAnnotationClassVisitor.MethodAnnotationVisitorImpl.this.methodDesc;
/*    */         }
/*    */ 
/*    */         public String getClassName()
/*    */         {
/* 81 */           return MethodAnnotationVisitor.MethodAnnotationClassVisitor.this.className;
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.util.MethodAnnotationVisitor
 * JD-Core Version:    0.6.2
 */