plugins {
    alias(libs.plugins.android.application)
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.filemanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.filemanager"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    //Splash Apimate
    implementation("androidx.core:core-splashscreen:1.0.1")

    //Compose Navigation
    var nav_version = "2.6.0"
    implementation("androidx.navigation:navigation-compose:$nav_version")


    //Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")


    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //Coil
    implementation("io.coil-kt:coil-compose:2.4.0")

    //Datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //Compose Foundation
    implementation("androidx.compose.foundation:foundation:1.4.3")

    //Accompanist
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.4-beta")

    //Paging 3
    var paging_version = "3.1.1"
    implementation("androidx.paging:paging-runtime:$paging_version")
    implementation("androidx.paging:paging-compose:3.2.0-rc01")

    //Room
    var room_version = "2.5.2"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:2.5.2")


    implementation("com.guolindev.permissionx:permissionx:1.8.0")

// https://mvnrepository.com/artifact/e-iceblue/spire.doc
//    implementation("e-iceblue:spire.doc:12.8.4")
//    implementation("e-iceblue:spire.doc:12.7.17")
//    implementation(libsorg.apache.poi.xwpf.converter.pdf)
//    implementation(libs.fr.opensagres.poi.xwpf.converter.pdf)
//    implementation(libs.fr.opensagres.poi.xwpf.converter.pdf.v210)
    implementation("fr.opensagres.xdocreport:fr.opensagres.poi.xwpf.converter.pdf:2.0.1")


//    implementation ("org.apache.poi:poi:5.2.3")
//    implementation ("org.apache.poi:poi-scratchpad:5.2.3") // Needed for HWPFDocument
//    implementation ("com.itextpdf:itext7-core:7.2.5") // or OpenPDF as an alternative

//    //Lifecycle
//    var lifecycle_version = "2.2.0-rc03"
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
//    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
//    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")


}