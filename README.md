WCI Terminology Transformer
===========================

"Transfomer" is a tooling platform that has bene developed to enable users to bridge the gap between their existing data and the capabilities that could be gained by integrating it with standard, structured terminologies or ontologies.  

It is a terminology-enabled analytical engine designed to take as input data that is unstructured, semi-structured, or fully structured (as an information model) and run that data through normalization, processing, and converter steps which make use of loaded terminologies, information models, and other metadata to produce a result that answers an important question about the input data.

This project hosts reference implementations of APIs and user interfaces.

For more information, see:
* http://www.westcoastinformatics.com/transformer.html

For examples of reference deployments, see:
* https://ndc.terminology.tools

Project Structure
-----------------

* top-level: aggregator for sub-modules (alphabetically):
  * admin: admin tools as maven plugins and poms
  * config: sample config files and data for windows dev environment and the reference deployment.
  * integration-test: integration tests (JPA, REST, and mojo)
  * jpa-model: a JPA enabled implementation of "model"
  * jpa-services: a JPA enabled implementation of "services"
  * model: interfaces representing the RF2 domain model
  * parent: parent project for managing dependency versions.
  * rest: the REST service implementation
  * rest-client: a Java client for the REST services
  * services: interfaces representing the service APIs

Documentation
-------------
Developer documentation we have is here: 
* http://wiki.terminology.tools/confluence/display/TER (requires account for now)


License
-------
See the included LICENSE.txt file.




  