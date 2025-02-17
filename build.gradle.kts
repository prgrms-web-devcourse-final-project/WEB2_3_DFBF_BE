plugins {
    java
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.dfbf"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
    }
}

dependencies {
    // Default
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Spring Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring OAuth2
    // implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
    implementation ("org.springframework.boot:spring-boot-starter-security")

    // Valid
    // implementation("org.springframework.boot:spring-boot-starter-validation")

    // MariaDB & Spring JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly ("org.mariadb.jdbc:mariadb-java-client")

    // JWT
//    implementation ("io.jsonwebtoken:jjwt-api:0.11.5")
//    implementation ("io.jsonwebtoken:jjwt-impl:0.11.5")
//    implementation ("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Swagger
    implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Test (Junit5)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Json (Jackson)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // logger
    implementation ("org.springframework.boot:spring-boot-starter-logging")

    // Feign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Mail
    implementation ("org.springframework.boot:spring-boot-starter-mail")

    // Kafka
//    implementation ("org.springframework.kafka:spring-kafka")
//    implementation ("org.apache.kafka:kafka-streams")
//    implementation ("org.apache.kafka:kafka-clients")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
