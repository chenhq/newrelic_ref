package com.newrelic.objectweb.asm;

final class FieldWriter
  implements FieldVisitor
{
  FieldWriter a;
  private final ClassWriter b;
  private final int c;
  private final int d;
  private final int e;
  private int f;
  private int g;
  private AnnotationWriter h;
  private AnnotationWriter i;
  private Attribute j;

  FieldWriter(ClassWriter paramClassWriter, int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    if (paramClassWriter.y == null)
      paramClassWriter.y = this;
    else
      paramClassWriter.z.a = this;
    paramClassWriter.z = this;
    this.b = paramClassWriter;
    this.c = paramInt;
    this.d = paramClassWriter.newUTF8(paramString1);
    this.e = paramClassWriter.newUTF8(paramString2);
    if (paramString3 != null)
      this.f = paramClassWriter.newUTF8(paramString3);
    if (paramObject != null)
      this.g = paramClassWriter.a(paramObject).a;
  }

  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    localByteVector.putShort(this.b.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(this.b, true, localByteVector, localByteVector, 2);
    if (paramBoolean)
    {
      localAnnotationWriter.g = this.h;
      this.h = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.g = this.i;
      this.i = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }

  public void visitAttribute(Attribute paramAttribute)
  {
    paramAttribute.a = this.j;
    this.j = paramAttribute;
  }

  public void visitEnd()
  {
  }

  int a()
  {
    int k = 8;
    if (this.g != 0)
    {
      this.b.newUTF8("ConstantValue");
      k += 8;
    }
    if (((this.c & 0x1000) != 0) && (((this.b.b & 0xFFFF) < 49) || ((this.c & 0x40000) != 0)))
    {
      this.b.newUTF8("Synthetic");
      k += 6;
    }
    if ((this.c & 0x20000) != 0)
    {
      this.b.newUTF8("Deprecated");
      k += 6;
    }
    if (this.f != 0)
    {
      this.b.newUTF8("Signature");
      k += 8;
    }
    if (this.h != null)
    {
      this.b.newUTF8("RuntimeVisibleAnnotations");
      k += 8 + this.h.a();
    }
    if (this.i != null)
    {
      this.b.newUTF8("RuntimeInvisibleAnnotations");
      k += 8 + this.i.a();
    }
    if (this.j != null)
      k += this.j.a(this.b, null, 0, -1, -1);
    return k;
  }

  void a(ByteVector paramByteVector)
  {
    int k = 0x60000 | (this.c & 0x40000) / 64;
    paramByteVector.putShort(this.c & (k ^ 0xFFFFFFFF)).putShort(this.d).putShort(this.e);
    int m = 0;
    if (this.g != 0)
      m++;
    if (((this.c & 0x1000) != 0) && (((this.b.b & 0xFFFF) < 49) || ((this.c & 0x40000) != 0)))
      m++;
    if ((this.c & 0x20000) != 0)
      m++;
    if (this.f != 0)
      m++;
    if (this.h != null)
      m++;
    if (this.i != null)
      m++;
    if (this.j != null)
      m += this.j.a();
    paramByteVector.putShort(m);
    if (this.g != 0)
    {
      paramByteVector.putShort(this.b.newUTF8("ConstantValue"));
      paramByteVector.putInt(2).putShort(this.g);
    }
    if (((this.c & 0x1000) != 0) && (((this.b.b & 0xFFFF) < 49) || ((this.c & 0x40000) != 0)))
      paramByteVector.putShort(this.b.newUTF8("Synthetic")).putInt(0);
    if ((this.c & 0x20000) != 0)
      paramByteVector.putShort(this.b.newUTF8("Deprecated")).putInt(0);
    if (this.f != 0)
    {
      paramByteVector.putShort(this.b.newUTF8("Signature"));
      paramByteVector.putInt(2).putShort(this.f);
    }
    if (this.h != null)
    {
      paramByteVector.putShort(this.b.newUTF8("RuntimeVisibleAnnotations"));
      this.h.a(paramByteVector);
    }
    if (this.i != null)
    {
      paramByteVector.putShort(this.b.newUTF8("RuntimeInvisibleAnnotations"));
      this.i.a(paramByteVector);
    }
    if (this.j != null)
      this.j.a(this.b, null, 0, -1, -1, paramByteVector);
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.FieldWriter
 * JD-Core Version:    0.6.2
 */