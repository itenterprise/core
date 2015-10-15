package com.it.core.menu;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.it.core.R;

import java.util.List;

public class DrawerMenuAdapter extends ArrayAdapter<SideMenuItem> {

    List<SideMenuItem> items;
    LayoutInflater inflater;

    public DrawerMenuAdapter(Context context, List<SideMenuItem> objects) {
        super(context, 0, objects);
        items = objects;
        inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        final ViewHolder holder;
        // Создать view и viewholder, если их нет, или получить viewholder из тега
        try {
            SideMenuItem item = items.get(position);
            if(item.IsSection){
                itemView = setMenuSection(item);
            } else {
                itemView = setMenuItem(item);
            }
        } catch (Exception e) {
            Log.d("ADAPTER_ERROR", e.getMessage());
        }
        return itemView;
    }

    private View setMenuSection(SideMenuItem section){
        final ViewHolder holder;
        View convertView = inflater.inflate(R.layout.drawer_list_item, null);
        holder = new ViewHolder();
        holder.title = (TextView)convertView.findViewById(R.id.drawer_list_item_label);
        holder.icon = (ImageView)convertView.findViewById(R.id.drawer_list_item_icon);
        convertView.setTag(holder);
        holder.title.setText(section.Title.toUpperCase());
        holder.title.setTextSize(18);
        holder.title.setTypeface(null, Typeface.BOLD);
        holder.title.setPadding(10, 10, 0 ,10);
        holder.icon.setVisibility(View.GONE);
        convertView.setEnabled(false);
        convertView.setOnClickListener(null);
        return convertView;
    }

    private View setMenuItem(SideMenuItem item){
        final ViewHolder holder;
        View convertView = inflater.inflate(R.layout.drawer_list_item, null);
        holder = new ViewHolder();
        holder.title = (TextView)convertView.findViewById(R.id.drawer_list_item_label);
        holder.icon = (ImageView)convertView.findViewById(R.id.drawer_list_item_icon);
        convertView.setTag(holder);
        holder.title.setText(item.Title);
        if(item.ImageId != null){
            holder.icon.setImageResource(item.ImageId);
        } else {
            holder.icon.setImageDrawable(null);
        }
        return convertView;
    }

    private static class ViewHolder{
        TextView title;
        ImageView icon;
    }
}