package automation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class KeyService {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final HttpResponse.BodyHandler<String> RESPONSE_BODY_HANDLER = HttpResponse.BodyHandlers.ofString();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String getAESKey(EventId eventId, int questNumber, int part) {

        String url = "https://everybody.codes/api/event/" + eventId.number() + "/quest/" + questNumber;

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

        JsonNode keyNode = node.get("key" + part);

        if (keyNode == null) {
            throw new RuntimeException("Decryption key is not available. (Either because the puzzle has not " +
                    "yet been unlocked or because the previous part has not been solved)");
        }

        return keyNode.asText();

    }

    public static String retrieveCorrectAnswer(EventId eventId, int questNumber, int part) {

        String url = "https://everybody.codes/api/event/" + eventId.number() + "/quest/" + questNumber;

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

        JsonNode keyNode = node.get("answer" + part);

        if (keyNode == null) {
            throw new RuntimeException("Answer is not available. (You did not post it to the server).");
        }

        return keyNode.asText();

    }

    public static String decrypt(String key, String encryptedText) {

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = key.substring(0, 16).getBytes(StandardCharsets.UTF_8);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        try {
            byte[] encryptedBytes = Hex.decodeHex(encryptedText);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivBytes));

            // Decrypt
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (DecoderException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

    }

}
