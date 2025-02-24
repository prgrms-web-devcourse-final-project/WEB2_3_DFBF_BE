plugins {
    id("java")
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
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Default
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Spring Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring OAuth2
    // implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Valid
    // implementation("org.springframework.boot:spring-boot-starter-validation")

    // MariaDB & Spring JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Test (Junit5)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core:4.0.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.0.0")
    testImplementation("org.mockito:mockito-inline:4.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")

    // Json (Jackson)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // logger
    implementation("org.springframework.boot:spring-boot-starter-logging")

    // validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Feign -> NETFLIX API 통신 라이브러리
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Mail
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Kafka
//    implementation ("org.springframework.kafka:spring-kafka")
//    implementation ("org.apache.kafka:kafka-streams")
//    implementation ("org.apache.kafka:kafka-clients")

    //QueryDSL 추가
    implementation ("com.querydsl:querydsl-apt:5.0.0")
    implementation ("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    implementation ("com.querydsl:querydsl-core:5.0.0")
    annotationProcessor ("com.querydsl:querydsl-apt:5.0.0:jakarta")
    annotationProcessor ("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor ("jakarta.persistence:jakarta.persistence-api")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Querydsl 빌드 옵션 설정
val generatedDir = "src/main/generated"

// querydsl QClass 파일 생성 위치를 지정
tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(file(generatedDir))
}

// java source set에 querydsl QClass 위치 추가
sourceSets {
    getByName("main") {
        java {
            srcDir(generatedDir)
        }
    }
}

// gradle clean 시에 QClass 디렉토리 삭제
tasks.named<Delete>("clean") {
    delete(file(generatedDir))
}