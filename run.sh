#!/bin/bash

echo "Cleaning, compiling, packaging, and running the Java project..."
mvn clean install compile package exec:java -Dexec.mainClass="com.hetic.App" -Dexec.args="naza .jpeg" -DskipTests