package org.jetbrains.kotlin.experimental.gradle.plugin.internal

import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.attributes.Usage
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.language.ComponentDependencies
import org.gradle.language.ComponentWithDependencies
import org.gradle.language.ComponentWithOutputs
import org.gradle.language.PublishableComponent
import org.gradle.language.cpp.CppBinary
import org.gradle.language.internal.DefaultComponentDependencies
import org.gradle.language.nativeplatform.internal.ComponentWithNames
import org.gradle.language.nativeplatform.internal.Names
import org.jetbrains.kotlin.experimental.gradle.plugin.ComponentWithBaseName
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeBinary
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeBinary.Companion.KOTLIN_NATIVE_TARGET_ATTRIBUTE
import org.jetbrains.kotlin.konan.target.KonanTarget

// TODO: Implement ComponentWithObjectFiles when we are able to compile from klibs only.
// TODO: Do we need something like objectDir?
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
        private val name_: String,
        private val baseName_: Provider<String>,
        override val sources: FileCollection,
        val identity: VariantIdentity,
        objects: ObjectFactory,
        componentImplementation: Configuration,
        configurations: ConfigurationContainer,
        fileOperations: FileOperations
) : KotlinNativeBinary,
    ComponentWithNames,
    ComponentWithDependencies,
    ComponentWithBaseName,
    PublishableComponent,
    ComponentWithOutputs
{

    override val konanTarget: KonanTarget
        get() = identity.konanTarget

    val debuggable: Boolean
        get() = identity.isDebuggable

    val optimized: Boolean
        get() = identity.isOptimized

    private val names_ = Names.of(name)
    val dependencies = objects
            .newInstance(DefaultComponentDependencies::class.java, "${name}Implementation")
            .apply {
                implementationDependencies.extendsFrom(componentImplementation) // TODO: may be rename all these configurations
            }

    override val klibraries = configurations.create(names.withPrefix("klibraries")).apply {
        isCanBeConsumed = false
        attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, KotlinNativeUsage.KLIB))
        attributes.attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, debuggable)
        attributes.attribute(CppBinary.OPTIMIZED_ATTRIBUTE, optimized)
        attributes.attribute(KOTLIN_NATIVE_TARGET_ATTRIBUTE, konanTarget.name)
        // TODO: Support operating system attribute for Kotlin/Native binaries
        //attributes.attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, )
        extendsFrom(this@DefaultKotlinNativeBinary.dependencies.implementationDependencies)
    }

    override fun getName(): String = name_
    override fun getNames(): Names = names_
    override fun getBaseName(): Provider<String> = baseName_

    override fun getDependencies(): ComponentDependencies = dependencies

    override val compileTask: Provider<Task> = objects.property<Task>(Task::class.java)

    override fun getCoordinates(): ModuleVersionIdentifier = identity.coordinates

    private val outputsProperty: ConfigurableFileCollection = fileOperations.files()
    override fun getOutputs(): ConfigurableFileCollection = outputsProperty
}