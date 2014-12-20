package com.newrelic.objectweb.asm.commons;

import com.newrelic.objectweb.asm.tree.AbstractInsnNode;
import com.newrelic.objectweb.asm.tree.InsnList;
import com.newrelic.objectweb.asm.tree.LabelNode;
import java.util.AbstractMap;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class JSRInlinerAdapter$Instantiation extends AbstractMap
{
  final Instantiation previous;
  public final JSRInlinerAdapter.Subroutine subroutine;
  public final Map rangeTable;
  public final LabelNode returnLabel;
  private final JSRInlinerAdapter this$0;

  JSRInlinerAdapter$Instantiation(JSRInlinerAdapter paramJSRInlinerAdapter, Instantiation paramInstantiation, JSRInlinerAdapter.Subroutine paramSubroutine)
  {
    this.this$0 = paramJSRInlinerAdapter;
    this.rangeTable = new HashMap();
    this.previous = paramInstantiation;
    this.subroutine = paramSubroutine;
    for (Object localObject = paramInstantiation; localObject != null; localObject = ((Instantiation)localObject).previous)
      if (((Instantiation)localObject).subroutine == paramSubroutine)
        throw new RuntimeException("Recursive invocation of " + paramSubroutine);
    if (paramInstantiation != null)
      this.returnLabel = new LabelNode();
    else
      this.returnLabel = null;
    localObject = null;
    int i = 0;
    int j = paramJSRInlinerAdapter.instructions.size();
    while (i < j)
    {
      AbstractInsnNode localAbstractInsnNode = paramJSRInlinerAdapter.instructions.get(i);
      if (localAbstractInsnNode.getType() == 7)
      {
        LabelNode localLabelNode = (LabelNode)localAbstractInsnNode;
        if (localObject == null)
          localObject = new LabelNode();
        this.rangeTable.put(localLabelNode, localObject);
      }
      else if (findOwner(i) == this)
      {
        localObject = null;
      }
      i++;
    }
  }

  public Instantiation findOwner(int paramInt)
  {
    if (!this.subroutine.ownsInstruction(paramInt))
      return null;
    if (!this.this$0.dualCitizens.get(paramInt))
      return this;
    Object localObject = this;
    for (Instantiation localInstantiation = this.previous; localInstantiation != null; localInstantiation = localInstantiation.previous)
      if (localInstantiation.subroutine.ownsInstruction(paramInt))
        localObject = localInstantiation;
    return localObject;
  }

  public LabelNode gotoLabel(LabelNode paramLabelNode)
  {
    Instantiation localInstantiation = findOwner(this.this$0.instructions.indexOf(paramLabelNode));
    return (LabelNode)localInstantiation.rangeTable.get(paramLabelNode);
  }

  public LabelNode rangeLabel(LabelNode paramLabelNode)
  {
    return (LabelNode)this.rangeTable.get(paramLabelNode);
  }

  public Set entrySet()
  {
    return null;
  }

  public Object get(Object paramObject)
  {
    return gotoLabel((LabelNode)paramObject);
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.JSRInlinerAdapter.Instantiation
 * JD-Core Version:    0.6.2
 */