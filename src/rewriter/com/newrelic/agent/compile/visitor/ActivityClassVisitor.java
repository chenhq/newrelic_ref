/*    */ package com.newrelic.agent.compile.visitor;
/*    */ 
/*    */ import com.newrelic.agent.compile.InstrumentationContext;
/*    */ import com.newrelic.agent.compile.Log;
/*    */ import com.newrelic.com.google.common.collect.ImmutableMap;
/*    */ import com.newrelic.com.google.common.collect.ImmutableSet;
/*    */ import com.newrelic.objectweb.asm.ClassVisitor;
/*    */ import com.newrelic.objectweb.asm.MethodVisitor;
/*    */ import com.newrelic.objectweb.asm.Type;
/*    */ import com.newrelic.objectweb.asm.commons.GeneratorAdapter;
/*    */ import com.newrelic.objectweb.asm.commons.Method;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class ActivityClassVisitor extends EventHookClassVisitor
/*    */ {
/* 23 */   static final ImmutableSet<String> ACTIVITY_CLASS_NAMES = ImmutableSet.of("android/app/Activity", "android/app/Fragment", "android/accounts/AccountAuthenticatorActivity", "android/app/ActivityGroup", "android/app/TabActivity", "android/app/AliasActivity", new String[] { "android/app/ExpandableListActivity", "android/app/ListActivity", "android/app/LauncherActivity", "android/preference/PreferenceActivity", "android/app/NativeActivity", "android/support/v4/app/FragmentActivity", "android/support/v4/app/Fragment", "android/support/v4/app/DialogFragment", "android/support/v4/app/ListFragment", "android/support/v7/app/ActionBarActivity" });
/*    */ 
/* 41 */   static final Type applicationStateMonitorType = Type.getObjectType("com/newrelic/agent/android/background/ApplicationStateMonitor");
/*    */ 
/* 44 */   public static final ImmutableMap<String, String> traceMethodMap = ImmutableMap.of("onCreate", "(Landroid/os/Bundle;)V", "onCreateView", "(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;");
/*    */ 
/* 50 */   public static final ImmutableSet<String> startTracingOn = ImmutableSet.of("onCreate");
/*    */ 
/*    */   public ActivityClassVisitor(ClassVisitor cv, InstrumentationContext context, Log log)
/*    */   {
/* 55 */     super(cv, context, log, ACTIVITY_CLASS_NAMES, ImmutableMap.of(new Method("onStart", "()V"), new Method("activityStarted", "()V"), new Method("onStop", "()V"), new Method("activityStopped", "()V")));
/*    */   }
/*    */ 
/*    */   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
/*    */   {
/* 63 */     if (this.baseClasses.contains(superName)) {
/* 64 */       interfaces = TraceClassDecorator.addInterface(interfaces);
/*    */     }
/*    */ 
/* 67 */     super.visit(version, access, name, signature, superName, interfaces);
/*    */   }
/*    */ 
/*    */   protected void injectCodeIntoMethod(GeneratorAdapter generatorAdapter, Method method, Method monitorMethod)
/*    */   {
/* 72 */     generatorAdapter.invokeStatic(applicationStateMonitorType, new Method("getInstance", applicationStateMonitorType, new Type[0]));
/*    */ 
/* 74 */     generatorAdapter.invokeVirtual(applicationStateMonitorType, monitorMethod);
/*    */   }
/*    */ 
/*    */   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*    */   {
/* 80 */     if (ACTIVITY_CLASS_NAMES.contains(this.context.getClassName())) {
/* 81 */       return super.visitMethod(access, name, desc, signature, exceptions);
/*    */     }
/*    */ 
/* 84 */     if ((this.instrument) && (traceMethodMap.containsKey(name)) && (((String)traceMethodMap.get(name)).equals(desc))) {
/* 85 */       MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
/* 86 */       TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor(methodVisitor, access, name, desc, this.context);
/*    */ 
/* 88 */       if (startTracingOn.contains(name)) {
/* 89 */         traceMethodVisitor.setStartTracing();
/*    */       }
/*    */ 
/* 92 */       return traceMethodVisitor;
/*    */     }
/*    */ 
/* 95 */     return super.visitMethod(access, name, desc, signature, exceptions);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.visitor.ActivityClassVisitor
 * JD-Core Version:    0.6.2
 */