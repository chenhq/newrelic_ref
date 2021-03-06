package com.newrelic.org.slf4j;

public abstract interface Logger
{
  public static final String ROOT_LOGGER_NAME = "ROOT";

  public abstract String getName();

  public abstract boolean isTraceEnabled();

  public abstract void trace(String paramString);

  public abstract void trace(String paramString, Object paramObject);

  public abstract void trace(String paramString, Object paramObject1, Object paramObject2);

  public abstract void trace(String paramString, Object[] paramArrayOfObject);

  public abstract void trace(String paramString, Throwable paramThrowable);

  public abstract boolean isTraceEnabled(Marker paramMarker);

  public abstract void trace(Marker paramMarker, String paramString);

  public abstract void trace(Marker paramMarker, String paramString, Object paramObject);

  public abstract void trace(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2);

  public abstract void trace(Marker paramMarker, String paramString, Object[] paramArrayOfObject);

  public abstract void trace(Marker paramMarker, String paramString, Throwable paramThrowable);

  public abstract boolean isDebugEnabled();

  public abstract void debug(String paramString);

  public abstract void debug(String paramString, Object paramObject);

  public abstract void debug(String paramString, Object paramObject1, Object paramObject2);

  public abstract void debug(String paramString, Object[] paramArrayOfObject);

  public abstract void debug(String paramString, Throwable paramThrowable);

  public abstract boolean isDebugEnabled(Marker paramMarker);

  public abstract void debug(Marker paramMarker, String paramString);

  public abstract void debug(Marker paramMarker, String paramString, Object paramObject);

  public abstract void debug(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2);

  public abstract void debug(Marker paramMarker, String paramString, Object[] paramArrayOfObject);

  public abstract void debug(Marker paramMarker, String paramString, Throwable paramThrowable);

  public abstract boolean isInfoEnabled();

  public abstract void info(String paramString);

  public abstract void info(String paramString, Object paramObject);

  public abstract void info(String paramString, Object paramObject1, Object paramObject2);

  public abstract void info(String paramString, Object[] paramArrayOfObject);

  public abstract void info(String paramString, Throwable paramThrowable);

  public abstract boolean isInfoEnabled(Marker paramMarker);

  public abstract void info(Marker paramMarker, String paramString);

  public abstract void info(Marker paramMarker, String paramString, Object paramObject);

  public abstract void info(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2);

  public abstract void info(Marker paramMarker, String paramString, Object[] paramArrayOfObject);

  public abstract void info(Marker paramMarker, String paramString, Throwable paramThrowable);

  public abstract boolean isWarnEnabled();

  public abstract void warn(String paramString);

  public abstract void warn(String paramString, Object paramObject);

  public abstract void warn(String paramString, Object[] paramArrayOfObject);

  public abstract void warn(String paramString, Object paramObject1, Object paramObject2);

  public abstract void warn(String paramString, Throwable paramThrowable);

  public abstract boolean isWarnEnabled(Marker paramMarker);

  public abstract void warn(Marker paramMarker, String paramString);

  public abstract void warn(Marker paramMarker, String paramString, Object paramObject);

  public abstract void warn(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2);

  public abstract void warn(Marker paramMarker, String paramString, Object[] paramArrayOfObject);

  public abstract void warn(Marker paramMarker, String paramString, Throwable paramThrowable);

  public abstract boolean isErrorEnabled();

  public abstract void error(String paramString);

  public abstract void error(String paramString, Object paramObject);

  public abstract void error(String paramString, Object paramObject1, Object paramObject2);

  public abstract void error(String paramString, Object[] paramArrayOfObject);

  public abstract void error(String paramString, Throwable paramThrowable);

  public abstract boolean isErrorEnabled(Marker paramMarker);

  public abstract void error(Marker paramMarker, String paramString);

  public abstract void error(Marker paramMarker, String paramString, Object paramObject);

  public abstract void error(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2);

  public abstract void error(Marker paramMarker, String paramString, Object[] paramArrayOfObject);

  public abstract void error(Marker paramMarker, String paramString, Throwable paramThrowable);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.Logger
 * JD-Core Version:    0.6.2
 */