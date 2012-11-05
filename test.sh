#!/bin/bash

if [ -f build.version ]
then
    for input in $(cat build.version)
    do

        case "${input}" in 
            version.major=*)

                version_major=$(echo $input | sed 's/.*=//')
                ;;
            version.minor=*)

                version_minor=$(echo $input | sed 's/.*=//')
                ;;
            version.build=*)

                version_build=$(echo $input | sed 's/.*=//')
                ;;
        esac
    done
    if [ -n "${version_major}" ]&&[ -n "${version_major}" ]&&[ -n "${version_major}" ]
    then
        jarf="path-test-${version_major}.${version_minor}.${version_build}.jar"

        if [ -f "${jarf}" ]
        then

            java -jar ${jarf} 
        else
            cat<<EOF>&2
Error, file not found: '${jarf}'
EOF
            exit 1
        fi
    else
        cat<<EOF>&2
Error, unrecognized contents of 'build.version'
  version_major:  '${version_major}'
  version_minor:  '${version_minor}'
  version_build:  '${version_build}'
EOF
        exit 1
    fi
else
    cat<<EOF>&2
Error, file not found: 'build.version'
EOF
    exit 1
fi
