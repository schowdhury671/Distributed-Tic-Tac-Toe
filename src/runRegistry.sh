#!/bin/bash

javac *.java

rmic TTTClass

rmiregistry &
