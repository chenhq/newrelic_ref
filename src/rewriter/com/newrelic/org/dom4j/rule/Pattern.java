package com.newrelic.org.dom4j.rule;

import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.NodeFilter;

public abstract interface Pattern extends NodeFilter
{
  public static final short ANY_NODE = 0;
  public static final short NONE = 9999;
  public static final short NUMBER_OF_TYPES = 14;
  public static final double DEFAULT_PRIORITY = 0.5D;

  public abstract boolean matches(Node paramNode);

  public abstract double getPriority();

  public abstract Pattern[] getUnionPatterns();

  public abstract short getMatchType();

  public abstract String getMatchesNodeName();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.rule.Pattern
 * JD-Core Version:    0.6.2
 */