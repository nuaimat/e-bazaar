# e-bazaar
e-bazaar software engineering class project

This Project has two presentation components:
  - JavaFX (find it in src/main/java/launch/Start.java)
  - Web App


Prerequisite
  - install docker and docker-compose

To get Started
  - mvn clean verify
  - cd docker-dir
  - docker-compose up
  - open [http://localhost:18080/e-bazaar/](http://localhost:18080/e-bazaar/)

Intellij 2017.1 Note:
  * add docker integration plugin
  * setup docker [follow this tutoral](https://www.jetbrains.com/help/idea/docker.html)
  * add a new build configuration with "docker container", let it point to docker-dir/docker-compose.yml and add a "Before launch" goal
      * Run Maven Goal
      * use "clean verify" in the command line
  * click "Ok" then run it.
