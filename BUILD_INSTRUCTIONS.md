# 🚀 تعليمات بناء تطبيق P2P Chat

## 📋 المتطلبات المسبقة

### 1. تثبيت Android Studio
- قم بتحميل وتثبيت [Android Studio](https://developer.android.com/studio)
- تأكد من تثبيت Android SDK (API 24 أو أحدث)
- تأكد من تثبيت Kotlin plugin

### 2. تثبيت Java Development Kit (JDK)
- قم بتثبيت JDK 11 أو أحدث
- تأكد من إعداد متغير البيئة `JAVA_HOME`

## 🔧 خطوات البناء

### الطريقة الأولى: استخدام Android Studio (الأسهل)

1. **فتح المشروع**
   ```
   - افتح Android Studio
   - اختر "Open an existing project"
   - اختر مجلد المشروع: chat_p2p
   ```

2. **مزامنة المشروع**
   ```
   - انتظر حتى يتم تحميل المشروع
   - اضغط على "Sync Now" إذا ظهرت الرسالة
   ```

3. **بناء APK**
   ```
   - اذهب إلى Build > Build Bundle(s) / APK(s) > Build APK(s)
   - انتظر حتى اكتمال البناء
   - ستجد APK في: app/build/outputs/apk/debug/
   ```

### الطريقة الثانية: استخدام Command Line

1. **تثبيت Gradle Wrapper**
   ```bash
   # في مجلد المشروع
   gradle wrapper
   ```

2. **بناء APK**
   ```bash
   # Windows
   .\gradlew.bat assembleDebug
   
   # Linux/Mac
   ./gradlew assembleDebug
   ```

## 🔑 إعداد API Keys (اختياري)

لتفعيل ميزات الذكاء الاصطناعي، قم بإضافة مفتاح Gemini API:

1. احصل على مفتاح API من [Google AI Studio](https://makersuite.google.com/app/apikey)
2. افتح ملف `app/src/main/java/com/example/p2pchat/core/ai/GeminiAIManager.kt`
3. استبدل `"demo_key_for_build"` بمفتاحك الفعلي

## 📱 تثبيت APK

1. **تفعيل Developer Options**
   ```
   - اذهب إلى Settings > About Phone
   - اضغط على "Build Number" 7 مرات
   ```

2. **تفعيل USB Debugging**
   ```
   - اذهب إلى Settings > Developer Options
   - فعل "USB Debugging"
   ```

3. **تثبيت APK**
   ```bash
   # باستخدام ADB
   adb install app/build/outputs/apk/debug/app-debug.apk
   
   # أو انسخ APK إلى الهاتف وثبته يدوياً
   ```

## 🛠️ حل المشاكل الشائعة

### مشكلة: Gradle Build Failed
```
الحل:
1. تأكد من اتصال الإنترنت
2. امسح cache: ./gradlew clean
3. أعد المحاولة: ./gradlew assembleDebug
```

### مشكلة: SDK Not Found
```
الحل:
1. افتح Android Studio
2. اذهب إلى File > Project Structure
3. تأكد من إعداد Android SDK بشكل صحيح
```

### مشكلة: OutOfMemoryError
```
الحل:
1. أضف إلى gradle.properties:
   org.gradle.jvmargs=-Xmx4g -XX:MaxPermSize=512m
2. أعد تشغيل Android Studio
```

## 📂 هيكل المشروع

```
chat_p2p/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/p2pchat/
│   │   │   ├── core/              # الوظائف الأساسية
│   │   │   ├── presentation/      # واجهة المستخدم
│   │   │   └── P2PChatApplication.kt
│   │   ├── res/                   # الموارد (layouts, colors, etc.)
│   │   └── AndroidManifest.xml
│   └── build.gradle               # إعدادات البناء
├── build.gradle                   # إعدادات المشروع
├── settings.gradle               # إعدادات Gradle
└── README.md                     # وثائق المشروع
```

## ✨ الميزات المتاحة في النسخة الحالية

- ✅ واجهة مستخدم متطورة مع Material Design 3
- ✅ نظام الرسوم المتحركة المتقدم
- ✅ نظام الثيمات الديناميكي
- ✅ هيكل P2P أساسي (مبسط للعرض)
- ✅ نظام الأمان الأساسي
- ✅ تكامل AI (مبسط للعرض)

## 🔮 الميزات المستقبلية

- 🔄 تفعيل WebRTC الكامل
- 🔄 تكامل Gemini AI الكامل
- 🔄 مشاركة الملفات
- 🔄 المكالمات الصوتية والمرئية
- 🔄 الدردشة الجماعية

## 📞 الدعم

إذا واجهت أي مشاكل:
1. تأكد من تحديث Android Studio إلى أحدث إصدار
2. تأكد من تثبيت جميع SDK المطلوبة
3. راجع ملف README.md للمزيد من التفاصيل

---
**تم إنشاؤه بـ ❤️ باستخدام Kotlin و Material Design 3**
