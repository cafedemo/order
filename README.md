# Order Application

# Overview
This application is a test application that helps testing connection to varioaus databases using spring boot and docker. When developing on local, this application works on docker profiles and spring profiles to provide access to intended database. When deployed on server, same configuration will be handled using environment vairables.

# To run locally

## MongoDB

Go to application folder and run docker compose as

```powershell
docker compose --profile mongodb --profile ops up -d
```

This will start application that connects only to mongodb and starts operational containers like prometheus, grafana, tempo and loki.

To stop docker, run command

```powershell
docker compose --profile mongodb --profile ops stop;docker compose --profile mongodb --profile ops rm -v -f
```

## Kafka

Go to application folder and run docker compose as

```powershell
docker compose --profile kafka --profile ops up -d
```

This will start application that connects only to kafka and starts operational containers like prometheus, grafana, tempo and loki.

To stop docker, run command

```powershell
docker compose --profile kafka --profile ops stop;docker compose --profile kafka --profile ops rm -v -f
```

## Application

Go to application folder

To run as mongo DB

```powershell
gradle clean bootRun --args="--spring.profiles.active=mongodb --mongodb.host=localhost:27017"
```

To run as kafka 

```powershell
gradle clean bootRun --args="--spring.profiles.active=kafka --kafka.bootstrap-servers=localhost:9092,localhost:9093,localhost:9094"
```

