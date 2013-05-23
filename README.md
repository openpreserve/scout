Scout: a preservation watch system
=============================

Scout is a preservation watch system being developed within the [SCAPE project](http://www.scape-project.eu). It provides an ontological knowledge base to centralize all necessary information to detect preservation risks and opportunities. It uses plugins to allow easy integration of new sources of information, as file format registries, tools for characterization, migration and quality assurance, policies, human knowledge and others. The knowledge base can be easily browsed and triggers can be installed to automatically notify users of new risks and opportunities. Examples of such notification could be: content fails to conform to defined policies, a format became obsolete or new tools able to render your
content are available.

## Features (version 0.1.0 - alpha)
- Ontologic knowledge base backend
- Information sources:
 - Content characterization profile (via C3PO adaptor)
 - File format information (via PRONOM adaptor)
 - Institutional policy information (using a policy model)
- Web and REST interface with:
 - Browsing of knowledge base
 - Advanced query with SPARQL
 - Simple query with query templates
 - Create triggers and be notified
- Notifications:
 - Email

## Roadmap (version 1.0.0 - final)
* Information sources:
 * Repository events (e.g. ingest, access, preservation action)
 * Web content renderability analisys
 * Tools catalogues (via myExperiment)
 * External assessment via Plato
 * Simulation of repository resources
* Web and REST interface with:
  * User support
  * Form for manual insertion of knowledge
  * Notification history
* Notifications:
  * HTTP push API

## License

Scout is released under [Apache version 2.0 license](LICENSE.txt).

*****

## Install

### Requirements
 - *nix operative system (tested in Ubuntu LTS 12.04)
 - Maven 3
 - Apache Tomcat 7.x
 - Optional[^1]: Mail Transport Agent (e.g. Postfix) 

### Instructions

 1. Download and install [Apache Tomcat 7.x](http://tomcat.apache.org/download-70.cgi)
 2. Configure Apache Tomcat to use more memory. Edit `bin/catalina.sh` and add the following line in the beggining of the file, after the comments:
```
JAVA_OPTS="-Xmx512m -Xms128m $JAVA_OPTS"
```
 3. Optional[^1]: Install a mail transport agent (e.g. `$ sudo apt-get install postfix`)
 4. Download and uncompress the [last stable version of Scout sources](https://github.com/openplanets/scout/tags)
 5. Go to the Scout sources folder and compile with:
 
 ```
 $ cd [SOURCES]
 $ mvn clean install
 ```
 
 6. Install Scout Web Application into Apache Tomcat
```
$ rm -rf [TOMCAT]/webapps/ROOT
$ cp [SOURCES]/web/target/scout-web-0.1.0.war [TOMCAT]/webapps/ROOT.war
```
 7. Create the following directories with write permissions by the user running the Apache Tomcat server
 ```
 $ sudo mkdir /usr/local/scout
 $ sudo chown [TOMCAT_USER] /usr/local/scout
 $ sudo su [TOMCAT_USER]
 $ mkdir /usr/local/scout/data
 $ mkdir /usr/local/scout/plugins
 $ mkdir /usr/local/scout/plugins/adaptors
 $ mkdir /usr/local/scout/plugins/notifications
 $ mkdir ~/.scout 
 ```
 8. Copy available adaptor plugins
```
 $ find [SOURCES]/adaptors/ -name *-jar-with-dependencies.jar -exec cp -v {} /usr/local/scout/plugins/adaptors/ \;
```
 9. Copy available notification plugins
```
 $ find [SOURCES]/notifications/ -name *-jar-with-dependencies.jar -exec cp -v {} /usr/local/scout/plugins/notifications/ \;
```
 10. Start Apache Tomcat server

[^1]: An external SMTP server can optionally be configured.

## Configure

### Create the PRONOM adaptor

 1. Open the scout-web web application
 2. Go to Administration
 3. Create a new Source Adaptor
 4. Create a new Source called "PRONOM"
 5. Select the new Source Adaptor with the pronom plugin and the instance name "pronom-default"

*****

## Develop

[![Build Status](https://travis-ci.org/openplanets/scout.png)](https://travis-ci.org/openplanets/scout)

### Requirements
 - Eclipse Indigo: http://www.eclipse.org/downloads/index-developer.php
 - Eclipse checkstyle plugin: http://marketplace.eclipse.org/node/150
 - Eclipse m2eclipse plugin: http://marketplace.eclipse.org/content/maven-integration-eclipse
 - Maven 3: http://maven.apache.org/
 - clone this repo (if you haven't)

### Setup IDE
After you install eclipse and clone the repo, install the following
plugins listed above. To install a plugin click on Help > Eclipse Market Place
and search them or just use the nice drag and drop feature and drag them from the links above.

As soon as you are ready import the maven modules by selecting File > Import > Maven > Existing Maven Projects.
Maven will fetch the whole internet (this is normal) and will import the projects for you.

If you are planning to contribute please setup the provided eclipse_formatter, cleanup_profile and checkstyle config file
(in the build-tools-config project). To do this for all projects in this eclipse instance follow these steps:

Select Window > Preferences. In the new window select Java > Code Style > Clean Up
and import the cleanup_profile.xml (mentioned above). Do the same for the Formatter in
Java > Code Style > Formatter 

For Checkstyle open again the preferences window and select Checkstyle. Click on the New button and select
Project Relative Configuration. Afterwards give a name and browse to the checkstyle.xml file provided in the build-tools-config
maven module. At the end select this to be the default checkstyle config.

To activate checkstyle for a certain project just right click on it select checkstyle > activate checkstyle.
This will continouosly check the code as you type and mark the bad spots with yellow and will provide
warnings.

Acknowledgements
----------------

Part of this work was supported by the European Union in the 7th Framework Program, IST, through the SCAPE project, Contract 270137.

