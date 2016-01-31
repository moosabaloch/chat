package pana.com.chat.DataModel;

/**
 * Created by Moosa
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class DataModelCurrentGroupChat {
    private static DataModelCurrentGroupChat ourInstance;
    private String groupName;
    private String groupIDKEY;
    private String imageUrl;
    private String groupDescription;

    private DataModelCurrentGroupChat() {
    }

    public static DataModelCurrentGroupChat getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataModelCurrentGroupChat();
        }
        return ourInstance;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupIDKEY() {
        return groupIDKEY;
    }

    public void setGroupIDKEY(String groupIDKEY) {
        this.groupIDKEY = groupIDKEY;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }
}
