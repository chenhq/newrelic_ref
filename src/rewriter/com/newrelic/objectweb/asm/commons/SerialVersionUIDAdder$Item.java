package com.newrelic.objectweb.asm.commons;

class SerialVersionUIDAdder$Item
  implements Comparable
{
  final String name;
  final int access;
  final String desc;

  SerialVersionUIDAdder$Item(String paramString1, int paramInt, String paramString2)
  {
    this.name = paramString1;
    this.access = paramInt;
    this.desc = paramString2;
  }

  public int compareTo(Object paramObject)
  {
    Item localItem = (Item)paramObject;
    int i = this.name.compareTo(localItem.name);
    if (i == 0)
      i = this.desc.compareTo(localItem.desc);
    return i;
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.SerialVersionUIDAdder.Item
 * JD-Core Version:    0.6.2
 */