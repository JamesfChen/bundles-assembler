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

import com.jamesfchen.IInsertCode
import com.qihoo360.replugin.util.Util
import javassist.ClassPool
import javassist.expr.ExprEditor
import org.gradle.api.Project

/**
 * @author RePlugin Team
 */
abstract class BaseInjector implements IInsertCode {
    protected Project project
    protected ClassPool pool
    protected String canonicalName
    protected ExprEditor editor
    BaseInjector(Project project, ClassPool pool) {
        this.project = project
        this.pool = pool
    }

    String name() {
        return getClass().getSimpleName()
    }

    ExprEditor getEditor() {
        return null
    }

    protected void setVariantName(String variantName) {

    }

    @Override
    void onInsertCodeBegin() {
        if (editor == null) {
            editor = getEditor()
        }
    }

    @Override
    byte[] onInsertCode(File mather, InputStream classStream, String canonicalName) {
        this.canonicalName = canonicalName
        Util.newSection()
        def ctCls
        try {
            ctCls = pool.makeClass(classStream);
            if (ctCls.isFrozen()) {
                ctCls.defrost()
            }

            (ctCls.getDeclaredMethods() + ctCls.getMethods()).each {
                it.instrument(editor)
            }

            ctCls.writeFile(mather.absolutePath)
            return ctCls.toBytecode()
        } catch (Throwable t) {
            println "    [Warning] --> ${t.toString()}"
            return null
        } finally {
            if (ctCls != null) {
                ctCls.detach()
            }
        }
    }

    @Override
    void onInsertCodeEnd() {

    }
}
