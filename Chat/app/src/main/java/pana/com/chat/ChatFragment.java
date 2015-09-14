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
import java.util.HashMap;


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
    private DataModelFriendSingleTon friendData;
    private DataModelMeSingleton ME;
    private String chatUserDataNode = "chatuserdata";
    private boolean chatIsNew = true;
    private ListView chatListView;
    private ChatMessageAdaptor chatMessageAdaptor;
    private String conversationId = null;
    private boolean onceSet = true;
    private ValueEventListener VEL1,VEL2;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        firebaseURL = new Firebase("https://pcchatapp.firebaseio.com/");

        friendData = DataModelFriendSingleTon.getInstance();

        ME = DataModelMeSingleton.getInstance();

        messagesArrayList = new ArrayList<>();

        sendMessageButton = (ImageButton) view.findViewById(R.id.chatFragmentButtonSendMessage);
        sendMessageText = (EditText) view.findViewById(R.id.chatFragmentEditTextWriteMessageHere);
        friendName = (TextView) view.findViewById(R.id.chatFragmentTextViewFriendName);
        friendEmail = (TextView) view.findViewById(R.id.chatFragmentTextViewFriendEmailView);
        logoutButton = (Button) view.findViewById(R.id.chatFragmentButtonLogout);

        chatListView = (ListView) view.findViewById(R.id.chatFragmentListViewChatMessages);

        setConversationID();

        setFriendDetails();

        loadConversation();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMyMessage();
            }
        });

        logoutBtn();

        return view;

    }

    private void setConversationID() {
        if (friendData.getConversationID().toString().equals("null")){
            firebaseURL.child("user_friend").child(ME.getId()).child(friendData.getUuidUserFriend()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    VEL1=this;
                    for(DataSnapshot d:dataSnapshot.getChildren()){
                        Log.d("ConversationID........",d.getValue().toString());
                        friendData.setConversationID(d.getValue().toString());
                        loadConversation();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    private void setFriendDetails() {
        Log.d("Getting Friend Detail..........",friendData.getEmailUserFriend()+"\n"+friendData.getNameUserFriend());
        friendEmail.setText(friendData.getEmailUserFriend());
        friendName.setText(friendData.getNameUserFriend());
    }

    private void loadConversation() {
        if (isNewChat()) {
            Toast.makeText(getActivity(),"No conversation Found",Toast.LENGTH_SHORT).show();
        } else {
            Log.d("StartChatAndSetMessage", "ChatIsRegistered");
            firebaseURL.child("conversation").child(friendData.getConversationID().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    VEL2=this;
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

    private boolean isNewChat(){
        if (friendData.getConversationID().toString().equals("null")){
            Log.d("Is Chat is New..........","True");
            return true;
        }
        else{
            Log.d("Is Chat is New..........","False");
            return false;
        }
    }

    private void sendMyMessage(){

        if (isNewChat()){
            Firebase newchatref=firebaseURL.child("conversation").push();

            Log.d("New Convo Key..........",newchatref.getKey());

            HashMap<String,Object> hashMap=new HashMap<String, Object>();
            hashMap.put("ConversationID",newchatref.getKey());

            firebaseURL.child("user_friend").child(ME.getId()).child(friendData.getUuidUserFriend()).setValue(hashMap);
            firebaseURL.child("user_friend").child(friendData.getUuidUserFriend()).child(ME.getId()).setValue(hashMap);

            //friendData.setConversationID(hashMap.get("ConversationID").toString());
            //Log.d("Setting Conversation Key........",hashMap.get("ConversationID").toString());

            if (!sendMessageText.getText().toString().equals("")) {
                long time = System.currentTimeMillis();
                String timeInString = String.valueOf(time);
                newchatref.push().setValue(new Messages(timeInString, sendMessageText.getText().toString(), ME.getId()),new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        Toast.makeText(getActivity(),"Sent",Toast.LENGTH_SHORT).show();
                        loadConversation();
                    }
                });
                sendMessageText.setText("");
            }
        }
        else{
            if (!sendMessageText.getText().toString().equals("")) {
                long time = System.currentTimeMillis();
                String timeInString = String.valueOf(time);
                firebaseURL.child("conversation").child(friendData.getConversationID()).push().setValue(new Messages(timeInString, sendMessageText.getText().toString(), ME.getId()),new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        Toast.makeText(getActivity(),"Sent",Toast.LENGTH_SHORT).show();

                    }
                });
                sendMessageText.setText("");
            }
        }
    }

    private void startChat() {

        if (friendData.getConversationID().toString().equals("null")){
            chatIsNew=true;
        }
        else{
            chatIsNew=false;
        }

        //startChatAndSetMessages();

        /*firebaseURL.child(chatUserDataNode).child(ME.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
        });*/

    }

    private void logoutBtn() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(VEL1!=null)
            firebaseURL.child("user_friend").child(ME.getId()).child(friendData.getUuidUserFriend()).removeEventListener(VEL1);
        if(VEL2!=null)
            firebaseURL.child("conversation").child(friendData.getConversationID().toString()).removeEventListener(VEL2);
    }
}
