package org.jetbrains.kotlin.experimental.gradle.plugin.plugins

import org.gradle.api.Plugin
import org.gradle.api.internal.FeaturePreviews
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.BasePlugin
import org.gradle.language.plugins.NativeBasePlugin
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeBinary
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeExecutable
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeKlib
import org.jetbrains.kotlin.experimental.gradle.plugin.ProductionKotlinNativeComponent
import org.jetbrains.kotlin.experimental.gradle.plugin.internal.DefaultKotlinNativeBinary
import org.jetbrains.kotlin.experimental.gradle.plugin.internal.DefaultKotlinNativeExecutable
import org.jetbrains.kotlin.gradle.plugin.KonanBuildingConfig
import org.jetbrains.kotlin.gradle.plugin.environmentVariables
import org.jetbrains.kotlin.gradle.plugin.hostManager
import org.jetbrains.kotlin.gradle.plugin.targetSubdir
import org.jetbrains.kotlin.gradle.plugin.tasks.KonanBuildingTask
import org.jetbrains.kotlin.gradle.plugin.tasks.KonanCompileLibraryTask
import org.jetbrains.kotlin.gradle.plugin.tasks.KonanCompileProgramTask
import org.jetbrains.kotlin.konan.target.KonanTarget


class KotlinNativeBasePlugin: Plugin<ProjectInternal> {

    private fun ProjectInternal.createCompileTask(binary: DefaultKotlinNativeBinary): KonanBuildingTask {
        val names = binary.names
        val taskName = names.getCompileTaskName(LANGUAGE_NAME)
        return when (binary) {
            is KotlinNativeExecutable -> tasks.create(taskName, KonanCompileProgramTask::class.java).apply {
                val destination = binary.executableFile.get().asFile.parentFile
                //val name =

                group = BasePlugin.BUILD_GROUP
                // TODO: Separate description for executables and libraries
                description = "Builds Kotlin/Native artifact ${binary.getName()}"
            }
            is KotlinNativeKlib -> tasks.create(taskName, KonanCompileLibraryTask::class.java).apply {

            }
            // TODO: Use sealed classes?
            else -> throw IllegalArgumentException("Cannot determine building task for binary: ${this}")
        }
    }
    override fun apply(project: ProjectInternal): Unit = with(project) {
        pluginManager.apply(NativeBasePlugin::class.java)
        gradle.services.get(FeaturePreviews::class.java).enableFeature(FeaturePreviews.Feature.GRADLE_METADATA)
        val tasks = project.tasks
        // TODO: Add a konan extension. Remove it when konan toolchain will be supported


        project.components.withType(DefaultKotlinNativeBinary::class.java) { binary ->
            val target = binary.konanTarget
            if (hostManager.isEnabled(target)) {
                logger.info("The target is not enabled on the current host: ${target.visibleName}")
                return@withType
            }

            // TODO: check if target is supported for framework and wasm (see targetIsSupported in KonanBuildingConfig)
            val names = binary.names
            //val compileTaks = tasks.create(names.getCompileTaskName(LANGUAGE_NAME), binary.compileTaskClass).apply {
//
            //}


        }

        project.components.withType(ProductionKotlinNativeComponent::class.java) {
            // Create publications for all these tasks
        }

    }

    companion object {
        const val LANGUAGE_NAME = "KotlinNative"
    }

}
