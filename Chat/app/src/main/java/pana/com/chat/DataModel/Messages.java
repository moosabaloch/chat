package pana.com.chat.DataModel;

/**
 * Created by Moosa on 9/12/2015.
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class Messages {
    private String timeStamp;
    private String message;
    private String user;

    public Messages() {

    }

    public Messages(String timeStamp, String message,String user) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.user = user;
    }

    @Override
    public String toString() {
        return "Messages{" +
                "timeStamp='" + timeStamp + '\'' +
                ", message='" + message + '\'' +
                ", user='" + user + '\'' +
                '}';
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
