package org.jetbrains.kotlin.experimental.gradle.plugin.tasks

import org.gradle.api.Project
import org.gradle.nativeplatform.internal.DefaultLinkerSpec
import org.gradle.nativeplatform.internal.LinkerSpec
import org.jetbrains.kotlin.experimental.gradle.plugin.toolchain.KotlinNativePlatform
import java.io.File

class KotlinNativeLinkSpec: DefaultLinkerSpec() {

    override fun getTargetPlatform(): KotlinNativePlatform {
        // TODO: is it ok?
        return super.getTargetPlatform() as KotlinNativePlatform
        val a: Array<Int>
    }

    val project: Project
        get() = TODO()

    val linkerOpts: List<String>
        get() = TODO("not implemented")

    var optimized = false
}