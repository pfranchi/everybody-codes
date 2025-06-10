package automation;

import automation.model.PartDetail;
import automation.model.QuestDetail;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AutomationServiceTest {

    @Test
    void event2024Quest1Part1GetInput() {

        EventType type = EventType.MAIN_EVENT;
        int eventNumber = 2024;
        int questNumber = 1;
        int part = 1;

        EventId eventId = new EventId(type, eventNumber);
        PuzzleId puzzleId = new PuzzleId(eventId, questNumber, part);

        String input = AutomationService.getInput(puzzleId).getParts().getFirst().getDecrypted();

        assertEquals("CCAAACACAACCAABAACABACBCCBBABBCAABCACBCBCCABAAACBBBCCACCBAACABBBBCBAAAACABACACBACACCCACACABABACCCCAABBABABBCCCBCBCBBCBBCACBBBAACCCBACBAABACABACAAACABCBCBCBCBCCBBAABBABAAABCCAABBCCABCACCCBBBCCAABBBCACACAACABCCACBBBBBCCCABBACCBAACABCAACCCCABBBCCCBCBBBCABACCCCCCACBBBCBACABCABBBCCBBBCCABACCBCCBBAABABBCCBCBABCCCAACAACCBBCCCACACAACAAABABBCBAABABCCCABABCAABABBBCBCCAAABABCAACAABBACCBCBABAABCAAABBBACBCACAACCBAACACABCBABABBAAACBAACCCCBCABAACBBABCBCABCAABABABACBAABCCABCABBCBCABBBBBABBBBAAABBACBAACBCABCCBCBACCBABBCBACCABAAAABCAACAABCABAABCCCAAABBAAABAACCAACCBBCBCBAAAAAABBAAABABBCCCBAACCABACCCBBBBBCBABCACCACBBACBBBBCABBCCCAACBACBAAABACACCABBCBCBACABBCACBABAAAACCCBAACACACBCCBBBAACCAACBBACCCBABACAACBCABBBACBBBBCCBCCBBCBBBBAACAACBBCCCBABBABBBBCAACBBACBACCACAAACCCBBCCABBCABCCBBBCAABBACBCBBAABCACBCBABCBBBACBAAAACBABCBABAACABCCABCBBAABCAACCCCABCBBBCCCBBBCABBBABBCACACCCCCBABCABBCCCAABACBCAAACCCCBBAACABBAACCBBCBBBABBBBAABCBCACABACBACBBBBACACCAACAABACBBBAACCBABBBBABBBBAAACBAAAAABBCCCACCACAACBAABBCAAABCCCCCB",
                input);

    }

    @Test
    void event2024Quest1Part1PostAnswer() {

        EventType type = EventType.MAIN_EVENT;
        int eventNumber = 2024;
        int questNumber = 1;
        int part = 1;

        EventId eventId = new EventId(type, eventNumber);
        PuzzleId puzzleId = new PuzzleId(eventId, questNumber, part);

        QuestDetail questDetail = QuestDetailService.getQuestDetail(eventId, questNumber);
        PartDetail partDetail = questDetail.getParts().getFirst();

        String answer = "1317";

        boolean isCorrect = AutomationService.postAnswer(puzzleId, questDetail, partDetail, answer);

        assertTrue(isCorrect);

    }


}