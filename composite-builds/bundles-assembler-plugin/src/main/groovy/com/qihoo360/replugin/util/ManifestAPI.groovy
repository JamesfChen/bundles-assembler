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

package com.qihoo360.replugin.util

import com.qihoo360.replugin.util.manifest.IManifest
import com.qihoo360.replugin.util.manifest.ManifestReader
import org.gradle.api.GradleException
import org.gradle.api.Project
class ManifestAPI {

    IManifest sManifestAPIImpl

    def getActivities(Project project, String variantName) {
        if (sManifestAPIImpl == null) {
            sManifestAPIImpl = new ManifestReader(manifestPath(project, variantName))
        }
        sManifestAPIImpl.activities
    }

    def static manifestPath(Project project, String variantName) {
        def processManifestTask = project.tasks.getByName("process${variantName}Manifest")
        //transform的task目前能保证在processManifestTask之后执行
        if (processManifestTask) {
            File manifestOutputFile = null
            try {
                manifestOutputFile = processManifestTask.getManifestOutputFile()
            } catch (Exception ignored) {
                def dir = processManifestTask.getMultiApkManifestOutputDirectory()
                if (dir instanceof File || dir instanceof String) {
                    manifestOutputFile = new File(dir, "AndroidManifest.xml")
                } else {
                    manifestOutputFile = new File(dir.getAsFile().get(), "AndroidManifest.xml")
                }
            }

            if (manifestOutputFile == null) {
                throw new GradleException("can't get manifest file")
            }
            if (!manifestOutputFile.exists()) {
                println ' AndroidManifest.xml not exist'
            }
            println " AndroidManifest.xml 路径：$manifestOutputFile"
            return manifestOutputFile.absolutePath
        }

        return ""
    }
}
