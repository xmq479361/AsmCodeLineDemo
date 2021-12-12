package com.xmq.lifecycle.core


import xmq.hooks.IMatcher
import xmq.hooks.IMethodInvoker

/**
 * @author xmqyeah* @CreateDate 2021/12/10 22:14
 */
class HookerImpl extends IMatcher.MethodPatternMatcher {
    IMethodCall methodCall;
    String regex

    HookerImpl(String regex, IMethodCall methodCall) {
        super(regex.replace("*", "[a-zA-Z;/]+"))
        this.methodCall = methodCall;
        this.regex = regex;
    }

    static interface IMethodCall {
        void execute(IMethodInvoker invoker)
    }

    @Override
    int hashCode() {
        return regex.hashCode()
    }

    @Override
    boolean equals(Object obj) {
        if (obj == null || obj.class != HookerImpl.class) {
            return false
        }
        return regex == ((HookerImpl) obj).regex
    }
//    void execute(IMethodCall call) {
//        methodCall.execute(new MethodInvokerImpl(call, regex))
//    }

}
