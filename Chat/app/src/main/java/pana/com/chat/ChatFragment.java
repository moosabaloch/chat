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
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    ArrayList<Messages> messagesArrayList;
    private Firebase firebaseURL;
    private ImageButton sendMessageButton;
    private EditText sendMessageText;
    private TextView friendName, friendEmail;
    private Button logoutButton;
    private DataModelChatUserSingleTon friendData;
    private DataModelMeSingleton ME;
    private String chatUserDataNode = "chatuserdata";
    private boolean chatIsNew = true;
    private ListView chatListView;
    private ChatMessageAdaptor chatMessageAdaptor;
    private String conversationId = null;
    private boolean onceSet = true;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        firebaseURL = new Firebase("https://pcchatapp.firebaseio.com/");
        friendData = DataModelChatUserSingleTon.getInstance();
        ME = DataModelMeSingleton.getInstance();
        messagesArrayList = new ArrayList<>();
        sendMessageButton = (ImageButton) view.findViewById(R.id.chatFragmentButtonSendMessage);
        sendMessageText = (EditText) view.findViewById(R.id.chatFragmentEditTextWriteMessageHere);
        friendName = (TextView) view.findViewById(R.id.chatFragmentTextViewFriendName);
        friendEmail = (TextView) view.findViewById(R.id.chatFragmentTextViewFriendEmailView);
        logoutButton = (Button) view.findViewById(R.id.chatFragmentButtonLogout);
        chatListView = (ListView) view.findViewById(R.id.chatFragmentListViewChatMessages);
        setFriendDetails();
        startChat();
        sendMessage();
        logoutBtn();
        return view;
    }

    private void logoutBtn() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void sendMessage() {
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sendMessageText.getText().toString().equals("")) {
                    long time = System.currentTimeMillis();
                    String timeInString = String.valueOf(time);
                    firebaseURL.child("conversation").child(conversationId).push().setValue(new Messages(timeInString, sendMessageText.getText().toString(), ME.getId()));
                    sendMessageText.setText("");
                }
            }
        });
    }

    private void startChat() {

        firebaseURL.child(chatUserDataNode).child(ME.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    //traversing chatNode for logged in User if chat available start chat
                    ChatFriendMEId node = d.getValue(ChatFriendMEId.class);
                    if (node.getUser1().equals(friendData.getUuidUserFriend()) || node.getUser2().equals(friendData.getUuidUserFriend())) {
                        conversationId = node.getConversationId();
                        chatIsNew = false;
                        Log.d("Start Chat", "Node id Found " + conversationId);

                    }
                }
                startChatAndSetMessages();


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getActivity(), "Can't Load Chat " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void startChatAndSetMessages() {
        if (chatIsNew) {
            Log.d("StartChatAndMessage", "Chat Is New");
            //getting push key
            Firebase newConversation = firebaseURL.child("conversation").push();
            //Storing push key in ChatNode and user details
            ChatFriendMEId newNode = new ChatFriendMEId();
            newNode.setUser1(ME.getId());
            newNode.setUser2(friendData.getUuidUserFriend());
            newNode.setConversationId(newConversation.getKey());

            firebaseURL.child(chatUserDataNode).child(ME.getId()).push().setValue(newNode);
            firebaseURL.child(chatUserDataNode).child(friendData.getUuidUserFriend()).push().setValue(newNode);
            startChat();
        } else {
            Log.d("StartChatAndSetMessage", "ChatIsRegistered");

            firebaseURL.child("conversation").child(conversationId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    messagesArrayList.clear();
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            Messages messages = d.getValue(Messages.class);
                            messagesArrayList.add(messages);

                        }
                        if (onceSet) {
                            setAdaptor();
                            onceSet = false;
                        } else {
                            refreshAdaptor();
                        }
                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getActivity(), "Can't Load Conversation " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void refreshAdaptor() {
        chatMessageAdaptor.notifyDataSetChanged();
        chatListView.setSelection(messagesArrayList.size());

    }

    private void setAdaptor() {
        chatMessageAdaptor = new ChatMessageAdaptor(getActivity(), messagesArrayList);
        chatListView.setAdapter(chatMessageAdaptor);
        chatListView.setSelection(messagesArrayList.size() - 1);

    }


    private void setFriendDetails() {
        friendEmail.setText(friendData.getEmailUserFriend());
        friendName.setText(friendData.getNameUserFriend());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
