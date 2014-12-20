package com.newrelic.agent.android;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.callsite.CallSite;

class NewRelicGradlePlugin$_apply_closure1_closure3 extends Closure
        implements GeneratedClosure {
    public NewRelicGradlePlugin$_apply_closure1_closure3(Object _outerInstance, Object _thisObject) {
        super(_outerInstance, _thisObject);
    }

    public Object doCall(Object variant) {
        CallSite[] arrayOfCallSite = $getCallSiteArray();
        arrayOfCallSite[0].call(arrayOfCallSite[1].callGetProperty(variant), "newRelicInstrumentTask");
        arrayOfCallSite[2].call(arrayOfCallSite[3].callGetProperty(variant), "newRelicDeinstrumentTask");

        return arrayOfCallSite[4].call(arrayOfCallSite[5].callGroovyObjectGetProperty(this), arrayOfCallSite[6].call(arrayOfCallSite[7].call("[newrelic] Added instrumentation tasks to ", arrayOfCallSite[8].callGetProperty(variant)), " variant."));
        return null;
    }

    static {
        __$swapInit();
    }
}





