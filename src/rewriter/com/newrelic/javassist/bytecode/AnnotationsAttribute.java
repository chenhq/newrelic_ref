/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.annotation.Annotation;
/*     */ import com.newrelic.javassist.bytecode.annotation.AnnotationMemberValue;
/*     */ import com.newrelic.javassist.bytecode.annotation.AnnotationsWriter;
/*     */ import com.newrelic.javassist.bytecode.annotation.ArrayMemberValue;
/*     */ import com.newrelic.javassist.bytecode.annotation.BooleanMemberValue;
/*     */ import com.newrelic.javassist.bytecode.annotation.ByteMemberValue;
/*     */ import com.newrelic.javassist.bytecode.annotation.CharMemberValue;
/*     */ import com.newrelic.javassist.bytecode.annotation.ClassMemberValue;
/*     */ import com.newrelic.javassist.bytecode.annotation.DoubleMemberValue;
/*     */ import com.newrelic.javassist.bytecode.annotation.EnumMemberValue;
/*     */ import com.newrelic.javassist.bytecode.annotation.FloatMemberValue;
/*     */ import com.newrelic.javassist.bytecode.annotation.IntegerMemberValue;
/*     */ import com.newrelic.javassist.bytecode.annotation.LongMemberValue;
/*     */ import com.newrelic.javassist.bytecode.annotation.MemberValue;
/*     */ import com.newrelic.javassist.bytecode.annotation.ShortMemberValue;
/*     */ import com.newrelic.javassist.bytecode.annotation.StringMemberValue;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class AnnotationsAttribute extends AttributeInfo
/*     */ {
/*     */   public static final String visibleTag = "RuntimeVisibleAnnotations";
/*     */   public static final String invisibleTag = "RuntimeInvisibleAnnotations";
/*     */ 
/*     */   public AnnotationsAttribute(ConstPool cp, String attrname, byte[] info)
/*     */   {
/* 124 */     super(cp, attrname, info);
/*     */   }
/*     */ 
/*     */   public AnnotationsAttribute(ConstPool cp, String attrname)
/*     */   {
/* 139 */     this(cp, attrname, new byte[] { 0, 0 });
/*     */   }
/*     */ 
/*     */   AnnotationsAttribute(ConstPool cp, int n, DataInputStream in)
/*     */     throws IOException
/*     */   {
/* 148 */     super(cp, n, in);
/*     */   }
/*     */ 
/*     */   public int numAnnotations()
/*     */   {
/* 155 */     return ByteArray.readU16bit(this.info, 0);
/*     */   }
/*     */ 
/*     */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*     */   {
/* 162 */     Copier copier = new Copier(this.info, this.constPool, newCp, classnames);
/*     */     try {
/* 164 */       copier.annotationArray();
/* 165 */       return new AnnotationsAttribute(newCp, getName(), copier.close());
/*     */     }
/*     */     catch (Exception e) {
/* 168 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Annotation getAnnotation(String type)
/*     */   {
/* 182 */     Annotation[] annotations = getAnnotations();
/* 183 */     for (int i = 0; i < annotations.length; i++) {
/* 184 */       if (annotations[i].getTypeName().equals(type)) {
/* 185 */         return annotations[i];
/*     */       }
/*     */     }
/* 188 */     return null;
/*     */   }
/*     */ 
/*     */   public void addAnnotation(Annotation annotation)
/*     */   {
/* 198 */     String type = annotation.getTypeName();
/* 199 */     Annotation[] annotations = getAnnotations();
/* 200 */     for (int i = 0; i < annotations.length; i++) {
/* 201 */       if (annotations[i].getTypeName().equals(type)) {
/* 202 */         annotations[i] = annotation;
/* 203 */         setAnnotations(annotations);
/* 204 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 208 */     Annotation[] newlist = new Annotation[annotations.length + 1];
/* 209 */     System.arraycopy(annotations, 0, newlist, 0, annotations.length);
/* 210 */     newlist[annotations.length] = annotation;
/* 211 */     setAnnotations(newlist);
/*     */   }
/*     */ 
/*     */   public Annotation[] getAnnotations()
/*     */   {
/*     */     try
/*     */     {
/* 225 */       return new Parser(this.info, this.constPool).parseAnnotations();
/*     */     }
/*     */     catch (Exception e) {
/* 228 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setAnnotations(Annotation[] annotations)
/*     */   {
/* 240 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/* 241 */     AnnotationsWriter writer = new AnnotationsWriter(output, this.constPool);
/*     */     try {
/* 243 */       int n = annotations.length;
/* 244 */       writer.numAnnotations(n);
/* 245 */       for (int i = 0; i < n; i++) {
/* 246 */         annotations[i].write(writer);
/*     */       }
/* 248 */       writer.close();
/*     */     }
/*     */     catch (IOException e) {
/* 251 */       throw new RuntimeException(e);
/*     */     }
/*     */ 
/* 254 */     set(output.toByteArray());
/*     */   }
/*     */ 
/*     */   public void setAnnotation(Annotation annotation)
/*     */   {
/* 265 */     setAnnotations(new Annotation[] { annotation });
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 272 */     Annotation[] a = getAnnotations();
/* 273 */     StringBuffer sbuf = new StringBuffer();
/* 274 */     int i = 0;
/* 275 */     while (i < a.length) {
/* 276 */       sbuf.append(a[(i++)].toString());
/* 277 */       if (i != a.length) {
/* 278 */         sbuf.append(", ");
/*     */       }
/*     */     }
/* 281 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   static class Parser extends AnnotationsAttribute.Walker
/*     */   {
/*     */     ConstPool pool;
/*     */     Annotation[][] allParams;
/*     */     Annotation[] allAnno;
/*     */     Annotation currentAnno;
/*     */     MemberValue currentMember;
/*     */ 
/*     */     Parser(byte[] info, ConstPool cp)
/*     */     {
/* 493 */       super();
/* 494 */       this.pool = cp;
/*     */     }
/*     */ 
/*     */     Annotation[][] parseParameters() throws Exception {
/* 498 */       parameters();
/* 499 */       return this.allParams;
/*     */     }
/*     */ 
/*     */     Annotation[] parseAnnotations() throws Exception {
/* 503 */       annotationArray();
/* 504 */       return this.allAnno;
/*     */     }
/*     */ 
/*     */     MemberValue parseMemberValue() throws Exception {
/* 508 */       memberValue(0);
/* 509 */       return this.currentMember;
/*     */     }
/*     */ 
/*     */     void parameters(int numParam, int pos) throws Exception {
/* 513 */       Annotation[][] params = new Annotation[numParam][];
/* 514 */       for (int i = 0; i < numParam; i++) {
/* 515 */         pos = annotationArray(pos);
/* 516 */         params[i] = this.allAnno;
/*     */       }
/*     */ 
/* 519 */       this.allParams = params;
/*     */     }
/*     */ 
/*     */     int annotationArray(int pos, int num) throws Exception {
/* 523 */       Annotation[] array = new Annotation[num];
/* 524 */       for (int i = 0; i < num; i++) {
/* 525 */         pos = annotation(pos);
/* 526 */         array[i] = this.currentAnno;
/*     */       }
/*     */ 
/* 529 */       this.allAnno = array;
/* 530 */       return pos;
/*     */     }
/*     */ 
/*     */     int annotation(int pos, int type, int numPairs) throws Exception {
/* 534 */       this.currentAnno = new Annotation(type, this.pool);
/* 535 */       return super.annotation(pos, type, numPairs);
/*     */     }
/*     */ 
/*     */     int memberValuePair(int pos, int nameIndex) throws Exception {
/* 539 */       pos = super.memberValuePair(pos, nameIndex);
/* 540 */       this.currentAnno.addMemberValue(nameIndex, this.currentMember);
/* 541 */       return pos;
/*     */     }
/*     */ 
/*     */     void constValueMember(int tag, int index) throws Exception
/*     */     {
/* 546 */       ConstPool cp = this.pool;
/*     */       MemberValue m;
/* 547 */       switch (tag) {
/*     */       case 66:
/* 549 */         m = new ByteMemberValue(index, cp);
/* 550 */         break;
/*     */       case 67:
/* 552 */         m = new CharMemberValue(index, cp);
/* 553 */         break;
/*     */       case 68:
/* 555 */         m = new DoubleMemberValue(index, cp);
/* 556 */         break;
/*     */       case 70:
/* 558 */         m = new FloatMemberValue(index, cp);
/* 559 */         break;
/*     */       case 73:
/* 561 */         m = new IntegerMemberValue(index, cp);
/* 562 */         break;
/*     */       case 74:
/* 564 */         m = new LongMemberValue(index, cp);
/* 565 */         break;
/*     */       case 83:
/* 567 */         m = new ShortMemberValue(index, cp);
/* 568 */         break;
/*     */       case 90:
/* 570 */         m = new BooleanMemberValue(index, cp);
/* 571 */         break;
/*     */       case 115:
/* 573 */         m = new StringMemberValue(index, cp);
/* 574 */         break;
/*     */       default:
/* 576 */         throw new RuntimeException("unknown tag:" + tag);
/*     */       }
/*     */ 
/* 579 */       this.currentMember = m;
/* 580 */       super.constValueMember(tag, index);
/*     */     }
/*     */ 
/*     */     void enumMemberValue(int typeNameIndex, int constNameIndex)
/*     */       throws Exception
/*     */     {
/* 586 */       this.currentMember = new EnumMemberValue(typeNameIndex, constNameIndex, this.pool);
/*     */ 
/* 588 */       super.enumMemberValue(typeNameIndex, constNameIndex);
/*     */     }
/*     */ 
/*     */     void classMemberValue(int index) throws Exception {
/* 592 */       this.currentMember = new ClassMemberValue(index, this.pool);
/* 593 */       super.classMemberValue(index);
/*     */     }
/*     */ 
/*     */     int annotationMemberValue(int pos) throws Exception {
/* 597 */       Annotation anno = this.currentAnno;
/* 598 */       pos = super.annotationMemberValue(pos);
/* 599 */       this.currentMember = new AnnotationMemberValue(this.currentAnno, this.pool);
/* 600 */       this.currentAnno = anno;
/* 601 */       return pos;
/*     */     }
/*     */ 
/*     */     int arrayMemberValue(int pos, int num) throws Exception {
/* 605 */       ArrayMemberValue amv = new ArrayMemberValue(this.pool);
/* 606 */       MemberValue[] elements = new MemberValue[num];
/* 607 */       for (int i = 0; i < num; i++) {
/* 608 */         pos = memberValue(pos);
/* 609 */         elements[i] = this.currentMember;
/*     */       }
/*     */ 
/* 612 */       amv.setValue(elements);
/* 613 */       this.currentMember = amv;
/* 614 */       return pos;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Copier extends AnnotationsAttribute.Walker
/*     */   {
/*     */     ByteArrayOutputStream output;
/*     */     AnnotationsWriter writer;
/*     */     ConstPool srcPool;
/*     */     ConstPool destPool;
/*     */     Map classnames;
/*     */ 
/*     */     Copier(byte[] info, ConstPool src, ConstPool dest, Map map)
/*     */     {
/* 404 */       super();
/* 405 */       this.output = new ByteArrayOutputStream();
/* 406 */       this.writer = new AnnotationsWriter(this.output, dest);
/* 407 */       this.srcPool = src;
/* 408 */       this.destPool = dest;
/* 409 */       this.classnames = map;
/*     */     }
/*     */ 
/*     */     byte[] close() throws IOException {
/* 413 */       this.writer.close();
/* 414 */       return this.output.toByteArray();
/*     */     }
/*     */ 
/*     */     void parameters(int numParam, int pos) throws Exception {
/* 418 */       this.writer.numParameters(numParam);
/* 419 */       super.parameters(numParam, pos);
/*     */     }
/*     */ 
/*     */     int annotationArray(int pos, int num) throws Exception {
/* 423 */       this.writer.numAnnotations(num);
/* 424 */       return super.annotationArray(pos, num);
/*     */     }
/*     */ 
/*     */     int annotation(int pos, int type, int numPairs) throws Exception {
/* 428 */       this.writer.annotation(copy(type), numPairs);
/* 429 */       return super.annotation(pos, type, numPairs);
/*     */     }
/*     */ 
/*     */     int memberValuePair(int pos, int nameIndex) throws Exception {
/* 433 */       this.writer.memberValuePair(copy(nameIndex));
/* 434 */       return super.memberValuePair(pos, nameIndex);
/*     */     }
/*     */ 
/*     */     void constValueMember(int tag, int index) throws Exception {
/* 438 */       this.writer.constValueIndex(tag, copy(index));
/* 439 */       super.constValueMember(tag, index);
/*     */     }
/*     */ 
/*     */     void enumMemberValue(int typeNameIndex, int constNameIndex)
/*     */       throws Exception
/*     */     {
/* 445 */       this.writer.enumConstValue(copy(typeNameIndex), copy(constNameIndex));
/* 446 */       super.enumMemberValue(typeNameIndex, constNameIndex);
/*     */     }
/*     */ 
/*     */     void classMemberValue(int index) throws Exception {
/* 450 */       this.writer.classInfoIndex(copy(index));
/* 451 */       super.classMemberValue(index);
/*     */     }
/*     */ 
/*     */     int annotationMemberValue(int pos) throws Exception {
/* 455 */       this.writer.annotationValue();
/* 456 */       return super.annotationMemberValue(pos);
/*     */     }
/*     */ 
/*     */     int arrayMemberValue(int pos, int num) throws Exception {
/* 460 */       this.writer.arrayValue(num);
/* 461 */       return super.arrayMemberValue(pos, num);
/*     */     }
/*     */ 
/*     */     int copy(int srcIndex)
/*     */     {
/* 474 */       return this.srcPool.copy(srcIndex, this.destPool, this.classnames);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Walker
/*     */   {
/*     */     byte[] info;
/*     */ 
/*     */     Walker(byte[] attrInfo)
/*     */     {
/* 288 */       this.info = attrInfo;
/*     */     }
/*     */ 
/*     */     final void parameters() throws Exception {
/* 292 */       int numParam = this.info[0] & 0xFF;
/* 293 */       parameters(numParam, 1);
/*     */     }
/*     */ 
/*     */     void parameters(int numParam, int pos) throws Exception {
/* 297 */       for (int i = 0; i < numParam; i++)
/* 298 */         pos = annotationArray(pos);
/*     */     }
/*     */ 
/*     */     final void annotationArray() throws Exception {
/* 302 */       annotationArray(0);
/*     */     }
/*     */ 
/*     */     final int annotationArray(int pos) throws Exception {
/* 306 */       int num = ByteArray.readU16bit(this.info, pos);
/* 307 */       return annotationArray(pos + 2, num);
/*     */     }
/*     */ 
/*     */     int annotationArray(int pos, int num) throws Exception {
/* 311 */       for (int i = 0; i < num; i++) {
/* 312 */         pos = annotation(pos);
/*     */       }
/* 314 */       return pos;
/*     */     }
/*     */ 
/*     */     final int annotation(int pos) throws Exception {
/* 318 */       int type = ByteArray.readU16bit(this.info, pos);
/* 319 */       int numPairs = ByteArray.readU16bit(this.info, pos + 2);
/* 320 */       return annotation(pos + 4, type, numPairs);
/*     */     }
/*     */ 
/*     */     int annotation(int pos, int type, int numPairs) throws Exception {
/* 324 */       for (int j = 0; j < numPairs; j++) {
/* 325 */         pos = memberValuePair(pos);
/*     */       }
/* 327 */       return pos;
/*     */     }
/*     */ 
/*     */     final int memberValuePair(int pos) throws Exception {
/* 331 */       int nameIndex = ByteArray.readU16bit(this.info, pos);
/* 332 */       return memberValuePair(pos + 2, nameIndex);
/*     */     }
/*     */ 
/*     */     int memberValuePair(int pos, int nameIndex) throws Exception {
/* 336 */       return memberValue(pos);
/*     */     }
/*     */ 
/*     */     final int memberValue(int pos) throws Exception {
/* 340 */       int tag = this.info[pos] & 0xFF;
/* 341 */       if (tag == 101) {
/* 342 */         int typeNameIndex = ByteArray.readU16bit(this.info, pos + 1);
/* 343 */         int constNameIndex = ByteArray.readU16bit(this.info, pos + 3);
/* 344 */         enumMemberValue(typeNameIndex, constNameIndex);
/* 345 */         return pos + 5;
/*     */       }
/* 347 */       if (tag == 99) {
/* 348 */         int index = ByteArray.readU16bit(this.info, pos + 1);
/* 349 */         classMemberValue(index);
/* 350 */         return pos + 3;
/*     */       }
/* 352 */       if (tag == 64)
/* 353 */         return annotationMemberValue(pos + 1);
/* 354 */       if (tag == 91) {
/* 355 */         int num = ByteArray.readU16bit(this.info, pos + 1);
/* 356 */         return arrayMemberValue(pos + 3, num);
/*     */       }
/*     */ 
/* 359 */       int index = ByteArray.readU16bit(this.info, pos + 1);
/* 360 */       constValueMember(tag, index);
/* 361 */       return pos + 3;
/*     */     }
/*     */ 
/*     */     void constValueMember(int tag, int index) throws Exception {
/*     */     }
/*     */ 
/*     */     void enumMemberValue(int typeNameIndex, int constNameIndex) throws Exception {
/*     */     }
/*     */ 
/*     */     void classMemberValue(int index) throws Exception {
/*     */     }
/*     */ 
/*     */     int annotationMemberValue(int pos) throws Exception {
/* 374 */       return annotation(pos);
/*     */     }
/*     */ 
/*     */     int arrayMemberValue(int pos, int num) throws Exception {
/* 378 */       for (int i = 0; i < num; i++) {
/* 379 */         pos = memberValue(pos);
/*     */       }
/*     */ 
/* 382 */       return pos;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.AnnotationsAttribute
 * JD-Core Version:    0.6.2
 */