plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "me.bristermitten"
version = "1.0"

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
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("me.zombie_striker:QualityArmory:1.1.169")
    compileOnly("org.jetbrains:annotations:21.0.1")

    compileOnly("me.clip:placeholderapi:2.10.10")

    implementation("com.google.inject:guice:5.0.1")
    implementation("com.google.code.gson:gson:2.8.7")
    implementation("io.vavr:vavr:0.9.0")
    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("com.github.Redempt:Crunch:master")

    implementation("net.kyori:adventure-api:4.8.1")
    implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")

    implementation("co.aikar:acf-paper:0.5.0-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testImplementation("org.yaml:snakeyaml:1.28")
    testImplementation("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
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
    }
}

tasks.register<Copy>("copyJarToServerPlugins") {
    from(tasks.getByPath("shadowJar"))
    into(layout.projectDirectory.dir("server/plugins"))
}
