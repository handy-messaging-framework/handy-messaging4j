---
title: Photon Messaging System
layout: default
parent: Features
---

# Photon Messaging System
Before dwelving to the details of using the test toolkit, lets see a new messaging system that comes as a part of the Eclectique Messaging framework - `Photon Messaging System`
Photon is an in-memory messaging system that is built from ground up with focus on testing messaging handler routines within an application. While testing a message producer or consumer, Photon Messaging Service abstracts the messaging system and allows the testing code to send and receive messages from Photon Messaging just like how they can do with any other messaging systems like Apache Kafka, Google Pub/Sub etc...
Photon Messaging System derives its features from common behavior of several messaging service which is the core foundation on which the Eclectique-Messaging framework is built upon. Just like how an application can send and receive asynchronous messages from any messaging system, it can do the same with Photon Messaging System.

## Why Photon Messaging System?
Photon Messaging Systems brings the common denominator of different messaging systems together, there are several characteristics that makes Photon ideal for integrating with your test code:
- Running in the same memory space as that of your application
 One of the main charactersic of Photon is, it runs in the same memory space as that of the application code that is getting tested. Unlike any other messaging systems, Photon is not running in a different memory space as a different process. This makes request and response processing way faster than handling the same through a messaging system that runs in a different process space.

- Support for rasing multiple instances of messaging systems - If you have a scenario where you are interfacing with multiple messaging systems and want to test the interactions among them, Photon Messaging has the capability to spin up multiple messaging services that can then be tied to the application tested.

- Seamless integration with Eclectique's Test Toolkit - Eclectque's Test Toolkit is built around the Photon Messaging system. This means that to test the code, there is no need of any extra bootstrapping of the Photon Messaging system as it will be taken care seamlessly by the Test Toolkit. Hence any bootstrapping steps like - raising messaging instances, creation of queues, subscribing to queues are all taken care behind the scene by the Test Tookit. More on this in [Seamless Testing](/features/seamless_testing.html)

## Working with Photon Messaging System
Though Eclectique's Test Toolkit makes it easier to work with Photon Messaging System, here are some steps to get you started:

### Setting up Photon Messaging System: 

Here is an example of setting up a Photon Messaging instance:

```java
// Create an instance of PhotonMessagingAdministrator
    PhotonMessagingAdministrator admin = new PhotonMessagingAdministrator();

// Register a new messaging service
    String serviceId = "myService";
    admin.registerMessagingService(serviceId);
```

### Creating and Managing Queues
Learn how to create and manage queues within Photon

```java
String queueName = "myQueue";
CommandResponse response = admin.registerMessagingQueue(queueName, serviceId);

// Check the response status
if (response.getCommandExecutionStatus().equals(CommandExecutionStatus.SUCCESS)) {
    System.out.println("Queue registered successfully");
} else {
    System.out.println("Failed to register queue");
}
```

### Sending and Receiving Messages: 
 Understand the process of sending and receiving messages using Photon. Once the Service and Queue are setup, the next step is to creating a subscribe for the queue. Here is an example of creating a subscriber and fetching messages from the queue.

#### Sending Messages

```java
 // Create an instance of PhotonProducer
String serviceId = "myService";
PhotonProducer producer = new PhotonProducer(serviceId);

// Create a message
SimpleMessage message = new SimpleMessage();
message.setPayload("Hello, World!".getBytes());

// Send the message to a queue
String queueName = "myQueue";
CommandResponse response = producer.sendMessage(queueName, message);

// Check the response status
if (response.getCommandExecutionStatus().equals(CommandExecutionStatus.SUCCESS)) {
    System.out.println("Message sent successfully");
} else {
    System.out.println("Failed to send message");
}
```


#### Receiving Messages

```java
String serviceId = "myService";
String queueName = "myQueue";
String consumerId = "myConsumer";
try(PhotonConsumer consumer = new PhotonConsumer(serviceId, queueName, consumerId, SimpleMessage.class)){
    // Read messages from the queue
    long waitDurationMillis = 1000; // wait for 1 second
    List<SimpleMessage> messages = consumer.readMessages(waitDurationMillis);

    // Print the messages
    for (SimpleMessage message : messages) {
        System.out.println("MessageId: " + message.getId());
    }
}
```