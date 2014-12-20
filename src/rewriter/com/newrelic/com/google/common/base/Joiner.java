/*     */ package com.newrelic.com.google.common.base;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import java.io.IOException;
/*     */ import java.util.AbstractList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public class Joiner
/*     */ {
/*     */   private final String separator;
/*     */ 
/*     */   public static Joiner on(String separator)
/*     */   {
/*  71 */     return new Joiner(separator);
/*     */   }
/*     */ 
/*     */   public static Joiner on(char separator)
/*     */   {
/*  78 */     return new Joiner(String.valueOf(separator));
/*     */   }
/*     */ 
/*     */   private Joiner(String separator)
/*     */   {
/*  84 */     this.separator = ((String)Preconditions.checkNotNull(separator));
/*     */   }
/*     */ 
/*     */   private Joiner(Joiner prototype) {
/*  88 */     this.separator = prototype.separator;
/*     */   }
/*     */ 
/*     */   public <A extends Appendable> A appendTo(A appendable, Iterable<?> parts)
/*     */     throws IOException
/*     */   {
/*  96 */     return appendTo(appendable, parts.iterator());
/*     */   }
/*     */ 
/*     */   public <A extends Appendable> A appendTo(A appendable, Iterator<?> parts)
/*     */     throws IOException
/*     */   {
/* 106 */     Preconditions.checkNotNull(appendable);
/* 107 */     if (parts.hasNext()) {
/* 108 */       appendable.append(toString(parts.next()));
/* 109 */       while (parts.hasNext()) {
/* 110 */         appendable.append(this.separator);
/* 111 */         appendable.append(toString(parts.next()));
/*     */       }
/*     */     }
/* 114 */     return appendable;
/*     */   }
/*     */ 
/*     */   public final <A extends Appendable> A appendTo(A appendable, Object[] parts)
/*     */     throws IOException
/*     */   {
/* 122 */     return appendTo(appendable, Arrays.asList(parts));
/*     */   }
/*     */ 
/*     */   public final <A extends Appendable> A appendTo(A appendable, @Nullable Object first, @Nullable Object second, Object[] rest)
/*     */     throws IOException
/*     */   {
/* 131 */     return appendTo(appendable, iterable(first, second, rest));
/*     */   }
/*     */ 
/*     */   public final StringBuilder appendTo(StringBuilder builder, Iterable<?> parts)
/*     */   {
/* 140 */     return appendTo(builder, parts.iterator());
/*     */   }
/*     */ 
/*     */   public final StringBuilder appendTo(StringBuilder builder, Iterator<?> parts)
/*     */   {
/*     */     try
/*     */     {
/* 152 */       appendTo(builder, parts);
/*     */     } catch (IOException impossible) {
/* 154 */       throw new AssertionError(impossible);
/*     */     }
/* 156 */     return builder;
/*     */   }
/*     */ 
/*     */   public final StringBuilder appendTo(StringBuilder builder, Object[] parts)
/*     */   {
/* 165 */     return appendTo(builder, Arrays.asList(parts));
/*     */   }
/*     */ 
/*     */   public final StringBuilder appendTo(StringBuilder builder, @Nullable Object first, @Nullable Object second, Object[] rest)
/*     */   {
/* 175 */     return appendTo(builder, iterable(first, second, rest));
/*     */   }
/*     */ 
/*     */   public final String join(Iterable<?> parts)
/*     */   {
/* 183 */     return join(parts.iterator());
/*     */   }
/*     */ 
/*     */   public final String join(Iterator<?> parts)
/*     */   {
/* 193 */     return appendTo(new StringBuilder(), parts).toString();
/*     */   }
/*     */ 
/*     */   public final String join(Object[] parts)
/*     */   {
/* 201 */     return join(Arrays.asList(parts));
/*     */   }
/*     */ 
/*     */   public final String join(@Nullable Object first, @Nullable Object second, Object[] rest)
/*     */   {
/* 209 */     return join(iterable(first, second, rest));
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public Joiner useForNull(final String nullText)
/*     */   {
/* 218 */     Preconditions.checkNotNull(nullText);
/* 219 */     return new Joiner(this, nullText) {
/*     */       CharSequence toString(@Nullable Object part) {
/* 221 */         return part == null ? nullText : Joiner.this.toString(part);
/*     */       }
/*     */ 
/*     */       public Joiner useForNull(String nullText) {
/* 225 */         throw new UnsupportedOperationException("already specified useForNull");
/*     */       }
/*     */ 
/*     */       public Joiner skipNulls() {
/* 229 */         throw new UnsupportedOperationException("already specified useForNull");
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public Joiner skipNulls()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 13	com/newrelic/com/google/common/base/Joiner$2
/*     */     //   3: dup
/*     */     //   4: aload_0
/*     */     //   5: aload_0
/*     */     //   6: invokespecial 150	com/newrelic/com/google/common/base/Joiner$2:<init>	(Lcom/newrelic/com/google/common/base/Joiner;Lcom/newrelic/com/google/common/base/Joiner;)V
/*     */     //   9: areturn
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public MapJoiner withKeyValueSeparator(String keyValueSeparator)
/*     */   {
/* 278 */     return new MapJoiner(this, keyValueSeparator, null);
/*     */   }
/*     */ 
/*     */   CharSequence toString(Object part)
/*     */   {
/* 433 */     Preconditions.checkNotNull(part);
/* 434 */     return (part instanceof CharSequence) ? (CharSequence)part : part.toString();
/*     */   }
/*     */ 
/*     */   private static Iterable<Object> iterable(final Object first, final Object second, Object[] rest)
/*     */   {
/* 439 */     Preconditions.checkNotNull(rest);
/* 440 */     return new AbstractList() {
/*     */       public int size() {
/* 442 */         return this.val$rest.length + 2;
/*     */       }
/*     */ 
/*     */       public Object get(int index) {
/* 446 */         switch (index) {
/*     */         case 0:
/* 448 */           return first;
/*     */         case 1:
/* 450 */           return second;
/*     */         }
/* 452 */         return this.val$rest[(index - 2)];
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static final class MapJoiner
/*     */   {
/*     */     private final Joiner joiner;
/*     */     private final String keyValueSeparator;
/*     */ 
/*     */     private MapJoiner(Joiner joiner, String keyValueSeparator)
/*     */     {
/* 304 */       this.joiner = joiner;
/* 305 */       this.keyValueSeparator = ((String)Preconditions.checkNotNull(keyValueSeparator));
/*     */     }
/*     */ 
/*     */     public <A extends Appendable> A appendTo(A appendable, Map<?, ?> map)
/*     */       throws IOException
/*     */     {
/* 313 */       return appendTo(appendable, map.entrySet());
/*     */     }
/*     */ 
/*     */     public StringBuilder appendTo(StringBuilder builder, Map<?, ?> map)
/*     */     {
/* 322 */       return appendTo(builder, map.entrySet());
/*     */     }
/*     */ 
/*     */     public String join(Map<?, ?> map)
/*     */     {
/* 330 */       return join(map.entrySet());
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public <A extends Appendable> A appendTo(A appendable, Iterable<? extends Map.Entry<?, ?>> entries)
/*     */       throws IOException
/*     */     {
/* 342 */       return appendTo(appendable, entries.iterator());
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public <A extends Appendable> A appendTo(A appendable, Iterator<? extends Map.Entry<?, ?>> parts)
/*     */       throws IOException
/*     */     {
/* 354 */       Preconditions.checkNotNull(appendable);
/* 355 */       if (parts.hasNext()) {
/* 356 */         Map.Entry entry = (Map.Entry)parts.next();
/* 357 */         appendable.append(this.joiner.toString(entry.getKey()));
/* 358 */         appendable.append(this.keyValueSeparator);
/* 359 */         appendable.append(this.joiner.toString(entry.getValue()));
/* 360 */         while (parts.hasNext()) {
/* 361 */           appendable.append(this.joiner.separator);
/* 362 */           Map.Entry e = (Map.Entry)parts.next();
/* 363 */           appendable.append(this.joiner.toString(e.getKey()));
/* 364 */           appendable.append(this.keyValueSeparator);
/* 365 */           appendable.append(this.joiner.toString(e.getValue()));
/*     */         }
/*     */       }
/* 368 */       return appendable;
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public StringBuilder appendTo(StringBuilder builder, Iterable<? extends Map.Entry<?, ?>> entries)
/*     */     {
/* 380 */       return appendTo(builder, entries.iterator());
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public StringBuilder appendTo(StringBuilder builder, Iterator<? extends Map.Entry<?, ?>> entries)
/*     */     {
/*     */       try
/*     */       {
/* 393 */         appendTo(builder, entries);
/*     */       } catch (IOException impossible) {
/* 395 */         throw new AssertionError(impossible);
/*     */       }
/* 397 */       return builder;
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public String join(Iterable<? extends Map.Entry<?, ?>> entries)
/*     */     {
/* 408 */       return join(entries.iterator());
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public String join(Iterator<? extends Map.Entry<?, ?>> entries)
/*     */     {
/* 419 */       return appendTo(new StringBuilder(), entries).toString();
/*     */     }
/*     */ 
/*     */     @CheckReturnValue
/*     */     public MapJoiner useForNull(String nullText)
/*     */     {
/* 428 */       return new MapJoiner(this.joiner.useForNull(nullText), this.keyValueSeparator);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.Joiner
 * JD-Core Version:    0.6.2
 */