package com.example.hyg.amap2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.example.hyg.amap2.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by hyg on 2016/12/19.
 */

public class SearchAddressAdapter extends BaseAdapter {
    Context context;
    List<Tip> list;
    int selectPosition = -1;
    private TextView textView;
    private RelativeLayout relativeLayout;
    public SearchAddressAdapter(Context context,List<Tip> list){
        this.context = context;
        this.list = list;
    }
    public void setSelectPosition(int position){
        this.selectPosition = position;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView ==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_layout,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        Tip poiItem = (Tip) getItem(position);
        holder.textView.setText(poiItem.getName());
        return null;

    }

    class ViewHolder {
        @Bind(R.id.textView)
        TextView textView;
        @Bind(R.id.relativeLayout)
        RelativeLayout relativeLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
