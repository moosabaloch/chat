package pana.com.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class FriendsFragment extends Fragment implements View.OnClickListener {

    private ListView listView;

    private Button button;

    private Firebase pcchatapp;

    private ArrayList friendsID, friendsData;

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
        button = (Button) view.findViewById(R.id.friend_btn_addfriend);
        button.setOnClickListener(this);

        friendsID = new ArrayList();
        friendsData = new ArrayList();

        pcchatapp.child("user_friend").child(pcchatapp.getAuth().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Friend Data", dataSnapshot.getValue().toString());
                friendsID.clear();
                friendsData.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) d.getValue();
                    Log.d("Id id ", "---> " + hashMap.get("id"));
                    friendsID.add(hashMap.get("id"));
                    pcchatapp.child("users").child(hashMap.get("id").toString()).addValueEventListener(new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        friendsData = null;
        friendsID = null;
    }

    @Override
    public void onClick(View view) {
        getFragmentManager().beginTransaction()
                .addToBackStack("")
                .replace(R.id.fragment, new AddFriendFragment())
                .commit();
    }
}
