# Platform Labeler Plugin

Adds labels to Jenkins agents based on characteristics of the operating system running the agent.
Labels commonly include operating system name, version, and architecture.

| Operating System | Labels assigned by plugin                                                                         |
| ---------------- | ------------------------------------------------------------------------------------------------- |
| Amazon Linux     | `2018.03` `AmazonAMI` `AmazonAMI-2018.03` `amd64` `amd64-AmazonAMI` `amd64-AmazonAMI-2018.03`     |
| CentOS 7         | `7.4.1708` `CentOS` `CentOS-7.4.1708` `amd64` `amd64-CentOS` `amd64-CentOS-7.4.1708`              |
| Debian 9         | `9.6` `Debian` `Debian-9.6` `amd64` `amd64-Debian` `amd64-Debian-9.6`                             |
| FreeBSD 11       | `11.1-STABLE` `amd64` `amd64-freebsd` `amd64-freebsd-11.1-STABLE` `freebsd` `freebsd-11.1-STABLE` |
| Ubuntu 16.04     | `16.04` `Ubuntu` `Ubuntu-16.04` `amd64` `amd64-Ubuntu` `amd64-Ubuntu-16.04`                       |
| Windows 10       | `10.0` `amd64` `amd64-windows` `amd64-windows-10.0` `windows` `windows-10.0`                      |

On Linux computers, the plugin uses the output of the `lsb_release` command.
If `lsb_release` is not installed, the labels on Linux machines will be incorrect.

Source code is formatted with the command:

    mvn com.coveo:fmt-maven-plugin:format
