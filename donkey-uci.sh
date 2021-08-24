#!/bin/sh

touch "donkey$(date).txt"
CLASSPATH="/Users/joshualevine/IdeaProjects/donkeyfactory/DonkeyFactory.jar:/Users/joshualevine/IdeaProjects/donkeyfactory/src/lib/commons-codec-1.6.jar:/Users/joshualevine/IdeaProjects/donkeyfactory/src/lib/commons-logging-1.1.1.jar:/Users/joshualevine/IdeaProjects/donkeyfactory/src/lib/fluent-hc-4.2.3.jar:/Users/joshualevine/IdeaProjects/donkeyfactory/src/lib/httpclient-4.2.3.jar:/Users/joshualevine/IdeaProjects/donkeyfactory/src/lib/httpclient-cache-4.2.3.jar:/Users/joshualevine/IdeaProjects/donkeyfactory/src/lib/httpcore-4.2.2.jar:/Users/joshualevine/IdeaProjects/donkeyfactory/src/lib/httpmime-4.2.3.jar:/Users/joshualevine/IdeaProjects/donkeyfactory/src/lib/twitter4j-async-4.0.7.jar:/Users/joshualevine/IdeaProjects/donkeyfactory/src/lib/twitter4j-core-4.0.7.jar:/Users/joshualevine/IdeaProjects/donkeyfactory/src/lib/twitter4j-examples-4.0.7.jar:/Users/joshualevine/IdeaProjects/donkeyfactory/src/lib/twitter4j-stream-4.0.7.jar;/Users/joshualevine/IdeaProjects/donkeyfactory/hashKeys.dat"
java -cp "$CLASSPATH" -Xms64m -Xmx2024m chess.controller.DonkeyUCI
