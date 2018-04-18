package org.jetbrains.kotlin.experimental.gradle.plugin.tasks

import org.gradle.api.Project
import org.gradle.language.nativeplatform.internal.AbstractNativeCompileSpec
import org.gradle.nativeplatform.platform.NativePlatform
import org.gradle.nativeplatform.toolchain.internal.NativeCompileSpec
import org.jetbrains.kotlin.experimental.gradle.plugin.toolchain.KotlinNativePlatform
import java.io.File

class KotlinNativeCompileSpec: AbstractNativeCompileSpec() {
    val repos: Iterable<File>
        get() = TODO("not implemented")
    val klibs: Iterable<String>
        get() = TODO("not implemented")
    val outputFile: File // TODO: Do we need it?
        get() = TODO("not implemented")

    override fun getTargetPlatform(): KotlinNativePlatform {
        // TODO: is it ok?
        return super.getTargetPlatform() as KotlinNativePlatform
    }

    val project: Project
        get() = TODO()
}
