@ECHO off
SET JAVA_HOME=C:\Program Files\Java\jdk1.8.0_101
SET CURRENT_DIR=%cd%
JAVA -Djsse.enableSNIExtension=false -Xmx1024m -cp %CURRENT_DIR%/*;"%JAVA_HOME%/jre/lib/ext/*" com.dev.main.LabelMessageValidator
PAUSE