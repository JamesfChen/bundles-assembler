package com.jamesfchen.b


import org.gradle.api.Project

abstract class AbsParasiteModulePlugin extends AbsAndroidPlugin {

    @Override
    String mainPlugin() {
        return 'com.android.application'
    }

    @Override
    void addPlugins(Project project) {
        //插件包上传到某个服务器
//        def simepleName = project.gradle.sourcePath2SimpleNameMap[project.path]
//        if (simepleName) {
//            project.plugins.apply("io.github.jamesfchen.module-publisher-plugin")
//            project['publish'].with {
//                name = simepleName
//                groupId = project.gradle.groupId
//                artifactId = simepleName
//                version = "1.0.0-${project.gradle.activeBuildVariant}-SNAPSHOT"
//                website = "https://github.com/JamesfChen/bundles-assembler"
//            }
//        }
    }

    @Override
    void onApply(Project project) {
        super.onApply(project)
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
                debug {
                    signingConfig signingConfigs.debugSigningConfig
                }
                release {
                    signingConfig signingConfigs.releaseSigningConfig
                }
            }
        }
    }

}
