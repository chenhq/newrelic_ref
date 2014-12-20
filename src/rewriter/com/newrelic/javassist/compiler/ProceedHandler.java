package com.newrelic.javassist.compiler;

import com.newrelic.javassist.bytecode.Bytecode;
import com.newrelic.javassist.compiler.ast.ASTList;

public abstract interface ProceedHandler
{
  public abstract void doit(JvstCodeGen paramJvstCodeGen, Bytecode paramBytecode, ASTList paramASTList)
    throws CompileError;

  public abstract void setReturnType(JvstTypeChecker paramJvstTypeChecker, ASTList paramASTList)
    throws CompileError;
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ProceedHandler
 * JD-Core Version:    0.6.2
 */