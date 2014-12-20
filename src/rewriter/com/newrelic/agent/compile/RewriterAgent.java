/*      */ package com.newrelic.agent.compile;
/*      */ 
/*      */ import com.newrelic.agent.compile.visitor.ActivityClassVisitor;
/*      */ import com.newrelic.agent.compile.visitor.AnnotatingClassVisitor;
/*      */ import com.newrelic.agent.compile.visitor.AsyncTaskClassVisitor;
/*      */ import com.newrelic.agent.compile.visitor.ContextInitializationClassVisitor;
/*      */ import com.newrelic.agent.compile.visitor.NewRelicClassVisitor;
/*      */ import com.newrelic.agent.compile.visitor.PrefilterClassVisitor;
/*      */ import com.newrelic.agent.compile.visitor.TraceAnnotationClassVisitor;
/*      */ import com.newrelic.agent.compile.visitor.WrapMethodClassVisitor;
/*      */ import com.newrelic.agent.util.Streams;
/*      */ import com.newrelic.objectweb.asm.ClassAdapter;
/*      */ import com.newrelic.objectweb.asm.ClassReader;
/*      */ import com.newrelic.objectweb.asm.ClassVisitor;
/*      */ import com.newrelic.objectweb.asm.ClassWriter;
/*      */ import com.newrelic.objectweb.asm.MethodVisitor;
/*      */ import com.newrelic.objectweb.asm.Type;
/*      */ import com.newrelic.objectweb.asm.commons.AdviceAdapter;
/*      */ import com.newrelic.objectweb.asm.commons.GeneratorAdapter;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.lang.instrument.ClassDefinition;
/*      */ import java.lang.instrument.ClassFileTransformer;
/*      */ import java.lang.instrument.IllegalClassFormatException;
/*      */ import java.lang.instrument.Instrumentation;
/*      */ import java.lang.instrument.UnmodifiableClassException;
/*      */ import java.lang.management.ManagementFactory;
/*      */ import java.lang.management.RuntimeMXBean;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.InvocationHandler;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.URL;
/*      */ import java.security.CodeSource;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.logging.Logger;
/*      */ 
/*      */ public class RewriterAgent
/*      */ {
/*      */   public static final String VERSION = "4.120.0";
/*   48 */   private static final Set<String> DX_COMMAND_NAMES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "dx", "dx.bat" })));
/*      */ 
/*   53 */   private static final Set<String> JAVA_NAMES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "java", "java.exe" })));
/*      */ 
/*   58 */   private static final Set<String> AGENT_JAR_NAMES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "newrelic.android.fat.jar", "newrelic.android.jar", "obfuscated.jar" })));
/*      */   private static final String DISABLE_INSTRUMENTATION_SYSTEM_PROPERTY = "newrelic.instrumentation.disabled";
/*      */   private static final String INVOCATION_DISPATCHER_FIELD_NAME = "treeLock";
/*   63 */   private static final Class INVOCATION_DISPATCHER_CLASS = Logger.class;
/*      */   private static final String SET_INSTRUMENTATION_DISABLED_FLAG = "SET_INSTRUMENTATION_DISABLED_FLAG";
/*      */   private static final String PRINT_TO_INFO_LOG = "PRINT_TO_INFO_LOG";
/*      */   static final String DEXER_MAIN_CLASS_NAME = "com/android/dx/command/dexer/Main";
/*      */   private static final String ANT_DEX_EXEC_TASK = "com/android/ant/DexExecTask";
/*      */   private static final String ECLIPSE_BUILD_HELPER = "com/android/ide/eclipse/adt/internal/build/BuildHelper";
/*      */   private static final String MAVEN_DEX_MOJO = "com/jayway/maven/plugins/android/phase08preparepackage/DexMojo";
/*      */   private static final String PROCESS_BUILDER_CLASS_NAME = "java/lang/ProcessBuilder";
/*      */   private static final String PROCESS_CLASS_METHOD_NAME = "processClass";
/*      */   private static final String EXECUTE_DX_METHOD_NAME = "executeDx";
/*      */   private static final String PRE_DEX_LIBRARIES_METHOD_NAME = "preDexLibraries";
/*      */   private static final String START_METHOD_NAME = "start";
/*      */   private static String agentArgs;
/*   95 */   private static Map<String, String> agentOptions = Collections.emptyMap();
/*      */ 
/*   97 */   private static final HashSet<String> EXCLUDED_PACKAGES = new HashSet() { } ;
/*      */ 
/*      */   public static void agentmain(String agentArgs, Instrumentation instrumentation)
/*      */   {
/*  104 */     premain(agentArgs, instrumentation);
/*      */   }
/*      */ 
/*      */   public static String getVersion() {
/*  108 */     return "4.120.0";
/*      */   }
/*      */ 
/*      */   public static Map<String, String> getAgentOptions() {
/*  112 */     return agentOptions;
/*      */   }
/*      */ 
/*      */   public static void premain(String agentArgs, Instrumentation instrumentation)
/*      */   {
/*  121 */     agentArgs = agentArgs;
/*      */ 
/*  123 */     Throwable argsError = null;
/*      */     try
/*      */     {
/*  126 */       agentOptions = parseAgentArgs(agentArgs);
/*      */     } catch (Throwable t) {
/*  128 */       argsError = t;
/*      */     }
/*      */ 
/*  131 */     String logFileName = (String)agentOptions.get("logfile");
/*      */ 
/*  133 */     Log log = logFileName == null ? new SystemErrLog(agentOptions) : new FileLogImpl(agentOptions, logFileName);
/*  134 */     if (argsError != null) {
/*  135 */       log.error("Agent args error: " + agentArgs, argsError);
/*      */     }
/*  137 */     log.debug("Bootstrapping New Relic Android class rewriter");
/*      */ 
/*  139 */     String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
/*  140 */     int p = nameOfRunningVM.indexOf('@');
/*  141 */     String pid = nameOfRunningVM.substring(0, p);
/*  142 */     log.debug("Agent running in pid " + pid + " arguments: " + agentArgs);
/*      */     try
/*      */     {
/*      */       NewRelicClassTransformer classTransformer;
/*      */       NewRelicClassTransformer classTransformer;
/*  147 */       if (agentOptions.containsKey("deinstrument")) {
/*  148 */         log.info("Deinstrumenting...");
/*  149 */         classTransformer = new NoOpClassTransformer(null);
/*      */       } else {
/*  151 */         classTransformer = new DexClassTransformer(log);
/*  152 */         createInvocationDispatcher(log);
/*      */       }
/*      */ 
/*  155 */       instrumentation.addTransformer(classTransformer, true);
/*      */ 
/*  157 */       List classes = new ArrayList();
/*  158 */       for (Class clazz : instrumentation.getAllLoadedClasses()) {
/*  159 */         if (classTransformer.modifies(clazz)) {
/*  160 */           classes.add(clazz);
/*      */         }
/*      */       }
/*      */ 
/*  164 */       if (!classes.isEmpty()) {
/*  165 */         if (instrumentation.isRetransformClassesSupported()) {
/*  166 */           log.debug("Retransform classes: " + classes);
/*  167 */           instrumentation.retransformClasses((Class[])classes.toArray(new Class[classes.size()]));
/*      */         } else {
/*  169 */           log.error("Unable to retransform classes: " + classes);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  180 */       if (!agentOptions.containsKey("deinstrument"))
/*  181 */         redefineClass(instrumentation, classTransformer, ProcessBuilder.class);
/*      */     }
/*      */     catch (Throwable ex) {
/*  184 */       log.error("Agent startup error", ex);
/*  185 */       throw new RuntimeException(ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void redefineClass(Instrumentation instrumentation, ClassFileTransformer classTransformer, Class<?> klass)
/*      */     throws IOException, IllegalClassFormatException, ClassNotFoundException, UnmodifiableClassException
/*      */   {
/*  192 */     String internalClassName = klass.getName().replace('.', '/');
/*  193 */     String classPath = internalClassName + ".class";
/*      */ 
/*  195 */     ClassLoader cl = klass.getClassLoader() == null ? RewriterAgent.class.getClassLoader() : klass.getClassLoader();
/*  196 */     InputStream stream = cl.getResourceAsStream(classPath);
/*  197 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/*  198 */     Streams.copy(stream, output);
/*      */ 
/*  200 */     stream.close();
/*      */ 
/*  202 */     byte[] newBytes = classTransformer.transform(klass.getClassLoader(), internalClassName, klass, null, output.toByteArray());
/*      */ 
/*  204 */     ClassDefinition def = new ClassDefinition(klass, newBytes);
/*  205 */     instrumentation.redefineClasses(new ClassDefinition[] { def });
/*      */   }
/*      */ 
/*      */   private static Map<String, String> parseAgentArgs(String agentArgs) {
/*  209 */     if (agentArgs == null) {
/*  210 */       return Collections.emptyMap();
/*      */     }
/*  212 */     Map options = new HashMap();
/*  213 */     for (String arg : agentArgs.split(";")) {
/*  214 */       String[] keyValue = arg.split("=");
/*  215 */       if (keyValue.length == 2)
/*  216 */         options.put(keyValue[0], keyValue[1]);
/*      */       else {
/*  218 */         throw new IllegalArgumentException("Invalid argument: " + arg);
/*      */       }
/*      */     }
/*      */ 
/*  222 */     return options;
/*      */   }
/*      */ 
/*      */   private static ClassAdapter createDexerMainClassAdapter(ClassVisitor cw, Log log)
/*      */   {
/*  356 */     return new ClassAdapterBase(log, cw, new HashMap()
/*      */     {
/*      */     });
/*      */   }
/*      */ 
/*      */   private static ClassAdapter createEclipseBuildHelperClassAdapter(ClassVisitor cw, Log log)
/*      */   {
/*  391 */     return new ClassAdapterBase(log, cw, new HashMap()
/*      */     {
/*      */     });
/*      */   }
/*      */ 
/*      */   private static ClassAdapter createAntTaskClassAdapter(ClassVisitor cw, Log log)
/*      */   {
/*  432 */     String agentFileFieldName = "NewRelicAgentFile";
/*  433 */     Map methodVisitors = new HashMap()
/*      */     {
/*      */     };
/*  492 */     return new ClassAdapterBase(log, cw, methodVisitors)
/*      */     {
/*      */       public void visitEnd()
/*      */       {
/*  499 */         super.visitEnd();
/*  500 */         visitField(2, "NewRelicAgentFile", Type.getType(Object.class).getDescriptor(), null, null);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private static ClassAdapter createProcessBuilderClassAdapter(ClassVisitor cw, Log log)
/*      */   {
/*  511 */     return new ClassAdapter(cw)
/*      */     {
/*      */       public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*      */       {
/*  516 */         MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
/*      */ 
/*  518 */         if ("start".equals(name)) {
/*  519 */           mv = new SkipInstrumentedMethodsMethodVisitor(new RewriterAgent.BaseMethodVisitor(mv, access, name, desc)
/*      */           {
/*      */             protected void onMethodEnter()
/*      */             {
/*  525 */               this.builder.loadInvocationDispatcher().loadInvocationDispatcherKey(RewriterAgent.getProxyInvocationKey("java/lang/ProcessBuilder", this.methodName)).loadArray(new Runnable[] { new Runnable()
/*      */               {
/*      */                 public void run()
/*      */                 {
/*  533 */                   RewriterAgent.6.1.this.loadThis();
/*  534 */                   RewriterAgent.6.1.this.invokeVirtual(Type.getObjectType("java/lang/ProcessBuilder"), new com.newrelic.objectweb.asm.commons.Method("command", "()Ljava/util/List;"));
/*      */                 }
/*      */               }
/*      */                }).invokeDispatcher();
/*      */             }
/*      */ 
/*      */           });
/*      */         }
/*      */ 
/*  544 */         return mv;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private static ClassAdapter createMavenClassAdapter(ClassVisitor cw, Log log, String agentJarPath)
/*      */   {
/*  560 */     Map methodVisitors = new HashMap()
/*      */     {
/*      */     };
/*  612 */     return new ClassAdapterBase(log, cw, methodVisitors);
/*      */   }
/*      */ 
/*      */   private static String getAgentJarPath()
/*      */     throws URISyntaxException
/*      */   {
/*  619 */     return new File(RewriterAgent.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
/*      */   }
/*      */ 
/*      */   private static void createInvocationDispatcher(Log log)
/*      */     throws Exception
/*      */   {
/*  807 */     Field field = INVOCATION_DISPATCHER_CLASS.getDeclaredField("treeLock");
/*  808 */     field.setAccessible(true);
/*      */ 
/*  811 */     Field modifiersField = Field.class.getDeclaredField("modifiers");
/*  812 */     modifiersField.setAccessible(true);
/*  813 */     modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
/*      */ 
/*  816 */     if ((field.get(null) instanceof InvocationDispatcher))
/*  817 */       log.info("Detected cached instrumentation.");
/*      */     else
/*  819 */       field.set(null, new InvocationDispatcher(log));
/*      */   }
/*      */ 
/*      */   private static String getProxyInvocationKey(String className, String methodName)
/*      */   {
/*  825 */     return className + "." + methodName;
/*      */   }
/*      */ 
/*      */   private static class InvocationDispatcher
/*      */     implements InvocationHandler
/*      */   {
/*      */     private final Log log;
/*      */     private final ClassRemapperConfig config;
/*      */     private final InstrumentationContext context;
/*      */     private final Map<String, InvocationHandler> invocationHandlers;
/*  843 */     private boolean writeDisabledMessage = true;
/*      */     private final String agentJarPath;
/*  845 */     private boolean disableInstrumentation = false;
/*      */ 
/*      */     public InvocationDispatcher(final Log log)
/*      */       throws FileNotFoundException, IOException, ClassNotFoundException, URISyntaxException
/*      */     {
/*  851 */       this.log = log;
/*  852 */       this.config = new ClassRemapperConfig(log);
/*  853 */       this.context = new InstrumentationContext(this.config, log);
/*  854 */       this.agentJarPath = RewriterAgent.access$100();
/*  855 */       this.invocationHandlers = Collections.unmodifiableMap(new HashMap()
/*      */       {
/*      */       });
/*      */     }
/*      */ 
/*      */     private boolean isInstrumentationDisabled()
/*      */     {
/*  995 */       return (this.disableInstrumentation) || (System.getProperty("newrelic.instrumentation.disabled") != null);
/*      */     }
/*      */ 
/*      */     private boolean isExcludedPackage(String packageName)
/*      */     {
/* 1000 */       for (String name : RewriterAgent.EXCLUDED_PACKAGES) {
/* 1001 */         if (packageName.contains(name)) {
/* 1002 */           return true;
/*      */         }
/*      */       }
/*      */ 
/* 1006 */       return false;
/*      */     }
/*      */ 
/*      */     public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args)
/*      */       throws Throwable
/*      */     {
/* 1012 */       InvocationHandler handler = (InvocationHandler)this.invocationHandlers.get(proxy);
/* 1013 */       if (handler == null) {
/* 1014 */         this.log.error("Unknown invocation type: " + proxy + ".  Arguments: " + Arrays.asList(args));
/* 1015 */         return null;
/*      */       }
/*      */       try {
/* 1018 */         return handler.invoke(proxy, method, args);
/*      */       } catch (Throwable t) {
/* 1020 */         this.log.error("Error:" + t.getMessage(), t);
/* 1021 */       }return null;
/*      */     }
/*      */ 
/*      */     private ClassData visitClassBytes(byte[] bytes)
/*      */     {
/* 1032 */       String className = "an unknown class";
/*      */       try
/*      */       {
/* 1035 */         ClassReader cr = new ClassReader(bytes);
/* 1036 */         ClassWriter cw = new ClassWriter(cr, 1);
/*      */ 
/* 1038 */         this.context.reset();
/*      */ 
/* 1043 */         cr.accept(new PrefilterClassVisitor(this.context, this.log), 7);
/*      */ 
/* 1045 */         className = this.context.getClassName();
/*      */ 
/* 1047 */         if (!this.context.hasTag("Lcom/newrelic/agent/android/instrumentation/Instrumented;"))
/*      */         {
/* 1051 */           ClassVisitor cv = cw;
/*      */ 
/* 1053 */           if (this.context.getClassName().startsWith("com/newrelic/agent/android"))
/*      */           {
/* 1055 */             cv = new NewRelicClassVisitor(cv, this.context, this.log);
/*      */           }
/* 1057 */           else if (this.context.getClassName().startsWith("android/support/"))
/*      */           {
/* 1060 */             cv = new ActivityClassVisitor(cv, this.context, this.log); } else {
/* 1061 */             if (isExcludedPackage(this.context.getClassName())) {
/* 1062 */               return null;
/*      */             }
/* 1064 */             cv = new AnnotatingClassVisitor(cv, this.context, this.log);
/* 1065 */             cv = new ActivityClassVisitor(cv, this.context, this.log);
/* 1066 */             cv = new AsyncTaskClassVisitor(cv, this.context, this.log);
/* 1067 */             cv = new TraceAnnotationClassVisitor(cv, this.context, this.log);
/* 1068 */             cv = new WrapMethodClassVisitor(cv, this.context, this.log);
/*      */           }
/* 1070 */           cv = new ContextInitializationClassVisitor(cv, this.context);
/* 1071 */           cr.accept(cv, 12);
/*      */         }
/*      */         else {
/* 1074 */           this.log.warning(MessageFormat.format("[{0}] class is already instrumented! skipping ...", new Object[] { this.context.getFriendlyClassName() }));
/*      */         }
/*      */ 
/* 1077 */         return this.context.newClassData(cw.toByteArray());
/*      */       }
/*      */       catch (SkipException ex) {
/* 1080 */         return null;
/*      */       } catch (HaltBuildException e) {
/* 1082 */         throw new RuntimeException(e);
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/* 1087 */         this.log.error("Unfortunately, an error has occurred while processing " + className + ". Please copy your build logs and the jar containing this class and send a message to support@newrelic.com, thanks!\n" + t.getMessage(), t);
/* 1088 */       }return new ClassData(bytes, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static abstract class BaseMethodVisitor extends AdviceAdapter
/*      */   {
/*      */     protected final String methodName;
/*  785 */     protected final RewriterAgent.BytecodeBuilder builder = new RewriterAgent.BytecodeBuilder(this);
/*      */ 
/*      */     protected BaseMethodVisitor(MethodVisitor mv, int access, String methodName, String desc) {
/*  788 */       super(access, methodName, desc);
/*  789 */       this.methodName = methodName;
/*      */     }
/*      */ 
/*      */     public void visitEnd()
/*      */     {
/*  797 */       super.visitAnnotation(Type.getDescriptor(InstrumentedMethod.class), false);
/*  798 */       super.visitEnd();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class BytecodeBuilder
/*      */   {
/*      */     private final GeneratorAdapter mv;
/*      */ 
/*      */     public BytecodeBuilder(GeneratorAdapter adapter)
/*      */     {
/*  655 */       this.mv = adapter;
/*      */     }
/*      */ 
/*      */     public BytecodeBuilder loadNull() {
/*  659 */       this.mv.visitInsn(1);
/*  660 */       return this;
/*      */     }
/*      */ 
/*      */     public BytecodeBuilder loadInvocationDispatcher()
/*      */     {
/*  668 */       this.mv.visitLdcInsn(Type.getType(RewriterAgent.INVOCATION_DISPATCHER_CLASS));
/*  669 */       this.mv.visitLdcInsn("treeLock");
/*  670 */       this.mv.invokeVirtual(Type.getType(Class.class), new com.newrelic.objectweb.asm.commons.Method("getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;"));
/*      */ 
/*  673 */       this.mv.dup();
/*  674 */       this.mv.visitInsn(4);
/*  675 */       this.mv.invokeVirtual(Type.getType(Field.class), new com.newrelic.objectweb.asm.commons.Method("setAccessible", "(Z)V"));
/*      */ 
/*  678 */       this.mv.visitInsn(1);
/*      */ 
/*  680 */       this.mv.invokeVirtual(Type.getType(Field.class), new com.newrelic.objectweb.asm.commons.Method("get", "(Ljava/lang/Object;)Ljava/lang/Object;"));
/*      */ 
/*  682 */       return this;
/*      */     }
/*      */ 
/*      */     public BytecodeBuilder loadArgumentsArray(String methodDesc)
/*      */     {
/*  692 */       com.newrelic.objectweb.asm.commons.Method method = new com.newrelic.objectweb.asm.commons.Method("dummy", methodDesc);
/*  693 */       this.mv.push(method.getArgumentTypes().length);
/*  694 */       Type objectType = Type.getType(Object.class);
/*  695 */       this.mv.newArray(objectType);
/*      */ 
/*  697 */       for (int i = 0; i < method.getArgumentTypes().length; i++) {
/*  698 */         this.mv.dup();
/*  699 */         this.mv.push(i);
/*  700 */         this.mv.loadArg(i);
/*  701 */         this.mv.arrayStore(objectType);
/*      */       }
/*  703 */       return this;
/*      */     }
/*      */ 
/*      */     public BytecodeBuilder loadArray(Runnable[] r)
/*      */     {
/*  713 */       this.mv.push(r.length);
/*  714 */       Type objectType = Type.getObjectType("java/lang/Object");
/*  715 */       this.mv.newArray(objectType);
/*      */ 
/*  717 */       for (int i = 0; i < r.length; i++) {
/*  718 */         this.mv.dup();
/*  719 */         this.mv.push(i);
/*  720 */         r[i].run();
/*  721 */         this.mv.arrayStore(objectType);
/*      */       }
/*      */ 
/*  724 */       return this;
/*      */     }
/*      */ 
/*      */     public BytecodeBuilder printToInfoLogFromBytecode(final String message) {
/*  728 */       loadInvocationDispatcher();
/*      */ 
/*  730 */       this.mv.visitLdcInsn("PRINT_TO_INFO_LOG");
/*  731 */       this.mv.visitInsn(1);
/*      */ 
/*  733 */       loadArray(new Runnable[] { new Runnable() {
/*      */         public void run() {
/*  735 */           RewriterAgent.BytecodeBuilder.this.mv.visitLdcInsn(message);
/*      */         }
/*      */       }
/*      */        });
/*  739 */       invokeDispatcher();
/*  740 */       return this;
/*      */     }
/*      */ 
/*      */     public BytecodeBuilder invokeDispatcher()
/*      */     {
/*  749 */       return invokeDispatcher(true);
/*      */     }
/*      */ 
/*      */     public BytecodeBuilder invokeDispatcher(boolean popReturnOffStack)
/*      */     {
/*  759 */       this.mv.invokeInterface(Type.getType(InvocationHandler.class), new com.newrelic.objectweb.asm.commons.Method("invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"));
/*  760 */       if (popReturnOffStack) {
/*  761 */         this.mv.pop();
/*      */       }
/*  763 */       return this;
/*      */     }
/*      */ 
/*      */     public BytecodeBuilder loadInvocationDispatcherKey(String key)
/*      */     {
/*  773 */       this.mv.visitLdcInsn(key);
/*      */ 
/*  776 */       this.mv.visitInsn(1);
/*      */ 
/*  778 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static abstract class SafeInstrumentationMethodVisitor extends RewriterAgent.BaseMethodVisitor
/*      */   {
/*      */     protected SafeInstrumentationMethodVisitor(MethodVisitor mv, int access, String methodName, String desc)
/*      */     {
/*  631 */       super(access, methodName, desc);
/*      */     }
/*      */ 
/*      */     protected final void onMethodExit(int opcode)
/*      */     {
/*  636 */       this.builder.loadInvocationDispatcher().loadInvocationDispatcherKey("SET_INSTRUMENTATION_DISABLED_FLAG").loadNull().invokeDispatcher();
/*      */ 
/*  642 */       super.onMethodExit(opcode);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class DexClassTransformer
/*      */     implements RewriterAgent.NewRelicClassTransformer
/*      */   {
/*      */     private Log log;
/*      */     private final Map<String, ClassVisitorFactory> classVisitors;
/*      */ 
/*      */     public DexClassTransformer(final Log log)
/*      */       throws URISyntaxException
/*      */     {
/*      */       final String agentJarPath;
/*      */       try
/*      */       {
/*  261 */         agentJarPath = RewriterAgent.access$100();
/*      */       } catch (URISyntaxException e) {
/*  263 */         log.error("Unable to get the path to the New Relic class rewriter jar", e);
/*  264 */         throw e;
/*      */       }
/*      */ 
/*  267 */       this.log = log;
/*      */ 
/*  269 */       this.classVisitors = new HashMap()
/*      */       {
/*      */       };
/*      */     }
/*      */ 
/*      */     public boolean modifies(Class<?> clazz)
/*      */     {
/*  313 */       Type t = Type.getType(clazz);
/*  314 */       return this.classVisitors.containsKey(t.getInternalName());
/*      */     }
/*      */ 
/*      */     public byte[] transform(ClassLoader classLoader, String className, Class<?> clazz, ProtectionDomain protectionDomain, byte[] bytes)
/*      */       throws IllegalClassFormatException
/*      */     {
/*  322 */       ClassVisitorFactory factory = (ClassVisitorFactory)this.classVisitors.get(className);
/*  323 */       if (factory != null)
/*      */       {
/*  325 */         if ((clazz != null) && (!factory.isRetransformOkay())) {
/*  326 */           this.log.error("Cannot instrument " + className);
/*  327 */           return null;
/*      */         }
/*  329 */         this.log.debug("Patching " + className);
/*      */         try
/*      */         {
/*  332 */           ClassReader cr = new ClassReader(bytes);
/*  333 */           ClassWriter cw = new PatchedClassWriter(3, classLoader);
/*      */ 
/*  335 */           ClassAdapter adapter = factory.create(cw);
/*  336 */           cr.accept(adapter, 4);
/*      */ 
/*  338 */           return cw.toByteArray();
/*      */         } catch (SkipException ex) {
/*      */         }
/*      */         catch (Exception ex) {
/*  342 */           this.log.error("Error transforming class " + className, ex);
/*      */         }
/*      */       }
/*      */ 
/*  346 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class NoOpClassTransformer
/*      */     implements RewriterAgent.NewRelicClassTransformer
/*      */   {
/*  230 */     private static HashSet<String> classVisitors = new HashSet() { } ;
/*      */ 
/*      */     public byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes)
/*      */       throws IllegalClassFormatException
/*      */     {
/*  240 */       return null;
/*      */     }
/*      */ 
/*      */     public boolean modifies(Class<?> clazz) {
/*  244 */       Type t = Type.getType(clazz);
/*  245 */       return classVisitors.contains(t.getInternalName());
/*      */     }
/*      */   }
/*      */ 
/*      */   private static abstract interface NewRelicClassTransformer extends ClassFileTransformer
/*      */   {
/*      */     public abstract boolean modifies(Class<?> paramClass);
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.RewriterAgent
 * JD-Core Version:    0.6.2
 */