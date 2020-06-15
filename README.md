# Accenture Re-boarding

## Build and run using docker

```sh
./gradlew build
docker build -t green-fox-academy/re-boarding .
docker run -p 8080:8080 green-fox-academy/re-boarding
```
