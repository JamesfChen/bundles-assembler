package com.jamesfchen.b

import org.gradle.api.Project

class ApiModulePlugin extends AndroidWithMavenPlugin {
    @Override
    String mainPlugin() {
        return 'com.android.library'
    }
    @Override
    void onApply(Project project) {
        super.onApply(project)
        project.dependencies {
            api Initializer.routerLibrary
        }
    }
}