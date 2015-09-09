package pana.com.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;

public class AddFriendFragment extends Fragment {

    ListView listView;
    View view;
    ArrayList ids, names, images;
    Firebase ref;

    public AddFriendFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ids = new ArrayList();
        names = new ArrayList();
        images = new ArrayList();
        ref = new Firebase("https://pcchatapp.firebaseio.com/users");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        listView = (ListView) view.findViewById(R.id.addfriend_listview);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataModelUser user = dataSnapshot.getValue(DataModelUser.class);
                ids.add(dataSnapshot.getKey().toString());
                names.add(user.getName());
                images.add(user.getImage_url());
                listView.setAdapter(new CustomFriendsListAdapter(getActivity(), names));
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Add as Friend")
                        .setMessage("Are you sure you want to add " + names.get(i).toString() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                hashMap.put("id", ids.get(i).toString());
                                ref.child(ref.getAuth().getUid()).child("friends").push().setValue(hashMap);
                                ref.child(ids.get(i).toString()).child("friends").push().setValue(new HashMap<String, Object>().put("id", ref.getAuth().getUid()));

                                backToFriendFragment();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        }).show();
            }
        });
        return view;
    }

    private void backToFriendFragment() {
        FragmentManager fragmentManager2 = getFragmentManager();
        FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
        FriendsFragment friendsFragment = new FriendsFragment();
        fragmentTransaction2.addToBackStack("");
        fragmentTransaction2.hide(AddFriendFragment.this);
        fragmentTransaction2.add(android.R.id.content, friendsFragment);
        fragmentTransaction2.commit();
    }
}
