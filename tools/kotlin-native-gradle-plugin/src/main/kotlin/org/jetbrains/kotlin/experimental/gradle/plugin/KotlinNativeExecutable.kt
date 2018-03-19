package org.jetbrains.kotlin.experimental.gradle.plugin

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.language.ComponentWithOutputs
import org.gradle.api.component.PublishableComponent
import org.gradle.language.nativeplatform.ComponentWithRuntimeUsage

/*
 * TODO: We also need to extend ComponentWithExecutable and ComponentWithInstallation
 */

/**
 * Represents Kotlin/Native executable.
 */
interface KotlinNativeExecutable: KotlinNativeBinary, ComponentWithRuntimeUsage, ComponentWithOutputs {
    /**  Returns the executable file to use with a debugger for this executable. */
    val debuggerExecutableFile: Provider<RegularFile>

    /**  Returns the executable file to produce. */
    val executableFile: Provider<RegularFile>
}