#!/bin/bash
clean:
	mvn clean

test:
	mvn test

install: clean
	mvn install

package:
	@rm salesman.jar
	mvn clean compile assembly:single
	@cp target/salesman-java*.jar salesman.jar
