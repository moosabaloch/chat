package pana.com.chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Firebase;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private Firebase firebaseURL;
    private ImageButton sendMessageButton;
    private EditText sendMessageText;
    private TextView friendName, friendEmail;
    private Button logoutButton;

    public ChatFragment() {
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
        setFriendDetails();
        return view;
    }

    private void setFriendDetails() {
        DataModelChatUserSingleTon friend = DataModelChatUserSingleTon.getInstance();
        friendEmail.setText(friend.getEmailUserFriend());
        friendName.setText(friend.getNameUserFriend());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
