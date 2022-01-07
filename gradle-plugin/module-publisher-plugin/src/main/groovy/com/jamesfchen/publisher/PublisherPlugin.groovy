package com.jamesfchen.publisher

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.plugins.signing.SigningExtension

/**
 * [Android库发布到Maven Central全攻略](https://xiaozhuanlan.com/topic/6174835029)
 * [发布android库AAR至mavenCentral看这篇文章就可以了](https://zhuanlan.zhihu.com/p/22351830)
 * [nexus repo](https://s01.oss.sonatype.org/#stagingRepositories)
 * [sonatype办事处](https://issues.sonatype.org/secure/Dashboard.jspa)
 * [手动搭建maven](https://blog.csdn.net/intbird/article/details/105969242)
 *
 * [gradle module 查找](https://search.maven.org/)
 * [gradle plugin 查找](https://plugins.gradle.org/)
 *
 *
 *   发布本地project repo
 *    ./gradlew publishXxxPublicationsToMyProjectRepository
 *   发布本地maven local
 * ./gradlew publishToMavenLocal
 *   发布到远程maven central(nexus仓库)
 * ./gradlew publishXxxPublicationsToMavenCentralRepository
 *
 * ps：对于发布的类型来说只会有两种aar和jar
 */
class PublisherPlugin implements Plugin<Project> {

    def isApk(Project project) { return project.getPlugins().hasPlugin('com.android.application') }

    def isAar(Project project) { return project.getPlugins().hasPlugin('com.android.library') }

    def isJar(Project project) { return project.getPlugins().hasPlugin('java-library') }

    def isJava(Project project) { return project.getPlugins().hasPlugin('java') }

    def isKotlin(Project project) { return project.getPlugins().hasPlugin('kotlin') }
    Project myProject

    @Override
    void apply(Project project) {
        myProject = project
        project.plugins.apply('maven-publish')
        project.plugins.apply('signing')
        project.extensions.create('publish', PublishExtension)
        project.tasks.whenTaskAdded { task ->
            if (task.name.contains('ToMavenCentralRepository')) {
                def ext = project['publish'] as PublishExtension
                task.doFirst {
                    ext.checkMavenCentralRelease()
                }
            }
        }
        project.afterEvaluate {
            if (isApk(project)) return
            def ext = project['publish'] as PublishExtension
            project.group = ext.groupId
            project.version = ext.version
            loadUserInfo(project, ext)
            configMaven(project, ext)
        }
    }

    private void loadUserInfo(Project project, PublishExtension ext) {
        Properties properties = new Properties()
        File localPropertiesFile = project.rootProject.file("local.properties");
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.newDataInputStream())
            //发布release必须要求签名，否则发布会失败
            if (!ext.version.endsWith('SNAPSHOT')) {
                project.ext["signing.keyId"] = properties.getProperty("signingKeyId")
                project.ext["signing.password"] = properties.getProperty("signingPassword")
                project.ext["signing.secretKeyRingFile"] = properties.getProperty("signingSecretKeyRingFile")
                ext.signingKeyId = properties.getProperty("signingKeyId")
                ext.signingSecretKeyRingFile = properties.getProperty("signingPassword")
                ext.signingPassword = properties.getProperty("signingSecretKeyRingFile")
            }
        }
        if (project.properties.containsKey("useJamesfChenSnapshots")) {
            def useJamesfChenSnapshots = project["useJamesfChenSnapshots"]
            if (useJamesfChenSnapshots != null && useJamesfChenSnapshots == "true") {
                println(">>> 使用jamesfchen的maven central发布，但是只能发布snapshot")
                ext.ossrhUsername = 'delta'
                ext.ossrhPassword = 'vCKe*5vHBh3xH2.'
                ext.releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                ext.snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            }
        } else {
            ext.ossrhUsername = properties.getProperty("ossrhUsername")
            ext.ossrhPassword = properties.getProperty("ossrhPassword")
            ext.releasesRepoUrl = properties.getProperty("releasesRepoUrl")
            ext.snapshotsRepoUrl = properties.getProperty("snapshotsRepoUrl")
        }
    }

