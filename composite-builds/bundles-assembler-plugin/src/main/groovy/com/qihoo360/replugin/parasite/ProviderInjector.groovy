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
 * @author RePlugin Team
 */
public class ProviderInjector extends BaseInjector {

    // 处理以下方法
    public static def includeMethodCall = ['query',
                                           'getType',
                                           'insert',
                                           'bulkInsert',
                                           'delete',
                                           'update',
                                           /// 以下方法 replugin plugin lib 暂未支持，导致字节码修改失败。
                                           'openInputStream',
                                           'openOutputStream',
                                           'openFileDescriptor',
                                           'registerContentObserver',
                                           'acquireContentProviderClient',
                                           'notifyChange',
                                           'toCalledUri',
    ]

    @Override
    ExprEditor getEditor() {
        return new ExprEditor() {
            static def PROVIDER_CLASS = 'com.qihoo360.replugin.loader.p.PluginProviderClient'

            @Override
            void edit(MethodCall m) throws CannotCompileException {
                final String clsName = m.getClassName()
                final String methodName = m.getMethodName()
                if (!clsName.equalsIgnoreCase('android.content.ContentResolver')) {
                    return
                }
                if (!(methodName in ProviderInjector.includeMethodCall)) {
                    // println "跳过$methodName"
                    return
                }
                try {
                    replaceStatement(m, methodName, m.lineNumber)
                } catch (Exception e) { //确保不影响其他 MethodCall
                    println "    [Warning] --> ProviderExprEditor : ${e.toString()}"
                }
            }

            def private replaceStatement(MethodCall methodCall, String method, def line) {
                if (methodCall.getMethodName() == 'registerContentObserver' || methodCall.getMethodName() == 'notifyChange') {
                    methodCall.replace('{' + PROVIDER_CLASS + '.' + method + '(com.qihoo360.replugin.RePlugin.getPluginContext(), $$);}')
                } else {
                    methodCall.replace('{$_ = ' + PROVIDER_CLASS + '.' + method + '(com.qihoo360.replugin.RePlugin.getPluginContext(), $$);}')
                }
                println ">>> Replace: ${canonicalName} Provider.${method}():${line}"
            }
        }
    }

    ProviderInjector(Project project, ClassPool pool) {
        super(project, pool)
    }

    @Override
    byte[] onInsertCode(File mather, InputStream classStream, String canonicalName) {
        if (canonicalName.contains('PluginProviderClient.class')) throw new Exception('can not replace self ')
        super.onInsertCode(mather, classStream, canonicalName)

    }
}
