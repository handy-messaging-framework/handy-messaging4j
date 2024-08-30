---
layout: default
title: Google Pub/Sub Integration
parent: Supported Messaging Platforms
---

# Google Pub/Sub Integration

The Google Pub/Sub Connector enables the framework to interface with Google's pub/sub messaging system. The following dependecy along with other eclectique messaging libraries imports Google's pub/sub connector:

```xml
 <dependency>
    <groupId>com.eclectique</groupId>
    <version>1.0-SNAPSHOT</version>
    <artifactId>eclectique-messaging-google-pubsub-connector</artifactId>
</dependency>
```

The `system` parameter in the configuration profile should be `google-pubsub`. The supported `producer` and `consumer` properties are given below:

### Producer Properties

| Property | Semantics | DataType
| -------- | --------- | ------------
| emulator.exec.flag | Tells whether to use local emulator or google cloud's version. If using emulator, need to provide the hostname and port | Boolean
| host.name | Optional field needed if emulator.exec.flag is set to true | String
| host.port | Optional field needed if emulator.exec.flag is set to true | Integer
| project.id | Project ID registered for the pub/sub topic | String

### Consumer Properties

| Property | Semantics | DataType
| -------- | --------- | ------------
| emulator.exec.flag | Tells whether to use local emulator or google cloud's version. If using emulator, need to provide the hostname and port | Boolean
| host.name | Optional field needed if emulator.exec.flag is set to true | String
| host.port | Optional field needed if emulator.exec.flag is set to true | Integer
| project.id | Project ID registered for the pub/sub topic | String
| subscription.id | Subsciber's ID used to uniquely identify the subscriber | String
| max.messages.per.batch | Mandatory Eclectique-Messaging framework's dispatcher property | Integer
| max.poll.duration.millis | Mandatory Eclectique-Messaging framework's dispatcher property | Integer

A sample configuration file illustrating the producer and consumer properties is given below:

```yaml
eclectique:
 profiles:
  - profileName: pubsub_profile
    system: google-pubsub
    consumer:
      properties:
        emulator.exec.flag: true
        host.name: "localhost"
        host.port: 8085
        project.id: "sample-pubsub"
        subscription.id: "test_sub"
        max.messages.per.batch: 3
        max.poll.duration.millis: 15000
    producer:
      properties:
        emulator.exec.flag: true
        host.name: "localhost"
        host.port: 8085
        project.id: "sample-pubsub"
```