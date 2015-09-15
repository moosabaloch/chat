package pana.com.chat;


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


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchGroupFragment extends Fragment {

    private Firebase firebaseURL;
    private ImageButton addNewGroupButton;
    private ListView groupsList;
    private ArrayList<Groups> groupsArrayList;


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
        addGroup();
        checkMyGroups();
        return view;
    }

    private void checkMyGroups() {
        firebaseURL.child("mygroups").child(DataModelMeSingleton.getInstance().getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getValue();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Log.d("Check My Groups", "Values " + d.getValue().toString());

                }
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

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void loadGroupAdaptor() {
        groupsList.setAdapter(new GroupsViewAdaptor(getActivity(), groupsArrayList));
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
                        Toast.makeText(getActivity(), groupName + " created successfully as group.", Toast.LENGTH_LONG).show();
                    }
                });
                builder.show();
            }
        });
    }


}
