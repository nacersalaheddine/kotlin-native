package org.jetbrains.kotlin.experimental.gradle.plugin

import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.language.BuildableComponent
import org.gradle.language.ComponentWithDependencies
import org.jetbrains.kotlin.konan.target.KonanTarget

// TODO: implement ComponentWithObjectFiles when we are built klibs as objects
interface KotlinNativeBinary: ComponentWithDependencies, BuildableComponent {

    /** Returns the source files of this binary. */
    val sources: FileCollection

    // TODO: Gradle uses NativePlatform (like CppPlatform) here. Do we need it?
    /**
     * Konan target for which the library is built for
     */
    val konanTarget: KonanTarget

    /** Compile task for this library */
    // TODO: Task -> KonanBuildingTask
    val compileTask: Provider<Task>

    // TODO: Rename?
    // TODO: Support native libraries here.
    // TODO: Support runtime libraries here.
    // Looks like we need at least 3 filecollections here: for klibs, for linktime native librareis and for runtime native libraries.
    /**
     * The link libraries (klibs only!) used to link this binary.
     * Includes the link libraries of the component's dependencies.
     */
    fun getLinkLibraries(): FileCollection
}