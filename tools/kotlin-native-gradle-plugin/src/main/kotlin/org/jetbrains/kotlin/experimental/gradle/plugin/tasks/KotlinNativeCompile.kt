package org.jetbrains.kotlin.experimental.gradle.plugin.tasks

import org.gradle.language.nativeplatform.tasks.AbstractNativeCompileTask
import org.gradle.nativeplatform.toolchain.internal.NativeCompileSpec

class KotlinNativeCompile: AbstractNativeCompileTask() {
    override fun createCompileSpec(): NativeCompileSpec = KotlinNativeCompileSpec()
}
