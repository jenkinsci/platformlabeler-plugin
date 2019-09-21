# Platform Labeler Plugin

Adds labels to Jenkins agents based on characteristics of the operating system running the agent.
Labels commonly include operating system name, version, and architecture.

| Operating System | Labels assigned by plugin                                                                            |
| ---------------- | ---------------------------------------------------------------------------------------------------- |
| Amazon Linux     | `2018.03` `AmazonAMI` `AmazonAMI-2018.03` `amd64` `amd64-AmazonAMI` `amd64-AmazonAMI-2018.03`        |
| Amazon Linux 2   | `2` `Amazon` `Amazon-2` `amd64` `amd64-Amazon` `amd64-Amazon-2`                                      |
| CentOS 6         | `6.10` `CentOS` `CentOS-6.10` `amd64` `amd64-CentOS` `amd64-CentOS-6.10`                             |
| CentOS 7         | `7.4.1708` `CentOS` `CentOS-7.4.1708` `amd64` `amd64-CentOS` `amd64-CentOS-7.4.1708`                 |
| Debian 9         | `9.6` `Debian` `Debian-9.6` `amd64` `amd64-Debian` `amd64-Debian-9.6`                                |
| Debian 10        | `10` `Debian` `Debian-10` `amd64` `amd64-Debian` `amd64-Debian-10`                                   |
| FreeBSD 11       | `11.1-STABLE` `amd64` `amd64-freebsd` `amd64-freebsd-11.1-STABLE` `freebsd` `freebsd-11.1-STABLE`    |
| FreeBSD 12       | `12.0-RELEASE` `amd64` `amd64-freebsd` `amd64-freebsd-12.0-RELEASE` `freebsd` `freebsd-12.0-RELEASE` |
| Ubuntu 16.04     | `16.04` `Ubuntu` `Ubuntu-16.04` `amd64` `amd64-Ubuntu` `amd64-Ubuntu-16.04`                          |
| Ubuntu 18.04     | `18.04` `Ubuntu` `Ubuntu-18.04` `amd64` `amd64-Ubuntu` `amd64-Ubuntu-18.04`                          |
| Windows 10       | `10.0` `amd64` `amd64-windows` `amd64-windows-10.0` `windows` `windows-10.0`                         |

On Linux computers, the plugin uses the output of the `lsb_release` command.
If `lsb_release` is not installed, labels on Linux machines will be guessed based on values in /etc/os-release.

Source code is formatted with the command:

    mvn com.coveo:fmt-maven-plugin:format
