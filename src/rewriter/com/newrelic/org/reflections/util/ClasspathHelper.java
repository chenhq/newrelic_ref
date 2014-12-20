/*     */ package com.newrelic.org.reflections.util;
/*     */ 
/*     */ import com.newrelic.com.google.common.collect.Sets;
/*     */ import com.newrelic.org.reflections.Reflections;
/*     */ import com.newrelic.org.slf4j.Logger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.Attributes.Name;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.jar.Manifest;
/*     */ import javax.servlet.ServletContext;
/*     */ 
/*     */ public abstract class ClasspathHelper
/*     */ {
/*     */   public static ClassLoader contextClassLoader()
/*     */   {
/*  26 */     return Thread.currentThread().getContextClassLoader();
/*     */   }
/*     */   public static ClassLoader staticClassLoader() {
/*  29 */     return Reflections.class.getClassLoader();
/*     */   }
/*     */ 
/*     */   public static ClassLoader[] classLoaders(ClassLoader[] classLoaders) {
/*  33 */     if ((classLoaders != null) && (classLoaders.length != 0)) {
/*  34 */       return classLoaders;
/*     */     }
/*  36 */     ClassLoader contextClassLoader = contextClassLoader(); ClassLoader staticClassLoader = staticClassLoader();
/*  37 */     return new ClassLoader[] { contextClassLoader != staticClassLoader ? new ClassLoader[] { contextClassLoader, staticClassLoader } : contextClassLoader };
/*     */   }
/*     */ 
/*     */   public static Set<URL> forPackage(String name, ClassLoader[] classLoaders)
/*     */   {
/*  46 */     Set result = Sets.newHashSet();
/*     */ 
/*  48 */     ClassLoader[] loaders = classLoaders(classLoaders);
/*  49 */     String resourceName = resourceName(name);
/*     */ 
/*  51 */     for (ClassLoader classLoader : loaders) {
/*     */       try {
/*  53 */         Enumeration urls = classLoader.getResources(resourceName);
/*  54 */         while (urls.hasMoreElements()) {
/*  55 */           URL url = (URL)urls.nextElement();
/*  56 */           int index = url.toExternalForm().lastIndexOf(resourceName);
/*  57 */           if (index != -1)
/*  58 */             result.add(new URL(url.toExternalForm().substring(0, index)));
/*     */           else
/*  60 */             result.add(url);
/*     */         }
/*     */       }
/*     */       catch (IOException e) {
/*  64 */         if (Reflections.log != null) {
/*  65 */           Reflections.log.error("error getting resources for package " + name, e);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  70 */     return result;
/*     */   }
/*     */ 
/*     */   public static URL forClass(Class<?> aClass, ClassLoader[] classLoaders)
/*     */   {
/*  77 */     ClassLoader[] loaders = classLoaders(classLoaders);
/*  78 */     String resourceName = aClass.getName().replace(".", "/") + ".class";
/*     */ 
/*  80 */     for (ClassLoader classLoader : loaders) {
/*     */       try {
/*  82 */         URL url = classLoader.getResource(resourceName);
/*  83 */         if (url != null) {
/*  84 */           String normalizedUrl = url.toExternalForm().substring(0, url.toExternalForm().lastIndexOf(aClass.getPackage().getName().replace(".", "/")));
/*  85 */           return new URL(normalizedUrl);
/*     */         }
/*     */       } catch (MalformedURLException e) {
/*  88 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */ 
/*  92 */     return null;
/*     */   }
/*     */ 
/*     */   public static Set<URL> forClassLoader(ClassLoader[] classLoaders)
/*     */   {
/*  99 */     Set result = Sets.newHashSet();
/*     */ 
/* 101 */     ClassLoader[] loaders = classLoaders(classLoaders);
/*     */ 
/* 103 */     for (ClassLoader classLoader : loaders) {
/* 104 */       while (classLoader != null) {
/* 105 */         if ((classLoader instanceof URLClassLoader)) {
/* 106 */           URL[] urls = ((URLClassLoader)classLoader).getURLs();
/* 107 */           if (urls != null) {
/* 108 */             result.addAll(Sets.newHashSet(urls));
/*     */           }
/*     */         }
/* 111 */         classLoader = classLoader.getParent();
/*     */       }
/*     */     }
/*     */ 
/* 115 */     return result;
/*     */   }
/*     */ 
/*     */   public static Set<URL> forJavaClassPath()
/*     */   {
/* 120 */     Set urls = Sets.newHashSet();
/*     */ 
/* 122 */     String javaClassPath = System.getProperty("java.class.path");
/* 123 */     if (javaClassPath != null) {
/* 124 */       for (String path : javaClassPath.split(File.pathSeparator)) try {
/* 125 */           urls.add(new File(path).toURI().toURL()); } catch (Exception e) {
/* 126 */           e.printStackTrace();
/*     */         }
/*     */     }
/*     */ 
/* 130 */     return urls;
/*     */   }
/*     */ 
/*     */   public static Set<URL> forWebInfLib(ServletContext servletContext)
/*     */   {
/* 135 */     Set urls = Sets.newHashSet();
/*     */ 
/* 137 */     for (Iterator i$ = servletContext.getResourcePaths("/WEB-INF/lib").iterator(); i$.hasNext(); ) { Object urlString = i$.next();
/*     */       try { urls.add(servletContext.getResource((String)urlString));
/*     */       } catch (MalformedURLException e)
/*     */       {
/*     */       } }
/* 142 */     return urls;
/*     */   }
/*     */ 
/*     */   public static URL forWebInfClasses(ServletContext servletContext)
/*     */   {
/*     */     try {
/* 148 */       String path = servletContext.getRealPath("/WEB-INF/classes");
/* 149 */       if (path != null) {
/* 150 */         File file = new File(path);
/* 151 */         if (file.exists())
/* 152 */           return file.toURL();
/*     */       } else {
/* 154 */         return servletContext.getResource("/WEB-INF/classes");
/*     */       }
/*     */     }
/*     */     catch (MalformedURLException e) {
/*     */     }
/* 159 */     return null;
/*     */   }
/*     */ 
/*     */   public static Set<URL> forManifest()
/*     */   {
/* 165 */     return forManifest(forClassLoader(new ClassLoader[0]));
/*     */   }
/*     */ 
/*     */   public static Set<URL> forManifest(URL url)
/*     */   {
/* 171 */     Set result = Sets.newHashSet();
/*     */ 
/* 173 */     result.add(url);
/*     */     try
/*     */     {
/* 176 */       String part = cleanPath(url);
/* 177 */       File jarFile = new File(part);
/* 178 */       JarFile myJar = new JarFile(part);
/*     */ 
/* 180 */       URL validUrl = tryToGetValidUrl(jarFile.getPath(), new File(part).getParent(), part);
/* 181 */       if (validUrl != null) result.add(validUrl);
/*     */ 
/* 183 */       Manifest manifest = myJar.getManifest();
/* 184 */       if (manifest != null) {
/* 185 */         String classPath = manifest.getMainAttributes().getValue(new Attributes.Name("Class-Path"));
/* 186 */         if (classPath != null) {
/* 187 */           for (String jar : classPath.split(" ")) {
/* 188 */             validUrl = tryToGetValidUrl(jarFile.getPath(), new File(part).getParent(), jar);
/* 189 */             if (validUrl != null) result.add(validUrl);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/* 197 */     return result;
/*     */   }
/*     */ 
/*     */   public static Set<URL> forManifest(Iterable<URL> urls)
/*     */   {
/* 203 */     Set result = Sets.newHashSet();
/*     */ 
/* 206 */     for (URL url : urls) {
/* 207 */       result.addAll(forManifest(url));
/*     */     }
/*     */ 
/* 210 */     return result;
/*     */   }
/*     */ 
/*     */   static URL tryToGetValidUrl(String workingDir, String path, String filename)
/*     */   {
/*     */     try
/*     */     {
/* 217 */       if (new File(filename).exists())
/* 218 */         return new File(filename).toURI().toURL();
/* 219 */       if (new File(path + File.separator + filename).exists())
/* 220 */         return new File(path + File.separator + filename).toURI().toURL();
/* 221 */       if (new File(workingDir + File.separator + filename).exists())
/* 222 */         return new File(workingDir + File.separator + filename).toURI().toURL();
/* 223 */       if (new File(new URL(filename).getFile()).exists())
/* 224 */         return new File(new URL(filename).getFile()).toURI().toURL();
/*     */     }
/*     */     catch (MalformedURLException e) {
/*     */     }
/* 228 */     return null;
/*     */   }
/*     */ 
/*     */   public static String cleanPath(URL url) {
/* 232 */     String path = url.getPath();
/*     */     try {
/* 234 */       path = URLDecoder.decode(path, "UTF-8"); } catch (UnsupportedEncodingException e) {
/*     */     }
/* 236 */     if (path.startsWith("jar:")) {
/* 237 */       path = path.substring("jar:".length());
/*     */     }
/* 239 */     if (path.startsWith("file:")) {
/* 240 */       path = path.substring("file:".length());
/*     */     }
/* 242 */     if (path.endsWith("!/")) {
/* 243 */       path = path.substring(0, path.lastIndexOf("!/")) + "/";
/*     */     }
/* 245 */     return path;
/*     */   }
/*     */ 
/*     */   private static String resourceName(String name) {
/* 249 */     if (name != null) {
/* 250 */       String resourceName = name.replace(".", "/");
/* 251 */       resourceName = resourceName.replace("\\", "/");
/* 252 */       if (resourceName.startsWith("/")) {
/* 253 */         resourceName = resourceName.substring(1);
/*     */       }
/* 255 */       return resourceName;
/*     */     }
/* 257 */     return name;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.util.ClasspathHelper
 * JD-Core Version:    0.6.2
 */