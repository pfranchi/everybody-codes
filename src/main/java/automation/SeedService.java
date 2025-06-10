package automation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class SeedService {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final HttpResponse.BodyHandler<String> RESPONSE_BODY_HANDLER = HttpResponse.BodyHandlers.ofString();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String getSeed() {

        Path path = Path.of("src/main/resources/seed.txt");
        if (!Files.exists(path)) {

            String url = "https://everybody.codes/api/user/me";

            String sessionCookie;
            try {
                sessionCookie = Files.readString(Path.of("src/main/resources/sessionCookie.txt"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            HttpRequest request = HttpRequest.newBuilder().GET()
                    .header(HttpHeaders.COOKIE, "everybody-codes=" + sessionCookie)
                    .headers(HttpHeaders.USER_AGENT, "User 1940 Paolo Franchi (franchi.paolo94@gmail.com)")
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response;
            try {
                response = HTTP_CLIENT.send(request, RESPONSE_BODY_HANDLER);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            JsonNode node;
            try {
                node = OBJECT_MAPPER.readTree(response.body());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            String seed = node.get("seed").asText();

            try {
                Files.writeString(path, seed);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return seed;

        }

        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
