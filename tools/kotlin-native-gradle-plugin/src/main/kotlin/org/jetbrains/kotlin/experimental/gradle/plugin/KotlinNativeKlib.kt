package org.jetbrains.kotlin.experimental.gradle.plugin

import org.gradle.language.ComponentWithOutputs
import org.gradle.language.PublishableComponent
import org.gradle.language.nativeplatform.ComponentWithLinkUsage

/**
 *  A component representing a klibrary.
 */
interface KotlinNativeKlib: KotlinNativeBinary, ComponentWithLinkUsage, ComponentWithOutputs, PublishableComponent