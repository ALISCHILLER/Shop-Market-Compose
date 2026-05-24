pluginManagement {
    repositories {
        // Mirrors - useful when Google / MavenCentral access is unstable
        maven(url = uri("https://maven.aliyun.com/repository/google"))
        maven(url = uri("https://maven.aliyun.com/repository/gradle-plugin"))
        maven(url = uri("https://maven.aliyun.com/repository/public"))
        maven(url = uri("https://maven.aliyun.com/repository/central"))

        // Official repositories
        google()
        mavenCentral()
        gradlePluginPortal()

        // Optional fallback mirror
        maven(url = uri("https://mvnhub.ir/"))
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        // Mirrors - AndroidX, Google artifacts, MavenCentral artifacts
        maven(url = uri("https://maven.aliyun.com/repository/google"))
        maven(url = uri("https://maven.aliyun.com/repository/public"))
        maven(url = uri("https://maven.aliyun.com/repository/central"))
        maven(url = uri("https://maven.aliyun.com/repository/gradle-plugin"))

        // Official repositories
        google()
        mavenCentral()

        // Extra repositories
        maven(url = uri("https://jitpack.io"))

        // Use only if you really have SNAPSHOT dependencies
        maven(url = uri("https://oss.sonatype.org/content/repositories/snapshots/"))

        // Optional fallback mirror
        maven(url = uri("https://mvnhub.ir/"))
    }

    versionCatalogs {
        create("dependency") {
            from(files("gradle/dependency.versions.toml"))
        }
    }
}

rootProject.name = "EShop"
include(":app")