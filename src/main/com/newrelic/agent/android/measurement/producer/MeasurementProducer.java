package com.newrelic.agent.android.measurement.producer;

import com.newrelic.agent.android.measurement.Measurement;
import com.newrelic.agent.android.measurement.MeasurementType;
import java.util.Collection;

public abstract interface MeasurementProducer
{
  public abstract MeasurementType getMeasurementType();

  public abstract void produceMeasurement(Measurement paramMeasurement);

  public abstract void produceMeasurements(Collection<Measurement> paramCollection);

  public abstract Collection<Measurement> drainMeasurements();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.producer.MeasurementProducer
 * JD-Core Version:    0.6.2
 */