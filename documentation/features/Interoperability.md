---
layout: default
title: "Interoperability"
parent: Features
---

# Interoperability with multiple messaging platforms
This feature of the Eclectique-Messaging4J framework focuses on building an ecosystem where multiple different messaging systems can be used within the same application at the same time. The framework seamlessly allows the user to send and receive messages between these different systems. 
For example, consider a use case where our application go to read a message from a Kafka topic and publish it to a Google PubSub topic. 
Kafka Topic Name - `kafka_topic`
PubSub Topic Name - `pub_sub_topic`

The following snippet of code will achieve this. Note that, the example listed below is assuming that you are using the SpringBoot Framework. However, the same can be done without the Springboot integration of eclectique-messaging framework.


### The configuration file - eclectique-conf.yml
```yaml
eclectique:
 profiles:
  - profileName: profile_kafka
    system: kafka
    consumer:
      properties:
        bootstrap.servers: localhost:9092
        group.id: test_app
        max.messages.per.batch: 3
        max.poll.duration.millis: 10000
  - profileName: profile_pubsub
    system: google-pubsub
    producer:
      properties:
        emulator.exec.flag: true
        host.name: "localhost"
        host.port: 8085
        project.id: "sample-pubsub"
```


Eclectique-Messagig4J uses a concept called `profiles` to distinguish between different messaging systems. The above configuration file has registered 2 profiles as detailed below:
- `profile_kafka` - This profile has a consumer defined in it with its connection parameters. For this example we only need to consume messages from the Kafka topic.
- `profile_pubsub` - This profile has a producer defined. This producer is needed since we need to publish messages to the PubSub topic

### Pub-Sub Producer Bean
```java
@Component
public class PubSubProducer extends MessageProducerFoundation {

    @Override
    protected String getQueueName() {
        return "pub_sub_topic";
    }

    @Override
    protected String getProfile() {
        return "profile_pubsub";
    }

    public PubSubProducer() throws InterruptedException {
        super();
    }
}

```
 The above bean is registering the Producer Bean of pubsub which is used for the sending message to the `pub_sub_topic` of Google PubSub. The `getProfile()` property decides which profile name should the producer use to wire up its messaging systems capabilities. Since it is using the `profile_pubsub` it uses the so called profile from the `eclectique-conf.yml`
 
 Now lets take a look at the Kafka consumer bean.

 ```java
 @Component
public class SampleMessageProcessor extends MessageHandlerFoundation {

    PubSubProducer pubSubProducer;
    public SampleMessageProcessor(PubSubProducer producer){
        this.pubSubProducer = producer;
    }
    @Override
    protected String getQueueName() {
        return "kafka_topic";
    }

    @Override
    protected String getProfileName() {
        return "profile_kafka";
    }

    @Override
    protected String getMessageTypeClass() {
        return "io.github.handy.messaging.types.simplemessage.SimpleMessage";
    }

    @Override
    public void handleMessage(Message message) {
        try {
            SimpleMessage msg = (SimpleMessage) message;
            String str = new String(msg.getPayload(), "utf-8");
            System.out.println(String.format("Message received %s", str));
            System.out.println(String.format("Transferring messaging to PubSub"));
            this.pubSubProducer.sendMessage(msg);
        } catch (Exception ex){
            System.out.println("Exception ===> "+ex.getMessage());
        }

    }

    @Override
    public Optional<MessageHandler> getNewInstance() {
        return Optional.empty();
    }
}
 ```
The above code represents a consumer using the `profile_kafka`. When a message is received in the Kafka topic `kafka_topic` it will be taken care by the `handleMessage(...)` function. As can be inferred from the code above, the `handleMessage` function relays the message to the Google PubSub producer.

