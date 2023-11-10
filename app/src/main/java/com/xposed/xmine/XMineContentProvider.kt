package com.xposed.xmine

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectory
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

class XMineContentProvider : ContentProvider() {

    companion object {
        const val AUTH = "com.xposed.xmine.content.provider"

        const val GET_FILE_URI = "GET_FILE_URI"
        const val READ_FILE_METHOD = "READ_FILE_METHOD"

        const val FILE_URI = "FILE_URI"
        const val READ_FILE = "READ_FILE"
    }

    override fun call(authority: String, method: String, arg: String?, extras: Bundle?): Bundle? {
        Logger.dd("XMineContentProvider", "call auth = %s, math = %s, arg = %s", authority, method, arg)
        if (GET_FILE_URI == method && !arg.isNullOrBlank()) {
            val file = File(context!!.filesDir, arg)
            if (!file.exists()) {
                return null
            }
            val bundle = Bundle()
            bundle.putParcelable(READ_FILE, ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
            return bundle
        }

        return super.call(authority, method, arg, extras)
    }

    override fun onCreate(): Boolean {
        Logger.dd("XMineContentProvider", "onCreate")
        context?.let {
            val srcPath = Paths.get(it.packageCodePath, "lib")
            val dstPath = Paths.get(it.filesDir.absolutePath, "libs")
            Logger.dd("XMineContentProvider", "onCreate >> src = %s, dst = %s", srcPath, dstPath)
//            copyFolder(File(File(it.packageCodePath).parent, "lib"), File(it.filesDir, "lib"))
            copyFolder(srcPath, dstPath)
            File(it.filesDir, "test.txt").writeText("hello")
        }
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        val cursor = MatrixCursor(arrayOf())
        Logger.dd("XMineContentProvider", "query")
        val bundle = Bundle()
        val file = File(context!!.filesDir, "test.txt")
        bundle.putParcelable(READ_FILE, ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
        return cursor
    }

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        Logger.dd("XMineContentProvider", "openFile2")
        return super.openFile(uri, mode)
    }

    override fun openFile(uri: Uri, mode: String, signal: CancellationSignal?): ParcelFileDescriptor? {
        Logger.dd("XMineContentProvider", "openFile3")
        return super.openFile(uri, mode, signal)
    }

    private fun copyFolder(sourceFolder: Path, destinationFolder: Path) {
        if (destinationFolder.exists()) {
            return
        }
        destinationFolder.deleteIfExists()
        destinationFolder.createDirectory()

        val sourceFiles = Files.walk(sourceFolder)
        sourceFiles.forEach { file ->
            val destinationFile = destinationFolder.resolve(sourceFolder.relativize(file))
            Files.copy(file, destinationFile, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private fun copyFolder(src: File, dst: File) {
        if (src.isDirectory) {
            if (!dst.exists()) {
                dst.mkdirs()
            }
            val files = src.list() ?: return
            for (file in files) {
                val srcFile = File(src, file)
                val destFile = File(dst, file)
                // 递归复制
                copyFolder(srcFile, destFile)
            }
        } else {
            src.copyTo(dst)
        }
    }
}
