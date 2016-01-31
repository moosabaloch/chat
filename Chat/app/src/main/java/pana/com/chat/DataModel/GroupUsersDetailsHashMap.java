package pana.com.chat.DataModel;

import java.util.HashMap;

/**
 * Created by Moosa
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class GroupUsersDetailsHashMap extends HashMap<String, DataModelUser> {
    private static GroupUsersDetailsHashMap groupUsersDetailsHashMap;

    public static GroupUsersDetailsHashMap getInstance() {
        if (groupUsersDetailsHashMap == null) {
            groupUsersDetailsHashMap = new GroupUsersDetailsHashMap();
        }
        return groupUsersDetailsHashMap;
    }
}
