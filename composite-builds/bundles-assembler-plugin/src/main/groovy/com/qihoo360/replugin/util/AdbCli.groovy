package com.qihoo360.replugin.util

import com.qihoo360.replugin.Constants
import org.gradle.api.Project

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen* @since: Feb/13/2022  Sun
 */
class AdbCli {
    /**
     * 同步阻塞执行命令
     * @param cmd 命令
     * @return 命令执行完毕返回码
     */
    int executeCmd(String cmd) {
        if (adbFile == null || !adbFile.exists()) {
            System.err.println "${Constants.TAG} Could not find the adb file !!!"
            System.exit(-1)
//            return -1
        }
//        int cmdReturnCode

        try {
            println "${Constants.TAG} \$ ${cmd}"

            Process process = cmd.execute()
            process.inputStream.eachLine {
                println "${Constants.TAG} - ${it}"
            }
            process.waitFor()

            cmdReturnCode = process.exitValue()

        } catch (Exception e) {
            System.err.println "${Constants.TAG} the cmd run error !!!"
            System.err.println "${Constants.TAG} ${e}"
            System.exit(-1)
//            return -1
        }

//        return cmdReturnCode
    }
    File adbFile

    private AdbCli(adbFile) {
        this.adbFile = adbFile
    }
    static create(Project project) {
        return new AdbCli(project.android.getAdbExe())
    }

    boolean shell(cmd) {
        return executeCmd("$adbFile shell $cmd") == 0
    }

    boolean push(src, dest) {
        return executeCmd("$adbFile push $src $dest") == 0
    }

    boolean pull(src, dest) {
        return executeCmd("$adbFile pull $src $dest") == 0
    }

//    boolean install(appPath) {
//        return executeCmd("$adbFile install -r -t $appPath") == 0
//    }
    void installParasite(pkgName,srcFile, destDir) {
//        push(apkFile,config.phoneStorageDir)
        push(srcFile, destDir)
        String apkPath = "$destDir"
        if (!apkPath.endsWith("/")) {
            //容错处理
            apkPath += "/"
        }
        apkPath += "${srcFile.name}"
        shell("am broadcast -a ${pkgName}.replugin.install -e path ${apkPath} -e immediately true ")
    }

    void uninstallParasite(pkgName,pluginName) {
        shell("am broadcast -a ${pkgName}.replugin.uninstall -e plugin ${pluginName}")
    }
    boolean runParasite(pkgName,pluginName) {
        shell("am broadcast -a ${pkgName}.replugin.start_activity -e plugin ${pluginName}")
    }
    void forceStopHostApp(pkgName) {
        shell("am force-stop $pkgName")
    }
    void startActivity(activityName) {
        shell("am start -n $activityName s")
    }
    void startHostApp(hostSplashActivity) {
        shell("am start -n $hostSplashActivity -a android.intent.action.MAIN -c android.intent.category.LAUNCHER")
    }
}
