package com.newrelic.agent.android;

import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.harvest.ApplicationInformation;
import com.newrelic.agent.android.harvest.DeviceInformation;
import com.newrelic.agent.android.harvest.EnvironmentInformation;
import com.newrelic.agent.android.util.Encoder;
import java.util.List;

public abstract interface AgentImpl
{
  public abstract void addTransactionData(TransactionData paramTransactionData);

  public abstract List<TransactionData> getAndClearTransactionData();

  public abstract void mergeTransactionData(List<TransactionData> paramList);

  public abstract String getCrossProcessId();

  public abstract int getStackTraceLimit();

  public abstract int getResponseBodyLimit();

  public abstract void start();

  public abstract void stop();

  public abstract void disable();

  public abstract boolean isDisabled();

  public abstract String getNetworkCarrier();

  public abstract String getNetworkWanType();

  public abstract void setLocation(String paramString1, String paramString2);

  public abstract Encoder getEncoder();

  public abstract DeviceInformation getDeviceInformation();

  public abstract ApplicationInformation getApplicationInformation();

  public abstract EnvironmentInformation getEnvironmentInformation();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.AgentImpl
 * JD-Core Version:    0.6.2
 */