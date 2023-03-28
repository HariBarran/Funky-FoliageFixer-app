package com.example.foliagefixer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foliagefixer.ImageItem;

import java.util.List;

public class CustomListAdapter extends ArrayAdapter<ImageItem> {

    private final LayoutInflater inflater;

    public CustomListAdapter(Context context, List<ImageItem> items) {
        super(context, R.layout.list_item, items);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.imageView);
            viewHolder.textView = convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageItem item = getItem(position);
        viewHolder.imageView.setImageBitmap(item.getImage());
        viewHolder.textView.setText(item.getTitle());

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}