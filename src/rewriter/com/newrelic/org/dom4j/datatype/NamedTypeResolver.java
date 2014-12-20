/*    */ package com.newrelic.org.dom4j.datatype;
/*    */ 
/*    */ import com.newrelic.org.dom4j.DocumentFactory;
/*    */ import com.newrelic.org.dom4j.Element;
/*    */ import com.newrelic.org.dom4j.QName;
/*    */ import com.sun.msv.datatype.xsd.XSDatatype;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ class NamedTypeResolver
/*    */ {
/* 29 */   protected Map complexTypeMap = new HashMap();
/*    */ 
/* 31 */   protected Map simpleTypeMap = new HashMap();
/*    */ 
/* 33 */   protected Map typedElementMap = new HashMap();
/*    */ 
/* 35 */   protected Map elementFactoryMap = new HashMap();
/*    */   protected DocumentFactory documentFactory;
/*    */ 
/*    */   NamedTypeResolver(DocumentFactory documentFactory)
/*    */   {
/* 40 */     this.documentFactory = documentFactory;
/*    */   }
/*    */ 
/*    */   void registerComplexType(QName type, DocumentFactory factory) {
/* 44 */     this.complexTypeMap.put(type, factory);
/*    */   }
/*    */ 
/*    */   void registerSimpleType(QName type, XSDatatype datatype) {
/* 48 */     this.simpleTypeMap.put(type, datatype);
/*    */   }
/*    */ 
/*    */   void registerTypedElement(Element element, QName type, DocumentFactory parentFactory)
/*    */   {
/* 53 */     this.typedElementMap.put(element, type);
/* 54 */     this.elementFactoryMap.put(element, parentFactory);
/*    */   }
/*    */ 
/*    */   void resolveElementTypes() {
/* 58 */     Iterator iterator = this.typedElementMap.keySet().iterator();
/*    */ 
/* 60 */     while (iterator.hasNext()) {
/* 61 */       Element element = (Element)iterator.next();
/* 62 */       QName elementQName = getQNameOfSchemaElement(element);
/* 63 */       QName type = (QName)this.typedElementMap.get(element);
/*    */ 
/* 65 */       if (this.complexTypeMap.containsKey(type)) {
/* 66 */         DocumentFactory factory = (DocumentFactory)this.complexTypeMap.get(type);
/*    */ 
/* 68 */         elementQName.setDocumentFactory(factory);
/* 69 */       } else if (this.simpleTypeMap.containsKey(type)) {
/* 70 */         XSDatatype datatype = (XSDatatype)this.simpleTypeMap.get(type);
/* 71 */         DocumentFactory factory = (DocumentFactory)this.elementFactoryMap.get(element);
/*    */ 
/* 74 */         if ((factory instanceof DatatypeElementFactory))
/* 75 */           ((DatatypeElementFactory)factory).setChildElementXSDatatype(elementQName, datatype);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   void resolveNamedTypes()
/*    */   {
/* 83 */     resolveElementTypes();
/*    */   }
/*    */ 
/*    */   private QName getQNameOfSchemaElement(Element element) {
/* 87 */     String name = element.attributeValue("name");
/*    */ 
/* 89 */     return getQName(name);
/*    */   }
/*    */ 
/*    */   private QName getQName(String name) {
/* 93 */     return this.documentFactory.createQName(name);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.datatype.NamedTypeResolver
 * JD-Core Version:    0.6.2
 */