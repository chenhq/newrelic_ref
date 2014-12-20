/*     */ package com.newrelic.org.dom4j.util;
/*     */ 
/*     */ import com.newrelic.org.dom4j.Attribute;
/*     */ import com.newrelic.org.dom4j.CDATA;
/*     */ import com.newrelic.org.dom4j.Comment;
/*     */ import com.newrelic.org.dom4j.Document;
/*     */ import com.newrelic.org.dom4j.DocumentFactory;
/*     */ import com.newrelic.org.dom4j.DocumentType;
/*     */ import com.newrelic.org.dom4j.Element;
/*     */ import com.newrelic.org.dom4j.Entity;
/*     */ import com.newrelic.org.dom4j.Namespace;
/*     */ import com.newrelic.org.dom4j.NodeFilter;
/*     */ import com.newrelic.org.dom4j.ProcessingInstruction;
/*     */ import com.newrelic.org.dom4j.QName;
/*     */ import com.newrelic.org.dom4j.Text;
/*     */ import com.newrelic.org.dom4j.XPath;
/*     */ import com.newrelic.org.dom4j.rule.Pattern;
/*     */ import java.util.Map;
/*     */ import org.jaxen.VariableContext;
/*     */ 
/*     */ public abstract class ProxyDocumentFactory
/*     */ {
/*     */   private DocumentFactory proxy;
/*     */ 
/*     */   public ProxyDocumentFactory()
/*     */   {
/*  47 */     this.proxy = DocumentFactory.getInstance();
/*     */   }
/*     */ 
/*     */   public ProxyDocumentFactory(DocumentFactory proxy) {
/*  51 */     this.proxy = proxy;
/*     */   }
/*     */ 
/*     */   public Document createDocument()
/*     */   {
/*  57 */     return this.proxy.createDocument();
/*     */   }
/*     */ 
/*     */   public Document createDocument(Element rootElement) {
/*  61 */     return this.proxy.createDocument(rootElement);
/*     */   }
/*     */ 
/*     */   public DocumentType createDocType(String name, String publicId, String systemId)
/*     */   {
/*  66 */     return this.proxy.createDocType(name, publicId, systemId);
/*     */   }
/*     */ 
/*     */   public Element createElement(QName qname) {
/*  70 */     return this.proxy.createElement(qname);
/*     */   }
/*     */ 
/*     */   public Element createElement(String name) {
/*  74 */     return this.proxy.createElement(name);
/*     */   }
/*     */ 
/*     */   public Attribute createAttribute(Element owner, QName qname, String value) {
/*  78 */     return this.proxy.createAttribute(owner, qname, value);
/*     */   }
/*     */ 
/*     */   public Attribute createAttribute(Element owner, String name, String value) {
/*  82 */     return this.proxy.createAttribute(owner, name, value);
/*     */   }
/*     */ 
/*     */   public CDATA createCDATA(String text) {
/*  86 */     return this.proxy.createCDATA(text);
/*     */   }
/*     */ 
/*     */   public Comment createComment(String text) {
/*  90 */     return this.proxy.createComment(text);
/*     */   }
/*     */ 
/*     */   public Text createText(String text) {
/*  94 */     return this.proxy.createText(text);
/*     */   }
/*     */ 
/*     */   public Entity createEntity(String name, String text) {
/*  98 */     return this.proxy.createEntity(name, text);
/*     */   }
/*     */ 
/*     */   public Namespace createNamespace(String prefix, String uri) {
/* 102 */     return this.proxy.createNamespace(prefix, uri);
/*     */   }
/*     */ 
/*     */   public ProcessingInstruction createProcessingInstruction(String target, String data)
/*     */   {
/* 107 */     return this.proxy.createProcessingInstruction(target, data);
/*     */   }
/*     */ 
/*     */   public ProcessingInstruction createProcessingInstruction(String target, Map data)
/*     */   {
/* 112 */     return this.proxy.createProcessingInstruction(target, data);
/*     */   }
/*     */ 
/*     */   public QName createQName(String localName, Namespace namespace) {
/* 116 */     return this.proxy.createQName(localName, namespace);
/*     */   }
/*     */ 
/*     */   public QName createQName(String localName) {
/* 120 */     return this.proxy.createQName(localName);
/*     */   }
/*     */ 
/*     */   public QName createQName(String name, String prefix, String uri) {
/* 124 */     return this.proxy.createQName(name, prefix, uri);
/*     */   }
/*     */ 
/*     */   public QName createQName(String qualifiedName, String uri) {
/* 128 */     return this.proxy.createQName(qualifiedName, uri);
/*     */   }
/*     */ 
/*     */   public XPath createXPath(String xpathExpression) {
/* 132 */     return this.proxy.createXPath(xpathExpression);
/*     */   }
/*     */ 
/*     */   public XPath createXPath(String xpathExpression, VariableContext variableContext)
/*     */   {
/* 137 */     return this.proxy.createXPath(xpathExpression, variableContext);
/*     */   }
/*     */ 
/*     */   public NodeFilter createXPathFilter(String xpathFilterExpression, VariableContext variableContext)
/*     */   {
/* 142 */     return this.proxy.createXPathFilter(xpathFilterExpression, variableContext);
/*     */   }
/*     */ 
/*     */   public NodeFilter createXPathFilter(String xpathFilterExpression) {
/* 146 */     return this.proxy.createXPathFilter(xpathFilterExpression);
/*     */   }
/*     */ 
/*     */   public Pattern createPattern(String xpathPattern) {
/* 150 */     return this.proxy.createPattern(xpathPattern);
/*     */   }
/*     */ 
/*     */   protected DocumentFactory getProxy()
/*     */   {
/* 156 */     return this.proxy;
/*     */   }
/*     */ 
/*     */   protected void setProxy(DocumentFactory proxy) {
/* 160 */     if (proxy == null)
/*     */     {
/* 162 */       proxy = DocumentFactory.getInstance();
/*     */     }
/*     */ 
/* 165 */     this.proxy = proxy;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.util.ProxyDocumentFactory
 * JD-Core Version:    0.6.2
 */