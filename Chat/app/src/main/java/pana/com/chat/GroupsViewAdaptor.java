package pana.com.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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
    private LayoutInflater inflater;
    private ArrayList<Groups> groups;
    private Context context;

    public GroupsViewAdaptor(Context context, ArrayList<Groups> groups) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groups = groups;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
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
        view.groupName.setText(grp.getGroupName());
        view.groupDescription.setText(grp.getGroupDescription());
        view.joinThisGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "JoinTo Group Clicked", Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }

    class ViewHolder {
        public TextView groupName;
        public ImageView groupImage;
        public TextView groupDescription;
        public ImageButton joinThisGroupButton;
    }
}
