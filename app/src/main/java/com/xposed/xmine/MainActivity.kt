package com.xposed.xmine

import android.app.ProgressDialog.show
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.topjohnwu.superuser.Shell
import com.xposed.xmine.databinding.ActivityMainBinding

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
        binding.button.setOnClickListener {
            ModuleInitializer.saveFile("test.txt", "我是设置的内容")
        }

        binding.button2.setOnClickListener {
            val content = ModuleInitializer.getFileContent("test.txt")
            binding.textView.text = "content = $content"
        }
    }
}
