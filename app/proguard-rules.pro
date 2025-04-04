

-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}

-keepclassmembers class * {
        public void *(android.view.View);
    }

   # No eliminar clases de Android
   -dontwarn android.support.**
   -dontwarn androidx.**

   # Firebase - Evitar que ProGuard elimine clases usadas por Firebase
   -keepattributes *Annotation*
   -keep class com.google.firebase.** { *; }
   -keep class com.google.android.gms.** { *; }

   # Evitar warnings de Firebase
   -dontwarn com.google.firebase.**
   -dontwarn com.google.android.gms.**

   # MPAndroidChart - Evitar eliminación de clases y métodos
   -keep class com.github.mikephil.charting.** { *; }
   -dontwarn com.github.mikephil.charting.**