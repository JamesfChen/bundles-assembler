package com.qihoo360.replugin

import com.qihoo360.replugin.host.HostConfig
import com.qihoo360.replugin.parasite.ParasiteConfig
import org.gradle.api.logging.LogLevel

class Checker {
    /**
     * 检查配置项是否正确
     * @param name 配置项
     * @param count 配置值
     */
    private static def doCheckConfig(def name, def count) {
        if (!(count instanceof Integer) || count < 0) {
            this.project.logger.log(LogLevel.ERROR, "\n--------------------------------------------------------")
            this.project.logger.log(LogLevel.ERROR, " ${Constants.TAG} ERROR: ${name} must be an positive integer. ")
            this.project.logger.log(LogLevel.ERROR, "--------------------------------------------------------\n")
            System.exit(-1)
        }
    }
    /**
     * 检查用户配置项
     */
    static void checkHostConfig(HostConfig config) {
/*
        def persistentName = config.persistentName

        if (persistentName == null || persistentName.trim().equals("")) {
            project.logger.log(LogLevel.ERROR, "\n---------------------------------------------------------------------------------")
            project.logger.log(LogLevel.ERROR, " ERROR: persistentName can'te be empty, please set persistentName in replugin. ")
            project.logger.log(LogLevel.ERROR, "---------------------------------------------------------------------------------\n")
            System.exit(0)
            return
        }
*/
        doCheckConfig("countProcess", config.countProcess)
        doCheckConfig("countTranslucentStandard", config.countTranslucentStandard)
        doCheckConfig("countTranslucentSingleTop", config.countTranslucentSingleTop)
        doCheckConfig("countTranslucentSingleTask", config.countTranslucentSingleTask)
        doCheckConfig("countTranslucentSingleInstance", config.countTranslucentSingleInstance)
        doCheckConfig("countNotTranslucentStandard", config.countNotTranslucentStandard)
        doCheckConfig("countNotTranslucentSingleTop", config.countNotTranslucentSingleTop)
        doCheckConfig("countNotTranslucentSingleTask", config.countNotTranslucentSingleTask)
        doCheckConfig("countNotTranslucentSingleInstance", config.countNotTranslucentSingleInstance)
        doCheckConfig("countTask", config.countTask)
        println config
    }

    /**
     * 检查用户配置项是否为空
     * @param config
     * @return
     */
    static void checkParasiteConfig(ParasiteConfig config) {
        if (null == config) {
            System.err.println "${Constants.TAG} the config object can not be null!!!"
            System.err.println "${Constants.CONFIG_EXAMPLE}"
            System.exit(-1)
        }

        if (null == config.hostApplicationId) {
            System.err.println "${Constants.TAG} the config hostApplicationId can not be null!!!"
            System.err.println "${Constants.CONFIG_EXAMPLE}"
            System.exit(-1)
        }

        if (null == config.hostAppLauncherActivity) {
            System.err.println "${Constants.TAG} the config hostAppLauncherActivity can not be null!!!"
            System.err.println "${Constants.CONFIG_EXAMPLE}"
            System.exit(-1)
        }
    }
}