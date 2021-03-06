package pana.com.chat.ui.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import pana.com.chat.Adaptor.CustomFriendRequestAdapter;
import pana.com.chat.DataModel.DataModelMeSingleton;
import pana.com.chat.DataModel.DataModelUser;
import pana.com.chat.R;
import pana.com.chat.Util.Utils;

public class FriendsRequestFragment extends Fragment implements TabFragment.TabInterAction {

    private ListView listView;
    private ArrayList friendsID, requestDate;
    private ArrayList<DataModelUser> friendsData;
    private Firebase pcchatapp;
    private DataModelMeSingleton ME;
    private ValueEventListener VEL1, VEL2;
    private ProgressDialog progressDialog;
    private boolean friendRequestAvailable = true;

    public FriendsRequestFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_request, container, false);
        progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait", true, false);
        pcchatapp = new Firebase("https://pcchatapp.firebaseio.com/");

        ME = DataModelMeSingleton.getInstance();

        listView = (ListView) view.findViewById(R.id.friendrequestlistView);
        try {


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view2 = inflater.inflate(R.layout.profiledialog, null);
                    TextView name = (TextView) view2.findViewById(R.id.profiledialog_name);
                    TextView email = (TextView) view2.findViewById(R.id.profiledialog_email);
                    TextView phone = (TextView) view2.findViewById(R.id.profiledialog_phone);
                    ImageView imageView = (ImageView) view2.findViewById(R.id.profiledialog_imageview);
                    name.setText(friendsData.get(i).getName());
                    email.setText(friendsData.get(i).getEmail_id());
                    phone.setText(friendsData.get(i).getPhone());
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setView(view2);
                    alertDialog.show();
                    alertDialog.getWindow().setLayout(Utils.dialogHW, Utils.dialogHW);

                }
            });
            friendsData = new ArrayList<DataModelUser>();
            friendsID = new ArrayList();
            requestDate = new ArrayList();

            pcchatapp.child("friend_requests").child(ME.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("Friend Request", "On Data Changed Called");
                    VEL1 = this;
                    friendRequestAvailable = true;

                    friendsData.clear();
                    friendsID.clear();
                    requestDate.clear();
                    listView.setAdapter(new CustomFriendRequestAdapter(friendsID, friendsData, requestDate, getActivity()));
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            friendsID.add(d.getKey().toString());
                            requestDate.add(((HashMap<String, Object>) d.getValue()).get("RequestDate"));
                            pcchatapp.child("users").child(d.getKey().toString()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    VEL2 = this;
                                    DataModelUser dataModelUser = dataSnapshot.getValue(DataModelUser.class);
                                    friendsData.add(dataModelUser);
                                    listView.setAdapter(new CustomFriendRequestAdapter(friendsID, friendsData, requestDate, getActivity()));

                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }
                    } else {
                        Log.d("Requests", "No requests");
                        friendRequestAvailable = false;
                        progressDialog.dismiss();

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        } catch (Exception ex) {
            Utils.ToastLong(getActivity(), "Sorry we are facing a problem due slow Internet connection please try again later");
        }
        return view;
    }

    @Override
    public void friendRequestFragment() {
        if (!friendRequestAvailable) {
            Snackbar.make(listView, "No Friend Request Available", Snackbar.LENGTH_LONG).show();
        }
    }

}
