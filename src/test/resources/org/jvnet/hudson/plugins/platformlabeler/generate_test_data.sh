#!/bin/bash

# Generate os-release test data files from operating system images

# Copy /etc/os-release from the operating system into a directory
# named for the operating system docker image and the operating system version.
# Once that file is created, this script will detect the existence of the file
# and use the docker image and version from the directory name to insert
# the content of the file.

if [ ! -d alpine ]; then
        cd src/test/resources/org/jvnet/hudson/plugins/platformlabeler || exit 1
fi

args=$@

for Dockerfile in $(find * -type f -name Dockerfile ! -path '*7.9.2009*' -print); do
        name_version=$(dirname $Dockerfile)
        name=$(dirname $name_version)
        version=$(basename $name_version)
        echo "Processing Dockerfile $Dockerfile for name $name and version $version"
        (cd $name_version && docker build --pull -t platformlabeler/$name:$version .)
        docker run --rm -t platformlabeler/$name:$version cat /etc/os-release | tr -d '\015' > $name_version/os-release
done

exit 0
