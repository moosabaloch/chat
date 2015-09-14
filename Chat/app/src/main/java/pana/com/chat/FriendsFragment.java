package pana.com.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsFragment extends Fragment implements View.OnClickListener {

    ValueEventListener VEL;
    private ListView listView;
    private Button button1, button2;
    private Firebase pcchatapp;
    private ArrayList friendsID,friendsConversationID, friendsData;

    public FriendsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pcchatapp = new Firebase("https://pcchatapp.firebaseio.com/");

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        listView = (ListView) view.findViewById(R.id.friends_listview);
        button1 = (Button) view.findViewById(R.id.friend_btn_addfriend);
        button2 = (Button) view.findViewById(R.id.friend_btn_logout);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

        friendsID = new ArrayList();
        friendsData = new ArrayList();
        friendsConversationID=new ArrayList();

        pcchatapp.child("user_friend").child(pcchatapp.getAuth().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VEL = this;
                friendsData.clear();
                friendsID.clear();
                listView.setAdapter(new CustomFriendsListAdapter(getActivity(), friendsID, friendsData));
                try {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {

                        friendsID.add(d.getKey().toString());
                        friendsConversationID.add(((HashMap<String, Object>) d.getValue()).get("ConversationID"));

                        pcchatapp.child("users").child(d.getKey().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DataModelUser dataModelUser = dataSnapshot.getValue(DataModelUser.class);
                                friendsData.add(dataModelUser);
                                listView.setAdapter(new CustomFriendsListAdapter(getActivity(), friendsID, friendsData));
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "No Friend Added", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getActivity(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModelFriendSingleTon friend = DataModelFriendSingleTon.getInstance();

                DataModelUser dataModelUser = ((DataModelUser) friendsData.get(position));

                friend.setUuidUserFriend(friendsID.get(position).toString());

                friend.setEmailUserFriend(dataModelUser.getEmail_id());
                friend.setImageUrlUserFriend(dataModelUser.getImage_url());
                friend.setNameUserFriend(dataModelUser.getName());
                friend.setPhoneUserFriend(dataModelUser.getPhone());
                friend.setConversationID(friendsConversationID.get(position).toString());

                Log.d("Data in Friend Sngltn", "-->" + friend.toString());


                LayoutInflater inflater1=(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView=inflater1.inflate(R.layout.alertadaptor,null);

                final AlertDialog builder=new AlertDialog.Builder(getActivity()).create();
                builder.setView(dialogView);
                builder.show();

                ((Button) dialogView.findViewById(R.id.dialog_profilebtn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("Profile Button", "Clicked");
                    }
                });

                ((Button) dialogView.findViewById(R.id.dialog_convobtn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("Conversation Button","Clicked");
                        getFragmentManager().beginTransaction()
                                .addToBackStack("")
                                .replace(R.id.fragment, new ChatFragment())
                                .commit();
                        builder.dismiss();

                    }
                });

                ((Button) dialogView.findViewById(R.id.dialog_deletebtn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("Delete Button","Clicked");
                    }
                });

            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("FRIEND FRAGMENT", "OnDestroy");
        if(VEL!=null)
            pcchatapp.child("user_friend").child(pcchatapp.getAuth().getUid()).removeEventListener(VEL);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.friend_btn_addfriend:
                getFragmentManager().beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.fragment, new AddFriendFragment())
                        .commit();
                break;
            case R.id.friend_btn_logout:

                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new LoginFragment())
                        .commit();
                pcchatapp.unauth();
                break;
        }

    }
}
