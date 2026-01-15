package io.github.handy.messaging.core.consumer.telemetry;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.FutureTask;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import io.github.handy.messaging.interfaces.TelemetryHandler;
import io.github.handy.messaging.interfaces.TelemetryInitializationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelemetryActorFactory {
    private FutureTask<ActorRef> initializationFuture;
    ActorRef initializedTelemetryActor;
    private Logger LOGGER = LoggerFactory.getLogger(TelemetryActorFactory.class);

    private TelemetryActorFactory(){
        this.initializationFuture = new FutureTask<>(()->{
            return this.initializedTelemetryActor;
        });
    }

    public static FutureTask<ActorRef> initializeTelemetryActor(String telemetryHandlerClassName, TelemetryInitializationInfo telemetryInitializationInfo, ActorSystem consumerSystem) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {

        TelemetryActorFactory factory = new TelemetryActorFactory();
        Class<?> telemetryHandlerClass = Class.forName(telemetryHandlerClassName);
        TelemetryHandler telemetryGenerator = (TelemetryHandler) telemetryHandlerClass.getConstructor(TelemetryInitializationInfo.class).newInstance(telemetryInitializationInfo);
        consumerSystem.actorOf(TelemetryActor.getActorProperties(factory::onTelemetryActorInitialize, telemetryGenerator), "TELEMETRY-ACTOR");
        return factory.initializationFuture;
    }

    private void onTelemetryActorInitialize(ActorRef analyticsActor){
        this.initializedTelemetryActor = analyticsActor;
        this.initializationFuture.run();
    }
}
