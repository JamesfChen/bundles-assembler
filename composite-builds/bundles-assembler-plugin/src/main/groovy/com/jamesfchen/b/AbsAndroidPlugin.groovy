package com.jamesfchen.b

import org.gradle.api.JavaVersion
import org.gradle.api.Project

abstract class AbsAndroidPlugin extends AbsModuleifyPlugin {
    @Override
    void onApply(Project project) {
        project.android {
            compileSdkVersion Integer.parseInt(project.rootProject.compileSdkVersion)
            buildToolsVersion project.rootProject.buildToolsVersion
            defaultConfig {
                minSdkVersion Integer.parseInt(project.rootProject.minSdkVersion)
                targetSdkVersion Integer.parseInt(project.rootProject.targetSdkVersion)
                def hited = project.rootProject.hasProperty("versionCode") || project.rootProject.hasProperty("versionName")
                if (hited) {
                    versionCode Integer.parseInt(project.rootProject.versionCode)
                    versionName project.rootProject.versionName
                }
                testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
            }
            compileOptions {
                sourceCompatibility JavaVersion.VERSION_1_8
                targetCompatibility JavaVersion.VERSION_1_8
            }
            buildTypes {
                debug {
                    minifyEnabled false
                    proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
                }
                release {
                    minifyEnabled false
                    proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
                }
            }
            variantFilter { variant ->
                def flavors = variant.flavors*.name
                def buildType = variant.buildType.name
                def a = project.gradle.activeBuildVariant.toLowerCase()
                if (!a.contains(buildType)) {
                    setIgnore(true)
                }
                for (def flavor in flavors) {
                    if (!a.contains(flavor)) {
                        setIgnore(true)
                    }
                }

            }
        }
    }
}
