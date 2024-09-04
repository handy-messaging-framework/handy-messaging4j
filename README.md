# Handy-Messaging4J
Asynchronous messaging is one of the fundamental pillars of any distributed system. In this day and age, there is no scarcity of such asynchronous messaging platforms. Though these messaging systems have different implementation methods the core of what they offer pretty much stays the same - A publisher produces a message and one or more consumers can subscribe to that message though a topic or a queue. The consumers on receiving the message will process the message. In essence this fundamental behavior pretty much stays the same across most of these messaging systems. Though these messaging systems have plus and minus features to these fundamental behavior, there is no such standard framework for developers to interface with these messaging systems. This is the motivation for the Handy Messaging Framework.


## Using the library in your project

### Maven Import

#### Importing the core module
The core module needs to be imported for HMF4J to work. 
```xml
<dependency>
    <groupId>io.github.handy-messaging-framework</groupId>
    <artifactId>hmf4j-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Importing connector module(s)
Any connector module can be imported based on need. For example if interfacing to a kafka system import the Kafka connector as below:
```xml
<dependency>
    <groupId>io.github.handy-messaging-framework</groupId>
    <artifactId>hmf4j-kafka-connector</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Importing TestKit

*Importing Photon Messaging System and its connector*

```xml
<dependency>
    <groupId>io.github.handy-messaging-framework</groupId>
    <artifactId>hmf4j-photon-mq</artifactId>
    <version>1.0.0</version>
</dependency>
```

```xml
<dependency>
    <groupId>io.github.handy-messaging-framework</groupId>
    <artifactId>hmf4j-photon-connector</artifactId>
    <version>1.0.0</version>
</dependency>
```

*Importing Test Toolkit*

```xml
<dependency>
    <groupId>io.github.handy-messaging-framework</groupId>
    <artifactId>hmf4j-test-toolkit</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Importing the SimpleMessage Type

```xml
<dependency>
    <groupId>io.github.handy-messaging-framework</groupId>
    <artifactId>hmf4j-types-simplemessage</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Compiling and installing the source
```shell
git pull https://github.com/handy-messaging-framework/handy-messaging4j
mvn clean install
```

## License
This project is licensed under the MIT License. See the [LICENSE](/LICENSE.md) file for details.

## Contact
For any inquiries, please contact Aron Sajan Philip at [arondeveloper@yahoo.com](mailto:arondeveloper@yahoo.com)

## Additional Information

For additional details, visit [project website](https://handy-messaging-framework.github.io/handy-messaging4j-docs/) or [Javadocs](https://javadoc.io/doc/io.github.handy-messaging-framework)