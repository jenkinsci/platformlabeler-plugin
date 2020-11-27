# Platform Labeler Plugin

[![Build Status](https://ci.jenkins.io/job/Plugins/job/platformlabeler-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/platformlabeler-plugin/job/master/)
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/platformlabeler.svg)](https://plugins.jenkins.io/platformlabeler)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/platformlabeler-plugin.svg?label=changelog)](https://github.com/jenkinsci/platformlabeler-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/platformlabeler.svg?color=blue)](https://plugins.jenkins.io/platformlabeler)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MarkEWaite_platformlabeler-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=MarkEWaite_platformlabeler-plugin)
[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/3537/badge)](https://bestpractices.coreinfrastructure.org/projects/3537)

Adds labels to Jenkins agents based on characteristics of the operating system running the agent.

Labels commonly include operating system name, version, and architecture.

| Platform                   | Operating System   | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| Alpine                     | `Alpine`           | `3.9.4`        | `amd64`      |
| Alpine                     | `Alpine`           | `3.12.0`       | `amd64`      |
| Amazon Linux               | `AmazonAMI`        | `2018.03`      | `amd64`      |
| Amazon Linux 2             | `Amazon`           | `2`            | `amd64`      |
| CentOS 6                   | `CentOS`           | `6.10`         | `amd64`      |
| CentOS 7                   | `CentOS`           | `7.8.2003`     | `amd64`      |
| CentOS 8                   | `CentOS`           | `8.0.1905`     | `amd64`      |
| CentOS 8.2                 | `CentOS`           | `8.2.2004`     | `amd64`      |
| Clear Linux                | `clear-linux-os`   | `31890`        | `amd64`      |
| Debian 9                   | `Debian`           | `9.13`         | `amd64`      |
| Debian 10                  | `Debian`           | `10`           | `aarch64`    |
| Debian 10                  | `Debian`           | `10`           | `amd64`      |
| Debian testing             | `Debian`           | `testing`      | `amd64`      |
| Debian unstable            | `Debian`           | `unstable`     | `amd64`      |
| Fedora 29                  | `Fedora`           | `29`           | `amd64`      |
| Fedora 30                  | `Fedora`           | `30`           | `amd64`      |
| Fedora 31                  | `Fedora`           | `31`           | `amd64`      |
| Fedora 32                  | `Fedora`           | `32`           | `amd64`      |
| Fedora 33                  | `Fedora`           | `33`           | `amd64`      |
| FreeBSD 11                 | `freebsd`          | `11.2-STABLE`  | `amd64`      |
| FreeBSD 12                 | `freebsd`          | `12.0-RELEASE` | `amd64`      |
| IBM PowerPC Ubuntu 18.04   | `Ubuntu`           | `18.04`        | `ppc64le`    |
| IBM s390x Ubuntu 18.04     | `Ubuntu`           | `18.04`        | `s390x`      |
| Linux Mint 19.3            | `LinuxMint`        | `19.03`        | `amd64`      |
| Oracle Linux 6             | `OracleServer`     | `6.10`         | `amd64`      |
| Oracle Linux 7             | `OracleServer`     | `7.8`          | `amd64`      |
| Oracle Linux 8             | `OracleServer`     | `8.2`          | `amd64`      |
| Raspbian 9                 | `Raspbian`         | `9.11`         | `arm`        |
| Raspbian 10                | `Raspbian`         | `10`           | `arm`        |
| Red Hat Enterprise Linux 7 | `RedHatEnterprise` | `7.8`          | `amd64`      |
| Red Hat Enterprise Linux 8 | `RedHatEnterprise` | `8.2`          | `amd64`      |
| Scientific 6.10            | `Scientific`       | `6.10`         | `amd64`      |
| Scientific 7.7             | `Scientific`       | `7.8`          | `amd64`      |
| SLES 11.3                  | `SUSE`             | `11.3`         | `amd64`      |
| SLES 12.1                  | `SUSE`             | `12.1`         | `amd64`      |
| SLES 12.2                  | `SUSE`             | `12.2`         | `amd64`      |
| SLES 15                    | `SUSE`             | `15`           | `amd64`      |
| Ubuntu 14                  | `Ubuntu`           | `14.04`        | `amd64`      |
| Ubuntu 16                  | `Ubuntu`           | `16.04`        | `amd64`      |
| Ubuntu 18                  | `Ubuntu`           | `18.04`        | `amd64`      |
| Ubuntu 19                  | `Ubuntu`           | `19.10`        | `amd64`      |
| Ubuntu 20                  | `Ubuntu`           | `20.04`        | `amd64`      |
| Windows 10                 | `windows`          | `10.0`         | `amd64`      |

On Linux computers, the plugin uses the output of the [`lsb_release`](https://linux.die.net/man/1/lsb_release) command.

If `lsb_release` is not installed, labels on Linux agents will be guessed based on values in /etc/os-release.
Red Hat Linux and Scientific Linux agents have another fallback based on /etc/redhat-release.
Agents with an older version of SuSE Linux will fallback to `/etc/SuSE-release`. Older versions of this plugin might return "sles" or "SUSE LINUX" as OS name.
This has been unified to "SUSE" as this is the lsb_release ID since 'SLES 12 SP2'.

When `/etc/os-release` is used, less detailed labels may be provided because more specific version information is not included in the file.
For example:

| Platform                   | Operating System   | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| CentOS 7                   | `CentOS`           | `7`            | `amd64`      |

The types of labels can be configured globally and per agent with the 'Automatic Platform Labels' setting.

To define the labels for an agent, activate 'Automatic Platform Labels' in the Node Properties section and select the desired labels.
