package com.newrelic.objectweb.asm.commons;

import com.newrelic.objectweb.asm.Label;

public abstract interface TableSwitchGenerator
{
  public abstract void generateCase(int paramInt, Label paramLabel);

  public abstract void generateDefault();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.TableSwitchGenerator
 * JD-Core Version:    0.6.2
 */