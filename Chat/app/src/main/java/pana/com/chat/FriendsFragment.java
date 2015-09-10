package pana.com.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    View view;

    ListView listView;

    ImageView imageView;

    TextView tv;

    Button button;

    Firebase pcchatapp;

    public FriendsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                fragmentTransaction2.hide(FriendsFragment.this);
                fragmentTransaction2.add(android.R.id.content, addFriendsFragment);
                fragmentTransaction2.commit();
            }
        });
        pcchatapp.child("user_friend").child(pcchatapp.getAuth().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
        });
        ArrayList arrayList = new ArrayList();
        //arrayList.add("Moosa Baloch");
        //arrayList.add("ZeeshanHanif");
        //listView.setAdapter(new CustomFriendsListAdapter(getActivity(), arrayList));
        return view;
    }

}
