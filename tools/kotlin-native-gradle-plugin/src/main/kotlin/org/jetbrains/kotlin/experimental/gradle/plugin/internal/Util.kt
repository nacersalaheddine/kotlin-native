package org.jetbrains.kotlin.experimental.gradle.plugin.internal

import org.gradle.api.model.ObjectFactory
import org.gradle.nativeplatform.OperatingSystemFamily
import org.gradle.nativeplatform.platform.NativePlatform
import org.gradle.nativeplatform.platform.OperatingSystem
import org.gradle.nativeplatform.platform.internal.Architectures
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.util.visibleName


fun KonanTarget.createGradleOSFamily(objects: ObjectFactory): OperatingSystemFamily {
    val familyName = when(this.family) {
        Family.OSX -> OperatingSystemFamily.MACOS
        Family.LINUX -> OperatingSystemFamily.LINUX
        Family.MINGW -> OperatingSystemFamily.WINDOWS
        Family.IOS,
        Family.ANDROID,
        Family.WASM,
        Family.ZEPHYR -> this.family.name.toLowerCase()
    }
    return objects.named(OperatingSystemFamily::class.java, familyName)
}

val KonanTarget.asGradleNativePlatform: NativePlatform
    get() = object: NativePlatform {
        override fun operatingSystem(name: String?) = TODO("Cannot change operation system for KonanTarget")
        override fun architecture(name: String?) =  TODO("Cannot change architecture for KonanTarget")
        override fun getName(): String = this@asGradleNativePlatform.name
        override fun getArchitecture() = Architectures.forInput(this@asGradleNativePlatform.architecture.visibleName)
        override fun getDisplayName(): String = name
        override fun getOperatingSystem(): OperatingSystem = DefaultOperatingSystem(family.visibleName)
    }