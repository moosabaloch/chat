package pana.com.chat.DataModel;

/**
 * Created by Moosa .
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class DataModelFriendSingleTon {

    private static DataModelFriendSingleTon ourInstance;

    private String emailUserFriend;
    private String imageUrlUserFriend;
    private String nameUserFriend;
    private String phoneUserFriend;
    private String uuidUserFriend;
    private String conversationID;

    private DataModelFriendSingleTon() {
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public static DataModelFriendSingleTon getInstance() {
        if (ourInstance == null) {
            return ourInstance = new DataModelFriendSingleTon();
        }

        return ourInstance;
    }

    @Override
    public String toString() {
        return "DataModelChatUserSingleTon{" +
                "emailUserFriend='" + emailUserFriend + '\'' +
                ", imageUrlUserFriend='" + imageUrlUserFriend + '\'' +
                ", nameUserFriend='" + nameUserFriend + '\'' +
                ", phoneUserFriend='" + phoneUserFriend + '\'' +
                ", uuidUserFriend='" + uuidUserFriend + '\'' +
                '}';
    }

    public String getEmailUserFriend() {
        return emailUserFriend;
    }

    public void setEmailUserFriend(String emailUserFriend) {
        this.emailUserFriend = emailUserFriend;
    }

    public String getImageUrlUserFriend() {
        return imageUrlUserFriend;
    }

    public void setImageUrlUserFriend(String imageUrlUserFriend) {
        this.imageUrlUserFriend = imageUrlUserFriend;
    }

    public String getNameUserFriend() {
        return nameUserFriend;
    }

    public void setNameUserFriend(String nameUserFriend) {
        this.nameUserFriend = nameUserFriend;
    }

    public String getPhoneUserFriend() {
        return phoneUserFriend;
    }

    public void setPhoneUserFriend(String phoneUserFriend) {
        this.phoneUserFriend = phoneUserFriend;
    }

    public String getUuidUserFriend() {
        return uuidUserFriend;
    }

    public void setUuidUserFriend(String uuidUserFriend) {
        this.uuidUserFriend = uuidUserFriend;
    }

}
