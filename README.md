# Platform Labeler Plugin

Adds labels to Jenkins agents based on characteristics of the operating system running the agent.
Labels commonly include operating system name, version, and architecture.

| Platform                     | Operating System   | Version        | Platform |
| ---------------------------- | ------------------ | -------------- | -------- |
| Alpine                       | `Alpine`           | `3.9.4`        | `amd64`  |
| Amazon Linux                 | `AmazonAMI`        | `2018.03`      | `amd64`  |
| Amazon Linux 2               | `Amazon`           | `2`            | `amd64`  |
| CentOS 6                     | `CentOS`           | `6.10`         | `amd64`  |
| CentOS 7                     | `CentOS`           | `7.7.1908`     | `amd64`  |
| Debian 9                     | `Debian`           | `9.11`         | `amd64`  |
| Debian 10                    | `Debian`           | `10`           | `amd64`  |
| Debian testing               | `Debian`           | `testing`      | `amd64`  |
| FreeBSD 11                   | `freebsd`          | `11.1-STABLE`  | `amd64`  |
| FreeBSD 12                   | `freebsd`          | `12.0-RELEASE` | `amd64`  |
| Oracle Linux 6               | `OracleServer`     | `6.10`         | `amd64`  |
| Oracle Linux 7               | `OracleServer`     | `7.6`          | `amd64`  |
| Oracle Linux 8               | `OracleServer`     | `8.0`          | `amd64`  |
| Red Hat Enterprise Linux 7.7 | `RedHatEnterprise` | `7.7`          | `amd64`  |
| Red Hat Enterprise Linux 8.0 | `RedHatEnterprise` | `8.0`          | `amd64`  |
| Scientific 6.10              | `Scientific`       | `6.10`         | `amd64`  |
| Scientific 7.7               | `Scientific`       | `7.7`          | `amd64`  |
| Ubuntu 14.04                 | `Ubuntu`           | `14.04`        | `amd64`  |
| Ubuntu 16.04                 | `Ubuntu`           | `16.04`        | `amd64`  |
| Ubuntu 18.04                 | `Ubuntu`           | `18.04`        | `amd64`  |
| Windows 10                   | `windows`          | `10.0`         | `amd64`  |

On Linux computers, the plugin uses the output of the `[lsb_release](https://linux.die.net/man/1/lsb_release)` command.
If `lsb_release` is not installed, labels on Linux agents will be guessed based on values in /etc/os-release.
Redhat and Scientific agents have another fallback based on /etc/redhat-release.
When /etc/os-release is used, less detailed labels are provided.
For example:

| Platform                     | Operating System   | Version        | Platform |
| ---------------------------- | ------------------ | -------------- | -------- |
| CentOS 7                     | `CentOS`           | `7`            | `amd64`  |
