package com.xmq.lifecycle.core

import com.xmq.lifecycle.ext.HookExtension
import javassist.CannotCompileException
import javassist.expr.MethodCall
import xmq.hooks.IMethodInvoker

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author xmqyeah* @CreateDate 2021/12/12 20:45
 */
class HookManager {
//    static List<HookerImpl> hookers = new LinkedList<>()
//    NamedDomainObjectContainer<IMatcher> hookers
//    NamedDomainObjectContainer<List<Hooker>> hookers
//    NamedDomainObjectContainer<Hooker> hookers

    static void execute(MethodCall call) {
        if (null == call) return
        def hookEntry = new HookEntry(call)
        System.out.println("execute hookers: "+ HookExtension.hookers.size())
        HookExtension.hookers.each { hooker ->
            print("==check: " + hooker.regex)
            println("--: " + call.method)
            def statement = call.className + "." + call.methodName + call.signature
            if (hooker.match(statement)) {
                println("\t\t\t==execute: " + hooker.regex + " = " + statement)
                hooker.methodCall.execute(hookEntry)
            } else {
                println("not match: " + hooker.regex + " = " + statement)
            }
        }
        hookEntry.executed()
    }

    static class HookEntry implements IMethodInvoker {
        void executed() {
            if (beforeStatement.length() == 0 && afterStatement.length() == 0) {
                return
            }
            StringBuffer fullStatement = new StringBuffer()
            if (beforeStatement.length() > 0) {
                fullStatement.append(beforeStatement)
            }
            if (hasSourceExec) {
                if (call.method.returnType.name == "void") {
                    fullStatement.append("\$proceed(\$\$);")
                } else {
                    fullStatement.append("\$_ = \$proceed(\$\$);")
                }
            }
            if (afterStatement.length() > 0) {
                fullStatement.append(afterStatement)
            }
            replaceFor("{ " + fullStatement+" }")
        }
        StringBuffer beforeStatement = new StringBuffer()
        StringBuffer afterStatement = new StringBuffer()
        MethodCall call
        boolean hasSourceExec = true
        HookEntry(MethodCall call) {
            this.call = call
        }

        @Override
        void insertBefore(String statement) {
            System.out.println("insertBefore: " + statement)
//            replaceFor("{ " + statement + " \$proceed(\$\$);}")
            beforeStatement.append(statement)
        }

        @Override
        void insertAfter(String statement) {
            System.out.println("insertAfter: " + statement)
//            replaceFor("{ \$proceed(\$\$);" + statement + " }")
            afterStatement.append(statement)
        }

        @Override
        void replace(String statement) {
            System.out.println("replace: " + statement)
            beforeStatement.insert(0,"\$_ = null; " + statement)
            hasSourceExec = false
//            replaceFor("{\$_ = null; " + statement + "}")
        }

        @Override
        void replaceParameter(int index, String statement) {
            System.out.println("replaceParameter: " +index+","+ statement)
            beforeStatement.insert(0, "\$" + index + " =  " + statement)
//            replaceFor("{\$" + index + " =  " + statement + " \$proceed(\$\$); }")
        }

        @Override
        void calcParameter(int index, String statement) {
            System.out.println("calcParameter: " +index+","+ statement)
            beforeStatement.insert(0, "\$" + index + " =  " + statement)
//            replaceFor("{" + statement + "}")
        }
        Pattern pattern = Pattern.compile("\\\$\\{([a-zA-Z]+)}")
        void replaceFor(String statement) {
            Matcher matcher = pattern.matcher(statement)
            System.out.println("------replaceFor: " + statement
                    +", "+ matcher.find())
            if (matcher.find()){
                HashMap<String, String> keyPairs = new HashMap<>()
                keyPairs.put("\\\$\\{fileName}", call.fileName)
                keyPairs.put("\\\$\\{line}", call.lineNumber.toString())
//                keyPairs.put("callMethod", callMethod)
                Iterator<Map.Entry<String, String>> iterator = keyPairs.entrySet().iterator()
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry =  iterator.next()
                    statement = statement.replaceAll(entry.key, entry.value)
                }
                System.out.println("------replaceFor: of" + statement)
            }
            try {
                call.replace(statement)
            } catch (CannotCompileException e) {
                e.printStackTrace()
            }
        }
        static Set<String> keys = Arrays.asList("")
    }
}
