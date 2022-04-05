package com.jamesfchen.b

import org.gradle.api.Project

class Initializer {
    static String routerName
    static String routerPlugin
    static String routerLibrary
    private static boolean isFirst = true
    static String navigationVersion
    static String lifecycleVersion

    static init(def project) {
        if (isFirst) {
            navigationVersion = project.rootProject.findProperty("NAVIGATION_VERSION")
            lifecycleVersion = project.rootProject.findProperty("LIFECYCLE_VERSION")
            (routerName, routerPlugin, routerLibrary) = pickupRouter(project)
            com.jamesfchen.P.info("pick up router, $routerLibrary")
            isFirst = false
        }
    }

    protected static def pickupRouter(Project project) {
        if (project.rootProject.findProperty("AROUTER_VERSION")
                && project.rootProject.findProperty("WROUTER_VERSION")
                && project.rootProject.findProperty("IBCROUTER_VERSION")) {
            throw new IllegalArgumentException("三个只能选择一个")
        } else if (project.rootProject.findProperty("IBCROUTER_VERSION") && project.rootProject.findProperty("WROUTER_VERSION")) {
            throw new IllegalArgumentException("两个只能选择一个")
        } else if (project.rootProject.findProperty("AROUTER_VERSION") && project.rootProject.findProperty("IBCROUTER_VERSION")) {
            throw new IllegalArgumentException("两个只能选择一个")
        } else if (project.rootProject.findProperty("WROUTER_VERSION") && project.rootProject.findProperty("AROUTER_VERSION")) {
            throw new IllegalArgumentException("两个只能选择一个")
        }
        if (project.rootProject.findProperty("AROUTER_VERSION")) return ["ARouter", "com.alibaba.arouter", "com.alibaba:arouter-api:$project.rootProject.AROUTER_VERSION"]
        if (project.rootProject.findProperty("WROUTER_VERSION")) return ["WRouter", "WMRouter", "io.github.meituan-dianping:router:$project.rootProject.WROUTER_VERSION"]
        if (project.rootProject.findProperty("IBCROUTER_VERSION")) return ["IBCRouter", "io.github.jamesfchen.ibc-plugin", "io.github.jamesfchen:ibc-api:$project.rootProject.IBCROUTER_VERSION"]
        return ["IBCRouter", "io.github.jamesfchen.ibc-plugin", "io.github.jamesfchen:ibc-api:$project.rootProject.IBCROUTER_VERSION"]
    }
}