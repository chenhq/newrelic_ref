/*    */ package com.newrelic.org.dom4j.io;
/*    */ 
/*    */ import javax.xml.parsers.DocumentBuilder;
/*    */ import javax.xml.parsers.DocumentBuilderFactory;
/*    */ import javax.xml.parsers.SAXParser;
/*    */ import javax.xml.parsers.SAXParserFactory;
/*    */ import org.w3c.dom.Document;
/*    */ import org.xml.sax.XMLReader;
/*    */ 
/*    */ class JAXPHelper
/*    */ {
/*    */   public static XMLReader createXMLReader(boolean validating, boolean namespaceAware)
/*    */     throws Exception
/*    */   {
/* 46 */     SAXParserFactory factory = SAXParserFactory.newInstance();
/* 47 */     factory.setValidating(validating);
/* 48 */     factory.setNamespaceAware(namespaceAware);
/*    */ 
/* 50 */     SAXParser parser = factory.newSAXParser();
/*    */ 
/* 52 */     return parser.getXMLReader();
/*    */   }
/*    */ 
/*    */   public static Document createDocument(boolean validating, boolean namespaceAware) throws Exception
/*    */   {
/* 57 */     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
/* 58 */     factory.setValidating(validating);
/* 59 */     factory.setNamespaceAware(namespaceAware);
/*    */ 
/* 61 */     DocumentBuilder builder = factory.newDocumentBuilder();
/*    */ 
/* 63 */     return builder.newDocument();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.io.JAXPHelper
 * JD-Core Version:    0.6.2
 */