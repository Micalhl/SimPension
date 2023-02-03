plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.56"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    description {
        contributors {
            name("Mical")
        }
        dependencies {
            name("MarriageMaster")
            name("Adyeshach")
            name("GemsEconomy")
        }
    }
    install("common")
    install("common-5")
    install("module-ai")
    install("module-chat")
    install("module-database")
    install("module-configuration")
    install("module-ui")
    install("module-nms")
    install("module-lang")
    install("platform-bukkit")
    install("expansion-command-helper")
    install("expansion-player-database")
    classifier = null
    version = "6.0.10-83"

    relocate("org.serverct.parrot.parrotx", "me.mical.simpension.taboolib.parrotx")
}

repositories {
    mavenCentral()
    maven {
        name = "pcgf-repo"
        url = uri("https://repo.pcgamingfreaks.at/repository/maven-everything")
    }
    maven {
        name = "codemc-releases"
        url = uri("https://repo.codemc.org/repository/maven-releases/")
    }
}

dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("com.zaxxer:HikariCP:4.0.3")
    compileOnly("ink.ptms.core:v11701:11701:mapped")
    compileOnly("ink.ptms.core:v11902:11902-minimize:universal")
    compileOnly("at.pcgamingfreaks:MarriageMaster-API-Bukkit:2.6.8")
    compileOnly("ink.ptms.adyeshach:all:2.0.0-snapshot-10")
    compileOnly("com.mojang:authlib:1.5.25")
    taboo("org.tabooproject.taboolib:module-parrotx:1.4.19")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}