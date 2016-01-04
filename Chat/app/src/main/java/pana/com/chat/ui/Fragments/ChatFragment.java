package pana.com.chat.ui.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import pana.com.chat.Adaptor.ChatMessageAdaptor;
import pana.com.chat.DataModel.DataModelFriendSingleTon;
import pana.com.chat.DataModel.DataModelMeSingleton;
import pana.com.chat.DataModel.Messages;
import pana.com.chat.R;
import pana.com.chat.apicall.PostReq;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private ArrayList<Messages> messagesArrayList;
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
    private ValueEventListener VEL1, VEL2;
    private ImageView friendImageView;
    private Picasso picasso;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        friendImageView = (ImageView) view.findViewById(R.id.fragmentChatImageView);

        friendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view2 = inflater.inflate(R.layout.profiledialog, null);
                TextView name = (TextView) view2.findViewById(R.id.profiledialog_name);
                TextView email = (TextView) view2.findViewById(R.id.profiledialog_email);
                TextView phone = (TextView) view2.findViewById(R.id.profiledialog_phone);
                ImageView imageView = (ImageView) view2.findViewById(R.id.profiledialog_imageview);

                name.setText(friendData.getNameUserFriend());
                email.setText(firebaseURL.getAuth().getProviderData().get("email").toString());
                phone.setText(friendData.getPhoneUserFriend());
//                Picasso Implementation
                picasso.load(friendData.getImageUrlUserFriend()).placeholder(R.drawable.friend).error(R.drawable.friend).into(imageView);
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setView(view2);
                alertDialog.show();
            }
        });

        firebaseURL = new Firebase("https://pcchatapp.firebaseio.com/");

        friendData = DataModelFriendSingleTon.getInstance();

        ME = DataModelMeSingleton.getInstance();

        messagesArrayList = new ArrayList<>();
        picasso = Picasso.with(getActivity());
        sendMessageButton = (ImageButton) view.findViewById(R.id.chatFragmentButtonSendMessage);
        sendMessageText = (EditText) view.findViewById(R.id.chatFragmentEditTextWriteMessageHere);
        friendName = (TextView) view.findViewById(R.id.chatFragmentTextViewFriendName);
        friendEmail = (TextView) view.findViewById(R.id.chatFragmentTextViewFriendEmailView);

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

        return view;

    }

    private void setConversationID() {
        if (friendData.getConversationID().toString().equals("null")) {
            firebaseURL.child("user_friend").child(ME.getId()).child(friendData.getUuidUserFriend()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    VEL1 = this;
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Log.d("ConversationID........", d.getValue().toString());
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
        friendEmail.setText(friendData.getEmailUserFriend());
        friendName.setText(friendData.getNameUserFriend());
        picasso.load(friendData.getImageUrlUserFriend())
                .placeholder(R.drawable.friend)
                .error(R.drawable.friend)
                .into(friendImageView);

    }

    private void loadConversation() {
        if (isNewChat()) {
            Toast.makeText(getActivity(), "No conversation Found", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("StartChatAndSetMessage", "ChatIsRegistered");
            firebaseURL.child("conversation").child(friendData.getConversationID().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    VEL2 = this;
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

    private boolean isNewChat() {
        if (friendData.getConversationID().toString().equals("null")) {
            return true;
        } else {
            return false;
        }
    }

    private void sendMyMessage() {

        if (isNewChat()) {
            Firebase newchatref = firebaseURL.child("conversation").push();

            Log.d("New Convo Key..........", newchatref.getKey());

            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("ConversationID", newchatref.getKey());

            firebaseURL.child("user_friend").child(ME.getId()).child(friendData.getUuidUserFriend()).setValue(hashMap);
            firebaseURL.child("user_friend").child(friendData.getUuidUserFriend()).child(ME.getId()).setValue(hashMap);

            //friendData.setConversationID(hashMap.get("ConversationID").toString());
            //Log.d("Setting Conversation Key........",hashMap.get("ConversationID").toString());

            if (!sendMessageText.getText().toString().equals("")) {
                long time = System.currentTimeMillis();
                String timeInString = String.valueOf(time);
                newchatref.push().setValue(new Messages(timeInString, sendMessageText.getText().toString(), ME.getId()), new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        Toast.makeText(getActivity(), "Sent", Toast.LENGTH_SHORT).show();
                        loadConversation();
                    }
                });
                sendMessageText.setText("");
            }
        } else {
            if (!sendMessageText.getText().toString().equals("")) {
                long time = System.currentTimeMillis();
                final String msg=sendMessageText.getText().toString();
                String timeInString = String.valueOf(time);
                firebaseURL.child("conversation").child(friendData.getConversationID())
                        .push().setValue(new Messages(timeInString,msg, ME.getId()),
                        new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        Toast.makeText(getActivity(), "Sent", Toast.LENGTH_SHORT).show();
                        PostReq.getMyInstance().notifySingleUser(friendData.getUuidUserFriend(),msg,ME.getName());

                    }
                });
                sendMessageText.setText("");
            }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (VEL1 != null)
            firebaseURL.child("user_friend").child(ME.getId()).child(friendData.getUuidUserFriend()).removeEventListener(VEL1);
        if (VEL2 != null)
            firebaseURL.child("conversation").child(friendData.getConversationID().toString()).removeEventListener(VEL2);
    }
}
