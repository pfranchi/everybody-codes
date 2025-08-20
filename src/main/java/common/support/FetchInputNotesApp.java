package common.support;

import automation.EventId;
import automation.EventType;
import automation.QuestDetailService;
import automation.model.PartDetail;
import automation.model.QuestDetail;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class FetchInputNotesApp {

    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final HttpResponse.BodyHandler<String> RESPONSE_BODY_HANDLER = HttpResponse.BodyHandlers.ofString();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws IOException, InterruptedException {

        EventId eventId = new EventId(EventType.MAIN_EVENT, 2025);

        for (int questNumber = 1; questNumber <= 20; questNumber++) {
            QuestDetail questDetail = QuestDetailService.loadQuestDetail(eventId, questNumber);

            String seed = Files.readString(Path.of("src/main/resources/seed.txt"));

            if (questDetail.getParts().stream().anyMatch(partDetail -> !partDetail.isEncryptedObtained())) {

                String url = "https://everybody-codes.b-cdn.net/assets/" + eventId.number() + "/"
                        + questNumber + "/input/" + seed + ".json";

                String sessionCookie = Files.readString(Path.of("src/main/resources/sessionCookie.txt"));

                HttpRequest request = HttpRequest.newBuilder().GET()
                        .header(HttpHeaders.COOKIE, "everybody-codes=" + sessionCookie)
                        .headers(HttpHeaders.USER_AGENT, "User 1940 Paolo Franchi (franchi.paolo94@gmail.com)")
                        .uri(URI.create(url))
                        .build();

                HttpResponse<String> response = HTTP_CLIENT.send(request, RESPONSE_BODY_HANDLER);

                int statusCode = response.statusCode();
                System.out.println("Quest " + questNumber + ": obtained status code " + statusCode);

                JsonNode node = OBJECT_MAPPER.readTree(response.body());

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
        }

    }

}
