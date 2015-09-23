package pana.com.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddFriendFragment extends Fragment {

    DataModelMeSingleton ME;
    SimpleDateFormat sdf;
    private ListView listView;
    private ArrayList ids, myFriends;
    private ArrayList<DataModelUser> allUsers;
    private Firebase pcchatapp;
    private ValueEventListener VEL1, VEL2;
    private Picasso picasso;

    public AddFriendFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        picasso = Picasso.with(getActivity());
        pcchatapp = new Firebase("https://pcchatapp.firebaseio.com");

        ME = DataModelMeSingleton.getInstance();

        sdf = new SimpleDateFormat("dd-MM-yyyy");

        ids = new ArrayList();
        myFriends = new ArrayList();
        allUsers = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.addfriend_listview);

        pcchatapp.child("user_friend").child(pcchatapp.getAuth().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VEL1 = this;
                myFriends.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    myFriends.add(d.getKey());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        pcchatapp.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VEL2 = this;
                allUsers.clear();
                ids.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (!d.getKey().toString().equals(pcchatapp.getAuth().getUid()) && !isFriend(d.getKey().toString())) {

                        DataModelUser user = d.getValue(DataModelUser.class);

                        ids.add(d.getKey().toString());
                        allUsers.add(user);

                        listView.setAdapter(new CustomFriendsListAdapter(getActivity(), ids, allUsers));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        listView.setOnItemClickListener(new listview_OnItemClickListener());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("ADD FRIEND FRAGMENT", "OnDestroy");
        if (VEL1 != null)
            pcchatapp.child("user_friend").child(pcchatapp.getAuth().getUid()).removeEventListener(VEL1);
        if (VEL2 != null)
            pcchatapp.child("users").removeEventListener(VEL2);
    }

    private void backToFriendFragment() {
        getFragmentManager().popBackStack();
    }

    private boolean isFriend(String id) {
        boolean isFriend = false;
        Log.d("IsFriend", myFriends.size() + "");
        for (int i = 0; i < myFriends.size(); i++) {
            Log.d("IsFriend", id);
            Log.d("IsFriend", myFriends.get(i).toString());
            if (myFriends.get(i).toString().equals(id)) {
                Log.d("Isfriend", "true");
                isFriend = true;
                break;
            } else {
                isFriend = false;
            }
        }
        return isFriend;
    }

    private class listview_OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

            new AlertDialog.Builder(getActivity())
                    .setTitle("Add as Friend")
                    .setMessage("Are you sure you want to add " + allUsers.get(i).getName().toString() + "?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            HashMap<String, Object> hashMap = new HashMap<String, Object>();
                            hashMap.put("RequestDate", (sdf.format(Calendar.getInstance().getTime())).toString());

                            pcchatapp.child("friend_requests").child(ids.get(i).toString()).child(ME.getId()).setValue(hashMap, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    Log.d("message", "Request Sent");
                                }
                            });

                            /*HashMap<String, Object> hashMap = new HashMap<String, Object>();
                            hashMap.put("ConversationID", "null");

                            pcchatapp.child("user_friend").child(pcchatapp.getAuth().getUid()).child(ids.get(i).toString()).setValue(hashMap, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    Log.d("message", "Completed");
                                }
                            });

                            pcchatapp.child("user_friend").child(ids.get(i).toString()).child(pcchatapp.getAuth().getUid()).setValue(hashMap, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    Log.d("message", "Completed");
                                }
                            });*/

                            //backToFriendFragment();

                        }
                    })
                    .setNegativeButton("View Profile", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view2 = inflater.inflate(R.layout.profiledialog, null);
                            TextView name = (TextView) view2.findViewById(R.id.profiledialog_name);
                            TextView email = (TextView) view2.findViewById(R.id.profiledialog_email);
                            TextView phone = (TextView) view2.findViewById(R.id.profiledialog_phone);
                            ImageView imageView = (ImageView) view2.findViewById(R.id.profiledialog_imageview);
                            picasso.load(allUsers.get(i).getImage_url()).placeholder(R.drawable.friend).error(android.R.drawable.stat_sys_download).into(imageView);

                            name.setText(allUsers.get(i).getName());
                            email.setText(allUsers.get(i).getEmail_id());
                            phone.setText(allUsers.get(i).getPhone());
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setView(view2);
                            alertDialog.show();
                        }
                    }).show();
        }
    }

}
