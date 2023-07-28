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

I have attached screenshots of test results at the bottom of this file & also in root path of the project, the file name is 
"testing-screenshots.docx"



## Http Status


200 OK: CREATED - New resource has been created
400 Bad Request: BAD_REQUEST - The request was invalid or cannot be served
404 Not Found: NOT_FOUND - There is no resource behind the URL
500 Internal Server Error: The server encountered an unexpected condition


## Test Scenarios

1. If maxPostedDate is smaller than minPostedDate.
![image](https://github.com/mohilm/product-catalog-ms/assets/13063336/11ed7232-dcb7-4d88-837a-2cefdc807f7b)

2. If minPrice and maxPrice are passed with same value.
![image](https://github.com/mohilm/product-catalog-ms/assets/13063336/4be0276e-c33a-479a-837c-abb0f663efa1)


3. If maxPrice is smaller than minPrice.
![image](https://github.com/mohilm/product-catalog-ms/assets/13063336/9d5d9698-d482-495c-9b7f-9bf1e1086475)


4. If incorrect date format is passed.
   ![image](https://github.com/mohilm/product-catalog-ms/assets/13063336/7d6e77dc-bbab-4833-b565-7660fd3ceb7d)

5. Product created Successfully.
![image](https://github.com/mohilm/product-catalog-ms/assets/13063336/42b28e34-385c-4efe-8177-ddf71274c790)

6. Product added to approval queue if value is gereater than $5000.
![image](https://github.com/mohilm/product-catalog-ms/assets/13063336/5ddc35d8-a3aa-4aab-b355-24b852d4cf53)

7. If product value exceeds $10,000.
 ![image](https://github.com/mohilm/product-catalog-ms/assets/13063336/7c0c43d9-5432-4faa-ab6c-aeddf08f9cda)

8. Product added to approval queue if value is gereater than %50 of it's original value
 ![image](https://github.com/mohilm/product-catalog-ms/assets/13063336/176ac2e4-9edf-4bf3-ab50-d30e3c24bba9)

9. Product updated successfully.
 ![image](https://github.com/mohilm/product-catalog-ms/assets/13063336/f2ae5d1f-e0ae-4a6f-baab-f684930e8bdc)

10. Update product failed as price is greatee than $10,000.
   ![image](https://github.com/mohilm/product-catalog-ms/assets/13063336/7d84312d-e180-4a1b-8505-5d92f7851346)

11. List all products in approval queue
     ![image](https://github.com/mohilm/product-catalog-ms/assets/13063336/5e996fdb-ccab-4177-8d0f-4089486cf511)

12. Product Approved Succesully from Approval Queue
    ![image](https://github.com/mohilm/product-catalog-ms/assets/13063336/4c12b6ef-c5a0-4edc-89c3-10303613b9a3)

13. Product Rejected from Approval Queue
     ![image](https://github.com/mohilm/product-catalog-ms/assets/13063336/7b2365c2-72ce-4260-ae45-47aaeb25ef2d)










 

