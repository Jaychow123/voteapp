package com.example.votegvp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    DBConnection controldb;


    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> namesCounts = new ArrayList<String>();

    public CustomAdapter(Context context, ArrayList<String> names, ArrayList<String> namesCounts) {
        this.mContext = context;
        this.names = names;
        this.namesCounts = namesCounts;
    }

    @Override
    public int getCount() {
        return names.size();
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
        final ViewHolder holder;
        controldb = new DBConnection(mContext);
        LayoutInflater layoutInflater;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.single_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.party_name);
            holder.cname = (TextView) convertView.findViewById(R.id.cand_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(names.get(position));
        holder.cname.setText(namesCounts.get(position));
        return convertView;
    }

    public static class ViewHolder {
        TextView name;
        TextView cname;
    }
}
