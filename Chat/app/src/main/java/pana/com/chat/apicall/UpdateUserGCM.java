package pana.com.chat.apicall;

import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;

import pana.com.chat.DataModel.DataModelMeSingleton;
import pana.com.chat.Util.FirebaseHandler;

/**
 * Created by Moosa on 1/5/2016.
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class UpdateUserGCM {
    private static UpdateUserGCM updateUserGCM;

    public static UpdateUserGCM getInstance() {
        if (updateUserGCM == null) {
            updateUserGCM = new UpdateUserGCM();
        }
        return updateUserGCM;
    }

    public void updateUserDataOnFirebase(String token) {
        DataModelMeSingleton userData = DataModelMeSingleton.getInstance();
        Log.d("updateUserOnFirebase()", "Invoked:"+token);
        Log.d("updateUserOnFirebase()", "User Data:"+userData.toString());

        if (!(userData.getId().length() < 10)) {
            HashMap<String, String> userDetails = new HashMap<>();
            userDetails.put("email_id", userData.getEmail());
            userDetails.put("image_url", userData.getImageUrl());
            userDetails.put("name", userData.getName());
            userDetails.put("phone", userData.getPhone());
            userDetails.put("gcm", token);
            FirebaseHandler.getInstance().getFirebaseUsersURL().child(userData.getId()).setValue(userDetails, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    Log.d("User Token Updated", "Firebase GCM Token refresh");
                }
            });
        }
    }
}
