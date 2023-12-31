buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
        google()
    }

    dependencies {


    }
}
plugins {
    id 'scala'
}

allprojects {
    apply plugin: "eclipse"
    apply plugin: 'idea'
//    plugins {
//        id 'scala'
//    }


    version = '1.0'
    ext {
        appName = "my-gdx-game"
        gdxVersion = '1.10.0'
        roboVMVersion = '2.3.12'
        box2DLightsVersion = '1.5'
//        ashleyVersion = '1.7.3'
        aiVersion = '1.8.2'
        gdxControllersVersion = '2.1.0'
    }

    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
        maven { url "https://oss.sonatype.org/content/repositories/releases/" }

    }
    dependencies {
    }
}

project(":desktop") {
    apply plugin: "java-library"


    dependencies {
        implementation project(":core")
        api "com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion"
        api "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop"
        api "com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop"


//    implementation 'commons-collections:commons-collections:3.2.2'

    }
}


project(":core") {
    apply plugin: "java-library"

    dependencies {
//        implementation project(":core")
        api "com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion"
        api "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop"
        api "com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop"

        api "com.badlogicgames.gdx:gdx:$gdxVersion"
        implementation "com.badlogicgames.gdx:gdx-box2d:$gdxVersion"
        implementation "com.badlogicgames.box2dlights:box2dlights:$box2DLightsVersion"
//        implementation "com.badlogicgames.ashley:ashley:$ashleyVersion"

        implementation group: 'net.onedaybeard.artemis', name: 'artemis-odb', version: '2.3.0'


        // https://mvnrepository.com/artifact/org.json/json
        api group: 'org.json', name: 'json', version: '20230227'
        //        implementation group: 'com.google.code.gson', name: 'gson', version: '2.9.1'
        // https://mvnrepository.com/artifact/org.apache.poi/poi

        implementation group: 'org.apache.poi', name: 'poi', version: '5.2.3'
        // https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml
        implementation group: 'org.apache.poi', name: 'poi-ooxml', version: '5.2.3'
        // https://mvnrepository.com/artifact/org.dhatim/fastexcel
        implementation group: 'org.dhatim', name: 'fastexcel', version: '0.15.7'
// https://mvnrepository.com/artifact/org.dhatim/fastexcel-reader
        implementation group: 'org.dhatim', name: 'fastexcel-reader', version: '0.15.7'
// https://mvnrepository.com/artifact/org.dom4j/dom4j
        implementation group: 'org.dom4j', name: 'dom4j', version: '2.1.4'
// https://mvnrepository.com/artifact/org.slf4j/slf4j-api
        implementation group: 'org.slf4j', name: 'slf4j-api', version: '2.0.9'
        // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
        implementation group: 'ch.qos.logback', name: 'logback-classic', version: '1.4.11'
// https://mvnrepository.com/artifact/ch.qos.logback/logback-core
        implementation group: 'ch.qos.logback', name: 'logback-core', version: '1.4.11'


// https://mvnrepository.com/artifact/org.scala-lang/scala3-library
        api group: 'org.scala-lang', name: 'scala3-library_3', version: '3.0.0'
        api 'commons-collections:commons-collections:3.2.2'
// https://mvnrepository.com/artifact/com.typesafe.scala-logging/scala-logging
        api group: 'com.typesafe.scala-logging', name: 'scala-logging_3', version: '3.9.5'

        testImplementation 'org.scalatest:scalatest_3:3.2.9'
//        testImplementation 'junit:junit:4.13'
        testImplementation 'org.junit.jupiter:junit-jupiter:5.7.1'
        testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

    }
}
//tasks.named('test', Test) {
//    useJUnitPlatform()
//
//    maxHeapSize = '1G'
//
//    testLogging {
//        events "passed"
//    }
//}