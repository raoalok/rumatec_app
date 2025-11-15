pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Panav Biotech"
//include(":app")
include(":components")
//include(":rumatec_vetcare_biotech")
include(":rumatecvetcare")
include(":common_mediator")
include(":mediator_common")
