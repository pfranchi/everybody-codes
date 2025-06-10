package automation.model;

import java.util.List;

public class AnswerAttempts {

    private List<AnswerAttemptDetail> part1;
    private List<AnswerAttemptDetail> part2;
    private List<AnswerAttemptDetail> part3;

    public List<AnswerAttemptDetail> getPart1() {
        return part1;
    }

    public void setPart1(List<AnswerAttemptDetail> part1) {
        this.part1 = part1;
    }

    public List<AnswerAttemptDetail> getPart2() {
        return part2;
    }

    public void setPart2(List<AnswerAttemptDetail> part2) {
        this.part2 = part2;
    }

    public List<AnswerAttemptDetail> getPart3() {
        return part3;
    }

    public void setPart3(List<AnswerAttemptDetail> part3) {
        this.part3 = part3;
    }
}
