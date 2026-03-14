# android-okhttp-demo

## 简介

本 demo 展示 Android 中 OkHttp 的基本用法，包括发送 GET 和 POST 请求。

## 基本原理

OkHttp 是 Square 公司开发的高效 HTTP 客户端，支持 HTTP/2 和 SPDY，用于 Android 和 Java 应用。

主要特点：
- 支持 HTTP/2 和 SPDY
- 连接池复用
- 自动处理 GZIP 压缩
- 响应缓存
- 拦截器机制

## 启动和使用

### 环境要求
- Android Studio 3.0+
- JDK 1.8+
- Android SDK 28

### 安装和运行
1. 用 Android Studio 打开此项目
2. 连接 Android 设备或启动模拟器
3. 点击 Run 运行项目
4. 点击按钮发送网络请求

## 教程

### 什么是 OkHttp？

OkHttp 是一个高效的 HTTP 客户端，它不仅可以在 Android 上使用，也可以在 Java 服务器端使用。它是 Retrofit 的底层实现，也可以单独使用。

### 创建 OkHttpClient

```kotlin
val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()
```

常用配置：
- connectTimeout：连接超时时间
- readTimeout：读取超时时间
- writeTimeout：写入超时时间
- addInterceptor：添加拦截器

### 发送 GET 请求

```kotlin
val request = Request.Builder()
    .url("https://example.com/api")
    .get()
    .build()

client.newCall(request).enqueue(object : Callback {
    override fun onFailure(call: Call, e: IOException) {
        // 请求失败
    }

    override fun onResponse(call: Call, response: Response) {
        // 请求成功
        val body = response.body()?.string()
    }
})
```

### 发送 POST 请求

**表单提交：**

```kotlin
val formBody = FormBody.Builder()
    .add("username", "test")
    .add("password", "123456")
    .build()

val request = Request.Builder()
    .url("https://example.com/login")
    .post(formBody)
    .build()
```

**JSON 请求体：**

```kotlin
val json = """{"username": "test", "password": "123456"}"""
val requestBody = RequestBody.create(
    MediaType.parse("application/json"),
    json
)

val request = Request.Builder()
    .url("https://example.com/api")
    .post(requestBody)
    .build()
```

### 拦截器

拦截器可以拦截请求和响应，用于：
- 添加通用请求头
- 日志打印
- 认证处理
- 错误重试

```kotlin
val interceptor = Interceptor { chain ->
    val request = chain.request().newBuilder()
        .addHeader("Authorization", "Bearer token")
        .build()
    chain.proceed(request)
}

client = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .build()
```

### 注意事项

1. **网络权限**：需要在 AndroidManifest.xml 中添加 `<uses-permission android:name="android.permission.INTERNET"/>`
2. **异步回调**：enqueue 的回调在子线程，需要使用 runOnUiThread 更新 UI
3. **响应体关闭**：使用完 Response 后要关闭响应体，或使用 use 扩展函数
4. **线程安全**：OkHttpClient 是线程安全的，应该复用单个实例
5. **协程支持**：OkHttp 4.x 开始支持 Kotlin 协程
