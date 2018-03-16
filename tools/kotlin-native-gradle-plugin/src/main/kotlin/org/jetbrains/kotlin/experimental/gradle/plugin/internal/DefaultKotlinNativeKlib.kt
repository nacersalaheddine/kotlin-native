package org.jetbrains.kotlin.experimental.gradle.plugin.internal

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.nativeplatform.platform.NativePlatform
import org.gradle.nativeplatform.toolchain.NativeToolChain
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeKlib

open class DefaultKotlinNativeKlib(
        name: String,
        baseName: Provider<String>,
        sources: FileCollection,
        identity: KotlinNativeVariantIdentity,
        objects: ObjectFactory,
        componentImplementation: Configuration,
        configurations: ConfigurationContainer,
        fileOperations: FileOperations
) : DefaultKotlinNativeBinary(name,
        baseName,
        sources,
        identity,
        objects,
        componentImplementation,
        configurations,
        fileOperations),
    KotlinNativeKlib
{

    override fun isDebuggable(): Boolean = debuggable
    override fun isOptimized(): Boolean = optimized

    private val linkElementsProperty: Property<Configuration> = objects.property(Configuration::class.java)
    override fun getLinkElements(): Provider<Configuration> = linkElementsProperty

    // TODO: Do we need it?
    override fun getTargetPlatform(): NativePlatform = konanTarget.asGradleNativePlatform

    @Deprecated("Not implemented yet")
    override fun getToolChain(): NativeToolChain = TODO("Implement Kotlin/Native toolchain")
}
