/*     */ package com.newrelic.org.reflections.vfs;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Predicate;
/*     */ import com.newrelic.com.google.common.collect.Iterables;
/*     */ import com.newrelic.com.google.common.collect.Lists;
/*     */ import com.newrelic.org.reflections.Reflections;
/*     */ import com.newrelic.org.reflections.ReflectionsException;
/*     */ import com.newrelic.org.reflections.util.Utils;
/*     */ import com.newrelic.org.slf4j.Logger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.JarURLConnection;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.jar.JarFile;
/*     */ 
/*     */ public abstract class Vfs
/*     */ {
/*  51 */   private static List<UrlType> defaultUrlTypes = Lists.newArrayList(DefaultUrlTypes.values());
/*     */ 
/*     */   public static List<UrlType> getDefaultUrlTypes()
/*     */   {
/*  75 */     return defaultUrlTypes;
/*     */   }
/*     */ 
/*     */   public static void setDefaultURLTypes(List<UrlType> urlTypes)
/*     */   {
/*  80 */     defaultUrlTypes = urlTypes;
/*     */   }
/*     */ 
/*     */   public static void addDefaultURLTypes(UrlType urlType)
/*     */   {
/*  85 */     defaultUrlTypes.add(urlType);
/*     */   }
/*     */ 
/*     */   public static Dir fromURL(URL url)
/*     */   {
/*  90 */     return fromURL(url, defaultUrlTypes);
/*     */   }
/*     */ 
/*     */   public static Dir fromURL(URL url, List<UrlType> urlTypes)
/*     */   {
/*  95 */     for (UrlType type : urlTypes) {
/*     */       try {
/*  97 */         if (type.matches(url)) {
/*  98 */           Dir dir = type.createDir(url);
/*  99 */           if (dir != null) return dir; 
/*     */         }
/*     */       }
/* 102 */       catch (Throwable e) { if (Reflections.log != null) {
/* 103 */           Reflections.log.warn("could not create Dir using " + type + " from url " + url.toExternalForm() + ". skipping.", e);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 108 */     throw new ReflectionsException("could not create Vfs.Dir from url, no matching UrlType was found [" + url.toExternalForm() + "]\n" + "either use fromURL(final URL url, final List<UrlType> urlTypes) or " + "use the static setDefaultURLTypes(final List<UrlType> urlTypes) or addDefaultURLTypes(UrlType urlType) " + "with your specialized UrlType.");
/*     */   }
/*     */ 
/*     */   public static Dir fromURL(URL url, UrlType[] urlTypes)
/*     */   {
/* 116 */     return fromURL(url, Lists.newArrayList(urlTypes));
/*     */   }
/*     */ 
/*     */   public static Iterable<File> findFiles(Collection<URL> inUrls, String packagePrefix, final Predicate<String> nameFilter)
/*     */   {
/* 121 */     Predicate fileNamePredicate = new Predicate() {
/*     */       public boolean apply(Vfs.File file) {
/* 123 */         String path = file.getRelativePath();
/* 124 */         if (path.startsWith(this.val$packagePrefix)) {
/* 125 */           String filename = path.substring(path.indexOf(this.val$packagePrefix) + this.val$packagePrefix.length());
/* 126 */           return (!Utils.isEmpty(filename)) && (nameFilter.apply(filename.substring(1)));
/*     */         }
/* 128 */         return false;
/*     */       }
/*     */     };
/* 133 */     return findFiles(inUrls, fileNamePredicate);
/*     */   }
/*     */ 
/*     */   public static Iterable<File> findFiles(Collection<URL> inUrls, Predicate<File> filePredicate)
/*     */   {
/* 138 */     Iterable result = new ArrayList();
/*     */ 
/* 140 */     for (URL url : inUrls) {
/*     */       try {
/* 142 */         result = Iterables.concat(result, Iterables.filter(new Iterable()
/*     */         {
/*     */           public Iterator<Vfs.File> iterator() {
/* 145 */             return Vfs.fromURL(this.val$url).getFiles().iterator();
/*     */           }
/*     */         }
/*     */         , filePredicate));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 149 */         if (Reflections.log != null) {
/* 150 */           Reflections.log.error("could not findFiles for url. continuing. [" + url + "]", e);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 155 */     return result;
/*     */   }
/*     */ 
/*     */   static File getFile(URL url)
/*     */   {
/*     */     String path;
/*     */     File file;
/*     */     try {
/* 164 */       path = url.toURI().getSchemeSpecificPart();
/* 165 */       if ((file = new File(path)).exists()) return file; 
/*     */     }
/*     */     catch (URISyntaxException e)
/*     */     {
/*     */     }
/*     */     try { path = URLDecoder.decode(url.getPath(), "UTF-8");
/* 171 */       if (path.contains(".jar!")) path = path.substring(0, path.lastIndexOf(".jar!") + ".jar".length());
/* 172 */       if ((file = new File(path)).exists()) return file; 
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/*     */     }
/*     */     try
/*     */     {
/* 178 */       path = url.toExternalForm();
/* 179 */       if (path.startsWith("jar:")) path = path.substring("jar:".length());
/* 180 */       if (path.startsWith("file:")) path = path.substring("file:".length());
/* 181 */       if (path.contains(".jar!")) path = path.substring(0, path.indexOf(".jar!") + ".jar".length());
/* 182 */       if ((file = new File(path)).exists()) return file;
/*     */ 
/* 184 */       path = path.replace("%20", " ");
/* 185 */       if ((file = new File(path)).exists()) return file;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/* 190 */     return null;
/*     */   }
/*     */ 
/*     */   public static abstract enum DefaultUrlTypes
/*     */     implements Vfs.UrlType
/*     */   {
/* 201 */     jarFile, 
/*     */ 
/* 211 */     jarUrl, 
/*     */ 
/* 223 */     directory;
/*     */   }
/*     */ 
/*     */   public static abstract interface UrlType
/*     */   {
/*     */     public abstract boolean matches(URL paramURL)
/*     */       throws Exception;
/*     */ 
/*     */     public abstract Vfs.Dir createDir(URL paramURL)
/*     */       throws Exception;
/*     */   }
/*     */ 
/*     */   public static abstract interface File
/*     */   {
/*     */     public abstract String getName();
/*     */ 
/*     */     public abstract String getRelativePath();
/*     */ 
/*     */     public abstract InputStream openInputStream()
/*     */       throws IOException;
/*     */   }
/*     */ 
/*     */   public static abstract interface Dir
/*     */   {
/*     */     public abstract String getPath();
/*     */ 
/*     */     public abstract Iterable<Vfs.File> getFiles();
/*     */ 
/*     */     public abstract void close();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.vfs.Vfs
 * JD-Core Version:    0.6.2
 */