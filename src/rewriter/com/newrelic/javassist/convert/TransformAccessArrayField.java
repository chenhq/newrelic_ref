/*     */ package com.newrelic.javassist.convert;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.CodeConverter.ArrayAccessReplacementMethodNames;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator.Gap;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.bytecode.analysis.Analyzer;
/*     */ import com.newrelic.javassist.bytecode.analysis.Frame;
/*     */ import com.newrelic.javassist.bytecode.analysis.Type;
/*     */ 
/*     */ public final class TransformAccessArrayField extends Transformer
/*     */ {
/*     */   private final String methodClassname;
/*     */   private final CodeConverter.ArrayAccessReplacementMethodNames names;
/*     */   private Frame[] frames;
/*     */   private int offset;
/*     */ 
/*     */   public TransformAccessArrayField(Transformer next, String methodClassname, CodeConverter.ArrayAccessReplacementMethodNames names)
/*     */     throws NotFoundException
/*     */   {
/*  45 */     super(next);
/*  46 */     this.methodClassname = methodClassname;
/*  47 */     this.names = names;
/*     */   }
/*     */ 
/*     */   public void initialize(ConstPool cp, CtClass clazz, MethodInfo minfo)
/*     */     throws CannotCompileException
/*     */   {
/*  62 */     CodeIterator iterator = minfo.getCodeAttribute().iterator();
/*  63 */     while (iterator.hasNext())
/*     */       try {
/*  65 */         int pos = iterator.next();
/*  66 */         int c = iterator.byteAt(pos);
/*     */ 
/*  68 */         if (c == 50) {
/*  69 */           initFrames(clazz, minfo);
/*     */         }
/*  71 */         if ((c == 50) || (c == 51) || (c == 52) || (c == 49) || (c == 48) || (c == 46) || (c == 47) || (c == 53))
/*     */         {
/*  74 */           pos = replace(cp, iterator, pos, c, getLoadReplacementSignature(c));
/*  75 */         } else if ((c == 83) || (c == 84) || (c == 85) || (c == 82) || (c == 81) || (c == 79) || (c == 80) || (c == 86))
/*     */         {
/*  78 */           pos = replace(cp, iterator, pos, c, getStoreReplacementSignature(c));
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/*  82 */         throw new CannotCompileException(e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void clean()
/*     */   {
/*  88 */     this.frames = null;
/*  89 */     this.offset = -1;
/*     */   }
/*     */ 
/*     */   public int transform(CtClass tclazz, int pos, CodeIterator iterator, ConstPool cp)
/*     */     throws BadBytecode
/*     */   {
/*  95 */     return pos;
/*     */   }
/*     */ 
/*     */   private Frame getFrame(int pos) throws BadBytecode {
/*  99 */     return this.frames[(pos - this.offset)];
/*     */   }
/*     */ 
/*     */   private void initFrames(CtClass clazz, MethodInfo minfo) throws BadBytecode {
/* 103 */     if (this.frames == null) {
/* 104 */       this.frames = new Analyzer().analyze(clazz, minfo);
/* 105 */       this.offset = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   private int updatePos(int pos, int increment) {
/* 110 */     if (this.offset > -1) {
/* 111 */       this.offset += increment;
/*     */     }
/* 113 */     return pos + increment;
/*     */   }
/*     */ 
/*     */   private String getTopType(int pos) throws BadBytecode {
/* 117 */     Frame frame = getFrame(pos);
/* 118 */     if (frame == null) {
/* 119 */       return null;
/*     */     }
/* 121 */     CtClass clazz = frame.peek().getCtClass();
/* 122 */     return clazz != null ? Descriptor.toJvmName(clazz) : null;
/*     */   }
/*     */ 
/*     */   private int replace(ConstPool cp, CodeIterator iterator, int pos, int opcode, String signature) throws BadBytecode
/*     */   {
/* 127 */     String castType = null;
/* 128 */     String methodName = getMethodName(opcode);
/* 129 */     if (methodName != null)
/*     */     {
/* 131 */       if (opcode == 50) {
/* 132 */         castType = getTopType(iterator.lookAhead());
/*     */ 
/* 136 */         if (castType == null)
/* 137 */           return pos;
/* 138 */         if ("java/lang/Object".equals(castType)) {
/* 139 */           castType = null;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 144 */       iterator.writeByte(0, pos);
/* 145 */       CodeIterator.Gap gap = iterator.insertGapAt(pos, castType != null ? 5 : 2, false);
/*     */ 
/* 147 */       pos = gap.position;
/* 148 */       int mi = cp.addClassInfo(this.methodClassname);
/* 149 */       int methodref = cp.addMethodrefInfo(mi, methodName, signature);
/* 150 */       iterator.writeByte(184, pos);
/* 151 */       iterator.write16bit(methodref, pos + 1);
/*     */ 
/* 153 */       if (castType != null) {
/* 154 */         int index = cp.addClassInfo(castType);
/* 155 */         iterator.writeByte(192, pos + 3);
/* 156 */         iterator.write16bit(index, pos + 4);
/*     */       }
/*     */ 
/* 159 */       pos = updatePos(pos, gap.length);
/*     */     }
/*     */ 
/* 162 */     return pos;
/*     */   }
/*     */ 
/*     */   private String getMethodName(int opcode) {
/* 166 */     String methodName = null;
/* 167 */     switch (opcode) {
/*     */     case 50:
/* 169 */       methodName = this.names.objectRead();
/* 170 */       break;
/*     */     case 51:
/* 172 */       methodName = this.names.byteOrBooleanRead();
/* 173 */       break;
/*     */     case 52:
/* 175 */       methodName = this.names.charRead();
/* 176 */       break;
/*     */     case 49:
/* 178 */       methodName = this.names.doubleRead();
/* 179 */       break;
/*     */     case 48:
/* 181 */       methodName = this.names.floatRead();
/* 182 */       break;
/*     */     case 46:
/* 184 */       methodName = this.names.intRead();
/* 185 */       break;
/*     */     case 53:
/* 187 */       methodName = this.names.shortRead();
/* 188 */       break;
/*     */     case 47:
/* 190 */       methodName = this.names.longRead();
/* 191 */       break;
/*     */     case 83:
/* 193 */       methodName = this.names.objectWrite();
/* 194 */       break;
/*     */     case 84:
/* 196 */       methodName = this.names.byteOrBooleanWrite();
/* 197 */       break;
/*     */     case 85:
/* 199 */       methodName = this.names.charWrite();
/* 200 */       break;
/*     */     case 82:
/* 202 */       methodName = this.names.doubleWrite();
/* 203 */       break;
/*     */     case 81:
/* 205 */       methodName = this.names.floatWrite();
/* 206 */       break;
/*     */     case 79:
/* 208 */       methodName = this.names.intWrite();
/* 209 */       break;
/*     */     case 86:
/* 211 */       methodName = this.names.shortWrite();
/* 212 */       break;
/*     */     case 80:
/* 214 */       methodName = this.names.longWrite();
/*     */     case 54:
/*     */     case 55:
/*     */     case 56:
/*     */     case 57:
/*     */     case 58:
/*     */     case 59:
/*     */     case 60:
/*     */     case 61:
/*     */     case 62:
/*     */     case 63:
/*     */     case 64:
/*     */     case 65:
/*     */     case 66:
/*     */     case 67:
/*     */     case 68:
/*     */     case 69:
/*     */     case 70:
/*     */     case 71:
/*     */     case 72:
/*     */     case 73:
/*     */     case 74:
/*     */     case 75:
/*     */     case 76:
/*     */     case 77:
/* 218 */     case 78: } if (methodName.equals("")) {
/* 219 */       methodName = null;
/*     */     }
/* 221 */     return methodName;
/*     */   }
/*     */ 
/*     */   private String getLoadReplacementSignature(int opcode) throws BadBytecode {
/* 225 */     switch (opcode) {
/*     */     case 50:
/* 227 */       return "(Ljava/lang/Object;I)Ljava/lang/Object;";
/*     */     case 51:
/* 229 */       return "(Ljava/lang/Object;I)B";
/*     */     case 52:
/* 231 */       return "(Ljava/lang/Object;I)C";
/*     */     case 49:
/* 233 */       return "(Ljava/lang/Object;I)D";
/*     */     case 48:
/* 235 */       return "(Ljava/lang/Object;I)F";
/*     */     case 46:
/* 237 */       return "(Ljava/lang/Object;I)I";
/*     */     case 53:
/* 239 */       return "(Ljava/lang/Object;I)S";
/*     */     case 47:
/* 241 */       return "(Ljava/lang/Object;I)J";
/*     */     }
/*     */ 
/* 244 */     throw new BadBytecode(opcode);
/*     */   }
/*     */ 
/*     */   private String getStoreReplacementSignature(int opcode) throws BadBytecode {
/* 248 */     switch (opcode) {
/*     */     case 83:
/* 250 */       return "(Ljava/lang/Object;ILjava/lang/Object;)V";
/*     */     case 84:
/* 252 */       return "(Ljava/lang/Object;IB)V";
/*     */     case 85:
/* 254 */       return "(Ljava/lang/Object;IC)V";
/*     */     case 82:
/* 256 */       return "(Ljava/lang/Object;ID)V";
/*     */     case 81:
/* 258 */       return "(Ljava/lang/Object;IF)V";
/*     */     case 79:
/* 260 */       return "(Ljava/lang/Object;II)V";
/*     */     case 86:
/* 262 */       return "(Ljava/lang/Object;IS)V";
/*     */     case 80:
/* 264 */       return "(Ljava/lang/Object;IJ)V";
/*     */     }
/*     */ 
/* 267 */     throw new BadBytecode(opcode);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.convert.TransformAccessArrayField
 * JD-Core Version:    0.6.2
 */