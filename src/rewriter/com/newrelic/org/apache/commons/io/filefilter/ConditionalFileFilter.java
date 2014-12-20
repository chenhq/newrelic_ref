package com.newrelic.org.apache.commons.io.filefilter;

import java.util.List;

public abstract interface ConditionalFileFilter
{
  public abstract void addFileFilter(IOFileFilter paramIOFileFilter);

  public abstract List<IOFileFilter> getFileFilters();

  public abstract boolean removeFileFilter(IOFileFilter paramIOFileFilter);

  public abstract void setFileFilters(List<IOFileFilter> paramList);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.ConditionalFileFilter
 * JD-Core Version:    0.6.2
 */