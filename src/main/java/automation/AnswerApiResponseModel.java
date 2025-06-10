package automation;

public class AnswerApiResponseModel {

    private boolean correct;
    private boolean lengthCorrect;
    private boolean firstCorrect;
    private Object time;
    private Object localTime;
    private Object globalTime;
    private Object globalPlace;
    private Object globalScore;

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public boolean isLengthCorrect() {
        return lengthCorrect;
    }

    public void setLengthCorrect(boolean lengthCorrect) {
        this.lengthCorrect = lengthCorrect;
    }

    public boolean isFirstCorrect() {
        return firstCorrect;
    }

    public void setFirstCorrect(boolean firstCorrect) {
        this.firstCorrect = firstCorrect;
    }

    public Object getTime() {
        return time;
    }

    public void setTime(Object time) {
        this.time = time;
    }

    public Object getLocalTime() {
        return localTime;
    }

    public void setLocalTime(Object localTime) {
        this.localTime = localTime;
    }

    public Object getGlobalTime() {
        return globalTime;
    }

    public void setGlobalTime(Object globalTime) {
        this.globalTime = globalTime;
    }

    public Object getGlobalPlace() {
        return globalPlace;
    }

    public void setGlobalPlace(Object globalPlace) {
        this.globalPlace = globalPlace;
    }

    public Object getGlobalScore() {
        return globalScore;
    }

    public void setGlobalScore(Object globalScore) {
        this.globalScore = globalScore;
    }
}
