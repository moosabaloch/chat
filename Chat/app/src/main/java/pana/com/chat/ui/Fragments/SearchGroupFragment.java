package pana.com.chat.ui.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import pana.com.chat.DataModel.DataModelMeSingleton;
import pana.com.chat.DataModel.Groups;
import pana.com.chat.Adaptor.GroupsViewAdaptor;
import pana.com.chat.DataModel.Messages;
import pana.com.chat.R;
import pana.com.chat.Util.Utils;


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
                    if (!Utils.addedToGroup(dataSnapshotForSingleGroup.getKey())) {
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


    private void loadGroupAdaptor() {
        try {
            groupsList.setAdapter(groupsViewAdaptor = new GroupsViewAdaptor(getActivity(), groupsArrayList, this, arrayListForGroupKeys, Utils.TYPEGROUPSEARCH,"#"));

        } catch (Exception ex) {
            Log.d("Exception ", "" + ex.getMessage());
            ex.printStackTrace();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);

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
                        final String groupName = name.getText().toString();
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
                        firebaseURL.child("conversation").child(groupKey).push().setValue(new Messages(String.valueOf(System.currentTimeMillis()), "Group Created By " + DataModelMeSingleton.getInstance().getName(), DataModelMeSingleton.getInstance().getId()), new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                Toast.makeText(getActivity(), groupName + " created successfully as group.", Toast.LENGTH_LONG).show();
                                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                            }
                        });
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
        Utils.addedToGroup(arrayListForGroupKeys.get(position));
        groupsArrayList.remove(position);
        arrayListForGroupKeys.remove(position);
        groupsViewAdaptor.notifyDataSetChanged();
    }
}
