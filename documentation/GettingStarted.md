---
layout: default
title: "Getting Started"
nav_order: 2
---

# Getting Started

## Dependencies

The below dependency is the bare minimum one that will be needed with any project. The remaining depends on the type of the project. The below dependency add the core layer of eclectique-messaging4J to your project

```xml
<dependency>
    <artifactId>eclectique-messaging-core</artifactId>
    <groupId>com.eclectique</groupId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Quick Example

Example here illustrates how to use the eclectique-messaging4J framework to send and receive messages from the Apache Kafka messaging system.

### Pre-requisites for the example
- Kafka ver 2.13 running
- Create a topic named `demo_topic` in Kafka

Apart from the dependencies listed above, include the following dependecies to your `pom.xml` file.

```xml
<dependency>
    <groupId>com.eclectique</groupId>
    <version>1.0-SNAPSHOT</version>
    <artifactId>eclectique-messaging-kafka-connector</artifactId>
</dependency>
<dependency>
    <groupId>com.eclectique</groupId>
    <version>1.0-SNAPSHOT</version>
    <artifactId>eclectique-messaging-types-simplemessage</artifactId>
</dependency>
```

The above dependencies add the following capabilities:
- eclectique-messaging's Kafka connector which enables the framework to interface with Kafka
- SimpleMessage - A messaging schema designed on Google's protobuf standard. More details on this later.

### Configuration file
Create a file by the name `eclectique-conf.yml` in the application's `resource` directory

The contents of the `eclectique-conf.yml` file will look as:
```yaml
eclectique:
 profiles:
  - profileName: profile1
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

In the configuration we have defined a profile called `profile1` which uses `kafka` as the messaging system. Its producer and consumer properties are defined below.

Lets take the case of the Producer example now. The producer in a messaging system creates and sends messages through the messaging channel. In our example the producer will be a Kafka producer.

### Sending a message


```java

public class SimpleProducer {
    MessageProducerSystem producerSystem;

    /**
     * Initializes the producer system. Producer is asked to use `profile1` and write to `demo_topic`
     */
    public void SimpleProducer() {
        producerSystem = new MessageProducerSystem("profile1", "demo_topic");
    }

    /**
     * Function sends the message generated using the producer to the Kafka `demo_topic`
     */
    public void sendMsg() {
        SimpleMessage contentMsg = generateMessage();
        this.producerSystem.sendMessage(contentMsg);
        this.producerSystem.close();
        System.out.println("Message sent");
    }

    /**
     * Function generates a message with SimpleMessage schema.
     *
     * @return
     */
    private SimpleMessage generateMessage() {
        SimpleMessage contentMsg = new SimpleMessage();
        contentMsg.setContentSchema(String.class.toString());
        contentMsg.setDateTime(Optional.of(Date.from(Instant.now())));
        contentMsg.setMessageId("msg-1");
        contentMsg.setPayload("Hello, this is a sample message".getBytes());
        contentMsg.setSender("app-1");
        return contentMsg;
    }
}

```

The `producerSystem` uses `profile1` as the profile and writes to `demo_topic`. `profile1` is defined in the `eclectique-conf.yml` to use the Kafka messaging system. 
Run the program and verify that the data lands in the `demo_topic` in Kafka. Use the protobuf compiler to parse the data written to the Kafka topic.

### Consuming message

#### Initialzing the Message Consumer

```java
public class SimpleConsumer {

    MessageConsumingSystem consumingSystem;
    SimpleHandler messageHandler;

    /**
     * Type of the message expected to be received by the consumer.
     * In this case it is of the type SimpleMessage
     */

    String getMessageTypeClass(){
        return "com.eclectique.messaging.types.simplemessage.SimpleMessage";
    }

    /**
     * Initializes and starts the consumer. The consumer is initialized with profile1 and the demo_topic.
     * The consumer will start listening from this point.
     * Once a message is received in the channel, the `messageHandler` is invoked.
     */

    public SimpleConsumer(){
        messageHandler = new SimpleHandler();
        MessageConsumingSystem.getInstance().setupConsumer("profile1",
                "demo_topic",
                getMessageTypeClass(), messageHandler);
    }
}

```

The above class initializes and starts the MessageConsumingSystem. It is asked to use `profile1`. This implies the consumer will connect to Kafka. The kafka topic to which it listens to will be `demo_topic`. 

When a message is received by the consumer it passes it over to the messageHandler defined below:

#### Message Handler

```java
public class SimpleHandler implements MessageHandler {

    /**
     * Function that gets invoked for every message read from the channel
     * @param message - Message received from the channel
     */
    @Override
    public void handleMessage(Message message) {
        SimpleMessage msg = (SimpleMessage) message;
        System.out.println(String.format("Id: %s",msg.getId()));
        System.out.println(String.format("Message payload: %s", new String(msg.getId())));
    }

    /**
     * Function decides if a new instance of the handler is needed for each of the messages received
     * @return optional MessageHandler
     */
    @Override
    public Optional<MessageHandler> getNewInstance() {
        return Optional.empty();
    }
}

```

The `SimpleHandler` class here is an extension of `MessageHandler` which is a pre-requisite for registering a class as a message handler. Whenever a new message is received, the `handleMessage(Message message) function gets invoked`. The `getNewInstance()` function decides if a new instance of the SimpleHandler class is needed for every message received from the input channel.

# Springboot Integration
The framework provides an easy way to integrate with your Springboot application. This is by using the `eclectique-messaging-springboot` artifact. The maven coordinates for this artifact is given below:

```xml
<dependency>
    <groupId>com.eclectique</groupId>
    <version>1.0-SNAPSHOT</version>
    <artifactId>eclectique-messaging-springboot</artifactId>
</dependency>
```

Here is an example of the above code using Eclectique Messaging's Springboot Integration.

The configuration file - `electique-conf.yml` remains the same as above.


### Springboot Main Class

```java
@ComponentScan(basePackages = {"com.eclectique", "org.example"})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

The `main` class has explicit scanning for the package prefix `com.eclectique`

### Sending Messages

The first step towards sending a message is to register the Producer bean. This can be done as follows:

```java
@Component
public class SampleProducer extends MessageProducerFoundation {

    @Override
    protected String getQueueName() {
        return "test_topic";
    }

    @Override
    protected String getProfile() {
        return "profile1";
    }

    public SampleProducer() throws InterruptedException {
        super();
    }
}
```

- The class `SampleProducer` is inherting `MessageProducerFoundation`. The `MessageProducerFoundation` class performs all the wiring that is needed to setup a producer. 
- As seen above, the `getProfile()` function tells the producer which profile from the `eclectique-conf.yml` has to be used for setting up the producer. In this case we are using `profile1`, which per the configuration uses the `system` `kafka`. This means that the `SampleProducer` wired up to send messages to a Kafka messaging system. 

The `SampleProducer` is configured to write to the topic `test_topic` per the `getQueueName()` function

Next step is to use the above producer to send a message. The below code illustrates that:

```java
@Component
public class ProducerExec {

    public ProducerExec(SampleProducer producer){
        SimpleMessage contentMsg = new SimpleMessage();
        contentMsg.setContentSchema(String.class.toString());
        contentMsg.setDateTime(Optional.of(Date.from(Instant.now())));
        contentMsg.setMessageId("msg-1");
        contentMsg.setPayload("Hello, this is a sample message".getBytes());
        contentMsg.setSender("app-1");
        producer.sendMessage(contentMsg);
        System.out.println("Message sent");
    }
}
```

The above `ProducerExec` class is a Spring Bean that while getting bootstrapped uses the `SampleProducer` that has been defined before to send a message to the Kafka 
