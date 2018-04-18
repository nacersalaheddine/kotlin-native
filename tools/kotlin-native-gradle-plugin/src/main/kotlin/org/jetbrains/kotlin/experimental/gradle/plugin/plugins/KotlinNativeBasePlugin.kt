package org.jetbrains.kotlin.experimental.gradle.plugin.plugins

import org.apache.tools.ant.TaskContainer
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.component.SoftwareComponentContainer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.internal.FactoryNamedDomainObjectContainer
import org.gradle.api.internal.FeaturePreviews
import org.gradle.api.internal.artifacts.ivyservice.projectmodule.DefaultProjectPublication
import org.gradle.api.internal.artifacts.ivyservice.projectmodule.ProjectPublicationRegistry
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.TaskContainerInternal
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.internal.reflect.Instantiator
import org.gradle.language.ComponentWithBinaries
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.language.cpp.ProductionCppComponent
import org.gradle.language.cpp.internal.DefaultCppComponent
import org.gradle.language.nativeplatform.internal.ConfigurableComponentWithExecutable
import org.gradle.language.plugins.NativeBasePlugin
import org.gradle.nativeplatform.plugins.NativeComponentModelPlugin
import org.gradle.nativeplatform.toolchain.internal.plugins.StandardToolChainsPlugin
import org.gradle.swiftpm.internal.SwiftPmTarget
import org.jetbrains.kotlin.container.ComponentContainer
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeBinary
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeExecutable
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeKlib
import org.jetbrains.kotlin.experimental.gradle.plugin.ProductionKotlinNativeComponent
import org.jetbrains.kotlin.experimental.gradle.plugin.internal.DefaultKotlinNativeBinary
import org.jetbrains.kotlin.experimental.gradle.plugin.internal.DefaultKotlinNativeComponent
import org.jetbrains.kotlin.experimental.gradle.plugin.internal.DefaultKotlinNativeExecutable
import org.jetbrains.kotlin.experimental.gradle.plugin.tasks.KotlinNativeCompile
import org.jetbrains.kotlin.experimental.gradle.plugin.tasks.KotlinNativeLinkExecutable
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.tasks.*
import java.io.File
import javax.inject.Inject


class KotlinNativeBasePlugin @Inject constructor(val publicationRegistry: ProjectPublicationRegistry) : Plugin<ProjectInternal> {


    private fun addBaseTasks(components: SoftwareComponentContainer) {
        components.withType(ComponentWithBinaries::class.java) { component ->
            component.binaries.whenElementKnown { binary ->
                components.add(binary)
            }
        }
    }

    private fun addLinkTasks(
            tasks: TaskContainerInternal,
            components: SoftwareComponentContainer,
            buildDirectory: DirectoryProperty,
            providers: ProviderFactory,
            configurations: ConfigurationContainer
    ) {
        fun File.asRegularFile(): Provider<RegularFile> = buildDirectory.file(providers.provider { this.absolutePath })

        // TODO: Change the type. Something like KotlinNativeComponentWithLink?
        components.withType(DefaultKotlinNativeExecutable::class.java) { executabe ->
            println("TTTT!!! Create tasks")
            val names = executabe.names

            val linkTask = tasks.create(
                    names.getTaskName("link"),
                    KotlinNativeLinkExecutable::class.java
            ).apply {
                dependsOn(project.konanCompilerDownloadTask)

                // TODO: REmove:
                configuration = executabe.implementationDependencies
                platformConfiguration = configuration
                configuration.isCanBeResolved = true

                konanTarget = executabe.konanTarget
                destinationDir = buildDirectory.dir("exe/${names.dirName}").get().asFile
                artifactName = executabe.baseName.get()

                libraries {
                    files(executabe.objects)
                    files(executabe.linkLibraries)
                }

                enableDebug = executabe.isDebuggable
                enableOptimizations = executabe.isOptimized
                // TODO: Support output paths according to Gradle's conventions.
            }

            // TODO: Our link task should mimic the Gradle's one to allow this:
            //executabe.linkTask.set(linkTask)
            executabe.getDebuggerExecutableFile().set(linkTask.artifact.asRegularFile())
        }
    }

