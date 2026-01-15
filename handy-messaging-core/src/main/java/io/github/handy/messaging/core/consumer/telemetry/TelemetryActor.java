package io.github.handy.messaging.core.consumer.telemetry;


import java.util.Date;
import io.github.handy.messaging.interfaces.TelemetryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import io.github.handy.messaging.core.consumer.ActorInitializationCallback;
import io.github.handy.messaging.interfaces.Message;



public class TelemetryActor extends AbstractActor {

    public final static class MessageReceived {
        Message message;
        Date receivedTS;
        String topicName;
        public MessageReceived(String topicName, Message message, Date receivedTS){
            this.message = message;
            this.receivedTS = receivedTS;
            this.topicName = topicName;
        }
    }

    final static class AnalyticsActorInitialized {}

    private Logger LOGGER = LoggerFactory.getLogger(TelemetryActor.class);

    private final TelemetryHandler telemetryHandler;

    public TelemetryActor(ActorInitializationCallback onInitializationCallback, TelemetryHandler telemetryHandler){
        this.telemetryHandler = telemetryHandler;
        onInitializationCallback.afterInitialize(getSelf());
    }

    public static Props getActorProperties(ActorInitializationCallback onInitializationCallback, TelemetryHandler telemetryGenerator){
        return Props.create(TelemetryActor.class, onInitializationCallback, telemetryGenerator);
    }

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder().match(MessageReceived.class, args -> {
            this.onNewMessageReceived(args.topicName, args.message, args.receivedTS);
        }).build();
    }


    private void  onNewMessageReceived(String topicName, Message message, Date receivedTS) {
        try {
            this.telemetryHandler.onNewMessageReceived(topicName, message, receivedTS);
        } catch (Exception ex){
            LOGGER.error("Telemetry Invocation Failed");
        }
    }


    
}