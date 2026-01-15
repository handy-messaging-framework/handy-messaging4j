package io.github.handy.messaging.interfaces;

import java.util.Date;
import java.util.UUID;

public abstract class TelemetryHandler {

    TelemetryInitializationInfo telemetryInitializationInfo;
    UUID telemetryInstanceId;

    public TelemetryHandler(TelemetryInitializationInfo telemetryInitializationInfo){
        this.telemetryInitializationInfo = telemetryInitializationInfo;
        this.telemetryInstanceId = UUID.randomUUID();
    }

    public TelemetryInitializationInfo getTelemetryInitializationInfo() {
        return this.telemetryInitializationInfo;
    }

    public UUID getTelemetryInstanceId() {
        return this.telemetryInstanceId;
    }

    public abstract void onNewMessageReceived(String topicName, Message message, Date receivedTS);
}
