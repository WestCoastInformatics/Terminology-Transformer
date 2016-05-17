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

NDC-RXNORM
----------
This reference deployment includes services for performing lookup operations
among RXNORM concept ids (RXCUI), NDCs (National Drug Codes), and SPL Set Ids
(used in FDA Standard Product Labels)

The project contains example code for running sample queries against
the reference deployment API.  Following are some example curl calls.
The reference deployment uses "default" security, so the authorization
token "guest" can be used in all cases.

1. Test looking up NDC information and RXCUI history (with and without history flag)

```
curl -H Authorization:guest https://ndc.terminology.tools/rxnorm/ndc/00143314501?history=true
curl -H Authorization:guest https://ndc.terminology.tools/rxnorm/ndc/0143-3145-01?history=true
curl -H Authorization:guest https://ndc.terminology.tools/rxnorm/ndc/00143314501
curl -H Authorization:guest https://ndc.terminology.tools/rxnorm/ndc/0143-3145-01
```

2. Test looking up RXCUI information and NDC history

```
curl -H Authorization:guest https://ndc.terminology.tools/rxnorm/rxcui/351772?history=true
curl -H Authorization:guest https://ndc.terminology.tools/rxnorm/rxcui/351772
```

3. Test looking up NDC properties from the current RXNORM version.

```
curl -H Authorization:guest https://ndc.terminology.tools/rxnorm/ndc/00143314501/properties
curl -H Authorization:guest https://ndc.terminology.tools/rxnorm/ndc/0143-3145-01/properties
```

4. Test looking up NDC properties list info for an SPL Set Id for the current RXNORM version.

```
curl -H Authorization:guest https://ndc.terminology.tools/rxnorm/spl/8d24bacb-feff-4c6a-b8df-625e1435387a/ndc/properties
```

TODO: batch calls

Examples of how to call the services from a Java client are provided in an 
integration test that cat can be run via Junit.

See integration-tests/src/main/resources/java/com/wci/tt/test/examples/NdcServiceRestExamples


License
-------
See the included LICENSE.txt file.




  