#!groovy

import java.util.Collections

if (env.JENKINS_URL.contains('markwaite.net')) {
    // Valid Jenkins versions for markwaite.net test
    def testJenkinsVersions = [ '2.387.3', '2.401.3', '2.414.2', '2.416', '2.417', '2.418', '2.419', '2.420', '2.421', '2.423', '2.424' ]
    Collections.shuffle(testJenkinsVersions)

    // build with randomized Jenkins versions
    subsetConfiguration = [

                        // Intel Linux is labeled as 'linux' for legacy reasons
                        // Linux first for coverage report on ci.jenkins.io
                        [ jdk: 21, platform: 'linux',                                    ],
                        [ jdk: 17, platform: 'linux',    jenkins: testJenkinsVersions[0] ],
                        [ jdk: 11, platform: 'linux',    jenkins: testJenkinsVersions[1] ],

                        // Windows
                        [ jdk: 11,  platform: 'windows', jenkins: testJenkinsVersions[2] ],

                        // s390x label is also Linux
                        [ jdk: 11, platform: 's390x',    jenkins: testJenkinsVersions[3] ],
                        [ jdk: 17, platform: 's390x',    jenkins: testJenkinsVersions[4] ],
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
