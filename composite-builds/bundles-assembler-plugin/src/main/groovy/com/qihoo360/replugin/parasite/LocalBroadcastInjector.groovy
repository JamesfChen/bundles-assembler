/*
 * Copyright (C) 2005-2017 Qihoo 360 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.qihoo360.replugin.parasite


import javassist.CannotCompileException
import javassist.ClassPool
import javassist.expr.ExprEditor
import javassist.expr.MethodCall
import org.gradle.api.Project
/**
 * LocalBroadcastInjector
 *
 * 将插件中的 LocalBroadcast 调用转发到宿主
 *
 * @author RePlugin Team
 */
class LocalBroadcastInjector extends BaseInjector {

    @Override
    ExprEditor getEditor() {
        return new  ExprEditor() {

            static def TARGET_CLASS = 'android.support.v4.content.LocalBroadcastManager'
            static def PROXY_CLASS = 'com.qihoo360.replugin.loader.b.PluginLocalBroadcastManager'

            /** 处理以下方法 */
            static def includeMethodCall = ['getInstance',
                                            'registerReceiver',
                                            'unregisterReceiver',
                                            'sendBroadcast',
                                            'sendBroadcastSync']

            /** 待处理文件的物理路径 */
            public def canonicalName

            @Override
            void edit(MethodCall call) throws CannotCompileException {
                if (call.getClassName().equalsIgnoreCase(TARGET_CLASS)) {
                    if (!(call.getMethodName() in includeMethodCall)) {
                        // println "Skip $methodName"
                        return
                    }

                    replaceStatement(call)
                }
            }

            def private replaceStatement(MethodCall call) {
                String method = call.getMethodName()
                if (method == 'getInstance') {
                    call.replace('{$_ = ' + PROXY_CLASS + '.' + method + '($$);}')
                } else {

                    def returnType = call.method.returnType.getName()
                    // getInstance 之外的调用，要增加一个参数，请参看 i-library 的 LocalBroadcastClient.java
                    if (returnType == 'void') {
                        call.replace('{' + PROXY_CLASS + '.' + method + '($0, $$);}')
                    } else {
                        call.replace('{$_ = ' + PROXY_CLASS + '.' + method + '($0, $$);}')
                    }
                }

                println ">>> Replace: ${canonicalName} <line:${call.lineNumber}> ${TARGET_CLASS}.${method}() <With> ${PROXY_CLASS}.${method}()\n"
            }
        }

    }

    LocalBroadcastInjector(Project project, ClassPool pool) {
        super(project, pool)
    }

    @Override
    byte[] onInsertCode(File mather, InputStream classStream, String canonicalName) {
        if (canonicalName.contains('LocalBroadcastManager')) {
            println "Ignore ${canonicalName}"
            return  super.onInsertCode(mather, classStream, canonicalName)
        }
        return super.onInsertCode(mather, classStream, canonicalName)
    }
}
