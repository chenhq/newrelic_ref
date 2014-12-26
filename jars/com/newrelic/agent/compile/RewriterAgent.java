// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RewriterAgent.java

package com.newrelic.agent.compile;

import com.newrelic.agent.compile.visitor.ActivityClassVisitor;
import com.newrelic.agent.compile.visitor.AnnotatingClassVisitor;
import com.newrelic.agent.compile.visitor.AsyncTaskClassVisitor;
import com.newrelic.agent.compile.visitor.ContextInitializationClassVisitor;
import com.newrelic.agent.compile.visitor.NewRelicClassVisitor;
import com.newrelic.agent.compile.visitor.PrefilterClassVisitor;
import com.newrelic.agent.compile.visitor.TraceAnnotationClassVisitor;
import com.newrelic.agent.compile.visitor.WrapMethodClassVisitor;
import com.newrelic.agent.util.Streams;
import com.newrelic.objectweb.asm.ClassAdapter;
import com.newrelic.objectweb.asm.ClassReader;
import com.newrelic.objectweb.asm.ClassVisitor;
import com.newrelic.objectweb.asm.ClassWriter;
import com.newrelic.objectweb.asm.MethodVisitor;
import com.newrelic.objectweb.asm.Type;
import com.newrelic.objectweb.asm.commons.AdviceAdapter;
import com.newrelic.objectweb.asm.commons.GeneratorAdapter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

// Referenced classes of package com.newrelic.agent.compile:
//            SystemErrLog, FileLogImpl, Log, ClassAdapterBase, 
//            ClassRemapperConfig, InstrumentationContext, SkipException, HaltBuildException, 
//            ClassData, InstrumentedMethod, ClassVisitorFactory, PatchedClassWriter, 
//            MethodVisitorFactory, SkipInstrumentedMethodsMethodVisitor

public class RewriterAgent
{
    private static class InvocationDispatcher
        implements InvocationHandler
    {

        private boolean isInstrumentationDisabled()
        {
            return disableInstrumentation || System.getProperty("newrelic.instrumentation.disabled") != null;
        }

        private boolean isExcludedPackage(String packageName)
        {
            for(Iterator i$ = RewriterAgent.EXCLUDED_PACKAGES.iterator(); i$.hasNext();)
            {
                String name = (String)i$.next();
                if(packageName.contains(name))
                    return true;
            }

            return false;
        }

        public Object invoke(Object proxy, Method method, Object args[])
            throws Throwable
        {
            InvocationHandler handler;
            handler = (InvocationHandler)invocationHandlers.get(proxy);
            if(handler == null)
            {
                log.error((new StringBuilder()).append("Unknown invocation type: ").append(proxy).append(".  Arguments: ").append(Arrays.asList(args)).toString());
                return null;
            }
            return handler.invoke(proxy, method, args);
            Throwable t;
            t;
            log.error((new StringBuilder()).append("Error:").append(t.getMessage()).toString(), t);
            return null;
        }

        private ClassData visitClassBytes(byte bytes[])
        {
            String className = "an unknown class";
            ClassReader cr;
            ClassWriter cw;
            ClassVisitor cv;
            cr = new ClassReader(bytes);
            cw = new ClassWriter(cr, 1);
            context.reset();
            cr.accept(new PrefilterClassVisitor(context, log), 7);
            className = context.getClassName();
            if(context.hasTag("Lcom/newrelic/agent/android/instrumentation/Instrumented;"))
                break MISSING_BLOCK_LABEL_286;
            cv = cw;
            if(context.getClassName().startsWith("com/newrelic/agent/android"))
            {
                cv = new NewRelicClassVisitor(cv, context, log);
                break MISSING_BLOCK_LABEL_260;
            }
            if(context.getClassName().startsWith("android/support/"))
            {
                cv = new ActivityClassVisitor(cv, context, log);
                break MISSING_BLOCK_LABEL_260;
            }
            if(isExcludedPackage(context.getClassName()))
                return null;
            cv = new AnnotatingClassVisitor(cv, context, log);
            cv = new ActivityClassVisitor(cv, context, log);
            cv = new AsyncTaskClassVisitor(cv, context, log);
            cv = new TraceAnnotationClassVisitor(cv, context, log);
            cv = new WrapMethodClassVisitor(cv, context, log);
            cv = new ContextInitializationClassVisitor(cv, context);
            cr.accept(cv, 12);
            break MISSING_BLOCK_LABEL_314;
            log.warning(MessageFormat.format("[{0}] class is already instrumented! skipping ...", new Object[] {
                context.getFriendlyClassName()
            }));
            return context.newClassData(cw.toByteArray());
            SkipException ex;
            ex;
            return null;
            HaltBuildException e;
            e;
            throw new RuntimeException(e);
            Throwable t;
            t;
            log.error((new StringBuilder()).append("Unfortunately, an error has occurred while processing ").append(className).append(". Please copy your build logs and the jar containing this class and send a message to support@newrelic.com, thanks!\n").append(t.getMessage()).toString(), t);
            return new ClassData(bytes, false);
        }

        private final Log log;
        private final ClassRemapperConfig config;
        private final InstrumentationContext context;
        private final Map invocationHandlers;
        private boolean writeDisabledMessage;
        private final String agentJarPath = RewriterAgent.getAgentJarPath();
        private boolean disableInstrumentation;









        public InvocationDispatcher(final Log log)
            throws FileNotFoundException, IOException, ClassNotFoundException, URISyntaxException
        {
            writeDisabledMessage = true;
            disableInstrumentation = false;
            this.log = log;
            config = new ClassRemapperConfig(log);
            context = new InstrumentationContext(config, log);
            invocationHandlers = Collections.unmodifiableMap(new _cls1());
        }
    }

