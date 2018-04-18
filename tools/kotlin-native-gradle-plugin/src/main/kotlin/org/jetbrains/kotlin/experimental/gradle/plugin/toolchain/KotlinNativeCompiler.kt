package org.jetbrains.kotlin.experimental.gradle.plugin.toolchain

import org.gradle.api.Action
import org.gradle.api.Transformer
import org.gradle.api.tasks.WorkResult
import org.gradle.api.tasks.WorkResults
import org.gradle.internal.Transformers
import org.gradle.internal.operations.BuildOperationExecutor
import org.gradle.internal.operations.BuildOperationQueue
import org.gradle.internal.work.WorkerLeaseService
import org.gradle.language.base.internal.compile.Compiler
import org.gradle.nativeplatform.internal.CompilerOutputFileNamingSchemeFactory
import org.gradle.nativeplatform.toolchain.internal.*
import org.jetbrains.kotlin.experimental.gradle.plugin.tasks.KotlinNativeCompileSpec
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.tasks.Produce
import java.io.File

class KotlinNativeCompiler : Compiler<KotlinNativeCompileSpec>{

    val produce: Produce = Produce.LIBRARY

    // TODO: Should we build args here or in the spec?
    fun KotlinNativeCompileSpec.konancArgs(): List<String> = mutableListOf<String>().apply {
        val spec = this@konancArgs
        addArg("-output", outputFile.absolutePath)

        addArgs("-repo", spec.repos.map { it.canonicalPath })
        addArgs("-library", spec.klibs)

        addArg("-produce", produce.cliOption)

        addArgIfNotNull("-target", spec.targetPlatform.target.visibleName)

        addKey("-g", spec.isDebuggable)
        addKey("-opt", spec.isOptimized)
        addKey("-ea", !spec.isOptimized)

        spec.sourceFiles.filter { it.name.endsWith(".kt") }.mapTo(this) { it.absolutePath }

        // TODO: Support these parameters
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

    // TODO: Gralde uses some buildqueue. Can use do so?
    override fun execute(spec: KotlinNativeCompileSpec): WorkResult {
        KonanCompilerRunner(spec.project).run(spec.konancArgs())
        return WorkResults.didWork(true) // TODO: Actually check the result.
    }

}
