package com.newrelic.agent.android;

import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.gradle.api.tasks.TaskAction;

public class NewRelicDeinstrumentTask extends NewRelicTask {
    public NewRelicDeinstrumentTask() {
        NewRelicDeinstrumentTask this;
        CallSite[] arrayOfCallSite = $getCallSiteArray();
    }

    @TaskAction
    public Object newRelicInsturmentTask() {
        CallSite[] arrayOfCallSite = $getCallSiteArray();
        try {
            try {
                String extraArgs = (String) ScriptBytecodeAdapter.castToType(arrayOfCallSite[0].call($get$$class$java$lang$System(), "NewRelic.AgentArgs"), $get$$class$java$lang$String());
                String agentArgs = "deinstrument=true";
                if ((!BytecodeInterface8.isOrigZ()) || (__$stMC) || (BytecodeInterface8.disabledStandardMetaClass())) {
                    if (ScriptBytecodeAdapter.compareNotEqual(extraArgs, null)) {
                        Object localObject1 = arrayOfCallSite[1].call(arrayOfCallSite[2].call(agentArgs, ";"), extraArgs);
                        agentArgs = (String) ScriptBytecodeAdapter.castToType(localObject1, $get$$class$java$lang$String());
                    }
                } else if (ScriptBytecodeAdapter.compareNotEqual(extraArgs, null)) {
                    Object localObject2 = arrayOfCallSite[3].call(arrayOfCallSite[4].call(agentArgs, ";"), extraArgs);
                    agentArgs = (String) ScriptBytecodeAdapter.castToType(localObject2, $get$$class$java$lang$String());
                }

                arrayOfCallSite[5].call(arrayOfCallSite[6].callGroovyObjectGetProperty(this), arrayOfCallSite[7].call("[newrelic] Attaching to process for deinstrumentation ", arrayOfCallSite[8].call(arrayOfCallSite[9].callGroovyObjectGetProperty(this))));
                return arrayOfCallSite[10].callCurrent(this, agentArgs);
            } catch (Exception localException1) {
                Exception e = (Exception) localException1;

                arrayOfCallSite[11].call(arrayOfCallSite[12].callGroovyObjectGetProperty(this), "[newrelic] Error encountered while loading the New Relic agent", e);
                throw ((Throwable) arrayOfCallSite[13].callConstructor($get$$class$java$lang$RuntimeException(), e));
            }
        } finally {
        }
        return null;
    }

    static {
        __$swapInit();
        Long localLong1 = (Long) DefaultTypeTransformation.box(0L);
        __timeStamp__239_neverHappen1416597642138 = localLong1.longValue();
        Long localLong2 = (Long) DefaultTypeTransformation.box(1416597642138L);
    }
}





