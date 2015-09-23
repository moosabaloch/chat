package pana.com.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomFriendsListAdapter extends BaseAdapter {

    Context context;

    ArrayList ids;

    ArrayList<DataModelUser> allUsers;

    public CustomFriendsListAdapter(Context context, ArrayList ids, ArrayList<DataModelUser> allUsers) {
        this.context = context;
        this.ids = ids;
        this.allUsers = allUsers;
    }

    @Override
    public int getCount() {
        return allUsers.size();
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_friendslist, null);
        ImageView imageView = (ImageView) v.findViewById(R.id.friends_iv_profileimg);
        TextView name = (TextView) v.findViewById(R.id.friends_tv_name);
        name.setText(allUsers.get(i).getName());
        Picasso.with(context).load(allUsers.get(i).getImage_url()).placeholder(R.drawable.friend).error(R.drawable.friend).into(imageView);

        return v;
    }
}
