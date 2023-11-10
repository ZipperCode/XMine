package com.xposed.xmine

import android.content.Context

/**
 *
 * @author zhangzhipeng
 * @date 2023/11/6
 */
object DexKitHelper {

    fun init(context: Context) {
//        val modulePackage = context.packageManager.getPackageInfo(BuildConfig.APPLICATION_ID, 0)
//        val nativeDir = modulePackage.applicationInfo.nativeLibraryDir
//        Logger.d("DexKitHelper", "nativeDir = $nativeDir")
//        val uri = Uri.parse("content://com.xposed.xmine.content.provider")
//        context.grantUriPermission(BuildConfig.APPLICATION_ID, uri, FLAG_GRANT_READ_URI_PERMISSION)
//        Logger.d("DexKitHelper", "query")
//        val cursor = context.contentResolver.query(uri, null, null, null, null)
//        Logger.d("DexKitHelper", "cursor = $cursor")
//        val provider = context.contentResolver.acquireContentProviderClient(uri)
//        Logger.d("DexKitHelper", "provider = $provider")

        val list = context.packageManager.getInstalledPackages(0)
        Logger.dd("DexKitHelper", "list = $list")

    }
}
