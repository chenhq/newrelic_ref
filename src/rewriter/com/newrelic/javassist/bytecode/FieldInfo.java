/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public final class FieldInfo
/*     */ {
/*     */   ConstPool constPool;
/*     */   int accessFlags;
/*     */   int name;
/*     */   String cachedName;
/*     */   String cachedType;
/*     */   int descriptor;
/*     */   ArrayList attribute;
/*     */ 
/*     */   private FieldInfo(ConstPool cp)
/*     */   {
/*  39 */     this.constPool = cp;
/*  40 */     this.accessFlags = 0;
/*  41 */     this.attribute = null;
/*     */   }
/*     */ 
/*     */   public FieldInfo(ConstPool cp, String fieldName, String desc)
/*     */   {
/*  54 */     this(cp);
/*  55 */     this.name = cp.addUtf8Info(fieldName);
/*  56 */     this.cachedName = fieldName;
/*  57 */     this.descriptor = cp.addUtf8Info(desc);
/*     */   }
/*     */ 
/*     */   FieldInfo(ConstPool cp, DataInputStream in) throws IOException {
/*  61 */     this(cp);
/*  62 */     read(in);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  69 */     return getName() + " " + getDescriptor();
/*     */   }
/*     */ 
/*     */   void compact(ConstPool cp)
/*     */   {
/*  81 */     this.name = cp.addUtf8Info(getName());
/*  82 */     this.descriptor = cp.addUtf8Info(getDescriptor());
/*  83 */     this.attribute = AttributeInfo.copyAll(this.attribute, cp);
/*  84 */     this.constPool = cp;
/*     */   }
/*     */ 
/*     */   void prune(ConstPool cp) {
/*  88 */     ArrayList newAttributes = new ArrayList();
/*  89 */     AttributeInfo invisibleAnnotations = getAttribute("RuntimeInvisibleAnnotations");
/*     */ 
/*  91 */     if (invisibleAnnotations != null) {
/*  92 */       invisibleAnnotations = invisibleAnnotations.copy(cp, null);
/*  93 */       newAttributes.add(invisibleAnnotations);
/*     */     }
/*     */ 
/*  96 */     AttributeInfo visibleAnnotations = getAttribute("RuntimeVisibleAnnotations");
/*     */ 
/*  98 */     if (visibleAnnotations != null) {
/*  99 */       visibleAnnotations = visibleAnnotations.copy(cp, null);
/* 100 */       newAttributes.add(visibleAnnotations);
/*     */     }
/*     */ 
/* 103 */     AttributeInfo signature = getAttribute("Signature");
/*     */ 
/* 105 */     if (signature != null) {
/* 106 */       signature = signature.copy(cp, null);
/* 107 */       newAttributes.add(signature);
/*     */     }
/*     */ 
/* 110 */     int index = getConstantValue();
/* 111 */     if (index != 0) {
/* 112 */       index = this.constPool.copy(index, cp, null);
/* 113 */       newAttributes.add(new ConstantAttribute(cp, index));
/*     */     }
/*     */ 
/* 116 */     this.attribute = newAttributes;
/* 117 */     this.name = cp.addUtf8Info(getName());
/* 118 */     this.descriptor = cp.addUtf8Info(getDescriptor());
/* 119 */     this.constPool = cp;
/*     */   }
/*     */ 
/*     */   public ConstPool getConstPool()
/*     */   {
/* 127 */     return this.constPool;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 134 */     if (this.cachedName == null) {
/* 135 */       this.cachedName = this.constPool.getUtf8Info(this.name);
/*     */     }
/* 137 */     return this.cachedName;
/*     */   }
/*     */ 
/*     */   public void setName(String newName)
/*     */   {
/* 144 */     this.name = this.constPool.addUtf8Info(newName);
/* 145 */     this.cachedName = newName;
/*     */   }
/*     */ 
/*     */   public int getAccessFlags()
/*     */   {
/* 154 */     return this.accessFlags;
/*     */   }
/*     */ 
/*     */   public void setAccessFlags(int acc)
/*     */   {
/* 163 */     this.accessFlags = acc;
/*     */   }
/*     */ 
/*     */   public String getDescriptor()
/*     */   {
/* 172 */     return this.constPool.getUtf8Info(this.descriptor);
/*     */   }
/*     */ 
/*     */   public void setDescriptor(String desc)
/*     */   {
/* 181 */     if (!desc.equals(getDescriptor()))
/* 182 */       this.descriptor = this.constPool.addUtf8Info(desc);
/*     */   }
/*     */ 
/*     */   public int getConstantValue()
/*     */   {
/* 192 */     if ((this.accessFlags & 0x8) == 0) {
/* 193 */       return 0;
/*     */     }
/* 195 */     ConstantAttribute attr = (ConstantAttribute)getAttribute("ConstantValue");
/*     */ 
/* 197 */     if (attr == null) {
/* 198 */       return 0;
/*     */     }
/* 200 */     return attr.getConstantValue();
/*     */   }
/*     */ 
/*     */   public List getAttributes()
/*     */   {
/* 214 */     if (this.attribute == null) {
/* 215 */       this.attribute = new ArrayList();
/*     */     }
/* 217 */     return this.attribute;
/*     */   }
/*     */ 
/*     */   public AttributeInfo getAttribute(String name)
/*     */   {
/* 228 */     return AttributeInfo.lookup(this.attribute, name);
/*     */   }
/*     */ 
/*     */   public void addAttribute(AttributeInfo info)
/*     */   {
/* 238 */     if (this.attribute == null) {
/* 239 */       this.attribute = new ArrayList();
/*     */     }
/* 241 */     AttributeInfo.remove(this.attribute, info.getName());
/* 242 */     this.attribute.add(info);
/*     */   }
/*     */ 
/*     */   private void read(DataInputStream in) throws IOException {
/* 246 */     this.accessFlags = in.readUnsignedShort();
/* 247 */     this.name = in.readUnsignedShort();
/* 248 */     this.descriptor = in.readUnsignedShort();
/* 249 */     int n = in.readUnsignedShort();
/* 250 */     this.attribute = new ArrayList();
/* 251 */     for (int i = 0; i < n; i++)
/* 252 */       this.attribute.add(AttributeInfo.read(this.constPool, in));
/*     */   }
/*     */ 
/*     */   void write(DataOutputStream out) throws IOException {
/* 256 */     out.writeShort(this.accessFlags);
/* 257 */     out.writeShort(this.name);
/* 258 */     out.writeShort(this.descriptor);
/* 259 */     if (this.attribute == null) {
/* 260 */       out.writeShort(0);
/*     */     } else {
/* 262 */       out.writeShort(this.attribute.size());
/* 263 */       AttributeInfo.writeAll(this.attribute, out);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.FieldInfo
 * JD-Core Version:    0.6.2
 */