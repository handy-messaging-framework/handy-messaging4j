---
layout: page
title: "Messaging System Switch"
parent: Features
---


# Seamlessly switch between messaging systems

One of the key features of the eclectique-messaging4J framework is to integrate with different messaging systems seamlessly. For instance, the framework enables the application to switch between different messaging systems without any change in the application logic. 
Merely by adding or changing the profile and its details in the `eclectique-conf.yml` configuration file, an application can switch from one messaging system to another

For instance, lets take the example where the application has to consume messages from a Kafka topic - `demo_topic`. 

### Message Handler Class

```java
@Component
public class SampleMessageProcessor extends MessageHandlerFoundation {
 
    @Override
    protected String getQueueName() {
        return "demo_topic";
    }

    @Override
    protected String getProfileName() {
        return "profile";
    }

    @Override
    protected String getMessageTypeClass() {
        return "com.eclectique.messaging.types.simplemessage.SimpleMessage";
    }

    @Override
    public void handleMessage(Message message) {
        try {
            SimpleMessage msg = (SimpleMessage) message;
            String str = new String(msg.getPayload(), "utf-8");
            System.out.println(str);
        } catch (Exception ex){
            System.out.println("Exception ===> "+ex.getMessage());
        }

    }

    @Override
    public Optional<MessageHandler> getNewInstance() {
        return Optional.of(new SampleMessageProcessor());
    }
}

```

The `eclectique-conf.yml` configuration file looks as below:

```yaml
eclectique:
 profiles:
  - profileName: profile
    system: kafka
    consumer:
      properties:
        bootstrap.servers: localhost:9092
        group.id: test_app
        max.messages.per.batch: 3
        max.poll.duration.millis: 10000
```

Now if a need arise to switch the messaging platform from Apache Kafka to Google Pub/Sub, all that is needed is to change the configuration file to the one below:

```yaml
eclectique:
 profiles:
  - profileName: profile
    system: google-pubsub
    consumer:
      properties:
        emulator.exec.flag: true
        host.name: "localhost"
        host.port: 8085
        project.id: "sample-pubsub"
        subscription.id: "test_sub"
        max.messages.per.batch: 3
        max.poll.duration.millis: 10000  
```

In the new configuration file, the `profileName` is kept as `profile` but the `system` is changed to `google-pubsub`. The relevant consumer properties of Google Pub/Sub are given under the consumer properties.

Here we have seen that, no change is done in the `SampleMessageProcessor` which ultimately deals with processing the message. The framework took care of adpating from Apache Kafka to Google PubSub.

Also, note that, even though in the above case we have changed the contents of the the `profile` to specify Google's pub/sub configuration, an alternative approach is to add a new profile with a different name, say `profile_google_pubsub` and use that in the `getProfileName()` function within the message handler.
