package com.xmq.lifecycle.core.ext

import com.xmq.lifecycle.core.HookerImpl
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import xmq.hooks.Hooker

/**
 * @author xmqyeah* @CreateDate 2021/12/10 0:13
 */
class HookExtension {
    static Set<HookerImpl> hookers = new HashSet<>()
//    NamedDomainObjectContainer<IMatcher> hookers;
//    NamedDomainObjectContainer<List<Hooker>> hookers
//    NamedDomainObjectContainer<Hooker> hookers

//    static void execute(MethodCall call) {
//        if (null == call) return
//        hookers.each {hooker->
//            print("==check: "+hooker.regex)
//            println("--: "+call.method)
//            println("--: "+call.method.toString())
//            def statement = call.className + "." + call.methodName + call.signature
//            if (hooker.match(statement)) {
//                println("\t\t\t==execute: "+hooker.regex+" = "+statement)
//                hooker.execute(call)
////                return
//            } else {
//                println("not match: "+hooker.regex+" = "+statement)
//            }
//        }
//    }
    HookExtension(Project project) {
//         hookers = project.container(Hooker)
//         hookers = project.container(List<Hooker>)
    }
//
//
//    void hookers(Action<NamedDomainObjectContainer<List<Hooker>>> hookersAction){
//        hookersAction.execute(hookers)
//        System.out.println("hookers: "+hookers.properties)
//        hookers.each {
//            System.out.println("hooker: "+it.toString());
//        }
//    }
    void hookers(Action<NamedDomainObjectContainer<Hooker>> hookersAction) {
        hookersAction.execute(hookers)
        System.out.println("hookers: " + hookers.properties)
        hookers.each {
            System.out.println("hooker: " + it.toString());
        }
    }

    void addHooker(String regex, HookerImpl.IMethodCall methodCall) {
        def hooker = new HookerImpl(regex, methodCall)
        if (hookers.contains(hooker)) {
            System.err.println("addHooker failed: exsits: "+regex)
        }
        hookers.add(hooker)
    }
}
