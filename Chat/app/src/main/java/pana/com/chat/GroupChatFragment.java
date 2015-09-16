package pana.com.chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChatFragment extends Fragment {
    private Firebase firebaseURL;
    private ImageButton sendMessageButton;
    private EditText sendMessageText;
    private ListView chatListView;
    private TextView friendName, friendEmail;
    private Button logoutButton;
    private ArrayList<Messages> messagesArrayList;
    private DataModelCurrentGroupChat groupChatDetail = DataModelCurrentGroupChat.getInstance();

    public GroupChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        firebaseURL = new Firebase("https://pcchatapp.firebaseio.com/");
        sendMessageButton = (ImageButton) view.findViewById(R.id.chatFragmentButtonSendMessage);
        sendMessageText = (EditText) view.findViewById(R.id.chatFragmentEditTextWriteMessageHere);
        friendName = (TextView) view.findViewById(R.id.chatFragmentTextViewFriendName);
        friendEmail = (TextView) view.findViewById(R.id.chatFragmentTextViewFriendEmailView);
        logoutButton = (Button) view.findViewById(R.id.chatFragmentButtonLogout);
        chatListView = (ListView) view.findViewById(R.id.chatFragmentListViewChatMessages);
        messagesArrayList = new ArrayList<>();
        sendMessageButtonClickEvent();
        setDetails();
        loadGroupMembersData();
        return view;
    }

    private void sendMessageButtonClickEvent() {
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseURL.child("conversation")
                        .child(DataModelCurrentGroupChat.getInstance()
                                .getGroupIDKEY()).push()
                        .setValue(new Messages(String.valueOf(System.currentTimeMillis()), sendMessageText.getText().toString(), DataModelMeSingleton.getInstance().getId()));
                sendMessageText.setText("");
            }
        });
    }

    private void loadGroupMembersData() {
        firebaseURL.child("groupusers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Utils.myFrindsId.clear();
                GroupUsersDetailsHashMap.getInstance().clear();
                for (final DataSnapshot d : dataSnapshot.getChildren()) {
                    for (DataSnapshot data : d.getChildren()) {
                        final String UserID = data.getValue().toString();
                        if (!Utils.friendIdAddedToMap(UserID)) {
                            Utils.myFrindsId.add(UserID);
                            Log.d(DataModelCurrentGroupChat.getInstance().getGroupName(), "Users=" + UserID);
                            firebaseURL.child("users").child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshotForUser) {
                                    GroupUsersDetailsHashMap.getInstance().put(UserID, dataSnapshotForUser.getValue(DataModelUser.class));
                                    Log.d("User Added " + UserID, dataSnapshotForUser.getValue().toString());

                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }
                    }
                    loadAllMessages();

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void loadAllMessages() {
        firebaseURL.child("conversation").child(DataModelCurrentGroupChat.getInstance().getGroupIDKEY()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messagesArrayList.clear();
                for (DataSnapshot messages : dataSnapshot.getChildren()) {
                    Messages msg = messages.getValue(Messages.class);
                    messagesArrayList.add(msg);
                    Log.d("Msg is ", msg.toString());
                }
                loadChatAdaptor();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void loadChatAdaptor() {
        chatListView.setAdapter(new GroupChatAdaptor(getActivity(), messagesArrayList));

    }

    private void setDetails() {
        friendName.setText(groupChatDetail.getGroupName());
        friendEmail.setText(groupChatDetail.getGroupDescription());
    }


}
