package com.xmq.lifecycle

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter


class CodeInsertClassVisitor extends ClassVisitor {
    private String className;
    private String superName;

    CodeInsertClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM9, classVisitor)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.className = name
        this.superName = superName
    }

    @Override
    void visitSource(String source, String debug) {
        super.visitSource(source, debug)
        this.source = source
    }

    def source
    final static String TYPE_LOG_MOCK = "com/xmq/codeline/MockLog"
    final static String TYPE_STRING = "java/lang/String"
    final static String TYPE_STRING_BUILDER = "java/lang/StringBuilder"
    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        println("\t==ClassVisitor:$className: visitMethod: $name, super: $superName")
        def method = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (className != TYPE_LOG_MOCK) {
            return new CodeLineMethodVisitor(api, source, method, access, name, descriptor)
        }
        return method
    }


    static class CodeLineMethodVisitor extends AdviceAdapter {
        protected CodeLineMethodVisitor(int api, String source, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor)
            this.source = source
        }

        @Override
        void visitLineNumber(int line, Label start) {
            super.visitLineNumber(line, start)
            lineNo = line
        }
        def lineNo
        def source
        @Override
        void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
            System.out.println("\t\tvisitMethodInsn: "+owner+", "+name+", "+descriptor)
            if (owner == TYPE_LOG_MOCK) {
                // ??????????????????????????????
                String[] params = descriptor.substring(1, descriptor.length() - 3).split(";")
                if (params.length > 0) {
                    int paramLen = params.length - 1
                    int formatMsgIndex = 1
                    // ??????????????????????????????????????????(???????????????)????????????
                    if (paramLen == 0) formatMsgIndex = 0
                    // ?????????????????????format????????????
                    int tmp = params.length
                    // ?????????store????????????(?????????????????????),??????????????????
                    List<Integer> paramsTs = new LinkedList<>()
                    while (tmp-- > 0) {
                        String typeDesc = params[tmp]
                        int ts = newLocal(Type.getType(typeDesc))
                        storeLocal(ts)
                        paramsTs.add(ts)
                        // ???????????? ?????????format str???????????? ??????????????????
                        if (tmp <= formatMsgIndex && typeDesc == "L$TYPE_STRING") break
                    }
                    System.out.println("(" + source + ":" + lineNo + ") paramsTs: " + paramsTs)
                    // ?????????????????????????????????
                    mv.visitTypeInsn(NEW, TYPE_STRING_BUILDER)
                    mv.visitInsn(DUP)
                    mv.visitMethodInsn(INVOKESPECIAL, TYPE_STRING_BUILDER, "<init>", "()V", false)
                    def codeline = "(" + source + ":" + lineNo + ") "
                    mv.visitLdcInsn(codeline)
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                    loadLocal(paramsTs.remove(paramsTs.size() - 1))
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                    mv.visitMethodInsn(INVOKEVIRTUAL, TYPE_STRING_BUILDER, "toString", "()L${TYPE_STRING};", false)
                    // ???????????????????????????
                    for (Integer value: paramsTs) {
                        loadLocal(value);
                    }
                }
            }
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)
        }

    }
}