package com.example.potter.nct;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter{

    Context context;
    ArrayList<Song> list;

    public SongAdapter(Context context, ArrayList<Song> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_song,null);
            holder = new ViewHolder();
            //ánh xạ view
            holder.tvName = view.findViewById(R.id.textViewName);
            holder.tvArtist = view.findViewById(R.id.textViewArtist);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        Song song = list.get(position);
            holder.tvName.setText(song.getSongName());
            holder.tvArtist.setText(song.getSongArtist());
        return view;
    }
}

class ViewHolder{
    TextView tvName;
    TextView tvArtist;
}