    private static abstract class BaseMethodVisitor extends AdviceAdapter
    {

        public void visitEnd()
        {
            super.visitAnnotation(Type.getDescriptor(com/newrelic/agent/compile/InstrumentedMethod), false);
            super.visitEnd();
        }

        protected final String methodName;
        protected final BytecodeBuilder builder = new BytecodeBuilder(this);

        protected BaseMethodVisitor(MethodVisitor mv, int access, String methodName, String desc)
        {
            super(mv, access, methodName, desc);
            this.methodName = methodName;
        }
    }

    private static class BytecodeBuilder
    {

        public BytecodeBuilder loadNull()
        {
            mv.visitInsn(1);
            return this;
        }

        public BytecodeBuilder loadInvocationDispatcher()
        {
            mv.visitLdcInsn(Type.getType(RewriterAgent.INVOCATION_DISPATCHER_CLASS));
            mv.visitLdcInsn("treeLock");
            mv.invokeVirtual(Type.getType(java/lang/Class), new com.newrelic.objectweb.asm.commons.Method("getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;"));
            mv.dup();
            mv.visitInsn(4);
            mv.invokeVirtual(Type.getType(java/lang/reflect/Field), new com.newrelic.objectweb.asm.commons.Method("setAccessible", "(Z)V"));
            mv.visitInsn(1);
            mv.invokeVirtual(Type.getType(java/lang/reflect/Field), new com.newrelic.objectweb.asm.commons.Method("get", "(Ljava/lang/Object;)Ljava/lang/Object;"));
            return this;
        }

        public BytecodeBuilder loadArgumentsArray(String methodDesc)
        {
            com.newrelic.objectweb.asm.commons.Method method = new com.newrelic.objectweb.asm.commons.Method("dummy", methodDesc);
            mv.push(method.getArgumentTypes().length);
            Type objectType = Type.getType(java/lang/Object);
            mv.newArray(objectType);
            for(int i = 0; i < method.getArgumentTypes().length; i++)
            {
                mv.dup();
                mv.push(i);
                mv.loadArg(i);
                mv.arrayStore(objectType);
            }

            return this;
        }

        public transient BytecodeBuilder loadArray(Runnable r[])
        {
            mv.push(r.length);
            Type objectType = Type.getObjectType("java/lang/Object");
            mv.newArray(objectType);
            for(int i = 0; i < r.length; i++)
            {
                mv.dup();
                mv.push(i);
                r[i].run();
                mv.arrayStore(objectType);
            }

            return this;
        }

