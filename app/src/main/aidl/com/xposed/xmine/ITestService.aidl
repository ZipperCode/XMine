// ITestService.aidl
package com.xposed.xmine;

// Declare any non-default types here with import statements

interface ITestService {
    int getPid();
    int getUid();
    String getUUID();
    IBinder getFileSystemService();
}
