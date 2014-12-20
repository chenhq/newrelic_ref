/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class SignatureAttribute extends AttributeInfo
/*     */ {
/*     */   public static final String tag = "Signature";
/*     */ 
/*     */   SignatureAttribute(ConstPool cp, int n, DataInputStream in)
/*     */     throws IOException
/*     */   {
/*  36 */     super(cp, n, in);
/*     */   }
/*     */ 
/*     */   public SignatureAttribute(ConstPool cp, String signature)
/*     */   {
/*  46 */     super(cp, "Signature");
/*  47 */     int index = cp.addUtf8Info(signature);
/*  48 */     byte[] bvalue = new byte[2];
/*  49 */     bvalue[0] = ((byte)(index >>> 8));
/*  50 */     bvalue[1] = ((byte)index);
/*  51 */     set(bvalue);
/*     */   }
/*     */ 
/*     */   public String getSignature()
/*     */   {
/*  61 */     return getConstPool().getUtf8Info(ByteArray.readU16bit(get(), 0));
/*     */   }
/*     */ 
/*     */   public void setSignature(String sig)
/*     */   {
/*  72 */     int index = getConstPool().addUtf8Info(sig);
/*  73 */     ByteArray.write16bit(index, this.info, 0);
/*     */   }
/*     */ 
/*     */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*     */   {
/*  85 */     return new SignatureAttribute(newCp, getSignature());
/*     */   }
/*     */ 
/*     */   void renameClass(String oldname, String newname) {
/*  89 */     String sig = renameClass(getSignature(), oldname, newname);
/*  90 */     setSignature(sig);
/*     */   }
/*     */ 
/*     */   void renameClass(Map classnames) {
/*  94 */     String sig = renameClass(getSignature(), classnames);
/*  95 */     setSignature(sig);
/*     */   }
/*     */ 
/*     */   static String renameClass(String desc, String oldname, String newname) {
/*  99 */     if (desc.indexOf(oldname) < 0) {
/* 100 */       return desc;
/*     */     }
/* 102 */     StringBuffer newdesc = new StringBuffer();
/* 103 */     int head = 0;
/* 104 */     int i = 0;
/*     */     while (true) {
/* 106 */       int j = desc.indexOf('L', i);
/* 107 */       if (j < 0) { break; } int k = j;
/* 111 */       int p = 0;
/*     */ 
/* 113 */       boolean match = true;
/*     */       char c;
/*     */       try { int len = oldname.length();
/* 116 */         while (isNamePart(c = desc.charAt(++k)))
/* 117 */           if ((p >= len) || (c != oldname.charAt(p++)))
/* 118 */             match = false;
/*     */       } catch (IndexOutOfBoundsException e) {
/* 120 */         break;
/* 121 */       }i = k + 1;
/* 122 */       if ((match) && (p == oldname.length())) {
/* 123 */         newdesc.append(desc.substring(head, j));
/* 124 */         newdesc.append('L');
/* 125 */         newdesc.append(newname);
/* 126 */         newdesc.append(c);
/* 127 */         head = i;
/*     */       }
/*     */     }
/*     */ 
/* 131 */     if (head == 0) {
/* 132 */       return desc;
/*     */     }
/* 134 */     int len = desc.length();
/* 135 */     if (head < len) {
/* 136 */       newdesc.append(desc.substring(head, len));
/*     */     }
/* 138 */     return newdesc.toString();
/*     */   }
/*     */ 
/*     */   static String renameClass(String desc, Map map)
/*     */   {
/* 143 */     if (map == null) {
/* 144 */       return desc;
/*     */     }
/* 146 */     StringBuffer newdesc = new StringBuffer();
/* 147 */     int head = 0;
/* 148 */     int i = 0;
/*     */     while (true) {
/* 150 */       int j = desc.indexOf('L', i);
/* 151 */       if (j < 0) { break; }
/* 154 */       StringBuffer nameBuf = new StringBuffer();
/* 155 */       int k = j;
/*     */       char c;
/*     */       try {
/* 158 */         while (isNamePart(c = desc.charAt(++k)))
/* 159 */           nameBuf.append(c);
/*     */       } catch (IndexOutOfBoundsException e) {
/* 161 */         break;
/* 162 */       }i = k + 1;
/* 163 */       String name = nameBuf.toString();
/* 164 */       String name2 = (String)map.get(name);
/* 165 */       if (name2 != null) {
/* 166 */         newdesc.append(desc.substring(head, j));
/* 167 */         newdesc.append('L');
/* 168 */         newdesc.append(name2);
/* 169 */         newdesc.append(c);
/* 170 */         head = i;
/*     */       }
/*     */     }
/*     */ 
/* 174 */     if (head == 0) {
/* 175 */       return desc;
/*     */     }
/* 177 */     int len = desc.length();
/* 178 */     if (head < len) {
/* 179 */       newdesc.append(desc.substring(head, len));
/*     */     }
/* 181 */     return newdesc.toString();
/*     */   }
/*     */ 
/*     */   private static boolean isNamePart(int c)
/*     */   {
/* 186 */     return (c != 59) && (c != 60);
/*     */   }
/*     */ 
/*     */   public static ClassSignature toClassSignature(String sig)
/*     */     throws BadBytecode
/*     */   {
/*     */     try
/*     */     {
/* 646 */       return parseSig(sig);
/*     */     } catch (IndexOutOfBoundsException e) {
/*     */     }
/* 649 */     throw error(sig);
/*     */   }
/*     */ 
/*     */   public static MethodSignature toMethodSignature(String sig)
/*     */     throws BadBytecode
/*     */   {
/*     */     try
/*     */     {
/* 662 */       return parseMethodSig(sig);
/*     */     } catch (IndexOutOfBoundsException e) {
/*     */     }
/* 665 */     throw error(sig);
/*     */   }
/*     */ 
/*     */   public static ObjectType toFieldSignature(String sig)
/*     */     throws BadBytecode
/*     */   {
/*     */     try
/*     */     {
/* 679 */       return parseObjectType(sig, new Cursor(null), false);
/*     */     } catch (IndexOutOfBoundsException e) {
/*     */     }
/* 682 */     throw error(sig);
/*     */   }
/*     */ 
/*     */   private static ClassSignature parseSig(String sig)
/*     */     throws BadBytecode, IndexOutOfBoundsException
/*     */   {
/* 689 */     Cursor cur = new Cursor(null);
/* 690 */     TypeParameter[] tp = parseTypeParams(sig, cur);
/* 691 */     ClassType superClass = parseClassType(sig, cur);
/* 692 */     int sigLen = sig.length();
/* 693 */     ArrayList ifArray = new ArrayList();
/* 694 */     while ((cur.position < sigLen) && (sig.charAt(cur.position) == 'L')) {
/* 695 */       ifArray.add(parseClassType(sig, cur));
/*     */     }
/* 697 */     ClassType[] ifs = (ClassType[])ifArray.toArray(new ClassType[ifArray.size()]);
/*     */ 
/* 699 */     return new ClassSignature(tp, superClass, ifs);
/*     */   }
/*     */ 
/*     */   private static MethodSignature parseMethodSig(String sig)
/*     */     throws BadBytecode
/*     */   {
/* 705 */     Cursor cur = new Cursor(null);
/* 706 */     TypeParameter[] tp = parseTypeParams(sig, cur);
/* 707 */     if (sig.charAt(cur.position++) != '(') {
/* 708 */       throw error(sig);
/*     */     }
/* 710 */     ArrayList params = new ArrayList();
/* 711 */     while (sig.charAt(cur.position) != ')') {
/* 712 */       Type t = parseType(sig, cur);
/* 713 */       params.add(t);
/*     */     }
/*     */ 
/* 716 */     cur.position += 1;
/* 717 */     Type ret = parseType(sig, cur);
/* 718 */     int sigLen = sig.length();
/* 719 */     ArrayList exceptions = new ArrayList();
/* 720 */     while ((cur.position < sigLen) && (sig.charAt(cur.position) == '^')) {
/* 721 */       cur.position += 1;
/* 722 */       ObjectType t = parseObjectType(sig, cur, false);
/* 723 */       if ((t instanceof ArrayType)) {
/* 724 */         throw error(sig);
/*     */       }
/* 726 */       exceptions.add(t);
/*     */     }
/*     */ 
/* 729 */     Type[] p = (Type[])params.toArray(new Type[params.size()]);
/* 730 */     ObjectType[] ex = (ObjectType[])exceptions.toArray(new ObjectType[exceptions.size()]);
/* 731 */     return new MethodSignature(tp, p, ret, ex);
/*     */   }
/*     */ 
/*     */   private static TypeParameter[] parseTypeParams(String sig, Cursor cur)
/*     */     throws BadBytecode
/*     */   {
/* 737 */     ArrayList typeParam = new ArrayList();
/* 738 */     if (sig.charAt(cur.position) == '<') {
/* 739 */       cur.position += 1;
/* 740 */       while (sig.charAt(cur.position) != '>') {
/* 741 */         int nameBegin = cur.position;
/* 742 */         int nameEnd = cur.indexOf(sig, 58);
/* 743 */         ObjectType classBound = parseObjectType(sig, cur, true);
/* 744 */         ArrayList ifBound = new ArrayList();
/* 745 */         while (sig.charAt(cur.position) == ':') {
/* 746 */           cur.position += 1;
/* 747 */           ObjectType t = parseObjectType(sig, cur, false);
/* 748 */           ifBound.add(t);
/*     */         }
/*     */ 
/* 751 */         TypeParameter p = new TypeParameter(sig, nameBegin, nameEnd, classBound, (ObjectType[])ifBound.toArray(new ObjectType[ifBound.size()]));
/*     */ 
/* 753 */         typeParam.add(p);
/*     */       }
/*     */ 
/* 756 */       cur.position += 1;
/*     */     }
/*     */ 
/* 759 */     return (TypeParameter[])typeParam.toArray(new TypeParameter[typeParam.size()]);
/*     */   }
/*     */ 
/*     */   private static ObjectType parseObjectType(String sig, Cursor c, boolean dontThrow)
/*     */     throws BadBytecode
/*     */   {
/* 766 */     int begin = c.position;
/* 767 */     switch (sig.charAt(begin)) {
/*     */     case 'L':
/* 769 */       return parseClassType2(sig, c, null);
/*     */     case 'T':
/* 771 */       int i = c.indexOf(sig, 59);
/* 772 */       return new TypeVariable(sig, begin + 1, i);
/*     */     case '[':
/* 774 */       return parseArray(sig, c);
/*     */     }
/* 776 */     if (dontThrow) {
/* 777 */       return null;
/*     */     }
/* 779 */     throw error(sig);
/*     */   }
/*     */ 
/*     */   private static ClassType parseClassType(String sig, Cursor c)
/*     */     throws BadBytecode
/*     */   {
/* 786 */     if (sig.charAt(c.position) == 'L') {
/* 787 */       return parseClassType2(sig, c, null);
/*     */     }
/* 789 */     throw error(sig);
/*     */   }
/*     */ 
/*     */   private static ClassType parseClassType2(String sig, Cursor c, ClassType parent) throws BadBytecode
/*     */   {
/* 795 */     int start = ++c.position;
/*     */     char t;
/*     */     do
/* 798 */       t = sig.charAt(c.position++);
/* 799 */     while ((t != '$') && (t != '<') && (t != ';'));
/* 800 */     int end = c.position - 1;
/*     */     TypeArgument[] targs;
/* 802 */     if (t == '<') {
/* 803 */       TypeArgument[] targs = parseTypeArgs(sig, c);
/* 804 */       t = sig.charAt(c.position++);
/*     */     }
/*     */     else {
/* 807 */       targs = null;
/*     */     }
/* 809 */     ClassType thisClass = ClassType.make(sig, start, end, targs, parent);
/* 810 */     if (t == '$') {
/* 811 */       c.position -= 1;
/* 812 */       return parseClassType2(sig, c, thisClass);
/*     */     }
/*     */ 
/* 815 */     return thisClass;
/*     */   }
/*     */ 
/*     */   private static TypeArgument[] parseTypeArgs(String sig, Cursor c) throws BadBytecode {
/* 819 */     ArrayList args = new ArrayList();
/*     */     while (true)
/*     */     {
/*     */       char t;
/* 821 */       if ((t = sig.charAt(c.position++)) == '>')
/*     */         break;
/*     */       TypeArgument ta;
/*     */       TypeArgument ta;
/* 823 */       if (t == '*') {
/* 824 */         ta = new TypeArgument(null, '*');
/*     */       } else {
/* 826 */         if ((t != '+') && (t != '-')) {
/* 827 */           t = ' ';
/* 828 */           c.position -= 1;
/*     */         }
/*     */ 
/* 831 */         ta = new TypeArgument(parseObjectType(sig, c, false), t);
/*     */       }
/*     */ 
/* 834 */       args.add(ta);
/*     */     }
/*     */ 
/* 837 */     return (TypeArgument[])args.toArray(new TypeArgument[args.size()]);
/*     */   }
/*     */ 
/*     */   private static ObjectType parseArray(String sig, Cursor c) throws BadBytecode {
/* 841 */     int dim = 1;
/*     */     while (true) { if (sig.charAt(++c.position) != '[') break;
/* 843 */       dim++;
/*     */     }
/* 845 */     return new ArrayType(dim, parseType(sig, c));
/*     */   }
/*     */ 
/*     */   private static Type parseType(String sig, Cursor c) throws BadBytecode {
/* 849 */     Type t = parseObjectType(sig, c, true);
/* 850 */     if (t == null) {
/* 851 */       t = new BaseType(sig.charAt(c.position++));
/*     */     }
/* 853 */     return t;
/*     */   }
/*     */ 
/*     */   private static BadBytecode error(String sig) {
/* 857 */     return new BadBytecode("bad signature: " + sig);
/*     */   }
/*     */ 
/*     */   public static class TypeVariable extends SignatureAttribute.ObjectType
/*     */   {
/*     */     String name;
/*     */ 
/*     */     TypeVariable(String sig, int begin, int end)
/*     */     {
/* 619 */       this.name = sig.substring(begin, end);
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 626 */       return this.name;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 633 */       return this.name;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ArrayType extends SignatureAttribute.ObjectType
/*     */   {
/*     */     int dim;
/*     */     SignatureAttribute.Type componentType;
/*     */ 
/*     */     public ArrayType(int d, SignatureAttribute.Type comp)
/*     */     {
/* 584 */       this.dim = d;
/* 585 */       this.componentType = comp;
/*     */     }
/*     */ 
/*     */     public int getDimension()
/*     */     {
/* 591 */       return this.dim;
/*     */     }
/*     */ 
/*     */     public SignatureAttribute.Type getComponentType()
/*     */     {
/* 597 */       return this.componentType;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 604 */       StringBuffer sbuf = new StringBuffer(this.componentType.toString());
/* 605 */       for (int i = 0; i < this.dim; i++) {
/* 606 */         sbuf.append("[]");
/*     */       }
/* 608 */       return sbuf.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class NestedClassType extends SignatureAttribute.ClassType
/*     */   {
/*     */     SignatureAttribute.ClassType parent;
/*     */ 
/*     */     NestedClassType(String s, int b, int e, SignatureAttribute.TypeArgument[] targs, SignatureAttribute.ClassType p)
/*     */     {
/* 565 */       super(b, e, targs);
/* 566 */       this.parent = p;
/*     */     }
/*     */ 
/*     */     public SignatureAttribute.ClassType getDeclaringClass()
/*     */     {
/* 573 */       return this.parent;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ClassType extends SignatureAttribute.ObjectType
/*     */   {
/*     */     String name;
/*     */     SignatureAttribute.TypeArgument[] arguments;
/*     */ 
/*     */     static ClassType make(String s, int b, int e, SignatureAttribute.TypeArgument[] targs, ClassType parent)
/*     */     {
/* 498 */       if (parent == null) {
/* 499 */         return new ClassType(s, b, e, targs);
/*     */       }
/* 501 */       return new SignatureAttribute.NestedClassType(s, b, e, targs, parent);
/*     */     }
/*     */ 
/*     */     ClassType(String signature, int begin, int end, SignatureAttribute.TypeArgument[] targs) {
/* 505 */       this.name = signature.substring(begin, end).replace('/', '.');
/* 506 */       this.arguments = targs;
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 513 */       return this.name;
/*     */     }
/*     */ 
/*     */     public SignatureAttribute.TypeArgument[] getTypeArguments()
/*     */     {
/* 521 */       return this.arguments;
/*     */     }
/*     */ 
/*     */     public ClassType getDeclaringClass()
/*     */     {
/* 529 */       return null;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 535 */       StringBuffer sbuf = new StringBuffer();
/* 536 */       ClassType parent = getDeclaringClass();
/* 537 */       if (parent != null) {
/* 538 */         sbuf.append(parent.toString()).append('.');
/*     */       }
/* 540 */       sbuf.append(this.name);
/* 541 */       if (this.arguments != null) {
/* 542 */         sbuf.append('<');
/* 543 */         int n = this.arguments.length;
/* 544 */         for (int i = 0; i < n; i++) {
/* 545 */           if (i > 0) {
/* 546 */             sbuf.append(", ");
/*     */           }
/* 548 */           sbuf.append(this.arguments[i].toString());
/*     */         }
/*     */ 
/* 551 */         sbuf.append('>');
/*     */       }
/*     */ 
/* 554 */       return sbuf.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class ObjectType extends SignatureAttribute.Type
/*     */   {
/*     */   }
/*     */ 
/*     */   public static class BaseType extends SignatureAttribute.Type
/*     */   {
/*     */     char descriptor;
/*     */ 
/*     */     BaseType(char c)
/*     */     {
/* 459 */       this.descriptor = c;
/*     */     }
/*     */ 
/*     */     public char getDescriptor()
/*     */     {
/* 466 */       return this.descriptor;
/*     */     }
/*     */ 
/*     */     public CtClass getCtlass()
/*     */     {
/* 473 */       return Descriptor.toPrimitiveClass(this.descriptor);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 480 */       return Descriptor.toClassName(Character.toString(this.descriptor));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class Type
/*     */   {
/*     */     static void toString(StringBuffer sbuf, Type[] ts)
/*     */     {
/* 445 */       for (int i = 0; i < ts.length; i++) {
/* 446 */         if (i > 0) {
/* 447 */           sbuf.append(", ");
/*     */         }
/* 449 */         sbuf.append(ts[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class TypeArgument
/*     */   {
/*     */     SignatureAttribute.ObjectType arg;
/*     */     char wildcard;
/*     */ 
/*     */     TypeArgument(SignatureAttribute.ObjectType a, char w)
/*     */     {
/* 396 */       this.arg = a;
/* 397 */       this.wildcard = w;
/*     */     }
/*     */ 
/*     */     public char getKind()
/*     */     {
/* 406 */       return this.wildcard;
/*     */     }
/*     */ 
/*     */     public boolean isWildcard()
/*     */     {
/* 412 */       return this.wildcard != ' ';
/*     */     }
/*     */ 
/*     */     public SignatureAttribute.ObjectType getType()
/*     */     {
/* 421 */       return this.arg;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 427 */       if (this.wildcard == '*') {
/* 428 */         return "?";
/*     */       }
/* 430 */       String type = this.arg.toString();
/* 431 */       if (this.wildcard == ' ')
/* 432 */         return type;
/* 433 */       if (this.wildcard == '+') {
/* 434 */         return "? extends " + type;
/*     */       }
/* 436 */       return "? super " + type;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class TypeParameter
/*     */   {
/*     */     String name;
/*     */     SignatureAttribute.ObjectType superClass;
/*     */     SignatureAttribute.ObjectType[] superInterfaces;
/*     */ 
/*     */     TypeParameter(String sig, int nb, int ne, SignatureAttribute.ObjectType sc, SignatureAttribute.ObjectType[] si)
/*     */     {
/* 326 */       this.name = sig.substring(nb, ne);
/* 327 */       this.superClass = sc;
/* 328 */       this.superInterfaces = si;
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 335 */       return this.name;
/*     */     }
/*     */ 
/*     */     public SignatureAttribute.ObjectType getClassBound()
/*     */     {
/* 343 */       return this.superClass;
/*     */     }
/*     */ 
/*     */     public SignatureAttribute.ObjectType[] getInterfaceBound()
/*     */     {
/* 350 */       return this.superInterfaces;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 356 */       StringBuffer sbuf = new StringBuffer(getName());
/* 357 */       if (this.superClass != null) {
/* 358 */         sbuf.append(" extends ").append(this.superClass.toString());
/*     */       }
/* 360 */       int len = this.superInterfaces.length;
/* 361 */       if (len > 0) {
/* 362 */         for (int i = 0; i < len; i++) {
/* 363 */           if ((i > 0) || (this.superClass != null))
/* 364 */             sbuf.append(" & ");
/*     */           else {
/* 366 */             sbuf.append(" extends ");
/*     */           }
/* 368 */           sbuf.append(this.superInterfaces[i].toString());
/*     */         }
/*     */       }
/*     */ 
/* 372 */       return sbuf.toString();
/*     */     }
/*     */ 
/*     */     static void toString(StringBuffer sbuf, TypeParameter[] tp) {
/* 376 */       sbuf.append('<');
/* 377 */       for (int i = 0; i < tp.length; i++) {
/* 378 */         if (i > 0) {
/* 379 */           sbuf.append(", ");
/*     */         }
/* 381 */         sbuf.append(tp[i]);
/*     */       }
/*     */ 
/* 384 */       sbuf.append('>');
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class MethodSignature
/*     */   {
/*     */     SignatureAttribute.TypeParameter[] typeParams;
/*     */     SignatureAttribute.Type[] params;
/*     */     SignatureAttribute.Type retType;
/*     */     SignatureAttribute.ObjectType[] exceptions;
/*     */ 
/*     */     MethodSignature(SignatureAttribute.TypeParameter[] tp, SignatureAttribute.Type[] p, SignatureAttribute.Type ret, SignatureAttribute.ObjectType[] ex)
/*     */     {
/* 264 */       this.typeParams = tp;
/* 265 */       this.params = p;
/* 266 */       this.retType = ret;
/* 267 */       this.exceptions = ex;
/*     */     }
/*     */ 
/*     */     public SignatureAttribute.TypeParameter[] getTypeParameters()
/*     */     {
/* 275 */       return this.typeParams;
/*     */     }
/*     */ 
/*     */     public SignatureAttribute.Type[] getParameterTypes()
/*     */     {
/* 282 */       return this.params;
/*     */     }
/*     */ 
/*     */     public SignatureAttribute.Type getReturnType()
/*     */     {
/* 287 */       return this.retType;
/*     */     }
/*     */ 
/*     */     public SignatureAttribute.ObjectType[] getExceptionTypes()
/*     */     {
/* 295 */       return this.exceptions;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 301 */       StringBuffer sbuf = new StringBuffer();
/*     */ 
/* 303 */       SignatureAttribute.TypeParameter.toString(sbuf, this.typeParams);
/* 304 */       sbuf.append(" (");
/* 305 */       SignatureAttribute.Type.toString(sbuf, this.params);
/* 306 */       sbuf.append(") ");
/* 307 */       sbuf.append(this.retType);
/* 308 */       if (this.exceptions.length > 0) {
/* 309 */         sbuf.append(" throws ");
/* 310 */         SignatureAttribute.Type.toString(sbuf, this.exceptions);
/*     */       }
/*     */ 
/* 313 */       return sbuf.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ClassSignature
/*     */   {
/*     */     SignatureAttribute.TypeParameter[] params;
/*     */     SignatureAttribute.ClassType superClass;
/*     */     SignatureAttribute.ClassType[] interfaces;
/*     */ 
/*     */     ClassSignature(SignatureAttribute.TypeParameter[] p, SignatureAttribute.ClassType s, SignatureAttribute.ClassType[] i)
/*     */     {
/* 211 */       this.params = p;
/* 212 */       this.superClass = s;
/* 213 */       this.interfaces = i;
/*     */     }
/*     */ 
/*     */     public SignatureAttribute.TypeParameter[] getParameters()
/*     */     {
/* 222 */       return this.params;
/*     */     }
/*     */ 
/*     */     public SignatureAttribute.ClassType getSuperClass()
/*     */     {
/* 228 */       return this.superClass;
/*     */     }
/*     */ 
/*     */     public SignatureAttribute.ClassType[] getInterfaces()
/*     */     {
/* 235 */       return this.interfaces;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 241 */       StringBuffer sbuf = new StringBuffer();
/*     */ 
/* 243 */       SignatureAttribute.TypeParameter.toString(sbuf, this.params);
/* 244 */       sbuf.append(" extends ").append(this.superClass);
/* 245 */       if (this.interfaces.length > 0) {
/* 246 */         sbuf.append(" implements ");
/* 247 */         SignatureAttribute.Type.toString(sbuf, this.interfaces);
/*     */       }
/*     */ 
/* 250 */       return sbuf.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class Cursor
/*     */   {
/* 190 */     int position = 0;
/*     */ 
/*     */     private Cursor() {  } 
/* 193 */     int indexOf(String s, int ch) throws BadBytecode { int i = s.indexOf(ch, this.position);
/* 194 */       if (i < 0) {
/* 195 */         throw SignatureAttribute.error(s);
/*     */       }
/* 197 */       this.position = (i + 1);
/* 198 */       return i;
/*     */     }
/*     */ 
/*     */     Cursor(SignatureAttribute.1 x0)
/*     */     {
/* 189 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.SignatureAttribute
 * JD-Core Version:    0.6.2
 */