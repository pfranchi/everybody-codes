package automation.model;

public class AnswerAttemptDetail {

    private String answerAttempt;
    private String timestamp;
    private String outcome;

    public String getAnswerAttempt() {
        return answerAttempt;
    }

    public void setAnswerAttempt(String answerAttempt) {
        this.answerAttempt = answerAttempt;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
}
