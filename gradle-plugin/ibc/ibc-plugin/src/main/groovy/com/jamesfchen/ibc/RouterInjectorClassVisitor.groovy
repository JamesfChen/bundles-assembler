package com.jamesfchen.ibc

import com.jamesfchen.P
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class RouterInjectorClassVisitor extends ClassVisitor{
    List<RouterInfo> routers
    private hasInitMethod =false
    static final String ROUTERSMANAGER_CLASS_PATH = "com/jamesfchen/ibc/router/RoutersManager";
    RouterInjectorClassVisitor(ClassVisitor classVisitor, List<RouterInfo> routers) {
        super(Opcodes.ASM6, classVisitor)
        this.routers = routers
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        P.info("router 将要注入代码到${name}")
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if ("init" != name )return methodVisitor
        if (routers == null || routers.isEmpty()) {
            P.info("router数据为空")
            return methodVisitor
        }
        hasInitMethod = true
        methodVisitor = new AdviceAdapter(Opcodes.ASM6, methodVisitor, access, name, descriptor){
            @Override
            protected void onMethodEnter() {
                super.onMethodEnter()
            }

            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode)
                routers.each {
                    methodVisitor.visitMethodInsn(INVOKESTATIC, ROUTERSMANAGER_CLASS_PATH, "getInstance", "()Lcom/jamesfchen/ibc/router/RoutersManager;", false)
                    methodVisitor.visitLdcInsn(it.name)
                    methodVisitor.visitLdcInsn(Type.getType(it.descriptor))
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, ROUTERSMANAGER_CLASS_PATH, "register", "(Ljava/lang/String;Ljava/lang/Class;)V", false)
                }

            }
        }
        return methodVisitor
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        if (!hasInitMethod){
            P.info("没有找到init方法")
        }
    }
}