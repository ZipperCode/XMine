package com.xposed.xmine

import android.content.Intent
import android.os.IBinder
import com.topjohnwu.superuser.ipc.RootService
import com.xposed.xmine.protocol.ProtocolRequest
import com.xposed.xmine.protocol.ProtocolResponse
import org.json.JSONObject
import java.io.File

class CustomRootService : RootService() {
    companion object {
        private const val TAG = "CustomRootService"
    }

    override fun onCreate() {
        super.onCreate()
        Logger.d(TAG, "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d(TAG, "onDestroy")
    }

    override fun onBind(intent: Intent): IBinder {
        return RootInterfaceImpl()
    }

    inner class RootInterfaceImpl : IRootInterface.Stub() {
        override fun openProtocol(protocol: String?): String? {
            return null
        }

        override fun handleSync(request: ProtocolRequest?): ProtocolResponse {
            if (request == null) {
                return ProtocolResponse.error(-1, "request is null")
            }
            Logger.d(TAG, "handleSync request = %s", request)
            if (request.method == "save") {
                if (request.params.isNullOrEmpty()) {
                    return ProtocolResponse.error(-1, "参数不能为空")
                }
                val json = JSONObject(request.params)
                val filename = json.optString("filename", "")
                if (filename.isEmpty()) {
                    return ProtocolResponse.error(-1, "文件名获取失败 filename = $filename")
                }
                val file = File(filesDir, filename)
                if (file.parentFile != null && !file.parentFile!!.exists()) {
                    file.parentFile!!.mkdirs()
                }
                if (!file.exists()) {
                    file.createNewFile()
                }
                val content = json.optString("content", "empty")
                file.writeText(content)
                return ProtocolResponse.success()
            } else if (request.method == "load") {
                if (request.params.isNullOrEmpty()) {
                    return ProtocolResponse.error(-1, "参数不能为空")
                }
                val json = JSONObject(request.params)
                val filename = json.optString("filename", "")
                if (filename.isEmpty()) {
                    return ProtocolResponse.error(-1, "文件名获取失败 filename = $filename")
                }
                val file = File(filesDir, filename)
                if (!file.exists()) {
                    return ProtocolResponse.error(-1, "文件不存在")
                }
                return ProtocolResponse.success(file.readText())
            }
            return ProtocolResponse.error(-1, "未知错误")
        }

        override fun handleAsync(request: ProtocolRequest?, callback: IProtocolCallback?) {
        }
    }
}
