package org.jetbrains.kotlin.experimental.gradle.plugin.sourcesets

import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.SourceDirectorySet
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeBinary
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeComponent
import org.jetbrains.kotlin.konan.target.KonanTarget

interface KotlinNativeSourceSet {
    /*
        Нам нужно:
            sourceDir set-ы на каждый таргет по одному
            возможность получить sourceDirSet (ы) по таргету
            возможность получить binary по таргету.
            возможность получить KotlinNativeComponent по таргету
    */

    val name: String

    val commonSources: SourceDirectorySet
    val component: KotlinNativeComponent

    val objectKlibs: Configuration // TODO: may be provide some way to use different configs for different targets?

    fun getSources(target: KonanTarget): SourceDirectorySet
    fun getBinary(target: KonanTarget): KotlinNativeBinary


    // TODO: Также тут нужны:
    // Таска для сборки клибы
    // конфигурация с клибами для каждого таргета
}
