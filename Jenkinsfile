#!groovy

import java.util.Collections

// Valid Jenkins versions for markwaite.net test
def testJenkinsVersions = [ '2.319.3', '2.332.1', '2.335', '2.337', '2.338', '2.339', '2.340', '2.341' ]
Collections.shuffle(testJenkinsVersions)

// build recommended configurations
subsetConfiguration = [ [ jdk: '8',  platform: 'windows', jenkins: testJenkinsVersions[0], javaLevel: '8' ],

                        // Intel Linux is labeled as 'linux' for legacy reasons
                        [ jdk: '8',  platform: 'linux',   jenkins: testJenkinsVersions[1], javaLevel: '8' ],
                        [ jdk: '11', platform: 'linux',   jenkins: testJenkinsVersions[2], javaLevel: '8' ],

                        // ARM label is Linux also
                        [ jdk: '11', platform: 'arm64',   jenkins: testJenkinsVersions[3], javaLevel: '8' ],

                        // PowerPC 64 and s390x labels are also Linux
                        [ jdk: '8',  platform: 'ppc64le', jenkins: testJenkinsVersions[4], javaLevel: '8' ],
                        [ jdk: '11', platform: 's390x',   jenkins: testJenkinsVersions[5], javaLevel: '8' ],
                      ]

if (env.JENKINS_URL.contains('markwaite.net')) {
    // Use advanced buildPlugin on markwaite.net
    buildPlugin(configurations: subsetConfiguration, failFast: false)
} else {
    // Use simple buildPlugin elsewhere
    buildPlugin(failfast: false,
        configurations: [
            [platform: 'linux',   jdk: '11'],
            [platform: 'windows', jdk:  '8']
        ]
    )
}
