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

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;

public class AddFriendFragment extends Fragment {

    ListView listView;
    View view;
    ArrayList ids,myFriends;
    ArrayList<DataModelUser> allUsers;
    Firebase pcchatapp;

    public AddFriendFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ids = new ArrayList();
        myFriends = new ArrayList();
        allUsers=new ArrayList<>();
        pcchatapp = new Firebase("https://pcchatapp.firebaseio.com");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        //Initializing ListView
        listView = (ListView) view.findViewById(R.id.addfriend_listview);
        //Authenticating current User and Getting All Users
        pcchatapp.authWithPassword("abdulbst1994@gmail.com","aybee",new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                pcchatapp.child("user_friend").child(pcchatapp.getAuth().getUid()).addChildEventListener(new pcchatapp_user_friend_ChildEventListener());
                pcchatapp.child("users").addChildEventListener(new pcchatapp_users_ChildEventListener());
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Log.d("Errorr",firebaseError.getMessage());
                //Toast.makeText(getActivity(),firebaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        //Setting ListView OnClickListener
        listView.setOnItemClickListener(new listview_OnItemClickListener());

        return view;
    }

    private void backToFriendFragment() {
        getFragmentManager()
                .beginTransaction()
                .addToBackStack("")
                .hide(AddFriendFragment.this)
                .add(android.R.id.content, new FriendsFragment())
                .commit();
    }

    private boolean isFriend(String id){
        boolean isFriend=false;
        for (int i=0;i<myFriends.size();i++){
            if (myFriends.get(i).toString().equals(id)){
                isFriend=true;
                break;
            }
            else {
                isFriend=false;
            }
        }
        return isFriend;
    }

    private class pcchatapp_users_ChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (!dataSnapshot.getKey().toString().equals(pcchatapp.getAuth().getUid())&&!isFriend(dataSnapshot.getKey().toString())){
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
    };

    private class listview_OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {

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
    };

    private class pcchatapp_user_friend_ChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            myFriends.add(dataSnapshot.getValue().toString());
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
