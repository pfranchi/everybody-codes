package automation;

import automation.model.PartDetail;
import automation.model.QuestDetail;
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

public class InputService {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final HttpResponse.BodyHandler<String> RESPONSE_BODY_HANDLER = HttpResponse.BodyHandlers.ofString();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void fetchAndUpdateEncrypted(EventId eventId, int questNumber, QuestDetail questDetail) {

        String seed = SeedService.getSeed();

        String url = "https://everybody-codes.net/assets/" + eventId.number() + "/"
                + questNumber + "/input/" + seed + ".json";

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

        int statusCode = response.statusCode();

        if (statusCode != 200) {
            throw new RuntimeException("Encrypted input is not available");
        }

        JsonNode node;
        try {
            node = OBJECT_MAPPER.readTree(response.body());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        for (PartDetail partDetail: questDetail.getParts()) {

            if (!partDetail.isEncryptedObtained()) {
                int partNumber = partDetail.getPartNumber();

                String encrypted = node.get(Integer.toString(partNumber)).asText();

                partDetail.setEncryptedObtained(true);
                partDetail.setEncrypted(encrypted);
            }

        }

        QuestDetailService.save(eventId, questNumber, questDetail);

    }

    public static void fetchAndUpdateEncryptedSinglePart(EventId eventId, int questNumber, QuestDetail questDetail, PartDetail partDetail) {

        String seed = SeedService.getSeed();

        String url = "https://everybody-codes.net/assets/" + eventId.number() + "/"
                     + questNumber + "/input/" + seed + ".json";

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

        int statusCode = response.statusCode();

        if (statusCode != 200) {
            throw new RuntimeException("Encrypted input is not available");
        }

        JsonNode node;
        try {
            node = OBJECT_MAPPER.readTree(response.body());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        int partNumber = partDetail.getPartNumber();

        String encrypted = node.get(Integer.toString(partNumber)).asText();

        partDetail.setEncryptedObtained(true);
        partDetail.setEncrypted(encrypted);

        QuestDetailService.save(eventId, questNumber, questDetail);

    }

}
