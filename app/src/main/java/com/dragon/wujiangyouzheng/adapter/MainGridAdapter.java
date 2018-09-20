package com.dragon.wujiangyouzheng.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dragon.wujiangyouzheng.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/3/21.
 */

public class MainGridAdapter extends RecyclerView.Adapter<MainGridAdapter
        .MyHolder> {
    private ArrayList<String> paths;
    private Context context;

    public MainGridAdapter(ArrayList<String> paths, Context context) {
        this.paths = paths;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R
                .layout.main_grid_item, null);
        MyHolder myHolder = new MyHolder(inflate);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        Glide.with(context).load(paths.get(position)).error(R.mipmap
                .icon_add).override(540, 540).into(holder.iv);
        holder.parent.setTag(position);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = (int) holder.parent.getTag();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(tag);
                }
            }
        });
        holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int tag = (int) holder.parent.getTag();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemLongClick(tag);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return paths.size() >= 4 ? 4 : paths.size();
    }

    public void refresh(ArrayList<String> paths) {
        this.paths = paths;
        notifyDataSetChanged();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.parent)
        FrameLayout parent;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener
                                               onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemLongClick(int position);
    }
}
