#!groovy

// test base Jenkins version and 2.150.3
buildPlugin(jenkinsVersions: [null, '2.150.3'],
            jdkVersions: ['8'],
            findbugs: [run:true, archive:true, unstableTotalAll: '0'],
            checkstyle: [run:true, archive:true, unstableTotalAll: '16'],
            failFast: false)
