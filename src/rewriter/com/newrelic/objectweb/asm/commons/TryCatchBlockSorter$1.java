package com.newrelic.objectweb.asm.commons;

import com.newrelic.objectweb.asm.tree.InsnList;
import com.newrelic.objectweb.asm.tree.TryCatchBlockNode;
import java.util.Comparator;

class TryCatchBlockSorter$1
  implements Comparator
{
  private final TryCatchBlockSorter this$0;

  TryCatchBlockSorter$1(TryCatchBlockSorter paramTryCatchBlockSorter)
  {
    this.this$0 = paramTryCatchBlockSorter;
  }

  public int compare(Object paramObject1, Object paramObject2)
  {
    int i = blockLength((TryCatchBlockNode)paramObject1);
    int j = blockLength((TryCatchBlockNode)paramObject2);
    return i - j;
  }

  private int blockLength(TryCatchBlockNode paramTryCatchBlockNode)
  {
    int i = this.this$0.instructions.indexOf(paramTryCatchBlockNode.start);
    int j = this.this$0.instructions.indexOf(paramTryCatchBlockNode.end);
    return j - i;
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.TryCatchBlockSorter.1
 * JD-Core Version:    0.6.2
 */