        public BytecodeBuilder printToInfoLogFromBytecode(final String message)
        {
            loadInvocationDispatcher();
            mv.visitLdcInsn("PRINT_TO_INFO_LOG");
            mv.visitInsn(1);
            loadArray(new Runnable[] {
                new Runnable() {

                    public void run()
                    {
                        mv.visitLdcInsn(message);
                    }

                    final String val$message;
                    final BytecodeBuilder this$0;

                
                {
                    this$0 = BytecodeBuilder.this;
                    message = s;
                    super();
                }
                }
            });
            invokeDispatcher();
            return this;
        }

        public BytecodeBuilder invokeDispatcher()
        {
            return invokeDispatcher(true);
        }

        public BytecodeBuilder invokeDispatcher(boolean popReturnOffStack)
        {
            mv.invokeInterface(Type.getType(java/lang/reflect/InvocationHandler), new com.newrelic.objectweb.asm.commons.Method("invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"));
            if(popReturnOffStack)
                mv.pop();
            return this;
        }

        public BytecodeBuilder loadInvocationDispatcherKey(String key)
        {
            mv.visitLdcInsn(key);
            mv.visitInsn(1);
            return this;
        }

        private final GeneratorAdapter mv;


        public BytecodeBuilder(GeneratorAdapter adapter)
        {
            mv = adapter;
        }
    }

    private static abstract class SafeInstrumentationMethodVisitor extends BaseMethodVisitor
    {

        protected final void onMethodExit(int opcode)
        {
            builder.loadInvocationDispatcher().loadInvocationDispatcherKey("SET_INSTRUMENTATION_DISABLED_FLAG").loadNull().invokeDispatcher();
            super.onMethodExit(opcode);
        }

        protected SafeInstrumentationMethodVisitor(MethodVisitor mv, int access, String methodName, String desc)
        {
            super(mv, access, methodName, desc);
        }
    }

    private static final class DexClassTransformer
        implements NewRelicClassTransformer
    {

        public boolean modifies(Class clazz)
        {
            Type t = Type.getType(clazz);
            return classVisitors.containsKey(t.getInternalName());
        }

        public byte[] transform(ClassLoader classLoader, String className, Class clazz, ProtectionDomain protectionDomain, byte bytes[])
            throws IllegalClassFormatException
        {
            ClassVisitorFactory factory;
            factory = (ClassVisitorFactory)classVisitors.get(className);
            if(factory == null)
                break MISSING_BLOCK_LABEL_172;
            if(clazz != null && !factory.isRetransformOkay())
            {
                log.error((new StringBuilder()).append("Cannot instrument ").append(className).toString());
                return null;
            }
            log.debug((new StringBuilder()).append("Patching ").append(className).toString());
            ClassWriter cw;
            ClassReader cr = new ClassReader(bytes);
            cw = new PatchedClassWriter(3, classLoader);
            ClassAdapter adapter = factory.create(cw);
            cr.accept(adapter, 4);
            return cw.toByteArray();
            SkipException ex;
            ex;
            break MISSING_BLOCK_LABEL_172;
            ex;
            log.error((new StringBuilder()).append("Error transforming class ").append(className).toString(), ex);
            return null;
        }

        private Log log;
        private final Map classVisitors;

        public DexClassTransformer(final Log log)
            throws URISyntaxException
        {
            final String agentJarPath;
            try
            {
                agentJarPath = RewriterAgent.getAgentJarPath();
            }
            catch(URISyntaxException e)
            {
                log.error("Unable to get the path to the New Relic class rewriter jar", e);
                throw e;
            }
            this.log = log;
            classVisitors = new _cls1();
        }
    }

    private static final class NoOpClassTransformer
        implements NewRelicClassTransformer
    {

        public byte[] transform(ClassLoader classLoader, String s, Class aClass, ProtectionDomain protectiondomain, byte abyte0[])
            throws IllegalClassFormatException
        {
            return null;
        }

        public boolean modifies(Class clazz)
        {
            Type t = Type.getType(clazz);
            return classVisitors.contains(t.getInternalName());
        }

        private static HashSet classVisitors = new HashSet() {

                
                {
                    add("com/android/dx/command/dexer/Main");
                    add("com/android/ant/DexExecTask");
                    add("com/android/ide/eclipse/adt/internal/build/BuildHelper");
                    add("com/jayway/maven/plugins/android/phase08preparepackage/DexMojo");
                    add("java/lang/ProcessBuilder");
                }
        };


        private NoOpClassTransformer()
        {
        }

    }

