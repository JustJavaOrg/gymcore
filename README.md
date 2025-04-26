# Welcome to GymCore
[![Build](https://github.com/JustJavaOrg/gymcore/actions/workflows/CI.yml/badge.svg?branch=main)](https://github.com/JustJavaOrg/gymcore/actions/workflows/CI.yml)
## _A Just Java Project_ ☕

GymCore is a simple RESTful API for managing gym members and their subscriptions.

## Group Project Basics    
Before you open a PR you must:  
- Ensure all tests are passing ✅ 
- Run the application locally and test its working as expected ✅

### The Stack:
- Java version: 23
    - Can be installed with SDKMan:
      ```
      sdk install java 23.0.2-tem
      ```
- Spring Boot version: 3.4.2
- PostgresDB version: 17.2 
- H2 in-memory DB for tests
- Keycloak (for authentication)
- Docker
- GitHub Actions

---
### Running the application locally

- The first step is to run the DB and Keycloak containers. 
  - Run `docker-compose up` or open the `docker-compose.yml` file and use the UI buttons.
- Next, you need to run the service 
  - Two Run configuration files are included in the project (Intellij Idea only). This should be loaded in the IDE by default.
  One includes the 'dev' profile this profile will add basic test data to the local DB. 
  - If running via the command line use `--spring.profiles.active=dev`

Your application will be available at:

http://localhost:8080

---
### Swagger UI
To access the Swagger UI, in your browser navigate to:

http://localhost:8080/swagger-ui/index.html

From there you will also see a link to the OpenAPI specification in raw JSON format.