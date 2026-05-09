# ProGuard rules for Textbook Marketplace
-keep class com.example.textbookmarketplace.domain.model.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
