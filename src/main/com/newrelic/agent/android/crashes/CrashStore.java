package com.newrelic.agent.android.crashes;

import com.newrelic.agent.android.harvest.crash.Crash;
import java.util.List;

public abstract interface CrashStore
{
  public abstract void store(Crash paramCrash);

  public abstract List<Crash> fetchAll();

  public abstract int count();

  public abstract void clear();

  public abstract void delete(Crash paramCrash);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.crashes.CrashStore
 * JD-Core Version:    0.6.2
 */