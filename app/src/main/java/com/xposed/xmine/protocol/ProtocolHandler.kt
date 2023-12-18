package com.xposed.xmine.protocol

import com.xposed.xmine.utils.Logger

class ProtocolHandler {
    companion object {
        const val TAG = "ProtocolHandler"
        const val PROTOCOL_SCHEMA = "xmine://"
    }

    private val protocols: MutableList<String> = mutableListOf()

    init {
    }

    fun dispatcher(protocol: String?): String? {
        if (protocol.isNullOrEmpty() || !checkSchema(protocol)) {
            Logger.d(TAG, "dispatcher 失败，协议不合法")
            return null
        }
        val splits = protocol.split("?")
        if (splits.isEmpty()) {
            Logger.d(TAG, "dispatcher 失败，协议解析失败")
            return null
        }
        val head = splits[0]
        val params = splits.getOrNull(1)
        Logger.d(TAG, "dispatcher head = %s, params = %s", head, params)
        return protocol
    }

    fun checkSchema(protocol: String): Boolean {
        return protocol.startsWith(PROTOCOL_SCHEMA)
    }
}
