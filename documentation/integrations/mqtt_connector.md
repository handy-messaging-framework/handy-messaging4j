---
layout: default
title: MQTT Integration
parent: Supported Messaging Platforms
---

# MQTT Integration

The MQTT Connector enables the framework to interface with MQTT messaging system. The following dependency along with other eclectique messaging libraries imports MQTT connector:

```xml
<dependency>
    <groupId>com.eclectique</groupId>
    <version>1.0-SNAPSHOT</version>
    <artifactId>eclectique-messaging-mqtt-connector</artifactId>
</dependency>
```

The `system` parameter in the configuration profile should be `mqtt`. [Read more on configuration file here](../configuration.html). The supported `producer` and `consumer` properties are given below:

### Producer Properties

| Property | Semantics | DataType
| -------- | --------- | ------------
| mqtt.broker.host | The hostname of the MQTT broker | String
| mqtt.broker.port | The port number of the MQTT broker | Integer
| mqtt.qos | Quality of Service level for message delivery | Integer
| mqtt.broker.username | Username for the MQTT broker (if authentication is required) | String
| mqtt.broker.password | Password for the MQTT broker (if authentication is required) | String
| mqtt.clean_session.flag | Whether to start a clean session each time the client connects to the broker | Boolean
| mqtt.automatic_reconnect.flag | Whether the client should automatically attempt to reconnect to the broker if it loses connection | Boolean
| connection.timeout.sec | The maximum time in seconds to wait for a connection to be established with the broker | Integer

### Consumer Properties

| Property | Semantics | DataType
| -------- | --------- | ------------
| mqtt.broker.host | The hostname of the MQTT broker | String
| mqtt.broker.port | The port number of the MQTT broker | Integer
| mqtt.broker.username | Username for the MQTT broker (if authentication is required) | String
| mqtt.broker.password | Password for the MQTT broker (if authentication is required) | String
| mqtt.clean_session.flag | Whether to start a clean session each time the client connects to the broker | Boolean
| mqtt.automatic_reconnect.flag | Whether the client should automatically attempt to reconnect to the broker if it loses connection | Boolean
| connection.timeout.sec | The maximum time in seconds to wait for a connection to be established with the broker | Integer
| max.messages.per.batch | Mandatory Eclectique-Messaging framework's dispatcher property | Integer
| max.poll.duration.millis | Mandatory Eclectique-Messaging framework's dispatcher property | Integer


A sample configuration file illustrating the producer and consumer properties is given below:

```yaml
eclectique:
 profiles:
  - profileName: mqtt_profile
    system: mqtt
    consumer:
      properties:
        mqtt.broker.host: "localhost"
        mqtt.broker.port: 1883
        mqtt.clean_session.flag: true
        mqtt.automatic_reconnect.flag: true
        connection.timeout.sec: 10
        max.messages.per.batch: 3
        max.poll.duration.millis: 15000
    producer:
      properties:
        mqtt.broker.host: "localhost"
        mqtt.broker.port: 1883
        mqtt.qos: 1
        mqtt.clean_session.flag: true
        mqtt.automatic_reconnect.flag: true
        connection.timeout.sec: 10
```