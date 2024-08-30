---
layout: default
title: Apache Kafka Integration
parent: Supported Messaging Platforms
---

# Apache Kafka Integration

The Apache Kafka Connector enables Eclectique Messaging framework to interface with Apache Kafka messaging system. To use this connector import the following dependency along with the other Eclectique-Messaging4J libraries to the pom file.

```xml
<dependency>
    <groupId>com.eclectique</groupId>
    <version>1.0-SNAPSHOT</version>
    <artifactId>eclectique-messaging-kafka-connector</artifactId>
</dependency>
```

The `system` parameter in the configuration profile should be `kafka`. [Read more on configuration file here](../configuration.html). The `consumer` and `producer` properties in the configuration file should be any valid Apache Kafka properties. More details on these properties can be read [here](https://kafka.apache.org/intro). For the consumer the 2 mandatory properties required by the Eclectique-Messaging4J framework are `max.messages.per.batch` and `max.poll.duration.millis.` 

A sample of the configuration file for Apache Kafka system looks like below:

```yaml
eclectique:
 profiles:
  - profileName: kafka_profile
    system: kafka
    consumer:
      properties:
        bootstrap.servers: localhost:9092
        group.id: test_app
        max.messages.per.batch: 3
        max.poll.duration.millis: 10000
    producer:
      properties:
        bootstrap.servers: localhost:9092
```