    private fun addCompieTasks(
            tasks: TaskContainerInternal,
            components: SoftwareComponentContainer,
            buildDirectory: DirectoryProperty,
            configurations: ConfigurationContainer
    ) {
        components.withType(DefaultKotlinNativeBinary::class.java) { binary ->
            val names = binary.names
            val target = binary.konanTarget

            val compileTask = tasks.create(
                    names.getCompileTaskName(LANGUAGE_NAME),
                    KonanCompileLibraryTask::class.java
            ).apply {
                dependsOn(project.konanCompilerDownloadTask)

                println("TTT, create binary: ${binary.baseName}${binary.konanTarget.name}${binary.isDebuggable}")
                configuration = binary.implementationDependencies
                platformConfiguration = configuration
                configuration.isCanBeResolved = true

                konanTarget = binary.konanTarget
                destinationDir = buildDirectory.dir("obj/${names.dirName}").get().asFile
                artifactName = binary.baseName.get()
                srcFiles(binary.sourceSet.getAllSources(target))
                enableDebug = binary.isDebuggable
                enableOptimizations = binary.isOptimized
            }

            binary.objectsDir.set(compileTask.destinationDir) // TODO: Change, see above

            // TODO: Our compile task should mimic the Gradle's one to allow this:
            //binary.compileTask.set(compileTask)
        }
    }


    override fun apply(project: ProjectInternal): Unit = with(project) {
        // TODO: Remove when this feature is available by default
        gradle.services.get(FeaturePreviews::class.java).enableFeature(FeaturePreviews.Feature.GRADLE_METADATA)

        // Apply base plugins
        project.pluginManager.apply(LifecycleBasePlugin::class.java)
        project.pluginManager.apply(KotlinNativeToolchainPlugin::class.java) //  TODO: Remove it

        // TODO: Fixes to work with old tasks. Remove them.
        project.extensions.create(KonanPlugin.KONAN_EXTENSION_NAME, KonanExtension::class.java)
        project.tasks.create(KonanPlugin.KONAN_DOWNLOAD_TASK_NAME, KonanCompilerDownloadTask::class.java)
        // Set additional project properties like konan.home, konan.build.targets etc.
        if (!project.hasProperty(KonanPlugin.ProjectProperty.KONAN_HOME)) {
            project.setProperty(KonanPlugin.ProjectProperty.KONAN_HOME, project.konanCompilerDownloadDir())
            project.setProperty(KonanPlugin.ProjectProperty.DOWNLOAD_COMPILER, true)
        }

        val tasks = project.tasks
        val providers = project.providers
        val buildDirectory = project.layout.buildDirectory
        val components = project.components


        // TODO: Add Lifecycle tasks. See Base Native plugin.*
        addBaseTasks(components)


        // Create Kotlin/Native toolchain
        //StandardToolChainsPlugin

        // Create compile tasks
        addCompieTasks(tasks, components, buildDirectory, configurations)

        // Create link tasks.
        addLinkTasks(tasks, components, buildDirectory, providers, configurations)

        // Support publishing
        // TODO: SwiftPmTarget? O_o
        components.withType(ProductionKotlinNativeComponent::class.java) { component ->
            project.afterEvaluate { project ->
                if (component is DefaultKotlinNativeComponent) {

                    publicationRegistry.registerPublication(
                            project.path,
                            DefaultProjectPublication(
                                    component.displayName,
                                    SwiftPmTarget(component.getBaseName().get()),
                                    false
                            )
                    )
                }
            }
        }
    }

    companion object {
        const val LANGUAGE_NAME = "KotlinNative"
        const val SOURCE_SETS_EXTENSION = "sourceSets"
    }

}
