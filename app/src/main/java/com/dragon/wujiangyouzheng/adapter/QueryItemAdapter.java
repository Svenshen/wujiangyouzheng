package com.dragon.wujiangyouzheng.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dragon.wujiangyouzheng.R;
import com.dragon.wujiangyouzheng.config.Urls;
import com.dragon.wujiangyouzheng.entity.QueryEntity;
import com.dragon.wujiangyouzheng.utils.SpaceItemDecoration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.truba.touchgallery.GallerActivity;

/**
 * Created by Administrator on 2018/3/21.
 */

public class QueryItemAdapter extends BaseQuickAdapter<String
        , BaseViewHolder> implements BaseQuickAdapter.OnItemClickListener {

    public QueryItemAdapter(@Nullable List<String> data) {
        super(R.layout.activity_gallery_item_item, data);
        setOnItemClickListener(this);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, String
            path) {
        final ImageView view = baseViewHolder.getView(R.id.iv);
        String url = Urls.BASE_URL + path;
        url = url.substring(0, url.lastIndexOf(".")) + "_001" + url
                .substring(url.lastIndexOf("."));
        Glide.with(mContext).load(url).into(view);
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int
            i) {
        Intent intent = new Intent(mContext, GallerActivity.class);
        intent.putExtra("index", i);
        ArrayList showPaths = new ArrayList<String>();
        List<String> data = baseQuickAdapter.getData();
        for (String path : data) {
            showPaths.add(Urls.BASE_URL + path);
        }
        intent.putExtra("paths", showPaths);
        mContext.startActivity(intent);
    }
}
