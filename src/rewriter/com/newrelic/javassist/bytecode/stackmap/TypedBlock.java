/*     */ package com.newrelic.javassist.bytecode.stackmap;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ 
/*     */ public class TypedBlock extends BasicBlock
/*     */ {
/*     */   public int stackTop;
/*     */   public int numLocals;
/*     */   public TypeData[] stackTypes;
/*     */   public TypeData[] localsTypes;
/*     */   public boolean[] inputs;
/*     */   public boolean updating;
/*     */   public int status;
/*     */   public byte[] localsUsage;
/*     */ 
/*     */   public static TypedBlock[] makeBlocks(MethodInfo minfo, CodeAttribute ca, boolean optimize)
/*     */     throws BadBytecode
/*     */   {
/*  44 */     TypedBlock[] blocks = (TypedBlock[])new Maker().make(minfo);
/*  45 */     if ((optimize) && (blocks.length < 2) && (
/*  46 */       (blocks.length == 0) || (blocks[0].incoming == 0))) {
/*  47 */       return null;
/*     */     }
/*  49 */     ConstPool pool = minfo.getConstPool();
/*  50 */     boolean isStatic = (minfo.getAccessFlags() & 0x8) != 0;
/*  51 */     blocks[0].initFirstBlock(ca.getMaxStack(), ca.getMaxLocals(), pool.getClassName(), minfo.getDescriptor(), isStatic, minfo.isConstructor());
/*     */ 
/*  54 */     new Liveness().compute(ca.iterator(), blocks, ca.getMaxLocals(), blocks[0].localsTypes);
/*     */ 
/*  56 */     return blocks;
/*     */   }
/*     */ 
/*     */   protected TypedBlock(int pos) {
/*  60 */     super(pos);
/*  61 */     this.localsTypes = null;
/*  62 */     this.inputs = null;
/*  63 */     this.updating = false;
/*     */   }
/*     */ 
/*     */   protected void toString2(StringBuffer sbuf) {
/*  67 */     super.toString2(sbuf);
/*  68 */     sbuf.append(",\n stack={");
/*  69 */     printTypes(sbuf, this.stackTop, this.stackTypes);
/*  70 */     sbuf.append("}, locals={");
/*  71 */     printTypes(sbuf, this.numLocals, this.localsTypes);
/*  72 */     sbuf.append("}, inputs={");
/*  73 */     if (this.inputs != null) {
/*  74 */       for (int i = 0; i < this.inputs.length; i++)
/*  75 */         sbuf.append(this.inputs[i] != 0 ? "1, " : "0, ");
/*     */     }
/*  77 */     sbuf.append('}');
/*     */   }
/*     */ 
/*     */   private void printTypes(StringBuffer sbuf, int size, TypeData[] types)
/*     */   {
/*  82 */     if (types == null) {
/*  83 */       return;
/*     */     }
/*  85 */     for (int i = 0; i < size; i++) {
/*  86 */       if (i > 0) {
/*  87 */         sbuf.append(", ");
/*     */       }
/*  89 */       TypeData td = types[i];
/*  90 */       sbuf.append(td == null ? "<>" : td.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean alreadySet() {
/*  95 */     return this.localsTypes != null;
/*     */   }
/*     */ 
/*     */   public void setStackMap(int st, TypeData[] stack, int nl, TypeData[] locals)
/*     */     throws BadBytecode
/*     */   {
/* 101 */     this.stackTop = st;
/* 102 */     this.stackTypes = stack;
/* 103 */     this.numLocals = nl;
/* 104 */     this.localsTypes = locals;
/*     */   }
/*     */ 
/*     */   public void resetNumLocals()
/*     */   {
/* 111 */     if (this.localsTypes != null) {
/* 112 */       int nl = this.localsTypes.length;
/* 113 */       while ((nl > 0) && (this.localsTypes[(nl - 1)] == TypeTag.TOP)) {
/* 114 */         if (nl > 1) {
/* 115 */           TypeData td = this.localsTypes[(nl - 2)];
/* 116 */           if ((td == TypeTag.LONG) || (td == TypeTag.DOUBLE)) {
/*     */             break;
/*     */           }
/*     */         }
/* 120 */         nl--;
/*     */       }
/*     */ 
/* 123 */       this.numLocals = nl;
/*     */     }
/*     */   }
/*     */ 
/*     */   void initFirstBlock(int maxStack, int maxLocals, String className, String methodDesc, boolean isStatic, boolean isConstructor)
/*     */     throws BadBytecode
/*     */   {
/* 151 */     if (methodDesc.charAt(0) != '(') {
/* 152 */       throw new BadBytecode("no method descriptor: " + methodDesc);
/*     */     }
/* 154 */     this.stackTop = 0;
/* 155 */     this.stackTypes = new TypeData[maxStack];
/* 156 */     TypeData[] locals = new TypeData[maxLocals];
/* 157 */     if (isConstructor)
/* 158 */       locals[0] = new TypeData.UninitThis(className);
/* 159 */     else if (!isStatic) {
/* 160 */       locals[0] = new TypeData.ClassName(className);
/*     */     }
/* 162 */     int n = isStatic ? -1 : 0;
/* 163 */     int i = 1;
/*     */     try {
/* 165 */       while ((i = descToTag(methodDesc, i, ++n, locals)) > 0)
/* 166 */         if (locals[n].is2WordType())
/* 167 */           locals[(++n)] = TypeTag.TOP;
/*     */     }
/*     */     catch (StringIndexOutOfBoundsException e) {
/* 170 */       throw new BadBytecode("bad method descriptor: " + methodDesc);
/*     */     }
/*     */ 
/* 174 */     this.numLocals = n;
/* 175 */     this.localsTypes = locals;
/*     */   }
/*     */ 
/*     */   private static int descToTag(String desc, int i, int n, TypeData[] types)
/*     */     throws BadBytecode
/*     */   {
/* 182 */     int i0 = i;
/* 183 */     int arrayDim = 0;
/* 184 */     char c = desc.charAt(i);
/* 185 */     if (c == ')') {
/* 186 */       return 0;
/*     */     }
/* 188 */     while (c == '[') {
/* 189 */       arrayDim++;
/* 190 */       c = desc.charAt(++i);
/*     */     }
/*     */ 
/* 193 */     if (c == 'L') {
/* 194 */       int i2 = desc.indexOf(';', ++i);
/* 195 */       if (arrayDim > 0)
/* 196 */         types[n] = new TypeData.ClassName(desc.substring(i0, ++i2));
/*     */       else {
/* 198 */         types[n] = new TypeData.ClassName(desc.substring(i0 + 1, ++i2 - 1).replace('/', '.'));
/*     */       }
/* 200 */       return i2;
/*     */     }
/* 202 */     if (arrayDim > 0) {
/* 203 */       types[n] = new TypeData.ClassName(desc.substring(i0, ++i));
/* 204 */       return i;
/*     */     }
/*     */ 
/* 207 */     TypeData t = toPrimitiveTag(c);
/* 208 */     if (t == null) {
/* 209 */       throw new BadBytecode("bad method descriptor: " + desc);
/*     */     }
/* 211 */     types[n] = t;
/* 212 */     return i + 1;
/*     */   }
/*     */ 
/*     */   private static TypeData toPrimitiveTag(char c)
/*     */   {
/* 217 */     switch (c) {
/*     */     case 'B':
/*     */     case 'C':
/*     */     case 'I':
/*     */     case 'S':
/*     */     case 'Z':
/* 223 */       return TypeTag.INTEGER;
/*     */     case 'J':
/* 225 */       return TypeTag.LONG;
/*     */     case 'F':
/* 227 */       return TypeTag.FLOAT;
/*     */     case 'D':
/* 229 */       return TypeTag.DOUBLE;
/*     */     case 'E':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'K':
/*     */     case 'L':
/*     */     case 'M':
/*     */     case 'N':
/*     */     case 'O':
/*     */     case 'P':
/*     */     case 'Q':
/*     */     case 'R':
/*     */     case 'T':
/*     */     case 'U':
/*     */     case 'V':
/*     */     case 'W':
/*     */     case 'X':
/* 232 */     case 'Y': } return null;
/*     */   }
/*     */ 
/*     */   public static String getRetType(String desc)
/*     */   {
/* 237 */     int i = desc.indexOf(')');
/* 238 */     if (i < 0) {
/* 239 */       return "java.lang.Object";
/*     */     }
/* 241 */     char c = desc.charAt(i + 1);
/* 242 */     if (c == '[')
/* 243 */       return desc.substring(i + 1);
/* 244 */     if (c == 'L') {
/* 245 */       return desc.substring(i + 2, desc.length() - 1).replace('/', '.');
/*     */     }
/* 247 */     return "java.lang.Object";
/*     */   }
/*     */ 
/*     */   public static class Maker extends BasicBlock.Maker
/*     */   {
/*     */     protected BasicBlock makeBlock(int pos)
/*     */     {
/* 129 */       return new TypedBlock(pos);
/*     */     }
/*     */ 
/*     */     protected BasicBlock[] makeArray(int size) {
/* 133 */       return new TypedBlock[size];
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.stackmap.TypedBlock
 * JD-Core Version:    0.6.2
 */