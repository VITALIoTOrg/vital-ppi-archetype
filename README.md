# vital-ppi-archetype

* Author: Lorenzo Bracco
* Summary: This is a Maven archetype to generate template projects to create Java based and WildFly deployable PPIs
* Target Project: VITAL (<http://vital-iot.eu>)
* Source: <http://gitlab.atosresearch.eu/vital-iot/vital-ppi-archetype.git>

## System requirements

For this project you need:

* Git (<https://git-scm.com>)
* Maven (<https://maven.apache.org>)

Follow installation instructions of Git and Maven.

## Configure, Build and Deploy the CityBikes PPI

1. Checkout the code from the repository:

        git clone http://gitlab.atosresearch.eu/vital-iot/vital-ppi-archetype.git

2. Open a command line and navigate to the root directory of the project.
3. Type this command to build the archetype and create the distributable JAR artifact:

        mvn package

4. Type this command to locally install the archetype:

        mvn install

5. Type this command to create a new project from the archetype:

        mvn archetype:generate \
          -DarchetypeGroupId=eu.vital-iot \
          -DarchetypeArtifactId=vital-ppi-archetype \
          -DarchetypeVersion=1.0 \
          -DgroupId=eu.vital.ppi \
          -DartifactId=<my-artifactId>

