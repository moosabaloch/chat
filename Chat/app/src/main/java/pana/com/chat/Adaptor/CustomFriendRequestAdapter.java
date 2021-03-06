package pana.com.chat.Adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import pana.com.chat.DataModel.DataModelMeSingleton;
import pana.com.chat.DataModel.DataModelUser;
import pana.com.chat.R;

/*
 */
public class CustomFriendRequestAdapter extends BaseAdapter {

    ArrayList friendsID, requestDate;
    ArrayList<DataModelUser> friendsData;
    Context context;
    Firebase pcchatapp;
    DataModelMeSingleton ME;
    Boolean check1 = false, check2 = false;

    Button accept, cancel;

    public CustomFriendRequestAdapter(ArrayList friendsID, ArrayList friendsData, ArrayList requestDate, Context context) {
        this.friendsID = friendsID;
        this.requestDate = requestDate;
        this.friendsData = friendsData;
        this.context = context;

    }

    @Override
    public int getCount() {
        return friendsData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View view1=null;
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view1= inflater.inflate(R.layout.friendsrequestlistview, null);

            pcchatapp = new Firebase("https://pcchatapp.firebaseio.com/");

            ME = DataModelMeSingleton.getInstance();

            TextView textView1 = (TextView) view1.findViewById(R.id.textView4);
            TextView textView2 = (TextView) view1.findViewById(R.id.textView5);
            ImageView imageView = (ImageView) view1.findViewById(R.id.imageViewFriendRequest);
            textView1.setText(friendsData.get(i).getName());

            textView2.setText(requestDate.get(i).toString());

            Picasso.with(context).load(friendsData.get(i).getImage_url()).into(imageView);

            accept = ((Button) view1.findViewById(R.id.friendreq_btn_accept));
            cancel = ((Button) view1.findViewById(R.id.friendreq_btn_cancel));

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    accept.setEnabled(false);
                    cancel.setEnabled(false);
                    Toast.makeText(context, "Please wait a minute...", Toast.LENGTH_LONG).show();

                    Log.d("Accept Button....", i + "");
                    final HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("ConversationID", "null");
                    pcchatapp.child("user_friend").child(ME.getId()).child(friendsID.get(i).toString()).setValue(hashMap, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            check1 = true;
                            pcchatapp.child("user_friend").child(friendsID.get(i).toString()).child(ME.getId()).setValue(hashMap, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    check2 = true;
                                    if (check1) {
                                        pcchatapp.child("friend_requests").child(ME.getId()).child(friendsID.get(i).toString()).removeValue(new Firebase.CompletionListener() {
                                            @Override
                                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                Toast.makeText(context, "Friend Added", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context, "Error Accepting Request", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    accept.setEnabled(false);
                    cancel.setEnabled(false);
                    Log.d("Cancel Button....", i + "");
                    pcchatapp.child("friend_requests").child(ME.getId()).child(friendsID.get(i).toString()).removeValue(new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Toast.makeText(context, "Request Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return view1;
    }
}
