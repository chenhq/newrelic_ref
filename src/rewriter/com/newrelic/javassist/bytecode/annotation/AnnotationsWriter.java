/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.ByteArray;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class AnnotationsWriter
/*     */ {
/*     */   private OutputStream output;
/*     */   private ConstPool pool;
/*     */ 
/*     */   public AnnotationsWriter(OutputStream os, ConstPool cp)
/*     */   {
/*  70 */     this.output = os;
/*  71 */     this.pool = cp;
/*     */   }
/*     */ 
/*     */   public ConstPool getConstPool()
/*     */   {
/*  78 */     return this.pool;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  86 */     this.output.close();
/*     */   }
/*     */ 
/*     */   public void numParameters(int num)
/*     */     throws IOException
/*     */   {
/*  96 */     this.output.write(num);
/*     */   }
/*     */ 
/*     */   public void numAnnotations(int num)
/*     */     throws IOException
/*     */   {
/* 106 */     write16bit(num);
/*     */   }
/*     */ 
/*     */   public void annotation(String type, int numMemberValuePairs)
/*     */     throws IOException
/*     */   {
/* 121 */     annotation(this.pool.addUtf8Info(type), numMemberValuePairs);
/*     */   }
/*     */ 
/*     */   public void annotation(int typeIndex, int numMemberValuePairs)
/*     */     throws IOException
/*     */   {
/* 136 */     write16bit(typeIndex);
/* 137 */     write16bit(numMemberValuePairs);
/*     */   }
/*     */ 
/*     */   public void memberValuePair(String memberName)
/*     */     throws IOException
/*     */   {
/* 150 */     memberValuePair(this.pool.addUtf8Info(memberName));
/*     */   }
/*     */ 
/*     */   public void memberValuePair(int memberNameIndex)
/*     */     throws IOException
/*     */   {
/* 164 */     write16bit(memberNameIndex);
/*     */   }
/*     */ 
/*     */   public void constValueIndex(boolean value)
/*     */     throws IOException
/*     */   {
/* 174 */     constValueIndex(90, this.pool.addIntegerInfo(value ? 1 : 0));
/*     */   }
/*     */ 
/*     */   public void constValueIndex(byte value)
/*     */     throws IOException
/*     */   {
/* 184 */     constValueIndex(66, this.pool.addIntegerInfo(value));
/*     */   }
/*     */ 
/*     */   public void constValueIndex(char value)
/*     */     throws IOException
/*     */   {
/* 194 */     constValueIndex(67, this.pool.addIntegerInfo(value));
/*     */   }
/*     */ 
/*     */   public void constValueIndex(short value)
/*     */     throws IOException
/*     */   {
/* 204 */     constValueIndex(83, this.pool.addIntegerInfo(value));
/*     */   }
/*     */ 
/*     */   public void constValueIndex(int value)
/*     */     throws IOException
/*     */   {
/* 214 */     constValueIndex(73, this.pool.addIntegerInfo(value));
/*     */   }
/*     */ 
/*     */   public void constValueIndex(long value)
/*     */     throws IOException
/*     */   {
/* 224 */     constValueIndex(74, this.pool.addLongInfo(value));
/*     */   }
/*     */ 
/*     */   public void constValueIndex(float value)
/*     */     throws IOException
/*     */   {
/* 234 */     constValueIndex(70, this.pool.addFloatInfo(value));
/*     */   }
/*     */ 
/*     */   public void constValueIndex(double value)
/*     */     throws IOException
/*     */   {
/* 244 */     constValueIndex(68, this.pool.addDoubleInfo(value));
/*     */   }
/*     */ 
/*     */   public void constValueIndex(String value)
/*     */     throws IOException
/*     */   {
/* 254 */     constValueIndex(115, this.pool.addUtf8Info(value));
/*     */   }
/*     */ 
/*     */   public void constValueIndex(int tag, int index)
/*     */     throws IOException
/*     */   {
/* 268 */     this.output.write(tag);
/* 269 */     write16bit(index);
/*     */   }
/*     */ 
/*     */   public void enumConstValue(String typeName, String constName)
/*     */     throws IOException
/*     */   {
/* 282 */     enumConstValue(this.pool.addUtf8Info(typeName), this.pool.addUtf8Info(constName));
/*     */   }
/*     */ 
/*     */   public void enumConstValue(int typeNameIndex, int constNameIndex)
/*     */     throws IOException
/*     */   {
/* 298 */     this.output.write(101);
/* 299 */     write16bit(typeNameIndex);
/* 300 */     write16bit(constNameIndex);
/*     */   }
/*     */ 
/*     */   public void classInfoIndex(String name)
/*     */     throws IOException
/*     */   {
/* 310 */     classInfoIndex(this.pool.addUtf8Info(name));
/*     */   }
/*     */ 
/*     */   public void classInfoIndex(int index)
/*     */     throws IOException
/*     */   {
/* 320 */     this.output.write(99);
/* 321 */     write16bit(index);
/*     */   }
/*     */ 
/*     */   public void annotationValue()
/*     */     throws IOException
/*     */   {
/* 330 */     this.output.write(64);
/*     */   }
/*     */ 
/*     */   public void arrayValue(int numValues)
/*     */     throws IOException
/*     */   {
/* 344 */     this.output.write(91);
/* 345 */     write16bit(numValues);
/*     */   }
/*     */ 
/*     */   private void write16bit(int value) throws IOException {
/* 349 */     byte[] buf = new byte[2];
/* 350 */     ByteArray.write16bit(value, buf, 0);
/* 351 */     this.output.write(buf);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.AnnotationsWriter
 * JD-Core Version:    0.6.2
 */