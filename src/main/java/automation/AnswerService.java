package automation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class AnswerService {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final HttpResponse.BodyHandler<String> RESPONSE_BODY_HANDLER = HttpResponse.BodyHandlers.ofString();
    private static final Gson GSON = new Gson();

    public static AnswerApiResponseModel callAnswerApi(PuzzleId puzzleId, String answer) throws CorrectAnswerAlreadyPosted {

        String url = "https://everybody.codes/api/event/" + puzzleId.eventId().number() + "/quest/"
                + puzzleId.questNumber() + "/part/" + puzzleId.part() + "/answer";

        String sessionCookie;
        try {
            sessionCookie = Files.readString(Path.of("src/main/resources/sessionCookie.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonNode jsonNodeAnswerText = new TextNode(answer);
        Map<String, JsonNode> children = Map.of("answer", jsonNodeAnswerText);
        JsonNode jsonNodeRequestBody = new ObjectNode(JsonNodeFactory.instance, children);

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(jsonNodeRequestBody.toString());

        HttpRequest request = HttpRequest.newBuilder().POST(bodyPublisher)
                .header(HttpHeaders.COOKIE, "everybody-codes=" + sessionCookie)
                .headers(HttpHeaders.USER_AGENT, "User 1940 Paolo Franchi (franchi.paolo94@gmail.com)")
                .headers(HttpHeaders.CONTENT_TYPE, "application/json")
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response;
        try {
            response = HTTP_CLIENT.send(request, RESPONSE_BODY_HANDLER);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (response.statusCode() == 409) {
            // Status 409 CONFLICT
            // This status is returned when the correct answer has already been posted to the server
            throw new CorrectAnswerAlreadyPosted("Correct answer has already been posted to the server");
        }

        if (response.statusCode() == 423) {
            // Status 423 LOCKED
            // This status is returned when not enough time has passed since the last wrong answer attempt
            throw new RuntimeException("Wait some time after a wrong answer before posting again");
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Generic error from answer API");
        }

        return GSON.fromJson(response.body(), AnswerApiResponseModel.class);

    }

}
