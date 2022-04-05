package com.jamesfchen.b

import org.gradle.api.Project

class AppModulePlugin extends AndroidWithMavenPlugin {
    @Override
    String mainPlugin() {
        return 'com.android.application'
    }
    @Override
    void addPlugins(Project project) {
        super.addPlugins(project)
        Initializer.init(project)
        project.plugins.apply(Initializer.routerPlugin)
        if (Initializer.lifecycleVersion) {
            project.plugins.apply('io.github.jamesfchen.lifecycle-plugin')
        }
        if (Initializer.navigationVersion) {
            project.plugins.apply('androidx.navigation.safeargs')
//        project.plugins.apply('androidx.navigation.safeargs.kotlin')
        }
    }

    @Override
    void onApply(Project project) {
        super.onApply(project)
        project.ext{
            depsConfig = { configuration, simpleName ->
                //source时，源码什么也不做；binary时，插件包从远处服务器下载并copy到assets目录
                def module = project.gradle.ext.pluginBinaryModuleMap[simpleName]
                //该模块是binary，需要被引入到宿主的assets目录
                if (module) {
                    //copy to assets dir
                    if (module.dynamic == 'local-plugin') {
                        println("module:" + simpleName + ">>> copy to assets dir")
                    }
                    return
                }
                path = project.moduleify(simpleName)
                if (path) {
                    project.dependencies.add(configuration, path)
                }
            }
        }
        project.android {
            defaultConfig {
//        multiDexEnabled = true//support android 20 or lower
                applicationId project.rootProject.applicationId
            }
            signingConfigs {
                debugSigningConfig {
                    (keyAlias, keyPassword, storePassword) = [project.rootProject.keyAlias, project.rootProject.keyPassword, project.rootProject.storePassword]
                    storeFile project.file("$project.rootDir/$project.rootProject.storeFilePath")
                    v1SigningEnabled true
                    v2SigningEnabled true

                }
                releaseSigningConfig {
                    (keyAlias, keyPassword, storePassword) = [project.rootProject.keyAlias, project.rootProject.keyPassword, project.rootProject.storePassword]
                    storeFile project.file("$project.rootDir/$project.rootProject.storeFilePath")
                    v1SigningEnabled true
                    v2SigningEnabled true
                }

            }
            buildTypes {
                release {
                    signingConfig signingConfigs.releaseSigningConfig
                }
                debug {
                    signingConfig signingConfigs.debugSigningConfig
                }
            }
        }
    }
}