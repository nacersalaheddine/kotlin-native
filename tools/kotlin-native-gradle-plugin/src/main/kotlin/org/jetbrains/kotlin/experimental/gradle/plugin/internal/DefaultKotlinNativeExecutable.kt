package org.jetbrains.kotlin.experimental.gradle.plugin.internal

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.file.FileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.nativeplatform.platform.NativePlatform
import org.gradle.nativeplatform.toolchain.NativeToolChain
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeExecutable

// TODO: Also we have ConfigurableComponentWithExecutable, ConfigurableComponentWithRuntimeUsage, SoftwareComponentInternal. Do we need to implement them?
// TODO: SoftwareComponentInternal will be replaced by ComponentWithVariants
open class DefaultKotlinNativeExecutable(
        name: String,
        objects: ObjectFactory,
        componentImplementation: Configuration,
        configurations: ConfigurationContainer,
        baseName: Provider<String>,
        sources: FileCollection,
        identity: KotlinNativeVariantIdentity,
        projectLayout: ProjectLayout,
        fileOperations: FileOperations
) : DefaultKotlinNativeBinary(name,
        baseName,
        sources,
        identity,
        objects,
        componentImplementation,
        configurations,
        fileOperations),
    KotlinNativeExecutable
{
    override val executableFile: Provider<RegularFile> = projectLayout.fileProperty()

    override fun getCoordinates(): ModuleVersionIdentifier = identity.coordinates

    override fun isDebuggable(): Boolean = debuggable
    override fun isOptimized(): Boolean = optimized

    override val debuggerExecutableFile: RegularFileProperty = projectLayout.fileProperty()

    private val runtimeElementsProperty: Property<Configuration> = objects.property(Configuration::class.java)
    override fun getRuntimeElements(): Property<Configuration> = runtimeElementsProperty

    // TODO: Do we need it?
    override fun getTargetPlatform(): NativePlatform = konanTarget.asGradleNativePlatform

    @Deprecated("Not implemented yet")
    override fun getToolChain(): NativeToolChain = TODO("Implement Kotlin/Native toolchain")

}
