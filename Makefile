#!/bin/bash
clean:
	mvn clean

test:
	mvn test

install: clean
	mvn install

package: clean
	mvn compile assembly:single
	@cp target/salesman-java*.jar salesman.jar
