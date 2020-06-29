# Accenture Re-boarding Image Processing servicce

Image processing service that handles the layout image to identify possible areas for working and creating images to display which station is available. Marked images are uploaded to AWS S3 bucket. Credentails (access_key, secret_key and a bucket name) needs to be provided through environment vars.

## Build and run using docker

```sh
docker build -t green-fox-academy/reboarding-image .
docker run -p 8081:8081 -e AWS_ACCESS -e AWS_SECRET -e AWS_BUCKET green-fox-academy/reboarding-image
```

## Running test HTTP requests from VS Code

- Install [VS Code](https://code.visualstudio.com/)
- Install [REST Client extension](https://marketplace.visualstudio.com/items?itemName=humao.rest-client)
- Open [test.http](test.http) file and run the examples

