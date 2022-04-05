package com.jamesfchen.b

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * -整个app进行打包，最后输出apk
 * - 对某一个模块进行打包，最后输出aar
 * - 打包某个模块的热修插件
 */
class ModuleChefPlugin implements Plugin<Project> {
    static void configModule(Project project, String moduleName, boolean includeModule, boolean isBinary = null) {
        def localProperties = new Properties()
        def localPropertiesFile = new File(project.rootDir, 'local.properties')
        if (localPropertiesFile.exists()) {
            localPropertiesFile.withReader('UTF-8') { reader ->
                localProperties.load(reader)
            }
        }
        String sm = localProperties.getProperty("sourceModules")
        if (includeModule) {
            if (isBinary && sm.contains(moduleName)) {
                sm = sm.replace("${moduleName},", "")
                updateSourceModules(localPropertiesFile, localProperties, sm)
            } else if (!isBinary && !sm.contains(moduleName)) {
                sm = "${sm}${moduleName},"
                updateSourceModules(localPropertiesFile, localProperties, sm)
            }
        } else {
            sm = sm.replace("${moduleName},", "")
            updateSourceModules(localPropertiesFile, localProperties, sm)
        }


        String em = localProperties.getProperty("excludeModules")
        if (includeModule) {
            if (em.contains(moduleName)) {
                em = em.replace("${moduleName},", "")
                updateExcludeModules(localPropertiesFile, localProperties, em)
            }
        } else {
            if (!em.contains(moduleName)) {
                em = "${em}${moduleName},"
                updateExcludeModules(localPropertiesFile, localProperties, em)
            }
        }
    }

    static void updateSourceModules(File localPropertiesFile, Properties localProperties, String sm) {
        localProperties.setProperty("sourceModules", sm)
        OutputStream propertyFileOs = new FileOutputStream(localPropertiesFile)
        localProperties.store(propertyFileOs, "update modules")
        propertyFileOs.close()
    }

    static void updateExcludeModules(File localPropertiesFile, Properties localProperties, String em) {
        localProperties.setProperty("excludeModules", em)
        OutputStream propertyFileOs = new FileOutputStream(localPropertiesFile)
        localProperties.store(propertyFileOs, "update modules")
        propertyFileOs.close()
    }

    static void includeM(Project project, String moduleName, boolean isBinary) {
        configModule(project, moduleName, true, isBinary)
    }

    static void excludeM(Project project, String moduleName) {
        configModule(project, moduleName, false)

    }

    static def isWindows() {
        return System.properties['os.name'].contains('Windows');
    }

