# About Scout

Let Scout be your Preservation Guide.

#### What does Scout do?

Scout is a preservation watch system being developed within the [SCAPE project](http://www.scape-project.eu). It provides an ontological knowledge base to centralize all necessary information to detect preservation risks and opportunities. It uses plugins to allow easy integration of new sources of information, as file format registries, tools for characterization, migration and quality assurance, policies, human knowledge and others. The knowledge base can be easily browsed and triggers can be installed to automatically notify users of new risks and opportunities. Examples of such notification could be: content fails to conform to defined policies, a format became obsolete or new tools able to render your content are available.

#### What are the benefits for end user?

Scout brings the following benefits:
* Helps you know when your content is at risk
* Helps you reduce risks by community engagement and assistance
* Discover who else is holding content of a specific type (file format)
* Know whether your repository is the only one using a specific preservation tool

#### Who is intended audience?

Scout is for:

* Content holders
* Preservation experts

# Features and roadmap

#### Version 0.2.0

* Ontologic knowledge base backend
* Information sources:
 * Content characterization profile (via C3PO adaptor)
 * File format information (via PRONOM adaptor)
 * Institutional policy information (using a policy model)
 * Repository events (via Report API adaptor)
 * Web Archive renderability analysis (via C3PO adaptor)
* Web and REST interface with:
 * Browsing of knowledge base
 * Advanced query with SPARQL
 * Simple query with query templates
 * Create triggers and be notified
* Notifications:
 * Email

#### Roadmap

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

# How to install and use

#### Requirements
To install you need:
* *nix operative system (tested in Ubuntu LTS 12.04)
* Maven 3
* Apache Tomcat 7.x
* Optional[^1]: Mail Transport Agent (e.g. Postfix) 

#### Download

| Version | Size   | SHA1                                                    |                      |
|---------|--------|---------------------------------------------------------|----------------------|
| v0.0.1  | 1.2 MB | <small></small> |soon|


#### Install instructions

To install follow these steps:

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
$ cp [SOURCES]/web/target/scout-web-*.war [TOMCAT]/webapps/
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

#### Use

To use the tool, open it in your browser, e.g. at [http://localhost:8080/scout-web-v0.2.0/](http://localhost:8080/scout-web-v0.2.0/)

To create the PRONOM adaptor

 1. Open the scout-web web application
 2. Go to Administration
 3. Create a new Source Adaptor
 4. Create a new Source called "PRONOM"
 5. Select the new Source Adaptor with the pronom plugin and the instance name "pronom-default"

More instruction on how to use will be here soon.

#### Troubleshooting

Problems and workarounds will be here when needed.


# More information

#### Publications

TODO add publications.

#### Licence

Scout is released under [Apache version 2.0 license](LICENSE.txt).

#### Acknowledgements

Part of this work was supported by the European Union in the 7th Framework Program, IST, through the SCAPE project, Contract 270137.

#### Support

This tool is supported by the [Open Planets Foundation](http://www.openplanetsfoundation.org). Commercial support is provided by [KEEP SOLUTIONS](http://www.keep.pt).

# Develop

[![Build Status](https://travis-ci.org/openplanets/scout.png)](https://travis-ci.org/openplanets/scout)

#### Requirements

To build you require:
 * Maven 3: http://maven.apache.org/
 * clone this repo (if you haven't)

For using the recommended IDE you require:
 * Eclipse Indigo: http://www.eclipse.org/downloads/index-developer.php
 * Eclipse checkstyle plugin: http://marketplace.eclipse.org/node/150
 * Eclipse m2eclipse plugin: http://marketplace.eclipse.org/content/maven-integration-eclipse

#### Setup IDE

After you install eclipse and clone the repo, install the following
plugins listed above. To install a plugin click on Help > Eclipse Market Place
and search them or just use the nice drag and drop feature and drag them from the links above.

As soon as you are ready import the maven modules by selecting File > Import > Maven > Existing Maven Projects.
Maven will fetch the whole internet (this is normal) and will import the projects for you.

If you are planning to contribute please setup the provided eclipse\_formatter, cleanup\_profile and checkstyle config file
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

#### Build

To compile go to the sources folder and execute the command:

```bash
$ mvn clean install
```

After successful compile the binary will be available at `/web/target/scout-web-*.war`.

#### Deploy

To deploy to Tomcat, please see instalation instructions above.

#### Contribute

1. [Fork the GitHub project](https://help.github.com/articles/fork-a-repo)
2. Change the code and push into the forked project
3. [Submit a pull request](https://help.github.com/articles/using-pull-requests)

To increase the changes of you code being accepted and merged into the official source here's a checklist of things to go over before submitting a contribution. For example:
* Has unit tests (that covers at least 80% of the code)
* Has documentation (at least 80% of public API)
* Agrees to contributor license agreement, certifying that any contributed code is original work and that the copyright is turned over to the project
