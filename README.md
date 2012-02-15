Planning and Watch Repository
=============================
This is the SCAPE repository for the PW sub-project.

Requirements
-----------------------------
 - Eclipse Indigo: http://www.eclipse.org/downloads/index-developer.php
 - Eclipse checkstyle plugin: http://marketplace.eclipse.org/node/150
 - Eclipse m2eclipse plugin: http://marketplace.eclipse.org/content/maven-integration-eclipse
 - Maven 3: http://maven.apache.org/
 - clone this repo (if you haven't)

Setup IDE
-----------------------------
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


Watch Component
---------------
TBD
