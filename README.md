# Scout Installation & Use
*Let Scout be your Preservation Guide*

## Installation Guide

### Requirements

To install you need:

* Linux or MacOS X operative system (tested in Ubuntu LTS 12.04)
* Maven 3
* Apache Tomcat 7.x
* Optional[^1]: Mail Transport Agent (e.g. Postfix) 

### Download

Will be available soon.

### Installing Scout

To install follow these steps:

 1. Download and install [Apache Tomcat 7.x](http://tomcat.apache.org/download-70.cgi)
 2. Configure Apache Tomcat to use more memory. Edit `bin/catalina.sh` and add the following line in the beginning of the file, after the comments:
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
 10. Save [users.ini](users.ini) to /usr/local/scout
 11. Start Apache Tomcat server

[^1]: An external SMTP server can optionally be configured.

## Using SCOUT

To use the tool, open it in your browser, e.g. at [http://localhost:8080/scout-web-v0.2.0/](http://localhost:8080/scout-web-v0.2.0/). Use username 'admin' and password 'admin' to login as an administrator.

To create the PRONOM adaptor

 1. Open the scout-web web application
 2. Go to Administration
 3. Create a new Source Adaptor
 4. Create a new Source called "PRONOM"
 5. Select the new Source Adaptor with the pronom plugin and the instance name "pronom-default"

More instruction on how to use will be here soon.

## Troubleshooting

Problems and workarounds will be here when needed.

## License

Scout is released under [Apache version 2.0 license](LICENSE.txt).

## Support

This tool is supported by the [Open Planets Foundation](http://www.openplanetsfoundation.org). Commercial support is provided by [KEEP SOLUTIONS](http://www.keep.pt).

## Features

### Version 0.2.0

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
