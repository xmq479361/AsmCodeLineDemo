package com.xmq.lifecycle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

class CodeLineInsertAsmTransform extends Transform {

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
        return TransformManager.PROJECT_ONLY
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        def transformInputs = transformInvocation.inputs
        def outputProvider = transformInvocation.outputProvider
        def isIncremental = transformInvocation.isIncremental();
        println("find transform: ${outputProvider}, $isIncremental")
        //如果非增量，则清空旧的输出内容
        if (!isIncremental) {
            outputProvider.deleteAll()
        }
        transformInputs.each { TransformInput transInput ->
            transInput.directoryInputs.each { DirectoryInput dirInput ->
                def sourceFile = dirInput.file
                def destDir = outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes,
                        Format.DIRECTORY)
                println("find dir: ${sourceFile.name}, $destDir == ${dirInput.changedFiles.entrySet()}")
                if (isIncremental) {
                    def changeFileIterator = dirInput.changedFiles.entrySet().iterator()
                    while (changeFileIterator.hasNext()) {
                        def changeFileEntry = changeFileIterator.next()
                        def destPath = changeFileEntry.key.absolutePath.replace(changeFileEntry.key.parent, destDir.absolutePath)
                        println("Status ${changeFileEntry.value}: ${destDir}, ${changeFileEntry.key}, $destPath")
                        def destFile = new File(destPath)
                        println("Dir: $destPath, ${changeFileEntry.value}")
                        switch (changeFileEntry.value) {
                            case Status.REMOVED:
                                if (dest.exists()) {
                                    FileUtils.forceDelete(destDir)
                                }
                                break
                            case Status.ADDED:
                            case Status.CHANGED:
//                                transformSingleFile(changeFileEntry.key)
                                handleDirectory(changeFileEntry.key, destFile)
                                break
                            case Status.NOTCHANGED:
                                break
                        }
                    }
                } else {
                    // 首先全部拷贝，防止有后续处理异常导致文件的丢失
                    handleDirectory(sourceFile, destDir)
//                    transformDir(sourceFile, destDir)
                    println("copyDirectory: ${dirInput.file.path} => ${destDir.path}")
                    FileUtils.copyDirectory(sourceFile, destDir)
                }
            }
            //  !!!!!!!!!! !!!!!!!!!! !!!!!!!!!! !!!!!!!!!! !!!!!!!!!!
            //使用androidx的项目一定也注意jar也需要处理，否则所有的jar都不会最终编译到apk中，千万注意
            //导致出现ClassNotFoundException的崩溃信息，当然主要是因为找不到父类，因为父类AppCompatActivity在jar中
            transInput.jarInputs.forEach {
                def dest = outputProvider?.getContentLocation(
                        it.name,
                        it.contentTypes,
                        it.scopes,
                        Format.JAR
                )
                FileUtils.copyFile(it.file, dest)
            }
        }
    }

    private void handleDirectory(File sourceFile, File destDir) {
        def files = sourceFile.listFiles().findAll { file ->
            if (file != null && file.isDirectory()) {
                true
            } else file.name.endsWith(".class")
        }

        for (file in files) {
            try {
                def destFile = new File(destDir, file.name)
                if (file.isDirectory()) {
                    handleDirectory(file, destFile)
                } else {
                    def sourceBytes = FileUtils.readFileToByteArray(file)
                    def modifyBytes = null
                    if (!file.name.contains("BuildConfig") && !file.name.startsWith("R\$")) {
                        modifyBytes = modifyClass(sourceBytes)
                    }
                    if (modifyBytes != null) {
                        println("writeByteArrayToFile22: ${file.path} => ${destFile.path}")
                        FileUtils.writeByteArrayToFile(file, modifyBytes)
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace()
            }
        }
    }

    byte[] modifyClass(byte[] bytes) {
        // 对class文件进行解析
        def classReader = new ClassReader(bytes)
        // 对class文件的写入
        def classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        // 访问class文件对应的内容，解析到某个结构就会通知到ClassVisitor
        def classVisitor = new CodeInsertClassVisitor(classWriter)
        // 依次调用classVisitor接口的各个方法
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        // 最终toByteArray将结果以byte[]方式返回
        def byteArray = classWriter.toByteArray()
        return byteArray
    }
    void transformSingleFile(File file) {
        println("\ttransformSingleFile: ${file.name} => ${file.path}")
        // 对class文件进行解析
        def classReader = new ClassReader(file.bytes)
        // 对class文件的写入
        def classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        // 访问class文件对应的内容，解析到某个结构就会通知到ClassVisitor
        def classVisitor = new CodeInsertClassVisitor(classWriter)
        // 依次调用classVisitor接口的各个方法
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        // 最终toByteArray将结果以byte[]方式返回
        def byteArray = classWriter.toByteArray()
        // 以文件流方式写入，并替换原本的class文件
        def fileOutPut = new FileOutputStream(file.path)
        fileOutPut.write(byteArray)
        fileOutPut.close()
    }
}