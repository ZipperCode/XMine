package com.xposed.xmine

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.topjohnwu.superuser.Shell
import com.xposed.xmine.databinding.ActivityMainBinding
import com.xposed.xmine.initializer.ModuleInitializer
import com.xposed.xmine.utils.Logger

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val shell = Shell.getShell()
        Logger.d(TAG, "shell isRoot = %s", shell.isRoot)
        Logger.d(TAG, "shell isAlive = %s", shell.isAlive)
        if (!shell.isRoot) {
            AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("提示")
                .setMessage("未获取root权限")
                .setNegativeButton("确定") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
            return
        }

        binding.bind.setOnClickListener {
//            val intent = Intent(this, AIDLService::class.java)
//            RootService.bind(
//                intent,
//                object : ServiceConnection {
//                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//                        val ipc = ITestService.Stub.asInterface(service)
//                        Logger.d(TAG, "onServiceConnected ipc = %s", ipc)
//                    }
//
//                    override fun onServiceDisconnected(name: ComponentName?) {
//                        Logger.d(TAG, "onServiceDisconnected")
//                    }
//                },
//            )
            SuManager.bindService(this) {
            }
        }
        binding.protocol.setOnClickListener {
        }

        binding.button.setOnClickListener {
            binding.textView.text = "pending"
            ModuleInitializer.saveFileAsync("test.txt", "我是设置的内容")
        }

        binding.button2.setOnClickListener {
            ModuleInitializer.getFileAsync("test.txt"){
                binding.textView.text = "content = $it"
            }
        }

        binding.button3.setOnClickListener {
            val intent = Intent(this, DemoActivity::class.java)
            startActivity(intent)
        }
    }

}
