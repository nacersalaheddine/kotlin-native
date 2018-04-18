package org.jetbrains.kotlin.experimental.gradle.plugin.tasks

import org.jetbrains.kotlin.gradle.plugin.tasks.KonanCompileTask
import org.jetbrains.kotlin.gradle.plugin.tasks.Produce

// TODO: Create a base link task
open class KotlinNativeLinkExecutable: KonanCompileTask() {

    init {
        srcFiles(project.files())
    }


    override val produce: Produce
        get() = Produce.PROGRAM

}
