package pana.com.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomFriendsListAdapter extends BaseAdapter {

    Context context;

    ArrayList images,names;

    View view;

    LayoutInflater inflater;

    TextView name;

    ImageView profileImg;

    public CustomFriendsListAdapter(Context context,ArrayList names) {
        this.context=context;
        this.names=names;
        //this.images=images;
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override

    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=inflater.inflate(R.layout.custom_friendslist,null);
        profileImg=(ImageView) view.findViewById(R.id.friends_iv_profileimg);
        name=(TextView) view.findViewById(R.id.friends_tv_name);
        name.setText(names.get(i).toString());
        return view;
    }
}