package com.qihoo360.replugin
class Constants{
    /** 版本号 */
    def static final VER = "1.0.0"
//    def static final VER = "${RP_VERSION}"
    /** 打印信息时候的前缀 */
    def static final TAG = "< replugin-plugin-v${VER} >"
    /** 用户Task组 */
    def static final TASKS_GROUP = "replugin-plugin"

    /** 外部用户配置信息 */
    def static final HOST_CONFIG = "repluginHostConfig"

    /** 外部用户配置信息 */
    def static final PARASITE_CONFIG = "repluginPluginConfig"

    /** Task前缀 */
    def static final TASKS_PREFIX = "rp"

    /** 用户Task:安装插件 */
    def static final TASK_SHOW_PLUGIN = TASKS_PREFIX + "ShowPlugins"

    /** 用户Task:Generate任务 */
    def static final TASK_GENERATE = TASKS_PREFIX + "Generate"

    /** 用户Task:强制停止宿主app */
    def static final TASK_FORCE_STOP_HOST_APP = TASKS_PREFIX + "ForceStopHostApp"

    /** 用户Task:启动宿主app */
    def static final TASK_START_HOST_APP = TASKS_PREFIX + "StartHostApp"

    /** 用户Task:重启宿主app */
    def static final TASK_RESTART_HOST_APP = TASKS_PREFIX + "RestartHostApp"


    /** 用户Task:安装插件 */
    def static final TASK_INSTALL_PLUGIN = TASKS_PREFIX + "InstallPlugin"

    /** 用户Task:安装插件 */
    def static final TASK_UNINSTALL_PLUGIN = TASKS_PREFIX + "UninstallPlugin"

    /** 用户Task:运行插件 */
    def static final TASK_RUN_PLUGIN = TASKS_PREFIX + "RunPlugin"

    /** 用户Task:安装并运行插件 */
    def static final TASK_INSTALL_AND_RUN_PLUGIN = TASKS_PREFIX + "InstallAndRunPlugin"


    /** 配置例子 */
    static final String CONFIG_EXAMPLE = '''
// 这个plugin需要放在android配置之后，因为需要读取android中的配置项
apply plugin: 'replugin-plugin-gradle\'
repluginPluginConfig {
    pluginName = "demo3"
    hostApplicationId = "com.qihoo360.replugin.sample.host"
    hostAppLauncherActivity = "com.qihoo360.replugin.sample.host.MainActivity"
}
'''
}