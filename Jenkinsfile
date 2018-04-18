#!groovy

// test base Jenkins version and 2.60.3
buildPlugin(jenkinsVersions: [null, '2.60.3'],
            platforms: ['linux'],
            findbugs: [run:true, archive:true, unstableTotalAll: '0'],
            failFast: false)
