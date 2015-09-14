package pana.com.chat;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {


    private ImageButton addNewGroupButton;
    private ListView groupsList;

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
        addGroup();
        return view;
    }

    private void addGroup() {
        addNewGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Group");

            }
        });
    }


}
