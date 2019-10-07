# Platform Labeler Plugin

Adds labels to Jenkins agents based on characteristics of the operating system running the agent.
Labels commonly include operating system name, version, and architecture.

| Platform                     | Labels assigned by plugin                                                                                     |
| ---------------------------- | ------------------------------------------------------------------------------------------------------------- |
| Alpine                       | `3.9.4` `Alpine` `Alpine-3.9.4` `amd64` `amd64-Alpine` `amd64-Alpine-3.9.4`                                   |
| Amazon Linux                 | `2018.03` `AmazonAMI` `AmazonAMI-2018.03` `amd64` `amd64-AmazonAMI` `amd64-AmazonAMI-2018.03`                 |
| Amazon Linux 2               | `2` `Amazon` `Amazon-2` `amd64` `amd64-Amazon` `amd64-Amazon-2`                                               |
| CentOS 6                     | `6.10` `CentOS` `CentOS-6.10` `amd64` `amd64-CentOS` `amd64-CentOS-6.10`                                      |
| CentOS 7                     | `7.7.1908` `CentOS` `CentOS-7.7.1908` `amd64` `amd64-CentOS` `amd64-CentOS-7.7.1908`                          |
| Debian 9                     | `9.11` `Debian` `Debian-9.11` `amd64` `amd64-Debian` `amd64-Debian-9.11`                                      |
| Debian 10                    | `10` `Debian` `Debian-10` `amd64` `amd64-Debian` `amd64-Debian-10`                                            |
| Debian testing               | `testing` `Debian` `Debian-testing` `amd64` `amd64-Debian` `amd64-Debian-testing`                             |
| FreeBSD 11                   | `11.1-STABLE` `amd64` `amd64-freebsd` `amd64-freebsd-11.1-STABLE` `freebsd` `freebsd-11.1-STABLE`             |
| FreeBSD 12                   | `12.0-RELEASE` `amd64` `amd64-freebsd` `amd64-freebsd-12.0-RELEASE` `freebsd` `freebsd-12.0-RELEASE`          |
| Oracle Linux 6               | `6.10` `OracleServer` `OracleServer-6.10` `amd64` `amd64-OracleServer` `amd64-OracleServer-6.10`              |
| Oracle Linux 7               | `7.6` `OracleServer` `OracleServer-7.6` `amd64` `amd64-OracleServer` `amd64-OracleServer-7.6`                 |
| Oracle Linux 8               | `8.0` `OracleServer` `OracleServer-8.0` `amd64` `amd64-OracleServer` `amd64-OracleServer-8.0`                 |
| Red Hat Enterprise Linux 7.7 | `7.7` `RedHatEnterprise` `RedHatEnterprise-7.7` `amd64` `amd64-RedHatEnterprise` `amd64-RedHatEnterprise-7.7` |
| Red Hat Enterprise Linux 8.0 | `8.0` `RedHatEnterprise` `RedHatEnterprise-8.0` `amd64` `amd64-RedHatEnterprise` `amd64-RedHatEnterprise-8.0` |
| Scientific 6.10              | `6.10` `Scientific` `Scientific-6.10` `amd64` `amd64-Scientific` `amd64-Scientific-6.10`                      |
| Scientific 7.7               | `7.7` `Scientific` `Scientific-7.7` `amd64` `amd64-Scientific` `amd64-Scientific-7.7`                         |
| Ubuntu 14.04                 | `14.04` `Ubuntu` `Ubuntu-14.04` `amd64` `amd64-Ubuntu` `amd64-Ubuntu-14.04`                                   |
| Ubuntu 16.04                 | `16.04` `Ubuntu` `Ubuntu-16.04` `amd64` `amd64-Ubuntu` `amd64-Ubuntu-16.04`                                   |
| Ubuntu 18.04                 | `18.04` `Ubuntu` `Ubuntu-18.04` `amd64` `amd64-Ubuntu` `amd64-Ubuntu-18.04`                                   |
| Windows 10                   | `10.0` `amd64` `amd64-windows` `amd64-windows-10.0` `windows` `windows-10.0`                                  |

On Linux computers, the plugin uses the output of the `[lsb_release](https://linux.die.net/man/1/lsb_release)` command.
If `lsb_release` is not installed, labels on Linux agents will be guessed based on values in /etc/os-release.
Redhat and Scientific agents have another fallback based on /etc/redhat-release.
When /etc/os-release is used, less detailed labels are provided.
For example:

| Platform         | Labels assigned by plugin when lsb-release is not installed     |
| ---------------- | --------------------------------------------------------------- |
| CentOS 7         | `7` `CentOS` `CentOS-7` `amd64` `amd64-CentOS` `amd64-CentOS-7` |

Source code is formatted with the command:

    mvn tidy:pom com.coveo:fmt-maven-plugin:format
