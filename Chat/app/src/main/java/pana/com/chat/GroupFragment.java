package pana.com.chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
    ImageButton addNewSearchGroupFragmentButton;

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        addNewSearchGroupFragmentButton = (ImageButton) view.findViewById(R.id.groupFragmentButtonAddNewGroup);
        addNewSearchGroupFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, new SearchGroupFragment()).commit();
            }
        });
        return view;
    }

}
