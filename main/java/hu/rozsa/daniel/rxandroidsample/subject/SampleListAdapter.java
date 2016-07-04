package hu.rozsa.daniel.rxandroidsample.subject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import hu.rozsa.daniel.rxandroidsample.R;


public class SampleListAdapter extends BaseAdapter {

    private final List<SubjectObject> elements;

    public SampleListAdapter(List<SubjectObject> elements) {
        this.elements = elements;
    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @Override
    public SubjectObject getItem(int i) {
        return elements.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.item_sample_list, viewGroup, false);
            holder.longTextView = (TextView) view.findViewById(R.id.tvLong);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        SubjectObject currentItem = getItem(position);

        holder.longTextView.setText("sample: " + currentItem.timeInterval);

        return view;
    }

    private class ViewHolder {

        TextView longTextView;
    }

}
