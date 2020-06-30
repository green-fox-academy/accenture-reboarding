# Accenture Re-boarding project

## Deployed version

- Eureka Discovery server: http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8761/
- Image recognition service: http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8081/
- Reservation service: http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8082/
- Office service: http://ec2-18-195-13-220.eu-central-1.compute.amazonaws.com:8083/

## Configuration

The image processing service uploads to AWS S3 bucket, you need to provide credentials as environment variables:

- `AWS_ACCESS` - AWS S3 access key
- `AWS_SECRET` - AWS S3 secret key
- `AWS_BUCKET` - name of the S3 bucket to work with

## Build and run using docker-compose

```sh
docker-compose up
```

# Accenture Re-boarding

Our reboarding system consists of 3 microservices and an Eureka discovery server
- reservation
- office-service
- image-recognition

## Image-recognition
This microservice is responsible for all operations with images.

### Adding new templates for template matching
**POST /template**
Example request:
```
{
  "url": "https://raw.githubusercontent.com/green-fox-academy/accenture-reboarding/master/image-recognition/examples/template4.jpg"
}
```
Example response:
```
{
    "templateId": "1.jpg"
}
```
**Functionality**
* stores the given template for further template matching

### Matching patterns on image
**POST /layout**
Example request:
```
{
  "layoutUrl": "https://raw.githubusercontent.com/green-fox-academy/accenture-reboarding/master/image-recognition/examples/layout.jpg",
  "templateId": "1.jpg"
}
```
Example response:
```
{
    "matches": [
        {
            "x": 850.0,
            "y": 188.0
        },
        {
            "x": 767.0,
            "y": 188.0
        },
        {
            "x": 767.0,
            "y": 91.0
        }
    ],
    "layoutId": "2.jpg"
}
```
**Functionality**
* matches given pattern on the given image
* creates an image marked all the matches with a rectangle
* returns the position of the pattern matches and the id of the created layout image
* we used this service to match seats on an office layout image

### Marking positions on an image
**PUT/layout**
Example request:
```
{
  "layoutId": "1.jpg",
  "free": [
    {"x":100.0,"y":100.0},{"x":150.0,"y":100.0}
  ],
  "reserved": [
    {"x":729.0,"y":394.0},{"x":1130.0,"y":168.0}
  ],
  "inUse": [
    {"x":200.0,"y":200.0},{"x":250.0,"y":200.0}
  ]
}
```
Example response:
```
{
    "url": "https://accenture-reboarding.s3.eu-central-1.amazonaws.com/66560b16-3db7-48c1-8afa-86cc585ef76e.jpg"
}
```
**Functionality**
* marks the given positions on the given layout with different colors based on the category
* stores the created image in AWS S3
* returns the url of the created image
* we used this service to mark the image of the reserved seat for the employee and also to create the image for the HR to see the status of an office


## Office-service
This microservice is responsible for all operations purely with offices (without reservations).

### Register new office to the system
**POST /office**
Example request:
```
{
  "id": "A66",
  "layoutUrl": "https://raw.githubusercontent.com/green-fox-academy/accenture-reboarding/master/image-recognition/examples/layout.jpg"
}
```
Example response:
```
{
    "id": "A66",
    "layoutId": "1.jpg",
    "seats": [
        {
            "id": 1,
            "position": {
                "x": 149.0,
                "y": 100.0
            },
            "status": "NOT_AVAILABLE",
            "message": "TOO CLOSE"
        }
    ]
}
```
**Functionality**
* registers a new office with a given layout image
* calls the image service to find seats on the given layout
* registers all the seats to the office with FREE statuses

### Get office info
**GET /office/{officeId}**
Example request:
```
http://localhost:8083/office/A66
```
Example response:
```
{
    "id": "A66",
    "layoutId": "1.jpg",
    "seats": [
        {
            "id": 1,
            "position": {
                "x": 850.0,
                "y": 188.0
            },
            "status": "FREE",
            "message": null
        }
    ]
}
```
**Functionality**
* get all info from the given office


## Reservation
This microservice is responsible for all reservation operations.

