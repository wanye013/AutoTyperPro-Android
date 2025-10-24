#!/bin/sh

# Gradle Wrapper script for GitHub Actions
# This is a minimal version that works in CI environments

set -e

# Find the Gradle wrapper JAR
WRAPPER_JAR="$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Error: gradle-wrapper.jar not found!"
  exit 1
fi

# Run Gradle using Java
exec java -cp "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
