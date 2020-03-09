#!groovy

Random random = new Random()             // Randomize which Jenkins version is selected for more testing
use_newer_jenkins = random.nextBoolean() // Use newer Jenkins on one build but slightly older on other

// build recommended configurations
subsetConfiguration = [ [ jdk: '8',  platform: 'windows', jenkins: null                                                     ],
                        [ jdk: '8',  platform: 'linux',   jenkins: !use_newer_jenkins ? '2.204.2' : '2.220', javaLevel: '8' ],
                        [ jdk: '11', platform: 'linux',   jenkins:  use_newer_jenkins ? '2.204.2' : '2.220', javaLevel: '8' ]
                      ]

node('docker && linux') {
  stage('Clean') {
    deleteDir()
    sh 'rm -rf /tmp/*'
    sh 'docker system prune -y'
  }
}

buildPlugin(forceAci: true, configurations: subsetConfiguration, failFast: false)
