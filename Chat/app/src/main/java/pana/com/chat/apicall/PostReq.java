package pana.com.chat.apicall;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import pana.com.chat.AppController;
import pana.com.chat.Util.Utils;

/**
 * Created by Moosa on 1/1/2016.
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class PostReq {
    private static PostReq myInstance;

    public static PostReq getMyInstance() {
        if (myInstance == null) {
            myInstance = new PostReq();
        }
        return myInstance;
    }

    public void notifySingleUser(String userID, String msg, String notificationTitle) {
        Map<String, String> map = new HashMap<>();
        map.put("user", userID);
        map.put("notificationTitle", notificationTitle);
        map.put("msg", msg);
        AppController.getInstance().addToRequestQueue(new MyRequest(map, Utils.SERVER_SEND_NOTIFICATION_URL));
        Log.d("notifySingleUser()","Invoked:"+notificationTitle+" -> "+msg);
    }


    public void registerID(String token) {
        //Single Registration Data POST
        Map<String, String> map = new HashMap<>();
        map.put("registrationId", token);
        AppController.getInstance().addToRequestQueue(new MyRequest(map, Utils.SERVER_REGISTRATION_URL));

    }

    private class MyRequest extends StringRequest {
        private Map<String, String> postData;

        public MyRequest(Map<String, String> postData, String url) {
            super(Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Volley POST Response", "Message: " + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley POST Error", "Message: " + error.getMessage());
                }
            });
            this.postData = postData;
        }

        @Override
        protected Map<String, String> getParams() {
            Log.i("Map Post data",""+postData.toString());
            return postData;
        }
    }
}

