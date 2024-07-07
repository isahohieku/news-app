object Dependencies {
    val coreKtx by lazy { "androidx.core:core-ktx:${Versions.coreKtx}" }
    val lifecycleRuntimeKtx by lazy { "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleRuntimeKtx}" }
    val activityCompose by lazy { "androidx.activity:activity-compose:${Versions.activityCompose}" }
    val composeBom by lazy { "androidx.compose:compose-bom:${Versions.composeBom}" }
    val composeUi by lazy { "androidx.compose.ui:ui:${Versions.composeUi}"}
    val composeUiGraphics by lazy { "androidx.compose.ui:ui-graphics:${Versions.composeUiGraphics}"}
    val composeUiToolingPreview by lazy { "androidx.compose.ui:ui-graphics:${Versions.composeUiToolingPreview}"}
    val composeMaterial3 by lazy { "androidx.compose.material3:material3:${Versions.composeMaterial3}"}
    val jUnitTest by lazy { "junit:junit:4.13.2:${Versions.jUnitTest}"}
    val androidJunitTest by lazy { "androidx.test.ext:junit:${Versions.androidJunitTest}" }
    val androidTestEspresso by lazy { "androidx.test.espresso:espresso-core:${Versions.androidTestEspresso}" }
    val composeUiTooling by lazy { "androidx.compose.ui:ui-tooling:${Versions.composeUiTooling}"}
    val composeUiTestJUnit by lazy { "androidx.compose.ui:ui-test-junit4:${Versions.composeUiTestJUnit}"}
    val composeUiTestManifest by lazy { "androidx.compose.ui:ui-test-manifest:${Versions.composeUiTestManifest}"}

    val hiltAndroid by lazy { "com.google.dagger:hilt-android:${Versions.hilt}"}
    val hiltAndroidCompiler by lazy { "com.google.dagger:hilt-android-compiler:${Versions.hilt}"}
    val hiltCompiler by lazy { "androidx.hilt:hilt-compiler:${Versions.hiltCompiler}"}

    val hiltNavigationCompose by lazy { "androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigation}"}

    val retrofit by lazy { "com.squareup.retrofit2:retrofit:${Versions.retrofit}"}
    val okhttp by lazy { "com.squareup.okhttp3:okhttp:${Versions.okhttp}"}
    val gsonConverter by lazy { "com.squareup.retrofit2:converter-gson:${Versions.gsonConverter}"}
    val moshi by lazy { "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"}
    val moshiConverter by lazy { "com.squareup.retrofit2:converter-moshi:${Versions.moshiConverter}"}
    val loggingInterceptor by lazy { "com.squareup.okhttp3:logging-interceptor:${Versions.loggingInterceptor}"}

    val coroutinesCore by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"}
    val coroutinesAndroid by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"}

    val splashScreen by lazy { "androidx.core:core-splashscreen:${Versions.splashScreen}"}

    val coil by lazy { "io.coil-kt:coil-compose:${Versions.coil}"}
}

object Modules {
    const val utilities = ":utilities"
}