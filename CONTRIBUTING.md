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

Please don't introduce new findbugs output.
* `mvn findbugs:check` to analyze project using [Findbugs](http://findbugs.sourceforge.net/)
* `mvn findbugs:gui` to review Findbugs report using GUI

Code formatting in the Platform Labeler plugin is maintained by the fmt files.  Try to
maintain reasonable consistency with the existing files where
feasible.  Please don't perform wholesale reformatting of a file
without discussing with the current maintainers.
New code should follow the [SCM API code style guidelines](https://github.com/jenkinsci/scm-api-plugin/blob/master/CONTRIBUTING.md#code-style-guidelines).
