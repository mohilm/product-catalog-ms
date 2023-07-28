# product-catalog-ms
This Microservice tries to achieve the funtionality of a catalog which creates,updates,approves,maintains,rejects and lists products in their system. 

# Technology stack

Java 17
Gradle 
RESTful API
SpringBoot Framework (3.1.2)
Hibernate
JPA
Json
MySQL Database
 
## Build And Run

Used the below gradle command for building the application

gradle clean build


## Database connection

Used MySQL as datasource with Hibernate as Dialect.
The configuration is specified in application.properties,
please update the datasource values like username,password
if you have your own local instance of MySQL server up and running.
 
## Database connection

Used MySQL as datasource with Hibernate as Dialect.
The configuration is specified in application.properties,
please update the datasource values like username,password
if you have your own local instance of MySQL server up and running.

## Available RESTful APIs


product-catalog-ms APIs:

GET- Get all active products
http://localhost:8080/api/v1/products

GET- Get all products based on optional params passed in request params
http://localhost:8080/api/v1/products?productName=?&minPrice=?&maxPrice=?&minPostedDate=?&maxPostedDate=?

POST- create a new product
http://localhost:8080/api/v1/products

PUT- Update Product by Id
http://localhost:8080/api/v1/products/{productId}

DELETE- Delete Product by Id
http://localhost:8080/api/v1/products/{productId}

GET- List of all products that are awaiting approval
http://localhost:8080/api/v1/products/approval-queue/{approvalId}/approve

PUT- Approve a Product
http://localhost:8080/api/v1/products/approval-queue/{approvalId}/approve

PUT- Reject a product
http://localhost:8080/api/v1/products/approval-queue/{approvalId}/reject


## Validations
/search endpoint
  mix price and max price cannot be the same,
  acceptable date format for minPostedDate & maxPostedDate is
  eg: http://localhost:8080/api/products/search?minPrice=0&maxPrice=10&minPostedDate=2023-07-27T23:02:03&maxPostedDate=2023-07-27T23:02:03

I have attached test results of validation in root path of the project, the file name is "testing-screenshots.docx"



## Http Status


200 OK: CREATED - New resource has been created
400 Bad Request: BAD_REQUEST - The request was invalid or cannot be served
404 Not Found: NOT_FOUND - There is no resource behind the URL
500 Internal Server Error: The server encountered an unexpected condition
 

