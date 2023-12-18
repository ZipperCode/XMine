// IRootInterface.aidl
package com.xposed.xmine;
import com.xposed.xmine.protocol.ProtocolRequest;
import com.xposed.xmine.protocol.ProtocolResponse;
import com.xposed.xmine.IProtocolCallback;

interface IRootInterface {
    String openProtocol(String protocol);

    ProtocolResponse handleSync(in ProtocolRequest request);

    void handleAsync(in ProtocolRequest request, IProtocolCallback callback);

    IBinder getFileSystemService();
}