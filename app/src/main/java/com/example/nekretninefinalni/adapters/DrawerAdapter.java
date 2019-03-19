package com.example.nekretninefinalni.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nekretninefinalni.R;
import com.example.nekretninefinalni.model.NavigationItems;

import java.util.List;

public class DrawerAdapter extends BaseAdapter {

    private Context context;
    private List<NavigationItems> navigationItems;

    public DrawerAdapter(Context context, List<NavigationItems> navigationItems) {
        this.context = context;
        this.navigationItems = navigationItems;
    }

    @Override
    public int getCount() {
        return navigationItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navigationItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.drawer_item, null);

        ImageView imageView = convertView.findViewById(R.id.drawer_image_view);
        imageView.setImageResource(navigationItems.get(position).getIcon());

        TextView title = convertView.findViewById(R.id.drawer_text_title);
        title.setText(navigationItems.get(position).getTitle());

        TextView subtitle = convertView.findViewById(R.id.drawer_text_subTitle);
        subtitle.setText(navigationItems.get(position).getSubTitle());

        return convertView;
    }
}

