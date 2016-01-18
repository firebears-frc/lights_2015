#!/bin/sh

# Execute a java program as a Daemon

PROG_HOME=/home/pi
PROG_NAME=lights_2015
PROG_CLASS=org.firebears.lights.LightsMain
PROG_OPTS="-Dverbose=true -Dfadecandy.server=raspberrypi.local -Dnetwork_table.server=NONE"
CP="${PROG_HOME}/${PROG_NAME}.jar:${PROG_HOME}/NetworkTables.jar"

if [ -f ${PROG_HOME}/${PROG_NAME}.jar ] ; then
    /usr/bin/java  ${PROG_OPTS} -cp ${CP} ${PROG_CLASS}
else
    echo "No program at ${PROG_HOME}/${PROG_NAME}.jar"
    # Wait for 10 minutes
    sleep 600
fi
