/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ import java.util.Queue;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class TreeTraverser<T>
/*     */ {
/*     */   public abstract Iterable<T> children(T paramT);
/*     */ 
/*     */   public final FluentIterable<T> preOrderTraversal(final T root)
/*     */   {
/*  70 */     Preconditions.checkNotNull(root);
/*  71 */     return new FluentIterable()
/*     */     {
/*     */       public UnmodifiableIterator<T> iterator() {
/*  74 */         return TreeTraverser.this.preOrderIterator(root);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   UnmodifiableIterator<T> preOrderIterator(T root)
/*     */   {
/*  81 */     return new PreOrderIterator(root);
/*     */   }
/*     */ 
/*     */   public final FluentIterable<T> postOrderTraversal(final T root)
/*     */   {
/* 120 */     Preconditions.checkNotNull(root);
/* 121 */     return new FluentIterable()
/*     */     {
/*     */       public UnmodifiableIterator<T> iterator() {
/* 124 */         return TreeTraverser.this.postOrderIterator(root);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   UnmodifiableIterator<T> postOrderIterator(T root)
/*     */   {
/* 131 */     return new PostOrderIterator(root);
/*     */   }
/*     */ 
/*     */   public final FluentIterable<T> breadthFirstTraversal(final T root)
/*     */   {
/* 180 */     Preconditions.checkNotNull(root);
/* 181 */     return new FluentIterable()
/*     */     {
/*     */       public UnmodifiableIterator<T> iterator() {
/* 184 */         return new TreeTraverser.BreadthFirstIterator(TreeTraverser.this, root);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private final class BreadthFirstIterator extends UnmodifiableIterator<T> implements PeekingIterator<T>
/*     */   {
/*     */     private final Queue<T> queue;
/*     */ 
/*     */     BreadthFirstIterator() {
/* 194 */       this.queue = new ArrayDeque();
/* 195 */       this.queue.add(root);
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 200 */       return !this.queue.isEmpty();
/*     */     }
/*     */ 
/*     */     public T peek()
/*     */     {
/* 205 */       return this.queue.element();
/*     */     }
/*     */ 
/*     */     public T next()
/*     */     {
/* 210 */       Object result = this.queue.remove();
/* 211 */       Iterables.addAll(this.queue, TreeTraverser.this.children(result));
/* 212 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class PostOrderIterator extends AbstractIterator<T>
/*     */   {
/*     */     private final ArrayDeque<TreeTraverser.PostOrderNode<T>> stack;
/*     */ 
/*     */     PostOrderIterator()
/*     */     {
/* 148 */       this.stack = new ArrayDeque();
/* 149 */       this.stack.addLast(expand(root));
/*     */     }
/*     */ 
/*     */     protected T computeNext()
/*     */     {
/* 154 */       while (!this.stack.isEmpty()) {
/* 155 */         TreeTraverser.PostOrderNode top = (TreeTraverser.PostOrderNode)this.stack.getLast();
/* 156 */         if (top.childIterator.hasNext()) {
/* 157 */           Object child = top.childIterator.next();
/* 158 */           this.stack.addLast(expand(child));
/*     */         } else {
/* 160 */           this.stack.removeLast();
/* 161 */           return top.root;
/*     */         }
/*     */       }
/* 164 */       return endOfData();
/*     */     }
/*     */ 
/*     */     private TreeTraverser.PostOrderNode<T> expand(T t) {
/* 168 */       return new TreeTraverser.PostOrderNode(t, TreeTraverser.this.children(t).iterator());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class PostOrderNode<T>
/*     */   {
/*     */     final T root;
/*     */     final Iterator<T> childIterator;
/*     */ 
/*     */     PostOrderNode(T root, Iterator<T> childIterator)
/*     */     {
/* 139 */       this.root = Preconditions.checkNotNull(root);
/* 140 */       this.childIterator = ((Iterator)Preconditions.checkNotNull(childIterator));
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class PreOrderIterator extends UnmodifiableIterator<T>
/*     */   {
/*     */     private final Deque<Iterator<T>> stack;
/*     */ 
/*     */     PreOrderIterator()
/*     */     {
/*  88 */       this.stack = new ArrayDeque();
/*  89 */       this.stack.addLast(Iterators.singletonIterator(Preconditions.checkNotNull(root)));
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/*  94 */       return !this.stack.isEmpty();
/*     */     }
/*     */ 
/*     */     public T next()
/*     */     {
/*  99 */       Iterator itr = (Iterator)this.stack.getLast();
/* 100 */       Object result = Preconditions.checkNotNull(itr.next());
/* 101 */       if (!itr.hasNext()) {
/* 102 */         this.stack.removeLast();
/*     */       }
/* 104 */       Iterator childItr = TreeTraverser.this.children(result).iterator();
/* 105 */       if (childItr.hasNext()) {
/* 106 */         this.stack.addLast(childItr);
/*     */       }
/* 108 */       return result;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.TreeTraverser
 * JD-Core Version:    0.6.2
 */