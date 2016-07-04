package hu.rozsa.daniel.rxandroidsample.pulltorefresh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hu.rozsa.daniel.rxandroidsample.R;

public class UserAdapter extends BaseAdapter {

    private final List<SampleUser> users;

    public UserAdapter(List<SampleUser> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public SampleUser getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return users.hashCode();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            convertView = inflater.inflate(R.layout.item_user_list, viewGroup, false);

            holder.imgUser = (ImageView) convertView.findViewById(R.id.imgUserAvatar);
            holder.userName = (TextView) convertView.findViewById(R.id.tvUserName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SampleUser currentUser = getItem(pos);

        holder.userName.setText(currentUser.userName + " @" + String.valueOf(currentUser.userAge));

        return convertView;
    }

    private class ViewHolder {
        ImageView imgUser;
        TextView userName;
    }


}
