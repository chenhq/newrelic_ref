package com.newrelic.agent.compile;

import com.newrelic.objectweb.asm.MethodVisitor;

abstract interface MethodVisitorFactory
{
  public abstract MethodVisitor create(MethodVisitor paramMethodVisitor, int paramInt, String paramString1, String paramString2);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.MethodVisitorFactory
 * JD-Core Version:    0.6.2
 */