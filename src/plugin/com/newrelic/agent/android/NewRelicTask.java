package com.newrelic.agent.android;

import com.sun.tools.attach.VirtualMachine;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

public class NewRelicTask extends DefaultTask
        implements GroovyObject {
    protected Logger logger;

    public NewRelicTask() {
        NewRelicTask this;
        CallSite[] arrayOfCallSite = $getCallSiteArray();
        Object localObject = arrayOfCallSite[0].call($get$$class$com$newrelic$agent$android$NewRelicGradlePlugin());
        this.logger = ((Logger) ScriptBytecodeAdapter.castToType(localObject, $get$$class$org$gradle$api$logging$Logger()));
        MetaClass localMetaClass = $getStaticMetaClass();
        this.metaClass = localMetaClass;
    }

    public Object getPid() {
        CallSite[] arrayOfCallSite = $getCallSiteArray();
        String nameOfRunningVM = (String) ScriptBytecodeAdapter.castToType(arrayOfCallSite[1].call(arrayOfCallSite[2].call($get$$class$java$lang$management$ManagementFactory())), $get$$class$java$lang$String());
        int p = DefaultTypeTransformation.intUnbox(arrayOfCallSite[3].call(nameOfRunningVM, "@"));
        return arrayOfCallSite[4].call(nameOfRunningVM, (Integer) DefaultTypeTransformation.box(0), (Integer) DefaultTypeTransformation.box(p));
        return null;
    }

    public Object getJarFilePath() {
        CallSite[] arrayOfCallSite = $getCallSiteArray();
        try {
            try {
                String jarFilePath = (String) ScriptBytecodeAdapter.castToType(arrayOfCallSite[5].call(arrayOfCallSite[6].call(arrayOfCallSite[7].call(arrayOfCallSite[8].call(arrayOfCallSite[9].call($get$$class$com$newrelic$agent$compile$RewriterAgent()))))), $get$$class$java$lang$String());

                Object localObject1 = arrayOfCallSite[10].call(arrayOfCallSite[11].callConstructor($get$$class$java$io$File(), jarFilePath));
                jarFilePath = (String) ScriptBytecodeAdapter.castToType(localObject1, $get$$class$java$lang$String());

                arrayOfCallSite[12].call(this.logger, arrayOfCallSite[13].call("[newrelic] Found New Relic instrumentation within ", jarFilePath));

                return jarFilePath;
            } catch (URISyntaxException localURISyntaxException1) {
                URISyntaxException e = (URISyntaxException) localURISyntaxException1;

                arrayOfCallSite[14].call(this.logger, "[newrelic] Unable to find New Relic instrumentation jar");
                throw ((Throwable) arrayOfCallSite[15].callConstructor($get$$class$java$lang$RuntimeException(), e));
            } catch (IOException localIOException1) {
                IOException e = (IOException) localIOException1;

                arrayOfCallSite[16].call(this.logger, "[newrelic] Unable to find New Relic instrumentation jar");
                throw ((Throwable) arrayOfCallSite[17].callConstructor($get$$class$java$lang$RuntimeException(), e));
            }
        } finally {
        }
        return null;
    }

    public Object injectAgent(String agentArgs) {
        CallSite[] arrayOfCallSite = $getCallSiteArray();
        VirtualMachine vm = (VirtualMachine) null;
        Object localObject1;
        Object localObject2;
        if ((__$stMC) || (BytecodeInterface8.disabledStandardMetaClass())) {
            localObject1 = arrayOfCallSite[18].call($get$$class$com$sun$tools$attach$VirtualMachine(), arrayOfCallSite[19].callCurrent(this));
            vm = (VirtualMachine) ScriptBytecodeAdapter.castToType(localObject1, $get$$class$com$sun$tools$attach$VirtualMachine());
        } else {
            localObject2 = arrayOfCallSite[20].call($get$$class$com$sun$tools$attach$VirtualMachine(), getPid());
            vm = (VirtualMachine) ScriptBytecodeAdapter.castToType(localObject2, $get$$class$com$sun$tools$attach$VirtualMachine());
        }
        if ((__$stMC) || (BytecodeInterface8.disabledStandardMetaClass()))
            arrayOfCallSite[21].call(vm, arrayOfCallSite[22].callCurrent(this), agentArgs);
        else arrayOfCallSite[23].call(vm, getJarFilePath(), agentArgs);
        return arrayOfCallSite[24].call(vm);
        return null;
    }

    static {
        __$swapInit();
        Long localLong1 = (Long) DefaultTypeTransformation.box(0L);
        __timeStamp__239_neverHappen1416597642118 = localLong1.longValue();
        Long localLong2 = (Long) DefaultTypeTransformation.box(1416597642118L);
    }
}





