#!/bin/bash

# Report operating systems in README that have reached end of life.

declare -i eol_count=0
now=$(date +%s)
for eol_date in $(grep EOL: README.md | sed 's,^.*EOL:,,g' | xargs -r -i date -d {} +%s); do
        if [ "$now" -gt "$eol_date" ]; then
                date -d @$eol_date '+%e %b %Y'
                eol_count=eol_count+1
        fi
done

if [ $eol_count != 0 ] ; then
        echo Detected $eol_count end of life in README
        exit $eol_count
fi

# Check that the processor is new enough to do the work

if docker run --rm -i -t almalinux:10.1-minimal cat /etc/os-release 2>&1 | grep -q 'Fatal glibc error'; then
        echo Processor is too old to update test data
        exit 1
fi

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

# Generate os-release files
# Generate redhat-release files for all systems that include the redhat-release file
for Dockerfile in $(find * -type f -name Dockerfile -print); do
        name_version=$(dirname $Dockerfile)
        name=$(dirname $name_version)
        version=$(basename $name_version)
        image=platformlabeler/$name:$version
        echo "Processing Dockerfile $Dockerfile for name $name and version $version"
        (cd $name_version && docker build --pull -t platformlabeler/$name:$version .)

        #
        # Extract os-release file
        #
        echo "=== Generating os-release file for $name_version"
        container=$(docker container create $image)
        trap "docker container rm $container" EXIT
        docker cp -L $container:/etc/os-release $name_version/os-release

        #
        # Extract redhat-release file
        #
        docker cp -L $container:/etc/redhat-release $name_version/redhat-release 2> /dev/null
        # Remove redhat-release file for Oracle Linux, it contains the wrong vendor name
        if [ "$name" = "oraclelinux" ]; then
                rm -rf $name_version/redhat-release
        fi

        #
        # Collect lsb_release -a output
        #
        if docker run --rm -t $image ls /usr/bin/lsb_release > /dev/null 2>&1; then
                docker run -t $image lsb_release -a | tr -d '\015' > $name_version/lsb_release-a
        else
                # Create empty lsb_release-a data file
                : > $name_version/lsb_release-a
        fi
        # Remove lsb_release-a file if it does not exist in the image image
        grep -q -i cat:.*no.such.file $name_version/lsb_release-a && rm -rf $name_version/lsb_release-a
        if [ ! -s $name_version/lsb_release-a ]; then
                # Remove empty lsb_release-a data file
                rm -rf $name_version/lsb_release-a
        fi
        # Remove lsb_release-a file for opensuse_tumbleweed
        # Frequent changes create too much overhead to maintain its test data
        if [ $name_version = opensuse-tumbleweed/2026 ]; then
               rm -rf $name_version/lsb_release-a
        fi

done

exit 0
