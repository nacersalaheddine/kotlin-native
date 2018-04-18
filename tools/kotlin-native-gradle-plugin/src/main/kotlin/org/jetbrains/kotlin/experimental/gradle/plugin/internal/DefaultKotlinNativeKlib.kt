package org.jetbrains.kotlin.experimental.gradle.plugin.internal

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.file.FileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.language.nativeplatform.internal.ConfigurableComponentWithLinkUsage
import org.gradle.language.nativeplatform.internal.ConfigurableComponentWithRuntimeUsage
import org.gradle.language.nativeplatform.internal.ConfigurableComponentWithStaticLibrary
import org.gradle.nativeplatform.Linkage
import org.gradle.nativeplatform.platform.NativePlatform
import org.gradle.nativeplatform.tasks.CreateStaticLibrary
import org.gradle.nativeplatform.toolchain.NativeToolChain
import org.gradle.nativeplatform.toolchain.internal.PlatformToolProvider
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeKlib
import org.jetbrains.kotlin.experimental.gradle.plugin.sourcesets.KotlinNativeSourceSet
import org.jetbrains.kotlin.experimental.gradle.plugin.toolchain.KotlinNativeToolChain

open class DefaultKotlinNativeKlib(
        name: String,
        baseName: Provider<String>,
        sources: KotlinNativeSourceSet,
        identity: KotlinNativeVariantIdentity,
        objects: ObjectFactory,
        projectLayout: ProjectLayout,
        componentImplementation: Configuration,
        configurations: ConfigurationContainer,
        fileOperations: FileOperations
) : DefaultKotlinNativeBinary(name,
        baseName,
        sources,
        identity,
        objects,
        projectLayout,
        componentImplementation,
        configurations,
        fileOperations),
    KotlinNativeKlib,
    ConfigurableComponentWithStaticLibrary,
    ConfigurableComponentWithLinkUsage,
    ConfigurableComponentWithRuntimeUsage,
    SoftwareComponentInternal
{
    override fun isDebuggable(): Boolean = debuggable
    override fun isOptimized(): Boolean = optimized

    override fun getTargetPlatform(): NativePlatform = targetPlatform
    override fun getToolChain(): NativeToolChain = TODO()

    // Properties

    private val linkElementsProperty: Property<Configuration> = objects.property(Configuration::class.java)
    private val linkFileProperty: Property<RegularFile> = projectLayout.fileProperty()

    // Interface

    override fun getLinkElements(): Property<Configuration> = linkElementsProperty

    override fun getLinkFile(): Property<RegularFile> = linkFileProperty

    override fun getCreateTask(): Property<CreateStaticLibrary> {
        TODO("not implemented")
    }

    override fun getRuntimeElements(): Property<Configuration> {
        TODO("not implemented")
    }

    override fun getPlatformToolProvider(): PlatformToolProvider {
        TODO("not implemented")
    }

    override fun getLinkAttributes(): AttributeContainer {
        TODO("not implemented")
    }

    override fun getLinkage(): Linkage? {
        TODO("not implemented")
    }

    override fun hasRuntimeFile(): Boolean {
        TODO("not implemented")
    }

    override fun getRuntimeFile(): Provider<RegularFile> {
        TODO("not implemented")
    }

    override fun getRuntimeAttributes(): AttributeContainer {
        TODO("not implemented")
    }

    override fun getUsages(): MutableSet<out UsageContext> {
        TODO("not implemented")
    }
}
