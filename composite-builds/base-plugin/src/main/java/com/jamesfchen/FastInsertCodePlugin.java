package com.jamesfchen;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Set;

public abstract class FastInsertCodePlugin extends AbsInsertCodeTransform implements Plugin<Project> {
    protected abstract String pluginName();

    @Override
    public String getName() {
        return pluginName() + "Transform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
//                    ScanClassesPlugin.this.getScopes()
        return TransformManager.SCOPE_FULL_PROJECT;//app project
    }

    @Override
    public void apply(Project project) {
        if (project.getPlugins().hasPlugin("com.android.application")) {
            AppExtension android = (AppExtension) project.getExtensions().getByType(AppExtension.class);
            //groovy中不能使用匿名内部类，否则会报错
            //A problem occurred configuring project ':app'.
            //> java.lang.NullPointerException (no error message)
            android.registerTransform(this);
        }
    }

}