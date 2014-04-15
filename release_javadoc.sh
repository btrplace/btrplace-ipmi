#!/bin/sh

function getVersion {
    mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v "\[INFO\]"
}

if [ $# -ne 1 ]; then
	echo "Usage: $0 output_dir"
	exit 1
fi

WWW=$1
VERSION=$(getVersion)
ARTIFACT_ID="actuator-ipmi"
echo "Publishing javadoc for version $VERSION into ${WWW}"
mkdir -p ${WWW}

mvn javadoc:javadoc > /dev/null
mvn javadoc:jar > /dev/null
mkdir -p ${WWW}/${ARTIFACT_ID} > /dev/null
rm -rf ${WWW}/${ARTIFACT_ID}/${VERSION}
mv target/site/apidocs ${WWW}/${ARTIFACT_ID}/${VERSION}
mv target/${ARTIFACT_ID}-${VERSION}-javadoc.jar ${WWW}/${ARTIFACT_ID}/
rm -rf ${WWW}/${ARTIFACT_ID}/apidocs
ln -s ${WWW}/${ARTIFACT_ID}/${VERSION} ${WWW}/${ARTIFACT_ID}/apidocs
