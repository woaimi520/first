#!/bin/sh

java -cp /tmp/setup:/tmp/impl \
     -Djava.security.policy=/tmp/policy/setup.policy \
     -Djava.rmi.server.codebase=file:/tmp/impl/ \
     -Dexamples.activation.setup.codebase=file:/tmp/setup/ \
     -Dexamples.activation.impl.codebase=file:/tmp/impl/ \
     -Dexamples.activation.file=/tmp/count \
     -Dexamples.activation.policy=/tmp/policy/group.policy \
     -Dexamples.activation.name=examples.activation.Counter \
     examples.activation.Setup examples.activation.CounterImpl
