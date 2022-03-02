import org.ajoberstar.grgit.Grgit

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("maven-publish")
    id ("org.sonarqube") version "3.3"
}

val rootVersion = "1.0"
var buildNumber = ""

sonarqube {
  properties {
    property("sonar.projectKey", "knightzmc_Warzone")
    property("sonar.organization", "bristermitten")
    property("sonar.host.url", "https://sonarcloud.io")
  }
}

ext {
    val git: Grgit = Grgit.open {
        dir = File("$rootDir/.git")
    }
    val commit = git.head().abbreviatedId
    buildNumber = if (System.getenv("no-commits") != null) {
        rootVersion
    } else if (project.hasProperty("buildnumber")) {
        project.properties["buildnumber"] as String
    } else {
        commit.toString()
    }
}

version = "%s-%s".format(rootVersion, buildNumber)
group = "me.bristermitten"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = sourceCompatibility
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://repo.codemc.org/repository/maven-public/") // for qualityarmory
    maven("https://repo.viaversion.com") // for qualityarmory
    maven("https://repo.citizensnpcs.co") // also for qualityarmory
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("me.zombie_striker:QualityArmory:1.1.176")
    compileOnly("org.jetbrains:annotations:22.0.0")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("xyz.oribuin:eternaltags:1.0.10")

    compileOnly("org.apache.logging.log4j:log4j-core:2.14.1") // paper slf4j implementation


    compileOnly("me.clip:placeholderapi:2.10.10")

    implementation("com.google.inject:guice:5.0.1")
    implementation("com.google.inject.extensions:guice-assistedinject:5.0.1")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("io.vavr:vavr:0.10.4")
    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("com.github.Redempt:Crunch:master")

    implementation("net.kyori:adventure-api:4.9.2")
    implementation("net.kyori:adventure-platform-bukkit:4.0.0")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")

    implementation("co.aikar:acf-paper:0.5.0-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.yaml:snakeyaml:1.29")
    testImplementation("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}


tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
        options.isFork = true
    }
    test {
        useJUnitPlatform()
    }

    shadowJar {
        listOf(
            "com.google",
            "co.aikar.commands",
            "co.aikar.locales"
        ).forEach {
            relocate(it, "me.bristermitten.warzone.$it")
        }
        minimize()
    }
}

tasks.register<Copy>("copyJarToServerPlugins") {
    from(tasks.getByPath("shadowJar"))
    into(layout.projectDirectory.dir("server/plugins"))
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/knightzmc/Warzone")
            credentials {
                username = project.findProperty("gpr.user") as? String ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as? String ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register("jar", MavenPublication::class) {
            from(components["java"])
        }
    }
}
