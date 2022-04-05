package com.jamesfchen.b


import org.gradle.api.Project

abstract class AndroidWithMavenPlugin extends AbsAndroidPlugin {

    @Override
    void addPlugins(Project project) {
        def simepleName = project.gradle.sourcePath2SimpleNameMap[project.path]
        if (simepleName) {
            project.plugins.apply("io.github.jamesfchen.module-publisher-plugin")
            project['publish'].with {
                name = simepleName
                groupId = project.gradle.groupId
                artifactId = simepleName
                version = "1.0.0-${project.gradle.activeBuildVariant}-SNAPSHOT"
                website = "https://github.com/JamesfChen/bundles-assembler"
            }
        }
    }
    @Override
    void onApply(Project project) {
        super.onApply(project)
    }
}
