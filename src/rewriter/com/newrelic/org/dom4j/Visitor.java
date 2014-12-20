package com.newrelic.org.dom4j;

public abstract interface Visitor
{
  public abstract void visit(Document paramDocument);

  public abstract void visit(DocumentType paramDocumentType);

  public abstract void visit(Element paramElement);

  public abstract void visit(Attribute paramAttribute);

  public abstract void visit(CDATA paramCDATA);

  public abstract void visit(Comment paramComment);

  public abstract void visit(Entity paramEntity);

  public abstract void visit(Namespace paramNamespace);

  public abstract void visit(ProcessingInstruction paramProcessingInstruction);

  public abstract void visit(Text paramText);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.Visitor
 * JD-Core Version:    0.6.2
 */