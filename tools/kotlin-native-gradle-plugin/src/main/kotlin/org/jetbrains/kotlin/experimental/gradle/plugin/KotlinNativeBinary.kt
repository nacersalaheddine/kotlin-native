package org.jetbrains.kotlin.experimental.gradle.plugin

import org.gradle.api.Task
import org.gradle.api.attributes.Attribute
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.language.ComponentWithDependencies
import org.jetbrains.kotlin.gradle.plugin.tasks.KonanBuildingTask
import org.jetbrains.kotlin.konan.target.KonanTarget

// TODO: implement ComponentWithObjectFiles when we are built klibs as objects
interface KotlinNativeBinary: ComponentWithDependencies {

    /** Returns the source files of this binary. */
    val sources: FileCollection

    // TODO: Gradle uses NativePlatform (like CppPlatform) here. Do we need it?
    /**
     * Konan target for which the library is built for
     */
    val konanTarget: KonanTarget

    /** Compile task for this library */
    // TODO: Task -> KonanBuildingTask
    val compileTask: Provider<KonanBuildingTask>

    // TODO: Support native link libraries here.
    // TODO: Support runtime libraries here.
    // Looks like we need at least 3 file collections here: for klibs, for linktime native libraries and for runtime native libraries.
    /**
     * The link libraries (klibs only!) used to link this binary.
     * Includes the link libraries of the component's dependencies.
     */
    val klibraries: FileCollection

    // TODO: Change the fq name of the attribute when it's moved into another package.
    // TODO: Replace String with some special class
    companion object {
        val KOTLIN_NATIVE_TARGET_ATTRIBUTE =
                Attribute.of("org.jetbrains.kotlin.experimental.gradle.plugin", String::class.java)
    }

}