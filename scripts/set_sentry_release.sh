#!/usr/bin/env bash

# Run this script to set a new Sentry release and to get Sentry to track commits
# https://docs.sentry.io/workflow/releases/

# Assumes you're in a git repository
export SENTRY_AUTH_TOKEN=9b25fe435f0045e19608b1ae8010318d45f206268d104c9e9b28fcd08385b381
export SENTRY_ORG=neon-king-kong
VERSION=$(sentry-cli releases propose-version)

# Create and finalize a release
sentry-cli releases new -p chatloop-user-training "$VERSION" --finalize

# Associate commits with the release
sentry-cli releases set-commits --auto "$VERSION"