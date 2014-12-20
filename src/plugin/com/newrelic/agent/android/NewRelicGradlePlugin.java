package com.newrelic.agent.android;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

public class NewRelicGradlePlugin
        implements Plugin<Project>, GroovyObject {
    private static Logger logger;

    public NewRelicGradlePlugin() {
        NewRelicGradlePlugin this;
        CallSite[] arrayOfCallSite = $getCallSiteArray();
        MetaClass localMetaClass = $getStaticMetaClass();
        this.metaClass = localMetaClass;
    }

    public static Logger getLogger() {
        CallSite[] arrayOfCallSite = $getCallSiteArray();
        return (Logger) logger;
        return null;
    }
    static {
        __$swapInit();
        Long localLong1 = (Long) DefaultTypeTransformation.box(0L);
        __timeStamp__239_neverHappen1416597641990 = localLong1.longValue();
        Long localLong2 = (Long) DefaultTypeTransformation.box(1416597641990L);
    }

    public void apply(Project project) {
        Reference project = new Reference(project);
        CallSite[] arrayOfCallSite = $getCallSiteArray();
        Object localObject = arrayOfCallSite[0].call((Project) project.get());
        logger = (Logger) ScriptBytecodeAdapter.castToType(localObject, $get$$class$org$gradle$api$logging$Logger());

        arrayOfCallSite[1].call(logger, "[newrelic] New Relic plugin loaded.");

        arrayOfCallSite[2].call((Project) project.get(), (Project) project.get(), new _apply_closure1(this, project));
    }

    class _apply_closure1 extends Closure
            implements GeneratedClosure {
        public _apply_closure1(Object _thisObject, Reference project) {
            super(_thisObject);
            Reference localReference = project;
            this.project = localReference;
        }

        public Object doCall(Object it) {
            CallSite[] arrayOfCallSite = $getCallSiteArray();
            if (DefaultTypeTransformation.booleanUnbox(arrayOfCallSite[0].call(it, "android"))) {
                arrayOfCallSite[1].call(this.project.get(), ScriptBytecodeAdapter.createMap(new Object[]{"type", $get$$class$com$newrelic$agent$android$NewRelicInstrumentTask()}), "newRelicInstrumentTask");
                arrayOfCallSite[2].call(this.project.get(), ScriptBytecodeAdapter.createMap(new Object[]{"type", $get$$class$com$newrelic$agent$android$NewRelicDeinstrumentTask()}), "newRelicDeinstrumentTask");

                arrayOfCallSite[3].call(arrayOfCallSite[4].callGetProperty(arrayOfCallSite[5].callGroovyObjectGetProperty(this)), new NewRelicGradlePlugin._apply_closure1_closure2(this, getThisObject()));

                return arrayOfCallSite[6].call(arrayOfCallSite[7].callGetProperty(arrayOfCallSite[8].callGroovyObjectGetProperty(this)), new NewRelicGradlePlugin._apply_closure1_closure3(this, getThisObject()));
            } else {
                return null;
            }
            return null;
        }

        public Project getProject() {
            CallSite[] arrayOfCallSite = $getCallSiteArray();
            return (Project) ScriptBytecodeAdapter.castToType(this.project.get(), $get$$class$org$gradle$api$Project());
            return null;
        }

        public Object doCall() {
            CallSite[] arrayOfCallSite = $getCallSiteArray();
            return arrayOfCallSite[9].callCurrent(this, ScriptBytecodeAdapter.createPojoWrapper(null, $get$$class$java$lang$Object()));
            return null;
        }

        static {
            __$swapInit();
        }
    }
}





