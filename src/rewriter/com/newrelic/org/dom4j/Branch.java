package com.newrelic.org.dom4j;

import java.util.Iterator;
import java.util.List;

public abstract interface Branch extends Node
{
  public abstract Node node(int paramInt)
    throws IndexOutOfBoundsException;

  public abstract int indexOf(Node paramNode);

  public abstract int nodeCount();

  public abstract Element elementByID(String paramString);

  public abstract List content();

  public abstract Iterator nodeIterator();

  public abstract void setContent(List paramList);

  public abstract void appendContent(Branch paramBranch);

  public abstract void clearContent();

  public abstract List processingInstructions();

  public abstract List processingInstructions(String paramString);

  public abstract ProcessingInstruction processingInstruction(String paramString);

  public abstract void setProcessingInstructions(List paramList);

  public abstract Element addElement(String paramString);

  public abstract Element addElement(QName paramQName);

  public abstract Element addElement(String paramString1, String paramString2);

  public abstract boolean removeProcessingInstruction(String paramString);

  public abstract void add(Node paramNode);

  public abstract void add(Comment paramComment);

  public abstract void add(Element paramElement);

  public abstract void add(ProcessingInstruction paramProcessingInstruction);

  public abstract boolean remove(Node paramNode);

  public abstract boolean remove(Comment paramComment);

  public abstract boolean remove(Element paramElement);

  public abstract boolean remove(ProcessingInstruction paramProcessingInstruction);

  public abstract void normalize();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.Branch
 * JD-Core Version:    0.6.2
 */