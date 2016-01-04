package pana.com.chat.DataModel;

import java.util.HashMap;

/**
 * Created by Moosa on 9/14/2015.
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class Groups {
    private String groupName;
    private String groupImage;
    private String groupDescription;
    private HashMap<String, String> groupAdmin;

    public Groups() {
    }

    public Groups(String groupName, String groupImage, String groupDescription, HashMap<String, String> groupAdmin) {
        this.groupName = groupName;
        this.groupImage = groupImage;
        this.groupDescription = groupDescription;
        this.groupAdmin = groupAdmin;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public HashMap<String, String> getGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupAdmin(HashMap<String, String> groupAdmin) {
        this.groupAdmin = groupAdmin;
    }
}
