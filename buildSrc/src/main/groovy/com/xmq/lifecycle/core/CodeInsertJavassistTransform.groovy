package com.xmq.lifecycle.core

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.xmq.lifecycle.core.HookManager
import javassist.*
import javassist.expr.ExprEditor
import javassist.expr.MethodCall
import org.apache.commons.codec.digest.DigestUtils

/**
 * @author xmqyeah* @CreateDate 2021/11/28 15:14
 */
class CodeInsertJavassistTransform extends Transform {

    @Override
    String getName() {
        return "codeline"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        try {
            println("transform>>>> : " + getName())
            transformInvocation.outputProvider.deleteAll()
            ClassPool mClassPool = new ClassPool(ClassPool.getDefault());
            // 添加android.jar目录
            mClassPool.appendClassPath(AndroidJarPath())
            Map<String, String> dirMap = new HashMap<>();
            Map<String, String> jarMap = new HashMap<>();
            transformInvocation.inputs.each { input ->
                input.directoryInputs.each { dirInput ->
                    def destDir = transformInvocation.outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes,
                            Format.DIRECTORY)
                    dirMap.put(dirInput.getFile().getAbsolutePath(), destDir.getAbsolutePath());
                    mClassPool.appendClassPath(dirInput.getFile().getAbsolutePath());
                }
                input.jarInputs.each { jarInput ->
                    // 重命名输出文件
                    String jarName = jarInput.getName();
                    String md5Name = DigestUtils.md5Hex(jarInput.getFile().getAbsolutePath());
                    if (jarName.endsWith(".jar")) {
                        jarName = jarName.substring(0, jarName.length() - 4);
                    }
                    //生成输出路径
                    def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                            jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                    jarMap.put(jarInput.getFile().getAbsolutePath(), dest.getAbsolutePath());
                    mClassPool.appendClassPath(new JarClassPath(jarInput.getFile().getAbsolutePath()));
                }
            }
            for (Map.Entry<String, String> item : dirMap.entrySet()) {
                System.out.println("perform_directory : " + item.getKey());
                injectDir(item.getKey(), item.getValue(), mClassPool);
            }

            for (Map.Entry<String, String> item : jarMap.entrySet()) {
                System.out.println("perform_jar : " + item.getKey());
//                injectJar(item.getKey(), item.getValue(), mClassPool);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("MyJavassistTransform_end...");

    }

    void injectDir(String sourcePath, String destPath, ClassPool pool) {
        def srcDir = new File(sourcePath)
        def destDir = new File(destPath)
        injectDir(sourcePath, srcDir, destDir, pool)
    }

    void injectDir(String basePath, File srcDir, File destDir, ClassPool pool) {
        srcDir.listFiles().findAll { file ->
            if (file != null && file.isDirectory()) {
                true
            } else file.name.endsWith(".class")
        }.each {
//            def destFile = new File(destDir, it.name)
            if (it.isDirectory()) {
                injectDir(basePath, it, destDir, pool)
            } else {
                injectSingleFile(basePath, it, destDir, pool)
            }
        }
    }

    void injectSingleFile(String basePath, File sourceFile, File destFile, ClassPool pool) {
//        def sourceBytes = FileUtils.readFileToByteArray(sourceFile)
//        def modifyBytes = null
//        if (!file.name.contains("BuildConfig") && !file.name.startsWith("R\$")) {
//            modifyBytes = modifyClass(sourceBytes)
//        }
//        if (modifyBytes != null) {
//            println("writeByteArrayToFile22: ${file.path} => ${destFile.path}")
//            FileUtils.writeByteArrayToFile(file, modifyBytes)
//        }
//        int index = sourceFile.path.indexOf("main")
        String classFullName = sourceFile.path.replace(basePath + "\\", "")
                .replace(".class", "")
                .replace(".kt", "")
                .replaceAll("/", ".").replaceAll("\\\\", ".")

        if (classFullName == "com.xmq.codeline.MockLog") {
            return
        }
        def classCt = pool.get(classFullName)
        println("injectSingleFile($classFullName): ${sourceFile.path} => ${destFile.path}")
        modify(classCt, pool)
        classCt.writeFile(destFile.path)
    }

    void injectJar(String sourcePath, String destPath, ClassPool pool) {
        int index = sourcePath.indexOf("main")
        println("injectJar($index): ${sourcePath} => ${destPath}")
    }

    byte[] modifyClass(byte[] bytes) {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath("/usr/local/javalib")
        CtClass cc = pool.get("test.Rectangle");
        cc.setSuperclass(pool.get("test.Point"));
        cc.writeFile()
        return null
    }

    private static void modify(CtClass c, ClassPool mClassPool) {
        if (c.isFrozen()) {
            c.defrost()
        }
        System.out.println("find class===============" + c.getName())
        def methods = c.getMethods()
        for (def method : methods) {
            System.out.println("\tmethod: ${method.name}, ${method.longName}, ${method.getDeclaringClass().getName()}")
            method.instrument(new ExprEditor() {
                void edit(MethodCall m) throws CannotCompileException {
                    println("\t\tedit: ${m.className}#${m.methodName}, ${m.signature}, ${m.fileName}:${m.lineNumber}," +
                            "${m.metaClass}, ${m.method.modifiers} ")
//                    String statement = null
//                    if (m.className == "com.xmq.codeline.MockLog") {
//                        if (m.method.parameterTypes.length > 1 && m.method.parameterTypes[1].name == "java.lang.String") {
////                            if (m.method.parameterTypes[1].name == "java.lang.String")
//                            println("\t=====" + m.method.parameterTypes[1].name)
//                            statement = "{ \$2 = \"(" + m.fileName + ":" + m.lineNumber + ")= \" +  \$2; \$proceed(\$\$); }"
//                        } else if (m.method.parameterTypes[0].name == "java.lang.String") {
//                            statement = "{ \$1 = \"(" + m.fileName + ":" + m.lineNumber + ")= \" +  \$1; \$proceed(\$\$); }"
//                        }
//                    } else if (m.className == "com.xmq.codeline.MainActivity" && m.methodName == "test") {
////                        statement = "{ \$_ = null; test3(\"invoke test()\"); }"
//                        statement = "{ \$_ = null; com.xmq.codeline.MockLog.d(\"invoke MainActivity#test()\"); }"
//                    } else if (m.className == "com.xmq.codeline.TestUtil" && m.methodName == "clickLog") {
//                        statement = "{ com.xmq.codeline.MockLog.d(\"invoke TestUtil#clickLog()\"); }"
//                    }
//                    if (null != statement) {
//                        println("\t\t\treplace:: " + statement)
//                        m.replace(statement)
//                    }
                    HookManager.execute(m)

                }
            })
//            if (c.name == "com.xmq.codeline.MainActivity" && method.getDeclaringClass().name == c.name){
//                /**
//                 * 生成代理方法
//                 */
//                delegateOf(c, method)
//            }
        }
    }

    static void delegateOf(CtClass c, CtMethod ctMethod) {
        String method2 = ctMethod.name + "DhCut"
        println("\t\t------delegateOf: $method2")
        CtMethod ctMethod2 = CtNewMethod.copy(ctMethod, method2, c, null)
        c.addMethod(ctMethod2)
        int methodLen = ctMethod.getParameterTypes().length
        StringBuffer sb = new StringBuffer()
        sb.append("{try{")
        if (!ctMethod.getReturnType().getName().contains("void")) {
            sb.append("return ")
        }
        sb.append(method2)
        sb.append("(")
        for (int i = 0; i < methodLen; i++) {
            sb.append("\$" + (i + 1))
            if (i != methodLen - 1) {
                sb.append(",")
            }
        }
        sb.append(");}catch(Exception ex){ System.out.println(ex.toString());ex.printStackTrace();}")
        if (!ctMethod.getReturnType().getName().contains("void")) {
            sb.append("return ")
            String result = getReturnValue(ctMethod.getReturnType().getName())
//            String result = "("+ctMethod.getReturnType().getName().replace("/",".")+")null"
            sb.append(result)
            sb.append(";")
        }
        sb.append("}")
        System.out.println("return type  =======" + ctMethod.getReturnType().getName())
        System.out.println("delegateOf: " + sb.toString())
        ctMethod.setBody(sb.toString())
    }

    static String getReturnValue(String classPkgName) {
        String className = classPkgName.replace("/", ".")
        switch (className) {
            case String.name:
                return "\"\""
            case Long.name:
                return "0L"
            case Character.name:
                return "\' \'"
            case Double.name:
            case Float.name:
                return "0.0"
            case Boolean.name:
                return "false"
            case Byte.name:
            case Integer.name:
                return "0"
        }
        return "null"
    }

    byte test() {
        return
    }
    String AndroidJarPath() {
        return new File("D:\\dev\\Android\\sdk\\platforms\\android-30\\android.jar").getPath()
    }
}
