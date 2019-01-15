# Platform Labeler Plugin

Adds labels to Jenkins agents based on characteristics of the operating system running the agent.
Labels commonly include operating system name, version, and architecture.

| Operating System | Labels assigned by plugin                                                                         |
| ---------------- | ------------------------------------------------------------------------------------------------- |
| Debian 9         | `9.4` `Debian` `Debian-9` `Debian-9.4` `amd64` `amd64-Debian` `amd64-Debian-9.4`                  |
| CentOS 7         | `7.4.1708` `CentOS` `CentOS-7` `CentOS-7.4.1708` `amd64` `amd64-CentOS` `amd64-CentOS-7.4.1708`   |
| Ubuntu 16.04     | `16.04` `Ubuntu` `Ubuntu-16` `amd64` `amd64-Ubuntu` `amd64-Ubuntu-16.04`                          |
| Windows 10       | `10.0` `amd64` `amd64-windows` `amd64-windows-10.0` `windows` `windows-10.0`                      |
| FreeBSD 11       | `11.1-STABLE` `amd64` `amd64-freebsd` `amd64-freebsd-11.1-STABLE` `freebsd` `freebsd-11.1-STABLE` |

On Linux computers, the plugin uses the output of the `lsb_release` command.
If `lsb_release` is not installed, the labels on Linux machines will be incorrect.

Source code is formatted with the command:

    mvn com.coveo:fmt-maven-plugin:format
