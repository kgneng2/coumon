#!/bin/sh

module=coumon

SERVER_HOME=`pwd`

LOG_DIR=${SERVER_HOME}/log

mkdir -p LOG_DIR

echo "APPLICATION HOME : $SERVER_HOME"
echo "LOG_DIR : $LOG_DIR"

GC_LOG_PATH=$LOG_DIR/gc.log
HEAP_DUMP_PATH=$LOG_DIR/log

JVM_OPTIONS="-Xms1g -Xmx1g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$HEAP_DUMP_PATH -Xloggc:$GC_LOG_PATH -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+PrintReferenceGC -XX:+PrintGCApplicationStoppedTime -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"

mkdir -p $SERVER_HOME/bin/pid

nohup java ${JVM_OPTIONS} -jar $SERVER_HOME/build/libs/$module-0.0.1-SNAPSHOT.jar >LOG_DIR/application-stdout.log 2>&1 &

PID=$!

echo $PID >$SERVER_HOME/bin/pid/$module.pid
