---
title: Avoiding Race Condition
layout: default
parent: Features
---

# Avoiding Race Condition
One of the frequent use cases in distributed systems that uses asynchronous messaging is to avoid processing messages simulataneously that has some causal ordering among them. For instance, assume we have building a messaging system for a calculator.

![Time Sequence - Parallel Processing](/assets/async_race_condition.drawio.svg)

The above scenario describes a race condition scenario. Ideally we would have wanted one operation to be completely over before the next was even processed. Such as setting the value of X to 3 before decrementing it by 1 or setting the value of X as 0 before incrementing it by 2. However, neither of that is what has happened here. This is an unavoidable scenario in most of the distributed systems. 

## Solution
One of the solutions, the most obvious one here is not to use a thread pool but rather process messages one at a time as shown below:

![Time Sequence - Sequential Processing](/assets/async_sequential.drawio.svg)

So, as shown here based on this approach, since the calculator is not parallel dispatching the messages, it is guaranteed that only one message be processed at any point in time thus totally avoiding the race condition scenario. However, the downside with this approach is, it significanlty brings down the throughput of the system. In many cases where messages are mutually independent, this sequential processing will leave much of the resources of the system be unutilized. To avoid this, we definitely need to bring back parallel processing of the message. 

### How Eclectique Messaging framework solves this problem
Eclectique Messaging framework has adopted a solution that ensures throughput by utilizing parallel message processing at the same time avoid Race Condition scenario above. 
Before proceeding further read on [various messaging types here](../messagetypes/message_types.html)

Any messaging type that supports `transactionGroupId` (as it is called in `SimpleProtoMessage` type) or in similar sense any message type that supports such an identifier for grouping messages that have dependecies among them, can be used for ordering messages in the event of such race condition scenarios.

Lets detail this with `SimpleProtoMessage` type as an example.

There is a property `transactionGroupId` in `SimpleProtoMessage` format that is intended for grouping associated messages together.
Below is a depiction of how the Dispatcher system in Eclectique Messaging framework handles this scenario:

![Eclectique Message Ordering](/assets/eclectique_message_ordering.svg)

As per the dispatcher system above, any message that has a `transactionGroupID` gets enqueued in its respective transaction queue. These messages are then subjected to sequential dispatching to their individual workers. This means that the next message in a transaction queue will be processed only if the previous one in the queue has completed its processing. This ensures that messages in the same transaction queue will not get simulatenously processed. Thus avoiding the entire race condition scenario. At the same time, messages across transaction queues gets independently processed (parallelly) as they don't have any dependecies among them.
Those messages that do not have `transactionGroupID` are supposed to be independent and they are all subjected to parallel dispatching policy where they are get executed parallely. 
In this way Eclectique Messaging framework combines a hybrid architecture utilizing sequential processing among messages that have dependecies among them and parallel processing among messages that can be processed independently. Thus utilizing maximum throughput of the consumer system.