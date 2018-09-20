package com.dragon.wujiangyouzheng.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dragon.wujiangyouzheng.R;
import com.dragon.wujiangyouzheng.activity.BaseActivity;
import com.dragon.wujiangyouzheng.activity.LoginActivity;
import com.dragon.wujiangyouzheng.activity.MailqueryActivity;
import com.dragon.wujiangyouzheng.activity.MainActivity;
import com.dragon.wujiangyouzheng.activity.QueryActivity;
import com.dragon.wujiangyouzheng.config.Configs;
import com.dragon.wujiangyouzheng.config.Urls;
import com.dragon.wujiangyouzheng.entity.QueryEntity;
import com.dragon.wujiangyouzheng.entity.UserEntity;
import com.dragon.wujiangyouzheng.utils.OkHttpUtils;
import com.dragon.wujiangyouzheng.utils.SpaceItemDecoration;
import com.dragon.wujiangyouzheng.utils.ToastUtil;
import com.dragon.wujiangyouzheng.widget.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/3/21.
 */

public class QueryAdapter extends BaseQuickAdapter<QueryEntity
        , BaseViewHolder> implements BaseQuickAdapter.OnItemClickListener{


    public QueryAdapter(@Nullable List<QueryEntity> data) {
        super(R.layout.activity_gallery_item, data);

        setOnItemClickListener(this);
    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(mContext,view.getId()+"",Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, QueryEntity
            queryEntity) {

        baseViewHolder.setText(R.id.tv_mail_code, "单号:" + queryEntity
                .getMail_code());
        baseViewHolder.setText(R.id.tv_time, queryEntity
                .getUpload_date());
        baseViewHolder.setText(R.id.tv_uploader, "上传人:" + queryEntity
                .getUser_name());
        baseViewHolder.setText(R.id.tv_uploader_phone, "联系电话:" + queryEntity
                .getUser_tel());
        baseViewHolder.setVisible(R.id.tv_uploader_phone, !TextUtils.isEmpty
                (queryEntity.getUser_tel()));
        RecyclerView recy = baseViewHolder.getView(R.id.recy);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,
                4);
        recy.setLayoutManager(gridLayoutManager);
        if (recy.getItemDecorationCount() == 0) {
            recy.addItemDecoration(new SpaceItemDecoration(10, 15, 4));
        }
        String[] split = queryEntity.getPhotoPath().split(",");
        List<String> paths = new ArrayList<>();
        for (String s : split) {
            if (!TextUtils.isEmpty(s)) {
                paths.add(s);
            }
        }
        QueryItemAdapter queryItemAdapter = new QueryItemAdapter(paths);
        recy.setAdapter(queryItemAdapter);
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {

        QueryEntity queryEntity = (QueryEntity)baseQuickAdapter.getItem(i);
        Intent intent = new Intent(mContext, MailqueryActivity.class);
        intent.putExtra("mailno",queryEntity.getMail_code());
        mContext.startActivity(intent);
        //Toast.makeText(mContext,queryEntity.getMail_code(),Toast.LENGTH_LONG).show();

    }




}
