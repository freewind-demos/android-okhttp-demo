package com.example.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var client: OkHttpClient
    private lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化 OkHttpClient
        initOkHttpClient()

        textViewResult = findViewById(R.id.textViewResult)

        // GET 请求按钮
        findViewById<Button>(R.id.buttonGet).setOnClickListener {
            sendGetRequest()
        }

        // POST 请求按钮
        findViewById<Button>(R.id.buttonPost).setOnClickListener {
            sendPostRequest()
        }
    }

    private fun initOkHttpClient() {
        // 创建日志拦截器，用于打印请求日志
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 创建 OkHttpClient
        client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)  // 添加日志拦截器
            .connectTimeout(30, TimeUnit.SECONDS) // 连接超时
            .readTimeout(30, TimeUnit.SECONDS)    // 读取超时
            .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时
            .build()
    }

    // 发送 GET 请求
    private fun sendGetRequest() {
        textViewResult.text = "GET 请求中..."

        // 创建 GET 请求
        val request = Request.Builder()
            .url("https://jsonplaceholder.typicode.com/posts/1")
            .get()
            .build()

        // 异步执行请求
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 请求失败
                runOnUiThread {
                    textViewResult.text = "请求失败: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                // 请求成功
                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    runOnUiThread {
                        textViewResult.text = "GET 请求成功!\n\n$body"
                    }
                } else {
                    runOnUiThread {
                        textViewResult.text = "请求失败: ${response.code()}"
                    }
                }
            }
        })
    }

    // 发送 POST 请求
    private fun sendPostRequest() {
        textViewResult.text = "POST 请求中..."

        // 创建表单数据
        val formBody = FormBody.Builder()
            .add("title", "foo")
            .add("body", "bar")
            .add("userId", "1")
            .build()

        // 创建 POST 请求
        val request = Request.Builder()
            .url("https://jsonplaceholder.typicode.com/posts")
            .post(formBody)
            .build()

        // 异步执行请求
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    textViewResult.text = "请求失败: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    runOnUiThread {
                        textViewResult.text = "POST 请求成功!\n\n$body"
                    }
                } else {
                    runOnUiThread {
                        textViewResult.text = "请求失败: ${response.code()}"
                    }
                }
            }
        })
    }
}
