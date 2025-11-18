# Android Hello World Demo

一个最小的 Android 示例应用，点击按钮即可在界面上显示 "hello world" 文本。

## 功能
- 主界面包含提示文字和一个 Material Button。
- 点击按钮后，页面中央的文本会更新为 "hello world"。

## 运行方式
1. 安装 [Android Studio](https://developer.android.com/studio) 并配置 Android SDK（建议 API 34）。
2. 将此目录 `android-hello-world-demo` 作为现有项目导入 Android Studio。
3. 连接模拟器或真实设备后，点击 *Run* 按钮即可安装运行。

如需命令行构建，可在已配置好 ANDROID_HOME/ANDROID_SDK_ROOT 的环境中执行：

```bash
./gradlew assembleDebug
```

构建产物将在 `app/build/outputs/apk/debug/` 下生成。

## 测试
- 本地单元测试（Robolectric）：验证按钮点击后文本是否更新。

```bash
./gradlew testDebugUnitTest
```

- Gradle Managed Device 仪器化测试（首次执行会自动下载 Pixel 6 API 34 映像，需已安装 Android SDK）：

```bash
./gradlew pixel6Api34DebugAndroidTest
```

Managed Device 会复用 `app/src/androidTest` 下的 Espresso 用例，在云端/本地自动创建虚拟设备后执行。
