MAINCLASS=Alpha

SCALALIB="${SCALAHOME}/lib"
SCALALIBS=${SCALALIB}/scala-library.jar:${SCALALIB}/scala-swing.jar:${SCALALIB}/scala-actors.jar

# Path to java6 bin
JAVA6=/opt/java6/jre/bin/java

# Home of scala
SCALAHOME=/usr/share/scala
#SCALAHOME=/usr/local/Cellar/scala/2.10.0/libexec

MAINCLASSNAME=$(shell cat project/${MAINCLASS}Build.scala | grep "id[ \t]*=" | sed "s/.*\"\(.*\)\".*/\1/")
MAINCLASSVERSION=$(shell cat project/${MAINCLASS}Build.scala | grep "version[ \t]*:=" | sed "s/.*\"\(.*\)\".*/\1/")
DISTNAME=${MAINCLASS}-${MAINCLASSVERSION}

SBT=sbt
# $(shell echo "SBT_OPTS=\"-Xss1m -XX:+CMSClassUnloadingEnabled\" sbt -mem 300")


# For packaging
DISTFILES=$(shell git ls-files)

all: help javajar


help:
	@echo "make javajar	: build a java standalone jar into target/${MAINCLASS}.jar"
	@echo "make scalajar	: build a scala jar into target/scala-<version>"
	@echo "make compile	: compile ${MAINCLASS}"
	@echo "make run	: run ${MAINCLASS}"
	@echo "make jdk6-test	: run ${MAINCLASS} on jdk6"
	@echo "make clean	: clean working directory"
	@echo "make package	: package source files"
	@echo ""
	@echo "DO NOT FORGET TO EDIT project/${MAINCLASS}Build.scala and Makefile to SET CORRECT VERSIONS AND PATHS!!"
	@echo ""



.PHONY: compile run clean jdk6-test help package

scalajar:
	${SBT} package

dist/${DISTNAME}.jar:
	${SBT} package
	mkdir -p dist/
	m4 -DSCALAHOME="${SCALAHOME}" -DMAINCLASSNAME="${DISTNAME}" -DMAINCLASSJAR="$$(find target/ -iname "${MAINCLASSNAME}*${MAINCLASSVERSION}*.jar" | head -n 1)"  proguard/${MAINCLASS}.pro.m4 > ${MAINCLASS}.pro
	proguard @${MAINCLASS}.pro

install: dist/${DISTNAME}.jar
	mkdir -p "${DESTDIR}/usr/share/java/${MAINCLASS}"
	cp -riv dist/${DISTNAME}.jar "${DESTDIR}/usr/share/java/${MAINCLASS}/${MAINCLASS}.jar"
	mkdir -p "${DESTDIR}/usr/bin"
	cp Arch/${MAINCLASS} "${DESTDIR}/usr/bin"



javajar: dist/${DISTNAME}.jar

sign: dist/${DISTNAME}.jar
	jarsigner -keystore ${MAINCLASS}.keystore dist/${DISTNAME}.jar ${MYNAME}


compile:
	${SBT} compile

run:
	${SBT} run


clean:
	${SBT} clean
	rm -fr target/ project/target/ dist/ ${MAINCLASS}.pro
	find . -iname "*~" -delete
	
jdk6-test:
	cd target/scala-${SCALAVERSION}/classes ; \
	${JAVA6} -Xbootclasspath/a:"${SCALALIBS}" org.cc.${MAINCLASS}

package:
	rm -fr dist/ 
	mkdir -p dist/${DISTNAME}
	cp --parents -iv ${DISTFILES} dist/${DISTNAME}
	cd dist ;\
	zip -r ${DISTNAME}.zip ${DISTNAME}/
