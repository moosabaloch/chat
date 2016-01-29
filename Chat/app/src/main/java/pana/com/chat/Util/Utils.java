package pana.com.chat.Util;

import android.content.Context;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;

import pana.com.chat.R;

/**
 * Created by Moosa on 9/12/2015.
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class Utils {
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String SENDER_ID = "1049880136181";
    public static final String SERVER_KEY_FOR_THIS_PROJECT = "AIzaSyBtPwemSZxg9qIJeN85LHZisVxJpozX1Hg";
    private static final String SERVER_CURRENT_IP = "https://safe-crag-3193.herokuapp.com";//"http://192.168.0.109:3000";
    //"https://safe-crag-3193.herokuapp.com"; //"http://10.105.19.124:3000";//"http://192.168.1.142:3000";
    public static final String SERVER_REGISTRATION_URL = SERVER_CURRENT_IP + "/gcm/register";
    public static final String SERVER_SEND_NOTIFICATION_URL = SERVER_CURRENT_IP + "/gcm/sendMessage";
    public static final String SEND_FRIEND_REQUEST = SERVER_CURRENT_IP + "/gcm/sendrequest";
    public static int TYPEGROUPSEARCH = 123;
    public static int TYPEMYGROUPS = 321;
    public static ArrayList<String> myGroups = new ArrayList<>();
    public static ArrayList<String> myFrindsId = new ArrayList<>();
    public static int dialogHW = 600;
    public static String MY_TAB_FRAG_OBJ = "tabfrag";
    private static Cloudinary cloudinary;
    public static String MY_APP_SHARED="com.pana.chat";
    public static String MY_APP_IS_RUNNING="myappboolean";

    public static void ToastLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void ToastShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean addedToGroup(String key) {
        for (int i = 0; i < myGroups.size(); i++) {
            if (myGroups.get(i).equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean friendIdAddedToMap(String key) {
        for (int i = 0; i < myFrindsId.size(); i++) {
            if (myFrindsId.get(i).equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static int getTimeDistanceInMinutes(long time) {
        long timeDistance = System.currentTimeMillis() - time;
        // DateUtils.formatElapsedTime(time);


        return Math.round((Math.abs(timeDistance) / 1000) / 60);

    }


    public static String getTimeAgo(Date date, Context ctx) {

        if (date == null) {
            return null;
        }

        long time = date.getTime();

        Date curDate = new Date();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        int dim = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (dim == 0 || dim < 0) {
            timeAgo = ctx.getResources().getString(R.string.date_util_term_less) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim == 1) {
            return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
        } else if (dim >= 45 && dim <= 89) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_an) + " " + ctx.getResources().getString(R.string.date_util_unit_hour);
        } else if (dim >= 90 && dim <= 1439) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 60)) + " " + ctx.getResources().getString(R.string.date_util_unit_hours);
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
        } else if (dim >= 2520 && dim <= 43199) {
            timeAgo = (Math.round(dim / 1440)) + " " + ctx.getResources().getString(R.string.date_util_unit_days);
        } else if (dim >= 43200 && dim <= 86399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_month);
        } else if (dim >= 86400 && dim <= 525599) {
            timeAgo = (Math.round(dim / 43200)) + " " + ctx.getResources().getString(R.string.date_util_unit_months);
        } else if (dim >= 525600 && dim <= 655199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
        }

        return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }

    public static Cloudinary cloudinary() {
        if (cloudinary == null) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "moosabaloch",
                    "api_key", "976698733356172",
                    "api_secret", "2mpFCUAhsCzasvMQWbcUPG0waJM"));
        }
        return cloudinary;
    }
}
