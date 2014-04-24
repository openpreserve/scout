# Contribute to Scout
*Let Scout be your Preservation Guide*

## Develop

[![Build Status](https://travis-ci.org/openplanets/scout.png)](https://travis-ci.org/openplanets/scout)

### Requirements

To build you require:

 * Maven 3: http://maven.apache.org/
 * clone this repo (if you haven't)

For using the recommended IDE you require:

 * Eclipse Indigo: http://www.eclipse.org/downloads/index-developer.php
 * Eclipse checkstyle plugin: http://marketplace.eclipse.org/node/150
 * Eclipse m2eclipse plugin: http://marketplace.eclipse.org/content/maven-integration-eclipse

### Setup IDE

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

### Build

To compile go to the sources folder and execute the command:

```bash
$ mvn clean install
```

After successful compile the binary will be available at `/web/target/scout-web-*.war`.

### Deploy

To deploy to Tomcat, please see instalation instructions above.

## Contribute

1. [Fork the GitHub project](https://help.github.com/articles/fork-a-repo)
2. Change the code and push into the forked project
3. [Submit a pull request](https://help.github.com/articles/using-pull-requests)

To increase the changes of you code being accepted and merged into the official source here's a checklist of things to go over before submitting a contribution. For example:

* Has unit tests (that covers at least 80% of the code)
* Has documentation (at least 80% of public API)
* Agrees to contributor license agreement, certifying that any contributed code is original work and that the copyright is turned over to the project

## Roadmap

* Information sources:
 * Tools catalogues (via myExperiment)
 * External assessment via Plato
 * Simulation of repository resources
* Web and REST interface with:
  * User support
  * Form for manual insertion of knowledge
  * Notification history
* Notifications:
  * HTTP push API

