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
public class GroupFragment extends Fragment {

    private Firebase firebaseURL;
    private ImageButton addNewGroupButton;
    private ListView groupsList;
    private ArrayList<Groups> groupsArrayList;

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        addNewGroupButton = (ImageButton) view.findViewById(R.id.groupFragmentButtonAddNewGroup);
        groupsList = (ListView) view.findViewById(R.id.groupFragmentListViewGroupsView);
        firebaseURL = new Firebase("https://pcchatapp.firebaseio.com");
        groupsArrayList = new ArrayList<>();
        addGroup();
        groupsListLoad();
        return view;
    }

    private void groupsListLoad() {

        firebaseURL.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Groups group = snapshot.getValue(Groups.class);
                        groupsArrayList.add(group);
                    }
                    loadGroupAdaptor();
                }
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
                        HashMap<String, String> admin = new HashMap<>();
                        admin.put("id", DataModelMeSingleton.getInstance().getId());
                        String groupName = name.getText().toString();
                        String groupDescription = description.getText().toString();
                        firebaseURL.child("groups").push().setValue(new Groups(groupName, "N/A", groupDescription, admin));
                        Toast.makeText(getActivity(), groupDescription + " " + groupName, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


}
