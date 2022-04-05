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

import com.jamesfchen.FastInsertCodePlugin
import com.jamesfchen.P
import com.qihoo360.replugin.Checker
import com.qihoo360.replugin.Constants
import com.qihoo360.replugin.util.AdbCli
import com.qihoo360.replugin.util.Util
import com.qihoo360.replugin.util.VariantCompat
import javassist.ClassPool
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.lang.Integer
import java.util.regex.Pattern

/**
 * @author RePlugin Team
 */
class ParasitePlugin extends FastInsertCodePlugin {
    private Project mProject
    private ParasiteConfig mConfig
    private ClassPool mPool
    private Map<String, BaseInjector> mInjectors = new HashMap<>()

    @Override
    protected String pluginName() {
        return "Parasite"
    }

    @Override
    void apply(Project project) {
        mProject = project
        P.info("project[${project}] apply ${this.getClass().getSimpleName()}")
        project.extensions.create(Constants.PARASITE_CONFIG, ParasiteConfig)
        project.afterEvaluate {
//        def androidJar = Util.findAndroidJarPath(project,30)
//        def androidJar = Util.findAndroidJarPath(project,project.android.defaultConfig.targetSdkVersion.getApiLevel())
//            project.dependencies.add("compileOnly", androidJar)
            //plugins{
            //    id 'io.github.jamesfchen.replugin-parasite-plugin'
            //}
            //plugins设置插件的方式导致project在evaluate时，project.plugins.hasPlugin('com.android.application')找不到,需要在afterEvaluate才能找到
            super.apply(project)
            if (!project.plugins.hasPlugin('com.android.application')) throw new IllegalArgumentException("replugin-parasite 插件必须在app模块配置")
            mConfig = project.extensions.getByName(Constants.PARASITE_CONFIG)
            Checker.checkParasiteConfig(mConfig)

            project.android.applicationVariants.all { variant ->
                def adb = AdbCli.create(project)
                File apkDir = new File(project.getBuildDir(), "outputs" + File.separator + "apk")
                def buildType = variant.buildType.name
                def apkName = ''
                variant.outputs.each {
                    apkName = it.outputFileName
//                    println("cjf ${it.outputFileName}")
                }
                def flavor = variant.getFlavorName()
                def apkFile = new File("${apkDir}/$flavor/$buildType", apkName)

                def assembleTask = VariantCompat.getAssembleTask(variant)
                def installPluginTask = Util.createTask(project, Constants.TASK_INSTALL_PLUGIN) {
                    doLast {
                        adb.startHostApp("${mConfig.hostApplicationId}/${mConfig.hostAppLauncherActivity}")
                        adb.uninstallParasite(mConfig.hostApplicationId, mConfig.pluginName)
                        adb.forceStopHostApp(mConfig.hostApplicationId)
                        adb.startHostApp("${mConfig.hostApplicationId}/${mConfig.hostAppLauncherActivity}")
                        adb.installParasite(mConfig.hostApplicationId, mConfig.pluginName, apkFile, onfig.phoneStorageDir)
                    }
                }

                if (assembleTask) {
                    installPluginTask.dependsOn assembleTask
                }

                def uninstallPluginTask = Util.createTask(project, Constants.TASK_UNINSTALL_PLUGIN) {
                    doLast {
                        //generate json
                        adb.uninstallParasite(mConfig.hostApplicationId, mConfig.pluginName)
                    }
                }

                def forceStopHostAppTask = Util.createTask(project, Constants.TASK_FORCE_STOP_HOST_APP) {
                    doLast {
                        adb.forceStopHostApp(mConfig.hostApplicationId)
                        //generate json
                    }
                }

                def startHostAppTask = Util.createTask(project, Constants.TASK_START_HOST_APP) {
                    doLast {
                        adb.startHostApp("${mConfig.hostApplicationId}/${mConfig.hostAppLauncherActivity}")
                    }
                }

                def restartHostAppTask = Util.createTask(project, Constants.TASK_RESTART_HOST_APP) {
                    doLast {
                        //generate json
                        adb.startHostApp("${mConfig.hostApplicationId}/${mConfig.hostAppLauncherActivity}")
                    }
                }
                restartHostAppTask.dependsOn(forceStopHostAppTask)


                def runPluginTask = Util.createTask(project, Constants.TASK_RUN_PLUGIN) {
                    doLast {
                        adb.runParasite(mConfig.hostApplicationId, mConfig.pluginName)
                    }
                }
                def installAndRunPluginTask = Util.createTask(project, Constants.TASK_INSTALL_AND_RUN_PLUGIN) {
                    doLast {
                        adb.runParasite(mConfig.hostApplicationId, mConfig.pluginName)
                    }
                }
                installAndRunPluginTask.dependsOn installPluginTask
            }

            CommonData.appPackage = project.android.defaultConfig.applicationId

            println ">>> APP_PACKAGE " + CommonData.appPackage
        }
    }
    /**
     * 欢迎
     */
    def welcome() {
        println '\n'
        60.times { print '=' }
        println '\n                    replugin-plugin-gradle'
        60.times { print '=' }
        println("""
Add repluginPluginConfig to your build.gradle to enable this plugin:

repluginPluginConfig {
    // Name of 'App Module'，use '' if root dir is 'App Module'. ':app' as default.
    appModule = ':app'

    // Injectors ignored
    // LoaderActivityInjector: Replace Activity to LoaderActivity
    // ProviderInjector: Inject provider method call.
    ignoredInjectors = ['LoaderActivityInjector']
}""")
        println('\n')
    }

