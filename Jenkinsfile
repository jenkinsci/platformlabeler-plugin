#!groovy

// Use recommended configuration,
// run all tests to completion (don't immediately fail entire job if tests fail), and
// run on Azure Container Instances (ACI) for the Linux portion of the build

buildPlugin(configurations: buildPlugin.recommendedConfigurations(),
            failFast: false,
            useAci: true)
