#!/usr/bin/env bash
#creates a zip file that can be uploaded to the aws eb
set -euxo pipefail

find . -regex '.*/chatloop-user-training.*[0-9]\.jar' -or -name Dockerfile | xargs zip eb_deployable_archive.zip
