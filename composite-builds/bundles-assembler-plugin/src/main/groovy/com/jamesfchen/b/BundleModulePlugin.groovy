package com.jamesfchen.b


import org.gradle.api.Project

class BundleModulePlugin extends AndroidWithMavenPlugin {
    @Override
    String mainPlugin() {
        return 'com.android.library'
    }

    @Override
    void addPlugins(Project project) {
        super.addPlugins(project)
//        if (routerType == TYPE_AROUTER) {
//            project.plugins.apply('com.alibaba.arouter')
//        }
    }

    @Override
    void onApply(Project project) {
        super.onApply(project)
        def  routerName = Initializer.routerName
        def  lifecycleVersion = Initializer.lifecycleVersion
        def navigationVersion = Initializer.navigationVersion
        if ('ARouter' == routerName) {
            project['kapt'].arguments {
                arg("AROUTER_MODULE_NAME", project.getName())
            }
        }
        project.dependencies {
            if (lifecycleVersion) {
                api "io.github.jamesfchen:lifecycle-api:$lifecycleVersion"
            }
            api Initializer.routerLibrary
            if ('ARouter' == routerName) {
                kapt 'com.alibaba:arouter-compiler:1.2.1'
            } else if ('WRouter' == routerName) {
                annotationProcessor 'io.github.meituan-dianping:compiler:1.2.1'
            }
            if (navigationVersion) {
                api "androidx.navigation:navigation-fragment:$navigationVersion"
                api "androidx.navigation:navigation-runtime-ktx:$navigationVersion"
                api "androidx.navigation:navigation-common-ktx:$navigationVersion"
                api "androidx.navigation:navigation-ui:$navigationVersion"
                api "androidx.navigation:navigation-fragment-ktx:$navigationVersion"
                api "androidx.navigation:navigation-ui-ktx:$navigationVersion"
            }
        }
    }
}