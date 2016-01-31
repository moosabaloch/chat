package pana.com.chat.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import pana.com.chat.DataModel.DataModelMeSingleton;
import pana.com.chat.DataModel.GroupUsersDetailsHashMap;
import pana.com.chat.DataModel.Messages;
import pana.com.chat.R;
import pana.com.chat.Util.Utils;

/**
 * Created by Moosa
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class GroupChatAdaptor extends BaseAdapter {
    // private LayoutInflater inflater;
    private List<Messages> messagesList;
    private Context context;

    public GroupChatAdaptor(Context context, List<Messages> messagesList) {
        //  this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      try {


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
          GroupUsersDetailsHashMap groupUsersDetailsHashMap = GroupUsersDetailsHashMap.getInstance();
          Picasso picasso = Picasso.with(context);
          if (msg.getUser().equals(DataModelMeSingleton.getInstance().getId())) {
              view.profilePicMe.setVisibility(View.VISIBLE);
              view.messageMe.setVisibility(View.VISIBLE);
              view.nameMe.setVisibility(View.VISIBLE);
              view.timeStampMe.setVisibility(View.VISIBLE);
              view.messageMe.setText(msg.getMessage());
              view.nameMe.setText(DataModelMeSingleton.getInstance().getName());
              picasso.load(DataModelMeSingleton.getInstance()
                      .getImageUrl())
                      .placeholder(R.drawable.friend).error(R.drawable.friend)
                      .into(view.profilePicMe);
              view.timeStampMe.setText("" + Utils.getTimeAgo(new Date(Long.parseLong(msg.getTimeStamp())), context));
              view.profilePicFriend.setVisibility(View.INVISIBLE);
              view.messageFriend.setVisibility(View.INVISIBLE);
              view.nameFriend.setVisibility(View.INVISIBLE);
              view.timeStampFriend.setVisibility(View.INVISIBLE);

          } else {
              view.profilePicFriend.setVisibility(View.VISIBLE);
              view.messageFriend.setVisibility(View.VISIBLE);
              view.nameFriend.setVisibility(View.VISIBLE);
              view.timeStampFriend.setVisibility(View.VISIBLE);
              ///////////////FRIENDS DATA//////////////////////
              view.messageFriend.setText(msg.getMessage());
              picasso.load(groupUsersDetailsHashMap
                      .get(msg.getUser()).getImage_url())
                      .placeholder(R.drawable.friend)
                      .error(R.drawable.friend)
                      .into(view.profilePicFriend);
              view.nameFriend.setText(groupUsersDetailsHashMap.get(msg.getUser()).getName());
              view.timeStampFriend.setText("" + Utils.getTimeAgo(new Date(Long.parseLong(msg.getTimeStamp())), context));
              ///////////////////////////////////////////////
              view.profilePicMe.setVisibility(View.INVISIBLE);
              view.messageMe.setVisibility(View.INVISIBLE);
              view.nameMe.setVisibility(View.INVISIBLE);
              view.timeStampMe.setVisibility(View.INVISIBLE);

          }
      }catch (Exception ex){
          ex.printStackTrace();
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