    private static interface NewRelicClassTransformer
        extends ClassFileTransformer
    {

        public abstract boolean modifies(Class class1);
    }


    public RewriterAgent()
    {
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation)
    {
        premain(agentArgs, instrumentation);
    }

    public static String getVersion()
    {
        return "4.120.0";
    }

    public static Map getAgentOptions()
    {
        return agentOptions;
    }

    public static void premain(String agentArgs, Instrumentation instrumentation)
    {
        agentArgs = agentArgs;
        Throwable argsError = null;
        try
        {
            agentOptions = parseAgentArgs(agentArgs);
        }
        catch(Throwable t)
        {
            argsError = t;
        }
        String logFileName = (String)agentOptions.get("logfile");
        Log log = ((Log) (logFileName != null ? ((Log) (new FileLogImpl(agentOptions, logFileName))) : ((Log) (new SystemErrLog(agentOptions)))));
        if(argsError != null)
            log.error((new StringBuilder()).append("Agent args error: ").append(agentArgs).toString(), argsError);
        log.debug("Bootstrapping New Relic Android class rewriter");
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);
        log.debug((new StringBuilder()).append("Agent running in pid ").append(pid).append(" arguments: ").append(agentArgs).toString());
        try
        {
            NewRelicClassTransformer classTransformer;
            if(agentOptions.containsKey("deinstrument"))
            {
                log.info("Deinstrumenting...");
                classTransformer = new NoOpClassTransformer();
            } else
            {
                classTransformer = new DexClassTransformer(log);
                createInvocationDispatcher(log);
            }
            instrumentation.addTransformer(classTransformer, true);
            List classes = new ArrayList();
            Class arr$[] = instrumentation.getAllLoadedClasses();
            int len$ = arr$.length;
            for(int i$ = 0; i$ < len$; i$++)
            {
                Class clazz = arr$[i$];
                if(classTransformer.modifies(clazz))
                    classes.add(clazz);
            }

            if(!classes.isEmpty())
                if(instrumentation.isRetransformClassesSupported())
                {
                    log.debug((new StringBuilder()).append("Retransform classes: ").append(classes).toString());
                    instrumentation.retransformClasses((Class[])classes.toArray(new Class[classes.size()]));
                } else
                {
                    log.error((new StringBuilder()).append("Unable to retransform classes: ").append(classes).toString());
                }
            if(!agentOptions.containsKey("deinstrument"))
                redefineClass(instrumentation, classTransformer, java/lang/ProcessBuilder);
        }
        catch(Throwable ex)
        {
            log.error("Agent startup error", ex);
            throw new RuntimeException(ex);
        }
    }

    private static void redefineClass(Instrumentation instrumentation, ClassFileTransformer classTransformer, Class klass)
        throws IOException, IllegalClassFormatException, ClassNotFoundException, UnmodifiableClassException
    {
        String internalClassName = klass.getName().replace('.', '/');
        String classPath = (new StringBuilder()).append(internalClassName).append(".class").toString();
        ClassLoader cl = klass.getClassLoader() != null ? klass.getClassLoader() : com/newrelic/agent/compile/RewriterAgent.getClassLoader();
        InputStream stream = cl.getResourceAsStream(classPath);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Streams.copy(stream, output);
        stream.close();
        byte newBytes[] = classTransformer.transform(klass.getClassLoader(), internalClassName, klass, null, output.toByteArray());
        ClassDefinition def = new ClassDefinition(klass, newBytes);
        instrumentation.redefineClasses(new ClassDefinition[] {
            def
        });
    }

    private static Map parseAgentArgs(String agentArgs)
    {
        if(agentArgs == null)
            return Collections.emptyMap();
        Map options = new HashMap();
        String arr$[] = agentArgs.split(";");
        int len$ = arr$.length;
        for(int i$ = 0; i$ < len$; i$++)
        {
            String arg = arr$[i$];
            String keyValue[] = arg.split("=");
            if(keyValue.length == 2)
                options.put(keyValue[0], keyValue[1]);
            else
                throw new IllegalArgumentException((new StringBuilder()).append("Invalid argument: ").append(arg).toString());
        }

        return options;
    }

    private static ClassAdapter createDexerMainClassAdapter(ClassVisitor cw, Log log)
    {
        return new ClassAdapterBase(log, cw, new _cls2(log));
    }

    private static ClassAdapter createEclipseBuildHelperClassAdapter(ClassVisitor cw, Log log)
    {
        return new ClassAdapterBase(log, cw, new _cls3(log));
    }

    private static ClassAdapter createAntTaskClassAdapter(ClassVisitor cw, Log log)
    {
        String agentFileFieldName = "NewRelicAgentFile";
        Map methodVisitors = new HashMap() {

            
            {
                put(new com.newrelic.objectweb.asm.commons.Method("preDexLibraries", "(Ljava/util/List;)V"), new MethodVisitorFactory() {

                    public MethodVisitor create(MethodVisitor mv, int access, String name, String desc)
                    {
                        return new BaseMethodVisitor(mv, access, name, desc) {

                            protected void onMethodEnter()
                            {
                                builder.loadInvocationDispatcher().loadInvocationDispatcherKey(RewriterAgent.getProxyInvocationKey("com/android/ant/DexExecTask", methodName)).loadArray(new Runnable[] {
                                    new Runnable() {

                                        public void run()
                                        {
                                            loadArg(0);
                                        }

                                        final _cls1 this$2;

                            
                            {
                                this$2 = _cls1.this;
                                super();
                            }
                                    }
                                }).invokeDispatcher(false);
                                loadThis();
                                swap();
                                putField(Type.getObjectType("com/android/ant/DexExecTask"), "NewRelicAgentFile", Type.getType(java/lang/Object));
                            }

                            final _cls1 this$1;

                        
                        {
                            this$1 = _cls1.this;
                            super(x0, x1, x2, x3);
                        }
                        };
                    }

                    final _cls4 this$0;

                    
                    {
                        this$0 = _cls4.this;
                        super();
                    }
                });
                put(new com.newrelic.objectweb.asm.commons.Method("runDx", "(Ljava/util/Collection;Ljava/lang/String;Z)V"), new MethodVisitorFactory() {

                    public MethodVisitor create(MethodVisitor mv, int access, String name, String desc)
                    {
                        return new SafeInstrumentationMethodVisitor(mv, access, name, desc) {

                            protected void onMethodEnter()
                            {
                                builder.loadInvocationDispatcher().loadInvocationDispatcherKey("SET_INSTRUMENTATION_DISABLED_FLAG").loadArray(new Runnable[] {
                                    new Runnable() {

                                        public void run()
                                        {
                                            loadThis();
                                            getField(Type.getObjectType("com/android/ant/DexExecTask"), "NewRelicAgentFile", Type.getType(java/lang/Object));
                                        }

                                        final _cls1 this$2;

                            
                            {
                                this$2 = _cls1.this;
                                super();
                            }
                                    }
                                }).invokeDispatcher();
                            }

                            final _cls2 this$1;

                        
                        {
                            this$1 = _cls2.this;
                            super(x0, x1, x2, x3);
                        }
                        };
                    }

                    final _cls4 this$0;

                    
                    {
                        this$0 = _cls4.this;
                        super();
                    }
                });
            }
        };
        return new ClassAdapterBase(log, cw, methodVisitors) {

            public void visitEnd()
            {
                super.visitEnd();
                visitField(2, "NewRelicAgentFile", Type.getType(java/lang/Object).getDescriptor(), null, null);
            }

        };
    }

    private static ClassAdapter createProcessBuilderClassAdapter(ClassVisitor cw, Log log)
    {
        return new ClassAdapter(cw) {

            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String exceptions[])
            {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if("start".equals(name))
                    mv = new SkipInstrumentedMethodsMethodVisitor(new BaseMethodVisitor(mv, access, name, desc) {

                        protected void onMethodEnter()
                        {
                            builder.loadInvocationDispatcher().loadInvocationDispatcherKey(RewriterAgent.getProxyInvocationKey("java/lang/ProcessBuilder", methodName)).loadArray(new Runnable[] {
                                new Runnable() {

                                    public void run()
                                    {
                                        loadThis();
                                        invokeVirtual(Type.getObjectType("java/lang/ProcessBuilder"), new com.newrelic.objectweb.asm.commons.Method("command", "()Ljava/util/List;"));
                                    }

                                    final _cls1 this$1;

                        
                        {
                            this$1 = _cls1.this;
                            super();
                        }
                                }
                            }).invokeDispatcher();
                        }

                        final _cls6 this$0;

                    
                    {
                        this$0 = _cls6.this;
                        super(x0, x1, x2, x3);
                    }
                    });
                return mv;
            }

        };
    }

    private static ClassAdapter createMavenClassAdapter(ClassVisitor cw, Log log, String agentJarPath)
    {
        Map methodVisitors = new _cls7(agentJarPath);
        return new ClassAdapterBase(log, cw, methodVisitors);
    }

    private static String getAgentJarPath()
        throws URISyntaxException
    {
        return (new File(com/newrelic/agent/compile/RewriterAgent.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getAbsolutePath();
    }

    private static void createInvocationDispatcher(Log log)
        throws Exception
    {
        Field field = INVOCATION_DISPATCHER_CLASS.getDeclaredField("treeLock");
        field.setAccessible(true);
        Field modifiersField = java/lang/reflect/Field.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & 0xffffffef);
        if(field.get(null) instanceof InvocationDispatcher)
            log.info("Detected cached instrumentation.");
        else
            field.set(null, new InvocationDispatcher(log));
    }

    private static String getProxyInvocationKey(String className, String methodName)
    {
        return (new StringBuilder()).append(className).append(".").append(methodName).toString();
    }

    public static final String VERSION = "4.120.0";
    private static final Set DX_COMMAND_NAMES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] {
        "dx", "dx.bat"
    })));
    private static final Set JAVA_NAMES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] {
        "java", "java.exe"
    })));
    private static final Set AGENT_JAR_NAMES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] {
        "newrelic.android.fat.jar", "newrelic.android.jar", "obfuscated.jar"
    })));
    private static final String DISABLE_INSTRUMENTATION_SYSTEM_PROPERTY = "newrelic.instrumentation.disabled";
    private static final String INVOCATION_DISPATCHER_FIELD_NAME = "treeLock";
    private static final Class INVOCATION_DISPATCHER_CLASS = java/util/logging/Logger;
    private static final String SET_INSTRUMENTATION_DISABLED_FLAG = "SET_INSTRUMENTATION_DISABLED_FLAG";
    private static final String PRINT_TO_INFO_LOG = "PRINT_TO_INFO_LOG";
    static final String DEXER_MAIN_CLASS_NAME = "com/android/dx/command/dexer/Main";
    private static final String ANT_DEX_EXEC_TASK = "com/android/ant/DexExecTask";
    private static final String ECLIPSE_BUILD_HELPER = "com/android/ide/eclipse/adt/internal/build/BuildHelper";
    private static final String MAVEN_DEX_MOJO = "com/jayway/maven/plugins/android/phase08preparepackage/DexMojo";
    private static final String PROCESS_BUILDER_CLASS_NAME = "java/lang/ProcessBuilder";
    private static final String PROCESS_CLASS_METHOD_NAME = "processClass";
    private static final String EXECUTE_DX_METHOD_NAME = "executeDx";
    private static final String PRE_DEX_LIBRARIES_METHOD_NAME = "preDexLibraries";
    private static final String START_METHOD_NAME = "start";
    private static String agentArgs;
    private static Map agentOptions = Collections.emptyMap();
    private static final HashSet EXCLUDED_PACKAGES = new HashSet() {

            
            {
                add("com/newrelic/agent/android");
                add("com/google/gson");
                add("com/squareup/okhttp");
            }
    };














}
