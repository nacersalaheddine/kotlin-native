package org.jetbrains.kotlin.experimental.gradle.plugin.plugins

import org.gradle.api.Plugin
import org.gradle.api.internal.project.ProjectInternal

// TODO: Move from experimental package. What should be the new package?

/*
    TODO: Improve NativeBase support
    Once we support Kotlin/Native toolchain and klib linking we will be able to use the Gradle's install, link etc tasks.
    To do it we will need to implement interfaces like ConfigurableComponentWithStaticLibrary in according
    binaries. With that the NativeBase plugin will be able to create a set of tasks for this binaries.
    See details in NativeBase plugin.
 */

class KotlinNativeApplicationPlugin: Plugin<ProjectInternal> {

    override fun apply(target: ProjectInternal?) {
        println("TTTT!!!")
    }

}