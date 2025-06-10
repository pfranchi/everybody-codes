package automation;

import automation.model.AnswerAttemptDetail;
import automation.model.AnswerAttempts;
import automation.model.PartDetail;
import automation.model.QuestDetail;
import com.google.gson.Gson;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AutomationService {

    private static final Gson GSON = new Gson();

    public static QuestDetail getInput(PuzzleId puzzleId) {

        EventId eventId = puzzleId.eventId();
        int questNumber = puzzleId.questNumber();
        int part = puzzleId.part();

        QuestDetail questDetail = QuestDetailService.getQuestDetail(eventId, questNumber);

        PartDetail partDetail = questDetail.getParts().get(part - 1);

        if (!partDetail.isEncryptedObtained()) {
            InputService.fetchAndUpdateEncrypted(eventId, questNumber, questDetail);
        }

        String encrypted = partDetail.getEncrypted();

        if (!partDetail.isAESKeyObtained()) {
            String key = KeyService.getAESKey(eventId, questNumber, part);

            partDetail.setAESKeyObtained(true);
            partDetail.setAESKey(key);

            String decrypted = KeyService.decrypt(key, encrypted);

            partDetail.setDecrypted(decrypted);

            QuestDetailService.save(eventId, questNumber, questDetail);

        }

        return questDetail;

    }

    public static boolean postAnswer(PuzzleId puzzleId, QuestDetail questDetail, PartDetail partDetail, String answer) {

        if (partDetail.isCorrectAnswerFound()) {
            return partDetail.getAnswer().equals(answer);
        }

        AnswerAttempts allPartsAnswerAttempts = questDetail.getAnswerAttempts();

        List<AnswerAttemptDetail> answerAttemptDetails = switch (puzzleId.part()) {
            case 1 -> allPartsAnswerAttempts.getPart1();
            case 2 -> allPartsAnswerAttempts.getPart2();
            case 3 -> allPartsAnswerAttempts.getPart3();
            default -> throw new UnsupportedOperationException("Part " + puzzleId.part() + " is not valid");
        };

        if (!(answerAttemptDetails instanceof ArrayList<AnswerAttemptDetail>)) {
            answerAttemptDetails = new ArrayList<>();

            switch (puzzleId.part()) {
                case 1 -> allPartsAnswerAttempts.setPart1(answerAttemptDetails);
                case 2 -> allPartsAnswerAttempts.setPart2(answerAttemptDetails);
                case 3 -> allPartsAnswerAttempts.setPart3(answerAttemptDetails);
            }

        }

        if (answerAttemptDetails.stream().anyMatch(detail -> detail.getAnswerAttempt().equals(answer))) {
            // Answer already attempted
            return false;
        }

        // Call API
        AnswerApiResponseModel responseModel;
        try {
            responseModel = AnswerService.callAnswerApi(puzzleId, answer);
        } catch (CorrectAnswerAlreadyPosted e) {

            // This is used when the correct answer has already been posted but the QuestDetail is not updated accordingly.
            // Retrieve the correct answer from the key API.

            String correctAnswer = KeyService.retrieveCorrectAnswer(puzzleId.eventId(), puzzleId.questNumber(), puzzleId.part());

            partDetail.setCorrectAnswerFound(true);
            partDetail.setAnswer(correctAnswer);

            QuestDetailService.save(puzzleId.eventId(), puzzleId.questNumber(), questDetail);

            return answer.equals(correctAnswer);
        }

        AnswerAttemptDetail detail = new AnswerAttemptDetail();
        detail.setAnswerAttempt(answer);
        detail.setTimestamp(Instant.now().toString());
        detail.setOutcome( GSON.toJson(responseModel) );

        answerAttemptDetails.add(detail);

        if (responseModel.isCorrect()) {

            partDetail.setCorrectAnswerFound(true);
            partDetail.setAnswer(answer);

        }

        QuestDetailService.save(puzzleId.eventId(), puzzleId.questNumber(), questDetail);

        return responseModel.isCorrect();

    }


}
