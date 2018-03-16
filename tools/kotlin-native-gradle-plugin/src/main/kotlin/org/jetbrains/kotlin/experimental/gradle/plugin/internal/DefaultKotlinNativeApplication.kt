package org.jetbrains.kotlin.experimental.gradle.plugin.internal

import org.gradle.api.internal.file.FileOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.internal.Describables
import org.gradle.language.nativeplatform.internal.PublicationAwareComponent
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeApplication
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeExecutable

open class DefaultKotlinNativeApplication(
        name: String,
        objects: ObjectFactory,
        fileOperations: FileOperations
): DefaultKotlinNativeComponent(name, objects, fileOperations), KotlinNativeApplication, PublicationAwareComponent {

    override fun getDisplayName() = Describables.withTypeAndName("Kotlin/Native application", name)

    private val developmentBinary = objects.property(KotlinNativeExecutable::class.java)
    override fun getDevelopmentBinary(): Provider<out KotlinNativeExecutable> = developmentBinary
}

