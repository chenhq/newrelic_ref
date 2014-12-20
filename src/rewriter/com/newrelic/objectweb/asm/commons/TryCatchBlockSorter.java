package com.newrelic.objectweb.asm.commons;

import com.newrelic.objectweb.asm.MethodVisitor;
import com.newrelic.objectweb.asm.tree.MethodNode;
import java.util.Collections;

public class TryCatchBlockSorter extends MethodNode
{
  private final MethodVisitor mv;

  public TryCatchBlockSorter(MethodVisitor paramMethodVisitor, int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    super(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    this.mv = paramMethodVisitor;
  }

  public void visitEnd()
  {
    TryCatchBlockSorter.1 local1 = new TryCatchBlockSorter.1(this);
    Collections.sort(this.tryCatchBlocks, local1);
    if (this.mv != null)
      accept(this.mv);
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.TryCatchBlockSorter
 * JD-Core Version:    0.6.2
 */