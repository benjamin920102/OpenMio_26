package me.mioclient.util.io;

import me.mioclient.api.ClientAccessor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public final class HttpRequests implements ClientAccessor {
    public static HttpResponse<String> sendSyncRequest(HttpRequest httpRequest) {
        return sendSync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    public static <T> HttpResponse<T> sendSync(HttpRequest httpRequest, HttpResponse.BodyHandler<T> bodyHandler) {
        try {
            return HttpClient.newHttpClient().send(httpRequest, bodyHandler);
        } catch (Throwable th) {
            throw new java.lang.RuntimeException(th);
        }
    }

    public static CompletableFuture<HttpResponse<String>> sendStringRequest(HttpRequest httpRequest) {
        return getCompletableFuture2962(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    public static <T> CompletableFuture<HttpResponse<T>> getCompletableFuture2962(HttpRequest httpRequest, HttpResponse.BodyHandler<T> bodyHandler) {
        HttpClient newHttpClient = HttpClient.newHttpClient();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return newHttpClient.send(httpRequest, bodyHandler);
            } catch (Throwable th) {
                throw new java.lang.RuntimeException(th);
            }
        }, executorService);
    }

    public static HttpRequest.Builder newGetRequest(String str) {
        return newRequestBuilder(str).GET();
    }

    public static HttpRequest.Builder getBuilder2964(String str, HttpRequest.BodyPublisher bodyPublisher) {
        return newRequestBuilder(str).POST(bodyPublisher);
    }

    public static HttpRequest.Builder newRequestBuilder(String str) {
        return HttpRequest.newBuilder().uri(URI.create(str));
    }
}
