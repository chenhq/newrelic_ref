package com.newrelic.org.apache.commons.io.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public abstract interface IOFileFilter extends FileFilter, FilenameFilter
{
  public abstract boolean accept(File paramFile);

  public abstract boolean accept(File paramFile, String paramString);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.IOFileFilter
 * JD-Core Version:    0.6.2
 */