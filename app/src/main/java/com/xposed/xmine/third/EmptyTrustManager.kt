package com.xposed.xmine.third

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class EmptyTrustManager : X509TrustManager {

    companion object {
        val EMPTY_TRUST_LIST = Array<X509TrustManager>(1) { EmptyTrustManager() }
    }
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
}
