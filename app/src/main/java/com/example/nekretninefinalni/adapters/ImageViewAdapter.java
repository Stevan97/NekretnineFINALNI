package com.example.nekretninefinalni.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.nekretninefinalni.R;
import com.example.nekretninefinalni.db.model.Slike;

import java.util.List;

public class ImageViewAdapter extends BaseAdapter {

    private Context context;
    private List<Slike> slike;

    public ImageViewAdapter(Context context, List<Slike> slike) {
        this.context = context;
        this.slike = slike;
    }

    @Override
    public int getCount() {
        return slike.size();
    }

    @Override
    public Object getItem(int position) {
        return slike.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.image_view_adapter, null);

        ImageView imageView = convertView.findViewById(R.id.image_adapter);

        imageView.setImageURI(Uri.parse(slike.get(position).getSlike()));

        return convertView;
    }
}