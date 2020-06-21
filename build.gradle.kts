import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
}

group = "com.kakao.pay"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    maven {
        setUrl("https://raw.github.com/bulldog2011/bulldog-repo/master/repo/releases/")
    }

}

dependencies {
    implementation("io.github.microutils:kotlin-logging:1.7.10")
    implementation("com.h2database:h2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("commons-codec:commons-codec:1.14")
    implementation("com.auth0:java-jwt:2.1.0")
    implementation("com.leansoft:bigqueue:0.7.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    testImplementation("io.rest-assured:json-path:4.2.0")
    testImplementation("io.rest-assured:rest-assured:4.2.0")
    testImplementation("junit:junit:4.12")
    testImplementation("io.rest-assured:kotlin-extensions:4.2.0")
    testImplementation("io.rest-assured:json-schema-validator:4.3.0")
    testImplementation("io.rest-assured:xml-path:4.2.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
