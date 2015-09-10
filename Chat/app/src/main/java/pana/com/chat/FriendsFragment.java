package pana.com.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsFragment extends Fragment {

    ListView listView;

    Button button;

    Firebase pcchatapp;

    ArrayList friendsID,friendsData;

    public FriendsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendsID=new ArrayList();
        friendsData=new ArrayList();
        pcchatapp=new Firebase("https://pcchatapp.firebaseio.com/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        listView = (ListView) view.findViewById(R.id.friends_listview);
        button = (Button) view.findViewById(R.id.friend_btn_addfriend);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager2 = getFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                AddFriendFragment addFriendsFragment = new AddFriendFragment();
                fragmentTransaction2.addToBackStack("");
                fragmentTransaction2.remove(FriendsFragment.this);
                fragmentTransaction2.add(android.R.id.content, addFriendsFragment);
                fragmentTransaction2.commit();
            }
        });
        pcchatapp.child("user_friend").child(pcchatapp.getAuth().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Friend Data",dataSnapshot.getValue().toString());
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    HashMap<String,Object> hashMap= (HashMap<String, Object>) d.getValue();
                    friendsID.add(hashMap.get("id"));
                    pcchatapp.child("users").child(hashMap.get("id").toString()).addValueEventListener(new ValueEventListener() {
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

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return view;
    }

}
