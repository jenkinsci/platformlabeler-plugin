# Platform Labeler Plugin

[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/3537/badge)](https://bestpractices.coreinfrastructure.org/projects/3537)

Adds labels to Jenkins agents based on characteristics of the operating system running the agent.

Labels commonly include operating system name, version, architecture, and Windows feature update.

## Intel / AMD x64 (amd64)

| Platform                   | OS Name            | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| Alibaba Linux 3            | `AlibabaCloud`     | `3`            | `amd64`      | // EOL: 30 Apr 2031
| Alma Linux 8               | `AlmaLinux`        | `8.9`          | `amd64`      | // EOL: 31 Mar 2029
| Alma Linux 9               | `AlmaLinux`        | `9.3`          | `amd64`      | // EOL: 31 May 2032
| Alpine 3.16                | `Alpine`           | `3.16.8`       | `amd64`      | // EOL: 01 May 2024
| Alpine 3.17                | `Alpine`           | `3.17.6`       | `amd64`      | // EOL: 01 Nov 2024
| Alpine 3.18                | `Alpine`           | `3.18.5`       | `amd64`      | // EOL: 01 May 2025
| Amazon Linux 2             | `Amazon`           | `2`            | `amd64`      | // EOL: 30 Jun 2025
| Amazon Linux 2023          | `Amazon`           | `2023`         | `amd64`      | // EOL: 15 Mar 2028
| Clear Linux                | `clear-linux-os`   | `40390`        | `amd64`      |
| Debian 10                  | `Debian`           | `10`           | `amd64`      | // EOL: 30 Jun 2024
| Debian 11                  | `Debian`           | `11`           | `amd64`      | // EOL: 30 Jun 2026
| Debian 12                  | `Debian`           | `12`           | `amd64`      | // EOL: 30 Jun 2028
| Debian testing             | `Debian`           | `testing`      | `amd64`      |
| Debian unstable            | `Debian`           | `unstable`     | `amd64`      |
| Fedora 38                  | `Fedora`           | `38`           | `amd64`      | // EOL: 18 May 2024
| Fedora 39                  | `Fedora`           | `39`           | `amd64`      | // EOL:  7 Dec 2024
| FreeBSD 12                 | `freebsd`          | `12.4-RELEASE` | `amd64`      | // EOL: 31 Dec 2023
| FreeBSD 13                 | `freebsd`          | `13.2-RELEASE` | `amd64`      | // EOL: 31 Jan 2026
| openSUSE Leap              | `openSUSE`         | `15.5`         | `amd64`      | // EOL: 31 Dec 2024
| Oracle Linux 8             | `OracleServer`     | `8.8`          | `amd64`      | // EOL: 31 May 2029
| Oracle Linux 9             | `OracleServer`     | `9.3`          | `amd64`      | // EOL: 31 May 2032
| Red Hat Enterprise Linux 8 | `RedHatEnterprise` | `8.9`          | `amd64`      | // EOL: 31 May 2029
| Red Hat Enterprise Linux 9 | `RedHatEnterprise` | `9.3`          | `amd64`      | // EOL: 31 May 2032
| Rocky Linux 8              | `Rocky`            | `8.9`          | `amd64`      | // EOL: 31 May 2029
| Rocky Linux 9              | `Rocky`            | `9.3`          | `amd64`      | // EOL: 31 May 2032
| SLES 12                    | `SUSE`             | `12.5`         | `amd64`      | // EOL: 31 Oct 2027
| SLES 15                    | `SUSE`             | `15`           | `amd64`      | // EOL: 31 Jul 2031
| Ubuntu 20                  | `Ubuntu`           | `20.04`        | `amd64`      | // EOL:  2 Apr 2025
| Ubuntu 22                  | `Ubuntu`           | `22.04`        | `amd64`      | // EOL:  1 Apr 2027
| Windows 10                 | `windows`          | `10.0`         | `amd64`      | // EOL: 14 Oct 2025
| Windows Server 2016        | `WindowsServer2016`| `10.0`         | `amd64`      | // EOL: 12 Jan 2027
| Windows Server 2019        | `WindowsServer2019`| `10.0`         | `amd64`      | // EOL:  9 Jan 2029
| Windows Server 2022        | `WindowsServer2022`| `10.0`         | `amd64`      | // EOL: 14 Oct 2031

## ARM 64 bit (aarch64)

| Platform                   | OS Name            | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| Debian 10                  | `Debian`           | `10`           | `aarch64`    |
| Oracle Linux 8             | `OracleServer`     | `8.8`          | `aarch64`    |

## ARM 32 bit (arm)

| Platform                   | OS Name            | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| Raspberry Pi OS 10         | `Raspbian`         | `10`           | `arm`        |
| Raspberry Pi OS 11         | `Raspbian`         | `11`           | `arm`        |
| Raspberry Pi OS 12         | `Raspbian`         | `12`           | `arm`        |

## IBM System 390 (s390x)

| Platform                   | OS Name            | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| Ubuntu 22                  | `Ubuntu`           | `22.04`        | `s390x`      | // EOL:  2 Apr 2027

On Windows computers, the plugin assigns a label based on the Windows feature update.
Feature update labels use a two digit year and a two digit month representation.
Common values for Windows feature update are `1809`, `1903`, `2009`, and `2109`.

On Linux computers, the plugin uses the output of the [`lsb_release`](https://linux.die.net/man/1/lsb_release) command if the command is available.

If `lsb_release` is not installed, labels on Linux agents will be guessed based on values in `/etc/os-release`.
Red Hat Linux 9 and its derivatives intentionally do not delivery `lsb_release`.

Red Hat Linux agents have another fallback based on `/etc/redhat-release`.

Agents with an older version of SuSE Linux will fallback to `/etc/SuSE-release`. Older versions of this plugin might return "sles" or "SUSE LINUX" as OS name.
This has been unified to "SUSE" as this is the lsb_release ID since 'SLES 12 SP2'.

When `/etc/os-release` is used, less detailed labels may be provided because more specific version information is not included in the file.
For example:

| Platform                   | Operating System   | Version        | Architecture |
| -------------------------- | ------------------ | -------------- | ------------ |
| Debian testing             | `Debian`           | `bookworm`     | `amd64`      |
| Debian unstable            | `Debian`           | `bookworm`     | `amd64`      |

The types of labels can be configured globally and per agent with the 'Automatic Platform Labels' setting.

To reduce the set of labels defined for an agent, activate 'Automatic Platform Labels' in the Node Properties section and select the desired label types.

## Configuration as code

The platform labeler plugin supports configuration as code for global configuration and for agent configuration.
Here is a global configuration example:

```yaml
unclassified:
  platformLabelerGlobalConfiguration:
    labelConfig:
      architecture: true
      architectureName: false
      architectureNameVersion: false
      name: true
      nameVersion: false
      osName: true
      version: true
      windowsFeatureUpdate: false
```

Agent configuration uses a platform labeler node property like this:

```yaml
jenkins:
  nodes:
  - permanent:
      launcher:
        inbound:
          webSocket: true
      name: "my-windows-agent"
      nodeProperties:
      - platformLabeler:
          labelConfig:
            architecture: true
            architectureName: false
            architectureNameVersion: false
            name: true
            nameVersion: false
            osName: true
            version: true
            windowsFeatureUpdate: false
      remoteFS: "C:\\Users\\Jenkins\\agent"
      retentionStrategy: "always"
```

## Report an Issue

Please report issues and enhancements through the [Jenkins issue tracker](https://www.jenkins.io/participate/report-issue/redirect/#15650).
