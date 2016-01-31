package pana.com.chat.ui.Fragments;

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
import pana.com.chat.Util.Utils;

public class HomeFragment extends Fragment implements View.OnClickListener {
    Fragment fragment;
    private DataModelMeSingleton ME;
    private Firebase pcchatapp;
    private ArrayList friendsID, conversationID;
    private ArrayList<DataModelUser> friendsData;
    private ListView listView;
    private Button btn_profile, btn_groups, btn_friends, btn_requests, btn_logout;
    private TextView tv;
    private String TAG = "HOME FRAGMENT.....";
    private int count;
    private ImageView imageView;
    private Picasso picasso;
    private HomeFragInter homeFragInter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            homeFragInter = (HomeFragInter) getActivity();
            Log.d("HomeFrag", "onCreate");
        } catch (Exception ex) {
            Utils.ToastLong(getActivity(), "Sorry we are facing a problem please try again later");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("HomeFrag", "onCreateView");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        pcchatapp = new Firebase("https://pcchatapp.firebaseio.com/");
        picasso = Picasso.with(getActivity());

        friendsID = new ArrayList();
        conversationID = new ArrayList();
        friendsData = new ArrayList<DataModelUser>();

        ME = DataModelMeSingleton.getInstance();

        tv = (TextView) view.findViewById(R.id.hometxtconvo);

/*
        btn_profile = (Button) view.findViewById(R.id.homebtnprofile);
        btn_groups = (Button) view.findViewById(R.id.homebtngroups);
        btn_friends = (Button) view.findViewById(R.id.homebtnfriend);
        btn_requests = (Button) view.findViewById(R.id.homebtnrequest);
        btn_logout = (Button) view.findViewById(R.id.homebtnlogout);

        btn_groups.setOnClickListener(this);
        btn_friends.setOnClickListener(this);
        btn_requests.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_profile.setOnClickListener(this);
        btn_profile.setText(ME.getName());
*/

        listView = (ListView) view.findViewById(R.id.home_lv_chats);
//
        try {
            pcchatapp.child("user_friend").child(ME.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "ON DATA CHANGE");
                    Log.d(TAG, "MY ID:" + ME.getId());

                    friendsData.clear();
                    friendsID.clear();
                    conversationID.clear();
                    count = 0;
                    tv.setText(count + " Conversations");
                    listView.setAdapter(new CustomFriendsListAdapter(getActivity(), friendsID, friendsData, "#"));
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            HashMap<String, Object> hashMap = (HashMap<String, Object>) d.getValue();
                            if (!hashMap.get("ConversationID").toString().equals("null")) {
                                count = count + 1;
                                tv.setText(count + " Conversations");
                                Log.d(TAG, "FRIEND ID:" + d.getKey().toString());
                                Log.d(TAG, "CONVERSATION ID:" + hashMap.get("ConversationID").toString());
                                friendsID.add(d.getKey().toString());
                                conversationID.add(hashMap.get("ConversationID").toString());
                                pcchatapp.child("users").child(d.getKey().toString()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        DataModelUser dataModelUser = dataSnapshot.getValue(DataModelUser.class);
                                        friendsData.add(dataModelUser);
                                        listView.setAdapter(new CustomFriendsListAdapter(getActivity(), friendsID, friendsData, "#"));
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        }
                    } else {
                        listView.setAdapter(new CustomFriendsListAdapter(getActivity(), friendsID, friendsData, "#"));
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        setFriendSingleton(i);
                        goToChatFragment();
                    } catch (Exception ex) {
                        Utils.ToastLong(getActivity(), "Sorry you have Slow internet connection");
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        homeFragInter.registerGCMService();

        return view;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        Log.d("HomeFrag", "onDestroyView");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
      /*      case R.id.homebtngroups:
                getFragmentManager().beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.fragment, new GroupFragment())
                        .commit();
                break;
            case R.id.homebtnfriend:
                getFragmentManager().beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.fragment, new FriendsFragment())
                        .commit();
                break;
            case R.id.homebtnrequest:
                getFragmentManager().beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.fragment, new FriendsRequestFragment())
                        .commit();
                break;
            case R.id.homebtnprofile:
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view2 = inflater.inflate(R.layout.profiledialog, null);
                TextView name = (TextView) view2.findViewById(R.id.profiledialog_name);
                TextView email = (TextView) view2.findViewById(R.id.profiledialog_email);
                TextView phone = (TextView) view2.findViewById(R.id.profiledialog_phone);
                imageView = (ImageView) view2.findViewById(R.id.profiledialog_imageview);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        performSelect();
                    }
                });
                name.setText(ME.getName());
                email.setText(ME.getEmail());
                phone.setText(ME.getPhone());
//                Picasso Implementation
                picasso.load(ME.getImageUrl()).placeholder(R.drawable.friend).error(R.drawable.friend).into(imageView);
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setView(view2);
                alertDialog.show();
                break;
            case R.id.homebtnlogout:
                pcchatapp.unauth();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new LoginFragment())
                        .commit();
                break;
      */
        }
    }

    private void setFriendSingleton(int i) {
        try {
            DataModelFriendSingleTon friend = DataModelFriendSingleTon.getInstance();
            friend.setUuidUserFriend(friendsID.get(i).toString());
            friend.setEmailUserFriend(friendsData.get(i).getEmail_id());
            friend.setImageUrlUserFriend(friendsData.get(i).getImage_url());
            friend.setNameUserFriend(friendsData.get(i).getName());
            friend.setPhoneUserFriend(friendsData.get(i).getPhone());
            friend.setConversationID(conversationID.get(i).toString());
        } catch (Exception ex) {
            Utils.ToastLong(getActivity(), "Sorry you have Slow internet connection");

        }
    }

    private void goToChatFragment() {
        getFragmentManager().beginTransaction()
                .addToBackStack("")
                .add(R.id.homeActivityContent, new ChatFragment())

//                .replace(R.id.homeActivityContent, new ChatFragment())
                .commit();
    }


    public interface HomeFragInter {
        void registerGCMService();
    }

}
