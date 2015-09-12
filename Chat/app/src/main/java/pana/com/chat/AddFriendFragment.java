package pana.com.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AddFriendFragment extends Fragment {

    private ListView listView;
    private ArrayList ids, myFriends;
    private ArrayList<DataModelUser> allUsers;
    private Firebase pcchatapp;
    private ValueEventListener VEL1,VEL2;

    public AddFriendFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pcchatapp = new Firebase("https://pcchatapp.firebaseio.com");

        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);

        ids = new ArrayList();
        myFriends = new ArrayList();
        allUsers = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.addfriend_listview);

        pcchatapp.child("user_friend").child(pcchatapp.getAuth().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VEL1=this;
                Log.d("ADD FRIEND FRAGMENT_USER FRIENDS","On data changed");
                myFriends.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) d.getValue();
                    myFriends.add(hashMap.get("id"));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        pcchatapp.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VEL2=this;
                Log.d("ADD FRIEND FRAGMENT_ALL USERS","On data changed");
                allUsers.clear();
                ids.clear();
                for(DataSnapshot d:dataSnapshot.getChildren()){
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
        Log.d("ADD FRIEND FRAGMENT","OnDestroy");
        if(VEL1!=null)
            pcchatapp.child("user_friend").child(pcchatapp.getAuth().getUid()).removeEventListener(VEL1);
        if(VEL2!=null)
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

    private class pcchatapp_users_ChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (!dataSnapshot.getKey().toString().equals(pcchatapp.getAuth().getUid()) && !isFriend(dataSnapshot.getKey().toString())) {
                //Fetching data into DataModelUser
                DataModelUser user = dataSnapshot.getValue(DataModelUser.class);
                //Initializing ids and allUsers arraylist
                ids.add(dataSnapshot.getKey().toString());
                allUsers.add(user);
                //Setting CustomFriendListAdapter to ListView
                listView.setAdapter(new CustomFriendsListAdapter(getActivity(), ids, allUsers));
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    private class listview_OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

            new AlertDialog.Builder(getActivity())
                    .setTitle("Add as Friend")
                    .setMessage("Are you sure you want to add " + allUsers.get(i).getName().toString() + "?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Adding Selected User's ID to My Friend's List
                            HashMap<String, Object> hashMap = new HashMap<String, Object>();
                            hashMap.put("id", ids.get(i).toString());
                            Log.d("IDDDDDDD", hashMap.toString());
                            pcchatapp.child("user_friend").child(pcchatapp.getAuth().getUid()).push().setValue(hashMap, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    Log.d("message", "Completed");
                                }
                            });
                            //Adding Current User's ID to Selected User's Friend's List
                            hashMap.put("id", pcchatapp.getAuth().getUid());
                            Log.d("IDDDDDDD", hashMap.toString());
                            pcchatapp.child("user_friend").child(ids.get(i).toString()).push().setValue(hashMap, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    Log.d("message", "Completed");
                                }
                            });
                            //Going Back to FriendFragment
                            backToFriendFragment();

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    }).show();
        }
    }

    private class pcchatapp_user_friend_ChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) dataSnapshot.getValue();
            myFriends.add(hashMap.get("id").toString());
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }



}
