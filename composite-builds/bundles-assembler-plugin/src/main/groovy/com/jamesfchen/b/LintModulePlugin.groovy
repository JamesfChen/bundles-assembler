package com.jamesfchen.b

import org.gradle.api.Project

class LintModulePlugin extends JarModulePlugin {
    @Override
    void onApply(Project project) {
        super.onApply(project)
        project.dependencies {
            //30.0.4
            if (project.rootProject.hasProperty("LINT_VERSION")) {
                compileOnly "com.android.tools.lint:lint-api:${project.rootProject.LINT_VERSION}"
                compileOnly "com.android.tools.lint:lint-checks:${project.rootProject.LINT_VERSION}"
            }
        }
    }
}