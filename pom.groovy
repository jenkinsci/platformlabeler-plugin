project {
  modelVersion '4.0.0'
  parent {
    groupId 'org.jenkins-ci.plugins'
    artifactId 'plugin'
    version '3.15'
  }
  groupId 'org.jvnet.hudson.plugins'
  artifactId 'platformlabeler'
  version '${revision}${changelist}'
  packaging 'hpi'
  name 'Jenkins platformlabeler plugin'
  description 'Assigns labels to nodes based on their operating system properties'
  url 'https://wiki.jenkins.io/display/JENKINS/PlatformLabeler+Plugin'
  developers {
    developer {
      id 'MarkEWaite'
      name 'Mark Waite'
      email 'mark.earl.waite@gmail.com'
    }
  }
  scm {
    connection 'scm:git:git://github.com/jenkinsci/platformlabeler-plugin.git'
    developerConnection 'scm:git:git@github.com:jenkinsci/platformlabeler-plugin.git'
    tag '${scmTag}'
    url 'https://github.com/jenkinsci/platformlabeler-plugin'
  }
  properties {
    revision '2.1'
    changelist '-SNAPSHOT'
    'java.level' '8'
    'jenkins.version' '2.60.3'
  }
  dependencies {
    dependency {
      groupId 'net.robertcollins'
      artifactId 'lsb'
      version '1.0'
    }
    dependency {
      groupId 'commons-io'
      artifactId 'commons-io'
      version '2.4'
    }
  }
  repositories {
    repository {
      id 'repo.jenkins-ci.org'
      url 'https://repo.jenkins-ci.org/public/'
    }
  }
  pluginRepositories {
    pluginRepository {
      id 'repo.jenkins-ci.org'
      url 'https://repo.jenkins-ci.org/public/'
    }
  }
  build {
    plugins {
      plugin {
        groupId 'com.coveo'
        artifactId 'fmt-maven-plugin'
        version '2.5.0'
        executions {
          execution {
            goals {
              goal 'format'
            }
          }
        }
      }
    }
  }
}
