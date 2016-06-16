package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.Text;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by Administrator on 2016/6/15 0015.
 */
public class GoodAdapter extends RecyclerView.Adapter{
    Context mContext;
    ArrayList<NewGoodBean> mNewGoodList;



    ViewGroup parent;
    String footerText;
    static final int TYPE_ITEM = 0;
    static final int TYPE_FOOTER=1;
    boolean isMore;

    public GoodAdapter(Context context, ArrayList<NewGoodBean> mNewGoodList) {
        this.mContext = context;
        this.mNewGoodList = mNewGoodList;
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
    public void initContact(ArrayList<NewGoodBean> list) {
        this.mNewGoodList.clear();
        this.mNewGoodList.addAll(list);
        notifyDataSetChanged();
    }

    public void addContact(ArrayList<NewGoodBean> contactList) {
        this.mNewGoodList.addAll(contactList);
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
                layout = filter.inflate(R.layout.item_newgood, parent, false);
                holder = new GoodItemViewHolder(layout);
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
            ((FooterViewHolder)holder).mtvFootertext.setText(footerText);
            return;
        }
        GoodItemViewHolder holder1 = (GoodItemViewHolder)holder;
        NewGoodBean good = mNewGoodList.get(position);
        holder1.mtvGoodName.setText(good.getGoodsName());
        holder1.mtvPrice.setText(good.getShopPrice());
        ImageUtils.setNewGoodThumb(good.getGoodsThumb(),holder1.mAvatar);
    }

    @Override
    public int getItemCount() {
        return mNewGoodList==null?1:mNewGoodList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    class GoodItemViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView mAvatar;
        TextView mtvGoodName,mtvPrice;
        public GoodItemViewHolder(View itemView) {
            super(itemView);
            mAvatar = (NetworkImageView) itemView.findViewById(R.id.nivAvatar);
            mtvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            mtvPrice = (TextView) itemView.findViewById(R.id.tv_price);

        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView mtvFootertext;
        public FooterViewHolder(View itemView) {
            super(itemView);
            mtvFootertext = (TextView) itemView.findViewById(R.id.tv_footer);
        }
    }
}
