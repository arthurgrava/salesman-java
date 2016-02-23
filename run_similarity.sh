#!/bin/bash
export SALESMAN_DEBUG='true'

JARPATH=$1
AUTHORS=$2
TARGET=$3
THREADS=$4
TOPK=$5
INI=$6
END=$7

JAVA_OPTS='-Xmx6144m'

java $JAVA_OPTS -jar $JARPATH similarity $AUTHORS $TARGET 1 $THREADS $TOPK $INI $END
