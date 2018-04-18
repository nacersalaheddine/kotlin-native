package org.jetbrains.kotlin.experimental.gradle.plugin.toolchain

import org.gradle.internal.operations.BuildOperationExecutor
import org.gradle.language.base.internal.compile.*
import org.gradle.nativeplatform.internal.LinkerSpec
import org.gradle.nativeplatform.internal.StaticLibraryArchiverSpec
import org.gradle.nativeplatform.platform.internal.OperatingSystemInternal
import org.gradle.nativeplatform.toolchain.internal.AbstractPlatformToolProvider
import org.gradle.nativeplatform.toolchain.internal.EmptySystemLibraries
import org.gradle.nativeplatform.toolchain.internal.SystemLibraries
import org.gradle.nativeplatform.toolchain.internal.ToolType
import org.gradle.nativeplatform.toolchain.internal.compilespec.SwiftCompileSpec
import org.gradle.nativeplatform.toolchain.internal.gcc.ArStaticLibraryArchiver
import org.gradle.nativeplatform.toolchain.internal.tools.CommandLineToolConfigurationInternal
import org.gradle.platform.base.internal.toolchain.ComponentNotFound
import org.gradle.platform.base.internal.toolchain.ToolChainAvailability
import org.gradle.platform.base.internal.toolchain.ToolSearchResult
import org.gradle.util.TreeVisitor
import org.jetbrains.kotlin.experimental.gradle.plugin.tasks.KotlinNativeCompileSpec
import org.jetbrains.kotlin.experimental.gradle.plugin.tasks.KotlinNativeLinkSpec
import org.jetbrains.kotlin.konan.target.Family

class KotlinNativePlatformToolProvider(
        buildOperationExecutor: BuildOperationExecutor,
        targetOperatingSystem: OperatingSystemInternal
) : AbstractPlatformToolProvider(buildOperationExecutor, targetOperatingSystem) {

    private class DummySearchResult(val available: Boolean): ToolSearchResult {
        override fun explain(visitor: TreeVisitor<in String>?) {}
        override fun isAvailable(): Boolean = available
    }

    override fun isToolAvailable(toolType: ToolType): ToolSearchResult = when(toolType) {
        ToolType.LINKER,
        ToolType.STATIC_LIB_ARCHIVER -> DummySearchResult(true)
        else -> DummySearchResult(false)
    }

    private fun OperatingSystemInternal.toKonanOSFamily(): Family = when {
        isWindows -> Family.MINGW
        isLinux -> Family.LINUX
        isMacOsX -> Family.OSX
        else -> TODO("Support other OS families")
    }

    override fun getSystemLibraries(compilerType: ToolType) = EmptySystemLibraries()

    override fun <T : CompileSpec> newCompiler(spec: Class<T>): Compiler<T> = when {
        KotlinNativeCompileSpec::class.java.isAssignableFrom(spec) ->
            CompilerUtil.castCompiler(KotlinNativeCompiler())
        KotlinNativeLinkSpec::class.java.isAssignableFrom(spec) ->
            CompilerUtil.castCompiler(KotlinNativeLinker())
        else -> super.newCompiler(spec)
    }

    override fun createLinker(): Compiler<KotlinNativeLinkSpec> = newCompiler(KotlinNativeLinkSpec::class.java)

    override fun createStaticLibraryArchiver(): Compiler<StaticLibraryArchiverSpec> = TODO()

    override fun getExecutableName(executablePath: String?): String {
        val suffix = targetOperatingSystem.toKonanOSFamily().exeSuffix
        return "$executablePath.$suffix"
    }

}