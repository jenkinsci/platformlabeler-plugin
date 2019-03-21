Contributing to the Platform Labeler Plugin
==============================

Plugin source code is hosted on [GitHub](https://github.com/jenkinsci/platformlabeler-plugin).
New feature proposals and bug fix proposals should be submitted as
[GitHub pull requests](https://help.github.com/articles/creating-a-pull-request).
Your pull request will be evaluated by the [Jenkins job](https://ci.jenkins.io/job/Plugins/job/platformlabeler-plugin/).

Before submitting your change, please assure that you've added tests
which verify your change.

Code coverage reporting is available as a maven target.
Please try to improve code coverage with tests when you submit.
* `mvn -P enable-jacoco clean install jacoco:report` to report code coverage

Please don't introduce new spotbugs output.
* `mvn spotbugs:check` to analyze project using [Spotbugs](https://spotbugs.github.io)
* `mvn spotbugs:gui` to review report using GUI

Code formatting in the Platform Labeler plugin is maintained by fmt.
Before submitting a pull request, confirm the formatting is correct with:

* `mvn compile`

If the formatting is not correct, the build will fail.  Correct the formatting with:

* `mvn fmt:format`
