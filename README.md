# Quarkus-with-Kafka Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Using Docker
Before you running this application run in your terminal the command to up the docker images.
```shell script
docker-compose up -d
```
Into docker-compose.yaml file has what do you need to use this application, like:
- Kafka
- Zookeeper
- Kafdrop
- Mongodb

**Apache Kafka** is a popular open-source distributed event streaming platform. It is used commonly for high-performance data pipelines, streaming analytics, data integration, and mission-critical applications. Similar to a message queue, or an enterprise messaging platform

**Zookeeper** is a centralized service for maintaining configuration information, naming, providing distributed synchronization, and providing group services

**Kafdrop** is a web UI for viewing Kafka topics and browsing consumer groups. The tool displays information such as brokers, topics, partitions, consumers, and lets you view messages.

**Mongodb** is a document database with the scalability and flexibility that you want with the querying and indexing that you need

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.