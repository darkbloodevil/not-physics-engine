plugins {
//    id 'org.jetbrains.kotlin.jvm' version '1.7.20'
id 'scala'
}

sourceCompatibility = 1.17
[compileJava, compileTestJava]*.options*.encoding = 'UTF-8'
[compileScala, compileTestScala]*.options*.encoding = 'UTF-8'


sourceSets {
main {
scala {
srcDirs = ['src/scala',"src/java"]
}
}
test{
scala {
srcDirs = ['test/scala',"test/java"]
}
}
}
jar {
duplicatesStrategy(DuplicatesStrategy.EXCLUDE)
}
//sourceSets.main.java.srcDirs = [ "src/java" ]

//tasks.named("compileScala"){
//
//    classpath = sourceSets.main.compileClasspath
//}

eclipse.project.name = appName + "-core"
repositories {
mavenCentral()
}
//dependencies {
//    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
//}
//compileKotlin {
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
//}
//compileTestKotlin {
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
//}
