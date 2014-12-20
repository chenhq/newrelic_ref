package com.newrelic.agent.android.harvest;

public abstract interface HarvestLifecycleAware
{
  public abstract void onHarvestStart();

  public abstract void onHarvestStop();

  public abstract void onHarvestBefore();

  public abstract void onHarvest();

  public abstract void onHarvestFinalize();

  public abstract void onHarvestError();

  public abstract void onHarvestSendFailed();

  public abstract void onHarvestComplete();

  public abstract void onHarvestConnected();

  public abstract void onHarvestDisconnected();

  public abstract void onHarvestDisabled();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.HarvestLifecycleAware
 * JD-Core Version:    0.6.2
 */