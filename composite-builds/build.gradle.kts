// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        maven { url = uri("./local-repo") }
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.oschina.net/content/groups/public/") }
        maven { url = uri("https://maven.aliyun.com/repository/google/") }
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/public/") }
        gradlePluginPortal()

    }
    dependencies {
        val AGP_VERSION :String by project
        classpath("com.android.tools.build:gradle:${AGP_VERSION}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
        classpath("io.github.jamesfchen:module-publisher-plugin:1.4.3")
//        classpath "com.jamesfchen:lifecycle-plugin:1.0.0"
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
//plugins {
//    id("com.jamesfchen.perf-plugin") version "1.0.0" apply false
//}
allprojects {
    repositories {
        maven { url = uri("./local-repo") }
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.oschina.net/content/groups/public/") }
        maven { url = uri("https://maven.aliyun.com/repository/google/") }
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/public/") }
        gradlePluginPortal()

    }
//    tasks.withType(JavaCompile::class.java).configureEach { task ->
//        task.options.encoding = 'UTF-8'
//        task.sourceCompatibility = JavaVersion.VERSION_1_8
//        task.targetCompatibility = JavaVersion.VERSION_1_8
//    }
//    tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile::class.java).configureEach { task ->
//        task.kotlinOptions {
//            jvmTarget = '1.8'
//        }
//    }
    afterEvaluate {
    }
}

tasks.register("clean", Delete::class.java) {
    description = "Remove all the build files and intermediate build outputs"
//    delete(allprojects.map { it.buildDir })
    delete(rootProject.buildDir)
}