# Accenture Re-boarding project

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
