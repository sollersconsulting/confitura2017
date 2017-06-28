[![Build Status](https://travis-ci.org/sollersconsulting/confitura2017.svg?branch=master)](https://travis-ci.org/sollersconsulting/confitura2017)

# Snapgram
Yet another images upload

## Commit 10
Long story short
* New entity
* $expand - supported automatically thanks to https://github.com/mat3e/olingo-jpa (should be restricted, can be imrpoved)

Example URLs
* http://localhost:8080/OData.svc/Categories(1)/Images?$expand=User($select=Username)
* http://localhost:8080/OData.svc/Categories(1)?$expand=Images($expand=User($select=Username);$count=true)&$format=json [count ignored]
* http://localhost:8080/OData.svc/Images(2)?$expand=*
* http://localhost:8080/OData.svc/Images?$expand=Category&$format=json
* http://localhost:8080/OData.svc/Categories(1)?$expand=Images($levels=3)&$format=json
* http://localhost:8080/OData.svc/Categories(1)?$expand=*($levels=3)&$format=json

***

### Commit 9
Long story short
* $select - Olingo OoTB
* $count for collections

Example URLs
* http://localhost:8080/OData.svc/Users?$count=true
* http://localhost:8080/OData.svc/Images?$count=true&$format=json
* http://localhost:8080/OData.svc/Images?$count=true&$format=json&$select=Width,Height
* http://localhost:8080/OData.svc/Users('1')?$select=Username

### Commit 8
Long story short
* $value of media entities

Example URLs
* http://localhost:8080/OData.svc/Images(1)/$value
* http://localhost:8080/OData.svc/Users('2')/Images(2)/$value

### Commit 7
Long story short
* Navigation to the collection of entities

Example URLs
* http://localhost:8080/OData.svc/Users('2')/Images

### Commit 6
Long story short
* New entity (User)
* Navigation to single entity
* URL segments limitation (2 entities in the URL)

Example URLs
* http://localhost:8080/OData.svc/Users
* http://localhost:8080/OData.svc/Users('2')/Email/$value
* http://localhost:8080/OData.svc/Images(1)/User
* http://localhost:8080/OData.svc/Users('2')/Images(2)
* http://localhost:8080/OData.svc/Images(1)/User/Images(2) [error]

### Commit 5
Long story short
* PrimitiveProcessor
* PrimitiveValueProcessor

Example URLs
* http://localhost:8080/OData.svc/Images(1)/Description
* http://localhost:8080/OData.svc/Images(1)/IsPrivate
* http://localhost:8080/OData.svc/Images(2)/IsPrivate
* http://localhost:8080/OData.svc/Images(2)/IsPrivate/$value
* http://localhost:8080/OData.svc/Images(1)/Height/$value

### Commit 4
Long story short
* EntityProcessor
* Common part for processors

Example URLs
* http://localhost:8080/OData.svc/Images(1)
* http://localhost:8080/OData.svc/Images(2)?$format=json

### Commit 3
Long story short
* Repositories and the generic mechanism for repositories
* Sample data
* CollectionProcessor

Example URLs
* http://localhost:8080/OData.svc/Images
* http://localhost:8080/OData.svc/Images?$format=json

### Commit 2
Long story short
* First entity
* OData Service Document
* OData $metadata

Example URLs
* http://localhost:8080/OData.svc/
* http://localhost:8080/OData.svc/?$format=json
* http://localhost:8080/OData.svc/$metadata

### Commit 1
Long story short
* Spring Boot + `HttpServlet`
* Minimum Viable Product :)
* Actuator
* H2 console

Example URLs
* http://localhost:8080/ping
* http://localhost:8080/health
* http://localhost:8080/metrics
* http://localhost:8080/h2-console
