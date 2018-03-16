package org.jetbrains.kotlin.experimental.gradle.plugin.internal

import org.gradle.api.internal.component.UsageContext
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.language.cpp.internal.NativeVariantIdentity
import org.jetbrains.kotlin.konan.target.KonanTarget

class VariantIdentity(name: String,
                      baseName: Provider<String>,
                      group: Provider<String>,
                      version: Provider<String>,
                      val konanTarget: KonanTarget,
                      debuggable: Boolean,
                      optimized: Boolean,
                      linkUsage: UsageContext,
                      runtimeUsage: UsageContext,
                      objects: ObjectFactory
) : NativeVariantIdentity(
        name,
        baseName,
        group,
        version,
        debuggable,
        optimized,
        konanTarget.createGradleOSFamily(objects),
        linkUsage,
        runtimeUsage
) {


}