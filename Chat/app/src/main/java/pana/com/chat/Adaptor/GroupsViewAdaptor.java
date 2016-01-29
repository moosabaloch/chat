package pana.com.chat.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pana.com.chat.DataModel.Groups;
import pana.com.chat.R;
import pana.com.chat.Util.Utils;

/**
 * Created by Moosa on 9/14/2015.
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class GroupsViewAdaptor extends BaseAdapter {
    private Context context;
    private ArrayList<Groups> groups;
    private Object ref;
    private ArrayList<String> groupKeys;
    private int TYPE;
    private String title;

    public GroupsViewAdaptor(Context context, ArrayList<Groups> groups, Object ref, ArrayList<String> groupKeys, int TYPE, String title) {
        this.groups = groups;
        this.TYPE = TYPE;
        this.ref = ref;
        this.title = title;
        this.groupKeys = groupKeys;
        this.context = context;
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Groups getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.groupsviewadaptor, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.groupName = (TextView) convertView.findViewById(R.id.groupAdaptorTextViewGroupName);
            viewHolder.groupImage = (ImageView) convertView.findViewById(R.id.groupAdaptorImageViewGroupImage);
            viewHolder.joinThisGroupButton = (ImageButton) convertView.findViewById(R.id.groupAdaptorImageButtonJoinGroupButton);
            viewHolder.groupDescription = (TextView) convertView.findViewById(R.id.groupAdaptorTextViewGroupDescription);

            convertView.setTag(viewHolder);
        }

        ViewHolder view = (ViewHolder) convertView.getTag();

        Groups grp = groups.get(position);
        if (grp.getGroupName().equals(title)) {
            view.groupName.setText(grp.getGroupName()+" - New Message");
            view.groupName.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            view.groupName.setText(grp.getGroupName());
            view.groupName.setTextColor(context.getResources().getColor(R.color.colorPrimaryText));

        }
        view.groupDescription.setText(grp.getGroupDescription());
        if (TYPE == Utils.TYPEMYGROUPS) {
            view.joinThisGroupButton.setVisibility(View.INVISIBLE);
        }
        view.joinThisGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GroupAdaptorAddEvent) ref).addMeToThisGroup(groupKeys.get(position), position);
            }
        });

        return convertView;
    }

    public interface GroupAdaptorAddEvent {//Refactor Change Conflict

        void addMeToThisGroup(String key, int position);
    }

    class ViewHolder {
        public TextView groupName;
        public ImageView groupImage;
        public TextView groupDescription;
        public ImageButton joinThisGroupButton;
    }
}
