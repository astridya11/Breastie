package fyp.yuki.vitrox2;

public class Message {
    private String content;
    private String senderID;
    private String senderName;
    private String timestamp;
    private boolean isOutgoing;

    public Message(String content, String senderID, String senderName, String timestamp, boolean isOutgoing) {
        this.content = content;
        this.senderID = senderID;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.isOutgoing = isOutgoing;
    }

    public Message(String content, String senderName, String timestamp, boolean isOutgoing) {
        this.content = content;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.isOutgoing = isOutgoing;
    }


    public String getContent() {
        return content;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public boolean isOutgoing() {
        return isOutgoing;
    }

    public void setOutgoing(boolean outgoing) {
        isOutgoing = outgoing;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
