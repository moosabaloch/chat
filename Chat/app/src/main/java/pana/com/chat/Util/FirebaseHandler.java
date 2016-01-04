package pana.com.chat.Util;

import com.firebase.client.Firebase;

/**
 * Created by Moosa on 1/5/2016.
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class FirebaseHandler {
    private static FirebaseHandler firebaseHandler;
    private Firebase firebaseURL;
    private Firebase firebaseUsersURL;

    private FirebaseHandler() {
        firebaseURL = new Firebase("https://pcchatapp.firebaseio.com/");
        initialise();
    }

    public static FirebaseHandler getInstance() {
        if (firebaseHandler == null) {
            firebaseHandler = new FirebaseHandler();
        }

        return firebaseHandler;
    }

    private void initialise() {
        firebaseUsersURL = firebaseURL.child("users");
    }

    public Firebase getFirebaseBaseURL() {
        return firebaseURL;
    }

    public Firebase getFirebaseUsersURL() {
        return firebaseUsersURL;
    }
}
