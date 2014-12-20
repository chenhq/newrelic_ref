/*     */ package com.newrelic.org.reflections.util;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Predicate;
/*     */ import com.newrelic.com.google.common.collect.Lists;
/*     */ import com.newrelic.com.google.common.collect.ObjectArrays;
/*     */ import com.newrelic.com.google.common.collect.Sets;
/*     */ import com.newrelic.org.reflections.Configuration;
/*     */ import com.newrelic.org.reflections.Reflections;
/*     */ import com.newrelic.org.reflections.adapters.JavassistAdapter;
/*     */ import com.newrelic.org.reflections.adapters.MetadataAdapter;
/*     */ import com.newrelic.org.reflections.scanners.Scanner;
/*     */ import com.newrelic.org.reflections.scanners.SubTypesScanner;
/*     */ import com.newrelic.org.reflections.scanners.TypeAnnotationsScanner;
/*     */ import com.newrelic.org.reflections.serializers.Serializer;
/*     */ import com.newrelic.org.reflections.serializers.XmlSerializer;
/*     */ import com.newrelic.org.slf4j.Logger;
/*     */ import java.net.URL;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ 
/*     */ public class ConfigurationBuilder
/*     */   implements Configuration
/*     */ {
/*     */   private final Set<Scanner> scanners;
/*     */   private Set<URL> urls;
/*     */   private MetadataAdapter metadataAdapter;
/*     */   private Predicate<String> inputsFilter;
/*     */   private Serializer serializer;
/*     */   private ExecutorService executorService;
/*     */   private ClassLoader[] classLoaders;
/*     */ 
/*     */   public ConfigurationBuilder()
/*     */   {
/*  49 */     this.scanners = Sets.newHashSet(new Scanner[] { new TypeAnnotationsScanner(), new SubTypesScanner() });
/*  50 */     this.urls = Sets.newHashSet();
/*     */   }
/*     */ 
/*     */   public static ConfigurationBuilder build(Object[] params)
/*     */   {
/*  67 */     ConfigurationBuilder builder = new ConfigurationBuilder();
/*     */ 
/*  70 */     List parameters = Lists.newArrayList();
/*  71 */     for (Object param : params) {
/*  72 */       if (param != null) {
/*  73 */         if (param.getClass().isArray()) { for (Object p : (Object[])param) if (p != null) parameters.add(p);
/*     */         }
/*     */         else
/*     */         {
/*  74 */           Iterator i$;
/*  74 */           if ((param instanceof Iterable)) for (i$ = ((Iterable)param).iterator(); i$.hasNext(); ) { Object p = i$.next(); if (p != null) parameters.add(p);  } 
/*     */           else
/*  75 */             parameters.add(param);
/*     */         }
/*     */       }
/*     */     }
/*  79 */     List loaders = Lists.newArrayList();
/*  80 */     for (Iterator i$ = parameters.iterator(); i$.hasNext(); ) { Object param = i$.next(); if ((param instanceof ClassLoader)) loaders.add((ClassLoader)param);
/*     */     }
/*  82 */     ClassLoader[] classLoaders = loaders.isEmpty() ? null : (ClassLoader[])loaders.toArray(new ClassLoader[loaders.size()]);
/*  83 */     FilterBuilder filter = new FilterBuilder();
/*  84 */     List scanners = Lists.newArrayList();
/*     */ 
/*  86 */     for (Iterator i$ = parameters.iterator(); i$.hasNext(); ) { Object param = i$.next();
/*  87 */       if ((param instanceof String)) { builder.addUrls(ClasspathHelper.forPackage((String)param, classLoaders)); filter.include(FilterBuilder.prefix((String)param));
/*  88 */       } else if ((param instanceof Class)) { builder.addUrls(new URL[] { ClasspathHelper.forClass((Class)param, classLoaders) }); filter.includePackage((Class)param);
/*  89 */       } else if ((param instanceof Scanner)) { scanners.add((Scanner)param);
/*  90 */       } else if ((param instanceof URL)) { builder.addUrls(new URL[] { (URL)param });
/*  91 */       } else if ((!(param instanceof ClassLoader)) && 
/*  92 */         (Reflections.log != null)) { Reflections.log.warn("could not use param " + param); }
/*     */ 
/*     */     }
/*  95 */     builder.filterInputsBy(filter);
/*  96 */     if (!scanners.isEmpty()) builder.setScanners((Scanner[])scanners.toArray(new Scanner[scanners.size()]));
/*  97 */     if (!loaders.isEmpty()) builder.addClassLoaders(loaders);
/*     */ 
/*  99 */     return builder;
/*     */   }
/*     */ 
/*     */   public Set<Scanner> getScanners() {
/* 103 */     return this.scanners;
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder setScanners(Scanner[] scanners)
/*     */   {
/* 108 */     this.scanners.clear();
/* 109 */     return addScanners(scanners);
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder addScanners(Scanner[] scanners)
/*     */   {
/* 114 */     this.scanners.addAll(Sets.newHashSet(scanners));
/* 115 */     return this;
/*     */   }
/*     */ 
/*     */   public Set<URL> getUrls() {
/* 119 */     return this.urls;
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder setUrls(Collection<URL> urls)
/*     */   {
/* 126 */     this.urls = Sets.newHashSet(urls);
/* 127 */     return this;
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder setUrls(URL[] urls)
/*     */   {
/* 134 */     this.urls = Sets.newHashSet(urls);
/* 135 */     return this;
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder addUrls(Collection<URL> urls)
/*     */   {
/* 142 */     this.urls.addAll(urls);
/* 143 */     return this;
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder addUrls(URL[] urls)
/*     */   {
/* 150 */     this.urls.addAll(Sets.newHashSet(urls));
/* 151 */     return this;
/*     */   }
/*     */ 
/*     */   public MetadataAdapter getMetadataAdapter() {
/* 155 */     return this.metadataAdapter = new JavassistAdapter();
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder setMetadataAdapter(MetadataAdapter metadataAdapter)
/*     */   {
/* 160 */     this.metadataAdapter = metadataAdapter;
/* 161 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean acceptsInput(String inputFqn) {
/* 165 */     return (this.inputsFilter == null) || (this.inputsFilter.apply(inputFqn));
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder filterInputsBy(Predicate<String> inputsFilter)
/*     */   {
/* 171 */     this.inputsFilter = inputsFilter;
/* 172 */     return this;
/*     */   }
/*     */ 
/*     */   public ExecutorService getExecutorService() {
/* 176 */     return this.executorService;
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder setExecutorService(ExecutorService executorService)
/*     */   {
/* 181 */     this.executorService = executorService;
/* 182 */     return this;
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder useParallelExecutor()
/*     */   {
/* 188 */     return useParallelExecutor(Runtime.getRuntime().availableProcessors());
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder useParallelExecutor(int availableProcessors)
/*     */   {
/* 194 */     setExecutorService(Executors.newFixedThreadPool(availableProcessors));
/* 195 */     return this;
/*     */   }
/*     */ 
/*     */   public Serializer getSerializer() {
/* 199 */     return this.serializer = new XmlSerializer();
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder setSerializer(Serializer serializer)
/*     */   {
/* 204 */     this.serializer = serializer;
/* 205 */     return this;
/*     */   }
/*     */ 
/*     */   public ClassLoader[] getClassLoaders()
/*     */   {
/* 210 */     return this.classLoaders;
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder addClassLoader(ClassLoader classLoader)
/*     */   {
/* 215 */     return addClassLoaders(new ClassLoader[] { classLoader });
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder addClassLoaders(ClassLoader[] classLoaders)
/*     */   {
/* 220 */     this.classLoaders = (this.classLoaders == null ? classLoaders : (ClassLoader[])ObjectArrays.concat(this.classLoaders, classLoaders, ClassLoader.class));
/* 221 */     return this;
/*     */   }
/*     */ 
/*     */   public ConfigurationBuilder addClassLoaders(Collection<ClassLoader> classLoaders)
/*     */   {
/* 226 */     return addClassLoaders((ClassLoader[])classLoaders.toArray(new ClassLoader[classLoaders.size()]));
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.util.ConfigurationBuilder
 * JD-Core Version:    0.6.2
 */