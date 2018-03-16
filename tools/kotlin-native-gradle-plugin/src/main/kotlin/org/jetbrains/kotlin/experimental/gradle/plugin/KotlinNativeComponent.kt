package org.jetbrains.kotlin.experimental.gradle.plugin

import org.gradle.api.Action
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.language.BinaryCollection
import org.gradle.language.ComponentWithBinaries
import org.gradle.language.ComponentWithDependencies
import org.jetbrains.kotlin.konan.target.KonanTarget

/**
 *  Class representing a Kotlin/Native component: application or library (both klib and dynamic)
 *  built for different targets.
 */
interface KotlinNativeComponent: ComponentWithBinaries, ComponentWithDependencies {

    /**
     * Specifies the base name for this component. This name is used to calculate various output file names.
     * The default value is calculated from the project name.
     */
    // TODO: Do we need some kind of wrapper for Property (to get rid of baseName.get ... )
    val baseName: Property<String>

    /**
     * Defines the source files or directories of this component. You can add files or directories to this collection.
     * When a directory is added, all source files are included for compilation. When this collection is empty,
     * the directory src/main/kotlin is used by default.
     */
    val sources: ConfigurableFileCollection

    /** Configures the source files or directories for this component. */
    fun source(action: Action<in ConfigurableFileCollection>)

    /** Specifies the kotlin native target used to build this component. */
    val konanTarget: KonanTarget

    /** Returns the binaries for this library. */
    override fun getBinaries(): BinaryCollection<out KotlinNativeBinary>


    /** Returns the implementation dependencies of this component. */
    //val implementationDependencies: Configuration // TODO: May be change name?

}