#!/bin/bash

# Generate lsb_release -a files

if [ ! -d alpine ]; then
        cd src/test/resources/org/jvnet/hudson/plugins/platformlabeler || exit 1
fi

# If a Dockerfile is found, assume the parent directory name is the
# version number and the parent structure above it is the image name to
# be used.
#
# platformlabeler is used as a prefix for the tags because some of the tags are
# the same as the tags used in the official Docker images.

args=$@
all_files=$(find * -type f -name Dockerfile ! -path '*amzn/2022*' ! -path '*clearlinux*' ! -path '*tumbleweed*') # No lsb_release on clearlinux, lsb_release empty on tumbleweed
dockerfiles=${args:-$all_files}

for dockerfile in $dockerfiles; do
        parent=$(dirname $dockerfile)
        version=$(basename $parent)
        image=$(dirname $parent)
        if [[ "$image" == "alpine" ]]; then
	        continue
        fi
        echo ==== Generating lsb_release test data in $parent for image $image:$version
        (cd $parent \
                 && docker build -t platformlabeler/$image:$version . \
                 && docker run -t platformlabeler/$image:$version lsb_release -a | tr -d '\015' > lsb_release-a \
        )
done
