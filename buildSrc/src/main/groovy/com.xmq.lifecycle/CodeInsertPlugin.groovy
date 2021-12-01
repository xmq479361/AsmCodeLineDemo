package com.xmq.lifecycle

import com.android.build.gradle.AppExtension
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
//        android.registerTransform(new CodeLineInsertAsmTransform())
        android.registerTransform(new CodeInsertJavassistTransform())
    }
}