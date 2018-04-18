package org.jetbrains.kotlin.experimental.gradle.plugin.toolchain

import org.gradle.nativeplatform.platform.NativePlatform
import org.jetbrains.kotlin.konan.target.KonanTarget

interface KotlinNativePlatform: NativePlatform {
    val target: KonanTarget
}
