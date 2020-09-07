#!/bin/bash

# Generate os-release and redhat-release test data files from operating system images

# Copy the /etc/os-release and/or /etc/redhat-release file from the operating system into a directory
# named for the operating system docker image and the operating system version.
# Once that file is created, this script will detect the existence of the file
# and uses the docker image and version from the directory name to insert
# the content of the file.

if [ ! -d alpine ]; then
        cd src/test/resources/org/jvnet/hudson/plugins/platformlabeler || exit 1
fi

args=$@

for alpine_os_release in alpine/*/os-release; do
        name_version=$(dirname $alpine_os_release)
        name=$(dirname $name_version)
        version=$(basename $name_version)
        docker run --rm -i -t platformlabeler/$name:$version ls /etc/os-release || (cd $name_version && docker build -t platformlabeler/$name:$version .)
        docker run --rm -i -t platformlabeler/$name:$version cat /etc/os-release | tr -d '\015' > $alpine_os_release
done

files=("os-release" "redhat-release")
for name in "${files[@]}"
do
        all_files=$(find * -type f -name $name)
        release_files=${args:-$all_files}
        for os_release in $release_files; do
                parent=$(dirname $os_release)
                version=$(basename $parent)
                image=platformlabeler/$(dirname $parent)
                if [ "$image" == "amzn" ]; then
                        image="amazonlinux"
                fi
                 if [ "$image" == "scientific" ]; then
                        image="sl"
                        if [ "$version" == "6.10" ]; then
                                version="6"
                        fi
                        if [ "$version" == "7.7" ]; then
                                version="7"
                        fi
                fi
                if [ "$name" == "redhat-release" ]; then
                        if [ "$image" == "ubuntu" -o "$image" == "debian" ]; then
                                continue # No redhat-release file on distributions not derived from Red Hat
                        fi
                fi
                if ! docker images | grep -q $image; then
                        echo "Skipping image: $image"
                        continue
                fi
                echo
                echo "================"
                echo "= Finding image identifier: $image"
                echo "docker ps -lqf ancestor=$image:$version"
                id=`docker ps -lqf ancestor=$image:$version`
                echo "id is $id parent is $parent version is version image is $image $name"
                echo "cd $parent && docker cp -L $id:/etc/$name $name"
                (cd $parent && docker cp -L $id:/etc/$name $name)
                echo "finished id=$id parent=$parent version=$version image=$image $name"
                echo "================"
        done
done
