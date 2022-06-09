# Platform Labeler Plugin

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MarkEWaite_platformlabeler-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=MarkEWaite_platformlabeler-plugin)
[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/3537/badge)](https://bestpractices.coreinfrastructure.org/projects/3537)

Adds labels to Jenkins agents based on characteristics of the operating system running the agent.

Labels commonly include operating system name, version, architecture, and Windows feature update.

## Intel / AMD x64 (amd64)

| Platform                   | OS Name            | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| Alibaba Linux 3            | `AlibabaCloud`     | `3`            | `amd64`      | // EOL: 30 Apr 2031
| Alma Linux 8               | `AlmaLinux`        | `8.6`          | `amd64`      | // EOL: 31 Mar 2029
| Alpine 3.13                | `Alpine`           | `3.13.10`      | `amd64`      | // EOL: 01 Nov 2022
| Alpine 3.14                | `Alpine`           | `3.14.6`       | `amd64`      | // EOL: 01 May 2023
| Alpine 3.15                | `Alpine`           | `3.15.4`       | `amd64`      | // EOL: 01 Nov 2023
| Alpine 3.16                | `Alpine`           | `3.16.0`       | `amd64`      | // EOL: 01 May 2024
| Amazon Linux 2             | `Amazon`           | `2`            | `amd64`      | // EOL: 30 Jun 2023
| Amazon Linux 2022          | `Amazon`           | `2022`         | `amd64`      | // EOL: 31 Dec 2028?
| CentOS 7                   | `CentOS`           | `7.9.2009`     | `amd64`      | // EOL: 30 Jun 2024
| Clear Linux                | `clear-linux-os`   | `36120`        | `amd64`      |
| Debian 10                  | `Debian`           | `10`           | `amd64`      | // EOL: 30 Jun 2024
| Debian 11                  | `Debian`           | `11`           | `amd64`      | // EOL: 30 Jun 2026
| Debian testing             | `Debian`           | `testing`      | `amd64`      |
| Debian unstable            | `Debian`           | `unstable`     | `amd64`      |
| Fedora 35                  | `Fedora`           | `35`           | `amd64`      | // EOL: 15 Nov 2022
| FreeBSD 12                 | `freebsd`          | `12.3-RELEASE` | `amd64`      | // EOL: 30 Jun 2024
| FreeBSD 13                 | `freebsd`          | `13.0-RELEASE` | `amd64`      | // EOL: 31 Jan 2026
| Linux Mint 19.3            | `LinuxMint`        | `19.3`         | `amd64`      | // EOL:        2023
| Linux Mint 20.3            | `Linuxmint`        | `20.3`         | `amd64`      | // EOL:    Apr 2025
| openSUSE Leap              | `openSUSE`         | `15.3`         | `amd64`      | // EOL: 30 Nov 2022
| Oracle Linux 7             | `OracleServer`     | `7.9`          | `amd64`      | // EOL: 30 Jun 2024
| Oracle Linux 8             | `OracleServer`     | `8.6`          | `amd64`      | // EOL: 31 May 2029
| Red Hat Enterprise Linux 7 | `RedHatEnterprise` | `7.9`          | `amd64`      | // EOL: 30 Jun 2024
| Red Hat Enterprise Linux 8 | `RedHatEnterprise` | `8.6`          | `amd64`      | // EOL: 31 May 2029
| Scientific 7               | `Scientific`       | `7.9`          | `amd64`      | // EOL: 30 Jun 2024
| SLES 12                    | `SUSE`             | `12.4`         | `amd64`      | // EOL: 31 Oct 2027
| SLES 15                    | `SUSE`             | `15`           | `amd64`      | // EOL: 31 Jul 2031
| Ubuntu 18                  | `Ubuntu`           | `18.04`        | `amd64`      | // EOL: 30 Apr 2023
| Ubuntu 20                  | `Ubuntu`           | `20.04`        | `amd64`      | // EOL: 30 Apr 2025
| Ubuntu 21                  | `Ubuntu`           | `21.10`        | `amd64`      | // EOL: 31 Jul 2022
| Ubuntu 22                  | `Ubuntu`           | `22.04`        | `amd64`      | // EOL: 30 Apr 2027
| Windows 10                 | `windows`          | `10.0`         | `amd64`      | // EOL: 14 Oct 2025

## ARM 64 bit (aarch64)

| Platform                   | OS Name            | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| Debian 10                  | `Debian`           | `10`           | `aarch64`    |
| Oracle Linux 8             | `OracleServer`     | `8.5`          | `aarch64`    |

## ARM 32 bit (arm)

| Platform                   | OS Name            | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| Raspbian 10                | `Raspbian`         | `10`           | `arm`        |
| Raspbian 11                | `Raspbian`         | `11`           | `arm`        |

## IBM System 390 (s390x)

| Platform                   | OS Name            | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| Ubuntu 18                  | `Ubuntu`           | `18.04`        | `s390x`      | // EOL: 30 Apr 2023
| Ubuntu 20                  | `Ubuntu`           | `20.04`        | `s390x`      | // EOL: 30 Apr 2025

## IBM PowerPC 64 little endian (ppc64le)

| Platform                   | OS Name            | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| Ubuntu 18                  | `Ubuntu`           | `18.04`        | `ppc64le`    | // EOL: 30 Apr 2023

On Windows computers, the plugin assigns a label based on the Windows feature update.
Feature update labels use a two digit year and a two digit month representation.
Common values for Windows feature update are `1809`, `1903`, `2009`, and `2109`.

On Linux computers, the plugin uses the output of the [`lsb_release`](https://linux.die.net/man/1/lsb_release) command.

If `lsb_release` is not installed, labels on Linux agents will be guessed based on values in /etc/os-release.

Red Hat Linux and Scientific Linux agents have another fallback based on /etc/redhat-release.

Agents with an older version of SuSE Linux will fallback to `/etc/SuSE-release`. Older versions of this plugin might return "sles" or "SUSE LINUX" as OS name.
This has been unified to "SUSE" as this is the lsb_release ID since 'SLES 12 SP2'.

Linux Mint changed its lsb_release distributor ID from "LinuxMint" to "Linuxmint" in Linux Mint 20.
The Linux Mint label will be "LinuxMint" for Linux Mint 19.03.
The Linux Mint label will be "Linuxmint" for Linux Mint 20 and later.
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

## Report an Issue

Please report issues and enhancements through the [Jenkins issue tracker](https://www.jenkins.io/participate/report-issue/redirect/#15650).
