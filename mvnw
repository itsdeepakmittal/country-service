#!/bin/sh
# Maven wrapper bootstrap script
# Delegates to the real Maven binary or downloads one if not present.

# Resolve the script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# If a local Maven installation is detected, use it
if [ -n "$MAVEN_HOME" ] && [ -f "$MAVEN_HOME/bin/mvn" ]; then
  exec "$MAVEN_HOME/bin/mvn" "$@"
fi

if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
fi

# No Maven found – print a helpful error
echo "Maven not found. Please install Maven 3.8+ or set MAVEN_HOME."
echo "See: https://maven.apache.org/install.html"
exit 1
