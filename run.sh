#!/bin/sh
rm -rf out
mkdir -p out
javac -classpath {JAVA_8_HOME}/jre/lib/rt.jar:./out src/me/coconan/agileaccount/Fund.java -d out
javac -classpath {JAVA_8_HOME}/jre/lib/rt.jar:./out src/me/coconan/agileaccount/FundStore.java -d out
javac -classpath {JAVA_8_HOME}/jre/lib/rt.jar:./out src/me/coconan/agileaccount/Operation.java -d out
javac -classpath {JAVA_8_HOME}/jre/lib/rt.jar:./out src/me/coconan/agileaccount/Asset.java -d out
javac -classpath {JAVA_8_HOME}/jre/lib/rt.jar:./out src/me/coconan/agileaccount/Account.java -d out
javac -classpath {JAVA_8_HOME}/jre/lib/rt.jar:./out src/me/coconan/agileaccount/Application.java -d out
java -classpath {JAVA_8_HOME}/jre/lib/rt.jar:./out me.coconan.agileaccount.Application ~/enduring-patience/investment/assets.md ~/enduring-patience/investment/events ~/enduring-patience/investment/funds.md
