package com.xmq.lifecycle.core

import com.android.build.gradle.AppExtension
import com.xmq.lifecycle.ext.HookExtension
import org.gradle.api.Project
import org.gradle.api.Plugin

class CodeInsertPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.logger.warn(">>LifecyclePlugin apply: ${project.name}")
        System.out.println(":::lifecycle apply")
        if (project == project.rootProject) {
            return
        }
        def android = project.extensions.getByType(AppExtension)
        project.extensions.create("hookGo", HookExtension, project)
//        android.registerTransform(new CodeLineInsertAsmTransform())
        android.registerTransform(new CodeInsertJavassistTransform())
    }
}