### Reserve a seat in an office
**POST /entry**
Example request:
```
{
  "userId": "chuck",
  "officeId": "A66",
  "day": "2020-06-29"
}
``
Example response:
```
{
    "userId": "kond",
    "officeId": "A66",
    "seatId": 1,
    "seatLayoutUrl": "https://accenture-reboarding.s3.eu-central-1.amazonaws.com/66560b16-3db7-48c1-8afa-86cc585ef76e.jpg",
    "status": "ACCEPTED",
    "day": "2020-06-27",
    "enteredAt": null,
    "leftAt": null,
    "waitListPosition": 0
}
```
**Functionality**
* send reservation request with userId to the given office
* if there's enough FREE seat in the office, it grants you an access
* if not, it queues you to the waitlist

### Get reservation info
**GET /entry/{userId}**
Example request:
```
GET http://localhost:8082/entry/kond
```
Example response:
```
{
    "userId": "kond",
    "officeId": "A66",
    "seatId": 1,
    "seatLayoutUrl": "https://accenture-reboarding.s3.eu-central-1.amazonaws.com/66560b16-3db7-48c1-8afa-86cc585ef76e.jpg",
    "status": "ACCEPTED",
    "day": "2020-06-27",
    "enteredAt": null,
    "leftAt": null,
    "waitListPosition": 0
}
```
**Functionality**
* provides information of a user's reservation status

### Enter to an office
**PUT /entry/{userId}/enter**
Example request:
```
PUT http://localhost:8082/entry/kond/enter
```
Example response:
```
{
    "userId": "kond",
    "officeId": "A66",
    "seatId": 1,
    "seatLayoutUrl": "https://accenture-reboarding.s3.eu-central-1.amazonaws.com/66560b16-3db7-48c1-8afa-86cc585ef76e.jpg",
    "status": "ACCEPTED",
    "day": "2020-06-27",
    "enteredAt": "2020-06-27T15:01:11.590698",
    "leftAt": null,
    "waitListPosition": 0
}
```
**Functionality**
* if a user has access, they can enter to the reserved office
* if the user is on the VIP list, they can enter without reservation
* if the user doesn't have access, the system doesn't let them in

### Leave an office
**PUT /entry/{userId}/leave**
Example request:
```
PUT http://localhost:8082/entry/kond/leave
```
Example response:
```
{
    "userId": "kond",
    "officeId": "A66",
    "seatId": 1,
    "seatLayoutUrl": "https://accenture-reboarding.s3.eu-central-1.amazonaws.com/66560b16-3db7-48c1-8afa-86cc585ef76e.jpg",
    "status": "ACCEPTED",
    "day": "2020-06-27",
    "enteredAt": "2020-06-27T15:01:11.590698",
    "leftAt": "2020-06-27T15:02:02.903072",
    "waitListPosition": 0
}
```
**Functionality**
* the user has access leave the office

### Get visual reservation status of an office
**GET /office/{officeId}**
Example request:
```
GET http://localhost:8082/office/A66
```
Example response:
```
{
    "url": "https://accenture-reboarding.s3.eu-central-1.amazonaws.com/66560b16-3db7-48c1-8afa-86cc585ef76e.jpg"
}
```
**Functionality**
* generate an image marked the seats with different colors based on it statuses (FREE / RESERVED / IN_USE)

### Manually refresh office information
**PUT /office/{officeId}**
Example request:
```
PUT http://localhost:8082/office/A66
```
Example response:
```
{
    "id": "A66",
    "layoutId": "1.jpg",
    "seats": [
        {
            "id": 1,
            "position": {
                "x": 850.0,
                "y": 188.0
            },
            "status": "FREE",
            "message": null
        },
    ]
}
```
**Functionality**
* fetches fresh information from office service
* if there are new available seats, it registers those for reservation
* if existing seats get removed, it removes those from reservation


## Configuration

The image processing service uploads to AWS S3 bucket, you need to provide credentials as environment variables:

- `AWS_ACCESS` - AWS S3 access key
- `AWS_SECRET` - AWS S3 secret key
- `AWS_BUCKET` - name of the S3 bucket to work with

## Build and run using docker-compose

```sh
docker-compose up
```


## Example HTTP requests:

- [Reservation service](reservation/test.http)
- [Image processing service](image-recognition/test.http)
