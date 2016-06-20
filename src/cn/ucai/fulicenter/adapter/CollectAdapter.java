package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CollectActivity;
import cn.ucai.fulicenter.activity.GoodDetailActivity;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by Administrator on 2016/6/15 0015.
 */
public class CollectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context mContext;
    CollectActivity context;
    ArrayList<CollectBean> mCollectList;
    ViewGroup parent;
    String footerText;
    static final int TYPE_ITEM = 0;
    static final int TYPE_FOOTER=1;
    boolean isMore;
    FooterViewHolder mFooterViewHolder;
    public CollectAdapter(Context mContext, ArrayList<CollectBean> mCollectList) {
        this.mContext = mContext;
        this.mCollectList = mCollectList;
        context = (CollectActivity) mContext;
    }

    public boolean isMore() {
        return isMore;

    }

    public void setMore(boolean more) {
        isMore = more;
        if (isMore) {
            setFooterText("加载更多数据");
        } else {
            setFooterText("没有更多数据");
        }
        notifyDataSetChanged();
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
        notifyDataSetChanged();
    }
    public void initContact(ArrayList<CollectBean> list) {
        this.mCollectList.clear();
        this.mCollectList.addAll(list);
        notifyDataSetChanged();
    }

    public void addContact(ArrayList<CollectBean> contactList) {
        this.mCollectList.addAll(contactList);
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        RecyclerView.ViewHolder holder = null;
        View layout = null;
        final LayoutInflater filter = LayoutInflater.from(mContext);
        switch (viewType) {
            case TYPE_ITEM:
                layout = filter.inflate(R.layout.item_collect, parent, false);
                holder = new CollectItemViewHolder(layout);
                break;
            case TYPE_FOOTER :
                layout = filter.inflate(R.layout.item_footer, parent, false);
                holder = new FooterViewHolder(layout);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            mFooterViewHolder = (FooterViewHolder) holder;
            return;
        }
        final CollectItemViewHolder holder1 = (CollectItemViewHolder)holder;
        final CollectBean good = mCollectList.get(position);
        holder1.mtvGoodName.setText(good.getGoodsName());
        holder1.miv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除收藏宝贝
                CollectBean collectBean = new CollectBean();

                try {
                    String path = new ApiParams()
                            .with(I.Collect.GOODS_ID,collectBean.getGoodsId()+"")
                            .with(I.Collect.USER_NAME,collectBean.getUserName()+"")
                            .getRequestUrl(I.REQUEST_DELETE_COLLECT);
                    context.executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class,
                            responseDelCollectListener(), context.errorListener()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ImageUtils.setNewGoodThumb(good.getGoodsThumb(),holder1.mAvatar);
        holder1.rl_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, GoodDetailActivity.class)
                        .putExtra(D.NewGood.KEY_GOODS_ID,good.getGoodsId()));
            }
        });
    }

    private Response.Listener<MessageBean> responseDelCollectListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                Log.e("error", "删除收藏宝贝成功");
            }
        };
    }

    @Override
    public int getItemCount() {
        return mCollectList==null?1:mCollectList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    class CollectItemViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView mAvatar;
        TextView mtvGoodName;
        RelativeLayout rl_collect;
        ImageView miv_del;
        public CollectItemViewHolder(View itemView) {
            super(itemView);
            mAvatar = (NetworkImageView) itemView.findViewById(R.id.nivAvatar);
            mtvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            rl_collect = (RelativeLayout) itemView.findViewById(R.id.layout_collect);
            miv_del = (ImageView) itemView.findViewById(R.id.iv_collect_del);
        }
    }


}
