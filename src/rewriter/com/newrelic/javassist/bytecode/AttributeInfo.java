/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class AttributeInfo
/*     */ {
/*     */   protected ConstPool constPool;
/*     */   int name;
/*     */   byte[] info;
/*     */ 
/*     */   protected AttributeInfo(ConstPool cp, int attrname, byte[] attrinfo)
/*     */   {
/*  39 */     this.constPool = cp;
/*  40 */     this.name = attrname;
/*  41 */     this.info = attrinfo;
/*     */   }
/*     */ 
/*     */   protected AttributeInfo(ConstPool cp, String attrname) {
/*  45 */     this(cp, attrname, (byte[])null);
/*     */   }
/*     */ 
/*     */   public AttributeInfo(ConstPool cp, String attrname, byte[] attrinfo)
/*     */   {
/*  57 */     this(cp, cp.addUtf8Info(attrname), attrinfo);
/*     */   }
/*     */ 
/*     */   protected AttributeInfo(ConstPool cp, int n, DataInputStream in)
/*     */     throws IOException
/*     */   {
/*  63 */     this.constPool = cp;
/*  64 */     this.name = n;
/*  65 */     int len = in.readInt();
/*  66 */     this.info = new byte[len];
/*  67 */     if (len > 0)
/*  68 */       in.readFully(this.info);
/*     */   }
/*     */ 
/*     */   static AttributeInfo read(ConstPool cp, DataInputStream in)
/*     */     throws IOException
/*     */   {
/*  74 */     int name = in.readUnsignedShort();
/*  75 */     String nameStr = cp.getUtf8Info(name);
/*  76 */     if (nameStr.charAt(0) < 'L') {
/*  77 */       if (nameStr.equals("AnnotationDefault"))
/*  78 */         return new AnnotationDefaultAttribute(cp, name, in);
/*  79 */       if (nameStr.equals("Code"))
/*  80 */         return new CodeAttribute(cp, name, in);
/*  81 */       if (nameStr.equals("ConstantValue"))
/*  82 */         return new ConstantAttribute(cp, name, in);
/*  83 */       if (nameStr.equals("Deprecated"))
/*  84 */         return new DeprecatedAttribute(cp, name, in);
/*  85 */       if (nameStr.equals("EnclosingMethod"))
/*  86 */         return new EnclosingMethodAttribute(cp, name, in);
/*  87 */       if (nameStr.equals("Exceptions"))
/*  88 */         return new ExceptionsAttribute(cp, name, in);
/*  89 */       if (nameStr.equals("InnerClasses")) {
/*  90 */         return new InnerClassesAttribute(cp, name, in);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  95 */       if (nameStr.equals("LineNumberTable"))
/*  96 */         return new LineNumberAttribute(cp, name, in);
/*  97 */       if (nameStr.equals("LocalVariableTable"))
/*  98 */         return new LocalVariableAttribute(cp, name, in);
/*  99 */       if (nameStr.equals("LocalVariableTypeTable"))
/* 100 */         return new LocalVariableTypeAttribute(cp, name, in);
/* 101 */       if ((nameStr.equals("RuntimeVisibleAnnotations")) || (nameStr.equals("RuntimeInvisibleAnnotations")))
/*     */       {
/* 104 */         return new AnnotationsAttribute(cp, name, in);
/*     */       }
/* 106 */       if ((nameStr.equals("RuntimeVisibleParameterAnnotations")) || (nameStr.equals("RuntimeInvisibleParameterAnnotations")))
/*     */       {
/* 108 */         return new ParameterAnnotationsAttribute(cp, name, in);
/* 109 */       }if (nameStr.equals("Signature"))
/* 110 */         return new SignatureAttribute(cp, name, in);
/* 111 */       if (nameStr.equals("SourceFile"))
/* 112 */         return new SourceFileAttribute(cp, name, in);
/* 113 */       if (nameStr.equals("Synthetic"))
/* 114 */         return new SyntheticAttribute(cp, name, in);
/* 115 */       if (nameStr.equals("StackMap"))
/* 116 */         return new StackMap(cp, name, in);
/* 117 */       if (nameStr.equals("StackMapTable")) {
/* 118 */         return new StackMapTable(cp, name, in);
/*     */       }
/*     */     }
/* 121 */     return new AttributeInfo(cp, name, in);
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 128 */     return this.constPool.getUtf8Info(this.name);
/*     */   }
/*     */ 
/*     */   public ConstPool getConstPool()
/*     */   {
/* 134 */     return this.constPool;
/*     */   }
/*     */ 
/*     */   public int length()
/*     */   {
/* 142 */     return this.info.length + 6;
/*     */   }
/*     */ 
/*     */   public byte[] get()
/*     */   {
/* 152 */     return this.info;
/*     */   }
/*     */ 
/*     */   public void set(byte[] newinfo)
/*     */   {
/* 161 */     this.info = newinfo;
/*     */   }
/*     */ 
/*     */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*     */   {
/* 172 */     int s = this.info.length;
/* 173 */     byte[] srcInfo = this.info;
/* 174 */     byte[] newInfo = new byte[s];
/* 175 */     for (int i = 0; i < s; i++) {
/* 176 */       newInfo[i] = srcInfo[i];
/*     */     }
/* 178 */     return new AttributeInfo(newCp, getName(), newInfo);
/*     */   }
/*     */ 
/*     */   void write(DataOutputStream out) throws IOException {
/* 182 */     out.writeShort(this.name);
/* 183 */     out.writeInt(this.info.length);
/* 184 */     if (this.info.length > 0)
/* 185 */       out.write(this.info);
/*     */   }
/*     */ 
/*     */   static int getLength(ArrayList list) {
/* 189 */     int size = 0;
/* 190 */     int n = list.size();
/* 191 */     for (int i = 0; i < n; i++) {
/* 192 */       AttributeInfo attr = (AttributeInfo)list.get(i);
/* 193 */       size += attr.length();
/*     */     }
/*     */ 
/* 196 */     return size;
/*     */   }
/*     */ 
/*     */   static AttributeInfo lookup(ArrayList list, String name) {
/* 200 */     if (list == null) {
/* 201 */       return null;
/*     */     }
/* 203 */     ListIterator iterator = list.listIterator();
/* 204 */     while (iterator.hasNext()) {
/* 205 */       AttributeInfo ai = (AttributeInfo)iterator.next();
/* 206 */       if (ai.getName().equals(name)) {
/* 207 */         return ai;
/*     */       }
/*     */     }
/* 210 */     return null;
/*     */   }
/*     */ 
/*     */   static synchronized void remove(ArrayList list, String name) {
/* 214 */     if (list == null) {
/* 215 */       return;
/*     */     }
/* 217 */     ListIterator iterator = list.listIterator();
/* 218 */     while (iterator.hasNext()) {
/* 219 */       AttributeInfo ai = (AttributeInfo)iterator.next();
/* 220 */       if (ai.getName().equals(name))
/* 221 */         iterator.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   static void writeAll(ArrayList list, DataOutputStream out)
/*     */     throws IOException
/*     */   {
/* 228 */     if (list == null) {
/* 229 */       return;
/*     */     }
/* 231 */     int n = list.size();
/* 232 */     for (int i = 0; i < n; i++) {
/* 233 */       AttributeInfo attr = (AttributeInfo)list.get(i);
/* 234 */       attr.write(out);
/*     */     }
/*     */   }
/*     */ 
/*     */   static ArrayList copyAll(ArrayList list, ConstPool cp) {
/* 239 */     if (list == null) {
/* 240 */       return null;
/*     */     }
/* 242 */     ArrayList newList = new ArrayList();
/* 243 */     int n = list.size();
/* 244 */     for (int i = 0; i < n; i++) {
/* 245 */       AttributeInfo attr = (AttributeInfo)list.get(i);
/* 246 */       newList.add(attr.copy(cp, null));
/*     */     }
/*     */ 
/* 249 */     return newList;
/*     */   }
/*     */ 
/*     */   void renameClass(String oldname, String newname)
/*     */   {
/*     */   }
/*     */ 
/*     */   void renameClass(Map classnames)
/*     */   {
/*     */   }
/*     */ 
/*     */   static void renameClass(List attributes, String oldname, String newname) {
/* 261 */     Iterator iterator = attributes.iterator();
/* 262 */     while (iterator.hasNext()) {
/* 263 */       AttributeInfo ai = (AttributeInfo)iterator.next();
/* 264 */       ai.renameClass(oldname, newname);
/*     */     }
/*     */   }
/*     */ 
/*     */   static void renameClass(List attributes, Map classnames) {
/* 269 */     Iterator iterator = attributes.iterator();
/* 270 */     while (iterator.hasNext()) {
/* 271 */       AttributeInfo ai = (AttributeInfo)iterator.next();
/* 272 */       ai.renameClass(classnames);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.AttributeInfo
 * JD-Core Version:    0.6.2
 */