package edu.surfability.etl.io;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpCsv {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static InputStream get(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<InputStream> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofInputStream());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return resp.body();
        }
        throw new IOException("HTTP " + resp.statusCode() + " for " + url);
    }
}