//pluginManagement {
//    repositories {
//        maven {
//            url =uri("./local-repo")
//        }
//        gradlePluginPortal()
//    }
//    plugins {
//        id("io.github.jamesfchen.lifecycle-plugin")  version "1.0.0"
//        id("io.github.jamesfchen.ibc-plugin")  version "1.0.0"
//    }
//}
include(
    ":base-plugin", ":base-plugin-ktx",
    ":lifecycle:lifecycle-api", ":lifecycle:lifecycle-plugin",
    ":ibc:ibc-api", ":ibc:ibc-compiler", ":ibc:ibc-plugin", ":ibc:ibc-annotations",":ibc:ibc-ksp"
)
include(":module-publisher-plugin")
include(":bundles-assembler-plugin")
include(":replugin:replugin-host-api")
include(":replugin:replugin-parasite-api")
