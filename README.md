# Btrplace Ipmi actuators #

Actuators to execute the actions BootNode and ShutdownNode using IPMI

[![Build Status](http://btrp.inria.fr:8080/jenkins/buildStatus/icon?job=DEV actuator-ipmi)](http://btrp.inria.fr:8080/jenkins/job/DEV%20actuator-ipmi/)

## Integration ##

This artifact in in a private repository so you have first to edit your `pom.xml` to declare them:

```xml
<repositories>
    <repository>
        <id>btrp-releases</id>
        <url>http://btrp.inria.fr/repos/releases</url>
    </repository>
    <repository>
        <id>btrp-snapshots</id>
        <url>http://btrp.inria.fr/repos/snapshot-releases</url>
    </repository>
</repositories>
```

Next, just declare the dependency:

```xml
<dependency>
   <groupId>btrplace</groupId>
   <artifactId>actuator-ipmi</artifactId>
   <version>1.1-SNAPSHOT</version>
</dependency>
```

## Building from sources ##

Requirements:
* JDK 7+
* maven 3+

The source of the released versions are directly available in the `Tag` section.
You can also download them using github features.
Once downloaded, move to the source directory then execute the following command
to make the jar:

    $ mvn clean install

If the build succeeded, the resulting jar will be automatically
installed in your local maven repository and available in the `target` sub-folder.

## Documentation ##

* releases: http://btrp.inria.fr/actuator-ipmi/ (`apidocs` always refers to the last release)
* snapshot-releases: http://btrp.inria.fr/actuator-ipmi/apidocs-snapshot

## Usage ##

__ TO COMPLETE __

## Copyright ##
Copyright (c) 2014 Inria. See `LICENSE.txt` for details
