#!groovy

import java.util.Collections

if (env.JENKINS_URL.contains('markwaite.net')) {
    // Valid Jenkins versions for markwaite.net test
    def testJenkinsVersions = [ '2.479.1', '2.479.2', '2.479.3', '2.484', '2.485', '2.486', '2.487', '2.488', '2.489', '2.490', '2.491', '2.492']
    Collections.shuffle(testJenkinsVersions)

    // build with randomized Jenkins versions
    subsetConfiguration = [

                        // Intel Linux is labeled as 'linux' for legacy reasons
                        // Linux first for coverage report on ci.jenkins.io
                        [ jdk: 21, platform: 'linux',                                    ],
                        [ jdk: 17, platform: 'linux',    jenkins: testJenkinsVersions[0] ],

                        // Windows
                        [ jdk: 17,  platform: 'windows', jenkins: testJenkinsVersions[1] ],

                        // s390x label is also Linux
                        [ jdk: 17, platform: 's390x',    jenkins: testJenkinsVersions[2] ],
                        [ jdk: 21, platform: 's390x',    jenkins: testJenkinsVersions[3] ],
                      ]

    // Use advanced buildPlugin on markwaite.net
    buildPlugin(configurations: subsetConfiguration, failFast: false, forkCount: '1C')
    return
}

// Use simple buildPlugin elsewhere

/*
 See the documentation for more options:
 https://github.com/jenkins-infra/pipeline-library/
*/
buildPlugin(
  useContainerAgent: true, // Set to `false` if you need to use Docker for containerized tests
  configurations: [
    [platform: 'linux', jdk: 21],
    [platform: 'windows', jdk: 17],
])