    @Override
    void apply(Project project) {
//        project.tasks.create(name: "includeSourceModule", group: 'module chef') {
//            doLast {
//                if (project.hasProperty("moduleName")) {
//                    def moduleName = project.getProperty("moduleName")
//                    includeM(project, moduleName, false)
//                } else {
//                    throw new IllegalArgumentException("请传想要include的模块 eg. ./gradlew includeSourceModule -PmoduleName=home-myhome")
//                }
//            }
//
//        }
//        project.tasks.create(name: "includeBinaryModule", group: 'module chef') {
//            doLast {
//                if (project.hasProperty("moduleName")) {
//                    def moduleName = project.getProperty("moduleName")
//                    includeM(project, moduleName, true)
//                } else {
//                    throw new IllegalArgumentException("请传想要include的模块 eg. ./gradlew includeBinaryModule -PmoduleName=home-myhome")
//                }
//            }
//        }
//        project.tasks.create(name: "excludeModule", group: 'module chef') {
//            doLast {
//                if (project.hasProperty("moduleName")) {
//                    def moduleName = project.getProperty("moduleName")
//                    excludeM(project, moduleName)
//                } else {
//                    throw new IllegalArgumentException("请传想要exclude的模块 eg. ./gradlew excludeModule -PmoduleName=home-myhome")
//                }
//            }
//        }
//        project.tasks.create(name: "includeAll", group: 'module chef') {
//            doLast {
//                def localProperties = new Properties()
//                def localPropertiesFile = new File(project.rootDir, 'local.properties')
//                if (localPropertiesFile.exists()) {
//                    localPropertiesFile.withReader('UTF-8') { reader ->
//                        localProperties.load(reader)
//                    }
//                }
//                StringBuffer sourcesb = new StringBuffer();
//                project.gradle.ext.allModules.each { m ->
//                    sourcesb.append(m.simpleName);
//                    sourcesb.append(",");
//                }
//                localProperties.setProperty("sourceModules", sourcesb.toString());
//                OutputStream outputstream = new FileOutputStream(localPropertiesFile);
//                localProperties.store(outputstream, "update modules")
//
//                localProperties.setProperty("excludeModules", "")
//                localProperties.store(new FileOutputStream(localPropertiesFile), "update modules")
//            }
//        }
//        project.tasks.create(name: "publishModule", group: 'module chef') {
//            doLast {
//                if (project.hasProperty("moduleName")) {
//                    def moduleName = project.getProperty("moduleName")
//                    includeM(project, moduleName, false)
//                    project.gradle.ext.sourceModuleMap.each { name, module ->
//                        if (name != moduleName) {
//                            project.exec {
//                                executable "$project.rootDir$File.separator" + (isWindows() ? 'gradlew.bat' : 'gradlew')
//                                workingDir project.rootDir
//                                def argv = []
//                                argv << "${module.sourcePath}:assemble"
//                                argv << "publishToMavenLocal"
//                                args = argv
//                                println("command line:${commandLine}")
//                            }
//                        }
//                    }
//                } else {
//                    throw new IllegalArgumentException("请传想要publish的模块 eg. ./gradlew publishModule -PmoduleName=home-myhome")
//                }
//            }
//        }
//        project.tasks.create(name: "publishFwk", group: 'module chef') {
//            doFirst {
//                if (project.gradle.ext.framworkSrcModuleMap.isEmpty()) {
//                    println("没有framework模块")
//                }
//            }
//            doLast {
//                def ret = []
//                try {
//                    project.gradle.ext.framworkSrcModuleMap.each { name, module ->
//                        ret += name
//                        project.exec {
//                            executable "$project.rootDir$File.separator" + (isWindows() ? 'gradlew.bat' : 'gradlew')
//                            workingDir project.rootDir
//                            def argv = []
//                            argv << "${module.sourcePath}:publishToMavenLocal"
//                            args = argv
//                            println("command line:${commandLine}")
//                        }
//                    }
//                } catch (Exception ignored) {
//                    println(">>>> 出现异常没有全部发布，需要动手处理一下 !!!!!! !!!!!!")
//                    println("$ignored")
//                } finally {
//                    Collections.sort(ret)
//                    println(">>>> publish ${ret.size()} modules")
//                    println(">>>> ${ret}")
//                }
//            }
//        }
//
//        project.tasks.create(name: "publishBundle", group: 'module chef') {
//            doFirst {
//                if (project.gradle.ext.bundleSrcModuleMap.isEmpty()) {
//                    println("没有bundle模块")
//                }
//            }
//            doLast {
//                def ret = []
//                try {
//                    project.gradle.ext.bundleSrcModuleMap.each { name, module ->
//                        ret += name
//                        project.exec {
//                            executable "$project.rootDir$File.separator" + (isWindows() ? 'gradlew.bat' : 'gradlew')
//                            workingDir project.rootDir
//                            def argv = []
//                            argv << "${module.sourcePath}:publishToMavenLocal"
//                            args = argv
//                            println("command line:${commandLine}")
//                        }
//                    }
//                } catch (Exception ignored) {
//                    println(">>>> 出现异常没有全部发布，需要动手处理一下 !!!!!! !!!!!!")
//                    println("$ignored")
//                } finally {
//                    Collections.sort(ret)
//                    println(">>>> publish ${ret.size()} modules")
//                    println(">>>> ${ret}")
//                }
//            }
//        }
///**
// *
// * 模块之间的依赖关系如下
// *        app__
// /   \
// b1     b2
// \     /
// loader
// /  \
// image net ...
// \     /
// common
// * 优化发布：当common模块被编译就将其发布到本地maven，然后再并发编译image net 等模块这个过程集成的是common二进制组件，在本地maven，然后再编译loader，打包出一个供上传使用的包
// */
//        def assemble = project.getTasksByName("assemble", true)
//        project.tasks.create(name: 'publishAll', group: 'module chef', dependsOn: ['includeAll', assemble, 'publishFwk', 'publishBundle'])
//        project.tasks.create(name: "buildBigApp", group: 'module chef', dependsOn: 'includeAll') {
//            doLast {
//                project.exec {
//                    executable "$project.rootDir$File.separator" + (isWindows() ? 'gradlew.bat' : 'gradlew')
//                    workingDir project.rootDir
//                    def argv = []
//                    argv << "app:assemble"
//                    args = argv
//                }
//            }
//        }
    }
}
