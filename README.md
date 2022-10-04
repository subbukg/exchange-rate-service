# exchange-rate-service

Service that fetches the exchange rate from external service and uses them for currency conversion calculations.

## Assumptions:
  * considering that external api calls are expensive and to minimize the external calls, the base currency for all the operations in this service is set to EUR ie., `base = EUR`.
   However, this functionality could be altered or extended by setting a base currency other than EUR, if required.
  * while fetching the exchange rate from the external service, the `scale` is set to ``scale=4``, Exchange rate for any single currency / for conversion value it is set to `scale=2`. 
  * caffeine Cache is used in this service. 
  
### Setup

There is no special setup for this project. You just need Java 11 + Maven to run it.

## Running
The application can be started up by entering the following command from within the exchange-rate-service directory

`mvn spring-boot:run`

Alternately, the following command may be executed to build and execute an executable WAR

`mvn clean package`

`java -jar target/exchange-rate-service-0.0.1-SNAPSHOT.war --server.port=8080`

* Once the application is started, can be verified using the below health endpoint, the `status` must be `up`
``http://localhost:8080/actuator/health``

## Swagger specification
``http://localhost:8080/swagger-ui/index.html``

