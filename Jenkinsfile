#!groovy

// test base Jenkins version
buildPlugin(jenkinsVersions: [null],
            jdkVersions: ['8', '11'],
            findbugs: [run:true, archive:true, unstableTotalAll: '0'],
            checkstyle: [run:true, archive:true, unstableTotalAll: '34'],
            failFast: false)
