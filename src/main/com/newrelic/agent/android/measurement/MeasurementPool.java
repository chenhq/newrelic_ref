/*     */ package com.newrelic.agent.android.measurement;
/*     */ 
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.measurement.consumer.MeasurementConsumer;
/*     */ import com.newrelic.agent.android.measurement.producer.BaseMeasurementProducer;
/*     */ import com.newrelic.agent.android.measurement.producer.MeasurementProducer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class MeasurementPool extends BaseMeasurementProducer
/*     */   implements MeasurementConsumer
/*     */ {
/*  31 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */ 
/*  33 */   private final Collection<MeasurementProducer> producers = new ArrayList();
/*  34 */   private final Collection<MeasurementConsumer> consumers = new ArrayList();
/*     */ 
/*     */   public MeasurementPool() {
/*  37 */     super(MeasurementType.Any);
/*  38 */     addMeasurementProducer(this);
/*     */   }
/*     */ 
/*     */   public void addMeasurementProducer(MeasurementProducer producer)
/*     */   {
/*  46 */     synchronized (this.producers) {
/*  47 */       if (this.producers.contains(producer)) {
/*  48 */         log.debug("Attempted to add the same MeasurementProducer " + producer + "  multiple times.");
/*  49 */         return;
/*     */       }
/*  51 */       this.producers.add(producer);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeMeasurementProducer(MeasurementProducer producer)
/*     */   {
/*  60 */     synchronized (this.producers) {
/*  61 */       if (!this.producers.contains(producer)) {
/*  62 */         log.debug("Attempted to remove MeasurementProducer " + producer + " which is not registered.");
/*  63 */         return;
/*     */       }
/*  65 */       this.producers.remove(producer);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addMeasurementConsumer(MeasurementConsumer consumer)
/*     */   {
/*  74 */     synchronized (this.consumers) {
/*  75 */       if (this.consumers.contains(consumer)) {
/*  76 */         log.debug("Attempted to add the same MeasurementConsumer " + consumer + " multiple times.");
/*  77 */         return;
/*     */       }
/*  79 */       this.consumers.add(consumer);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeMeasurementConsumer(MeasurementConsumer consumer)
/*     */   {
/*  88 */     synchronized (this.consumers)
/*     */     {
/*  90 */       if (!this.consumers.contains(consumer)) {
/*  91 */         log.debug("Attempted to remove MeasurementConsumer " + consumer + " which is not registered.");
/*  92 */         return;
/*     */       }
/*  94 */       this.consumers.remove(consumer);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void broadcastMeasurements()
/*     */   {
/* 102 */     List allProducedMeasurements = new ArrayList();
/*     */ 
/* 105 */     synchronized (this.producers) {
/* 106 */       for (MeasurementProducer producer : this.producers) {
/* 107 */         Collection measurements = producer.drainMeasurements();
/* 108 */         if (measurements.size() > 0)
/* 109 */           allProducedMeasurements.addAll(measurements);
/*     */       }
/*     */     }
/*     */     Iterator i$;
/*     */     MeasurementConsumer consumer;
/* 115 */     synchronized (this.consumers) {
/* 116 */       for (i$ = this.consumers.iterator(); i$.hasNext(); ) { consumer = (MeasurementConsumer)i$.next();
/*     */ 
/* 118 */         List measurements = new ArrayList(allProducedMeasurements);
/*     */ 
/* 121 */         for (Measurement measurement : measurements)
/* 122 */           if ((consumer.getMeasurementType() == measurement.getType()) || (consumer.getMeasurementType() == MeasurementType.Any))
/* 123 */             consumer.consumeMeasurement(measurement);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void consumeMeasurement(Measurement measurement)
/*     */   {
/* 132 */     produceMeasurement(measurement);
/*     */   }
/*     */ 
/*     */   public void consumeMeasurements(Collection<Measurement> measurements)
/*     */   {
/* 137 */     produceMeasurements(measurements);
/*     */   }
/*     */ 
/*     */   public MeasurementType getMeasurementType()
/*     */   {
/* 142 */     return MeasurementType.Any;
/*     */   }
/*     */ 
/*     */   public Collection<MeasurementProducer> getMeasurementProducers() {
/* 146 */     return this.producers;
/*     */   }
/*     */ 
/*     */   public Collection<MeasurementConsumer> getMeasurementConsumers() {
/* 150 */     return this.consumers;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.MeasurementPool
 * JD-Core Version:    0.6.2
 */