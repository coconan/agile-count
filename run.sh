#!/bin/bash
mvn clean install > /dev/null
mvn package > /dev/null
java -jar target/agile-account-0.1.0-SNAPSHOT-jar-with-dependencies.jar \
    ~/enduring-patience/investment/assets.md \
    ~/enduring-patience/investment/events \
    ~/enduring-patience/investment/funds.md \
    $1 $2 $3 $4
