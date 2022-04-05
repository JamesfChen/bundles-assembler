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
 */

package com.qihoo360.replugin.host

import com.android.build.gradle.AppExtension
import com.qihoo360.replugin.Checker
import com.qihoo360.replugin.Constants
import com.qihoo360.replugin.util.Util
import com.qihoo360.replugin.util.VariantCompat
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author RePlugin Team
 */
class HostPlugin implements Plugin<Project> {
    def static TAG = Constants.TAG
    def project
    HostConfig config

    @Override
    void apply(Project project) {
        println "${TAG} Welcome to replugin world ! "
        this.project = project
        project.extensions.create(Constants.HOST_CONFIG, HostConfig)

        project.afterEvaluate {
            if (!project.plugins.hasPlugin('com.android.application')) throw new IllegalArgumentException("replugin-host 插件必须在app模块配置")
            def android = project.extensions.getByType(AppExtension)
            config = project.extensions.getByName(Constants.HOST_CONFIG)
            Checker.checkHostConfig(config)
            android.applicationVariants.all { variant ->
                addShowPluginTask(variant)
                def generateBuildConfigTask = VariantCompat.getGenerateBuildConfigTask(variant)
                //host generate task
                def generateHostConfigTask = Util.createTask(project, "${Constants.TASK_GENERATE}HostConfig") {
                    doLast {
                        FileCreators.createHostConfig(project, variant, config)
                    }
                }
//                //depends on build config task
                if (generateBuildConfigTask) {
                    generateHostConfigTask.dependsOn generateBuildConfigTask
                    generateBuildConfigTask.finalizedBy generateHostConfigTask
                }
//                //json generate task
                def generateBuiltinJsonTask = Util.createTask(project, "${Constants.TASK_GENERATE}BuiltinJson") {
                    doLast {
                        FileCreators.createBuiltinJson(project, variant, config)
                    }
                }
                //depends on mergeAssets Task
                def mergeAssetsTask = VariantCompat.getMergeAssetsTask(variant)
                if (mergeAssetsTask) {
                    generateBuiltinJsonTask.dependsOn mergeAssetsTask
                    mergeAssetsTask.finalizedBy generateBuiltinJsonTask
                }
                //                def appID = generateBuildConfigTask.appPackageName
                def appID = [variant.mergedFlavor.applicationId, variant.buildType.applicationIdSuffix].findAll().join()
                def newManifest = ComponentsGenerator.generateComponent(appID, config)
                variant.outputs.each { output ->
                    VariantCompat.getProcessManifestTask(output).doLast {
                        println "${Constants.TAG} processManifest: ${it.outputs.files}"
                        it.outputs.files.each { File file ->
                            updateManifest(file, newManifest)
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @hyongbai
     * 
     * 在gradle plugin 3.0.0之前，file是文件，且文件名为AndroidManifest.xml
     * 在gradle plugin 3.0.0之后，file是目录，(特别是3.3.2)在这里改成递归的方式替换内部所有的 manifest 文件
     *
     * @param file manifest文件
     * @param newManifest 需要添加的 manifest 信息
     */
    def updateManifest(def file, def newManifest) {
        // 除了目录和AndroidManifest.xml之外，还可能会包含manifest-merger-debug-report.txt等不相干的文件，过滤它
        if (file == null || !file.exists() || newManifest == null) return
        if (file.isDirectory()) {
            println "${TAG} updateManifest: ${file}"
            file.listFiles().each {
                updateManifest(it, newManifest)
            }
        } else if (file.name.equalsIgnoreCase("AndroidManifest.xml")) {
            appendManifest(file, newManifest)
        }
    }

    def appendManifest(def file, def content) {
        if (file == null || !file.exists()) return
        println "${TAG} appendManifest: ${file}"
        def updatedContent = file.getText("UTF-8").replaceAll("</application>", content + "</application>")
        file.write(updatedContent, 'UTF-8')
    }

    // 添加 【查看所有插件信息】 任务
    def addShowPluginTask(def variant) {
        def showPluginsTask = Util.createTask(project, (Constants.TASK_SHOW_PLUGIN)) {
            doLast {
                IFileCreator creator = new PluginBuiltinJsonCreator(project, variant, config)
                def dir = creator.getFileDir()

                if (!dir.exists()) {
                    println "${TAG} The ${dir.absolutePath} does not exist "
                    println "${TAG} pluginsInfo=null"
                    return
                }

                String fileContent = creator.getFileContent()
                if (null == fileContent) {
                    return
                }

                new File(dir, creator.getFileName()).write(fileContent, 'UTF-8')
            }
        }

        //get mergeAssetsTask name, get real gradle task
        def mergeAssetsTask = VariantCompat.getMergeAssetsTask(variant)

        //depend on mergeAssetsTask so that assets have been merged
        if (mergeAssetsTask) {
            showPluginsTask.dependsOn mergeAssetsTask
        }

    }
}