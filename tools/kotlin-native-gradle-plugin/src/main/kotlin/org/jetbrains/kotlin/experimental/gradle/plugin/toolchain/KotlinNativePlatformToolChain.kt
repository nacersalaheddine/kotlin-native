package org.jetbrains.kotlin.experimental.gradle.plugin.toolchain

import org.gradle.nativeplatform.platform.NativePlatform
import org.gradle.nativeplatform.toolchain.NativePlatformToolChain

interface KotlinNativePlatformToolChain: NativePlatformToolChain

class DefaultKotlinNativePlatformToolChain(val kotlinNativePlatform: DefaultKotlinNativePlatform) :
        KotlinNativePlatformToolChain
{
    override fun getPlatform(): NativePlatform  = kotlinNativePlatform
}
