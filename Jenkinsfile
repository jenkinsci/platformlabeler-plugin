#!groovy

Random random = new Random()             // Randomize which Jenkins version is selected for more testing
use_newer_jenkins = random.nextBoolean() // Use newer Jenkins on one build but slightly older on other

// build recommended configurations
subsetConfiguration = [ [ jdk: '8',  platform: 'windows', jenkins: null                                                     ],

                        // Intel Linux is mislabeled as 'linux' for legacy reasons
                        [ jdk: '8',  platform: 'linux',   jenkins: !use_newer_jenkins ? '2.204.2' : '2.222.3', javaLevel: '8' ],
                        [ jdk: '11', platform: 'linux',   jenkins:  use_newer_jenkins ? '2.204.2' : '2.222.3', javaLevel: '8' ],

                        // ARM label is Linux also
                        [ jdk: '8' , platform: 'arm64',   jenkins: !use_newer_jenkins ? '2.204.2' : '2.234', javaLevel: '8' ],

                        // PowerPC 64 and s390x labels are also Linux
                        [ jdk: '8' , platform: 'ppc64le', jenkins: !use_newer_jenkins ? '2.222.3' : '2.234', javaLevel: '8' ],
                        [ jdk: '11', platform: 's390x',   jenkins:  use_newer_jenkins ? '2.222.3' : '2.234', javaLevel: '8' ]
                      ]

buildPlugin(forceAci: true, configurations: subsetConfiguration, failFast: false)
