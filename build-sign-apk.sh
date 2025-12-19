#!/bin/bash
set -euxo pipefail

# 1️⃣ تنظيف وبناء نسخة Release APK (غير موقّعة)
cd PROCHAN
./gradlew :app:clean :app:assembleRelease

# 2️⃣ تحديد المسارات داخل مشروعك
UNSIGNED_APK="app/build/outputs/apk/release/app-release-unsigned.apk"
SIGNED_APK="app/build/outputs/apk/release/app-release-signed.apk"
KEYSTORE="my-release-key.jks"
ALIAS="my-key-alias"

# 3️⃣ إنشاء keystore إذا لم يكن موجود (سيطلب منك إدخال كلمة مرور وبيانات)
if [ ! -f "$KEYSTORE" ]; then
  keytool -genkey -v \
    -keystore "$KEYSTORE" \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -alias "$ALIAS"
fi

# 4️⃣ توقيع الـ APK (سيطلب منك كلمة مرور keystore عند التنفيذ)
apksigner sign \
  --ks "$KEYSTORE" \
  --ks-key-alias "$ALIAS" \
  --out "$SIGNED_APK" \
  "$UNSIGNED_APK"

# 5️⃣ التحقق من التوقيع
apksigner verify "$SIGNED_APK"

echo "✅ APK جاهز للتثبيت: $SIGNED_APK"
