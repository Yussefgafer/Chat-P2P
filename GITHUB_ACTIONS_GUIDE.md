# 🚀 دليل GitHub Actions - بناء APK تلقائياً

## 📋 الخطوات التفصيلية

### الخطوة 1: إنشاء حساب GitHub

1. **اذهب إلى GitHub**
   - افتح [github.com](https://github.com) في المتصفح
   - اضغط على "Sign up" في الزاوية العلوية اليمنى

2. **أنشئ الحساب**
   - أدخل عنوان بريد إلكتروني
   - أنشئ كلمة مرور قوية
   - اختر اسم مستخدم فريد
   - تحقق من البريد الإلكتروني

3. **اختر الخطة المجانية**
   - اختر "Free" plan
   - يتيح لك مستودعات عامة غير محدودة
   - 2000 دقيقة GitHub Actions شهرياً مجاناً

### الخطوة 2: إنشاء مستودع جديد

1. **إنشاء Repository**
   - اضغط على "+" في الزاوية العلوية اليمنى
   - اختر "New repository"

2. **إعدادات المستودع**
   ```
   Repository name: p2p-chat-android
   Description: Advanced P2P Chat Android Application
   Visibility: Public (للحصول على GitHub Actions مجاناً)
   ✅ Add a README file
   ✅ Add .gitignore (Android template)
   License: MIT License (اختياري)
   ```

3. **إنشاء المستودع**
   - اضغط "Create repository"

### الخطوة 3: رفع ملفات المشروع

#### الطريقة الأولى: عبر واجهة الويب (الأسهل)

1. **فك ضغط المشروع**
   - فك ضغط `P2P_Chat_Android_Project.zip`
   - ستحصل على مجلد يحتوي على جميع ملفات المشروع

2. **رفع الملفات**
   - في صفحة المستودع، اضغط "uploading an existing file"
   - اسحب وأفلت جميع الملفات والمجلدات
   - أو اضغط "choose your files" واختر الملفات

3. **ملفات مهمة يجب رفعها:**
   ```
   ✅ app/ (مجلد التطبيق كاملاً)
   ✅ build.gradle (ملف البناء الرئيسي)
   ✅ settings.gradle
   ✅ gradle.properties
   ✅ gradlew (ملف Gradle Wrapper)
   ✅ gradlew.bat
   ✅ gradle/ (مجلد Gradle Wrapper)
   ✅ .github/workflows/build-apk.yml (ملف GitHub Actions)
   ```

4. **Commit الملفات**
   ```
   Commit message: "Initial commit - P2P Chat Android App"
   Description: "Advanced P2P chat application with Material Design 3, AI integration, and security features"
   ```
   - اضغط "Commit changes"

#### الطريقة الثانية: عبر Git Command Line

```bash
# استنساخ المستودع
git clone https://github.com/YOUR_USERNAME/p2p-chat-android.git
cd p2p-chat-android

# نسخ ملفات المشروع
# (انسخ جميع ملفات P2P_Chat_Android_Project.zip هنا)

# إضافة الملفات
git add .
git commit -m "Initial commit - P2P Chat Android App"
git push origin main
```

### الخطوة 4: تفعيل GitHub Actions

1. **الذهاب إلى Actions**
   - في صفحة المستودع، اضغط على تبويب "Actions"

2. **تفعيل Workflows**
   - إذا ظهرت رسالة "Workflows aren't being run"
   - اضغط "I understand my workflows, go ahead and enable them"

3. **فحص Workflow**
   - ستجد workflow باسم "Build P2P Chat APK"
   - إذا لم يظهر، تأكد من وجود ملف `.github/workflows/build-apk.yml`

4. **تشغيل يدوي (اختياري)**
   - اضغط على "Build P2P Chat APK"
   - اضغط "Run workflow"
   - اضغط "Run workflow" مرة أخرى

### الخطوة 5: مراقبة البناء

1. **مراقبة التقدم**
   - في تبويب "Actions"، ستجد البناء الجاري
   - اضغط على اسم البناء لرؤية التفاصيل

2. **مراحل البناء:**
   ```
   ⏳ Checkout code
   ⏳ Set up JDK 11
   ⏳ Setup Android SDK
   ⏳ Cache Gradle packages
   ⏳ Grant execute permission for gradlew
   ⏳ Clean project
   ⏳ Build debug APK
   ⏳ Build release APK
   ⏳ Upload APKs
   ```

3. **وقت البناء المتوقع:**
   - البناء الأول: 8-12 دقيقة
   - البناءات التالية: 3-5 دقائق (بسبب التخزين المؤقت)

### الخطوة 6: تحميل APK

1. **بعد اكتمال البناء بنجاح:**
   - ستجد علامة ✅ خضراء
   - اضغط على اسم البناء

2. **تحميل من Artifacts:**
   - في أسفل الصفحة، قسم "Artifacts"
   - ستجد:
     - `P2PChat-Debug-APK` (للاختبار)
     - `P2PChat-Release-APK` (للإنتاج)

3. **تحميل APK:**
   - اضغط على اسم الـ Artifact
   - سيتم تحميل ملف ZIP
   - فك الضغط للحصول على APK

### الخطوة 7: تثبيت APK

1. **على الهاتف:**
   - انقل APK إلى الهاتف
   - فعل "Unknown Sources" في الإعدادات
   - اضغط على APK للتثبيت

2. **باستخدام ADB:**
   ```bash
   adb install app-debug.apk
   ```

## 🔧 استكشاف الأخطاء

### مشكلة: Build failed

#### الحلول الشائعة:

1. **فحص ملف build.gradle:**
   ```gradle
   # تأكد من وجود:
   compileSdk 34
   minSdk 24
   targetSdk 34
   ```

2. **فحص التبعيات:**
   ```gradle
   # تأكد من صحة إصدارات المكتبات
   implementation 'androidx.core:core-ktx:1.12.0'
   implementation 'com.google.android.material:material:1.11.0'
   ```

3. **فحص Gradle Wrapper:**
   ```properties
   # في gradle/wrapper/gradle-wrapper.properties
   distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
   ```

### مشكلة: Workflow لا يعمل

#### الحلول:

1. **فحص مسار الملف:**
   ```
   ✅ .github/workflows/build-apk.yml
   ❌ github/workflows/build-apk.yml
   ```

2. **فحص صيغة YAML:**
   - تأكد من المسافات البادئة
   - استخدم محرر YAML للتحقق

3. **فحص الصلاحيات:**
   - تأكد من أن المستودع عام
   - أو فعل GitHub Actions في الإعدادات

### مشكلة: APK لا يعمل

#### الحلول:

1. **استخدم Debug APK للاختبار**
2. **تأكد من إصدار Android المدعوم (7.0+)**
3. **فحص لوج الأخطاء:**
   ```bash
   adb logcat | grep P2PChat
   ```

## 📊 معلومات إضافية

### حجم APK المتوقع:
- **Debug APK:** 15-25 MB
- **Release APK:** 10-18 MB (مضغوط)

### الميزات المتوفرة:
- ✅ واجهة Material Design 3
- ✅ نظام الرسوم المتحركة
- ✅ الثيمات الديناميكية
- ✅ هيكل P2P أساسي
- ✅ تكامل AI مبسط
- ✅ نظام الأمان الأساسي

### التحديثات التلقائية:
- كل push إلى main/master يبني APK جديد
- إصدارات تلقائية مع رقم الإصدار
- تخزين APKs لمدة 30 يوم

## 🎉 النجاح!

بعد اتباع هذه الخطوات، ستحصل على:
- ✅ APK جاهز للتثبيت
- ✅ بناء تلقائي عند كل تحديث
- ✅ إصدارات منظمة
- ✅ تطبيق متطور بجميع الميزات

**🚀 مبروك! تطبيق P2P Chat جاهز للاستخدام!**
