package com.newrelic.org.dom4j.rule;

import com.newrelic.org.dom4j.Node;

public abstract interface Action
{
  public abstract void run(Node paramNode)
    throws Exception;
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.rule.Action
 * JD-Core Version:    0.6.2
 */