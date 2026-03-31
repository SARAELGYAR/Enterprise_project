@echo off

set DIRNAME=%~dp0
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

set JAVA_EXE=java
set DEFAULT_JVM_OPTS=-Xmx64m
set JVM_OPTS=%DEFAULT_JVM_OPTS%

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

:run
"%JAVA_EXE%" %JVM_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
if ERRORLEVEL 1 goto error
goto end

:error
echo Gradle build failed.
exit /b 1

:end
