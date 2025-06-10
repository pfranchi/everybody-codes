package automation.model;

import java.util.List;

public class QuestDetail {

    private ChallengeId challengeId;
    private List<PartDetail> parts;
    private AnswerAttempts answerAttempts;

    public ChallengeId getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(ChallengeId challengeId) {
        this.challengeId = challengeId;
    }

    public List<PartDetail> getParts() {
        return parts;
    }

    public void setParts(List<PartDetail> parts) {
        this.parts = parts;
    }

    public AnswerAttempts getAnswerAttempts() {
        return answerAttempts;
    }

    public void setAnswerAttempts(AnswerAttempts answerAttempts) {
        this.answerAttempts = answerAttempts;
    }
}
