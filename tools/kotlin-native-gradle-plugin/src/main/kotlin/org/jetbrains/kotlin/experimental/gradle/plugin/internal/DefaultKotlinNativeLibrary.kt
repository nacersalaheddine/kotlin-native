package org.jetbrains.kotlin.experimental.gradle.plugin.internal

import org.gradle.api.internal.file.FileOperations
import org.gradle.api.internal.provider.LockableSetProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.internal.Describables
import org.gradle.internal.DisplayName
import org.gradle.nativeplatform.Linkage
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeKlib
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeLibrary

open class DefaultKotlinNativeLibrary(
        name: String,
        objects: ObjectFactory,
        fileOperations: FileOperations
) : DefaultKotlinNativeComponent(name, objects, fileOperations), KotlinNativeLibrary {

    override fun getDisplayName(): DisplayName = Describables.withTypeAndName("Kotlin/Native library", name)

    private val developmentBinary = objects.property(KotlinNativeKlib::class.java)
    override fun getDevelopmentBinary(): Provider<out KotlinNativeKlib> = developmentBinary

    override val linkage: SetProperty<Linkage> =
            LockableSetProperty<Linkage>(objects.setProperty(Linkage::class.java)).apply {
                add(Linkage.STATIC)
            }
}
