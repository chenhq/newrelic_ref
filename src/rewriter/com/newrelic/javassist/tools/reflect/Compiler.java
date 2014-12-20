/*     */ package com.newrelic.javassist.tools.reflect;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class Compiler
/*     */ {
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  73 */     if (args.length == 0) {
/*  74 */       help(System.err);
/*  75 */       return;
/*     */     }
/*     */ 
/*  78 */     CompiledClass[] entries = new CompiledClass[args.length];
/*  79 */     int n = parse(args, entries);
/*     */ 
/*  81 */     if (n < 1) {
/*  82 */       System.err.println("bad parameter.");
/*  83 */       return;
/*     */     }
/*     */ 
/*  86 */     processClasses(entries, n);
/*     */   }
/*     */ 
/*     */   private static void processClasses(CompiledClass[] entries, int n)
/*     */     throws Exception
/*     */   {
/*  92 */     Reflection implementor = new Reflection();
/*  93 */     ClassPool pool = ClassPool.getDefault();
/*  94 */     implementor.start(pool);
/*     */ 
/*  96 */     for (int i = 0; i < n; i++) {
/*  97 */       CtClass c = pool.get(entries[i].classname);
/*  98 */       if ((entries[i].metaobject != null) || (entries[i].classobject != null))
/*     */       {
/*     */         String metaobj;
/*     */         String metaobj;
/* 102 */         if (entries[i].metaobject == null)
/* 103 */           metaobj = "com.newrelic.javassist.tools.reflect.Metaobject";
/*     */         else
/* 105 */           metaobj = entries[i].metaobject;
/*     */         String classobj;
/*     */         String classobj;
/* 107 */         if (entries[i].classobject == null)
/* 108 */           classobj = "com.newrelic.javassist.tools.reflect.ClassMetaobject";
/*     */         else {
/* 110 */           classobj = entries[i].classobject;
/*     */         }
/* 112 */         if (!implementor.makeReflective(c, pool.get(metaobj), pool.get(classobj)))
/*     */         {
/* 114 */           System.err.println("Warning: " + c.getName() + " is reflective.  It was not changed.");
/*     */         }
/*     */ 
/* 117 */         System.err.println(c.getName() + ": " + metaobj + ", " + classobj);
/*     */       }
/*     */       else
/*     */       {
/* 121 */         System.err.println(c.getName() + ": not reflective");
/*     */       }
/*     */     }
/* 124 */     for (int i = 0; i < n; i++) {
/* 125 */       implementor.onLoad(pool, entries[i].classname);
/* 126 */       pool.get(entries[i].classname).writeFile();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static int parse(String[] args, CompiledClass[] result) {
/* 131 */     int n = -1;
/* 132 */     for (int i = 0; i < args.length; i++) {
/* 133 */       String a = args[i];
/* 134 */       if (a.equals("-m")) {
/* 135 */         if ((n < 0) || (i + 1 > args.length)) {
/* 136 */           return -1;
/*     */         }
/* 138 */         result[n].metaobject = args[(++i)];
/* 139 */       } else if (a.equals("-c")) {
/* 140 */         if ((n < 0) || (i + 1 > args.length)) {
/* 141 */           return -1;
/*     */         }
/* 143 */         result[n].classobject = args[(++i)]; } else {
/* 144 */         if (a.charAt(0) == '-') {
/* 145 */           return -1;
/*     */         }
/* 147 */         CompiledClass cc = new CompiledClass();
/* 148 */         cc.classname = a;
/* 149 */         cc.metaobject = null;
/* 150 */         cc.classobject = null;
/* 151 */         result[(++n)] = cc;
/*     */       }
/*     */     }
/*     */ 
/* 155 */     return n + 1;
/*     */   }
/*     */ 
/*     */   private static void help(PrintStream out) {
/* 159 */     out.println("Usage: java javassist.tools.reflect.Compiler");
/* 160 */     out.println("            (<class> [-m <metaobject>] [-c <class metaobject>])+");
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.reflect.Compiler
 * JD-Core Version:    0.6.2
 */