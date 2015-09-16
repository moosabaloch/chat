package pana.com.chat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class FriendsRequestFragment extends Fragment {

    ListView listView;
    ArrayList friendsID,friendsData,requestDate;
    Firebase pcchatapp;
    DataModelMeSingleton ME;
    ValueEventListener VEL1,VEL2;
    public FriendsRequestFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_friends_request, container, false);

        pcchatapp=new Firebase("https://pcchatapp.firebaseio.com/");

        ME=DataModelMeSingleton.getInstance();

        listView=(ListView) view.findViewById(R.id.friendrequestlistView);
        friendsData=new ArrayList();
        friendsID=new ArrayList();
        friendsID=new ArrayList();

        pcchatapp.child("friend_requests").child(ME.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VEL1=this;
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    friendsID.add(d.getKey().toString());
                    requestDate.add(d.getValue().toString());
                    pcchatapp.child("users").child(d.getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            VEL2=this;
                            DataModelUser dataModelUser=new DataModelUser();
                            friendsData.add(dataModelUser);
                            listView.setAdapter(new CustomFriendRequestAdapter(friendsID,friendsData,requestDate,getActivity()));
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