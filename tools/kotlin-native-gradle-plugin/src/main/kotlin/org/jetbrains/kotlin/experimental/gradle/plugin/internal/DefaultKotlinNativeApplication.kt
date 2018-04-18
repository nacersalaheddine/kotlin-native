package org.jetbrains.kotlin.experimental.gradle.plugin.internal

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.artifacts.Configuration
import org.gradle.api.component.ComponentWithVariants
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.internal.Describables
import org.gradle.language.ComponentDependencies
import org.gradle.language.cpp.CppPlatform
import org.gradle.language.cpp.internal.DefaultCppExecutable
import org.gradle.language.cpp.internal.MainExecutableVariant
import org.gradle.language.cpp.internal.NativeVariantIdentity
import org.gradle.language.internal.DefaultComponentDependencies
import org.gradle.language.nativeplatform.internal.PublicationAwareComponent
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainInternal
import org.gradle.nativeplatform.toolchain.internal.PlatformToolProvider
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeApplication
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeExecutable
import org.jetbrains.kotlin.experimental.gradle.plugin.sourcesets.DefaultKotlinNativeSourceSet
import org.jetbrains.kotlin.experimental.gradle.plugin.toolchain.KotlinNativePlatform
import javax.inject.Inject

open class DefaultKotlinNativeApplication @Inject constructor(
        name: String,
        val objectFactory: ObjectFactory,
        fileOperations: FileOperations
): DefaultKotlinNativeComponent(name, objectFactory, fileOperations), KotlinNativeApplication, PublicationAwareComponent {

    override fun getDisplayName() = Describables.withTypeAndName("Kotlin/Native application", name)

    private val developmentBinaryProperty = objectFactory.property(KotlinNativeExecutable::class.java)
    override fun getDevelopmentBinary(): Property<KotlinNativeExecutable> = developmentBinaryProperty

    private val dependencies: DefaultComponentDependencies = objectFactory.newInstance(
            DefaultComponentDependencies::class.java,
            names.withSuffix("implementation"))

    override fun getDependencies(): ComponentDependencies = dependencies

    override fun getImplementationDependencies(): Configuration = dependencies.implementationDependencies

    private val mainVariant = MainExecutableVariant()

    override fun getMainPublication(): MainExecutableVariant = mainVariant

    fun addExecutable(identity: NativeVariantIdentity): DefaultKotlinNativeExecutable =
            objectFactory.newInstance(
                DefaultKotlinNativeExecutable::class.java,
                "$name${identity.name.capitalize()}",
                getImplementationDependencies(),
                getBaseName(),
                sources,
                identity
            ).apply {
                binaries.add(this)
            }
}

