# Bidding web application
Implementacion of Web applications with Java POJO technologies, with particular emphasis on the development of enterprise web application. 

Running Web appplications
---------------------
- subasta-app requires the database server to be running.

+ Running with Maven/Jetty.
    * Below it is indicated how to run subasta-app. Rest of Web applications are run in a similar way.
      cd subasta-app
      mvn jetty:run
    * Browse to http://localhost:9090/subasta-app

+ Running with Tomcat.
    * Copy the ".war" file (e.g. pojo-minibank/target/subasta-app.war) to
      Tomcat's "webapp" directory.

    * Start Tomcat:
      cd <<Tomcat home>>/bin
      startup.sh

    * Browse to http://localhost:8080/subasta-app.

    * Shutdown Tomcat:
      shutdown.sh
