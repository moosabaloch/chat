package pana.com.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

    private List<Messages> messagesList;
    private Context context;

    public ChatMessageAdaptor(Context context, List<Messages> messagesList) {

        this.messagesList = messagesList;
        this.context = context;
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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chatadaptor, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.messageMe = (TextView) convertView.findViewById(R.id.chatAdaptorTextViewForMessageMe);
            viewHolder.messageFriend = (TextView) convertView.findViewById(R.id.chatAdaptorTextViewForMessageFriend);
            viewHolder.nameMe = (TextView) convertView.findViewById(R.id.chatAdaptorTextViewForNameMe);
            viewHolder.nameFriend = (TextView) convertView.findViewById(R.id.chatAdaptorTextViewForNameFriend);
            viewHolder.timeStampMe = (TextView) convertView.findViewById(R.id.chatAdaptorTextViewTimeMe);
            viewHolder.timeStampFriend = (TextView) convertView.findViewById(R.id.chatAdaptorTextViewTimeFriend);
            viewHolder.profilePicMe = (ImageView) convertView.findViewById(R.id.chatAdaptorImageViewProfilePictureMe);
            viewHolder.profilePicFriend = (ImageView) convertView.findViewById(R.id.chatAdaptorImageViewProfilePictureFriend);
            convertView.setTag(viewHolder);
        }
        ViewHolder view = (ViewHolder) convertView.getTag();
        Messages msg = messagesList.get(position);
        Picasso picasso = Picasso.with(context);
        if (msg.getUser().equals(DataModelMeSingleton.getInstance().getId())) {
            view.profilePicMe.setVisibility(View.VISIBLE);
            view.messageMe.setVisibility(View.VISIBLE);
            view.nameMe.setVisibility(View.VISIBLE);
            view.timeStampMe.setVisibility(View.VISIBLE);
            view.messageMe.setText(msg.getMessage());
            picasso.load(DataModelMeSingleton.getInstance().getImageUrl())
                    .placeholder(R.drawable.friend).error(android.R.drawable.stat_sys_download)
                    .into(view.profilePicMe);

            view.nameMe.setText(DataModelMeSingleton.getInstance().getName());
            view.timeStampMe.setText("" + Utils.getTimeDistanceInMinutes(Long.parseLong(msg.getTimeStamp())));
            view.profilePicFriend.setVisibility(View.INVISIBLE);
            view.messageFriend.setVisibility(View.INVISIBLE);
            view.nameFriend.setVisibility(View.INVISIBLE);
            view.timeStampFriend.setVisibility(View.INVISIBLE);

        } else {
            view.profilePicFriend.setVisibility(View.VISIBLE);
            view.messageFriend.setVisibility(View.VISIBLE);
            view.nameFriend.setVisibility(View.VISIBLE);
            view.timeStampFriend.setVisibility(View.VISIBLE);
            view.messageFriend.setText(msg.getMessage());
            view.nameFriend.setText(DataModelFriendSingleTon.getInstance().getNameUserFriend());
            picasso.load(DataModelFriendSingleTon.getInstance().getImageUrlUserFriend())
                    .placeholder(R.drawable.friend).error(android.R.drawable.stat_sys_download)
                    .into(view.profilePicFriend);


            view.timeStampFriend.setText("" + Utils.getTimeDistanceInMinutes(Long.parseLong(msg.getTimeStamp())));
            view.profilePicMe.setVisibility(View.INVISIBLE);
            view.messageMe.setVisibility(View.INVISIBLE);
            view.nameMe.setVisibility(View.INVISIBLE);
            view.timeStampMe.setVisibility(View.INVISIBLE);

        }

        return convertView;
    }

    class ViewHolder {
        public TextView messageMe;
        public TextView messageFriend;
        public TextView nameMe;
        public TextView nameFriend;
        public TextView timeStampMe;
        public TextView timeStampFriend;
        public ImageView profilePicMe;
        public ImageView profilePicFriend;

    }
}
