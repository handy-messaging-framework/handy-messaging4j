package io.github.handy.messaging.core.consumer.analytics;


import java.io.IOException;
import java.util.Date;

import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import  java.util.Base64;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import io.github.handy.messaging.core.consumer.ActorInitializationCallback;
import io.github.handy.messaging.interfaces.Message;



public class AnalyticsActor extends AbstractActor {

    public final static class MessageReceived {
        Message message;
        Date receivedTS;
        public MessageReceived(Message message, Date receivedTS){
            this.message = message;
            this.receivedTS = receivedTS;
        }
    }

    final static class AnalyticsActorInitialized {}

    private Logger LOGGER = LoggerFactory.getLogger(AnalyticsActor.class);

    private final AnalyticsExporter analyticsExporter;

    public AnalyticsActor(String exporter_endpoint, ActorInitializationCallback onInitializationCallback){
        onInitializationCallback.afterInitialize(getSelf());
        this.analyticsExporter = new AnalyticsExporter(exporter_endpoint);
    }

    public static Props getActorProperties(String exporter_endpoint, ActorInitializationCallback onInitializationCallback){
        return Props.create(AnalyticsActor.class, exporter_endpoint, onInitializationCallback);
    }

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder().match(MessageReceived.class, args -> {
            this.onNewMessageReceived(args.message, args.receivedTS);
        }).build();
    }


    private void  onNewMessageReceived(Message message, Date receivedTS) {
        try {

            ConsumerTelemetryMessages.NewMessageReceived newMessageReceived = ConsumerTelemetryMessages.NewMessageReceived.newBuilder()
                    .setMessageId(message.getId())
                    .setReceivedTSEpoch(receivedTS.getTime())
                    .setMessagePayload(ByteString.copyFrom(message.serialize()))
                    .build();
            String b64_encoded_telemetry_data = Base64.getEncoder().encodeToString(newMessageReceived.toByteArray());
            this.analyticsExporter.exportAnalytics(b64_encoded_telemetry_data);
        } catch (Exception ex){
            LOGGER.error("Analytics export failed");
        }
    }


    
}