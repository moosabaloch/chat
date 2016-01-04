package pana.com.chat.ui.Fragments;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import pana.com.chat.Adaptor.CustomFriendsListAdapter;
import pana.com.chat.DataModel.DataModelFriendSingleTon;
import pana.com.chat.DataModel.DataModelMeSingleton;
import pana.com.chat.DataModel.DataModelUser;
import pana.com.chat.R;

public class FriendsFragment extends Fragment implements View.OnClickListener {

    DataModelMeSingleton ME;
    Boolean check1 = false, check2 = false;
    Button profile, delete, conversation;
    private ValueEventListener VEL;
    private ListView listView;
    private Button button1, button2;
    private Firebase pcchatapp;
    private ArrayList friendsID, friendsData, conversationID;

    public FriendsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        pcchatapp = new Firebase("https://pcchatapp.firebaseio.com/");

        ME = DataModelMeSingleton.getInstance();

        listView = (ListView) view.findViewById(R.id.friend_listView);
        button1 = (Button) view.findViewById(R.id.friend_btn_addfriend);
        button1.setOnClickListener(this);

        friendsID = new ArrayList();
        friendsData = new ArrayList();
        conversationID = new ArrayList();

        pcchatapp.child("user_friend").child(ME.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //     Log.d("Friend Fragment...", pcchatapp.getAuth().getUid());
                Log.d("Friend Fragment...", "getting User IDs");
                VEL = this;
                friendsData.clear();
                friendsID.clear();
                listView.setAdapter(new CustomFriendsListAdapter(getActivity(), friendsID, friendsData));
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        friendsID.add(d.getKey().toString());
                        HashMap<String, Object> hashMap = (HashMap<String, Object>) d.getValue();
                        conversationID.add(hashMap.get("ConversationID"));

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
                } else {
                    Log.d("Friend Fragment ", "No friend");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("Friends Fragment", firebaseError.getMessage());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final DataModelFriendSingleTon friend = DataModelFriendSingleTon.getInstance();

                DataModelUser dataModelUser = ((DataModelUser) friendsData.get(position));

                friend.setUuidUserFriend(friendsID.get(position).toString());
                friend.setEmailUserFriend(dataModelUser.getEmail_id());
                friend.setImageUrlUserFriend(dataModelUser.getImage_url());
                friend.setNameUserFriend(dataModelUser.getName());
                friend.setPhoneUserFriend(dataModelUser.getPhone());
                friend.setConversationID(conversationID.get(position).toString());

                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.alertadaptor, null);
                alertDialog.setView(v);
                alertDialog.show();

                profile = (Button) v.findViewById(R.id.dialog_profilebtn);
                profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        profile.setEnabled(false);
                        conversation.setEnabled(false);
                        delete.setEnabled(false);
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view2 = inflater.inflate(R.layout.profiledialog, null);
                        TextView name = (TextView) view2.findViewById(R.id.profiledialog_name);
                        TextView email = (TextView) view2.findViewById(R.id.profiledialog_email);
                        TextView phone = (TextView) view2.findViewById(R.id.profiledialog_phone);
                        ImageView profilePic = (ImageView) view2.findViewById(R.id.profiledialog_imageview);
                        name.setText(friend.getNameUserFriend());
                        email.setText(friend.getEmailUserFriend());
                        phone.setText(friend.getPhoneUserFriend());
                        Picasso.with(getActivity()).load(friend.getImageUrlUserFriend()).placeholder(R.drawable.friend).error(R.drawable.friend).into(profilePic);


                        AlertDialog alertDialogg = new AlertDialog.Builder(getActivity()).create();
                        alertDialogg.setView(view2);
                        alertDialogg.show();
                        alertDialog.dismiss();
                    }
                });

                conversation = (Button) v.findViewById(R.id.dialog_convobtn);
                conversation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        profile.setEnabled(false);
                        conversation.setEnabled(false);
                        delete.setEnabled(false);
                        getFragmentManager().beginTransaction()
                                .addToBackStack("")
                                .replace(R.id.fragment, new ChatFragment())
                                .commit();
                        alertDialog.dismiss();
                    }
                });

                delete = (Button) v.findViewById(R.id.dialog_deletebtn);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        profile.setEnabled(false);
                        conversation.setEnabled(false);
                        delete.setEnabled(false);
                        pcchatapp.child("user_friend").child(ME.getId()).child(friend.getUuidUserFriend()).removeValue(new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                check1 = true;
                                if (check1) {
                                    pcchatapp.child("user_friend").child(friend.getUuidUserFriend()).child(ME.getId()).removeValue(new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            check2 = true;
                                            if (check1 && check2) {
                                                Toast.makeText(getActivity(), "Friend Removed", Toast.LENGTH_SHORT).show();
                                                alertDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            }
                        });
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
        if (VEL != null && pcchatapp.getAuth() != null)
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
        }

    }
}
