# Accenture Re-boarding project

## Deployed version

- Eureka Discovery server: http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8761/
- Image recognition service: http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8081/
- Reservation service: http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8082/
- Office service: http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8083/

## API spec

- Image recognition service: http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8081/swagger-ui.html
- Reservation service: http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8082/swagger-ui.html
- Office service: http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8083/swagger-ui.html

## Configuration

The image processing service uploads to AWS S3 bucket, you need to provide credentials as environment variables:

- `AWS_ACCESS` - AWS S3 access key
- `AWS_SECRET` - AWS S3 secret key
- `AWS_BUCKET` - name of the S3 bucket to work with

## Run using docker-compose

```sh
docker-compose pull
docker-compose up
```

### Build and run your own version

```sh
docker-compose up --build
```

## Configuration

The image processing service uploads to AWS S3 bucket, you need to provide credentials as environment variables:

- `AWS_ACCESS` - AWS S3 access key
- `AWS_SECRET` - AWS S3 secret key
- `AWS_BUCKET` - name of the S3 bucket to work with

## Build and run using docker-compose

```sh
docker-compose up
```

## Functional description

Our reboarding system consists of 3 microservices and an Eureka discovery server
- Reservation service
- Office service
- Image recognition service

### Image recognition service
This microservice is responsible for all operations with images.

#### [POST /template](http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8081/swagger-ui.html#/template-controller/createTemplateUsingPOST) » Adding new templates for template matching
- stores the given template for further template matching

#### [POST /layout](http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8081/swagger-ui.html#/layout-controller/processLayoutUsingPOST) » Matching patterns on image
- matches given pattern on the given image
- creates an image marked all the matches with a rectangle
- returns the position of the pattern matches and the id of the created layout image
- we used this service to match seats on an office layout image

#### [PUT/layout](http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8081/swagger-ui.html#/layout-controller/markLayoutUsingPUT) » Marking positions on an image
- marks the given positions on the given layout with different colors based on the category
- stores the created image on AWS S3
- returns the url of the created image
- we used this service to mark the image of the reserved seat for the employee and also to create the image for the HR to see the status of an office


### Office service
This microservice is responsible for all operations purely with offices (without reservations).

#### [POST /office](http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8083/swagger-ui.html#/office-controller/registerOfficeUsingPOST) » Register new office to the system
- registers a new office with a given layout image
- calls the image service to find seats on the given layout
- registers all the seats to the office with FREE statuses

#### [GET /office/{officeId}](http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8083/swagger-ui.html#/office-controller/modifyOfficeUsingGET) » Get office info
- get all info from the given office


### Reservation service
This microservice is responsible for all reservation operations.

#### [POST /entry](http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8082/swagger-ui.html#/entry-controller/registerUsingPOST) » Reserve a seat in an office
- send reservation request with userId to the given office
- if there's enough FREE seat in the office, it grants you an access
- if not, it queues you to the waitlist

#### [GET /entry/{userId}](http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8082/swagger-ui.html#/entry-controller/statusUsingGET) » Get reservation info
- provides information of a user's reservation status

#### [PUT /entry/{userId}/enter](http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8082/swagger-ui.html#/entry-controller/enterUsingPUT) » Enter to an office
- if a user has access, they can enter to the reserved office
- if the user is on the VIP list, they can enter without reservation
- if the user doesn't have access, the system doesn't let them in
- if the office doesn't exist in the database of the Reservation service, it automatically fetches the office information from the Office service

#### [PUT /entry/{userId}/leave](http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8082/swagger-ui.html#/entry-controller/leaveUsingPUT) » Leave an office
- the user has access leave the office

#### [GET /office/{officeId}](http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8082/swagger-ui.html#/office-controller/getStatusUsingGET) » Get visual reservation status of an office
- generate an image marked the seats with different colors based on it statuses (FREE / RESERVED / IN_USE)

#### [PUT /office/{officeId}](http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8082/swagger-ui.html#/office-controller/updateOfficeUsingPUT) » Manually refresh office information
- fetches fresh information from office service
- if there are new available seats, it registers those for reservation
- if existing seats get removed, it removes those from reservation
