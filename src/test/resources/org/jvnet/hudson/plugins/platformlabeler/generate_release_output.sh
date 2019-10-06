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

files=("os-release" "redhat-release")
for name in "${files[@]}"
do
	for os_release in $(find * -type f -name $name); do
	        parent=$(dirname $os_release)
	        version=$(basename $parent)
	        image=$(dirname $parent)
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
	        id=`docker ps -lqf ancestor=$image:$version`
	        echo id=$id parent=$parent version=$version image=$image $name
	        (cd $parent && docker cp -L $id:/etc/$name $name)
	done
done