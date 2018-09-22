#!groovy

// test base Jenkins version and 2.121.2
buildPlugin(jenkinsVersions: [null, '2.121.2'],
            jdkVersions: ['8'],
            findbugs: [run:true, archive:true, unstableTotalAll: '0'],
            checkstyle: [run:true, archive:true, unstableTotalAll: '22'],
            failFast: false)
