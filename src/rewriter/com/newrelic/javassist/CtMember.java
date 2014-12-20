/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ public abstract class CtMember
/*     */ {
/*     */   CtMember next;
/*     */   protected CtClass declaringClass;
/*     */ 
/*     */   protected CtMember(CtClass clazz)
/*     */   {
/* 127 */     this.declaringClass = clazz;
/* 128 */     this.next = null;
/*     */   }
/*     */   final CtMember next() {
/* 131 */     return this.next;
/*     */   }
/*     */ 
/*     */   void nameReplaced()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 142 */     StringBuffer buffer = new StringBuffer(getClass().getName());
/* 143 */     buffer.append("@");
/* 144 */     buffer.append(Integer.toHexString(hashCode()));
/* 145 */     buffer.append("[");
/* 146 */     buffer.append(Modifier.toString(getModifiers()));
/* 147 */     extendToString(buffer);
/* 148 */     buffer.append("]");
/* 149 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   protected abstract void extendToString(StringBuffer paramStringBuffer);
/*     */ 
/*     */   public CtClass getDeclaringClass()
/*     */   {
/* 164 */     return this.declaringClass;
/*     */   }
/*     */ 
/*     */   public boolean visibleFrom(CtClass clazz)
/*     */   {
/* 170 */     int mod = getModifiers();
/* 171 */     if (Modifier.isPublic(mod))
/* 172 */       return true;
/* 173 */     if (Modifier.isPrivate(mod)) {
/* 174 */       return clazz == this.declaringClass;
/*     */     }
/* 176 */     String declName = this.declaringClass.getPackageName();
/* 177 */     String fromName = clazz.getPackageName();
/*     */     boolean visible;
/*     */     boolean visible;
/* 179 */     if (declName == null)
/* 180 */       visible = fromName == null;
/*     */     else {
/* 182 */       visible = declName.equals(fromName);
/*     */     }
/* 184 */     if ((!visible) && (Modifier.isProtected(mod))) {
/* 185 */       return clazz.subclassOf(this.declaringClass);
/*     */     }
/* 187 */     return visible;
/*     */   }
/*     */ 
/*     */   public abstract int getModifiers();
/*     */ 
/*     */   public abstract void setModifiers(int paramInt);
/*     */ 
/*     */   public abstract boolean hasAnnotation(Class paramClass);
/*     */ 
/*     */   public abstract Object getAnnotation(Class paramClass)
/*     */     throws ClassNotFoundException;
/*     */ 
/*     */   public abstract Object[] getAnnotations()
/*     */     throws ClassNotFoundException;
/*     */ 
/*     */   public abstract Object[] getAvailableAnnotations();
/*     */ 
/*     */   public abstract String getName();
/*     */ 
/*     */   public abstract String getSignature();
/*     */ 
/*     */   public abstract byte[] getAttribute(String paramString);
/*     */ 
/*     */   public abstract void setAttribute(String paramString, byte[] paramArrayOfByte);
/*     */ 
/*     */   static class Cache extends CtMember
/*     */   {
/*     */     private CtMember methodTail;
/*     */     private CtMember consTail;
/*     */     private CtMember fieldTail;
/*     */ 
/*     */     protected void extendToString(StringBuffer buffer)
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean hasAnnotation(Class clz)
/*     */     {
/*  32 */       return false;
/*     */     }
/*  34 */     public Object getAnnotation(Class clz) throws ClassNotFoundException { return null; } 
/*     */     public Object[] getAnnotations() throws ClassNotFoundException {
/*  36 */       return null; } 
/*  37 */     public byte[] getAttribute(String name) { return null; } 
/*  38 */     public Object[] getAvailableAnnotations() { return null; } 
/*  39 */     public int getModifiers() { return 0; } 
/*  40 */     public String getName() { return null; } 
/*  41 */     public String getSignature() { return null; }
/*     */ 
/*     */     public void setAttribute(String name, byte[] data) {
/*     */     }
/*     */ 
/*     */     public void setModifiers(int mod) {
/*     */     }
/*     */ 
/*     */     Cache(CtClassType decl) {
/*  50 */       super();
/*  51 */       this.methodTail = this;
/*  52 */       this.consTail = this;
/*  53 */       this.fieldTail = this;
/*  54 */       this.fieldTail.next = this;
/*     */     }
/*     */     CtMember methodHead() {
/*  57 */       return this; } 
/*  58 */     CtMember lastMethod() { return this.methodTail; } 
/*  59 */     CtMember consHead() { return this.methodTail; } 
/*  60 */     CtMember lastCons() { return this.consTail; } 
/*  61 */     CtMember fieldHead() { return this.consTail; } 
/*  62 */     CtMember lastField() { return this.fieldTail; }
/*     */ 
/*     */     void addMethod(CtMember method) {
/*  65 */       method.next = this.methodTail.next;
/*  66 */       this.methodTail.next = method;
/*  67 */       if (this.methodTail == this.consTail) {
/*  68 */         this.consTail = method;
/*  69 */         if (this.methodTail == this.fieldTail) {
/*  70 */           this.fieldTail = method;
/*     */         }
/*     */       }
/*  73 */       this.methodTail = method;
/*     */     }
/*     */ 
/*     */     void addConstructor(CtMember cons)
/*     */     {
/*  79 */       cons.next = this.consTail.next;
/*  80 */       this.consTail.next = cons;
/*  81 */       if (this.consTail == this.fieldTail) {
/*  82 */         this.fieldTail = cons;
/*     */       }
/*  84 */       this.consTail = cons;
/*     */     }
/*     */ 
/*     */     void addField(CtMember field) {
/*  88 */       field.next = this;
/*  89 */       this.fieldTail.next = field;
/*  90 */       this.fieldTail = field;
/*     */     }
/*     */ 
/*     */     static int count(CtMember head, CtMember tail) {
/*  94 */       int n = 0;
/*  95 */       while (head != tail) {
/*  96 */         n++;
/*  97 */         head = head.next;
/*     */       }
/*     */ 
/* 100 */       return n;
/*     */     }
/*     */ 
/*     */     void remove(CtMember mem) {
/* 104 */       CtMember m = this;
/*     */       CtMember node;
/* 106 */       while ((node = m.next) != this) {
/* 107 */         if (node == mem) {
/* 108 */           m.next = node.next;
/* 109 */           if (node == this.methodTail) {
/* 110 */             this.methodTail = m;
/*     */           }
/* 112 */           if (node == this.consTail) {
/* 113 */             this.consTail = m;
/*     */           }
/* 115 */           if (node != this.fieldTail) break;
/* 116 */           this.fieldTail = m; break;
/*     */         }
/*     */ 
/* 121 */         m = m.next;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtMember
 * JD-Core Version:    0.6.2
 */