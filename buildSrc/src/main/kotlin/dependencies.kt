/**
 *
 * @author zhangzhipeng
 * @date 2023/10/16
 */

object Xposed {
    private const val version = "82"
    const val API = "de.robv.android.xposed:api:$version"
}

object DexKit {
    private const val version = "2.0.0-rc7"
    const val dexkit = "org.luckypray:dexkit:$version"
}

object AndroidX {

    private const val coreKtxVersion = "1.7.0"
    private const val appcompatVersion = "1.5.1"
    private const val constraintVersion = "2.1.4"

    const val core = "androidx.core:core-ktx:$coreKtxVersion"
    const val appcompat = "androidx.appcompat:appcompat:$appcompatVersion"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:$constraintVersion"
}

object Material {
    private const val materialVersion = "1.7.0"
    const val material = "com.google.android.material:material:$materialVersion"
}

object LibSu {
    private const val version = "5.2.2"
    const val core = "com.github.topjohnwu.libsu:core:$version"

    const val service = "com.github.topjohnwu.libsu:service:$version"
    const val io = "com.github.topjohnwu.libsu:io:$version"
    const val nio = "com.github.topjohnwu.libsu:nio:$version"
}
