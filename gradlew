#!/usr/bin/env sh

##############################################################################
# Gradle start up script for UN*X
#
# Environment variables:
#
# JAVA_HOME - Must point to a JDK installation directory.
# GRADLE_HOME - Must point to your Gradle installation directory.
# GRADLE_OPTS - Optional JVM options to pass to the gradle process.
#
# Author: Gradle Inc.
##############################################################################

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS="-Xmx64m"

APP_NAME=gradle

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
  echo "$*" 1>&2
}

die () {
  echo
  echo "ERROR: $*" 1>&2
  echo
  exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false

case "$(uname)" in
  CYGWIN*) cygwin=true ;;
  MINGW*) msys=true ;;
  *) ;;
esac

CLASSPATH=""

# Determine Java command to use.
if [ -n "$JAVA_HOME" ] ; then
  if [ -x "$JAVA_HOME/bin/java" ] ; then
    JAVA_CMD="$JAVA_HOME/bin/java"
  else
    die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
  fi
else
  JAVA_CMD="java"
  if $cygwin ; then
    # Convert relative paths to absolute paths for Cygwin.
    JAVA_HOME=$(cygpath --absolute --windows "$JAVA_HOME")
    JAVA_CMD=$(cygpath --absolute --windows "$JAVA_CMD")
  fi
fi

# Increase the maximum file descriptors if we can.
if [ "$MAX_FD" != "-1" ] ; then
  MAX_FD_LIMIT=$(ulimit -H -n)
  if [ $? -eq 0 ] ; then
    if [ "$MAX_FD" = "maximum" ] || [ "$MAX_FD" -gt "$MAX_FD_LIMIT" ] ; then
      MAX_FD=$MAX_FD_LIMIT
    fi
    ulimit -n $MAX_FD
  fi
fi

# Determine the Gradle home directory.
if [ -n "$GRADLE_HOME" ] ; then
  GRADLE_HOME=$(cd "$GRADLE_HOME" && pwd)
fi

# Determine the location of the wrapper jar.
APP_BASE_NAME=$(basename "$0")
APP_HOME=$(dirname "$0")

if [ -n "$GRADLE_HOME" ] ; then
  WRAPPER_JAR="$GRADLE_HOME/lib/gradle-wrapper.jar"
else
  WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
fi

if [ ! -f "$WRAPPER_JAR" ] ; then
  die "ERROR: Could not find the Gradle wrapper jar file: $WRAPPER_JAR"
fi

# Setup the command line.
CLASSPATH="$WRAPPER_JAR"

exec "$JAVA_CMD" $DEFAULT_JVM_OPTS $GRADLE_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
