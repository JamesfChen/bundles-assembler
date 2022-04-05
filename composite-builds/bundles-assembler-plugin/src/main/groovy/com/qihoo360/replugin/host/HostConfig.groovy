package com.qihoo360.replugin.host

import com.qihoo360.replugin.Checker
import com.qihoo360.replugin.Constants

class HostConfig {

    /** 自定义进程的数量(除 UI 和 Persistent 进程) */
    def countProcess = 3

    /** 是否使用常驻进程？ */
    def persistentEnable = true

    /** 常驻进程名称（也就是上面说的 Persistent 进程，开发者可自定义）*/
    def persistentName = ':GuardService'

    /** 背景不透明的坑的数量 */
    def countNotTranslucentStandard = 6
    def countNotTranslucentSingleTop = 2
    def countNotTranslucentSingleTask = 3
    def countNotTranslucentSingleInstance = 2

    /** 背景透明的坑的数量 */
    def countTranslucentStandard = 2
    def countTranslucentSingleTop = 2
    def countTranslucentSingleTask = 2
    def countTranslucentSingleInstance = 3

    /** 宿主中声明的 TaskAffinity 的组数 */
    def countTask = 2

    /**
     * 是否使用 AppCompat 库
     * com.android.support:appcompat-v7:25.2.0
     */
    def useAppCompat = false

    /** HOST 向下兼容的插件版本 */
    def compatibleVersion = 10

    /** HOST 插件版本 */
    def currentVersion = 12

    /** plugins-builtin.json 文件名自定义,默认是 "plugins-builtin.json" */
    def builtInJsonFileName = "plugins-builtin.json"

    /** 是否自动管理 plugins-builtin.json 文件,默认自动管理 */
    def autoManageBuiltInJsonFile = true

    /** assert目录下放置插件文件的目录自定义,默认是 assert 的 "plugins" */
    def pluginDir = "plugins"

    /** 插件文件的后缀自定义,默认是".jar" 暂时支持 jar 格式*/
    def pluginFilePostfix = ".jar"

    /** 当发现插件目录下面有不合法的插件 jar (有可能是特殊定制 jar)时是否停止构建,默认是 true */
    def enablePluginFileIllegalStopBuild = true

    @Override
    String toString() {
        return new StringBuffer()
                .append('--------------------------------------------------------------------------')
                .append('\n')
                .append("${Constants.TAG} useAppCompat=${useAppCompat}")
                .append('\n')
//        println "${Constants.TAG} appID=${appID}"
                .append("${Constants.TAG} countProcess=${countProcess}")
                .append('\n')
        // println "${Constants.TAG} persistentName=${persistentName}"
                .append("${Constants.TAG} countTranslucentStandard=${countTranslucentStandard}")
                .append('\n')
                .append("${Constants.TAG} countTranslucentSingleTop=${countTranslucentSingleTop}")
                .append('\n')
                .append("${Constants.TAG} countTranslucentSingleTask=${countTranslucentSingleTask}")
                .append('\n')
                .append("${Constants.TAG} countTranslucentSingleInstance=${countTranslucentSingleInstance}")
                .append('\n')
                .append("${Constants.TAG} countNotTranslucentStandard=${countNotTranslucentStandard}")
                .append('\n')
                .append("${Constants.TAG} countNotTranslucentSingleTop=${countNotTranslucentSingleTop}")
                .append('\n')
                .append("${Constants.TAG} countNotTranslucentSingleTask=${countNotTranslucentSingleTask}")
                .append('\n')
                .append("${Constants.TAG} countNotTranslucentSingleInstance=${countNotTranslucentSingleInstance}")
                .append('\n')
                .append("${Constants.TAG} countTask=${countTask}")
                .append('\n')
                .append('--------------------------------------------------------------------------')
                .append('\n')
                .toString()
    }
}
