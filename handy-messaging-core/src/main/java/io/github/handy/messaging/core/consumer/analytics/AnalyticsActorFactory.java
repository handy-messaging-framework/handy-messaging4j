package io.github.handy.messaging.core.consumer.analytics;

import java.util.concurrent.FutureTask;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class AnalyticsActorFactory {
    private FutureTask<ActorRef> initializationFuture;
    ActorRef initializedAnalyticsActor;

    private AnalyticsActorFactory(){
        this.initializationFuture = new FutureTask<>(()->{
            return this.initializedAnalyticsActor;
        });
    }

    public static FutureTask<ActorRef> initializeAnalyticsActor(String analytics_exporter_endpoint, ActorSystem consumerSystem){
        AnalyticsActorFactory factory = new AnalyticsActorFactory();
        consumerSystem.actorOf(AnalyticsActor.getActorProperties(analytics_exporter_endpoint, factory::onAnalyticsActorInitialize), "ANALYTICS-ACTOR");
        return factory.initializationFuture;
    }

    private void onAnalyticsActorInitialize(ActorRef analyticsActor){
        this.initializedAnalyticsActor = analyticsActor;
        this.initializationFuture.run();
    }
}
