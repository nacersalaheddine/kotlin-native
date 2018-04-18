package org.jetbrains.kotlin.experimental.gradle.plugin.internal

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Usage
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.language.ComponentWithDependencies
import org.gradle.language.ComponentWithOutputs
import org.gradle.api.component.PublishableComponent
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property
import org.gradle.api.tasks.util.PatternSet
import org.gradle.language.cpp.CppBinary
import org.gradle.language.internal.DefaultNativeBinary
import org.gradle.language.nativeplatform.internal.ComponentWithNames
import org.gradle.nativeplatform.platform.NativePlatform
import org.gradle.nativeplatform.toolchain.NativeToolChain
import org.gradle.nativeplatform.toolchain.internal.PlatformToolProvider
import org.jetbrains.kotlin.experimental.gradle.plugin.ComponentWithBaseName
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeBinary
import org.jetbrains.kotlin.experimental.gradle.plugin.sourcesets.KotlinNativeSourceSet
import org.jetbrains.kotlin.experimental.gradle.plugin.tasks.KotlinNativeCompile
import org.jetbrains.kotlin.experimental.gradle.plugin.toolchain.KotlinNativePlatform
import org.jetbrains.kotlin.experimental.gradle.plugin.toolchain.KotlinNativeToolChain
import org.jetbrains.kotlin.konan.target.KonanTarget

/*
 *  We use the same configuration hierarchy as Gradle native:
 *
 *  componentImplementation (dependencies for the whole component: something like 'foo:bar:1.0') // move to sourceSet deps.
 *    ^
 *    |
 *  binaryImplementation (dependnecies of a particular target/flavor)
 *    ^             ^                ^
 *    |             |                |
 *  linkLibraries  runtimeLibraries  klibs (dependencies by type: klib, static lib, shared lib etc)
 *
 */
open class DefaultKotlinNativeBinary(
        name: String,
        private val baseName: Provider<String>,
        val sourceSet: KotlinNativeSourceSet,
        val identity: KotlinNativeVariantIdentity,
        objects: ObjectFactory,
        projectLayout: ProjectLayout,
        componentImplementation: Configuration,
        configurations: ConfigurationContainer,
        fileOperations: FileOperations
) : DefaultNativeBinary(name, objects, projectLayout, componentImplementation),
    KotlinNativeBinary,
    ComponentWithNames,
    ComponentWithDependencies,
    ComponentWithBaseName,
    PublishableComponent,
    ComponentWithOutputs
{
    override val konanTarget: KonanTarget
        get() = identity.konanTarget

    override val targetPlatform: KotlinNativePlatform
        get() = identity.targetPlatform

    open val debuggable: Boolean  get() = identity.isDebuggable
    open val optimized: Boolean   get() = identity.isOptimized

    override fun isDebuggable(): Boolean = debuggable
    override fun isOptimized(): Boolean = optimized

    override val sources: FileCollection
        get() = sourceSet.getAllSources(konanTarget)

    // TODO: Similar code is in plugins. Do we need it?
    override val klibraries = configurations.create(names.withPrefix("klibraries")).apply {
        isCanBeConsumed = false
        attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, KotlinNativeUsage.KLIB))
        attributes.attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, debuggable)
        attributes.attribute(CppBinary.OPTIMIZED_ATTRIBUTE, optimized)
        attributes.attribute(KotlinNativeBinary.KONAN_TARGET_ATTRIBUTE, konanTarget.name)
        // TODO: Support operating system attribute for Kotlin/Native binaries
        //attributes.attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, )
        extendsFrom(implementationDependencies)
    }

    override fun getBaseName(): Provider<String> = baseName

    override val compileTask: Property<KotlinNativeCompile> = objects.property(KotlinNativeCompile::class.java)

    override fun getCoordinates(): ModuleVersionIdentifier = identity.coordinates

    private val outputsProperty: ConfigurableFileCollection = fileOperations.files()
    override fun getOutputs(): ConfigurableFileCollection = outputsProperty

    override fun getTargetPlatform(): NativePlatform = identity.targetPlatform

    override fun getToolChain(): NativeToolChain = TODO() //toolChain

    override fun getObjects(): FileCollection = objectsDir.asFileTree.matching(PatternSet().include("**/*.klib"))
}