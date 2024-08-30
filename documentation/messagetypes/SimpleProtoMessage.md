---
layout: default
title: "SimpleProtoMessage Type"
parent: Message Types
---

# SimpleProtoMessage
SimpleProtoMessage is a message schema can be used for an event driven system. This schema uses Google's Protobuf as its underlying messaging protocol. Messages composed in SimpleProtoMessage gets serialized to a protobuf byte array before it gets sent. The section below details the protobuf schema of the SimpleProtoMessage.

## Version 1.0

```
syntax = "proto3";
option java_outer_classname="SimpleMessageType";
option java_package="com.eclectique.messaging.types.simplemessage";

message SimpleProtoMessage{
  string version = 1;
  string headerSchema=2;
  string contentSchema=3;
  string sender=4;
  string messageId=5;
  string transactionGroupId=6;
  string datetime=7;
  bytes payload=8;
}
```
### Semantics

| Field   | Semantics |
| -------- | --------- |
| version | Indicates the version of the SimpleProtoMessage  |
| headerSchema | Indicates the schema as SimpleProtoMessage |
| contentSchema | Indicates the schema of the payload |
| sender | Name of the sender | 
| messageId | Identifier for the message |
| transactionGroupId | Optional field - Messages pertaining to the same transaction will have a unique ID
| datetime | Date and time when the message is created | 
| payload | The payload contained in the message in binary. Conforms to the `contentSchema` type | 