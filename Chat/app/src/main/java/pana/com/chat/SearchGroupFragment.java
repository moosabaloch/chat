package pana.com.chat;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchGroupFragment extends Fragment implements GroupsViewAdaptor.GroupAdaptorAddEvent {

    private Firebase firebaseURL;
    private ImageButton addNewGroupButton;
    private ListView groupsList;
    private ArrayList<Groups> groupsArrayList;
    private ArrayList<String> myGroups;
    private ArrayList<String> arrayListForGroupKeys;
    private GroupsViewAdaptor groupsViewAdaptor;

    public SearchGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_group_fragmnent, container, false);
        addNewGroupButton = (ImageButton) view.findViewById(R.id.groupFragmentButtonAddNewGroup);
        groupsList = (ListView) view.findViewById(R.id.groupSearchFragmentListViewGroupsView);
        firebaseURL = new Firebase("https://pcchatapp.firebaseio.com");
        groupsArrayList = new ArrayList<>();
        myGroups = new ArrayList<>();
        arrayListForGroupKeys = new ArrayList<>();

        addGroup();
        checkMyGroups();
        return view;
    }

    private void checkMyGroups() {
        firebaseURL.child("mygroups").child(DataModelMeSingleton.getInstance().getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            myGroups.add(d.getValue().toString());
                        }
                        groupsListLoad();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
    }


    private void groupsListLoad() {

        firebaseURL.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshotForAllGroups) {
                groupsArrayList.clear();
                arrayListForGroupKeys.clear();
                for (DataSnapshot dataSnapshotForSingleGroup : dataSnapshotForAllGroups.getChildren()) {
                    if (!addedToGroup(dataSnapshotForSingleGroup.getKey())) {
                        Groups groups = dataSnapshotForSingleGroup.getValue(Groups.class);
                        arrayListForGroupKeys.add(dataSnapshotForSingleGroup.getKey());
                        groupsArrayList.add(groups);
                    }
                }
                loadGroupAdaptor();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private boolean addedToGroup(String key) {
        for (int i = 0; i < myGroups.size(); i++) {
            if (myGroups.get(i).equals(key)) {
                return true;
            }
        }
        return false;
    }

    private void loadGroupAdaptor() {
        groupsList.setAdapter(groupsViewAdaptor = new GroupsViewAdaptor(getActivity(), groupsArrayList, this, arrayListForGroupKeys));
    }

    private void addGroup() {
        addNewGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog builder = new AlertDialog.Builder(getActivity()).create();
                builder.setTitle("Add Group");
                View dialogView = View.inflate(getActivity(), R.layout.dialoglayoutview, null);
                final EditText name = (EditText) dialogView.findViewById(R.id.dialogEditTextGroupName);
                final EditText description = (EditText) dialogView.findViewById(R.id.dialogEditTextGroupDescription);
                builder.setView(dialogView);
                builder.setButton(DialogInterface.BUTTON_POSITIVE, "Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String myID = DataModelMeSingleton.getInstance().getId();
                        HashMap<String, String> admin = new HashMap<>();
                        admin.put("id", myID);
                        String groupName = name.getText().toString();
                        String groupDescription = description.getText().toString();
                        Firebase groupID = firebaseURL.child("groups").push();
                        String groupKey = groupID.getKey();
                        //Users in the Group....
                        firebaseURL.child("groupusers").child(groupKey).push().setValue(myID);
                        //Total groups associated with my account....
                        firebaseURL.child("mygroups").child(myID).push().setValue(groupKey);
                        //Create Group in the node group...
                        groupID.setValue(new Groups(groupName, "N/A", groupDescription, admin));
                        //Add group id to conversation node
                        firebaseURL.child("conversation").setValue(groupKey);
                        Toast.makeText(getActivity(), groupName + " created successfully as group.", Toast.LENGTH_LONG).show();
                    }
                });
                builder.show();
            }
        });
    }


    @Override
    public void addMeToThisGroup(String key, int position) {
        String myID = DataModelMeSingleton.getInstance().getId();
        //Add me to the Users in the Group....
        firebaseURL.child("groupusers").child(key).push().setValue(myID);
        //Add this group in my account....
        firebaseURL.child("mygroups").child(myID).push().setValue(key);
        Toast.makeText(getActivity(), "Joined Group", Toast.LENGTH_LONG).show();
        groupsArrayList.remove(position);
        arrayListForGroupKeys.remove(position);
        groupsViewAdaptor.notifyDataSetChanged();
    }
}
