/*     */ package com.newrelic.org.reflections;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Predicate;
/*     */ import com.newrelic.com.google.common.collect.Lists;
/*     */ import com.newrelic.com.google.common.collect.Sets;
/*     */ import com.newrelic.org.reflections.scanners.Scanner;
/*     */ import com.newrelic.org.reflections.serializers.Serializer;
/*     */ import com.newrelic.org.reflections.serializers.XmlSerializer;
/*     */ import com.newrelic.org.reflections.util.ClasspathHelper;
/*     */ import com.newrelic.org.reflections.util.ConfigurationBuilder;
/*     */ import com.newrelic.org.reflections.util.FilterBuilder;
/*     */ import com.newrelic.org.reflections.util.Utils;
/*     */ import com.newrelic.org.reflections.vfs.Vfs;
/*     */ import com.newrelic.org.reflections.vfs.Vfs.Dir;
/*     */ import com.newrelic.org.reflections.vfs.Vfs.File;
/*     */ import com.newrelic.org.slf4j.Logger;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class Reflections extends ReflectionUtils
/*     */ {
/*     */ 
/*     */   @Nullable
/*  74 */   public static Logger log = Utils.findLogger(Reflections.class);
/*     */   protected final transient Configuration configuration;
/*     */   private Store store;
/*     */ 
/*     */   public Reflections(Configuration configuration)
/*     */   {
/*  84 */     this.configuration = configuration;
/*  85 */     this.store = new Store(configuration.getExecutorService() != null);
/*     */ 
/*  87 */     if ((configuration.getScanners() != null) && (!configuration.getScanners().isEmpty()))
/*     */     {
/*  89 */       for (Scanner scanner : configuration.getScanners()) {
/*  90 */         scanner.setConfiguration(configuration);
/*  91 */         scanner.setStore(this.store.getOrCreate(scanner.getClass().getSimpleName()));
/*     */       }
/*     */ 
/*  94 */       scan();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Reflections(String prefix, @Nullable Scanner[] scanners)
/*     */   {
/* 108 */     this(new Object[] { prefix, scanners });
/*     */   }
/*     */ 
/*     */   public Reflections(Object[] params)
/*     */   {
/* 135 */     this(ConfigurationBuilder.build(params));
/*     */   }
/*     */ 
/*     */   protected Reflections() {
/* 139 */     this.configuration = new ConfigurationBuilder();
/* 140 */     this.store = new Store(false);
/*     */   }
/*     */ 
/*     */   protected void scan()
/*     */   {
/* 145 */     if ((this.configuration.getUrls() == null) || (this.configuration.getUrls().isEmpty())) {
/* 146 */       if (log != null) log.error("given scan urls are empty. set urls in the configuration");
/* 147 */       return;
/*     */     }
/* 149 */     if ((log != null) && (log.isDebugEnabled())) {
/* 150 */       StringBuilder urls = new StringBuilder();
/* 151 */       for (URL url : this.configuration.getUrls()) {
/* 152 */         urls.append("\t").append(url.toExternalForm()).append("\n");
/*     */       }
/* 154 */       log.debug("going to scan these urls:\n" + urls);
/*     */     }
/*     */ 
/* 158 */     long time = System.currentTimeMillis();
/* 159 */     int scannedUrls = 0;
/* 160 */     ExecutorService executorService = this.configuration.getExecutorService();
/*     */ 
/* 162 */     if (executorService == null) {
/* 163 */       for (URL url : this.configuration.getUrls())
/*     */         try {
/* 165 */           for (Vfs.File file : Vfs.fromURL(url).getFiles()) {
/* 166 */             scan(file);
/*     */           }
/* 168 */           scannedUrls++;
/*     */         } catch (ReflectionsException e) {
/* 170 */           if (log != null) log.error("could not create Vfs.Dir from url. ignoring the exception and continuing", e);
/*     */         }
/*     */     }
/*     */     else
/*     */     {
/* 175 */       List futures = Lists.newArrayList();
/*     */       try {
/* 177 */         for (URL url : this.configuration.getUrls()) {
/*     */           try {
/* 179 */             for (final Vfs.File file : Vfs.fromURL(url).getFiles()) {
/* 180 */               futures.add(executorService.submit(new Runnable() {
/*     */                 public void run() {
/* 182 */                   Reflections.this.scan(file);
/*     */                 }
/*     */               }));
/*     */             }
/* 186 */             scannedUrls++;
/*     */           } catch (ReflectionsException e) {
/* 188 */             if (log != null) log.error("could not create Vfs.Dir from url. ignoring the exception and continuing", e);
/*     */           }
/*     */         }
/*     */ 
/* 192 */         for (Future future : futures) try {
/* 193 */             future.get(); } catch (Exception e) { throw new RuntimeException(e); }  
/*     */       }
/*     */       finally
/*     */       {
/* 196 */         executorService.shutdown();
/*     */       }
/*     */     }
/*     */ 
/* 200 */     time = System.currentTimeMillis() - time;
/*     */ 
/* 202 */     Integer keys = this.store.getKeysCount();
/* 203 */     Integer values = this.store.getValuesCount();
/*     */ 
/* 205 */     if (log != null) log.info(String.format("Reflections took %d ms to scan %d urls, producing %d keys and %d values %s", new Object[] { Long.valueOf(time), Integer.valueOf(scannedUrls), keys, values, (executorService != null) && ((executorService instanceof ThreadPoolExecutor)) ? String.format("[using %d cores]", new Object[] { Integer.valueOf(((ThreadPoolExecutor)executorService).getMaximumPoolSize()) }) : "" }));
/*     */   }
/*     */ 
/*     */   private void scan(Vfs.File file)
/*     */   {
/* 212 */     String input = file.getRelativePath().replace('/', '.');
/* 213 */     if (this.configuration.acceptsInput(input))
/* 214 */       for (Scanner scanner : this.configuration.getScanners())
/*     */         try {
/* 216 */           if (scanner.acceptsInput(input))
/* 217 */             scanner.scan(file);
/*     */         }
/*     */         catch (Exception e) {
/* 220 */           log.warn("could not scan file " + file.toString() + " with scanner " + scanner.getClass().getSimpleName(), e);
/*     */         }
/*     */   }
/*     */ 
/*     */   public static Reflections collect()
/*     */   {
/* 231 */     return collect("META-INF/reflections", new FilterBuilder().include(".*-reflections.xml"), new Serializer[0]);
/*     */   }
/*     */ 
/*     */   public static Reflections collect(String packagePrefix, Predicate<String> resourceNameFilter, @Nullable Serializer[] optionalSerializer)
/*     */   {
/* 243 */     Serializer serializer = (optionalSerializer != null) && (optionalSerializer.length == 1) ? optionalSerializer[0] : new XmlSerializer();
/* 244 */     Reflections reflections = new Reflections();
/*     */ 
/* 246 */     for (Vfs.File file : Vfs.findFiles(ClasspathHelper.forPackage(packagePrefix, new ClassLoader[0]), packagePrefix, resourceNameFilter)) {
/* 247 */       InputStream inputStream = null;
/*     */       try {
/* 249 */         inputStream = file.openInputStream();
/* 250 */         reflections.merge(serializer.read(inputStream));
/* 251 */         if (log != null) log.info("Reflections collected metadata from " + file + " using serializer " + serializer.getClass().getName()); 
/*     */       }
/* 253 */       catch (IOException e) { throw new ReflectionsException("could not merge " + file, e);
/*     */       } finally {
/* 255 */         Utils.close(inputStream);
/*     */       }
/*     */     }
/*     */ 
/* 259 */     return reflections;
/*     */   }
/*     */ 
/*     */   public Reflections collect(InputStream inputStream)
/*     */   {
/*     */     try
/*     */     {
/* 267 */       merge(this.configuration.getSerializer().read(inputStream));
/* 268 */       if (log != null) log.info("Reflections collected metadata from input stream using serializer " + this.configuration.getSerializer().getClass().getName()); 
/*     */     }
/* 270 */     catch (Exception ex) { throw new ReflectionsException("could not merge input stream", ex); }
/*     */ 
/*     */ 
/* 273 */     return this;
/*     */   }
/*     */ 
/*     */   public Reflections collect(File file)
/*     */   {
/* 281 */     FileInputStream inputStream = null;
/*     */     try {
/* 283 */       inputStream = new FileInputStream(file);
/* 284 */       return collect(inputStream);
/*     */     } catch (FileNotFoundException e) {
/* 286 */       throw new ReflectionsException("could not obtain input stream from file " + file, e);
/*     */     } finally {
/* 288 */       Utils.close(inputStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Reflections merge(Reflections reflections)
/*     */   {
/* 296 */     this.store.merge(reflections.store);
/* 297 */     return this;
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public <T extends Scanner> T get(Class<T> scannerClass)
/*     */   {
/* 303 */     for (Scanner scanner : this.configuration.getScanners()) {
/* 304 */       if (scanner.getClass().equals(scannerClass))
/*     */       {
/* 306 */         return scanner;
/*     */       }
/*     */     }
/* 309 */     return null;
/*     */   }
/*     */ 
/*     */   public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type)
/*     */   {
/* 317 */     Set subTypes = this.store.getSubTypesOf(type.getName());
/* 318 */     return toClasses(subTypes);
/*     */   }
/*     */ 
/*     */   public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation)
/*     */   {
/* 329 */     Set typesAnnotatedWith = this.store.getTypesAnnotatedWith(annotation.getName());
/* 330 */     return toClasses(typesAnnotatedWith);
/*     */   }
/*     */ 
/*     */   public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation, boolean honorInherited)
/*     */   {
/* 341 */     Set typesAnnotatedWith = this.store.getTypesAnnotatedWith(annotation.getName(), honorInherited);
/* 342 */     return toClasses(typesAnnotatedWith);
/*     */   }
/*     */ 
/*     */   public Set<Class<?>> getTypesAnnotatedWith(Annotation annotation)
/*     */   {
/* 351 */     return getTypesAnnotatedWith(annotation, true);
/*     */   }
/*     */ 
/*     */   public Set<Class<?>> getTypesAnnotatedWith(Annotation annotation, boolean honorInherited)
/*     */   {
/* 360 */     Set types = this.store.getTypesAnnotatedWithDirectly(annotation.annotationType().getName());
/* 361 */     Set annotated = getAll(toClasses(types), withAnnotation(annotation));
/* 362 */     Set inherited = this.store.getInheritedSubTypes(names(annotated), annotation.annotationType().getName(), honorInherited);
/* 363 */     return toClasses(inherited);
/*     */   }
/*     */ 
/*     */   public Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> annotation)
/*     */   {
/* 371 */     Set annotatedWith = this.store.getMethodsAnnotatedWith(annotation.getName());
/*     */ 
/* 373 */     Set result = Sets.newHashSet();
/* 374 */     for (String annotated : annotatedWith) {
/* 375 */       result.add(Utils.getMethodFromDescriptor(annotated, this.configuration.getClassLoaders()));
/*     */     }
/*     */ 
/* 378 */     return result;
/*     */   }
/*     */ 
/*     */   public Set<Method> getMethodsAnnotatedWith(Annotation annotation)
/*     */   {
/* 386 */     return getAll(getMethodsAnnotatedWith(annotation.annotationType()), withAnnotation(annotation));
/*     */   }
/*     */ 
/*     */   public Set<Field> getFieldsAnnotatedWith(Class<? extends Annotation> annotation)
/*     */   {
/* 394 */     Set result = Sets.newHashSet();
/*     */ 
/* 396 */     Collection annotatedWith = this.store.getFieldsAnnotatedWith(annotation.getName());
/* 397 */     for (String annotated : annotatedWith) {
/* 398 */       result.add(Utils.getFieldFromString(annotated, this.configuration.getClassLoaders()));
/*     */     }
/*     */ 
/* 401 */     return result;
/*     */   }
/*     */ 
/*     */   public Set<Field> getFieldsAnnotatedWith(Annotation annotation)
/*     */   {
/* 409 */     return getAll(getFieldsAnnotatedWith(annotation.annotationType()), withAnnotation(annotation));
/*     */   }
/*     */ 
/*     */   public Set<String> getResources(Predicate<String> namePredicate)
/*     */   {
/* 416 */     return this.store.getResources(namePredicate);
/*     */   }
/*     */ 
/*     */   public Set<String> getResources(final Pattern pattern)
/*     */   {
/* 424 */     return getResources(new Predicate() {
/*     */       public boolean apply(String input) {
/* 426 */         return pattern.matcher(input).matches();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private <T> Set<Class<? extends T>> toClasses(Set<String> names) {
/* 432 */     return Sets.newHashSet(ReflectionUtils.forNames(names, this.configuration.getClassLoaders()));
/*     */   }
/*     */ 
/*     */   public Store getStore()
/*     */   {
/* 437 */     return this.store;
/*     */   }
/*     */ 
/*     */   public File save(String filename)
/*     */   {
/* 448 */     return save(filename, this.configuration.getSerializer());
/*     */   }
/*     */ 
/*     */   public File save(String filename, Serializer serializer)
/*     */   {
/* 457 */     File file = serializer.save(this, filename);
/* 458 */     if (log != null)
/* 459 */       log.info("Reflections successfully saved in " + file.getAbsolutePath() + " using " + serializer.getClass().getSimpleName());
/* 460 */     return file;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.Reflections
 * JD-Core Version:    0.6.2
 */