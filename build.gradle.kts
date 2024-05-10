plugins {
    java
    war
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.github.gluhov"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.session:spring-session-core")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.google.code.findbugs:findbugs:3.0.1")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")

    implementation("io.awspring.cloud:spring-cloud-aws-starter:3.2.0-M1")
    implementation("io.awspring.cloud:spring-cloud-aws-s3:3.2.0-M1")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.701")

    implementation("io.asyncer:r2dbc-mysql:1.1.3")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.flywaydb:flyway-core:10.11.0")
    implementation("org.flywaydb:flyway-mysql:10.11.0")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")

    implementation("io.jsonwebtoken:jjwt:0.9.1")

    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")
    testImplementation("org.testcontainers:mysql:1.19.7")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
