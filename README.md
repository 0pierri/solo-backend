## **Setup**

Import pom.xml and install dependencies.  
Requires JDK11 or newer.  
Requires a Redis server running on port 6379 (default).

## **Running**

Start as Spring Boot application.  
Runs on port 8088. Uses in-memory H2 by default.  
Configure in `src/main/resources/application.properties`.

## **Testing**

Start the application with an empty database for correct population with test data.  
Run `backend_tests.postman_collection.json` in Postman.  
Tests in `src/test/java` can be run independently.
  
