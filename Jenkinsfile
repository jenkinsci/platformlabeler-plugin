#!groovy

import java.util.Collections

// Valid Jenkins versions for markwaite.net test
def testJenkinsVersions = [ '2.361.1', '2.361.2', '2.371', '2.372' ]
Collections.shuffle(testJenkinsVersions)

// build with randomized Jenkins versions
subsetConfiguration = [ [ jdk: '11',  platform: 'windows', jenkins: testJenkinsVersions[0] ],

                        // Intel Linux is labeled as 'linux' for legacy reasons
                        [ jdk: '11', platform: 'linux',   ],
                        [ jdk: '17', platform: 'linux',   jenkins: '2.361.2' ],

                        // ARM label is Linux also
                        [ jdk: '11', platform: 'arm64',   jenkins: testJenkinsVersions[1] ],
                        [ jdk: '17', platform: 'arm64',   jenkins: '2.361.2' ],

                        // s390x label is also Linux
                        [ jdk: '11', platform: 's390x',   jenkins: testJenkinsVersions[2] ],
                        [ jdk: '17', platform: 's390x',   jenkins: testJenkinsVersions[3] ],
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
      // Opt-in to the Artifact Caching Proxy, to be removed when it will be opt-out.
      // See https://github.com/jenkins-infra/helpdesk/issues/2752 for more details and updates.
      artifactCachingProxyEnabled: true,
      // Test Java 11 with a recent LTS, Java 17 even more recent
      configurations: [
        [platform: 'linux',   jdk: '17', jenkins: '2.380'],
        [platform: 'linux',   jdk: '11', jenkins: '2.375.1'],
        [platform: 'windows', jdk: '11']
      ]
    )
}
