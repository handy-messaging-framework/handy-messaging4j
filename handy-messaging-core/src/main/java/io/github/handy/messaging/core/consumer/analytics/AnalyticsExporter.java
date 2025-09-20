package io.github.handy.messaging.core.consumer.analytics;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AnalyticsExporter {

    private String exportEndpoint;
    private HttpClient apiClient;

    public AnalyticsExporter(String exportEndpoint){
        this.exportEndpoint = exportEndpoint;
        apiClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
    }
    public void exportAnalytics(String payload) throws IOException, InterruptedException {
        HttpRequest exporterRequest = HttpRequest.newBuilder()
                .uri(URI.create(this.exportEndpoint))
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        this.apiClient.send(exporterRequest, HttpResponse.BodyHandlers.ofString());
    }
}
