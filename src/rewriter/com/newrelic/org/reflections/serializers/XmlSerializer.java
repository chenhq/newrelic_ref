/*     */ package com.newrelic.org.reflections.serializers;
/*     */ 
/*     */ import com.newrelic.com.google.common.collect.Multimap;
/*     */ import com.newrelic.org.dom4j.Document;
/*     */ import com.newrelic.org.dom4j.DocumentException;
/*     */ import com.newrelic.org.dom4j.DocumentFactory;
/*     */ import com.newrelic.org.dom4j.Element;
/*     */ import com.newrelic.org.dom4j.io.OutputFormat;
/*     */ import com.newrelic.org.dom4j.io.SAXReader;
/*     */ import com.newrelic.org.dom4j.io.XMLWriter;
/*     */ import com.newrelic.org.reflections.Reflections;
/*     */ import com.newrelic.org.reflections.ReflectionsException;
/*     */ import com.newrelic.org.reflections.Store;
/*     */ import com.newrelic.org.reflections.util.ConfigurationBuilder;
/*     */ import com.newrelic.org.reflections.util.Utils;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.StringWriter;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class XmlSerializer
/*     */   implements Serializer
/*     */ {
/*     */   public Reflections read(InputStream inputStream)
/*     */   {
/*  38 */     Reflections reflections = new Reflections(new ConfigurationBuilder());
/*     */     Document document;
/*     */     try
/*     */     {
/*  42 */       document = new SAXReader().read(inputStream);
/*     */     } catch (DocumentException e) {
/*  44 */       throw new RuntimeException(e);
/*     */     }
/*  46 */     for (Iterator i$ = document.getRootElement().elements().iterator(); i$.hasNext(); ) { Object e1 = i$.next();
/*  47 */       index = (Element)e1;
/*  48 */       for (i$ = index.elements().iterator(); i$.hasNext(); ) { Object e2 = i$.next();
/*  49 */         Element entry = (Element)e2;
/*  50 */         key = entry.element("key");
/*  51 */         Element values = entry.element("values");
/*  52 */         for (i$ = values.elements().iterator(); i$.hasNext(); ) { Object o3 = i$.next();
/*  53 */           Element value = (Element)o3;
/*  54 */           reflections.getStore().getOrCreate(index.getName()).put(key.getText(), value.getText());
/*     */         }
/*     */       }
/*     */     }
/*     */     Element index;
/*     */     Iterator i$;
/*     */     Element key;
/*     */     Iterator i$;
/*  59 */     return reflections;
/*     */   }
/*     */ 
/*     */   public File save(Reflections reflections, String filename) {
/*  63 */     File file = Utils.prepareFile(filename);
/*     */ 
/*  65 */     Document document = createDocument(reflections);
/*     */     try
/*     */     {
/*  68 */       XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file), OutputFormat.createPrettyPrint());
/*  69 */       xmlWriter.write(document);
/*  70 */       xmlWriter.close();
/*     */     } catch (IOException e) {
/*  72 */       throw new ReflectionsException("could not save to file " + filename, e);
/*     */     }
/*     */ 
/*  75 */     return file;
/*     */   }
/*     */ 
/*     */   public String toString(Reflections reflections) {
/*  79 */     Document document = createDocument(reflections);
/*     */     try
/*     */     {
/*  82 */       StringWriter writer = new StringWriter();
/*  83 */       XMLWriter xmlWriter = new XMLWriter(writer, OutputFormat.createPrettyPrint());
/*  84 */       xmlWriter.write(document);
/*  85 */       xmlWriter.close();
/*  86 */       return writer.toString(); } catch (IOException e) {
/*     */     }
/*  88 */     throw new RuntimeException();
/*     */   }
/*     */ 
/*     */   private Document createDocument(Reflections reflections)
/*     */   {
/*  93 */     Map map = reflections.getStore().getStoreMap();
/*     */ 
/*  95 */     Document document = DocumentFactory.getInstance().createDocument();
/*  96 */     Element root = document.addElement("Reflections");
/*  97 */     for (Iterator i$ = map.keySet().iterator(); i$.hasNext(); ) { indexName = (String)i$.next();
/*  98 */       indexElement = root.addElement(indexName);
/*  99 */       for (String key : ((Multimap)map.get(indexName)).keySet()) {
/* 100 */         Element entryElement = indexElement.addElement("entry");
/* 101 */         entryElement.addElement("key").setText(key);
/* 102 */         valuesElement = entryElement.addElement("values");
/* 103 */         for (String value : ((Multimap)map.get(indexName)).get(key))
/* 104 */           valuesElement.addElement("value").setText(value);
/*     */       }
/*     */     }
/*     */     String indexName;
/*     */     Element indexElement;
/*     */     Element valuesElement;
/* 108 */     return document;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.serializers.XmlSerializer
 * JD-Core Version:    0.6.2
 */