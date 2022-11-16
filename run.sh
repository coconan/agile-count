#!/bin/bash
mvn clean install
mvn package
java -jar target/agile-account-0.0.1-SNAPSHOT-jar-with-dependencies.jar ~/enduring-patience/investment/assets.md ~/enduring-patience/investment/events ~/enduring-patience/investment/funds.md
