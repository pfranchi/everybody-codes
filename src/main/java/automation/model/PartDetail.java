package automation.model;

public class PartDetail {

    private int partNumber;
    private boolean encryptedObtained;
    private String encrypted;
    private boolean AESKeyObtained;
    private String AESKey;
    private String decrypted;
    private boolean correctAnswerFound;
    private String answer;

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public boolean isEncryptedObtained() {
        return encryptedObtained;
    }

    public void setEncryptedObtained(boolean encryptedObtained) {
        this.encryptedObtained = encryptedObtained;
    }

    public String getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }

    public boolean isAESKeyObtained() {
        return AESKeyObtained;
    }

    public void setAESKeyObtained(boolean AESKeyObtained) {
        this.AESKeyObtained = AESKeyObtained;
    }

    public String getAESKey() {
        return AESKey;
    }

    public void setAESKey(String AESKey) {
        this.AESKey = AESKey;
    }

    public String getDecrypted() {
        return decrypted;
    }

    public void setDecrypted(String decrypted) {
        this.decrypted = decrypted;
    }

    public boolean isCorrectAnswerFound() {
        return correctAnswerFound;
    }

    public void setCorrectAnswerFound(boolean correctAnswerFound) {
        this.correctAnswerFound = correctAnswerFound;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
