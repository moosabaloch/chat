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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsRequestFragment extends Fragment {

    ListView listView;
    ArrayList friendsID,requestDate;
    ArrayList<DataModelUser> friendsData;
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view2 = inflater.inflate(R.layout.profiledialog, null);
                TextView name = (TextView) view2.findViewById(R.id.profiledialog_name);
                TextView email = (TextView) view2.findViewById(R.id.profiledialog_email);
                TextView phone = (TextView) view2.findViewById(R.id.profiledialog_phone);
                ImageView imageView=(ImageView) view2.findViewById(R.id.profiledialog_imageview);
                name.setText(friendsData.get(i).getName());
                email.setText(friendsData.get(i).getEmail_id());
                phone.setText(friendsData.get(i).getPhone());
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setView(view2);
                alertDialog.show();
            }
        });
        friendsData=new ArrayList<DataModelUser>();
        friendsID=new ArrayList();
        requestDate=new ArrayList();

        pcchatapp.child("friend_requests").child(ME.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Friend Request","On Data Changed Called");
                VEL1=this;
                friendsData.clear();
                friendsID.clear();
                requestDate.clear();
                listView.setAdapter(new CustomFriendRequestAdapter(friendsID,friendsData,requestDate,getActivity()));
                if (dataSnapshot.hasChildren()){
                    for(DataSnapshot d:dataSnapshot.getChildren()){
                        friendsID.add(d.getKey().toString());
                        requestDate.add(((HashMap<String,Object>)d.getValue()).get("RequestDate"));
                        pcchatapp.child("users").child(d.getKey().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                VEL2=this;
                                DataModelUser dataModelUser=dataSnapshot.getValue(DataModelUser.class);
                                friendsData.add(dataModelUser);
                                listView.setAdapter(new CustomFriendRequestAdapter(friendsID,friendsData,requestDate,getActivity()));
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
                else {
                    Log.d("Requests","No requests");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return view;
    }
}
