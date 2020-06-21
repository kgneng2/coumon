#!/bin/sh

module=coumon

SERVER_HOME=`pwd`

echo $SERVER_HOME/bin/pid

PIDFILE="$SERVER_HOME/bin/pid/$module.pid"

echo $PIDFILE

if [ -f $PIDFILE ]
then
       PID=`cat $PIDFILE`
else
       echo "there is no pid file [$PIDFILE]"
       echo "pass the stop process"
       exit 0
fi

PSCNT=`ps -fp $PID | grep -e "$module-[0-9]*\.[0-9]*\.[0-9]*-SNAPSHOT\.jar" | wc -l`

echo $PSCNT

if [ $PSCNT -eq 1 ]
then
         echo "kill $PID"
         kill $PID
         sleep 3
         rm $PIDFILE
         echo "stop success"
else
         echo "no process [pid $PID] to stop, pass stop process"
fi