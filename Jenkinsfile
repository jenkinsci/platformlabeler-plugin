#!groovy

// test base Jenkins version and 2.121.2
buildPlugin(jenkinsVersions: [null, '2.121.2'],
            findbugs: [run:true, archive:true, unstableTotalAll: '0'],
            checkstyle: [run:true, archive:true, unstableTotalAll: '34'],
            failFast: false)
