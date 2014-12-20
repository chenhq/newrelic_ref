package com.newrelic.objectweb.asm.commons;

import com.newrelic.objectweb.asm.Label;
import com.newrelic.objectweb.asm.MethodVisitor;
import com.newrelic.objectweb.asm.Opcodes;
import com.newrelic.objectweb.asm.tree.AbstractInsnNode;
import com.newrelic.objectweb.asm.tree.InsnList;
import com.newrelic.objectweb.asm.tree.InsnNode;
import com.newrelic.objectweb.asm.tree.JumpInsnNode;
import com.newrelic.objectweb.asm.tree.LabelNode;
import com.newrelic.objectweb.asm.tree.LocalVariableNode;
import com.newrelic.objectweb.asm.tree.LookupSwitchInsnNode;
import com.newrelic.objectweb.asm.tree.MethodNode;
import com.newrelic.objectweb.asm.tree.TableSwitchInsnNode;
import com.newrelic.objectweb.asm.tree.TryCatchBlockNode;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JSRInlinerAdapter extends MethodNode
  implements Opcodes
{
  private final MethodVisitor mv;
  private final Map subroutineHeads = new HashMap();
  private final JSRInlinerAdapter.Subroutine mainSubroutine = new JSRInlinerAdapter.Subroutine();
  final BitSet dualCitizens = new BitSet();

  public JSRInlinerAdapter(MethodVisitor paramMethodVisitor, int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    super(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    this.mv = paramMethodVisitor;
  }

  public void visitJumpInsn(int paramInt, Label paramLabel)
  {
    super.visitJumpInsn(paramInt, paramLabel);
    LabelNode localLabelNode = ((JumpInsnNode)this.instructions.getLast()).label;
    if ((paramInt == 168) && (!this.subroutineHeads.containsKey(localLabelNode)))
      this.subroutineHeads.put(localLabelNode, new JSRInlinerAdapter.Subroutine());
  }

  public void visitEnd()
  {
    if (!this.subroutineHeads.isEmpty())
    {
      markSubroutines();
      emitCode();
    }
    if (this.mv != null)
      accept(this.mv);
  }

  private void markSubroutines()
  {
    BitSet localBitSet = new BitSet();
    markSubroutineWalk(this.mainSubroutine, 0, localBitSet);
    Iterator localIterator = this.subroutineHeads.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      LabelNode localLabelNode = (LabelNode)localEntry.getKey();
      JSRInlinerAdapter.Subroutine localSubroutine = (JSRInlinerAdapter.Subroutine)localEntry.getValue();
      int i = this.instructions.indexOf(localLabelNode);
      markSubroutineWalk(localSubroutine, i, localBitSet);
    }
  }

  private void markSubroutineWalk(JSRInlinerAdapter.Subroutine paramSubroutine, int paramInt, BitSet paramBitSet)
  {
    markSubroutineWalkDFS(paramSubroutine, paramInt, paramBitSet);
    int i = 1;
    while (i != 0)
    {
      i = 0;
      Iterator localIterator = this.tryCatchBlocks.iterator();
      while (localIterator.hasNext())
      {
        TryCatchBlockNode localTryCatchBlockNode = (TryCatchBlockNode)localIterator.next();
        int j = this.instructions.indexOf(localTryCatchBlockNode.handler);
        if (!paramSubroutine.instructions.get(j))
        {
          int k = this.instructions.indexOf(localTryCatchBlockNode.start);
          int m = this.instructions.indexOf(localTryCatchBlockNode.end);
          int n = paramSubroutine.instructions.nextSetBit(k);
          if ((n != -1) && (n < m))
          {
            markSubroutineWalkDFS(paramSubroutine, j, paramBitSet);
            i = 1;
          }
        }
      }
    }
  }

  private void markSubroutineWalkDFS(JSRInlinerAdapter.Subroutine paramSubroutine, int paramInt, BitSet paramBitSet)
  {
    while (true)
    {
      AbstractInsnNode localAbstractInsnNode = this.instructions.get(paramInt);
      if (paramSubroutine.instructions.get(paramInt))
        return;
      paramSubroutine.instructions.set(paramInt);
      if (paramBitSet.get(paramInt))
        this.dualCitizens.set(paramInt);
      paramBitSet.set(paramInt);
      Object localObject;
      int i;
      if ((localAbstractInsnNode.getType() == 6) && (localAbstractInsnNode.getOpcode() != 168))
      {
        localObject = (JumpInsnNode)localAbstractInsnNode;
        i = this.instructions.indexOf(((JumpInsnNode)localObject).label);
        markSubroutineWalkDFS(paramSubroutine, i, paramBitSet);
      }
      int j;
      LabelNode localLabelNode;
      if (localAbstractInsnNode.getType() == 10)
      {
        localObject = (TableSwitchInsnNode)localAbstractInsnNode;
        i = this.instructions.indexOf(((TableSwitchInsnNode)localObject).dflt);
        markSubroutineWalkDFS(paramSubroutine, i, paramBitSet);
        for (j = ((TableSwitchInsnNode)localObject).labels.size() - 1; j >= 0; j--)
        {
          localLabelNode = (LabelNode)((TableSwitchInsnNode)localObject).labels.get(j);
          i = this.instructions.indexOf(localLabelNode);
          markSubroutineWalkDFS(paramSubroutine, i, paramBitSet);
        }
      }
      if (localAbstractInsnNode.getType() == 11)
      {
        localObject = (LookupSwitchInsnNode)localAbstractInsnNode;
        i = this.instructions.indexOf(((LookupSwitchInsnNode)localObject).dflt);
        markSubroutineWalkDFS(paramSubroutine, i, paramBitSet);
        for (j = ((LookupSwitchInsnNode)localObject).labels.size() - 1; j >= 0; j--)
        {
          localLabelNode = (LabelNode)((LookupSwitchInsnNode)localObject).labels.get(j);
          i = this.instructions.indexOf(localLabelNode);
          markSubroutineWalkDFS(paramSubroutine, i, paramBitSet);
        }
      }
      switch (this.instructions.get(paramInt).getOpcode())
      {
      case 167:
      case 169:
      case 170:
      case 171:
      case 172:
      case 173:
      case 174:
      case 175:
      case 176:
      case 177:
      case 191:
        return;
      case 168:
      case 178:
      case 179:
      case 180:
      case 181:
      case 182:
      case 183:
      case 184:
      case 185:
      case 186:
      case 187:
      case 188:
      case 189:
      case 190:
      }
      paramInt++;
    }
  }

  private void emitCode()
  {
    LinkedList localLinkedList = new LinkedList();
    localLinkedList.add(new JSRInlinerAdapter.Instantiation(this, null, this.mainSubroutine));
    InsnList localInsnList = new InsnList();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    while (!localLinkedList.isEmpty())
    {
      JSRInlinerAdapter.Instantiation localInstantiation = (JSRInlinerAdapter.Instantiation)localLinkedList.removeFirst();
      emitSubroutine(localInstantiation, localLinkedList, localInsnList, localArrayList1, localArrayList2);
    }
    this.instructions = localInsnList;
    this.tryCatchBlocks = localArrayList1;
    this.localVariables = localArrayList2;
  }

  private void emitSubroutine(JSRInlinerAdapter.Instantiation paramInstantiation, List paramList1, InsnList paramInsnList, List paramList2, List paramList3)
  {
    Object localObject1 = null;
    int i = 0;
    int j = this.instructions.size();
    Object localObject3;
    Object localObject4;
    LabelNode localLabelNode1;
    while (i < j)
    {
      localObject3 = this.instructions.get(i);
      localObject4 = paramInstantiation.findOwner(i);
      Object localObject5;
      if (((AbstractInsnNode)localObject3).getType() == 7)
      {
        localLabelNode1 = (LabelNode)localObject3;
        localObject5 = paramInstantiation.rangeLabel(localLabelNode1);
        if (localObject5 != localObject1)
        {
          paramInsnList.add((AbstractInsnNode)localObject5);
          localObject1 = localObject5;
        }
      }
      else if (localObject4 == paramInstantiation)
      {
        if (((AbstractInsnNode)localObject3).getOpcode() == 169)
        {
          localLabelNode1 = null;
          for (localObject5 = paramInstantiation; localObject5 != null; localObject5 = ((JSRInlinerAdapter.Instantiation)localObject5).previous)
            if (((JSRInlinerAdapter.Instantiation)localObject5).subroutine.ownsInstruction(i))
              localLabelNode1 = ((JSRInlinerAdapter.Instantiation)localObject5).returnLabel;
          if (localLabelNode1 == null)
            throw new RuntimeException("Instruction #" + i + " is a RET not owned by any subroutine");
          paramInsnList.add(new JumpInsnNode(167, localLabelNode1));
        }
        else if (((AbstractInsnNode)localObject3).getOpcode() == 168)
        {
          localLabelNode1 = ((JumpInsnNode)localObject3).label;
          localObject5 = (JSRInlinerAdapter.Subroutine)this.subroutineHeads.get(localLabelNode1);
          JSRInlinerAdapter.Instantiation localInstantiation = new JSRInlinerAdapter.Instantiation(this, paramInstantiation, (JSRInlinerAdapter.Subroutine)localObject5);
          LabelNode localLabelNode2 = localInstantiation.gotoLabel(localLabelNode1);
          paramInsnList.add(new InsnNode(1));
          paramInsnList.add(new JumpInsnNode(167, localLabelNode2));
          paramInsnList.add(localInstantiation.returnLabel);
          paramList1.add(localInstantiation);
        }
        else
        {
          paramInsnList.add(((AbstractInsnNode)localObject3).clone(paramInstantiation));
        }
      }
      i++;
    }
    Iterator localIterator = this.tryCatchBlocks.iterator();
    Object localObject2;
    while (localIterator.hasNext())
    {
      localObject2 = (TryCatchBlockNode)localIterator.next();
      localObject3 = paramInstantiation.rangeLabel(((TryCatchBlockNode)localObject2).start);
      localObject4 = paramInstantiation.rangeLabel(((TryCatchBlockNode)localObject2).end);
      if (localObject3 != localObject4)
      {
        localLabelNode1 = paramInstantiation.gotoLabel(((TryCatchBlockNode)localObject2).handler);
        if ((localObject3 == null) || (localObject4 == null) || (localLabelNode1 == null))
          throw new RuntimeException("Internal error!");
        paramList2.add(new TryCatchBlockNode((LabelNode)localObject3, (LabelNode)localObject4, localLabelNode1, ((TryCatchBlockNode)localObject2).type));
      }
    }
    localIterator = this.localVariables.iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (LocalVariableNode)localIterator.next();
      localObject3 = paramInstantiation.rangeLabel(((LocalVariableNode)localObject2).start);
      localObject4 = paramInstantiation.rangeLabel(((LocalVariableNode)localObject2).end);
      if (localObject3 != localObject4)
        paramList3.add(new LocalVariableNode(((LocalVariableNode)localObject2).name, ((LocalVariableNode)localObject2).desc, ((LocalVariableNode)localObject2).signature, (LabelNode)localObject3, (LabelNode)localObject4, ((LocalVariableNode)localObject2).index));
    }
  }

  private static void log(String paramString)
  {
    System.err.println(paramString);
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.JSRInlinerAdapter
 * JD-Core Version:    0.6.2
 */