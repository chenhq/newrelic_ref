/*     */ package com.newrelic.agent.util;
/*     */ 
/*     */ import com.newrelic.agent.android.Agent;
/*     */ import com.newrelic.objectweb.asm.ClassReader;
/*     */ import com.newrelic.objectweb.asm.Type;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class Annotations
/*     */ {
/*     */   public static Collection<ClassAnnotation> getClassAnnotations(Class annotationClass, String packageSearchPath, Set<URL> classpathURLs)
/*     */   {
/*  32 */     String annotationDescription = 'L' + annotationClass.getName().replace('.', '/') + ';';
/*  33 */     Map fileNames = getMatchingFiles(packageSearchPath, classpathURLs);
/*     */ 
/*  35 */     Collection list = new ArrayList();
/*  36 */     for (Map.Entry entry : fileNames.entrySet()) {
/*  37 */       String fileName = (String)entry.getKey();
/*  38 */       ByteArrayOutputStream out = new ByteArrayOutputStream();
/*     */       try {
/*  40 */         Streams.copy(Annotations.class.getResourceAsStream('/' + fileName), out, true);
/*  41 */         ClassReader cr = new ClassReader(out.toByteArray());
/*  42 */         Collection annotations = ClassAnnotationVisitor.getAnnotations(cr, annotationDescription);
/*  43 */         list.addAll(annotations);
/*     */       }
/*     */       catch (IOException e) {
/*  46 */         e.printStackTrace();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  51 */     return list;
/*     */   }
/*     */ 
/*     */   public static Collection<MethodAnnotation> getMethodAnnotations(Class annotationClass, String packageSearchPath, Set<URL> classpathURLs) {
/*  55 */     String annotationDescription = Type.getType(annotationClass).getDescriptor();
/*  56 */     Map fileNames = getMatchingFiles(packageSearchPath, classpathURLs);
/*     */ 
/*  58 */     Collection list = new ArrayList();
/*  59 */     for (Map.Entry entry : fileNames.entrySet()) {
/*  60 */       String fileName = (String)entry.getKey();
/*  61 */       ByteArrayOutputStream out = new ByteArrayOutputStream();
/*     */       try {
/*  63 */         Streams.copy(Annotations.class.getResourceAsStream('/' + fileName), out, true);
/*  64 */         ClassReader cr = new ClassReader(out.toByteArray());
/*  65 */         Collection annotations = MethodAnnotationVisitor.getAnnotations(cr, annotationDescription);
/*  66 */         list.addAll(annotations);
/*     */       }
/*     */       catch (IOException e) {
/*  69 */         e.printStackTrace();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  74 */     return list;
/*     */   }
/*     */ 
/*     */   private static Map<String, URL> getMatchingFiles(String packageSearchPath, Set<URL> classpathURLs)
/*     */   {
/*  79 */     if (!packageSearchPath.endsWith("/")) {
/*  80 */       packageSearchPath = packageSearchPath + "/";
/*     */     }
/*  82 */     Pattern pattern = Pattern.compile("(.*).class");
/*  83 */     Map fileNames = getMatchingFileNames(pattern, classpathURLs);
/*  84 */     for (String file : (String[])fileNames.keySet().toArray(new String[0])) {
/*  85 */       if (!file.startsWith(packageSearchPath)) {
/*  86 */         fileNames.remove(file);
/*     */       }
/*     */     }
/*  89 */     return fileNames;
/*     */   }
/*     */ 
/*     */   static Map<String, URL> getMatchingFileNames(Pattern pattern, Collection<URL> urls) {
/*  93 */     Map names = new HashMap();
/*  94 */     for (URL url : urls) {
/*  95 */       url = fixUrl(url);
/*     */       File file;
/*     */       try
/*     */       {
/* 100 */         file = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
/*     */       }
/*     */       catch (UnsupportedEncodingException e) {
/* 103 */         e.printStackTrace();
/* 104 */         System.exit(1);
/*     */ 
/* 106 */         return names;
/*     */       }
/*     */ 
/* 109 */       if (file.isDirectory()) {
/* 110 */         List files = PatternFileMatcher.getMatchingFiles(file, pattern);
/* 111 */         for (File f : files) {
/* 112 */           String path = f.getAbsolutePath();
/* 113 */           path = path.substring(file.getAbsolutePath().length() + 1);
/* 114 */           names.put(path, url);
/*     */         }
/* 116 */       } else if (file.isFile()) {
/* 117 */         JarFile jarFile = null;
/*     */         try
/*     */         {
/* 120 */           jarFile = new JarFile(file);
/*     */ 
/* 122 */           for (entries = jarFile.entries(); entries.hasMoreElements(); ) {
/* 123 */             JarEntry jarEntry = (JarEntry)entries.nextElement();
/* 124 */             if (pattern.matcher(jarEntry.getName()).matches())
/* 125 */               names.put(jarEntry.getName(), url);
/*     */           }
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/*     */           Enumeration entries;
/* 129 */           e.printStackTrace();
/* 130 */           System.exit(1);
/*     */         }
/*     */         finally {
/* 133 */           if (jarFile != null)
/*     */             try {
/* 135 */               jarFile.close();
/*     */             } catch (IOException e) {
/*     */             }
/*     */         }
/*     */       }
/*     */     }
/* 141 */     return names;
/*     */   }
/*     */ 
/*     */   private static URL fixUrl(URL url)
/*     */   {
/* 151 */     String protocol = url.getProtocol();
/*     */ 
/* 153 */     if ("jar".equals(protocol)) {
/*     */       try {
/* 155 */         String urlString = url.toString().substring(4);
/* 156 */         int index = urlString.indexOf("!/");
/* 157 */         if (index > 0) {
/* 158 */           urlString = urlString.substring(0, index);
/*     */         }
/* 160 */         url = new URL(urlString);
/*     */       } catch (Exception e) {
/* 162 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 165 */     return url;
/*     */   }
/*     */ 
/*     */   static URL[] getClasspathURLs() {
/* 169 */     ClassLoader classLoader = Agent.class.getClassLoader();
/* 170 */     if ((classLoader instanceof URLClassLoader)) {
/* 171 */       return ((URLClassLoader)classLoader).getURLs();
/*     */     }
/* 173 */     return new URL[0];
/*     */   }
/*     */ 
/*     */   static class PatternFileMatcher
/*     */   {
/*     */     private final FileFilter filter;
/* 183 */     private final List<File> files = new ArrayList();
/*     */ 
/*     */     public static List<File> getMatchingFiles(File directory, Pattern pattern) {
/* 186 */       PatternFileMatcher matcher = new PatternFileMatcher(pattern);
/* 187 */       directory.listFiles(matcher.filter);
/* 188 */       return matcher.files;
/*     */     }
/*     */ 
/*     */     private PatternFileMatcher(final Pattern pattern)
/*     */     {
/* 194 */       this.filter = new FileFilter()
/*     */       {
/*     */         public boolean accept(File f)
/*     */         {
/* 198 */           if (f.isDirectory()) {
/* 199 */             f.listFiles(this);
/*     */           }
/* 201 */           boolean match = pattern.matcher(f.getAbsolutePath()).matches();
/* 202 */           if (match) {
/* 203 */             Annotations.PatternFileMatcher.this.files.add(f);
/*     */           }
/* 205 */           return match;
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.util.Annotations
 * JD-Core Version:    0.6.2
 */