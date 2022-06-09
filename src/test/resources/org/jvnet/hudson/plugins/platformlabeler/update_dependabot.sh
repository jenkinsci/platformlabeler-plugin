#!/bin/bash

# Update dependabot definition for current files

if [ ! -f .github/dependabot.yml ]; then
  cd src/test/resources/org/jvnet/hudson/plugins/platformlabeler || exit 1
fi

cat > .github/dependabot.yml <<END-OF-DEPENDABOT-YML
# Per https://docs.github.com/en/github/administering-a-repository/configuration-options-for-dependency-updates
version: 2
updates:
- package-ecosystem: maven
  directory: "/"
  schedule:
    interval: weekly
  open-pull-requests-limit: 10
  target-branch: master
  reviewers:
  - MarkEWaite
  labels:
  - skip-changelog
  ignore:
  - dependency-name: org.slf4j*
    versions:
    - ">= 0"
END-OF-DEPENDABOT-YML

for file in $(git ls-files -- *Dockerfile | grep -E -v 'alpine/3.12|alpine/3.13|alpine/3.14|alpine/3.15|centos|clearlinux|debian/10|fedora|linuxmint|opensuse|oraclelinux|scientific|ubuntu/18' | sort -V); do
  dir=$(dirname $file)
  cat >> .github/dependabot.yml <<END-OF-DEPENDABOT-YML

- package-ecosystem: docker
  directory: "${dir}"
  schedule:
    interval: weekly
  open-pull-requests-limit: 2
  target-branch: master
  reviewers:
  - MarkEWaite
  labels:
  - skip-changelog
END-OF-DEPENDABOT-YML
done
