package automation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import automation.model.QuestDetail;
import common.support.SupportService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class QuestDetailService {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static QuestDetail getQuestDetail(EventId eventId, int questNumber) {

        String resourcesRootDirName = eventId.getResourcesRootDirectoryName();
        Path path = Path.of("src/main/resources/inputs/" + resourcesRootDirName + "/quest" + String.format("%02d", questNumber) + ".json");

        if (!Files.exists(path)) {
            path = SupportService.initializeQuestDetail(eventId, questNumber);
        }

        try (BufferedReader br = Files.newBufferedReader(path)) {
            return GSON.fromJson(br, QuestDetail.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Deprecated
    public static QuestDetail loadQuestDetail(EventId eventId, int questNumber) {

        String resourcesRootDirName = eventId.getResourcesRootDirectoryName();
        Path path = Path.of("src/main/resources/inputs/" + resourcesRootDirName + "/quest" + String.format("%02d", questNumber) + ".json");

        try (BufferedReader br = Files.newBufferedReader(path)) {
            return GSON.fromJson(br, QuestDetail.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void save(EventId eventId, int questNumber, QuestDetail questDetail) {

        String resourcesRootDirName = eventId.getResourcesRootDirectoryName();
        Path path = Path.of("src/main/resources/inputs/" + resourcesRootDirName + "/quest" + String.format("%02d", questNumber) + ".json");

        String s = GSON.toJson(questDetail);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
