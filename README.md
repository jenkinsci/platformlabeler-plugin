# Platform Labeler Plugin

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MarkEWaite_platformlabeler-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=MarkEWaite_platformlabeler-plugin)
[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/3537/badge)](https://bestpractices.coreinfrastructure.org/projects/3537)

Adds labels to Jenkins agents based on characteristics of the operating system running the agent.

Labels commonly include operating system name, version, and architecture.

| Platform                   | OS Name            | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| Alpine 3.12                | `Alpine`           | `3.12.8`       | `amd64`      |
| Alpine 3.13                | `Alpine`           | `3.13.6`       | `amd64`      |
| Alpine 3.14                | `Alpine`           | `3.14.2`       | `amd64`      |
| Amazon Linux 2             | `Amazon`           | `2`            | `amd64`      |
| CentOS 7                   | `CentOS`           | `7.9.2009`     | `amd64`      |
| CentOS 8                   | `CentOS`           | `8.4.2105`     | `amd64`      |
| Clear Linux                | `clear-linux-os`   | `35030`        | `amd64`      |
| Debian 9                   | `Debian`           | `9.13`         | `amd64`      |
| Debian 10                  | `Debian`           | `10`           | `aarch64`    |
| Debian 10                  | `Debian`           | `10`           | `amd64`      |
| Debian 11                  | `Debian`           | `11`           | `amd64`      |
| Debian testing             | `Debian`           | `testing`      | `amd64`      |
| Debian unstable            | `Debian`           | `unstable`     | `amd64`      |
| Fedora 33                  | `Fedora`           | `33`           | `amd64`      |
| Fedora 34                  | `Fedora`           | `34`           | `amd64`      |
| FreeBSD 11                 | `freebsd`          | `11.4-STABLE`  | `amd64`      |
| FreeBSD 12                 | `freebsd`          | `12.2-RELEASE` | `amd64`      |
| Linux Mint 19.3            | `LinuxMint`        | `19.3`         | `amd64`      |
| Linux Mint 20              | `Linuxmint`        | `20`           | `amd64`      |
| Linux Mint 20.1            | `Linuxmint`        | `20.1`         | `amd64`      |
| openSUSE Leap              | `openSUSE`         | `15.3`         | `amd64`      |
| Oracle Linux 7             | `OracleServer`     | `7.9`          | `amd64`      |
| Oracle Linux 8             | `OracleServer`     | `8.4`          | `amd64`      |
| Raspbian 9                 | `Raspbian`         | `9.13`         | `arm`        |
| Raspbian 10                | `Raspbian`         | `10`           | `arm`        |
| Red Hat Enterprise Linux 7 | `RedHatEnterprise` | `7.9`          | `amd64`      |
| Red Hat Enterprise Linux 8 | `RedHatEnterprise` | `8.4`          | `amd64`      |
| Scientific 7               | `Scientific`       | `7.9`          | `amd64`      |
| SLES 12                    | `SUSE`             | `12.4`         | `amd64`      |
| SLES 15                    | `SUSE`             | `15`           | `amd64`      |
| Ubuntu 18                  | `Ubuntu`           | `18.04`        | `amd64`      |
| Ubuntu 18                  | `Ubuntu`           | `18.04`        | `ppc64le`    |
| Ubuntu 18                  | `Ubuntu`           | `18.04`        | `s390x`      |
| Ubuntu 20                  | `Ubuntu`           | `20.04`        | `amd64`      |
| Ubuntu 21                  | `Ubuntu`           | `21.04`        | `amd64`      |
| Windows 10                 | `windows`          | `10.0`         | `amd64`      |

On Linux computers, the plugin uses the output of the [`lsb_release`](https://linux.die.net/man/1/lsb_release) command.

If `lsb_release` is not installed, labels on Linux agents will be guessed based on values in /etc/os-release.

Red Hat Linux and Scientific Linux agents have another fallback based on /etc/redhat-release.

Agents with an older version of SuSE Linux will fallback to `/etc/SuSE-release`. Older versions of this plugin might return "sles" or "SUSE LINUX" as OS name.
This has been unified to "SUSE" as this is the lsb_release ID since 'SLES 12 SP2'.

Linux Mint changed its lsb_release distributor ID from "LinuxMint" to "Linuxmint" in Linux Mint 20.
The Linux Mint label will be "LinuxMint" for Linux Mint 19.03.
The Linux Mint label will be "Linuxmint" for Linux Mint 20, Linux Mint 20.1, and Linux Mint 20.2.
Users running Linux Mint 19 agents may need to adjust their label references to use "Linuxmint" instead of "LinuxMint".

When `/etc/os-release` is used, less detailed labels may be provided because more specific version information is not included in the file.
For example:

| Platform                   | Operating System   | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| CentOS 7                   | `CentOS`           | `7`            | `amd64`      |
| Debian testing             | `Debian`           | `bookworm`     | `amd64`      |
| Debian unstable            | `Debian`           | `bookworm`     | `amd64`      |

The types of labels can be configured globally and per agent with the 'Automatic Platform Labels' setting.

To define the labels for an agent, activate 'Automatic Platform Labels' in the Node Properties section and select the desired labels.
