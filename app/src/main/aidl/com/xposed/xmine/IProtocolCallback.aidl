// IProtocolCallback.aidl
package com.xposed.xmine;
import com.xposed.xmine.protocol.ProtocolResponse;

// Declare any non-default types here with import statements

interface IProtocolCallback {
    void onResponse(in ProtocolResponse response);
}