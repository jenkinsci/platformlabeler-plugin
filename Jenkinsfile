#!groovy

import java.util.Collections

// Valid Jenkins versions for markwaite.net test
def testJenkinsVersions = [ '2.332.4', '2.346.3', '2.355', '2.356', '2.357', '2.358', '2.359', '2.360', '2.361', '2.362', '2.363', ]
Collections.shuffle(testJenkinsVersions)

// build with randomized Jenkins versions
subsetConfiguration = [ [ jdk: '11',  platform: 'windows', jenkins: testJenkinsVersions[0] ],

                        // Intel Linux is labeled as 'linux' for legacy reasons
                        [ jdk: '8',  platform: 'linux',   jenkins: testJenkinsVersions[1] ],
                        [ jdk: '11', platform: 'linux',   jenkins: testJenkinsVersions[2] ],
                        [ jdk: '17', platform: 'linux',   jenkins: '2.361' ],

                        // ARM label is Linux also
                        [ jdk: '11', platform: 'arm64',   jenkins: testJenkinsVersions[3] ],
                        [ jdk: '17', platform: 'arm64',   jenkins: '2.361' ],

                        // PowerPC 64 and s390x labels are also Linux
                        [ jdk: '11', platform: 'ppc64le', jenkins: testJenkinsVersions[4] ],
                        [ jdk: '17', platform: 'ppc64le', jenkins: testJenkinsVersions[5] ],
                        [ jdk: '11', platform: 's390x',   jenkins: testJenkinsVersions[6] ],
                        [ jdk: '17', platform: 's390x',   jenkins: testJenkinsVersions[7] ],
                      ]

if (env.JENKINS_URL.contains('markwaite.net')) {
    // Use advanced buildPlugin on markwaite.net
    buildPlugin(configurations: subsetConfiguration, failFast: false)
} else {
    // Use simple buildPlugin elsewhere
    buildPlugin(failfast: false,
        configurations: [
            [platform: 'linux',   jdk: '17', jenkins: '2.361'],
            [platform: 'linux',   jdk: '11'],
            [platform: 'windows', jdk:  '8']
        ]
    )
}
