package pana.com.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {
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

        ((TextView) view.findViewById(R.id.home_tv_name)).setText(ME.getName());
        ((TextView) view.findViewById(R.id.home_tv_email)).setText(pcchatapp.getAuth().getProviderData().get("email").toString());
        ((TextView) view.findViewById(R.id.home_tv_phone)).setText(ME.getPhone());
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

        ((Button) view.findViewById(R.id.homebtngroups)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.fragment, new GroupFragment())
                        .commit();
            }
        });

        ((Button) view.findViewById(R.id.homebtnfriend)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.fragment,new FriendsFragment())
                        .commit();
            }
        });

        ((Button) view.findViewById(R.id.homebtnrequest)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Request Module
            }
        });



        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
