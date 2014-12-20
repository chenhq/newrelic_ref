/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class InnerClassesAttribute extends AttributeInfo
/*     */ {
/*     */   public static final String tag = "InnerClasses";
/*     */ 
/*     */   InnerClassesAttribute(ConstPool cp, int n, DataInputStream in)
/*     */     throws IOException
/*     */   {
/*  34 */     super(cp, n, in);
/*     */   }
/*     */ 
/*     */   private InnerClassesAttribute(ConstPool cp, byte[] info) {
/*  38 */     super(cp, "InnerClasses", info);
/*     */   }
/*     */ 
/*     */   public InnerClassesAttribute(ConstPool cp)
/*     */   {
/*  47 */     super(cp, "InnerClasses", new byte[2]);
/*  48 */     ByteArray.write16bit(0, get(), 0);
/*     */   }
/*     */ 
/*     */   public int tableLength()
/*     */   {
/*  54 */     return ByteArray.readU16bit(get(), 0);
/*     */   }
/*     */ 
/*     */   public int innerClassIndex(int nth)
/*     */   {
/*  60 */     return ByteArray.readU16bit(get(), nth * 8 + 2);
/*     */   }
/*     */ 
/*     */   public String innerClass(int nth)
/*     */   {
/*  70 */     int i = innerClassIndex(nth);
/*  71 */     if (i == 0) {
/*  72 */       return null;
/*     */     }
/*  74 */     return this.constPool.getClassInfo(i);
/*     */   }
/*     */ 
/*     */   public void setInnerClassIndex(int nth, int index)
/*     */   {
/*  82 */     ByteArray.write16bit(index, get(), nth * 8 + 2);
/*     */   }
/*     */ 
/*     */   public int outerClassIndex(int nth)
/*     */   {
/*  89 */     return ByteArray.readU16bit(get(), nth * 8 + 4);
/*     */   }
/*     */ 
/*     */   public String outerClass(int nth)
/*     */   {
/*  99 */     int i = outerClassIndex(nth);
/* 100 */     if (i == 0) {
/* 101 */       return null;
/*     */     }
/* 103 */     return this.constPool.getClassInfo(i);
/*     */   }
/*     */ 
/*     */   public void setOuterClassIndex(int nth, int index)
/*     */   {
/* 111 */     ByteArray.write16bit(index, get(), nth * 8 + 4);
/*     */   }
/*     */ 
/*     */   public int innerNameIndex(int nth)
/*     */   {
/* 118 */     return ByteArray.readU16bit(get(), nth * 8 + 6);
/*     */   }
/*     */ 
/*     */   public String innerName(int nth)
/*     */   {
/* 128 */     int i = innerNameIndex(nth);
/* 129 */     if (i == 0) {
/* 130 */       return null;
/*     */     }
/* 132 */     return this.constPool.getUtf8Info(i);
/*     */   }
/*     */ 
/*     */   public void setInnerNameIndex(int nth, int index)
/*     */   {
/* 140 */     ByteArray.write16bit(index, get(), nth * 8 + 6);
/*     */   }
/*     */ 
/*     */   public int accessFlags(int nth)
/*     */   {
/* 147 */     return ByteArray.readU16bit(get(), nth * 8 + 8);
/*     */   }
/*     */ 
/*     */   public void setAccessFlags(int nth, int flags)
/*     */   {
/* 155 */     ByteArray.write16bit(flags, get(), nth * 8 + 8);
/*     */   }
/*     */ 
/*     */   public void append(String inner, String outer, String name, int flags)
/*     */   {
/* 167 */     int i = this.constPool.addClassInfo(inner);
/* 168 */     int o = this.constPool.addClassInfo(outer);
/* 169 */     int n = this.constPool.addUtf8Info(name);
/* 170 */     append(i, o, n, flags);
/*     */   }
/*     */ 
/*     */   public void append(int inner, int outer, int name, int flags)
/*     */   {
/* 182 */     byte[] data = get();
/* 183 */     int len = data.length;
/* 184 */     byte[] newData = new byte[len + 8];
/* 185 */     for (int i = 2; i < len; i++) {
/* 186 */       newData[i] = data[i];
/*     */     }
/* 188 */     int n = ByteArray.readU16bit(data, 0);
/* 189 */     ByteArray.write16bit(n + 1, newData, 0);
/*     */ 
/* 191 */     ByteArray.write16bit(inner, newData, len);
/* 192 */     ByteArray.write16bit(outer, newData, len + 2);
/* 193 */     ByteArray.write16bit(name, newData, len + 4);
/* 194 */     ByteArray.write16bit(flags, newData, len + 6);
/*     */ 
/* 196 */     set(newData);
/*     */   }
/*     */ 
/*     */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*     */   {
/* 208 */     byte[] src = get();
/* 209 */     byte[] dest = new byte[src.length];
/* 210 */     ConstPool cp = getConstPool();
/* 211 */     InnerClassesAttribute attr = new InnerClassesAttribute(newCp, dest);
/* 212 */     int n = ByteArray.readU16bit(src, 0);
/* 213 */     ByteArray.write16bit(n, dest, 0);
/* 214 */     int j = 2;
/* 215 */     for (int i = 0; i < n; i++) {
/* 216 */       int innerClass = ByteArray.readU16bit(src, j);
/* 217 */       int outerClass = ByteArray.readU16bit(src, j + 2);
/* 218 */       int innerName = ByteArray.readU16bit(src, j + 4);
/* 219 */       int innerAccess = ByteArray.readU16bit(src, j + 6);
/*     */ 
/* 221 */       if (innerClass != 0) {
/* 222 */         innerClass = cp.copy(innerClass, newCp, classnames);
/*     */       }
/* 224 */       ByteArray.write16bit(innerClass, dest, j);
/*     */ 
/* 226 */       if (outerClass != 0) {
/* 227 */         outerClass = cp.copy(outerClass, newCp, classnames);
/*     */       }
/* 229 */       ByteArray.write16bit(outerClass, dest, j + 2);
/*     */ 
/* 231 */       if (innerName != 0) {
/* 232 */         innerName = cp.copy(innerName, newCp, classnames);
/*     */       }
/* 234 */       ByteArray.write16bit(innerName, dest, j + 4);
/* 235 */       ByteArray.write16bit(innerAccess, dest, j + 6);
/* 236 */       j += 8;
/*     */     }
/*     */ 
/* 239 */     return attr;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.InnerClassesAttribute
 * JD-Core Version:    0.6.2
 */