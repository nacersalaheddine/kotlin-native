package org.jetbrains.kotlin.experimental.gradle.plugin.toolchain

import org.gradle.internal.file.PathToFileResolver
import org.gradle.internal.operations.BuildOperationExecutor
import org.gradle.internal.os.OperatingSystem
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal
import org.gradle.nativeplatform.toolchain.internal.ExtendableToolChain
import org.gradle.nativeplatform.toolchain.internal.NativeLanguage
import org.gradle.nativeplatform.toolchain.internal.PlatformToolProvider
import javax.inject.Inject


class KotlinNativeToolChain @Inject constructor(name: String,
                                                buildOperationExecutor: BuildOperationExecutor,
                                                operatingSystem: OperatingSystem,
                                                fileResolver: PathToFileResolver) :
        ExtendableToolChain<DefaultKotlinNativePlatformToolChain>(
                name,
                buildOperationExecutor,
                operatingSystem,
                fileResolver
        )
{

    override fun select(targetPlatform: NativePlatformInternal): PlatformToolProvider {
        TODO("not implemented")
    }

    override fun select(sourceLanguage: NativeLanguage, targetMachine: NativePlatformInternal): PlatformToolProvider {
        TODO("not implemented")
    }

    override fun getTypeName(): String {
        TODO("not implemented")
    }

}