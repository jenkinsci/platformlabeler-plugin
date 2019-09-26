#!/bin/bash

# Generate os-release test data files from operating system images

# Copy the /etc/os-release file from the operating system into a directory
# named for the operating system docker image and the operating system version.
# Once that file is created, this script will detect the existence of the file
# and uses the docker image and version from the directory name to insert
# the content of the file.

if [ ! -d alpine ]; then
        cd src/test/resources/org/jvnet/hudson/plugins/platformlabeler || exit 1
fi

for os_release in $(find * -type f -name os-release); do
        parent=$(dirname $os_release)
        version=$(basename $parent)
        image=$(dirname $parent)
        if [ "$image" == "amzn" ]; then
                image="amazonlinux"
        fi
        echo parent=$parent version=$version image=$image
        (cd $parent && docker run -t $image:$version cat /etc/os-release | tr -d '\015' > os-release)
done
