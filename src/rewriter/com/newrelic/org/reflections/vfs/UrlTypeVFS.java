/*     */ package com.newrelic.org.reflections.vfs;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Predicate;
/*     */ import com.newrelic.org.reflections.ReflectionsException;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class UrlTypeVFS
/*     */   implements Vfs.UrlType
/*     */ {
/*  26 */   public static final String[] REPLACE_EXTENSION = { ".ear/", ".jar/", ".war/", ".sar/", ".har/", ".par/" };
/*     */ 
/*  28 */   final String VFSZIP = "vfszip";
/*  29 */   final String VFSFILE = "vfsfile";
/*     */ 
/*  85 */   Predicate<File> realFile = new Predicate() {
/*     */     public boolean apply(File file) {
/*  87 */       return (file.exists()) && (file.isFile());
/*     */     }
/*  85 */   };
/*     */ 
/*     */   public boolean matches(URL url)
/*     */   {
/*  32 */     return ("vfszip".equals(url.getProtocol())) || ("vfsfile".equals(url.getProtocol()));
/*     */   }
/*     */ 
/*     */   public Vfs.Dir createDir(URL url) {
/*     */     try {
/*  37 */       URL adaptedUrl = adaptURL(url);
/*  38 */       return new ZipDir(new JarFile(adaptedUrl.getFile()));
/*     */     } catch (Exception e) {
/*  40 */       e.printStackTrace();
/*     */       try {
/*  42 */         return new ZipDir(new JarFile(url.getFile()));
/*     */       } catch (IOException e1) {
/*  44 */         e.printStackTrace();
/*     */       }
/*     */     }
/*  47 */     return null;
/*     */   }
/*     */ 
/*     */   public URL adaptURL(URL url) throws MalformedURLException {
/*  51 */     if ("vfszip".equals(url.getProtocol()))
/*  52 */       return replaceZipSeparators(url.getPath(), this.realFile);
/*  53 */     if ("vfsfile".equals(url.getProtocol())) {
/*  54 */       return new URL(url.toString().replace("vfsfile", "file"));
/*     */     }
/*  56 */     return url;
/*     */   }
/*     */ 
/*     */   URL replaceZipSeparators(String path, Predicate<File> acceptFile)
/*     */     throws MalformedURLException
/*     */   {
/*  62 */     int pos = 0;
/*  63 */     while (pos != -1) {
/*  64 */       pos = findFirstMatchOfDeployableExtention(path, pos);
/*     */ 
/*  66 */       if (pos > 0) {
/*  67 */         File file = new File(path.substring(0, pos - 1));
/*  68 */         if (acceptFile.apply(file)) return replaceZipSeparatorStartingFrom(path, pos);
/*     */       }
/*     */     }
/*     */ 
/*  72 */     throw new ReflectionsException("Unable to identify the real zip file in path '" + path + "'.");
/*     */   }
/*     */ 
/*     */   int findFirstMatchOfDeployableExtention(String path, int pos) {
/*  76 */     Pattern p = Pattern.compile("\\.[ejprw]ar/");
/*  77 */     Matcher m = p.matcher(path);
/*  78 */     if (m.find(pos)) {
/*  79 */       return m.end();
/*     */     }
/*  81 */     return -1;
/*     */   }
/*     */ 
/*     */   URL replaceZipSeparatorStartingFrom(String path, int pos)
/*     */     throws MalformedURLException
/*     */   {
/*  93 */     String zipFile = path.substring(0, pos - 1);
/*  94 */     String zipPath = path.substring(pos);
/*     */ 
/*  96 */     int numSubs = 1;
/*  97 */     for (String ext : REPLACE_EXTENSION) {
/*  98 */       while (zipPath.contains(ext)) {
/*  99 */         zipPath = zipPath.replace(ext, ext.substring(0, 4) + "!");
/* 100 */         numSubs++;
/*     */       }
/*     */     }
/*     */ 
/* 104 */     String prefix = "";
/* 105 */     for (int i = 0; i < numSubs; i++) {
/* 106 */       prefix = prefix + "zip:";
/*     */     }
/*     */ 
/* 109 */     return new URL(prefix + "/" + zipFile + "!" + zipPath);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.vfs.UrlTypeVFS
 * JD-Core Version:    0.6.2
 */