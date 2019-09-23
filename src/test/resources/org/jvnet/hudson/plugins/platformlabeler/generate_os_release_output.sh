#!/bin/bash

# Generate os-release test data files from operating system images

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
