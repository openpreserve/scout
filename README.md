Scout: a preservation watch system
=============================

Scout is a preservation watch system being developed within the [SCAPE project](http://www.scape-project.eu). 

## Install

### Requirements
 - *nix operative system (tested in Ubuntu LTS 12.04)
 - JBoss AS 7.1.1 Final
 - Optional[^1]: Mail Transport Agent (e.g. Postfix) 

### Instructions

 1. Download and install JBoss AS
 2. Optional[^1]: Install a mail transport agent (e.g. `$ sudo apt-get install postfix`)
 3. Configure JBoss AS to deactivate Resteasy by removing the following lines from `JBOSS/standalone/configuration/standalone.xml`
<pre>
    \<extension module="org.jboss.as.jaxrs"/>
    \<subsystem xmlns="urn:jboss:domain:jaxrs:1.0"/>
</pre>
 4. Install [Scout Web Application](https://github.com/downloads/openplanets/scout/scout-web-0.1.0.war) into JBoss AS
 5. Create directory `/usr/local/scout/data` with write permissions by the user running the JBoss AS server
 6. Create directory `/usr/local/scout/plugins/adaptors` and copy all adaptor jars there (e.g. [C3PO adaptor plugin](https://github.com/downloads/openplanets/scout/c3po-adaptor-0.0.5-jar-with-dependencies.jar) and [PRONOM adaptor plugin](https://github.com/downloads/openplanets/scout/pronom-adaptor-0.0.6-jar-with-dependencies.jar))
 7. Create directory `/usr/local/scout/plugins/notifications` and copy all notification jars there (e.g. [Email notification plugin](https://github.com/downloads/openplanets/scout/email-notification-0.0.3-jar-with-dependencies.jar))
 8. Create directory `.scout` in the home of the user running JBoss AS server
 9. Start JBoss AS server

[^1]: An external SMTP server can optionally be configured.

### Create the PRONOM adaptor

 1. Open the scout-web web application
 2. Go to Administration
 3. Create a new Source Adaptor
 4. Create a new Source called "PRONOM"
 5. Select the new Source Adaptor with the pronom plugin and the instance name "pronom-default"

*****

## Development

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

