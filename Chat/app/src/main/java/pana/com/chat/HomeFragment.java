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
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment implements View.OnClickListener {
    DataModelMeSingleton ME;
    Firebase pcchatapp;
    ArrayList friendsID,conversationID;
    ArrayList<DataModelUser> friendsData;
    ListView listView;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        pcchatapp=new Firebase("https://pcchatapp.firebaseio.com/");

        friendsID=new ArrayList();
        conversationID=new ArrayList();
        friendsData=new ArrayList<DataModelUser>();

        ME=DataModelMeSingleton.getInstance();

        ((Button) view.findViewById(R.id.homebtngroups)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.homebtnfriend)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.homebtnrequest)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.homebtnlogout)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.homebtnprofile)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.homebtnprofile)).setText(ME.getName());

        listView= (ListView) view.findViewById(R.id.home_lv_chats);

        pcchatapp.child("user_friend").child(ME.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d:dataSnapshot.getChildren()){
                    HashMap<String,Object> hashMap=new HashMap<String, Object>();
                    hashMap= (HashMap<String, Object>) d.getValue();
                    Log.d("Home Fragment.....",d.getKey().toString());
                    Log.d("Home Fragment.....",hashMap.get("ConversationID").toString());
                    if (!hashMap.get("ConversationID").toString().equals("null")){
                        friendsID.add(d.getKey().toString());
                        conversationID.add(hashMap.get("ConversationID").toString());
                        pcchatapp.child("users").child(d.getKey().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DataModelUser dataModelUser=dataSnapshot.getValue(DataModelUser.class);
                                friendsData.add(dataModelUser);
                                listView.setAdapter(new CustomFriendsListAdapter(getActivity(),friendsID,friendsData));
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setFriendSingleton(i);
                goToChatFragment();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.homebtngroups:
                getFragmentManager().beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.fragment, new GroupFragment())
                        .commit();
                break;
            case R.id.homebtnfriend:
                getFragmentManager().beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.fragment,new FriendsFragment())
                        .commit();
                break;
            case R.id.homebtnrequest:
                getFragmentManager().beginTransaction()
                    .addToBackStack("")
                    .replace(R.id.fragment,new FriendsRequestFragment())
                    .commit();
                break;
            case R.id.homebtnprofile:
                LayoutInflater inflater=(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view2=inflater.inflate(R.layout.profiledialog,null);
                TextView name=(TextView) view2.findViewById(R.id.profiledialog_name);
                TextView email=(TextView) view2.findViewById(R.id.profiledialog_email);
                TextView phone=(TextView) view2.findViewById(R.id.profiledialog_phone);
                name.setText(ME.getName());
                email.setText(pcchatapp.getAuth().getProviderData().get("email").toString());
                phone.setText(ME.getPhone());
                AlertDialog alertDialog=new AlertDialog.Builder(getActivity()).create();
                alertDialog.setView(view2);
                alertDialog.show();
                break;
            case R.id.homebtnlogout:
                pcchatapp.unauth();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment,new LoginFragment())
                        .commit();
                break;
        }
    }

    private void setFriendSingleton(int i){
        DataModelFriendSingleTon friend = DataModelFriendSingleTon.getInstance();
        friend.setUuidUserFriend(friendsID.get(i).toString());
        friend.setEmailUserFriend(friendsData.get(i).getEmail_id());
        friend.setImageUrlUserFriend(friendsData.get(i).getImage_url());
        friend.setNameUserFriend(friendsData.get(i).getName());
        friend.setPhoneUserFriend(friendsData.get(i).getPhone());
        friend.setConversationID(conversationID.get(i).toString());
    }

    private void goToChatFragment() {
        getFragmentManager().beginTransaction()
                .addToBackStack("")
                .replace(R.id.fragment, new ChatFragment())
                .commit();
    }
}
