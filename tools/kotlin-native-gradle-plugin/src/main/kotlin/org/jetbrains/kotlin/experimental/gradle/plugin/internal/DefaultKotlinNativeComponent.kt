package org.jetbrains.kotlin.experimental.gradle.plugin.internal

import org.gradle.api.component.ComponentWithVariants
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.internal.provider.LockableSetProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.language.BinaryCollection
import org.gradle.language.ComponentDependencies
import org.gradle.language.cpp.internal.MainExecutableVariant
import org.gradle.language.internal.DefaultBinaryCollection
import org.gradle.language.internal.DefaultComponentDependencies
import org.gradle.language.nativeplatform.internal.ComponentWithNames
import org.gradle.language.nativeplatform.internal.DefaultNativeComponent
import org.gradle.language.nativeplatform.internal.Names
import org.gradle.language.nativeplatform.internal.PublicationAwareComponent
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeBinary
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeComponent
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import javax.inject.Inject

abstract class DefaultKotlinNativeComponent @Inject constructor(
        private val name: String,
        objects: ObjectFactory,
        fileOperations: FileOperations
) : DefaultNativeComponent(fileOperations), KotlinNativeComponent, ComponentWithNames, PublicationAwareComponent {

    override val baseName: Property<String> = objects.property(String::class.java)
    override fun getBaseName(): Provider<String> = baseName

    // TODO: this should be replaced with source set.
    override val sources: FileCollection = createSourceView("src/$name/cpp", listOf("kt"))

    override val konanTargets: SetProperty<KonanTarget> =
            LockableSetProperty(objects.setProperty(KonanTarget::class.java)).apply {
                set(mutableSetOf(HostManager.host))
            }

    @Suppress("UNCHECKED_CAST")
    private val binaries = objects.newInstance(DefaultBinaryCollection::class.java, KotlinNativeBinary)
            as DefaultBinaryCollection<out KotlinNativeBinary>
    override fun getBinaries(): BinaryCollection<out KotlinNativeBinary> = binaries

    override fun getName(): String = name

    private val names = Names.of(name)
    override fun getNames(): Names = names

    private val dependencies: DefaultComponentDependencies = objects.newInstance(
            DefaultComponentDependencies::class.java,
            names.withSuffix("implementation"))

    override fun getDependencies(): ComponentDependencies = dependencies

    private val mainVariant = MainExecutableVariant()
    override fun getMainPublication(): ComponentWithVariants = mainVariant
}
