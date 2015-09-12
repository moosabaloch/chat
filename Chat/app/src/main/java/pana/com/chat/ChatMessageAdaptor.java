package pana.com.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Moosa on 9/12/2015.
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class ChatMessageAdaptor extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<Messages> messagesList;

    public ChatMessageAdaptor(Context context, List<Messages> messagesList) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.messagesList = messagesList;
    }

    @Override
    public int getCount() {
        return messagesList.size();
    }

    @Override
    public Messages getItem(int position) {
        return messagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.chatadaptor, null);
        TextView messageMe = (TextView) view.findViewById(R.id.chatAdaptorTextViewForMessageMe);
        TextView messageFriend = (TextView) view.findViewById(R.id.chatAdaptorTextViewForMessageFriend);
        TextView nameMe = (TextView) view.findViewById(R.id.chatAdaptorTextViewForNameMe);
        TextView nameFriend = (TextView) view.findViewById(R.id.chatAdaptorTextViewForNameFriend);
        TextView timeStampMe = (TextView) view.findViewById(R.id.chatAdaptorTextViewTimeMe);
        TextView timeStampFriend = (TextView) view.findViewById(R.id.chatAdaptorTextViewTimeFriend);
        ImageView profilePicMe = (ImageView) view.findViewById(R.id.chatAdaptorImageViewProfilePictureMe);
        ImageView profilePicFriend = (ImageView) view.findViewById(R.id.chatAdaptorImageViewProfilePictureFriend);
        Messages msg = messagesList.get(position);
        if (msg.getUser().getId().equals(DataModelMeSingleton.getInstance().getId())) {
            messageMe.setText(msg.getMessage());
            nameMe.setText(msg.getUser().getName());

            timeStampMe.setText(""+Utils.getTimeDistanceInMinutes(Long.parseLong(msg.getTimeStamp())));
            profilePicFriend.setVisibility(View.INVISIBLE);
            messageFriend.setVisibility(View.INVISIBLE);
            nameFriend.setVisibility(View.INVISIBLE);
            timeStampFriend.setVisibility(View.INVISIBLE);

        } else {
            messageFriend.setText(msg.getMessage());
            nameFriend.setText(msg.getUser().getName());
            timeStampFriend.setText(""+Utils.getTimeDistanceInMinutes(Long.parseLong(msg.getTimeStamp())));
            profilePicMe.setVisibility(View.INVISIBLE);
            messageMe.setVisibility(View.INVISIBLE);
            nameMe.setVisibility(View.INVISIBLE);
            timeStampMe.setVisibility(View.INVISIBLE);

        }

        return view;
    }
}
