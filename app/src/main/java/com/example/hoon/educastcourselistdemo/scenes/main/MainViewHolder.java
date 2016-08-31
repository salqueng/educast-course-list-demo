package com.example.hoon.educastcourselistdemo.scenes.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hoon.educastcourselistdemo.R;

/**
 * Created by kimin on 16. 8. 30.
 */
public class MainViewHolder extends RecyclerView.ViewHolder {
    public ImageView thumbnail;
    public TextView name;
    public TextView channelName;

    public MainViewHolder(View itemView) {
        super(itemView);
        this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        this.name = (TextView) itemView.findViewById(R.id.name);
        this.channelName = (TextView) itemView.findViewById(R.id.channel_name);
    }
}
