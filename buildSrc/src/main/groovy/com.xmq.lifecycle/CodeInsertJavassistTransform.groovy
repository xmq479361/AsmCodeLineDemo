package com.xmq.lifecycle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.*

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
            mClassPool.appendClassPath(AndroidJarPath.getPath(project));
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
                    def dest = outputProvider.getContentLocation(jarName + md5Name,
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
                injectJar(item.getKey(), item.getValue(), mClassPool);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("MyJavassistTransform_end...");

    }

    void injectDir(String sourcePath, String destPath, ClassPool pool) {
        def srcDir = new File(sourcePath)
        def destDir = new File(destPath)
        srcDir.listFiles().findAll { file ->
            if (file != null && file.isDirectory()) {
                true
            } else file.name.endsWith(".class")
        }.each {
            def destFile = new File(destDir, it.name)
            if (it.isDirectory()) {
                injectDir(it, destFile, pool)
            } else {
                injectSingleFile(it, destFile, pool)
            }
        }
    }

    void injectSingleFile(File sourceFile, File destFile, ClassPool pool) {
//        def sourceBytes = FileUtils.readFileToByteArray(sourceFile)
//        def modifyBytes = null
//        if (!file.name.contains("BuildConfig") && !file.name.startsWith("R\$")) {
//            modifyBytes = modifyClass(sourceBytes)
//        }
//        if (modifyBytes != null) {
//            println("writeByteArrayToFile22: ${file.path} => ${destFile.path}")
//            FileUtils.writeByteArrayToFile(file, modifyBytes)
//        }
        int index = sourceFile.path.indexOf("main")
        println("injectSingleFile($index): ${sourceFile.path} => ${destFile.path}")
//        pool.get()
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
        cc.writeFile();
        return null
    }

    private static void modify(CtClass c, ClassPool mClassPool, List<String> methods) {
        if (c.isFrozen()) {
            c.defrost()
        }
        System.out.println("find class===============" + c.getName())
        for (String method : methods) {
            CtMethod ctMethod = c.getDeclaredMethod(method)
            String method2 = method + "DhCut"
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
                sb.append(result)
                sb.append(";")
            }
            sb.append("}")
            System.out.println("return type  =======" + ctMethod.getReturnType().getName())
            ctMethod.setBody(sb.toString())
        }
    }
}
