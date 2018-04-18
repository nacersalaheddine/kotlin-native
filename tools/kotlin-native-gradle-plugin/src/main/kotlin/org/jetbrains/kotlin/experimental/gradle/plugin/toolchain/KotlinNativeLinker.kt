package org.jetbrains.kotlin.experimental.gradle.plugin.toolchain

import org.gradle.api.tasks.WorkResult
import org.gradle.api.tasks.WorkResults
import org.gradle.language.base.internal.compile.Compiler
import org.jetbrains.kotlin.experimental.gradle.plugin.tasks.KotlinNativeCompileSpec
import org.jetbrains.kotlin.experimental.gradle.plugin.tasks.KotlinNativeLinkSpec
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.tasks.Produce

class KotlinNativeLinker: Compiler<KotlinNativeLinkSpec> {

    val produce: Produce = Produce.PROGRAM

    // TODO: Should we build args here or in the spec?
    fun KotlinNativeLinkSpec.konancArgs(): List<String> = mutableListOf<String>().apply {
        val spec = this@konancArgs
        addArg("-output", outputFile.absolutePath)

        addArg("-produce", produce.cliOption)

        addListArg("-linkerOpts", spec.linkerOpts)

        addArgIfNotNull("-target", spec.targetPlatform.target.visibleName)

        addKey("-g", spec.isDebuggable)
        addKey("-opt", spec.optimized)
        addKey("-ea", !spec.optimized)

        addListArg("-library", spec.objectFiles.map { it.absolutePath })

        // TODO: Do we need to link libraries here?
        // TODO: Support these parameters
        //addArgs("-repo", spec.repos.map { it.canonicalPath })
        //addArgs("-library", spec.klibs)
        //addKey("-nostdlib", noStdLib)
        //addKey("-nomain", noMain)
        //addKey("--time", measureTime)
        //addKey("-nodefaultlibs", noDefaultLibs)
        //addKey("-Xmulti-platform", enableMultiplatform)
        //addAll(extraOpts)

        //addArgIfNotNull("-entry", entryPoint)

        // TODO: These parameters should be providied by the base plugin.
        // addArgIfNotNull("-language-version", languageVersion)
        // addArgIfNotNull("-api-version", apiVersion)
    }


    override fun execute(spec: KotlinNativeLinkSpec): WorkResult {
        KonanCompilerRunner(spec.project).run(spec.konancArgs())
        return WorkResults.didWork(true) // TODO: Add check
    }

}
