---
layout: page
title: "Message Types"
has_children: true
nav_order: 5
---

# Message Types
This section deals with various message formats supported by Eclectique Messaging framework. These message types essentially acts as wrappers for the payload be transferred through an asynchronous messaging channel. The responsibility of the message types is to standardize the communication between interacting systems. For this purpose, these messaging types carry various other meta-data pertaining to a transfer other than the payload itself. For instance, it may contain the timestamp of when the message publisher sent the message. Each of the types defined below can be used depending on the usecase. 