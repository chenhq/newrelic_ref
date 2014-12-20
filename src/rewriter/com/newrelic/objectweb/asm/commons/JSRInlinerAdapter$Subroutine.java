package com.newrelic.objectweb.asm.commons;

import java.util.BitSet;

public class JSRInlinerAdapter$Subroutine
{
  public final BitSet instructions = new BitSet();

  public void addInstruction(int paramInt)
  {
    this.instructions.set(paramInt);
  }

  public boolean ownsInstruction(int paramInt)
  {
    return this.instructions.get(paramInt);
  }

  public String toString()
  {
    return "Subroutine: " + this.instructions;
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.JSRInlinerAdapter.Subroutine
 * JD-Core Version:    0.6.2
 */