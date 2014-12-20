/*     */ package com.newrelic.com.google.common.io;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.base.Ascii;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.base.Splitter;
/*     */ import com.newrelic.com.google.common.collect.AbstractIterator;
/*     */ import com.newrelic.com.google.common.collect.ImmutableList;
/*     */ import com.newrelic.com.google.common.collect.Lists;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public abstract class CharSource
/*     */   implements InputSupplier<Reader>
/*     */ {
/*     */   public abstract Reader openStream()
/*     */     throws IOException;
/*     */ 
/*     */   @Deprecated
/*     */   public final Reader getInput()
/*     */     throws IOException
/*     */   {
/*  94 */     return openStream();
/*     */   }
/*     */ 
/*     */   public BufferedReader openBufferedStream()
/*     */     throws IOException
/*     */   {
/* 106 */     Reader reader = openStream();
/* 107 */     return (reader instanceof BufferedReader) ? (BufferedReader)reader : new BufferedReader(reader);
/*     */   }
/*     */ 
/*     */   public long copyTo(Appendable appendable)
/*     */     throws IOException
/*     */   {
/* 120 */     Preconditions.checkNotNull(appendable);
/*     */ 
/* 122 */     Closer closer = Closer.create();
/*     */     try {
/* 124 */       Reader reader = (Reader)closer.register(openStream());
/* 125 */       return CharStreams.copy(reader, appendable);
/*     */     } catch (Throwable e) {
/* 127 */       throw closer.rethrow(e);
/*     */     } finally {
/* 129 */       closer.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public long copyTo(CharSink sink)
/*     */     throws IOException
/*     */   {
/* 140 */     Preconditions.checkNotNull(sink);
/*     */ 
/* 142 */     Closer closer = Closer.create();
/*     */     try {
/* 144 */       Reader reader = (Reader)closer.register(openStream());
/* 145 */       Writer writer = (Writer)closer.register(sink.openStream());
/* 146 */       return CharStreams.copy(reader, writer);
/*     */     } catch (Throwable e) {
/* 148 */       throw closer.rethrow(e);
/*     */     } finally {
/* 150 */       closer.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String read()
/*     */     throws IOException
/*     */   {
/* 160 */     Closer closer = Closer.create();
/*     */     try {
/* 162 */       Reader reader = (Reader)closer.register(openStream());
/* 163 */       return CharStreams.toString(reader);
/*     */     } catch (Throwable e) {
/* 165 */       throw closer.rethrow(e);
/*     */     } finally {
/* 167 */       closer.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public String readFirstLine()
/*     */     throws IOException
/*     */   {
/* 181 */     Closer closer = Closer.create();
/*     */     try {
/* 183 */       BufferedReader reader = (BufferedReader)closer.register(openBufferedStream());
/* 184 */       return reader.readLine();
/*     */     } catch (Throwable e) {
/* 186 */       throw closer.rethrow(e);
/*     */     } finally {
/* 188 */       closer.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public ImmutableList<String> readLines()
/*     */     throws IOException
/*     */   {
/* 203 */     Closer closer = Closer.create();
/*     */     try {
/* 205 */       BufferedReader reader = (BufferedReader)closer.register(openBufferedStream());
/* 206 */       List result = Lists.newArrayList();
/*     */       String line;
/* 208 */       while ((line = reader.readLine()) != null) {
/* 209 */         result.add(line);
/*     */       }
/* 211 */       return ImmutableList.copyOf(result);
/*     */     } catch (Throwable e) {
/* 213 */       throw closer.rethrow(e);
/*     */     } finally {
/* 215 */       closer.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public <T> T readLines(LineProcessor<T> processor)
/*     */     throws IOException
/*     */   {
/* 235 */     Preconditions.checkNotNull(processor);
/*     */ 
/* 237 */     Closer closer = Closer.create();
/*     */     try {
/* 239 */       Reader reader = (Reader)closer.register(openStream());
/* 240 */       return CharStreams.readLines(reader, processor);
/*     */     } catch (Throwable e) {
/* 242 */       throw closer.rethrow(e);
/*     */     } finally {
/* 244 */       closer.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */     throws IOException
/*     */   {
/* 256 */     Closer closer = Closer.create();
/*     */     try {
/* 258 */       Reader reader = (Reader)closer.register(openStream());
/* 259 */       return reader.read() == -1;
/*     */     } catch (Throwable e) {
/* 261 */       throw closer.rethrow(e);
/*     */     } finally {
/* 263 */       closer.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static CharSource concat(Iterable<? extends CharSource> sources)
/*     */   {
/* 279 */     return new ConcatenatedCharSource(sources);
/*     */   }
/*     */ 
/*     */   public static CharSource concat(Iterator<? extends CharSource> sources)
/*     */   {
/* 301 */     return concat(ImmutableList.copyOf(sources));
/*     */   }
/*     */ 
/*     */   public static CharSource concat(CharSource[] sources)
/*     */   {
/* 317 */     return concat(ImmutableList.copyOf(sources));
/*     */   }
/*     */ 
/*     */   public static CharSource wrap(CharSequence charSequence)
/*     */   {
/* 328 */     return new CharSequenceCharSource(charSequence);
/*     */   }
/*     */ 
/*     */   public static CharSource empty()
/*     */   {
/* 337 */     return EmptyCharSource.INSTANCE;
/*     */   }
/*     */ 
/*     */   private static final class ConcatenatedCharSource extends CharSource
/*     */   {
/*     */     private final Iterable<? extends CharSource> sources;
/*     */ 
/*     */     ConcatenatedCharSource(Iterable<? extends CharSource> sources)
/*     */     {
/* 440 */       this.sources = ((Iterable)Preconditions.checkNotNull(sources));
/*     */     }
/*     */ 
/*     */     public Reader openStream() throws IOException
/*     */     {
/* 445 */       return new MultiReader(this.sources.iterator());
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() throws IOException
/*     */     {
/* 450 */       for (CharSource source : this.sources) {
/* 451 */         if (!source.isEmpty()) {
/* 452 */           return false;
/*     */         }
/*     */       }
/* 455 */       return true;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 460 */       return "CharSource.concat(" + this.sources + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class EmptyCharSource extends CharSource.CharSequenceCharSource
/*     */   {
/* 423 */     private static final EmptyCharSource INSTANCE = new EmptyCharSource();
/*     */ 
/*     */     private EmptyCharSource() {
/* 426 */       super();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 431 */       return "CharSource.empty()";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class CharSequenceCharSource extends CharSource
/*     */   {
/* 342 */     private static final Splitter LINE_SPLITTER = Splitter.on(Pattern.compile("\r\n|\n|\r"));
/*     */     private final CharSequence seq;
/*     */ 
/*     */     protected CharSequenceCharSource(CharSequence seq)
/*     */     {
/* 348 */       this.seq = ((CharSequence)Preconditions.checkNotNull(seq));
/*     */     }
/*     */ 
/*     */     public Reader openStream()
/*     */     {
/* 353 */       return new CharSequenceReader(this.seq);
/*     */     }
/*     */ 
/*     */     public String read()
/*     */     {
/* 358 */       return this.seq.toString();
/*     */     }
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 363 */       return this.seq.length() == 0;
/*     */     }
/*     */ 
/*     */     private Iterable<String> lines()
/*     */     {
/* 372 */       return new Iterable()
/*     */       {
/*     */         public Iterator<String> iterator() {
/* 375 */           return new AbstractIterator() {
/* 376 */             Iterator<String> lines = CharSource.CharSequenceCharSource.LINE_SPLITTER.split(CharSource.CharSequenceCharSource.this.seq).iterator();
/*     */ 
/*     */             protected String computeNext()
/*     */             {
/* 380 */               if (this.lines.hasNext()) {
/* 381 */                 String next = (String)this.lines.next();
/*     */ 
/* 383 */                 if ((this.lines.hasNext()) || (!next.isEmpty())) {
/* 384 */                   return next;
/*     */                 }
/*     */               }
/* 387 */               return (String)endOfData();
/*     */             }
/*     */           };
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public String readFirstLine()
/*     */     {
/* 396 */       Iterator lines = lines().iterator();
/* 397 */       return lines.hasNext() ? (String)lines.next() : null;
/*     */     }
/*     */ 
/*     */     public ImmutableList<String> readLines()
/*     */     {
/* 402 */       return ImmutableList.copyOf(lines());
/*     */     }
/*     */ 
/*     */     public <T> T readLines(LineProcessor<T> processor) throws IOException
/*     */     {
/* 407 */       for (String line : lines()) {
/* 408 */         if (!processor.processLine(line)) {
/*     */           break;
/*     */         }
/*     */       }
/* 412 */       return processor.getResult();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 417 */       return "CharSource.wrap(" + Ascii.truncate(this.seq, 30, "...") + ")";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.io.CharSource
 * JD-Core Version:    0.6.2
 */