/*     */ package com.newrelic.agent.android.metric;
/*     */ 
/*     */ import com.newrelic.agent.android.harvest.type.HarvestableObject;
/*     */ import com.newrelic.com.google.gson.JsonElement;
/*     */ import com.newrelic.com.google.gson.JsonObject;
/*     */ import com.newrelic.com.google.gson.JsonPrimitive;
/*     */ 
/*     */ public class Metric extends HarvestableObject
/*     */ {
/*     */   private String name;
/*     */   private String scope;
/*     */   private Double min;
/*     */   private Double max;
/*     */   private Double total;
/*     */   private Double sumOfSquares;
/*     */   private Double exclusive;
/*     */   private long count;
/*     */ 
/*     */   public Metric(String name)
/*     */   {
/*  27 */     this(name, null);
/*     */   }
/*     */ 
/*     */   public Metric(String name, String scope)
/*     */   {
/*  33 */     this.name = name;
/*  34 */     this.scope = scope;
/*     */ 
/*  36 */     this.count = 0L;
/*     */   }
/*     */ 
/*     */   public Metric(Metric metric) {
/*  40 */     this.name = metric.getName();
/*  41 */     this.scope = metric.getScope();
/*  42 */     this.min = Double.valueOf(metric.getMin());
/*  43 */     this.max = Double.valueOf(metric.getMax());
/*  44 */     this.total = Double.valueOf(metric.getTotal());
/*  45 */     this.sumOfSquares = Double.valueOf(metric.getSumOfSquares());
/*  46 */     this.exclusive = Double.valueOf(metric.getExclusive());
/*  47 */     this.count = metric.getCount();
/*     */   }
/*     */ 
/*     */   public void sample(double value) {
/*  51 */     this.count += 1L;
/*     */ 
/*  53 */     if (this.total == null) {
/*  54 */       this.total = Double.valueOf(value);
/*  55 */       this.sumOfSquares = Double.valueOf(value * value);
/*     */     } else {
/*  57 */       this.total = Double.valueOf(this.total.doubleValue() + value);
/*  58 */       this.sumOfSquares = Double.valueOf(this.sumOfSquares.doubleValue() + value * value);
/*     */     }
/*     */ 
/*  61 */     setMin(Double.valueOf(value));
/*  62 */     setMax(Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   public void setMin(Double value) {
/*  66 */     if (value == null) {
/*  67 */       return;
/*     */     }
/*  69 */     if (this.min == null) {
/*  70 */       this.min = value;
/*     */     }
/*  72 */     else if (value.doubleValue() < this.min.doubleValue())
/*  73 */       this.min = value;
/*     */   }
/*     */ 
/*     */   public void setMinFieldValue(Double value)
/*     */   {
/*  79 */     this.min = value;
/*     */   }
/*     */ 
/*     */   public void setMax(Double value) {
/*  83 */     if (value == null) {
/*  84 */       return;
/*     */     }
/*  86 */     if (this.max == null) {
/*  87 */       this.max = value;
/*     */     }
/*  89 */     else if (value.doubleValue() > this.max.doubleValue())
/*  90 */       this.max = value;
/*     */   }
/*     */ 
/*     */   public void setMaxFieldValue(Double value)
/*     */   {
/*  96 */     this.max = value;
/*     */   }
/*     */ 
/*     */   public void aggregate(Metric metric) {
/* 100 */     if (metric == null) {
/* 101 */       return;
/*     */     }
/* 103 */     increment(metric.getCount());
/*     */ 
/* 105 */     if (metric.isCountOnly()) {
/* 106 */       return;
/*     */     }
/*     */ 
/* 109 */     this.total = Double.valueOf(this.total == null ? metric.getTotal() : this.total.doubleValue() + metric.getTotal());
/* 110 */     this.sumOfSquares = Double.valueOf(this.sumOfSquares == null ? metric.getSumOfSquares() : this.sumOfSquares.doubleValue() + metric.getSumOfSquares());
/* 111 */     this.exclusive = Double.valueOf(this.exclusive == null ? metric.getExclusive() : this.exclusive.doubleValue() + metric.getExclusive());
/*     */ 
/* 113 */     setMin(Double.valueOf(metric.getMin()));
/* 114 */     setMax(Double.valueOf(metric.getMax()));
/*     */   }
/*     */ 
/*     */   public void increment(long value) {
/* 118 */     this.count += value;
/*     */   }
/*     */ 
/*     */   public void increment() {
/* 122 */     increment(1L);
/*     */   }
/*     */ 
/*     */   public double getSumOfSquares() {
/* 126 */     return this.sumOfSquares == null ? 0.0D : this.sumOfSquares.doubleValue();
/*     */   }
/*     */ 
/*     */   public long getCount() {
/* 130 */     return this.count;
/*     */   }
/*     */ 
/*     */   public double getExclusive() {
/* 134 */     return this.exclusive == null ? 0.0D : this.exclusive.doubleValue();
/*     */   }
/*     */ 
/*     */   public void addExclusive(double value) {
/* 138 */     if (this.exclusive == null)
/* 139 */       this.exclusive = Double.valueOf(value);
/*     */     else
/* 141 */       this.exclusive = Double.valueOf(this.exclusive.doubleValue() + value);
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 146 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/* 150 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public String getScope() {
/* 154 */     return this.scope;
/*     */   }
/*     */ 
/*     */   public String getStringScope() {
/* 158 */     return this.scope == null ? "" : this.scope;
/*     */   }
/*     */ 
/*     */   public void setScope(String scope) {
/* 162 */     this.scope = scope;
/*     */   }
/*     */ 
/*     */   public double getMin() {
/* 166 */     return this.min == null ? 0.0D : this.min.doubleValue();
/*     */   }
/*     */ 
/*     */   public double getMax() {
/* 170 */     return this.max == null ? 0.0D : this.max.doubleValue();
/*     */   }
/*     */ 
/*     */   public double getTotal() {
/* 174 */     return this.total == null ? 0.0D : this.total.doubleValue();
/*     */   }
/*     */ 
/*     */   public void setTotal(Double total)
/*     */   {
/* 179 */     this.total = total;
/*     */   }
/*     */ 
/*     */   public void setSumOfSquares(Double sumOfSquares) {
/* 183 */     this.sumOfSquares = sumOfSquares;
/*     */   }
/*     */ 
/*     */   public void setExclusive(Double exclusive) {
/* 187 */     this.exclusive = exclusive;
/*     */   }
/*     */ 
/*     */   public void setCount(long count) {
/* 191 */     this.count = count;
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 195 */     this.min = null;
/* 196 */     this.max = null;
/* 197 */     this.total = null;
/* 198 */     this.sumOfSquares = null;
/* 199 */     this.exclusive = null;
/* 200 */     this.count = 0L;
/*     */   }
/*     */ 
/*     */   public boolean isCountOnly() {
/* 204 */     return this.total == null;
/*     */   }
/*     */ 
/*     */   public boolean isScoped() {
/* 208 */     return this.scope != null;
/*     */   }
/*     */ 
/*     */   public boolean isUnscoped() {
/* 212 */     return this.scope == null;
/*     */   }
/*     */ 
/*     */   public JsonElement asJson()
/*     */   {
/* 217 */     if (isCountOnly()) {
/* 218 */       return new JsonPrimitive(Long.valueOf(this.count));
/*     */     }
/* 220 */     return asJsonObject();
/*     */   }
/*     */ 
/*     */   public JsonObject asJsonObject()
/*     */   {
/* 226 */     JsonObject jsonObject = new JsonObject();
/*     */ 
/* 228 */     jsonObject.add("count", new JsonPrimitive(Long.valueOf(this.count)));
/* 229 */     if (this.total != null)
/* 230 */       jsonObject.add("total", new JsonPrimitive(this.total));
/* 231 */     if (this.min != null)
/* 232 */       jsonObject.add("min", new JsonPrimitive(this.min));
/* 233 */     if (this.max != null)
/* 234 */       jsonObject.add("max", new JsonPrimitive(this.max));
/* 235 */     if (this.sumOfSquares != null)
/* 236 */       jsonObject.add("sum_of_squares", new JsonPrimitive(this.sumOfSquares));
/* 237 */     if (this.exclusive != null) {
/* 238 */       jsonObject.add("exclusive", new JsonPrimitive(this.exclusive));
/*     */     }
/* 240 */     return jsonObject;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 245 */     return "Metric{count=" + this.count + ", total=" + this.total + ", max=" + this.max + ", min=" + this.min + ", scope='" + this.scope + '\'' + ", name='" + this.name + '\'' + ", exclusive='" + this.exclusive + '\'' + ", sumofsquares='" + this.sumOfSquares + '\'' + '}';
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.metric.Metric
 * JD-Core Version:    0.6.2
 */