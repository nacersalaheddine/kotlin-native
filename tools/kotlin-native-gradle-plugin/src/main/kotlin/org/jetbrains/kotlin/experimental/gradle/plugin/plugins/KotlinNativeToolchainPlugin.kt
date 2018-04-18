package org.jetbrains.kotlin.experimental.gradle.plugin.plugins

import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.operations.BuildOperationExecutor
import org.gradle.internal.os.OperatingSystem
import org.gradle.internal.reflect.Instantiator
import org.gradle.internal.service.ServiceRegistry
import org.gradle.internal.work.WorkerLeaseService
import org.gradle.model.Defaults
import org.gradle.model.RuleSource
import org.gradle.nativeplatform.internal.CompilerOutputFileNamingSchemeFactory
import org.gradle.nativeplatform.plugins.NativeComponentPlugin
import org.gradle.nativeplatform.toolchain.Clang
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainRegistryInternal
import org.gradle.nativeplatform.toolchain.internal.clang.ClangToolChain
import org.gradle.nativeplatform.toolchain.internal.gcc.metadata.SystemLibraryDiscovery
import org.gradle.nativeplatform.toolchain.internal.metadata.CompilerMetaDataProviderFactory
import org.gradle.process.internal.ExecActionFactory
import org.jetbrains.kotlin.experimental.gradle.plugin.toolchain.KotlinNativeToolChain

class KotlinNativeToolchainPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(NativeComponentPlugin::class.java)
    }

    class Rules : RuleSource() {
        @Defaults
        fun addToolChain(toolChainRegistry: NativeToolChainRegistryInternal, serviceRegistry: ServiceRegistry) {
            val fileResolver = serviceRegistry.get(FileResolver::class.java)
            val instantiator = serviceRegistry.get(Instantiator::class.java)
            val buildOperationExecutor = serviceRegistry.get(BuildOperationExecutor::class.java)

            toolChainRegistry.registerFactory(KotlinNativeToolChain::class.java) { name ->
                instantiator.newInstance(
                        KotlinNativeToolChain::class.java,
                        name,
                        buildOperationExecutor,
                        OperatingSystem.current(),
                        fileResolver
                )
            }
        }

    }
}
