package org.jetbrains.kotlin.experimental.gradle.plugin

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.language.PublishableComponent
import org.gradle.language.nativeplatform.ComponentWithExecutable
import org.gradle.language.nativeplatform.ComponentWithInstallation
import org.gradle.language.nativeplatform.ComponentWithRuntimeUsage

/**
 * Represents Kotlin/Native executable.
 */
interface KotlinNativeExecutable:
        KotlinNativeBinary,
        ComponentWithExecutable,
        ComponentWithInstallation,
        ComponentWithRuntimeUsage,
        PublishableComponent
{
    /**  Returns the executable file to use with a debugger for this executable. */
    val debuggerExecutableFile: Provider<RegularFile>
}