package com.qihoo360.replugin.util

import com.qihoo360.replugin.Constants
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection

class Util {
    static def createTask(Project project, String taskName, Closure configureClosure) {
        return project.tasks.create(name: taskName, group: Constants.TASKS_GROUP, configureClosure)
    }
    /**
     * 将字符串的某个字符转换成 小写
     *
     * @param str 字符串
     * @param index 索引
     *
     * @return 转换后的字符串
     */
    def static lowerCaseAtIndex(String str, int index) {
        def len = str.length()
        if (index > -1 && index < len) {
            def arr = str.toCharArray()
            char c = arr[index]
            if (c >= 'A' && c <= 'Z') {
                c += 32
            }

            arr[index] = c
            arr.toString()
        } else {
            str
        }
    }
    /**
     * 编译环境中 android.jar 的路径
     * [查找某个版本的android.jar](https://github.com/stepango/android-jar/blob/master/src/main/java/com/stepango/androidjar/AndroidJar.kt)
     */
    static ConfigurableFileCollection findAndroidJarPath(project, int sdkVersion) {
//        return ScopeCompat.getAndroidJar(globalScope)
        return project.files("${findSdkLocation(project)}/platforms/android-$sdkVersion/android.jar")
    }


    static File findSdkLocation(Project project){
        def rootDir = project.rootDir
        def localProperties = new File(rootDir, "local.properties")
        if (localProperties.exists()) {
            def properties = new Properties()
            new FileInputStream(localProperties).withCloseable { instr ->
                properties.load(instr)
            }
            def sdkDirProp = properties.getProperty("sdk.dir")
            if (sdkDirProp != null) {
                return new File(sdkDirProp)
            } else {
                sdkDirProp = properties.getProperty("android.dir")
                if (sdkDirProp != null) {
                    return new File(rootDir, sdkDirProp)
                } else {
                    throw new RuntimeException("No sdk.dir property defined in local.properties file.")
                }
            }
        } else {
            def envVar = System.getenv("ANDROID_HOME")
            if (envVar != null) {
                return new File(envVar)
            } else {
                def property = System.getProperty("android.home")
                if (property != null) {
                    return new File(property)
                }
            }
        }
        throw  new RuntimeException("Can't find SDK path")
    }

    def static newSection() {
        50.times {
            print '--'
        }
        println()
    }
}