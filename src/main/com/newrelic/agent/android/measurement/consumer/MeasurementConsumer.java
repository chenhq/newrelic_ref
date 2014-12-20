package com.newrelic.agent.android.measurement.consumer;

import com.newrelic.agent.android.measurement.Measurement;
import com.newrelic.agent.android.measurement.MeasurementType;
import java.util.Collection;

public abstract interface MeasurementConsumer
{
  public abstract MeasurementType getMeasurementType();

  public abstract void consumeMeasurement(Measurement paramMeasurement);

  public abstract void consumeMeasurements(Collection<Measurement> paramCollection);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.consumer.MeasurementConsumer
 * JD-Core Version:    0.6.2
 */