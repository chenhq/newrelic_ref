package com.newrelic.agent.android;

import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.gradle.api.tasks.TaskAction;

public class NewRelicInstrumentTask extends NewRelicTask {
    public NewRelicInstrumentTask() {
        NewRelicInstrumentTask this;
        CallSite[] arrayOfCallSite = $getCallSiteArray();
    }

    @TaskAction
    public Object newRelicInstrumentTask() {
        CallSite[] arrayOfCallSite = $getCallSiteArray();
        try {
            try {
                String extraArgs = (String) ScriptBytecodeAdapter.castToType(arrayOfCallSite[0].call($get$$class$java$lang$System(), "NewRelic.AgentArgs"), $get$$class$java$lang$String());
                String encodedProjectRoot = (String) ScriptBytecodeAdapter.castToType(arrayOfCallSite[1].call(arrayOfCallSite[2].call($get$$class$com$google$common$io$BaseEncoding()), arrayOfCallSite[3].call(arrayOfCallSite[4].call(arrayOfCallSite[5].call(arrayOfCallSite[6].callGroovyObjectGetProperty(this))))), $get$$class$java$lang$String());
                String agentArgs = (String) ScriptBytecodeAdapter.castToType(arrayOfCallSite[7].call("projectRoot=", encodedProjectRoot), $get$$class$java$lang$String());
                if ((!BytecodeInterface8.isOrigZ()) || (__$stMC) || (BytecodeInterface8.disabledStandardMetaClass())) {
                    if (ScriptBytecodeAdapter.compareNotEqual(extraArgs, null)) {
                        Object localObject1 = arrayOfCallSite[8].call(arrayOfCallSite[9].call(agentArgs, ";"), extraArgs);
                        agentArgs = (String) ScriptBytecodeAdapter.castToType(localObject1, $get$$class$java$lang$String());
                    }
                } else if (ScriptBytecodeAdapter.compareNotEqual(extraArgs, null)) {
                    Object localObject2 = arrayOfCallSite[10].call(arrayOfCallSite[11].call(agentArgs, ";"), extraArgs);
                    agentArgs = (String) ScriptBytecodeAdapter.castToType(localObject2, $get$$class$java$lang$String());
                }

                arrayOfCallSite[12].call(arrayOfCallSite[13].callGroovyObjectGetProperty(this), arrayOfCallSite[14].call("[newrelic] Attaching to process ", arrayOfCallSite[15].call(arrayOfCallSite[16].callGroovyObjectGetProperty(this))));
                return arrayOfCallSite[17].callCurrent(this, agentArgs);
            } catch (Exception localException1) {
                Exception e = (Exception) localException1;

                arrayOfCallSite[18].call(arrayOfCallSite[19].callGroovyObjectGetProperty(this), "[newrelic] Error encountered while loading the New Relic agent", e);
                throw ((Throwable) arrayOfCallSite[20].callConstructor($get$$class$java$lang$RuntimeException(), e));
            }
        } finally {
        }
        return null;
    }

    static {
        __$swapInit();
        Long localLong1 = (Long) DefaultTypeTransformation.box(0L);
        __timeStamp__239_neverHappen1416597642152 = localLong1.longValue();
        Long localLong2 = (Long) DefaultTypeTransformation.box(1416597642152L);
    }
}





