package pana.com.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class HomeFragment extends Fragment {
    DataModelMeSingleton ME;
    Firebase pcchatapp;
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

        ME=DataModelMeSingleton.getInstance();

        ((TextView) view.findViewById(R.id.home_tv_name)).setText(ME.getName());
        ((TextView) view.findViewById(R.id.home_tv_email)).setText(pcchatapp.getAuth().getProviderData().get("email").toString());
        ((TextView) view.findViewById(R.id.home_tv_phone)).setText(ME.getPhone());

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
