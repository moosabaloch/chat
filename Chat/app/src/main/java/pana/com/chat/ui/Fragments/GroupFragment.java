package pana.com.chat.ui.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import pana.com.chat.Adaptor.GroupsViewAdaptor;
import pana.com.chat.DataModel.DataModelCurrentGroupChat;
import pana.com.chat.DataModel.DataModelMeSingleton;
import pana.com.chat.DataModel.Groups;
import pana.com.chat.R;
import pana.com.chat.Util.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment implements GroupsViewAdaptor.GroupAdaptorAddEvent {
    //  private ImageButton addNewSearchGroupFragmentButton;
    private ListView myGroupsListView;
    private Firebase firebaseURL;
    private GroupsViewAdaptor groupsViewAdaptor;
    private ArrayList<Groups> myJoinedGroups;
    private FloatingActionButton fab;
    private String sender = "#";

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        if (getArguments() != null) {
            sender = getArguments().getString("sender") != null ? getArguments().getString("sender") : "#";
        }
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        firebaseURL = new Firebase("https://pcchatapp.firebaseio.com");
      /*  addNewSearchGroupFragmentButton = (ImageButton) view.findViewById(R.id.groupFragmentButtonAddNewGroup);
        addNewSearchGroupFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
      */
        myJoinedGroups = new ArrayList<>();
        myGroupsListView = (ListView) view.findViewById(R.id.myGroupFragmentListViewGroupsView);
        checkMyGroups();

        return view;
    }

    private void checkMyGroups() {
        Log.d("CHECK MY GROUPS", "Invoked");
        firebaseURL.child("mygroups").child(DataModelMeSingleton.getInstance().getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot allGroupsID) {
                        if (allGroupsID.hasChildren()) {
                            Log.d("CHECK MY GROUPS", "Inside onDataChanged Clear List");
                            Utils.myGroups.clear();
                            myJoinedGroups.clear();
                            for (DataSnapshot groupUniqueID : allGroupsID.getChildren()) {
                                Log.d("CHECK MY GROUPS", "Inside For Loop myGroups " + groupUniqueID.getValue());

                                Utils.myGroups.add(groupUniqueID.getValue().toString());
                                firebaseURL.child("groups").child(groupUniqueID.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot singleGroupSnapshot) {
                                        if (singleGroupSnapshot.hasChildren()) {
                                            Log.d("CHECK MY GROUPS", "Inside inner OnDataChanged");

                                            Groups myGroups = singleGroupSnapshot.getValue(Groups.class);
                                            myJoinedGroups.add(myGroups);
                                            Log.d("CHECK MY GROUPS", "Group Added " + myGroups.getGroupName());
                                            refreshAdaptor();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                        Utils.ToastLong(getActivity(), "Error Loading Group : " + firebaseError.getMessage());
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Utils.ToastLong(getActivity(), "Error Loading Group : " + firebaseError.getMessage());

                    }
                });
    }

    private void refreshAdaptor() {
        Log.d("Refresh Adaptor", "Invoked");

        myGroupsListView.setAdapter(groupsViewAdaptor = new GroupsViewAdaptor(getActivity(), myJoinedGroups, this, Utils.myGroups, Utils.TYPEMYGROUPS, sender));
        Log.d("Finish", "Groups Added " + myJoinedGroups.size());
        myGroupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sender = "#";
                myGroupsListView.setAdapter(groupsViewAdaptor = new GroupsViewAdaptor(getActivity(), myJoinedGroups, this, Utils.myGroups, Utils.TYPEMYGROUPS, sender));

                DataModelCurrentGroupChat groupChatDetail = DataModelCurrentGroupChat.getInstance();
                groupChatDetail.setGroupIDKEY(Utils.myGroups.get(position));
                groupChatDetail.setGroupDescription(myJoinedGroups.get(position).getGroupDescription());
                groupChatDetail.setGroupName(myJoinedGroups.get(position).getGroupName());
                groupChatDetail.setImageUrl(myJoinedGroups.get(position).getGroupImage());
                fab.setVisibility(View.GONE);
                getFragmentManager().beginTransaction().addToBackStack("").add(R.id.homeActivityContent, new GroupChatFragment()).commit();

            }
        });

    }

    @Override
    public void addMeToThisGroup(String key, int position) {

    }

}
