package common;

import automation.EventId;
import automation.QuestDetailService;
import automation.model.PartDetail;
import automation.model.QuestDetail;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractQuestTest {

    protected void executeQuest(AbstractQuest quest, int part, String expectedAnswer) {

        EventId eventId = quest.getEventId();
        int questNumber = quest.getQuestNumber();

        Function<AbstractQuest, String> executor = switch (part) {
            case 1 -> AbstractQuest::executePart1;
            case 2 -> AbstractQuest::executePart2;
            case 3 -> AbstractQuest::executePart3;
            default -> throw new UnsupportedOperationException();
        };

        String actualSolution = executor.apply(quest);
        QuestDetail questDetail = QuestDetailService.getQuestDetail(eventId, questNumber);
        PartDetail partDetail = questDetail.getParts().get(part - 1);
        String savedAnswer = partDetail.getAnswer();

        assertEquals(expectedAnswer, actualSolution);
        assertEquals(expectedAnswer, savedAnswer);

    }

}