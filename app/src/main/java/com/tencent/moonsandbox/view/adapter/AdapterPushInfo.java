package com.tencent.moonsandbox.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.moonsandbox.R;
import com.tencent.moonsandbox.model.CurVideoInfo;
import com.tencent.moonsandbox.model.json.RspPushInfo;
import com.tencent.moonsandbox.view.MixPlayActivity;
import com.tencent.moonsandbox.view.PlayActivity;

import java.util.List;

/**
 * Created by tencent on 2016/8/16.
 */

public class AdapterPushInfo extends ArrayAdapter<RspPushInfo> {


    private class ViewHolder{
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvDesc;
        TextView tvTime;
        TextView tvId;
    }

    public AdapterPushInfo(Context context, int resource, List<RspPushInfo> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_pushlist, null);
            holder = new ViewHolder();
            holder.ivIcon = (ImageView)convertView.findViewById(R.id.iv_icon);
            holder.tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
            holder.tvDesc = (TextView)convertView.findViewById(R.id.tv_description);
            holder.tvTime = (TextView)convertView.findViewById(R.id.tv_time);
            holder.tvId = (TextView)convertView.findViewById(R.id.tv_id);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        RspPushInfo info = getItem(position);
        if (null != info){
            holder.tvTitle.setText(info.getTitle());
            holder.tvDesc.setText(info.getDescription());
            holder.tvTime.setText(""+info.getCreate_time());
            holder.tvId.setText("@"+info.getId());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RspPushInfo info = getItem(position);
                if(null == info || TextUtils.isEmpty(info.getPlayurl())){
                    Toast.makeText(getContext(), R.string.tip_invalid_url, Toast.LENGTH_SHORT).show();
                    return;
                }
                CurVideoInfo.setId(info.getId());
                CurVideoInfo.setPushId(info.getPushid());
                CurVideoInfo.setPlayUrl(info.getPlayurl());
                CurVideoInfo.setHost(false);    // 视频观看者
                Intent intent = new Intent();
                if (info.isAvsupport()){
                    intent.setClass(getContext(), MixPlayActivity.class);
                }else {
                    intent.setClass(getContext(), PlayActivity.class);
                }

                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
