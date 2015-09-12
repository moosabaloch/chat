package pana.com.chat;

/**
 * Created by Moosa on 9/12/2015.
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class DataModelChatUserSingleTon {
    private static DataModelChatUserSingleTon ourInstance;
    private String emailUserFriend;
    private String imageUrlUserFriend;
    private String nameUserFriend;
    private String phoneUserFriend;
    private String uuidUserFriend;

    private DataModelChatUserSingleTon() {
    }

    public static DataModelChatUserSingleTon getInstance() {
        if (ourInstance == null) {
            return ourInstance = new DataModelChatUserSingleTon();
        }
        return ourInstance;
    }

}
