package common.support;

import automation.EventId;
import automation.EventType;
import automation.InputService;
import automation.QuestDetailService;
import automation.model.PartDetail;
import automation.model.QuestDetail;

public class RefreshInputApp {

    public static void main(String[] args) {

        forceRefreshMultipleQuests();

    }

    private static void forceUpdateSinglePart() {

        EventId eventId = new EventId(EventType.MAIN_EVENT, 2025);
        int questNumber = 7;

        int partNumber = 3;

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

    private static void forceRefreshMultipleQuests() {
        EventId eventId = new EventId(EventType.MAIN_EVENT, 2025);

        for (int questNumber = 19; questNumber <= 19; questNumber++) {

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

}
