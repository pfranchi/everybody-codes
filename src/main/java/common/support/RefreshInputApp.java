package common.support;

import automation.EventId;
import automation.EventType;
import automation.InputService;
import automation.QuestDetailService;
import automation.model.PartDetail;
import automation.model.QuestDetail;

public class RefreshInputApp {

    public static void main(String[] args) {

        EventType eventType = EventType.STORY;
        int eventNumber = 3;
        EventId eventId = new EventId(eventType, eventNumber);

        int questNumber = 1;
        int partNumber = 3;

        forceUpdateSinglePart(eventId, questNumber, partNumber);

        //forceRefreshSingleQuest(eventId, questNumber);

        int fromQuest = 1;
        int toQuest = 3;
        //forceRefreshMultipleQuests(eventId, fromQuest, toQuest);

    }

    private static void forceUpdateSinglePart(EventId eventId, int questNumber, int partNumber) {

        QuestDetail questDetail = QuestDetailService.getQuestDetail(eventId, questNumber);

        PartDetail partDetail = questDetail.getParts().get(partNumber - 1);

        InputService.fetchAndUpdateEncryptedSinglePart(eventId, questNumber, questDetail, partDetail);

        partDetail.setAESKeyObtained(false);
        partDetail.setAESKey(null);
        partDetail.setDecrypted(null);
        partDetail.setCorrectAnswerFound(false);
        partDetail.setAnswer(null);

        QuestDetailService.save(eventId, questNumber, questDetail);

    }

    private static void forceRefreshMultipleQuests(EventId eventId, int fromQuest, int toQuest) {
        for (int questNumber = fromQuest; questNumber <= toQuest; questNumber++) {
            forceRefreshSingleQuest(eventId, questNumber);
        }
    }

    private static void forceRefreshSingleQuest(EventId eventId, int questNumber) {
        QuestDetail questDetail = QuestDetailService.getQuestDetail(eventId, questNumber);

        for (int partNumber = 1; partNumber <= 3; partNumber++) {
            PartDetail partDetail = questDetail.getParts().get(partNumber - 1);

            InputService.fetchAndUpdateEncryptedSinglePart(eventId, questNumber, questDetail, partDetail);

            partDetail.setAESKeyObtained(false);
            partDetail.setAESKey(null);
            partDetail.setDecrypted(null);
            partDetail.setCorrectAnswerFound(false);
            partDetail.setAnswer(null);
        }

        QuestDetailService.save(eventId, questNumber, questDetail);
    }

}