//配置maven local 和 maven central两个仓库
    def configMaven(Project project, PublishExtension ext) {
        def taskName
        def pkg
        if (isAar(project)) {
            taskName = 'Aar'
            pkg = "$project.buildDir/outputs/aar/${ext.artifactId}-release.aar"
        } else {
            taskName = 'Jar'
            pkg = "$project.buildDir/libs/${ext.artifactId}-${ext.version}.jar"
        }
        PublishingExtension publishing = myProject.extensions.getByType(PublishingExtension)
        myProject.components.configureEach {}
        myProject.components.each { component ->
            if (ext.buildVariant == null) {
                publishing.publications.create("${component.name}$taskName", MavenPublication, { MavenPublication publication ->
                    publication.from(component)
                    publication.groupId = ext.groupId
                    publication.artifactId = ext.artifactId
                    publication.version = ext.version
                    configJavadoc(project, publication, ext)
                    configPom(project, publication.pom, ext)
                })
            } else if (component.name.equalsIgnoreCase(ext.buildVariant)) {
                publishing.publications.create("${component.name}$taskName", MavenPublication, { MavenPublication publication ->
                    publication.from(component.name)
                    publication.groupId = ext.groupId
                    publication.artifactId = ext.artifactId
                    publication.version = ext.version
                    configJavadoc(project, publication, ext)
                    configPom(project, publication.pom, ext)
                })
                return
            }
        }

        //远程仓库
        publishing.repositories { artifactRepositories ->
//        artifactRepositories.maven {mavenArtifactRepository->
//            mavenArtifactRepository.name = 'myProject'
//            mavenArtifactRepository.url = "${project.rootDir}/local-repo"
//        }
            artifactRepositories.maven { mavenArtifactRepository ->
                mavenArtifactRepository.name = "mavenCentral"
                def releasesRepoUrl = ext.releasesRepoUrl
                def snapshotsRepoUrl = ext.snapshotsRepoUrl
                mavenArtifactRepository.url = ext.version.endsWith('SNAPSHOT') ? snapshotsRepoUrl : releasesRepoUrl
                mavenArtifactRepository.credentials {
                    username = ext.ossrhUsername
                    password = ext.ossrhPassword
                }
            }
        }

        if (ext.signingKeyId && ext.signingSecretKeyRingFile && ext.signingPassword) {
            SigningExtension signing = project.extensions.getByType(SigningExtension)
//    signing.useInMemoryPgpKeys(ext.signingKeyId, ext.signingSecretKeyRingFile,ext.signingPassword)
//            signing.sign(publishing.publications)
        }

    }

    def configPom(Project project, org.gradle.api.publish.maven.MavenPom pom, PublishExtension ext) {
        pom.name = ext.name
        pom.packaging = isAar(project) ? "aar" : "jar"
        pom.description = ext.name
        pom.url = ext.website
        pom.scm {
            url = ext.website
            connection = ext.website
            developerConnection = ext.website + ".git"
        }
        pom.licenses {
            license {
                name = 'The Apache Software License, Version 2.0'
                url = 'http://www.apache.org/licenses/LICENSE-2.0.txt'
            }
        }
        pom.developers {
            developer {
                id = ext.ossrhUsername
                name = ext.ossrhUsername
            }
        }
        // A slightly hacky fix so that your POM will include any transitive dependencies
        // that your library builds upon
//    pom.withXml {
//        def dependenciesNode = asNode().appendNode('dependencies')
//
////        project.configurations.implementation.allDependencies.findAll { dep -> dep.version != "unspecified" }.collect {dependency->
////            def dependencyNode = dependenciesNode.appendNode('dependency')
////            dependencyNode.appendNode('groupId', it.group)
////            dependencyNode.appendNode('artifactId', it.name)
////            dependencyNode.appendNode('version', it.version)
////        }
//        project.configurations.implementation.allDependencies.findAll { dep -> dep.version != "unspecified" }.collect {dependency->
//            def dependencyNode = dependenciesNode.appendNode('dependency')
//            dependencyNode.appendNode('groupId', it.group)
//            dependencyNode.appendNode('artifactId', it.name)
//            dependencyNode.appendNode('version', it.version)
//        }
//    }
    }

    private void configJavadoc(Project project, MavenPublication publication, PublishExtension ext) {
        //上传的android aar 文档有点问题,maven校验不通过
        if (isAar(project)) return
        if (isKotlin(project)) return
        if (isAar(project)) {
            // This generates sources.jar
            task sourcesJar(type: Jar) {
                classifier = 'sources'
                from android.sourceSets.main.java.source
            }

            task javadoc(type: Javadoc) {
                source = android.sourceSets.main.java.source
                classpath += configurations.implementation
                classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
            }

            task javadocJar(type: Jar, dependsOn: javadoc) {
                classifier = 'javadoc'
                from javadoc.destinationDir
            }
        } else {
            task sourcesJar(type: Jar, dependsOn: classes) {
                classifier = 'sources'
                from sourceSets.main.allSource
            }

            task javadocJar(type: Jar, dependsOn: javadoc) {
                classifier = 'javadoc'
                from javadoc.destinationDir
            }
        }

        if (project.hasProperty("kotlin")) {
            // Disable creating javadocs
            project.tasks.withType(Javadoc) {
                enabled = false
            }
        }

        javadoc {
            options {
                encoding "UTF-8"
                charSet 'UTF-8'
                author true
                version ext.version
                links "http://docs.oracle.com/javase/7/docs/api"
                title "${ext.name} ${ext.version}"
            }
        }
        publication.artifact(javadocJar)
        publication.artifact(sourcesJar)
//    publication.setArtifacts([sourcesJar])
//    artifacts {
//        archives javadocJar
//        archives sourcesJar
//    }
    }
}

class PublishExtension {
    String name
    String groupId
    String artifactId
    String version
    String website
    String buildVariant //只创建指定的变种发布task

    String ossrhUsername
    String ossrhPassword
    String releasesRepoUrl
    String snapshotsRepoUrl

    String signingKeyId
    String signingSecretKeyRingFile
    String signingPassword


    void checkMavenLocal() {
        check()
    }

    void checkMavenCentralRelease() {
        check()
        checkMavenCentralSnapshots()
        if (isMavenCentralSigningEmpty()) {
            throw new NullPointerException("U should set signingKeyId/signingPassword/signingSecretKeyRingFile in local.properties")
        }

    }

    void checkMavenCentralSnapshots() {
        check()
        if (isMavenCentralAccountEmpty()) {
            throw new NullPointerException("U should set ossrhUsername/ossrhPassword in local.properties")
        }
    }

    void check() {
        checkField(name, "name")
        checkField(groupId, "groupId")
        checkField(artifactId, "artifactId")
        checkField(version, "version")
        checkField(website, "website")
    }

    boolean isMavenCentralSigningEmpty() {
        return isEmpty(signingKeyId) || isEmpty(signingSecretKeyRingFile) || isEmpty(signingPassword)
    }

    boolean isMavenCentralAccountEmpty() {
        return isEmpty(ossrhUsername) || isEmpty(ossrhPassword)
    }

    static void checkField(String field, String fieldName) {
        if (isEmpty(field)) {
            throw new NullPointerException("$fieldName is empty!!")
        }
    }

    static boolean isEmpty(String str) {
        return str == null || str.length() == 0
    }
}