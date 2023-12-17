package com.xposed.xmine.protocol

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProtocolResponse(
    val code: Int,
    val msg: String,
    val data: String?,
) : Parcelable {

    companion object {

        fun success(data: String? = null): ProtocolResponse = ProtocolResponse(0, "success", data)

        fun error(code: Int, msg: String, data: String? = null): ProtocolResponse = ProtocolResponse(code, msg, data)
    }
}
