package com.jamesfchen.b

import org.gradle.api.Project

class JarModulePlugin extends AbsModuleifyPlugin {

    @Override
    protected String mainPlugin() {
        return 'java-library'
    }

    @Override
    void addPlugins(Project project) {
        project.plugins.apply('kotlin')
    }

    @Override
    void onApply(Project project) {
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
}