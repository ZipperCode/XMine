package com.xposed.xmine.protocol

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProtocolRequest(
    val method: String,
    val params: String?,
) : Parcelable
