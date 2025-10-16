#!/usr/bin/env sh

DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  LS=`ls -ld "$PRG"`
  LINK=`expr "$LS" : ".*-> \(.*\)$"`
  if expr "$LINK" : ".*/.*" > /dev/null; then
    PRG="$LINK"
  else
    PRG=`dirname "$PRG"`/"$LINK"
  fi
done

APP_HOME=`dirname "$PRG"`

# OS specific support (must be 'true' or 'false').
cygwin=false
darwin=false
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true ;;
esac

if [ "$cygwin" = "true" ]; then
  APP_HOME=`cygpath --unix "$APP_HOME"`
fi

# For Darwin, add options to allow Java to run without a Dock icon.
if [ "$darwin" = "true" ]; then
  JAVA_OPTS="$JAVA_OPTS -Dapple.awt.UIElement=true"
fi

# Determine if we are running on Windows
if [ "$cygwin" = "true" ]; then
  CLASSPATH=`cygpath --path --windows "$APP_HOME/gradle/wrapper/gradle-wrapper.jar"`
else
  CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
fi

# Determine if we are running in a Git Bash environment
if [ -n "$MSYSTEM" ] && [ "$MSYSTEM" = "MINGW64" ]; then
  CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

exec "$JAVA_HOME/bin/java" $JAVA_OPTS $DEFAULT_JVM_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"

