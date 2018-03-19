package org.jetbrains.kotlin.experimental.gradle.plugin.sourcesets

import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeBinary
import org.jetbrains.kotlin.experimental.gradle.plugin.KotlinNativeComponent
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

open class DefaultKotlinNativeSourceSet(
        override val name: String,
        val sourceDirectorySetFactory: SourceDirectorySetFactory
): KotlinNativeSourceSet {

    override val commonSources: SourceDirectorySet = newSourceDirectorySet("common")

    private val platformSources: MutableMap<KonanTarget, SourceDirectorySet> = mutableMapOf()

    private fun newSourceDirectorySet(name: String) = sourceDirectorySetFactory.create("common").apply {
        filter.include("**/*.kt")
    }

    fun addPlatformSourceDirectorySet(target: KonanTarget){
        platformSources[target] = newSourceDirectorySet(target.visibleName)
    }

    // TODO: May be use getOrPut here?
    override fun getSources(target: KonanTarget): SourceDirectorySet = platformSources.getValue(target)

    override fun getBinary(target: KonanTarget): KotlinNativeBinary = TODO()

    // TODO: Maybe use a Gradle's provider here?
    override lateinit var component: KotlinNativeComponent
    override lateinit var objectKlibs: Configuration

}