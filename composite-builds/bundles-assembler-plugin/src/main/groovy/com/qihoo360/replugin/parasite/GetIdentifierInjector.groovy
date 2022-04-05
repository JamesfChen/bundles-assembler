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
class GetIdentifierInjector extends BaseInjector {

    // 表达式编辑器
    @Override
    ExprEditor getEditor() {
        return  new ExprEditor() {
            @Override
            void edit(MethodCall m) throws CannotCompileException {
                String clsName = m.getClassName()
                String methodName = m.getMethodName()

                if (clsName.equalsIgnoreCase('android.content.res.Resources')) {
                    if (methodName == 'getIdentifier') {
                        m.replace('{ $3 = \"' +CommonData.appPackage + '\"; ' +
                                '$_ = $proceed($$);' +
                                ' }')
                        println " GetIdentifierCall => ${canonicalName} ${methodName}():${m.lineNumber}"
                    }
                }
            }
        }
    }

    GetIdentifierInjector(Project project, ClassPool pool) {
        super(project, pool)
    }
}
