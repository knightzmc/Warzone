plugins {
    java
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
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    compileOnly("me.zombie_striker:QualityArmory:1.1.169")
}
