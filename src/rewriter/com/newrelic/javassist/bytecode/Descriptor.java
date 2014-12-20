/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtPrimitiveType;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class Descriptor
/*     */ {
/*     */   public static String toJvmName(String classname)
/*     */   {
/*  38 */     return classname.replace('.', '/');
/*     */   }
/*     */ 
/*     */   public static String toJavaName(String classname)
/*     */   {
/*  51 */     return classname.replace('/', '.');
/*     */   }
/*     */ 
/*     */   public static String toJvmName(CtClass clazz)
/*     */   {
/*  59 */     if (clazz.isArray()) {
/*  60 */       return of(clazz);
/*     */     }
/*  62 */     return toJvmName(clazz.getName());
/*     */   }
/*     */ 
/*     */   public static String toClassName(String descriptor)
/*     */   {
/*  71 */     int arrayDim = 0;
/*  72 */     int i = 0;
/*  73 */     char c = descriptor.charAt(0);
/*  74 */     while (c == '[') {
/*  75 */       arrayDim++;
/*  76 */       c = descriptor.charAt(++i);
/*     */     }
/*     */ 
/*  80 */     if (c == 'L') {
/*  81 */       int i2 = descriptor.indexOf(';', i++);
/*  82 */       String name = descriptor.substring(i, i2).replace('/', '.');
/*  83 */       i = i2;
/*     */     }
/*     */     else
/*     */     {
/*     */       String name;
/*  85 */       if (c == 'V') {
/*  86 */         name = "void";
/*     */       }
/*     */       else
/*     */       {
/*     */         String name;
/*  87 */         if (c == 'I') {
/*  88 */           name = "int";
/*     */         }
/*     */         else
/*     */         {
/*     */           String name;
/*  89 */           if (c == 'B') {
/*  90 */             name = "byte";
/*     */           }
/*     */           else
/*     */           {
/*     */             String name;
/*  91 */             if (c == 'J') {
/*  92 */               name = "long";
/*     */             }
/*     */             else
/*     */             {
/*     */               String name;
/*  93 */               if (c == 'D') {
/*  94 */                 name = "double";
/*     */               }
/*     */               else
/*     */               {
/*     */                 String name;
/*  95 */                 if (c == 'F') {
/*  96 */                   name = "float";
/*     */                 }
/*     */                 else
/*     */                 {
/*     */                   String name;
/*  97 */                   if (c == 'C') {
/*  98 */                     name = "char";
/*     */                   }
/*     */                   else
/*     */                   {
/*     */                     String name;
/*  99 */                     if (c == 'S') {
/* 100 */                       name = "short";
/*     */                     }
/*     */                     else
/*     */                     {
/*     */                       String name;
/* 101 */                       if (c == 'Z')
/* 102 */                         name = "boolean";
/*     */                       else
/* 104 */                         throw new RuntimeException("bad descriptor: " + descriptor);
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     String name;
/* 106 */     if (i + 1 != descriptor.length()) {
/* 107 */       throw new RuntimeException("multiple descriptors?: " + descriptor);
/*     */     }
/* 109 */     if (arrayDim == 0) {
/* 110 */       return name;
/*     */     }
/* 112 */     StringBuffer sbuf = new StringBuffer(name);
/*     */     do {
/* 114 */       sbuf.append("[]");
/* 115 */       arrayDim--; } while (arrayDim > 0);
/*     */ 
/* 117 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   public static String of(String classname)
/*     */   {
/* 125 */     if (classname.equals("void"))
/* 126 */       return "V";
/* 127 */     if (classname.equals("int"))
/* 128 */       return "I";
/* 129 */     if (classname.equals("byte"))
/* 130 */       return "B";
/* 131 */     if (classname.equals("long"))
/* 132 */       return "J";
/* 133 */     if (classname.equals("double"))
/* 134 */       return "D";
/* 135 */     if (classname.equals("float"))
/* 136 */       return "F";
/* 137 */     if (classname.equals("char"))
/* 138 */       return "C";
/* 139 */     if (classname.equals("short"))
/* 140 */       return "S";
/* 141 */     if (classname.equals("boolean")) {
/* 142 */       return "Z";
/*     */     }
/* 144 */     return "L" + toJvmName(classname) + ";";
/*     */   }
/*     */ 
/*     */   public static String rename(String desc, String oldname, String newname)
/*     */   {
/* 158 */     if (desc.indexOf(oldname) < 0) {
/* 159 */       return desc;
/*     */     }
/* 161 */     StringBuffer newdesc = new StringBuffer();
/* 162 */     int head = 0;
/* 163 */     int i = 0;
/*     */     while (true) {
/* 165 */       int j = desc.indexOf('L', i);
/* 166 */       if (j < 0)
/*     */         break;
/* 168 */       if ((desc.startsWith(oldname, j + 1)) && (desc.charAt(j + oldname.length() + 1) == ';'))
/*     */       {
/* 170 */         newdesc.append(desc.substring(head, j));
/* 171 */         newdesc.append('L');
/* 172 */         newdesc.append(newname);
/* 173 */         newdesc.append(';');
/* 174 */         head = i = j + oldname.length() + 2;
/*     */       }
/*     */       else {
/* 177 */         i = desc.indexOf(';', j) + 1;
/* 178 */         if (i < 1) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/* 183 */     if (head == 0) {
/* 184 */       return desc;
/*     */     }
/* 186 */     int len = desc.length();
/* 187 */     if (head < len) {
/* 188 */       newdesc.append(desc.substring(head, len));
/*     */     }
/* 190 */     return newdesc.toString();
/*     */   }
/*     */ 
/*     */   public static String rename(String desc, Map map)
/*     */   {
/* 203 */     if (map == null) {
/* 204 */       return desc;
/*     */     }
/* 206 */     StringBuffer newdesc = new StringBuffer();
/* 207 */     int head = 0;
/* 208 */     int i = 0;
/*     */     while (true) {
/* 210 */       int j = desc.indexOf('L', i);
/* 211 */       if (j < 0) {
/*     */         break;
/*     */       }
/* 214 */       int k = desc.indexOf(';', j);
/* 215 */       if (k < 0) {
/*     */         break;
/*     */       }
/* 218 */       i = k + 1;
/* 219 */       String name = desc.substring(j + 1, k);
/* 220 */       String name2 = (String)map.get(name);
/* 221 */       if (name2 != null) {
/* 222 */         newdesc.append(desc.substring(head, j));
/* 223 */         newdesc.append('L');
/* 224 */         newdesc.append(name2);
/* 225 */         newdesc.append(';');
/* 226 */         head = i;
/*     */       }
/*     */     }
/*     */ 
/* 230 */     if (head == 0) {
/* 231 */       return desc;
/*     */     }
/* 233 */     int len = desc.length();
/* 234 */     if (head < len) {
/* 235 */       newdesc.append(desc.substring(head, len));
/*     */     }
/* 237 */     return newdesc.toString();
/*     */   }
/*     */ 
/*     */   public static String of(CtClass type)
/*     */   {
/* 245 */     StringBuffer sbuf = new StringBuffer();
/* 246 */     toDescriptor(sbuf, type);
/* 247 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   private static void toDescriptor(StringBuffer desc, CtClass type) {
/* 251 */     if (type.isArray()) {
/* 252 */       desc.append('[');
/*     */       try {
/* 254 */         toDescriptor(desc, type.getComponentType());
/*     */       }
/*     */       catch (NotFoundException e) {
/* 257 */         desc.append('L');
/* 258 */         String name = type.getName();
/* 259 */         desc.append(toJvmName(name.substring(0, name.length() - 2)));
/* 260 */         desc.append(';');
/*     */       }
/*     */     }
/* 263 */     else if (type.isPrimitive()) {
/* 264 */       CtPrimitiveType pt = (CtPrimitiveType)type;
/* 265 */       desc.append(pt.getDescriptor());
/*     */     }
/*     */     else {
/* 268 */       desc.append('L');
/* 269 */       desc.append(type.getName().replace('.', '/'));
/* 270 */       desc.append(';');
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String ofConstructor(CtClass[] paramTypes)
/*     */   {
/* 281 */     return ofMethod(CtClass.voidType, paramTypes);
/*     */   }
/*     */ 
/*     */   public static String ofMethod(CtClass returnType, CtClass[] paramTypes)
/*     */   {
/* 292 */     StringBuffer desc = new StringBuffer();
/* 293 */     desc.append('(');
/* 294 */     if (paramTypes != null) {
/* 295 */       int n = paramTypes.length;
/* 296 */       for (int i = 0; i < n; i++) {
/* 297 */         toDescriptor(desc, paramTypes[i]);
/*     */       }
/*     */     }
/* 300 */     desc.append(')');
/* 301 */     if (returnType != null) {
/* 302 */       toDescriptor(desc, returnType);
/*     */     }
/* 304 */     return desc.toString();
/*     */   }
/*     */ 
/*     */   public static String ofParameters(CtClass[] paramTypes)
/*     */   {
/* 315 */     return ofMethod(null, paramTypes);
/*     */   }
/*     */ 
/*     */   public static String appendParameter(String classname, String desc)
/*     */   {
/* 328 */     int i = desc.indexOf(')');
/* 329 */     if (i < 0) {
/* 330 */       return desc;
/*     */     }
/* 332 */     StringBuffer newdesc = new StringBuffer();
/* 333 */     newdesc.append(desc.substring(0, i));
/* 334 */     newdesc.append('L');
/* 335 */     newdesc.append(classname.replace('.', '/'));
/* 336 */     newdesc.append(';');
/* 337 */     newdesc.append(desc.substring(i));
/* 338 */     return newdesc.toString();
/*     */   }
/*     */ 
/*     */   public static String insertParameter(String classname, String desc)
/*     */   {
/* 353 */     if (desc.charAt(0) != '(') {
/* 354 */       return desc;
/*     */     }
/* 356 */     return "(L" + classname.replace('.', '/') + ';' + desc.substring(1);
/*     */   }
/*     */ 
/*     */   public static String appendParameter(CtClass type, String descriptor)
/*     */   {
/* 369 */     int i = descriptor.indexOf(')');
/* 370 */     if (i < 0) {
/* 371 */       return descriptor;
/*     */     }
/* 373 */     StringBuffer newdesc = new StringBuffer();
/* 374 */     newdesc.append(descriptor.substring(0, i));
/* 375 */     toDescriptor(newdesc, type);
/* 376 */     newdesc.append(descriptor.substring(i));
/* 377 */     return newdesc.toString();
/*     */   }
/*     */ 
/*     */   public static String insertParameter(CtClass type, String descriptor)
/*     */   {
/* 391 */     if (descriptor.charAt(0) != '(') {
/* 392 */       return descriptor;
/*     */     }
/* 394 */     return "(" + of(type) + descriptor.substring(1);
/*     */   }
/*     */ 
/*     */   public static String changeReturnType(String classname, String desc)
/*     */   {
/* 406 */     int i = desc.indexOf(')');
/* 407 */     if (i < 0) {
/* 408 */       return desc;
/*     */     }
/* 410 */     StringBuffer newdesc = new StringBuffer();
/* 411 */     newdesc.append(desc.substring(0, i + 1));
/* 412 */     newdesc.append('L');
/* 413 */     newdesc.append(classname.replace('.', '/'));
/* 414 */     newdesc.append(';');
/* 415 */     return newdesc.toString();
/*     */   }
/*     */ 
/*     */   public static CtClass[] getParameterTypes(String desc, ClassPool cp)
/*     */     throws NotFoundException
/*     */   {
/* 430 */     if (desc.charAt(0) != '(') {
/* 431 */       return null;
/*     */     }
/* 433 */     int num = numOfParameters(desc);
/* 434 */     CtClass[] args = new CtClass[num];
/* 435 */     int n = 0;
/* 436 */     int i = 1;
/*     */     do
/* 438 */       i = toCtClass(cp, desc, i, args, n++);
/* 439 */     while (i > 0);
/* 440 */     return args;
/*     */   }
/*     */ 
/*     */   public static boolean eqParamTypes(String desc1, String desc2)
/*     */   {
/* 450 */     if (desc1.charAt(0) != '(') {
/* 451 */       return false;
/*     */     }
/* 453 */     for (int i = 0; ; i++) {
/* 454 */       char c = desc1.charAt(i);
/* 455 */       if (c != desc2.charAt(i)) {
/* 456 */         return false;
/*     */       }
/* 458 */       if (c == ')')
/* 459 */         return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getParamDescriptor(String decl)
/*     */   {
/* 469 */     return decl.substring(0, decl.indexOf(')') + 1);
/*     */   }
/*     */ 
/*     */   public static CtClass getReturnType(String desc, ClassPool cp)
/*     */     throws NotFoundException
/*     */   {
/* 483 */     int i = desc.indexOf(')');
/* 484 */     if (i < 0) {
/* 485 */       return null;
/*     */     }
/* 487 */     CtClass[] type = new CtClass[1];
/* 488 */     toCtClass(cp, desc, i + 1, type, 0);
/* 489 */     return type[0];
/*     */   }
/*     */ 
/*     */   public static int numOfParameters(String desc)
/*     */   {
/* 500 */     int n = 0;
/* 501 */     int i = 1;
/*     */     while (true) {
/* 503 */       char c = desc.charAt(i);
/* 504 */       if (c == ')') {
/*     */         break;
/*     */       }
/* 507 */       while (c == '[') {
/* 508 */         c = desc.charAt(++i);
/*     */       }
/* 510 */       if (c == 'L') {
/* 511 */         i = desc.indexOf(';', i) + 1;
/* 512 */         if (i <= 0)
/* 513 */           throw new IndexOutOfBoundsException("bad descriptor");
/*     */       }
/*     */       else {
/* 516 */         i++;
/*     */       }
/* 518 */       n++;
/*     */     }
/*     */ 
/* 521 */     return n;
/*     */   }
/*     */ 
/*     */   public static CtClass toCtClass(String desc, ClassPool cp)
/*     */     throws NotFoundException
/*     */   {
/* 540 */     CtClass[] clazz = new CtClass[1];
/* 541 */     int res = toCtClass(cp, desc, 0, clazz, 0);
/* 542 */     if (res >= 0) {
/* 543 */       return clazz[0];
/*     */     }
/*     */ 
/* 547 */     return cp.get(desc.replace('/', '.'));
/*     */   }
/*     */ 
/*     */   private static int toCtClass(ClassPool cp, String desc, int i, CtClass[] args, int n)
/*     */     throws NotFoundException
/*     */   {
/* 558 */     int arrayDim = 0;
/* 559 */     char c = desc.charAt(i);
/* 560 */     while (c == '[') {
/* 561 */       arrayDim++;
/* 562 */       c = desc.charAt(++i);
/*     */     }
/*     */     String name;
/*     */     int i2;
/*     */     String name;
/* 565 */     if (c == 'L') {
/* 566 */       int i2 = desc.indexOf(';', ++i);
/* 567 */       name = desc.substring(i, i2++).replace('/', '.');
/*     */     }
/*     */     else {
/* 570 */       CtClass type = toPrimitiveClass(c);
/* 571 */       if (type == null) {
/* 572 */         return -1;
/*     */       }
/* 574 */       i2 = i + 1;
/* 575 */       if (arrayDim == 0) {
/* 576 */         args[n] = type;
/* 577 */         return i2;
/*     */       }
/*     */ 
/* 580 */       name = type.getName();
/*     */     }
/*     */ 
/* 583 */     if (arrayDim > 0) {
/* 584 */       StringBuffer sbuf = new StringBuffer(name);
/* 585 */       while (arrayDim-- > 0) {
/* 586 */         sbuf.append("[]");
/*     */       }
/* 588 */       name = sbuf.toString();
/*     */     }
/*     */ 
/* 591 */     args[n] = cp.get(name);
/* 592 */     return i2;
/*     */   }
/*     */ 
/*     */   static CtClass toPrimitiveClass(char c) {
/* 596 */     CtClass type = null;
/* 597 */     switch (c) {
/*     */     case 'Z':
/* 599 */       type = CtClass.booleanType;
/* 600 */       break;
/*     */     case 'C':
/* 602 */       type = CtClass.charType;
/* 603 */       break;
/*     */     case 'B':
/* 605 */       type = CtClass.byteType;
/* 606 */       break;
/*     */     case 'S':
/* 608 */       type = CtClass.shortType;
/* 609 */       break;
/*     */     case 'I':
/* 611 */       type = CtClass.intType;
/* 612 */       break;
/*     */     case 'J':
/* 614 */       type = CtClass.longType;
/* 615 */       break;
/*     */     case 'F':
/* 617 */       type = CtClass.floatType;
/* 618 */       break;
/*     */     case 'D':
/* 620 */       type = CtClass.doubleType;
/* 621 */       break;
/*     */     case 'V':
/* 623 */       type = CtClass.voidType;
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
/*     */     case 'W':
/*     */     case 'X':
/* 627 */     case 'Y': } return type;
/*     */   }
/*     */ 
/*     */   public static int arrayDimension(String desc)
/*     */   {
/* 639 */     int dim = 0;
/* 640 */     while (desc.charAt(dim) == '[') {
/* 641 */       dim++;
/*     */     }
/* 643 */     return dim;
/*     */   }
/*     */ 
/*     */   public static String toArrayComponent(String desc, int dim)
/*     */   {
/* 656 */     return desc.substring(dim);
/*     */   }
/*     */ 
/*     */   public static int dataSize(String desc)
/*     */   {
/* 671 */     return dataSize(desc, true);
/*     */   }
/*     */ 
/*     */   public static int paramSize(String desc)
/*     */   {
/* 684 */     return -dataSize(desc, false);
/*     */   }
/*     */ 
/*     */   private static int dataSize(String desc, boolean withRet) {
/* 688 */     int n = 0;
/* 689 */     char c = desc.charAt(0);
/* 690 */     if (c == '(') {
/* 691 */       int i = 1;
/*     */       while (true) {
/* 693 */         c = desc.charAt(i);
/* 694 */         if (c == ')') {
/* 695 */           c = desc.charAt(i + 1);
/* 696 */           break;
/*     */         }
/*     */ 
/* 699 */         boolean array = false;
/* 700 */         while (c == '[') {
/* 701 */           array = true;
/* 702 */           c = desc.charAt(++i);
/*     */         }
/*     */ 
/* 705 */         if (c == 'L') {
/* 706 */           i = desc.indexOf(';', i) + 1;
/* 707 */           if (i <= 0)
/* 708 */             throw new IndexOutOfBoundsException("bad descriptor");
/*     */         }
/*     */         else {
/* 711 */           i++;
/*     */         }
/* 713 */         if ((!array) && ((c == 'J') || (c == 'D')))
/* 714 */           n -= 2;
/*     */         else {
/* 716 */           n--;
/*     */         }
/*     */       }
/*     */     }
/* 720 */     if (withRet) {
/* 721 */       if ((c == 'J') || (c == 'D'))
/* 722 */         n += 2;
/* 723 */       else if (c != 'V')
/* 724 */         n++;
/*     */     }
/* 726 */     return n;
/*     */   }
/*     */ 
/*     */   public static String toString(String desc)
/*     */   {
/* 737 */     return PrettyPrinter.toString(desc);
/*     */   }
/*     */ 
/*     */   public static class Iterator
/*     */   {
/*     */     private String desc;
/*     */     private int index;
/*     */     private int curPos;
/*     */     private boolean param;
/*     */ 
/*     */     public Iterator(String s)
/*     */     {
/* 806 */       this.desc = s;
/* 807 */       this.index = (this.curPos = 0);
/* 808 */       this.param = false;
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 815 */       return this.index < this.desc.length();
/*     */     }
/*     */ 
/*     */     public boolean isParameter()
/*     */     {
/* 821 */       return this.param;
/*     */     }
/*     */ 
/*     */     public char currentChar()
/*     */     {
/* 826 */       return this.desc.charAt(this.curPos);
/*     */     }
/*     */ 
/*     */     public boolean is2byte()
/*     */     {
/* 832 */       char c = currentChar();
/* 833 */       return (c == 'D') || (c == 'J');
/*     */     }
/*     */ 
/*     */     public int next()
/*     */     {
/* 841 */       int nextPos = this.index;
/* 842 */       char c = this.desc.charAt(nextPos);
/* 843 */       if (c == '(') {
/* 844 */         this.index += 1;
/* 845 */         c = this.desc.charAt(++nextPos);
/* 846 */         this.param = true;
/*     */       }
/*     */ 
/* 849 */       if (c == ')') {
/* 850 */         this.index += 1;
/* 851 */         c = this.desc.charAt(++nextPos);
/* 852 */         this.param = false;
/*     */       }
/*     */ 
/* 855 */       while (c == '[') {
/* 856 */         c = this.desc.charAt(++nextPos);
/*     */       }
/* 858 */       if (c == 'L') {
/* 859 */         nextPos = this.desc.indexOf(';', nextPos) + 1;
/* 860 */         if (nextPos <= 0)
/* 861 */           throw new IndexOutOfBoundsException("bad descriptor");
/*     */       }
/*     */       else {
/* 864 */         nextPos++;
/*     */       }
/* 866 */       this.curPos = this.index;
/* 867 */       this.index = nextPos;
/* 868 */       return this.curPos;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class PrettyPrinter
/*     */   {
/*     */     static String toString(String desc)
/*     */     {
/* 742 */       StringBuffer sbuf = new StringBuffer();
/* 743 */       if (desc.charAt(0) == '(') {
/* 744 */         int pos = 1;
/* 745 */         sbuf.append('(');
/* 746 */         while (desc.charAt(pos) != ')') {
/* 747 */           if (pos > 1) {
/* 748 */             sbuf.append(',');
/*     */           }
/* 750 */           pos = readType(sbuf, pos, desc);
/*     */         }
/*     */ 
/* 753 */         sbuf.append(')');
/*     */       }
/*     */       else {
/* 756 */         readType(sbuf, 0, desc);
/*     */       }
/* 758 */       return sbuf.toString();
/*     */     }
/*     */ 
/*     */     static int readType(StringBuffer sbuf, int pos, String desc) {
/* 762 */       char c = desc.charAt(pos);
/* 763 */       int arrayDim = 0;
/* 764 */       while (c == '[') {
/* 765 */         arrayDim++;
/* 766 */         c = desc.charAt(++pos);
/*     */       }
/*     */ 
/* 769 */       if (c == 'L') {
/*     */         while (true) {
/* 771 */           c = desc.charAt(++pos);
/* 772 */           if (c == ';') {
/*     */             break;
/*     */           }
/* 775 */           if (c == '/') {
/* 776 */             c = '.';
/*     */           }
/* 778 */           sbuf.append(c);
/*     */         }
/*     */       }
/* 781 */       CtClass t = Descriptor.toPrimitiveClass(c);
/* 782 */       sbuf.append(t.getName());
/*     */ 
/* 785 */       while (arrayDim-- > 0) {
/* 786 */         sbuf.append("[]");
/*     */       }
/* 788 */       return pos + 1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.Descriptor
 * JD-Core Version:    0.6.2
 */