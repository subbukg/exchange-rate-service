# exchange-rate-service

Service that fetches the exchange rate from external service and uses them for currency conversion calculations.

* Assumptions:
  * considering that external api calls are expensive, most of the computations are made within this service, however this could also be externalized i.e., parameter beings et for the external exchange rate service and appropriate data could be directly fetched.   