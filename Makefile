#!/bin/bash
clean:
	mvn clean

test:
	mvn test

install: clean
	mvn install

package: clean
	mvn compile assembly:single