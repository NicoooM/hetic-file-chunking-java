#!/bin/bash

echo "Cleaning, compiling, packaging, and running the Java project..."
mvn clean compile package exec:java -Dexec.mainClass="com.hetic.App"