    @Override
    void onInsertCodeBegin() {
        welcome()
        mPool = new ClassPool(true)
        //'替换 Activity 为 LoaderActivity'
        mInjectors.put("LoaderActivityInjector", new LoaderActivityInjector(mProject,mPool))
        //'替换 LocalBroadcast 调用'
        mInjectors.put("LocalBroadcastInjector", new LocalBroadcastInjector(mProject,mPool))
        //'替换 Provider 调用'
        mInjectors.put("ProviderInjector", new ProviderInjector(mProject,mPool))
        //'替换 ContentProviderClient 调用'
        mInjectors.put("ProviderInjector2", new ProviderInjector2(mProject,mPool))
        //'替换 Resource.getIdentifier 调用'
        mInjectors.put("GetIdentifierInjector", new GetIdentifierInjector(mProject,mPool))
        mConfig.ignoredInjectors.each {
            println ">>> Skip: ${it.nickName}"
            mInjectors.remove(it)
        }
        mInjectors.each { nickName, injector ->
            injector.onInsertCodeBegin()
        }

    }

    @Override
    byte[] onInsertCode(File mather, InputStream classStream, String canonicalName) {
        def classPath = canonicalName.replace(".", File.separator) + ".class"
        mPool.insertClassPath(mather.absolutePath)
        def variantDir = mather.getParentFile().absolutePath.split(pluginName() + "Transform" + Pattern.quote(File.separator))[1]
        def variantDirArray = variantDir.split(Pattern.quote(File.separator))
        String variantName = ""
        variantDirArray.each {
            //首字母大写进行拼接
            variantName += it.capitalize()
        }
        println ">>> variantName:${variantName}"
        mInjectors.each { nickName, injector ->
            injector.setVariantName(variantName)
            println ">>> Do: ${nickName}"
            // 将 NickName 的第 0 个字符转换成小写，用作对应配置的名称
            injector.onInsertCode(mather, classStream, canonicalName)
        }
        if (mConfig.customInjectors != null) {
            mConfig.customInjectors.each {
                injector.onInsertCode(mather, classStream, canonicalName)
            }
        }
        return null
    }

    @Override
    void onInsertCodeEnd() {
        mInjectors.each { nickName, injector ->
            injector.onInsertCodeEnd()
        }
    }
}
