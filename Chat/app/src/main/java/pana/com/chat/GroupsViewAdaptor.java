package pana.com.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

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
    private Context context;
    private ArrayList<Groups> groups;


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chatadaptor, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.messageMe = (TextView) convertView.findViewById(R.id.chatAdaptorTextViewForMessageMe);
            convertView.setTag(viewHolder);
        }
        ViewHolder view = (ViewHolder) convertView.getTag();
     Groups grp=groups.get(position);

        return convertView;
    }

    class ViewHolder {
        public TextView messageMe;

    }
}
