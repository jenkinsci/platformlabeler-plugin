#!groovy

import java.util.Collections

// Valid Jenkins versions for markwaite.net test
def testJenkinsVersions = [ '2.375.4', '2.387.1', '2.387.2', '2.395', '2.396', '2.397', '2.398', '2.399', '2.400' ]
Collections.shuffle(testJenkinsVersions)

// build with randomized Jenkins versions
subsetConfiguration = [

                        // Intel Linux is labeled as 'linux' for legacy reasons
                        // Linux first for coverage report on ci.jenkins.io
                        [ jdk: '11', platform: 'linux',                                    ],
                        [ jdk: '17', platform: 'linux',    jenkins: testJenkinsVersions[0] ],

                        // Windows
                        [ jdk: '11',  platform: 'windows', jenkins: testJenkinsVersions[1] ],

                        // ARM label is Linux also
                        [ jdk: '11', platform: 'arm64',    jenkins: testJenkinsVersions[2] ],
                        [ jdk: '17', platform: 'arm64',    jenkins: testJenkinsVersions[3] ],

                        // s390x label is also Linux
                        [ jdk: '11', platform: 's390x',    jenkins: testJenkinsVersions[4] ],
                        [ jdk: '17', platform: 's390x',    jenkins: testJenkinsVersions[5] ],
                      ]

if (env.JENKINS_URL.contains('markwaite.net')) {
    // Use advanced buildPlugin on markwaite.net
    buildPlugin(configurations: subsetConfiguration, failFast: false)
} else {
    // Use simple buildPlugin elsewhere
    /* `buildPlugin` step provided by: https://github.com/jenkins-infra/pipeline-library */
    buildPlugin(
      // Container agents start faster and are easier to administer
      useContainerAgent: true,
      // Show failures on all configurations
      failFast: false,
      // Test Java 11 with a recent LTS, Java 17 even more recent
      configurations: [
        [platform: 'linux',   jdk: '11'], // Linux first for coverage report on ci.jenkins.io
        [platform: 'windows', jdk: '17', jenkins: '2.400'],
      ]
    )
}
